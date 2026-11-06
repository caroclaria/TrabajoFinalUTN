package com.utn.tpfinal.config;

import com.utn.tpfinal.entities.Usuario;
import com.utn.tpfinal.enums.Rol;
import com.utn.tpfinal.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// El admin inicial se crea automáticamente al iniciar la app
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.username}") String adminUsername,
                           @Value("${app.admin.password}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminPassword.isBlank() || usuarioRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRol(Rol.ADMINISTRADOR);
        usuarioRepository.save(admin);
    }
}