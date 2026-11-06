package com.utn.tpfinal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Este test levanta toda la aplicación, así que necesita la base de datos y las variables del .env.
// Por eso está deshabilitado: así "mvnw spring-boot:run" y "mvnw package" no dependen de la base.
// Para correrlo a mano, quitar @Disabled y definir DB_URL, DB_USERNAME, DB_PASSWORD y JWT_SECRET en el entorno.
@Disabled("Requiere base de datos y variables de entorno")
@SpringBootTest
class TpfinalApplicationTests {

	@Test
	void contextLoads() {
	}

}