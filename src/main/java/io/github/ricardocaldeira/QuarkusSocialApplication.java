package io.github.ricardocaldeira;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

// Classe usada para exibição de metadados do swagger
// Não é necessária para funcionamento do swagger. Apenas exibição de infos
@OpenAPIDefinition(
        info = @Info(
                title = "API Quarkus Social",
                version = "1.0.0",
                contact = @Contact(
                        name = "Ricardo Caldeira",
                        url = "https://github.com/RicardoCaldeira",
                        email = "rcscaldeiradev@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://apache.org/licenses/LICENSE-2.0.html"
                )

        )
)
public class QuarkusSocialApplication extends Application {
}
