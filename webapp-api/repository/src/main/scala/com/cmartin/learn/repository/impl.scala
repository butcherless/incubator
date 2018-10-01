package com.cmartin.learn.repository

import com.cmartin.learn.common.getDummyInt
import com.cmartin.learn.repository.spec.{DummyRepository, SimpleRepository}

import scala.collection.mutable.TreeSet

package object impl {

  // TODO change code to UUID
  case class Aircraft(k: Long, typeCode: String, registration: String)

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

  class DummyRepositoryImpl extends DummyRepository {
    override def saveDummy(): Int = getDummyInt()
  }

  object DummyRepositoryImpl {
    def apply(): DummyRepositoryImpl = new DummyRepositoryImpl()
  }


  class MemoryRepository extends SimpleRepository[Option, Aircraft, Long] {
    private val repo = TreeSet[Aircraft]()

    override def findAll(filter: Aircraft => Boolean): Option[List[Aircraft]] =
      Some(repo.filter(filter).toList)

    override def findById(k: Long): Option[Aircraft] = repo.find(_.k == k)

    override def remove(t: Aircraft): Option[Long] =
      if (repo.remove(t)) Some(t.k) else None

    override def removeAll(filter: Aircraft => Boolean): Option[List[Long]] = {
      val result = repo.filter(filter).map(_.k)
      repo.retain(filter)
      Some(result.toList)
    }

    override def save(t: Aircraft): Option[Long] = {
      repo.update(t, true)
      Some(t.k)
    }

    override def count(): Option[Long] = Some(repo.size)
  }

  object MemoryRepository extends MemoryRepository {
    def apply(): MemoryRepository = new MemoryRepository()
  }

}
