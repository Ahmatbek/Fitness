package kg.biamino.projects.dto;

import kg.biamino.projects.model.User;

public record NewUserCredentials(User user, String password) {}
