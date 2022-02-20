package com.cmartin.learn.adapter.zio2

import slick.basic.DatabaseConfig
import slick.jdbc._
import zio._

object ZioSlickIntegration {

  // Repository domain
  case class Item(id: Long, name: String)

  // Table definition
  object ItemTableDef
      extends JdbcProfile {
    import this.api._

    class Items(tag: Tag) extends Table[Item](tag, "ITEMS") {
      def id   = column[Long]("ID", O.PrimaryKey, O.AutoInc)
      def name = column[String]("NAME")
      def *    = (id, name) <> ((Item.apply _).tupled, Item.unapply _)
    }

    val items = TableQuery[Items]
  }

  // Slick <-> ZIO integration and syntax
  object SlickToZioSyntax
      extends JdbcProfile {
    import this.api._

    def fromDBIO[R](dbio: => DBIO[R]): RIO[JdbcBackend#DatabaseDef, R] = for {
      db <- ZIO.service[JdbcBackend#DatabaseDef]
      r  <- ZIO.fromFuture(_ => db.run(dbio))
    } yield r
  }

  object ItemRepositoryDef
      extends JdbcProfile {
    import this.api._, SlickToZioSyntax._, ItemTableDef.items

    trait ItemRepository {
      def add(name: String): Task[Long]
      def getById(id: Long): Task[Option[Item]]
      def count(): Task[Int]
    }

    case class SlickItemRepository(val db: JdbcBackend#DatabaseDef)
        extends ItemRepository {

      override def count(): Task[Int] =
        fromDBIO(items.size.result)
          .provideService(db)

      override def getById(id: Long): Task[Option[Item]] = {
        val query = items.filter(_.id === id).result
        fromDBIO(query)
          .map(_.headOption)
          .provideService(db)
      }

      override def add(name: String) =
        fromDBIO((items returning items.map(_.id)) += Item(0L, name))
          .provideService(db)

    }

    object SlickItemRepository {
      val live = (SlickItemRepository(_)).toLayer
    }
  }

  object MyServiceDef {
    import ItemRepositoryDef.ItemRepository

    trait ItemService {
      def count(): IO[String, Int]
    }

    case class LiveItemService(repo: ItemRepository)
        extends ItemService {

      override def count(): IO[String, Int] = for {
        count <- repo.count().mapError(_.toString())
      } yield count
    }

    object LiveItemService {
      val live = (LiveItemService(_)).toLayer
    }
  }

  object MainProgram {
    import ItemRepositoryDef.{ItemRepository, SlickItemRepository}
    import MyServiceDef.{ItemService, LiveItemService}

    // read config for underlying infrastructure, i.e. PostgreSQL
    val dc = DatabaseConfig.forConfig[JdbcProfile]("h2_dc")

    val zm: TaskLayer[JdbcProfile] = ZManaged.fromZIO(
      Task.attempt(DatabaseConfig.forConfig[JdbcProfile]("h2_dc"))
        .map(_.profile)
    ).toLayer

    val dbLayer: TaskLayer[JdbcBackend#DatabaseDef] = ZManaged.fromZIO(
      Task.attempt(DatabaseConfig.forConfig[JdbcProfile]("h2_dc"))
        .map(_.db)
    ).toLayer

    val dbEnv = ZLayer.make[ItemRepository with JdbcBackend#DatabaseDef](
      SlickItemRepository.live,
      dbLayer
    )

    val dbProgram: ZIO[ItemRepository, Throwable, Long] = for {
      repo <- ZIO.service[ItemRepository]
      r    <- repo.add("Chikito")
    } yield r

    val res: Long = zio.Runtime.default.unsafeRun(
      dbProgram.provide(dbEnv)
    )

    val srvEnv: ZLayer[Any, Throwable, ItemService with ItemRepository with JdbcBackend#DatabaseDef] =
      ZLayer.make[ItemService with ItemRepository with JdbcBackend#DatabaseDef](
        SlickItemRepository.live,
        dbLayer,
        LiveItemService.live
      )

    val serviceProgram: ZIO[ItemService, String, Int] = for {
      srv   <- ZIO.service[ItemService]
      count <- srv.count()
    } yield count

    val srvResut: Int = zio.Runtime.default.unsafeRun(
      serviceProgram.provide(srvEnv)
    )
  }

}
