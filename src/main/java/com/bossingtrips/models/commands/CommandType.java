package com.bossingtrips.models.commands;

public enum CommandType {
    START("START"),
    END("END"),
    RESET("RESET"),
    GET("GET");

    final String name;

    CommandType(String name) {
        this.name = name;
    }
}
