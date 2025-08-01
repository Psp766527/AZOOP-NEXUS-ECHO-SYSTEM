package com.azoopindia.it.asi.traffic.manager.application.controller;

import com.azoopindia.it.asi.traffic.manager.model.dto.GraphQLOperationRegistrationRequest;
import com.azoopindia.it.asi.traffic.manager.service.GraphQLOperationRoutingService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Controller for registering GraphQL operations for a specific service.
 * This endpoint is used by services to publish the set of GraphQL operations
 * they support, enabling dynamic routing of incoming queries.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "GraphQL Operation Registry", description = "APIs for registering GraphQL operations by service")
public class GraphQLOperationRegistryController {

    private final GraphQLOperationRoutingService registryService;

    /**
     * Registers GraphQL operations for the specified service.
     *
     * @param req the registration request containing the service ID and a list of operations
     * @return {@code 200 OK} if the operations were registered successfully
     */
    @PostMapping("/register-graphql-ops")
    @Operation(
            summary = "Register GraphQL operations",
            description = "Registers a list of GraphQL operations for a service, enabling query routing.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GraphQLOperationRegistrationRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully registered operations"),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Void> register(@org.springframework.web.bind.annotation.RequestBody GraphQLOperationRegistrationRequest req) {
        registryService.register(req.getServiceId(), req.getOperations());
        return ResponseEntity.ok().build();
    }
}
