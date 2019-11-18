package com.cmartin

import com.cmartin.learn.service.spec.GAV

package object learn {
  object TestConstants {
    val ArtifactName: String            = "mma-arch-base"
    val GroupName: String               = "es.mma.architecture"
    val Version                         = "5.3.0"
    val NotExistingArtifactName: String = "non-existing-artifact"
    val RepositoryName: String          = "mutua-releases-lib"

    val Gav: GAV = GAV(GroupName, ArtifactName, Version)

    //TODO properties
    val NexusSearchPath = "/nexus/service/local/lucene/search"
  }
}
