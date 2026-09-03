Feature: Fitness and trainer service integration
  As the fitness service publishing training events over ActiveMQ
  I want the trainer service to consume them
  and keep an accurate workload summary reachable through its own API

  Background:
    Given a registered trainee
    And a registered trainer

  Scenario: A newly created training is reflected in the trainer's workload summary
    When a training is created for 60 minutes 1 month(s) from now
    Then the trainer's summary should eventually show 60 minutes for 1 month(s) from now

  Scenario: Two trainings in the same month accumulate in the trainer's workload summary
    When a training is created for 40 minutes 2 month(s) from now
    And a training is created for 25 minutes 2 month(s) from now
    Then the trainer's summary should eventually show 65 minutes for 2 month(s) from now

  Scenario: A training referencing an unknown trainee is rejected and never reaches the trainer
    When a training is created for an unknown trainee for 30 minutes 1 month(s) from now
    Then the training creation should be rejected with status 404
    And the trainer should have no workload summary

  Scenario: Deleting a training removes its duration from the trainer's workload summary
    Given a training was created for 50 minutes 4 month(s) from now
    When the training is deleted
    Then the trainer's summary should eventually show 0 minutes for 4 month(s) from now
