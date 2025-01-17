package com.arbitaja.backend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @Operation(
            summary = "Health check",
            description = "Send OK if backend is up"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Send OK if backend is up"
            )})
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().build();
    }
}
