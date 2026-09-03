Feature: Getting trainer workload summary
  As a client of the trainer service
  I want to retrieve a trainer's workload summary by year and month
  using their username

  Scenario: Successful retrieval of an existing trainer's summary
    Given a valid JWT for an authenticated user
    When the summary for trainer "Akhmatbek.Tursunbaev" is requested
    Then the response status code should be 200
    And the summary response should match the seeded workload for "Akhmatbek.Tursunbaev"

  Scenario: Retrieval fails for an unknown trainer
    Given a valid JWT for an authenticated user
    When the summary for trainer "no-such-trainer" is requested
    Then the response status code should be 404

  Scenario: Retrieval fails without an Authorization header
    Given no Authorization header is provided
    When the summary for trainer "Akhmatbek.Tursunbaev" is requested
    Then the response status code should be 401

  Scenario: Retrieval fails with an invalid JWT
    Given an invalid JWT
    When the summary for trainer "Akhmatbek.Tursunbaev" is requested
    Then the response status code should be 401
