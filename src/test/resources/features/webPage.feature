Feature: Web Page Is Working

    @web
    Scenario: Page is loaded
        Given I am on the Scafi Web Page
        Then the page title should start with "ScaFi Web 3"
        Then the engine "EngineImpl" is loaded
        Then the canvas "three_canvas" is loaded

    @web
    Scenario: Compile an Aggregated Program with Scastie
        Given I am logged into my Scastie account
        When I upload an aggregated program
        And I click on "Compile"
        Then the compilation should be successful

    @web
    Scenario: 3D Graph Viewer Interaction
        Given I am on the Scafi Web Page
        When I load an aggregated program
        Then a 3D graph should be rendered
        And the graph should support zoom and rotation

    @web
    Scenario: Graph Player Controls
        Given a graph is displayed
        When I press "Play"
        Then the graph should start updating
        When I press "Pause"
        Then the graph should stop updating
        When I adjust the speed slider
        Then the graph update rate should change

    @web
    Scenario: Graph Size Modification
        Given a graph is displayed
        When I modify the graph size to "100 nodes"
        Then the graph should reflect the new size

    @web
    Scenario: Test Integration with Cucumber and Selenium
        Given I am testing the Scafi Web Page
        When I run the Cucumber tests
        Then the Selenium WebDriver should verify the graphical elements

    @web
    Scenario: CI/CD Pipeline for Testing and Deployment
        Given the CI/CD pipeline is configured
        When code is pushed to the repository
        Then automated tests should run
        And the web application should be deployed automatically

    @web
    Scenario: High Update Rate Support
        Given the graph is playing
        Then the graph should support more than 30 updates per second
