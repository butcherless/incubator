val ReleaseCommand = Command.command("release") {
  state =>
    "assembly" :: "doc" :: state
}

val CoverageShortcut = Command.command("mycoverage") {
  state =>
    "clean" :: "coverage" :: "test" :: "coverageReport" :: "coverageAggregate" :: state
}

commands ++= Seq(ReleaseCommand, CoverageShortcut)