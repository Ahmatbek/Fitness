Feature: User Login
  As a registered user
  I want to log in to the application
  using my account credentials

  Background:
    Given the application is running

  Scenario: Successful login with valid credentials
    Given a registered trainee
    When the user attempts to log in
    Then the response should contain a valid token

  Scenario: Failed login with wrong password
    Given a registered trainee
    And the wrong password is used instead
    When the user attempts to log in
    Then the response should indicate an authentication failure

  Scenario Outline: Login rejects invalid credential formats
    Given a user with username "<username>" and password "<password>"
    When the user attempts to log in
    Then the response should indicate <outcome>

    Examples:
      | username           | password | outcome                   |
      | invalid-email      | pass123  | an authentication failure |
      |                    | pass123  | a bad request             |
      | user@example.com   |          | a bad request             |
