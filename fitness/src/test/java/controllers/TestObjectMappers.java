package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

final class TestObjectMappers {

    private TestObjectMappers() {
    }

    static ObjectMapper create() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
