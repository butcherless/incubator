package com.cmartin.learn.adapter.graph

import com.cmartin.learn.adapter.graph.GraphRepository.Model.{TaskGroupDbo, VertexDbo}
import com.cmartin.learn.adapter.graph.GraphRepository.RepositoryLayer
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class GraphRepositorySpec
    extends AsyncFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  import GraphRepository.Model._
  import GraphRepositorySpec._
  import repoLayer.executeFromDb

  behavior of "Country Repository"

  "Create Vertex" should "create a Vertex into the repository" in {
    val result = for {
      id    <- repoLayer.vertexRepository.insert(vertexOne)
      count <- repoLayer.vertexRepository.count()
    } yield (id, count)

    result map { case (id, count) =>
      assert(id > 0)
      count shouldBe 1
    }
  }

  "Find Vertex" should "find a Vertex in the repository" in {
    val result = for {
      _     <- repoLayer.vertexRepository.insert(vertexOne)
      vx    <- repoLayer.vertexRepository.findByBusinessId(deviceOneId)
      count <- repoLayer.vertexRepository.count()
    } yield (vx, count)

    result map { case (vx, count) =>
      vx.get.businessId shouldBe deviceOneId
      vx.get.metadata shouldBe emptyMetadata
      count shouldBe 1
    }
  }

  "Create Edge" should "create an Edge into the repository" in {
    val result = for {
      (startId, endId) <- repoLayer.vertexRepository.insert(vertexOne) zip repoLayer.vertexRepository.insert(vertexTwo)
      id               <- repoLayer.edgeRepository.insert(EdgeDbo(startId, endId, metadataOne))
      vxCount          <- repoLayer.vertexRepository.count()
      edgeCount        <- repoLayer.edgeRepository.count()
    } yield (id, vxCount, edgeCount)

    result map { case (id, vxCount, edgeCount) =>
      assert(id > 0)
      vxCount shouldBe 2
      edgeCount shouldBe 1
    }
  }

  "Find Edge" should "find an Edge in the repository" in {
    val result = for {
      (startId, endId) <- repoLayer.vertexRepository.insert(vertexOne) zip repoLayer.vertexRepository.insert(vertexTwo)
      id               <- repoLayer.edgeRepository.insert(EdgeDbo(startId, endId, metadataOne))
      edge             <- repoLayer.edgeRepository.findByCoordinates(deviceOneId, deviceTwoId)
    } yield (startId, endId, edge, id)

    result map { case (startId, endId, edgeOption, id) =>
      edgeOption shouldBe Some(EdgeDbo(startId, endId, metadataOne, Some(id)))
    }
  }

  "Create TaskNode" should "create a TaskNode into the repository" in {
    val result = for {
      id    <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      count <- repoLayer.taskNodeRepository.count()
    } yield (id, count)

    result map { case (id, count) =>
      assert(id > 0)
      count shouldBe 1
    }
  }

  "Find TaskNode" should "find a TaskNode in the repository" in {
    val result = for {
      id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      tn <- repoLayer.taskNodeRepository.findById(Some(id))
    } yield tn

    result map { tn =>
      tn shouldBe Some(TaskNodeDbo(tn.get.id))
    }
  }

  "Create TaskGroup" should "create a TaskGroup into the repository" in {
    val result = for {
      id      <- repoLayer.taskGroupRepository.myInsert(TaskGroupDbo("filter-group"))
      tnCount <- repoLayer.taskNodeRepository.count()
      tgCount <- repoLayer.taskGroupRepository.count()
    } yield (id, tnCount, tgCount)

    result map { case (id, tnCount, tgCount) =>
      assert(id > 0)
      tnCount shouldBe 1
      tgCount shouldBe 1
    }
  }

  it should "find task associated with a task group" in {
    val result = for {
      tn1Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskGroupRepository.insert(TaskGroupDbo("filters", Some(tn1Id)))

      tn2Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskRepository.insert(TaskDbo("{metadata-1}", "named-task-1", Some(tn2Id)))

      tn3Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskRepository.insert(TaskDbo("{metadata-2}", "named-task-2", Some(tn3Id)))

      tn4Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskRepository.insert(TaskDbo("{metadata-3}", id = Some(tn4Id)))

      _ <- repoLayer.groupTaskRelRepository.insert(GroupTaskRelationDbo(1, (tn1Id, tn2Id)))
      _ <- repoLayer.groupTaskRelRepository.insert(GroupTaskRelationDbo(2, (tn1Id, tn3Id)))
      _ <- repoLayer.groupTaskRelRepository.insert(GroupTaskRelationDbo(3, (tn1Id, tn4Id)))

      tnCount <- repoLayer.taskNodeRepository.count()
      tasks   <- repoLayer.taskGroupRepository.findTasksByName("filters")
    } yield (tnCount, tasks)

    result map { case (tnCount, tasks) =>
      tnCount shouldBe 4
      tasks.size shouldBe 3
    }
  }

  it should "find task associated with a task reference" in {
    val result = for {
      tn1Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskRepository.insert(TaskDbo("{metadata-1}", "task-1", Some(tn1Id)))

      tn2Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskReferenceRepository.insert(TaskReferenceDbo("task-1", "alias", tn1Id, Some(tn2Id)))

      tnCount <- repoLayer.taskNodeRepository.count()
      task    <- repoLayer.taskReferenceRepository.getTaskById(tn2Id)
    } yield (tnCount, task, tn1Id)

    result map { case (tnCount, task, id) =>
      tnCount shouldBe 2
      task shouldBe TaskDbo("{metadata-1}", "task-1", Some(id))
    }
  }

  it should "find the nodes associated with an edge" in {
    val result = for {
      (startId, endId) <- repoLayer.vertexRepository.insert(vertexOne) zip repoLayer.vertexRepository.insert(vertexTwo)
      eId              <- repoLayer.edgeRepository.insert(EdgeDbo(startId, endId, metadataOne))

      tnId <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _    <- repoLayer.taskGroupRepository.insert(TaskGroupDbo("filters", Some(tnId)))
      _    <- repoLayer.edgeTaskNodeRelRepository.insert(EdgeTaskNodeRelationDbo(3, (eId, tnId)))

      tn2Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      t1Id  <- repoLayer.taskRepository.insert(TaskDbo("{metadata-1}", id = Some(tn2Id)))
      _     <- repoLayer.edgeTaskNodeRelRepository.insert(EdgeTaskNodeRelationDbo(2, (eId, tn2Id)))

      tn3Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskRepository.insert(TaskDbo("{metadata-2}", "named-task-1", Some(tn3Id)))

      tn4Id <- repoLayer.taskNodeRepository.insert(TaskNodeDbo())
      _     <- repoLayer.taskReferenceRepository.insert(TaskReferenceDbo("task-name", "", t1Id, Some(tn4Id)))
      _     <- repoLayer.edgeTaskNodeRelRepository.insert(EdgeTaskNodeRelationDbo(1, (eId, tn4Id)))

      nodes   <- repoLayer.edgeRepository.findTaskNodes(deviceOneId, deviceTwoId)
      tnCount <- repoLayer.taskNodeRepository.count()
    } yield (nodes, tnCount)

    result map { case (nodes, tnCount) =>
      info(s"nodes: ${nodes.mkString("\n\n")}")
      nodes.size shouldBe 3
      tnCount shouldBe 4
    }
  }

  override def beforeEach(): Unit = {
    Await.result(repoLayer.createSchema(), waitTimeout)
  }

  override def afterEach(): Unit = Await.result(repoLayer.dropSchema(), waitTimeout)

}

object GraphRepositorySpec {
  // TODO create GraphData object for testing data
  val waitTimeout: FiniteDuration = 5.seconds

  val taskGroupOne = TaskGroupDbo("filters")

  val deviceOneId   = UUID.randomUUID().toString
  val deviceTwoId   = UUID.randomUUID().toString
  val emptyMetadata = "{}"
  val metadataOne   =
    """
      |{ "version" : "1.0" }
      |""".stripMargin

  val vertexOne = VertexDbo(deviceOneId, emptyMetadata)
  val vertexTwo = VertexDbo(deviceTwoId, emptyMetadata)

  val repoLayer = new TestRepositoryLayer("h2_dc")

  class TestRepositoryLayer(configPath: String) extends RepositoryLayer(configPath) {
    import api._

    private val schema =
      vertexes.schema ++
        edges.schema ++
        edgeTaskNodeRel.schema ++
        taskNodes.schema ++
        groupTaskRel.schema ++
        taskGroups.schema ++
        tasks.schema ++
        taskReferences.schema

    def createSchema(): Future[Unit] = schema.create

    def dropSchema(): Future[Unit] = schema.dropIfExists

    def printSchema() = schema.createStatements.mkString("\n")
  }
}
