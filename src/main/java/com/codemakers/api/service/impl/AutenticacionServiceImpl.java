package com.codemakers.api.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codemakers.api.config.InvalidCredentialsException;
import com.codemakers.api.config.JwtUtil;
import com.codemakers.api.config.UserNotFoundException;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.UsuarioEntity;
import com.codemakers.commons.repositories.UsuarioRepository;
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

    public ResponseEntity<ResponseDTO> login(String username, String password) {
        try {
        	UsuarioEntity usuario = usuarioRepository.findByNombre(username)
        		    .orElseThrow(() -> new UserNotFoundException(Constantes.RECORD_NOT_FOUND));

        		if (!passwordEncoder.matches(password, usuario.getContrasena())) {
        		    throw new InvalidCredentialsException(Constantes.INVALID_CREDENTIALS);
        		}

            String token = jwtUtil.generateToken(usuario.getNombre());

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.AUTHENTICATION_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response("Bearer " + token)
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
