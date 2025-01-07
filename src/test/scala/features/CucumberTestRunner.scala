package features

import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("src/test/resources/features"),
  glue = Array("features"),
  plugin = Array("pretty", "html:target/cucumber-reports.html")
)
class CucumberTestRunner
