package features

import io.cucumber.scala.EN
import io.cucumber.scala.ScalaDsl
import io.cucumber.scala.Scenario
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

import java.time.Duration

object BrowserFactory:
  def createDriver(browserType: String, isLocal: Boolean): WebDriver =
    browserType.toLowerCase match
      case "firefox" =>
        val options = new FirefoxOptions()
        if (!isLocal) {
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
          options.addArguments("--headless")
        }
        FirefoxDriver(options)

      case "chrome" =>
        val options = new ChromeOptions()
        if (!isLocal) {
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
          options.addArguments("--headless")
        }
        ChromeDriver(options)

      case "edge" =>
        val options = new EdgeOptions()
        if (!isLocal) {
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
          options.addArguments("--headless")
        }
        EdgeDriver(options)

@SuppressWarnings(Array("org.wartremover.warts.All"))
class WebPageSteps extends ScalaDsl with EN {
  private val TestConfig = {
    val env     = System.getProperty("testEnv", "ci")
    val browser = System.getProperty("browser", "firefox")
    println(s"Test Configuration: env=$env, browser=$browser")
    (env, env == "local", browser)
  }

  private val (env, isLocal, browser) = TestConfig
  private lazy val driver: WebDriver =
    BrowserFactory.createDriver(browser, isLocal)
  private lazy val timeOut = new WebDriverWait(driver, Duration.ofSeconds(10))

  private object Selectors {
    val Canvas = "#app canvas"
    val StartButton =
      "#app > div > div.animation-controller > div.controls > button:nth-child(1)"
  }

  Given("I am on the Scafi Web Page") { () =>
    driver.get("http://localhost:8080")
  }

  Then("the page title should start with {string}") { (prefix: String) =>
    timeOut.until(ExpectedConditions.titleContains(prefix))
  }

  Then("the engine {string} is loaded") { (engine: String) =>
    timeOut.until(ExpectedConditions.jsReturnsValue(s"return $engine.name"))
  }

  Then("the canvas {string} is loaded") { (canvasId: String) =>
    if (isLocal) {
      timeOut.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector(s"#$canvasId canvas")
      ))
    } else {
      println(s"Skipping canvas check in CI/CD mode (env=$env)")
    }
  }

  Then(
    "the graph 10x10x2 should support more than {string} updates per second"
  ) { (minFps: String) =>
    if (isLocal) {
      timeOut.until(ExpectedConditions.elementToBeClickable(
        By.cssSelector(Selectors.StartButton)
      )).click()

      Thread.sleep(1000) // Allow initial tick collection

      val currentTick = getCurrentTick
      println(s"Performance check - Current tick: $currentTick")
      assert(
        currentTick > minFps.toInt,
        s"Expected > $minFps ticks, got $currentTick"
      )
    } else {
      println(s"Skipping performance check in CI/CD mode (env=$env)")
    }
  }

  After("@web") { (scenario: Scenario) =>
    println(s"[${scenario.getName}] => Tearing down driver. Mode: $env")
    if driver != null then
      driver.quit()
  }

  private def getCurrentTick: Long = {
    val jsExecutor = driver.asInstanceOf[JavascriptExecutor]
    jsExecutor.executeScript(
      """return AnimationState.currentTick
        |  .Lcom_raquo_airstream_state_VarSignal__f_maybeLastSeenCurrentValue
        |  .s_util_Success__f_value""".stripMargin
    ).asInstanceOf[Long]
  }
}
