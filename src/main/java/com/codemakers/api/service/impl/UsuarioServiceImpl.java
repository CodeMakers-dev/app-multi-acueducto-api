package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.IUsuarioService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.UsuarioDTO;
import com.codemakers.commons.entities.PersonaEntity;
import com.codemakers.commons.entities.RolEntity;
import com.codemakers.commons.entities.UsuarioEntity;
import com.codemakers.commons.maps.UsuarioMapper;
import com.codemakers.commons.repositories.PersonaRepository;
import com.codemakers.commons.repositories.RolRepository;
import com.codemakers.commons.repositories.UsuarioRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nicope
 * @version 1.0
 * 
 *          Clase que implementa la interfaz de la lógica de negocio.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final PersonaRepository personaRepository;
	private final UsuarioMapper usuarioMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Override
    public ResponseEntity<ResponseDTO> save(UsuarioDTO usuarioDTO) {
        log.info("Guardar/Actualizar usuario");
        try {
            boolean isUpdate = usuarioDTO.getId() != null && usuarioRepository.existsById(usuarioDTO.getId());
            UsuarioEntity entity;

            if (isUpdate) {
                entity = usuarioRepository.findById(usuarioDTO.getId()).orElseThrow();
                usuarioMapper.updateEntityFromDto(usuarioDTO, entity);
                entity.setFechaModificacion(new Date());
                entity.setUsuarioModificacion(usuarioDTO.getUsuarioModificacion());
                if (usuarioDTO.getContrasena() != null && !usuarioDTO.getContrasena().isEmpty()) {
                    entity.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
                    }
            } else {
                entity = usuarioMapper.dtoToEntity(usuarioDTO);
                entity.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
                entity.setFechaCreacion(new Date());
                entity.setUsuarioCreacion(usuarioDTO.getUsuarioCreacion());
                entity.setActivo(true);
            }

            if (usuarioDTO.getRol() != null && usuarioDTO.getRol().getId() != null) {
                RolEntity rol = rolRepository.findById(usuarioDTO.getRol().getId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrada"));
                entity.setRol(rol);
            }
            if (usuarioDTO.getPersona() != null && usuarioDTO.getPersona().getId() != null) {
                PersonaEntity persona = personaRepository.findById(usuarioDTO.getPersona().getId())
                    .orElseThrow(() -> new RuntimeException("Persona no encontrado"));
                entity.setPersona(persona);
            }

            UsuarioEntity saved = usuarioRepository.save(entity);
            UsuarioDTO savedDTO = usuarioMapper.entityToDto(saved);

            String message = isUpdate ? Constantes.UPDATED_SUCCESSFULLY : Constantes.SAVED_SUCCESSFULLY;
            int statusCode = isUpdate ? HttpStatus.OK.value() : HttpStatus.CREATED.value();

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(message)
                    .code(statusCode)
                    .response(savedDTO)
                    .build();

            return ResponseEntity.status(statusCode).body(responseDTO);

        } catch (Exception e) {
            log.error("Error guardando usuario", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.SAVE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar usuario por id: {}", id);
	    try {
	        Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
	        if (usuario.isPresent()) {
	            UsuarioDTO dto = usuarioMapper.entityToDto(usuario.get());
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(true)
	                    .message(Constantes.CONSULTED_SUCCESSFULLY)
	                    .code(HttpStatus.OK.value())
	                    .response(dto)
	                    .build();
	            return ResponseEntity.ok(responseDTO);
	        } else {
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(false)
	                    .message(Constantes.CONSULTING_ERROR)
	                    .code(HttpStatus.NOT_FOUND.value())
	                    .build();
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
	        }
	    } catch (Exception e) {
	        log.error("Error al buscar el usuario por id: {}", id, e);
	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.ERROR_QUERY_RECORD_BY_ID)
	                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
	                .build();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
	    }
	}

    @Override
    public ResponseEntity<ResponseDTO> findAll() {
        log.info("Listar todos los usuario");
        try {
            var list = usuarioRepository.findAll();
            var dtoList = usuarioMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los usuarios", e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.CONSULTING_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .response(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteById(Integer id) {
        log.info("Inicio método para eliminar usuario por id: {}", id);
        try {
            if (!usuarioRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            usuarioRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar el usuario con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
