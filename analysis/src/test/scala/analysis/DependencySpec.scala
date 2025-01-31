package analysis

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import munit.FunSuite

class DependencySpec extends FunSuite:

  private def noDependTest(
      testName: String,
      importRoot: String,
      packageToCheck: String,
      forbiddenPackages: Seq[String],
      becauseMsg: String
  ): Unit =
    test(testName) {
      val importedClasses = new ClassFileImporter()
        .importPackages(importRoot)

      val rule = ArchRuleDefinition.noClasses()
        .that()
        .resideInAPackage(packageToCheck)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(forbiddenPackages: _*)
        .because(becauseMsg)

      rule.check(importedClasses)
    }

  noDependTest(
    testName =
      "Domain package should only depend on itself and standard libraries",
    importRoot = "domain..",
    packageToCheck = "..domain..",
    forbiddenPackages = Seq("..laminar..", "..state..", "..js.."),
    becauseMsg =
      "Domain layer should be isolated from infrastructure and application layers"
  )

  noDependTest(
    testName = "State package should only depend on itself and Laminar",
    importRoot = "state..",
    packageToCheck = "..state..",
    forbiddenPackages = Seq("..view..", "..API.."),
    becauseMsg =
      "State layer should be isolated from infrastructure and application layers"
  )
