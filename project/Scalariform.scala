import com.typesafe.sbt.SbtScalariform.autoImport.scalariformPreferences
import scalariform.formatter.preferences.{AlignParameters, AlignSingleLineCaseStatements, IndentWithTabs}

object Scalariform {

  scalariformPreferences := scalariformPreferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(IndentWithTabs, false)
    .setPreference(AlignParameters, true)
}
