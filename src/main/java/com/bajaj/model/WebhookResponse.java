package com.bajaj.model;

import lombok.Data;
import java.util.List;

@Data
public class WebhookResponse {
    private String webhook;
    private String accessToken;
    private Data data;

    @Data
    public static class Data {
        private List<User> users;
    }

    @Data
    public static class User {
        private int id;
        private String name;
        private List<Integer> follows;
    }
} 