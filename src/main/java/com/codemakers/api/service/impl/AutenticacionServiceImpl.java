package com.codemakers.api.service.impl;

import java.util.Date;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codemakers.api.config.InvalidCredentialsException;
import com.codemakers.api.config.JwtUtil;
import com.codemakers.api.config.UserNotFoundException;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.UsuarioDTO;
import com.codemakers.commons.dtos.VigenciaUsuarioDTO;
import com.codemakers.commons.entities.UsuarioEntity;
import com.codemakers.commons.entities.VigenciaUsuarioEntity;
import com.codemakers.commons.repositories.UsuarioRepository;
import com.codemakers.commons.repositories.VigenciaUsuarioRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nicope
 * @version 1.0
 * 
 *          Clase que implementa lógica de autenticado.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AutenticacionServiceImpl {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VigenciaUsuarioRepository vigenciaUsuarioRepository;

    public ResponseEntity<ResponseDTO> autentication(String username, String password) {
        try {
            UsuarioEntity usuario = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new UserNotFoundException(Constantes.RECORD_NOT_FOUND));
            if (!passwordEncoder.matches(password, usuario.getContrasena())) {
                throw new InvalidCredentialsException(Constantes.INVALID_CREDENTIALS);
            }
            String token = jwtUtil.generateToken(usuario.getNombre());

            Date fechaActual = new Date();
            Date fechaVigencia = new Date(fechaActual.getTime() + 3L * 24 * 60 * 60 * 1000); 

            VigenciaUsuarioEntity vigenciaEntity = new VigenciaUsuarioEntity();
            vigenciaEntity.setToken(Constantes.BEARER + token);
            vigenciaEntity.setFechaVigencia(fechaVigencia);
            vigenciaEntity.setUsuario(usuario);
            vigenciaEntity.setUsuarioCreacion(usuario.getNombre());
            vigenciaEntity.setFechaCreacion(fechaActual);
            vigenciaEntity.setActivo(true);
            vigenciaUsuarioRepository.save(vigenciaEntity);
            VigenciaUsuarioDTO vigenciaDTO = VigenciaUsuarioDTO.builder()
                    .id(vigenciaEntity.getId())
                    .token(vigenciaEntity.getToken())
                    .fechaVigencia(vigenciaEntity.getFechaVigencia())
                    .usuario(UsuarioDTO.builder()
                            .id(usuario.getId())
                            .nombre(usuario.getNombre())
                            .build())
                    .usuarioCreacion(vigenciaEntity.getUsuarioCreacion())
                    .fechaCreacion(vigenciaEntity.getFechaCreacion())
                    .activo(vigenciaEntity.getActivo())
                    .build();
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.AUTHENTICATION_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(vigenciaDTO)
                    .build();

            return ResponseEntity.ok(responseDTO);

        } catch (RuntimeException e) {
            ResponseDTO errorDTO = ResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);

        } catch (Exception e) {
            ResponseDTO errorDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.AUTHENTICATION_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }
}
