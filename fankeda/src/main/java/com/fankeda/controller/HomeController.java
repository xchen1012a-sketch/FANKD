package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    ApiResponse<ApiIndexResponse> index() {
        return ApiResponse.ok(new ApiIndexResponse(
            "饭刻达 MVP API",
            List.of(
                "POST /api/auth/login",
                "GET /api/stalls",
                "GET /api/recommend?rushMode=false",
                "POST /api/queue/report",
                "GET /api/queue/latest",
                "POST /api/feedback",
                "GET /api/user/profile",
                "PUT /api/user/profile"
            )
        ));
    }

    record ApiIndexResponse(String name, List<String> endpoints) {
    }
}
