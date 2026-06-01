package com.fankeda.dto;

public record LoginResponse(String token, UserProfileResponse user) {
}
