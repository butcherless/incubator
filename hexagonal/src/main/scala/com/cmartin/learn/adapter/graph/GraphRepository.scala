package com.cmartin.learn.adapter.graph

import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcActionComponent, JdbcProfile}
import slick.lifted.{ForeignKeyQuery, Index, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

object GraphRepository {

  /* Persistence Model */
  object Model {

    type LongTuple = (Long, Long)

    trait IdentifiedDbo[T] {
      val id: T
    }

    trait LongDbo extends IdentifiedDbo[Option[Long]]

    sealed trait TaskNode extends LongDbo

    trait RelationDbo extends IdentifiedDbo[LongTuple]

    /** Base dbo for task nodes
      * @param id
      *   task node identifier
      */
    final case class TaskNodeDbo(
        id: Option[Long] = None
    ) extends TaskNode

    final case class TaskDbo(
        metadata: String,
        name: String = "",
        id: Option[Long] = None
    ) extends TaskNode

    /*
    final case class NamedTaskDbo(
        name: String,
        metadata: String,
        id: Option[Long] = None
    ) extends TaskNode
     */

    final case class TaskGroupDbo(
        name: String,
        id: Option[Long] = None
    ) extends TaskNode

    final case class GroupTaskRelationDbo(
        position: Int,
        id: LongTuple
    ) extends RelationDbo

    final case class TaskReferenceDbo(
        taskName: String,
        alias: String,
        taskId: Long,
        id: Option[Long] = None
    ) extends TaskNode

    final case class VertexDbo(
        businessId: String,
        metadata: String,
        id: Option[Long] = None
    ) extends LongDbo

    final case class EdgeDbo(
        start: Long,
        end: Long,
        metadata: String,
        id: Option[Long] = None
    ) extends LongDbo

    final case class EdgeTaskNodeRelationDbo(
        position: Int,
        id: LongTuple
    ) extends RelationDbo
  }

  /** Relational Profile for mapping domain and persistence models
    */
  trait Profile {
    val profile: JdbcProfile
  }

  object TableNames {
    val tasks          = "TASKS"
    val namedTasks     = "NAMED_TASKS"
    val taskGroups     = "TASK_GROUPS"
    val taskReferences = "TASK_REFERENCES"
    val taskNodes      = "TASK_NODES"
    val groupTasks     = "GROUP_TASKS"
    val vertexes       = "VERTEXES"
    val edges          = "EDGES"
    val edgeTaskNodes  = "EDGE_TASKNODES"
  }

  trait BaseDefinitions {
    self: JdbcProfile =>

    import Model._
    import api._

    abstract class LongBasedTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }

    abstract class ForeignLongTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] = column[Long]("ID")
    }

    abstract class RelationBasedTable[T <: RelationDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key columns */
      def left: Rep[Long]  = column[Long]("LEFT")
      def right: Rep[Long] = column[Long]("RIGHT")
    }

    abstract class AbstractRelationRepository[E <: RelationDbo, T <: RelationBasedTable[E]] {
      val entities: TableQuery[T]

      private def pkFilter(t: T, id: LongTuple): Rep[Boolean] =
        t.left === id._1 && t.right === id._2

      def findById(id: LongTuple): DBIO[Option[E]] =
        entities.filter(t => pkFilter(t, id)).result.headOption

      def findAll(): DBIO[Seq[E]] =
        entities.result

      def count(): DBIO[Int] =
        entities.length.result

      def insert(e: E): DBIO[LongTuple] =
        entityReturningId() += e

      def insert(seq: Seq[E]): DBIO[Seq[LongTuple]] =
        entities returning entities.map(e => (e.left, e.right)) ++= seq

      def update(e: E): DBIO[Int] =
        entities.filter(t => pkFilter(t, e.id)).update(e)

      def delete(id: LongTuple): DBIO[Int] =
        entities.filter(t => pkFilter(t, id)).delete

      def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): ReturningInsertActionComposer[E, LongTuple] =
        entities returning entities.map(e => (e.left, e.right))
    }

    abstract class AbstractForeignLongRepository[E <: LongDbo, T <: ForeignLongTable[E]] {
      val entities: TableQuery[T]

      private lazy val entityReturningId = entities returning entities.map(_.id)

      def insert(e: E): DBIO[Long] = entityReturningId += e
    }

    abstract class AbstractLongRepository[E <: LongDbo, T <: LongBasedTable[E]] {
      val entities: TableQuery[T]

      def findById(id: Option[Long]): DBIO[Option[E]] = {
        entities.filter(_.id === id).result.headOption
      }

      def findAll(): DBIO[Seq[E]] =
        entities.result

      def count(): DBIO[Int] =
        entities.length.result

      def insert(e: E): DBIO[Long] =
        entityReturningId() += e

      def insert(seq: Seq[E]): DBIO[Seq[Long]] =
        entities returning entities.map(_.id) ++= seq

      def update(e: E): DBIO[Int] =
        entities.filter(_.id === e.id).update(e)

      def delete(id: Long): DBIO[Int] =
        entities.filter(_.id === id).delete

      def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): ReturningInsertActionComposer[E, Long] =
        entities returning entities.map(_.id)
    }

  }

  trait GraphRepositories extends BaseDefinitions {
    self: JdbcProfile =>
    import Model._
    import api._

    lazy val taskNodes       = TableQuery[TaskNodesTable]
    lazy val tasks           = TableQuery[TasksTable]
    // lazy val namedTasks      = TableQuery[NamedTasksTable]
    lazy val taskReferences  = TableQuery[TaskReferencesTable]
    lazy val taskGroups      = TableQuery[TaskGroupsTable]
    lazy val groupTaskRel    = TableQuery[GroupTaskRelationTable]
    lazy val vertexes        = TableQuery[VertexesTable]
    lazy val edges           = TableQuery[EdgesTable]
    lazy val edgeTaskNodeRel = TableQuery[EdgeTaskNodeRelationTable]

    /* TASK NODE
     */
    final class TaskNodesTable(tag: Tag) extends LongBasedTable[TaskNodeDbo](tag, TableNames.taskNodes) {
      // mapper function
      def * : ProvenShape[TaskNodeDbo] =
        id.?.<>(TaskNodeDbo, TaskNodeDbo.unapply)
    }

    /* TASK
     */
    final class TasksTable(tag: Tag) extends ForeignLongTable[TaskDbo](tag, TableNames.tasks) {
      // property columns:
      def metadata: Rep[String] = column[String]("METADATA")
      def name: Rep[String]     = column[String]("NAME")

      // mapper function
      def * : ProvenShape[TaskDbo] =
        (metadata, name, id.?).<>(TaskDbo.tupled, TaskDbo.unapply)

      // foreign keys
      def taskNodeFk: ForeignKeyQuery[TaskNodesTable, TaskNodeDbo] =
        foreignKey("FK_TASK_NODE_TASK", id, taskNodes)(_.id)

      // indexes
      def iataIndex: Index =
        index("task_name_index", name, unique = true)
    }

    /* NAMED TASK
    final class NamedTasksTable(tag: Tag) extends ForeignLongTable[NamedTaskDbo](tag, TableNames.namedTasks) {
      // property columns:
      def name: Rep[String]     = column[String]("NAME")
      def metadata: Rep[String] = column[String]("METADATA")

      // mapper function
      def * : ProvenShape[NamedTaskDbo] =
        (name, metadata, id.?).<>(NamedTaskDbo.tupled, NamedTaskDbo.unapply)

      // foreign keys
      def taskNodeFk: ForeignKeyQuery[TaskNodesTable, TaskNodeDbo] =
        foreignKey("FK_TASK_NODE_NAMED_TASK", id, taskNodes)(_.id)

      // indexes
      def iataIndex: Index =
        index("task_name_index", name, unique = true)
    }
     */

    /* TASK REFERENCE
     */
    final class TaskReferencesTable(tag: Tag)
        extends ForeignLongTable[TaskReferenceDbo](tag, TableNames.taskReferences) {
      // property columns:
      def taskName: Rep[String] = column[String]("TASK_NAME")
      def alias: Rep[String]    = column[String]("ALIAS")
      def taskId: Rep[Long]     = column[Long]("TASK_ID")

      // mapper function
      def * : ProvenShape[TaskReferenceDbo] =
        (taskName, alias, taskId, id.?).<>(TaskReferenceDbo.tupled, TaskReferenceDbo.unapply)

      // foreign keys
      def taskNodeFk: ForeignKeyQuery[TaskNodesTable, TaskNodeDbo] =
        foreignKey("FK_TASK_NODE_TASK_REFERENCE", id, taskNodes)(_.id)

    }

    /* TASK GROUP
     */
    final class TaskGroupsTable(tag: Tag) extends LongBasedTable[TaskGroupDbo](tag, TableNames.taskGroups) {
      // property columns:
      def name: Rep[String] = column[String]("NAME")

      // mapper function
      def * : ProvenShape[TaskGroupDbo] =
        (name, id.?).<>(TaskGroupDbo.tupled, TaskGroupDbo.unapply)

      // foreign keys
      def taskNodeFk: ForeignKeyQuery[TaskNodesTable, TaskNodeDbo] =
        foreignKey("FK_TASK_NODE_TASK_GROUP", id, taskNodes)(_.id)

      // indexes
      def iataIndex: Index =
        index("taskgroup_name_index", name, unique = true)
    }

    /* TASK GROUP -- TASK  RELATION
     */
    final class GroupTaskRelationTable(tag: Tag)
        extends RelationBasedTable[GroupTaskRelationDbo](tag, TableNames.groupTasks) {
      // property columns:
      def position: Rep[Int] = column[Int]("POSITION")

      // mapper function
      def * : ProvenShape[GroupTaskRelationDbo] =
        (position, (left, right)).<>(GroupTaskRelationDbo.tupled, GroupTaskRelationDbo.unapply)

      def pk: PrimaryKey = primaryKey("pk_taskgroup_task", (left, right))
    }

    /* VERTEX
     */
    final class VertexesTable(tag: Tag) extends LongBasedTable[VertexDbo](tag, TableNames.vertexes) {
      // property columns:
      def businessId: Rep[String] = column[String]("BUSINESS_ID")
      def metadata: Rep[String]   = column[String]("METADATA")

      // mapper function
      def * : ProvenShape[VertexDbo] =
        (businessId, metadata, id.?).<>(VertexDbo.tupled, VertexDbo.unapply)

      // indexes
      def iataIndex: Index =
        index("business_id_index", businessId, unique = true)
    }

    /* EDGE
     */
    final class EdgesTable(tag: Tag) extends LongBasedTable[EdgeDbo](tag, TableNames.edges) {
      // property columns:
      def start: Rep[Long]      = column[Long]("START_ID")
      def end: Rep[Long]        = column[Long]("END_ID")
      def metadata: Rep[String] = column[String]("METADATA")

      // mapper function
      def * : ProvenShape[EdgeDbo] =
        (start, end, metadata, id.?).<>(EdgeDbo.tupled, EdgeDbo.unapply)

      // foreign keys
      def startFk: ForeignKeyQuery[VertexesTable, VertexDbo] =
        foreignKey("FK_START_VERTEX_EDGE", start, vertexes)(v => v.id)
      def endFk: ForeignKeyQuery[VertexesTable, VertexDbo]   =
        foreignKey("FK_END_VERTEX_EDGE", end, vertexes)(v => v.id)
    }

    /* E D G E -- T A S K N O D E   R E L A T I O N
     */
    final class EdgeTaskNodeRelationTable(tag: Tag)
        extends RelationBasedTable[EdgeTaskNodeRelationDbo](tag, TableNames.edgeTaskNodes) {
      // property columns:
      def position: Rep[Int] = column[Int]("POSITION")

      // mapper function
      def * : ProvenShape[EdgeTaskNodeRelationDbo] =
        (position, (left, right)).<>(EdgeTaskNodeRelationDbo.tupled, EdgeTaskNodeRelationDbo.unapply)
    }

    class TaskNodeRepository extends AbstractLongRepository[TaskNodeDbo, TaskNodesTable] {
      override val entities = taskNodes
    }

    class TaskGroupRepository extends AbstractLongRepository[TaskGroupDbo, TaskGroupsTable] {
      override val entities = taskGroups

      def findTasksByName(name: String): DBIO[Seq[TaskDbo]] = {
        val query = for {
          tg  <- entities if tg.name === name
          rel <- groupTaskRel.sortBy(_.position) if tg.id === rel.left
          ts  <- tasks if rel.right === ts.id
        } yield ts

        query.result
      }

      val tnRepo = new TaskNodeRepository

      def myInsert(taskGroup: TaskGroupDbo)(implicit ec: ExecutionContext): DBIO[Long] = {
        for {
          tnId <- tnRepo.insert(TaskNodeDbo())
          tg   <- insert(taskGroup.copy(id = Some(tnId)))
        } yield tg
      }
    }

    class TaskReferenceRepository extends AbstractForeignLongRepository[TaskReferenceDbo, TaskReferencesTable] {
      override val entities = taskReferences

      def getTaskById(id: Long): DBIO[TaskDbo] = {
        val query = for {
          ref  <- entities if ref.id === id
          task <- tasks if ref.taskName === task.name
        } yield task

        query.result.head
      }
    }

    class TaskRepository extends AbstractForeignLongRepository[TaskDbo, TasksTable] {
      override val entities = tasks
    }

    /*
    class NamedTaskRepository extends AbstractForeignLongRepository[NamedTaskDbo, NamedTasksTable] {
      override val entities = namedTasks
    }
     */

    class EdgeTaskNodeRelRepository
        extends AbstractRelationRepository[EdgeTaskNodeRelationDbo, EdgeTaskNodeRelationTable] {
      override val entities = edgeTaskNodeRel
    }

    class GroupTaskRelRepository extends AbstractRelationRepository[GroupTaskRelationDbo, GroupTaskRelationTable] {
      override val entities = groupTaskRel
    }

    class VertexRepository extends AbstractLongRepository[VertexDbo, VertexesTable] {
      override val entities = vertexes

      def findByBusinessId(bid: String): DBIO[Option[VertexDbo]] = {
        entities
          .filter(vx => vx.businessId === bid)
          .result
          .headOption
      }

      def findCoordinates(startId: String, endId: String): DBIO[Option[(LongTuple)]] = {
        val query = for {
          startVx <- entities if startVx.businessId === startId
          endVx   <- entities if endVx.businessId === endId
        } yield (startVx.id, endVx.id)

        query.result.headOption
      }
    }

    class EdgeRepository extends AbstractLongRepository[EdgeDbo, EdgesTable] {
      override val entities = edges

      def findByCoordinates(startId: String, endId: String): DBIO[Option[EdgeDbo]] = {
        val query = for {
          (startVxId, endVxId) <- __findVertexCoordinates(startId, endId)
          edges                <- entities if edges.start === startVxId && edges.end === endVxId
        } yield edges

        query.result.headOption
      }

      def findTaskNodes(startId: String, endId: String) = {
        // : DBIO[Seq[TaskNode]]

        val qj1 =
          entities
            .join(vertexes)
            .join(vertexes)
            .on { case ((edge, sVx), eVx) => edge.start === sVx.id && edge.end === eVx.id }
            .join(edgeTaskNodeRel)
            .on { case (((edge, _), _), rel) => edge.id === rel.left }
            .sortBy { case (((edge, _), _), rel) => rel.position }
            .join(taskNodes)
            .on { case ((_, rel), tn) => rel.right === tn.id }
            .joinLeft(taskGroups)
            .on { case ((_, tn), tg) => tn.id === tg.id }
            .joinLeft(tasks)
            .on { case (((_, tn), _), t) => tn.id === t.id }
            .joinLeft(taskReferences)
            .on { case ((((_, tn), _), _), tr) => tn.id === tr.id }
        // .joinLeft(namedTasks)
        // .on { case (((((_, tn), _), t), _),nt) => tn.id === nt.id &&  }

        val qf1 = qj1.filter { case (((((((edge, sVx), eVx), rel), tn), tg), t), tr) =>
          sVx.businessId === startId && eVx.businessId === endId
        }

        val qj2 =
          taskNodes
            .join(taskGroups)
            .on(_.id === _.id)

        val query = for {
          (startVxId, endVxId) <- __findVertexCoordinates(startId, endId)
          edge                 <- entities if edge.start === startVxId && edge.end === endVxId
          rel                  <- edgeTaskNodeRel if edge.id === rel.left
          node                 <- taskNodes if rel.right === node.id
          group                <- taskGroups if group.id === node.id
          ref                  <- taskReferences if ref.id === node.id
          task                 <- tasks if node.id === task.id
        } yield (group, ref, task)

        // qj2.result
        qf1.result
      }
    }

    /* query */
    private def __findVertexCoordinates(startId: String, endId: String) = {
      for {
        startVx <- vertexes if startVx.businessId === startId
        endVx   <- vertexes if endVx.businessId === endId
      } yield (startVx.id, endVx.id)
    }
  }

  class RepositoryLayer(configPath: String) extends JdbcProfile with JdbcActionComponent.MultipleRowsPerStatementSupport
      with GraphRepositories {
    val config = DatabaseConfig.forConfig[JdbcProfile](configPath)

    implicit def executeFromDb[A](action: api.DBIO[A]): Future[A] =
      config.db.run(action)

    val vertexRepository          = new VertexRepository
    val edgeRepository            = new EdgeRepository
    val taskGroupRepository       = new TaskGroupRepository
    val taskNodeRepository        = new TaskNodeRepository
    val edgeTaskNodeRelRepository = new EdgeTaskNodeRelRepository
    val taskRepository            = new TaskRepository
    val taskReferenceRepository   = new TaskReferenceRepository
    val groupTaskRelRepository    = new GroupTaskRelRepository
    // val namedTaskRepository       = new NamedTaskRepository
  }

}
