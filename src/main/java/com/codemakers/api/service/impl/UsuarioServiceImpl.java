package com.codemakers.api.service.impl;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemakers.api.config.JwtUtil;
import com.codemakers.api.service.IUsuarioService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.UsuarioDTO;
import com.codemakers.commons.entities.CorreoPersonaEntity;
import com.codemakers.commons.entities.PersonaEntity;
import com.codemakers.commons.entities.RolEntity;
import com.codemakers.commons.entities.UsuarioEntity;
import com.codemakers.commons.maps.UsuarioMapper;
import com.codemakers.commons.repositories.CorreoPersonaRepository;
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
	private final CorreoPersonaRepository correoPersonaRepository;
	private final RolRepository rolRepository;
	private final PersonaRepository personaRepository;
	private final UsuarioMapper usuarioMapper;
	private final PasswordEncoder passwordEncoder;
	private final EmailServiceImpl emailService;
	private final JwtUtil jwtUtil;
	
	 
	public ResponseEntity<ResponseDTO> updateImage(Integer id, byte[] nuevaImagen, String usuarioModificacion) {
	    log.info("Inicio de actualización de imagen para el usuario con ID: {}", id);
	    try {
	        Optional<UsuarioEntity> optionalUsuario = usuarioRepository.findById(id);
	        if (optionalUsuario.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(ResponseDTO.builder().success(false)
	                            .message(Constantes.USER_NOT_FOUND)
	                            .code(HttpStatus.NOT_FOUND.value()).build());
	        }

	        UsuarioEntity usuario = optionalUsuario.get();
	        usuario.setImagen(nuevaImagen);
	        usuario.setFechaModificacion(new Date());
	        usuario.setUsuarioModificacion(usuarioModificacion);

	        usuarioRepository.save(usuario);

	        String imagenBase64 = Base64.getEncoder().encodeToString(nuevaImagen);
	        Map<String, Object> responseData = new HashMap<>();
	        responseData.put("imagenBase64", imagenBase64);

	        return ResponseEntity.ok(ResponseDTO.builder()
	                .success(true)
	                .message("Imagen actualizada exitosamente")
	                .code(HttpStatus.OK.value())
	                .response(responseData)
	                .build());

	    } catch (Exception e) {
	        log.error("Error al actualizar la imagen del usuario con ID: {}", id, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(ResponseDTO.builder().success(false)
	                        .message("Error actualizando la imagen")
	                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
	    }
	}


	public ResponseEntity<ResponseDTO> recoverPassword(String correo) {
		log.info("Recuperación de contraseña solicitada para: {}", correo);

		try {
			Optional<CorreoPersonaEntity> correoPersonaOpt = correoPersonaRepository.findByCorreo(correo);

			if (correoPersonaOpt.isEmpty() || correoPersonaOpt.get().getPersona() == null) {
				return buildErrorResponse(Constantes.EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
			}
			Integer idPersona = correoPersonaOpt.get().getPersona().getId();
			Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByPersonaId(idPersona);

			if (usuarioOpt.isEmpty()) {
				return buildErrorResponse(Constantes.USER_NOT_ASCIATED, HttpStatus.NOT_FOUND);
			}
			UsuarioEntity usuario = usuarioOpt.get();
			String token = jwtUtil.generateToken(usuario.getNombre());
			String recoveryLink = "http://localhost:4200/auth/recover-password?token=" + token;

			String subject = "🔐 Recuperación de contraseña";
			String body = """
					<!DOCTYPE html>
					<html lang="es">
					<head>
					  <meta charset="UTF-8">
					  <style>
					    body {
					      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
					      background: #f4f7ff;
					      margin: 0;
					      padding: 0;
					    }

					    .container {
					      max-width: 600px;
					      margin: 40px auto;
					      background: white;
					      border-radius: 12px;
					      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
					      overflow: hidden;
					    }

					    .header {
					      background: linear-gradient(135deg, #3b82f6, #60a5fa);
					      color: white;
					      padding: 24px;
					      text-align: center;
					      font-size: 1.8em;
					      font-weight: bold;
					      border-top-left-radius: 12px;
					      border-top-right-radius: 12px;
					    }

					    .content {
					      padding: 30px;
					      text-align: center;
					    }

					    .btn {
					      display: inline-block;
					      padding: 12px 24px;
					      margin-top: 20px;
					      background-color: #3b82f6;
					      color: white;
					      border-radius: 8px;
					      text-decoration: none;
					      font-weight: bold;
					    }

					    .btn:hover {
					      background-color: #2563eb;
					    }

					    .footer {
					      font-size: 0.85em;
					      color: #666;
					      padding: 0 30px 30px;
					      text-align: center;
					    }

					    a {
					      color: #3b82f6;
					      text-decoration: none;
					    }
					  </style>
					</head>
					<body>
					  <div class="container">
					    <div class="header">MultiAcueductos</div>
					    <div class="content">
					      <p>Hola <strong>%s</strong>,</p>
					      <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
					      <p>Haz clic en el siguiente botón para continuar:</p>
					      <a href="%s" class="btn">Restablecer contraseña</a>
					    </div>
					    <div class="footer">
					      <p>Este enlace expirará en 10 horas.<br>
					      Si no hiciste esta solicitud, puedes ignorar este mensaje.</p>
					    </div>
					  </div>
					</body>
					</html>
					""".formatted(usuario.getNombre(), recoveryLink);

			emailService.sendEmail(correo, subject, body);

			return ResponseEntity.ok(ResponseDTO.builder().success(true).message(Constantes.EMAIL_SEND)
					.code(HttpStatus.OK.value()).build());

		} catch (Exception e) {
			log.error("Error durante la recuperación de contraseña para el correo {}", correo, e);
			return buildErrorResponse(Constantes.ERROR_APPLICATION, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<ResponseDTO> updatePasswordByToken(String token, UsuarioDTO usuarioDTO) {
		log.info("Inicio de actualización de contraseña usando token");

		if (token == null || token.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().success(false)
					.message("Token requerido").code(HttpStatus.UNAUTHORIZED.value()).build());
		}

		if (jwtUtil.isTokenInvalid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().success(false)
					.message("Token inválido o expirado").code(HttpStatus.UNAUTHORIZED.value()).build());
		}

		String username = jwtUtil.getUsernameFromToken(token);
		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().success(false)
					.message("Token expirado o sin usuario válido").code(HttpStatus.UNAUTHORIZED.value()).build());
		}

		return usuarioRepository.findByNombre(username).map(usuario -> {
			String nuevaContrasena = usuarioDTO.getContrasena();

			String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_.])[A-Za-z\\d@$!%*?&\\-_.]{8,}$";
			if (nuevaContrasena == null || !nuevaContrasena.matches(passwordRegex)) {
				return ResponseEntity.badRequest().body(ResponseDTO.builder().success(false).message(
						"La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, un número y un carácter especial.")
						.code(HttpStatus.BAD_REQUEST.value()).build());
			}

			if (passwordEncoder.matches(nuevaContrasena, usuario.getContrasena())) {
				return ResponseEntity.badRequest()
						.body(ResponseDTO.builder().success(false)
								.message("La nueva contraseña debe ser diferente a la actual.")
								.code(HttpStatus.BAD_REQUEST.value()).build());
			}

			usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
			usuario.setFechaModificacion(new Date());
			usuario.setUsuarioModificacion("Recuperación vía token");

			usuarioRepository.save(usuario);

			jwtUtil.invalidateToken(token);
			return ResponseEntity.ok(ResponseDTO.builder().success(true).message("Contraseña actualizada exitosamente")
					.code(HttpStatus.OK.value()).build());
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().success(false)
				.message("Usuario no encontrado").code(HttpStatus.NOT_FOUND.value()).build()));
	}

	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> save(UsuarioDTO usuarioDTO) {
		log.info("Guardar/Actualizar usuario");
		try {
			boolean isUpdate = isUpdate(usuarioDTO);

			if (!isUpdate && isDuplicated(usuarioDTO)) {
				return buildErrorResponse(Constantes.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
			}

			UsuarioEntity entity = isUpdate ? updateEntityFromDto(usuarioDTO) : createEntityFromDto(usuarioDTO);

			setRolAndPersona(entity, usuarioDTO);

			UsuarioEntity saved = usuarioRepository.save(entity);
			UsuarioDTO savedDTO = usuarioMapper.entityToDto(saved);

			String message = isUpdate ? Constantes.UPDATED_SUCCESSFULLY : Constantes.SAVED_SUCCESSFULLY;
			int statusCode = isUpdate ? HttpStatus.OK.value() : HttpStatus.CREATED.value();

			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(message).code(statusCode)
					.response(savedDTO).build();

			return ResponseEntity.status(statusCode).body(responseDTO);

		} catch (Exception e) {
			log.error("Error guardando usuario", e);
			return buildErrorResponse(Constantes.SAVE_ERROR, HttpStatus.BAD_REQUEST);
		}
	}

	private boolean isUpdate(UsuarioDTO usuarioDTO) {
		return usuarioDTO.getId() != null && usuarioRepository.existsById(usuarioDTO.getId());
	}

	private boolean isDuplicated(UsuarioDTO usuarioDTO) {
		return usuarioDTO.getNombre() != null && usuarioRepository.existsByNombre(usuarioDTO.getNombre());
	}

	private UsuarioEntity updateEntityFromDto(UsuarioDTO usuarioDTO) {
		UsuarioEntity entity = usuarioRepository.findById(usuarioDTO.getId()).orElseThrow();
		usuarioMapper.updateEntityFromDto(usuarioDTO, entity);
		entity.setFechaModificacion(new Date());
		entity.setUsuarioModificacion(usuarioDTO.getUsuarioModificacion());
		if (usuarioDTO.getContrasena() != null && !usuarioDTO.getContrasena().isEmpty()) {
			entity.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
		}
		return entity;
	}

	private UsuarioEntity createEntityFromDto(UsuarioDTO usuarioDTO) {
		UsuarioEntity entity = usuarioMapper.dtoToEntity(usuarioDTO);
		entity.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
		entity.setFechaCreacion(new Date());
		entity.setUsuarioCreacion(usuarioDTO.getUsuarioCreacion());
		entity.setActivo(true);
		return entity;
	}

	private void setRolAndPersona(UsuarioEntity entity, UsuarioDTO usuarioDTO) {
		if (usuarioDTO.getRol() != null && usuarioDTO.getRol().getId() != null) {
			RolEntity rol = rolRepository.findById(usuarioDTO.getRol().getId())
					.orElseThrow(() -> new RuntimeException(Constantes.ROLE_NOT_FOUND));
			entity.setRol(rol);
		}
		if (usuarioDTO.getPersona() != null && usuarioDTO.getPersona().getId() != null) {
			PersonaEntity persona = personaRepository.findById(usuarioDTO.getPersona().getId())
					.orElseThrow(() -> new RuntimeException(Constantes.PERSON_NOT_FOUND));
			entity.setPersona(persona);
		}
	}

	private ResponseEntity<ResponseDTO> buildErrorResponse(String message, HttpStatus status) {
		ResponseDTO errorResponse = ResponseDTO.builder().success(false).message(message).code(status.value()).build();
		return ResponseEntity.status(status).body(errorResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDTO> findById(Integer id) {
		log.info("Buscar usuario por id: {}", id);
		try {
			Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
			if (usuario.isPresent()) {
				UsuarioDTO dto = usuarioMapper.entityToDto(usuario.get());
				ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(Constantes.CONSULTED_SUCCESSFULLY)
						.code(HttpStatus.OK.value()).response(dto).build();
				return ResponseEntity.ok(responseDTO);
			} else {
				ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.CONSULTING_ERROR)
						.code(HttpStatus.NOT_FOUND.value()).build();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}
		} catch (Exception e) {
			log.error("Error al buscar el usuario por id: {}", id, e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.ERROR_QUERY_RECORD_BY_ID)
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDTO> findAll() {
		log.info("Listar todos los usuario");
		try {
			var list = usuarioRepository.findAll();
			var dtoList = usuarioMapper.listEntityToDtoList(list);
			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(Constantes.CONSULTED_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).response(dtoList).build();
			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			log.error("Error al listar los usuarios", e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.CONSULTING_ERROR)
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).response(null).build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> deleteById(Integer id) {
		log.info("Inicio método para eliminar usuario por id: {}", id);
		try {
			if (!usuarioRepository.existsById(id)) {
				ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.RECORD_NOT_FOUND)
						.code(HttpStatus.NOT_FOUND.value()).build();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}
			usuarioRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(Constantes.DELETED_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).build();
			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			log.error("Error al eliminar el usuario con id: {}", id, e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.DELETE_ERROR)
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	public ResponseEntity<ResponseDTO> updatePassword(Integer idUsuario, String nuevaContrasena,
			String usuarioModificacion) {
		log.info("Inicio de actualización de contraseña para el usuario con ID: {}", idUsuario);
		try {
			String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_.])[A-Za-z\\d@$!%*?&\\-_.]{8,}$";
			if (nuevaContrasena == null || !nuevaContrasena.matches(passwordRegex)) {
				return ResponseEntity.badRequest().body(ResponseDTO.builder().success(false).message(
						"La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, un número y un carácter especial.")
						.code(HttpStatus.BAD_REQUEST.value()).build());
			}
			Optional<UsuarioEntity> optionalUsuario = usuarioRepository.findById(idUsuario);
			if (optionalUsuario.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().success(false)
						.message("Usuario no encontrado").code(HttpStatus.NOT_FOUND.value()).build());
			}

			UsuarioEntity usuario = optionalUsuario.get();
			usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
			usuario.setFechaModificacion(new Date());
			usuario.setUsuarioModificacion(usuarioModificacion);

			usuarioRepository.save(usuario);

			return ResponseEntity.ok(ResponseDTO.builder().success(true).message("Contraseña actualizada exitosamente")
					.code(HttpStatus.OK.value()).build());

		} catch (Exception e) {
			log.error("Error al actualizar la contraseña del usuario con ID: {}", idUsuario, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseDTO.builder().success(false).message("Error actualizando la contraseña")
							.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
		}
	}
}
