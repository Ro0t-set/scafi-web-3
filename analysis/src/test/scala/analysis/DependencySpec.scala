package analysis

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import munit.FunSuite

class DependencySpec extends FunSuite:

  test(
    "The package 'domain' should not depend on other packages within this project"
  ) {
    val importedClasses = new ClassFileImporter()
      .importPackages("domain")

    val allowedPackages = Seq(
      "domain..",
      "java..",
      "scala.."
    )

    val rule = classes()
      .that()
      .resideInAPackage("domain..")
      .should()
      .onlyDependOnClassesThat()
      .resideInAnyPackage(allowedPackages: _*)
    rule.check(importedClasses)

  }
