package com.cmartin.learn.repository

import java.util.UUID

import com.cmartin.learn.repository.frm.{Countries, Country}
import com.cmartin.learn.repository.spec.SimpleRepository
import slick.lifted.TableQuery

import scala.collection.mutable.TreeSet

package object impl {

  case class Aircraft(k: String = "", typeCode: String, registration: String)

  object Aircraft {
    implicit val ord = new Ordering[Aircraft] {
      /**
        * Comparator for dependencies classes
        *
        * @param c1 one dependency
        * @param c2 another one dependency
        * @return 0 if equals, -1 if less than, +1 if greater than
        */
      def compare(a1: Aircraft, a2: Aircraft): Int = {
        a1.k.compareTo(a2.k)
      }
    }
  }


  class MemoryRepository extends SimpleRepository[Option, Aircraft, String] {
    private val repo = TreeSet[Aircraft]()

    override def findAll(filter: Aircraft => Boolean): Option[List[Aircraft]] =
      Some(repo.filter(filter).toList)

    override def findById(k: String): Option[Aircraft] = repo.find(_.k == k)

    override def remove(aircraft: Aircraft): Option[String] =
      if (repo.remove(aircraft)) Some(aircraft.k) else None

    override def removeAll(filter: Aircraft => Boolean): Option[List[String]] = {
      val removableList = repo.filter(filter)
      repo --= removableList

      Some(removableList.map(_.k).toList)
    }

    override def save(aircraft: Aircraft): Option[String] = {
      if (aircraft.k.isEmpty) {
        val id = nextId()
        repo += aircraft.copy(k = id)
        Some(id)
      } else {
        remove(aircraft)
        repo += aircraft
        Some(aircraft.k)
      }
    }

    override def count(): Option[Long] = Some(repo.size)

    private def nextId(): String = UUID.randomUUID().toString
  }

  object MemoryRepository extends MemoryRepository {
    def apply(): MemoryRepository = new MemoryRepository()
  }


  class CountryDbio(countries: TableQuery[Countries]) {

    import slick.jdbc.H2Profile.api._

    def findById(id: Long): Query[Countries, Country, Seq] = for {
      user <- countries.filter(_.id === 1L)
    } yield user
  }

}
