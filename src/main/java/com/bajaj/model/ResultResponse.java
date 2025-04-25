package com.bajaj.model;

import lombok.Data;
import java.util.List;

@Data
public class ResultResponse {
    private String regNo;
    private List<List<Integer>> outcome;
} 