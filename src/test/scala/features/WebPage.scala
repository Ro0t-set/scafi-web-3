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
class WebPageSteps extends ScalaDsl with EN:

  println(System.getProperty("testEnv", "ci"))
  println(System.getProperty("browser", "firefox"))

  private val env     = System.getProperty("testEnv", "ci")
  private val isLocal = env == "local"
  var driver: WebDriver = BrowserFactory.createDriver(
    System.getProperty("browser", "firefox"),
    isLocal
  )

  Given("I am on the Scafi Web Page") {
    driver.get("http://localhost:8080")
  }

  Then("the page title should start with {string}") {
    (titleStartsWith: String) =>
      WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleContains(titleStartsWith))
  }

  Then("the engine {string} is loaded") { (engine: String) =>
    WebDriverWait(driver, Duration.ofSeconds(10))
      .until(ExpectedConditions.jsReturnsValue(s"return $engine.name"))
  }

  Then("the canvas {string} is loaded") { (canvasClassName: String) =>
    if !isLocal then println(s"Skipping canvas check in CI/CD mode (env=$env)")
    else
      WebDriverWait(driver, Duration.ofSeconds(10)).until(
        ExpectedConditions.visibilityOfElementLocated(
          By.xpath(s"//*[@id='$canvasClassName']/canvas")
        )
      )
  }

  Then(
    "the graph 10x10x2 should support more than {string} updates per second"
  ) {
    (fps: String) =>
      if !isLocal then
        println(s"Skipping canvas check in CI/CD mode (env=$env)")
      else
        WebDriverWait(driver, Duration.ofSeconds(10))
          .until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[@id=\"app\"]/div/div[3]/div[2]/button[1]")
          ))

        driver.findElement(
          By.xpath("//*[@id=\"app\"]/div/div[3]/div[2]/button[1]")
        ).click()

        Thread.sleep(1000)

        val currentTick: Long = driver
          .asInstanceOf[JavascriptExecutor]
          .executeScript(
            "return AnimationState.currentTick." +
              "Lcom_raquo_airstream_state_VarSignal__f_maybeLastSeenCurrentValue." +
              "s_util_Success__f_value"
          )
          .asInstanceOf[Long]

        println(s"Current tick: $currentTick")

        assert(
          currentTick > fps.toInt,
          s"Expected currentTick to be > $fps but it was $currentTick"
        )
  }

  After("@web") { (scenario: Scenario) =>
    println(s"[${scenario.getName}] => Tearing down driver. Mode: $env")
    if driver != null then
      driver.quit()

  }
