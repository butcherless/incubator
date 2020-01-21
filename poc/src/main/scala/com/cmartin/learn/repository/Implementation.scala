package com.cmartin.learn.repository

import java.util.UUID

import com.cmartin.learn.repository.Definition.SimpleRepository

import scala.collection.mutable.TreeSet

object Implementation {

  case class Airplane(k: String = "", typeCode: String, registration: String)

  object Airplane {
    implicit val ord = new Ordering[Airplane] {
      /**
        * Comparator for dependencies classes
        *
        * @param c1 one dependency
        * @param c2 another one dependency
        * @return 0 if equals, -1 if less than, +1 if greater than
        */
      def compare(a1: Airplane, a2: Airplane): Int = {
        a1.k.compareTo(a2.k)
      }
    }
  }

  class MemoryRepository extends SimpleRepository[Option, Airplane, String] {
    private val repo = TreeSet[Airplane]()

    override def findAll(filter: Airplane => Boolean): Option[List[Airplane]] =
      Some(repo.filter(filter).toList)

    override def findById(k: String): Option[Airplane] = repo.find(_.k == k)

    override def remove(aircraft: Airplane): Option[String] =
      if (repo.remove(aircraft)) Some(aircraft.k) else None

    override def removeAll(filter: Airplane => Boolean): Option[List[String]] = {
      val removableList = repo.filter(filter)
      repo --= removableList

      Some(removableList.map(_.k).toList)
    }

    override def save(aircraft: Airplane): Option[String] = {
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

}
