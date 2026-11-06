package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.nutricionista.NutricionistaCreate;
import com.utn.tpfinal.dtos.nutricionista.NutricionistaDto;
import com.utn.tpfinal.dtos.nutricionista.NutricionistaEdit;
import com.utn.tpfinal.entities.Nutricionista;
import com.utn.tpfinal.enums.Rol;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.NutricionistaRepository;
import com.utn.tpfinal.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class NutricionistaService {

    private final NutricionistaRepository nutricionistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public NutricionistaService(NutricionistaRepository nutricionistaRepository,
                                UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.nutricionistaRepository = nutricionistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public NutricionistaDto crear(NutricionistaCreate dto) {
        if (nutricionistaRepository.findByMatricula(dto.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un nutricionista con esa matrícula");
        }
        if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username");
        }

        Nutricionista nutricionista = new Nutricionista();
        nutricionista.setUsername(dto.getUsername());
        nutricionista.setPassword(passwordEncoder.encode(dto.getPassword()));
        nutricionista.setRol(Rol.NUTRICIONISTA);
        nutricionista.setNombre(dto.getNombre());
        nutricionista.setApellido(dto.getApellido());
        nutricionista.setMatricula(dto.getMatricula());

        return aDto(nutricionistaRepository.save(nutricionista));
    }

    public List<NutricionistaDto> listarTodos() {
        return nutricionistaRepository.findAll().stream().map(this::aDto).toList();
    }

    public NutricionistaDto buscarPorId(Long id) {
        return aDto(obtener(id));
    }

    public NutricionistaDto editar(Long id, NutricionistaEdit dto) {
        Nutricionista nutricionista = obtener(id);

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            nutricionista.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null && !dto.getApellido().isBlank()) {
            nutricionista.setApellido(dto.getApellido());
        }

        return aDto(nutricionistaRepository.save(nutricionista));
    }

    public void eliminar(Long id) {
        if (!nutricionistaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Nutricionista no encontrado con id " + id);
        }
        nutricionistaRepository.deleteById(id);
    }

    private Nutricionista obtener(Long id) {
        return nutricionistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nutricionista no encontrado con id " + id));
    }

    private NutricionistaDto aDto(Nutricionista n) {
        return new NutricionistaDto(n.getId(), n.getNombre(), n.getApellido(), n.getMatricula());
    }
}