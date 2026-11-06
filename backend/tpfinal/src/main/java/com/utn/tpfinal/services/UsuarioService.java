package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.usuario.UsuarioCreate;
import com.utn.tpfinal.dtos.usuario.UsuarioDto;
import com.utn.tpfinal.dtos.usuario.UsuarioEdit;
import com.utn.tpfinal.entities.Usuario;
import com.utn.tpfinal.enums.Rol;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDto crear(UsuarioCreate dto) {
        if (dto.getRol() == Rol.NUTRICIONISTA) {
            throw new IllegalArgumentException("Los nutricionistas se crean desde /api/nutricionistas");
        }
        if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());

        return aDto(usuarioRepository.save(usuario));
    }

    public List<UsuarioDto> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::aDto).toList();
    }

    public UsuarioDto buscarPorId(Long id) {
        return aDto(obtener(id));
    }

    public UsuarioDto editar(Long id, UsuarioEdit dto) {
        Usuario usuario = obtener(id);

        if (dto.getRol() != null && dto.getRol() != usuario.getRol()) {
            if (dto.getRol() == Rol.NUTRICIONISTA || usuario.getRol() == Rol.NUTRICIONISTA) {
                throw new IllegalArgumentException("No se puede cambiar el rol hacia o desde NUTRICIONISTA");
            }
            usuario.setRol(dto.getRol());
        }
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }

        return aDto(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
    }

    private UsuarioDto aDto(Usuario u) {
        return new UsuarioDto(u.getId(), u.getUsername(), u.getRol(), u.getActivo());
    }
}