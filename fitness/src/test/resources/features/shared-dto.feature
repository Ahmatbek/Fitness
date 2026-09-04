Feature: Shared contract check
  As the fitness service producing training events
  I want every TrainerWorkloadRequest message
  to satisfy the contract shared with the trainer service

  Background:
    Given the application is running

  Scenario: A valid ADD workload request is delivered to the consumer
    When a TrainerWorkloadRequest is created with trainerUsername "Bekzat.Isakov" and actionType "ADD"
    Then the consumer should receive a valid TrainerWorkloadRequest
    And the incoming dto should match the "schemas/shared-object-dto.json" schema contract

  Scenario: A valid DELETE workload request is delivered to the consumer
    When a TrainerWorkloadRequest is created with trainerUsername "Bekzat.Isakov" and actionType "DELETE"
    Then the consumer should receive a valid TrainerWorkloadRequest
    And the incoming dto should match the "schemas/shared-object-dto.json" schema contract
