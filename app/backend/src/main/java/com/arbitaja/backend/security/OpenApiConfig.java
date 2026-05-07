package com.arbitaja.backend.security;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.BASE_URL}")
    private String BASE_URL;
    @Value("${app.BASE_PATH}")
    private String SERVER_ENDPOINT;
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("My API").version("1.0"))
                .addServersItem(new Server().url(BASE_URL + SERVER_ENDPOINT))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic"))
                        .addSecuritySchemes("apiKeyAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }

    @Bean
    public OpenApiCustomizer loginFormCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                openApi.setPaths(new Paths());
            }

            Operation loginOperation = new Operation()
                    .summary("Authenticate with username/password")
                    .description("Spring Security form login endpoint. Submit as application/x-www-form-urlencoded with username, password, and optional rememberMe.")
                    .security(List.of())
                    .requestBody(new RequestBody()
                            .required(true)
                            .content(new Content().addMediaType("application/x-www-form-urlencoded",
                                    new MediaType().schema(new ObjectSchema()
                                            .addProperty("username", new Schema<>().type("string"))
                                            .addProperty("password", new Schema<>().type("string").format("password"))
                                            .addProperty("rememberMe", new Schema<>().type("boolean"))
                                            .addRequiredItem("username")
                                            .addRequiredItem("password")))))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Login successful"))
                            .addApiResponse("302", new ApiResponse().description("Redirected after login"))
                            .addApiResponse("401", new ApiResponse().description("Invalid credentials")));

            openApi.getPaths().addPathItem("/login-user", new PathItem().post(loginOperation));
        };
    }
}
