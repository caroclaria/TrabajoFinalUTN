package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.auth.LoginDto;
import com.utn.tpfinal.dtos.auth.LoginRequest;
import com.utn.tpfinal.dtos.auth.MeDto;
import com.utn.tpfinal.entities.Nutricionista;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.entities.Usuario;
import com.utn.tpfinal.exceptions.CredencialesInvalidasException;
import com.utn.tpfinal.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public LoginDto login(LoginRequest request) {
        // Mismo mensaje si el usuario no existe, está inactivo o la contraseña es incorrecta:
        // así no se le revela a un atacante cuál de las tres cosas falló.
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .filter(Usuario::getActivo)
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .orElseThrow(CredencialesInvalidasException::new);

        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getUsername())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expirationMinutes, ChronoUnit.MINUTES))
                .claim("rol", usuario.getRol().name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new LoginDto(token, usuario.getUsername(), usuario.getRol());
    }

    // Perfil del usuario logueado (RF01). Si es nutricionista, incluye nombre, apellido y matrícula.
    public MeDto me(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (usuario instanceof Nutricionista n) {
            return new MeDto(n.getId(), n.getUsername(), n.getRol(), n.getNombre(), n.getApellido(), n.getMatricula());
        }
        return new MeDto(usuario.getId(), usuario.getUsername(), usuario.getRol(), null, null, null);
    }
}
