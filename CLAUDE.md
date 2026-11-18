# Incubator — CLAUDE.md

Research project of small concept tests (Scala 3, sbt 2, ZIO). One sbt project per concept;
`build.sbt` is the source of truth for the module list and their dependencies.

## Build and verify

sbt 2, so use `testFull`, not `test` (`test` is `testQuick` and can pass with zero tests run):

```bash
sbt "clean; scalafmtAll; scalafmtSbt; compile; testFull"
```

`compile` must finish with zero warnings, including the meta-build (`project/*.scala`). CI runs
`compile`, `testFull`, `assembly` and `dependencyUpdates` on Java 21.

## Versioning policy

- **Scala** — always the latest LTS or newer (`project/Versions.scala`), never a mainline "Next"
  release. Renovate can't tell LTS from mainline, so check it with the `bump-versions` skill.
- **JDK** — Java 21 LTS, locally and in CI.
- **SBT** — the 2.x line (Renovate tracks it).
- **Direct deps** — stable GA. Named exception: **ZIO Prelude 1.x** (`1.0.0-RC48`), which has no
  GA release yet; don't chase a newer RC/M/SNAPSHOT without a GA release or a needed capability.
- **Everything else** (libraries, sbt plugins, sbt, scalafmt, GitHub Actions) — Renovate tracks
  and automerges these (`renovate.json`), so no manual bump is needed.
- **Unused declarations** — every entry in `project/Dependencies.scala` must be used by a project
  in `build.sbt`. `dependencyUpdates` never checks an unused one, so delete it rather than bump it.
