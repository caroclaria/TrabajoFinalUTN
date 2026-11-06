package com.utn.tpfinal;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TpfinalApplication {

    public static void main(String[] args) {
        // Los servidores en la nube (Render) usan UTC. Fijamos la zona horaria de Argentina para que la
        // fecha/hora de las evoluciones y las altas coincida con la del sanatorio.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));

        // Lee el .env (si existe) y lo deja disponible para ${DB_URL}, ${JWT_SECRET}, etc.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(e -> {
            if (System.getenv(e.getKey()) == null) {   // una variable real del sistema tiene prioridad
                System.setProperty(e.getKey(), e.getValue());
            }
        });

        SpringApplication.run(TpfinalApplication.class, args);
    }
}