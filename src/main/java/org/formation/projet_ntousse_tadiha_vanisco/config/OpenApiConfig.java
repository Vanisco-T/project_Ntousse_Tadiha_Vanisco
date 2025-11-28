package org.formation.projet_ntousse_tadiha_vanisco.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI simpleCashOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Environnement de développement");

        Contact contact = new Contact();
        contact.setName("Support SimpleCash");
        contact.setEmail("support@simplecash.com");

        Info info = new Info()
                .title("SimpleCashSI API")
                .version("1.0.0")
                .contact(contact)
                .description("API REST pour la gestion des opérations bancaires SimpleCash")
                .termsOfService("http://simplecash.com/terms")
                .license(new License().name("MIT").url("http://simplecash.com/license"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
