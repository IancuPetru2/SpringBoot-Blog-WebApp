package net.projects.springboot.dto;

import java.util.List;

public record PostSummaryResponse(String hook, String excerpt, List<String> tldr) {}