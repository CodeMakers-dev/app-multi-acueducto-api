package com.codemakers.api.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.postgresql.util.PGobject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemakers.api.service.IEmpresaService;
import com.codemakers.commons.dtos.EmpresaDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.EmpresaEntity;
import com.codemakers.commons.maps.EmpresaMapper;
import com.codemakers.commons.repositories.EmpresaRepository;
import com.codemakers.commons.utils.Constantes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaServiceImpl implements IEmpresaService{
	
	private final EmpresaRepository empresaRepository;
	private final EmpresaMapper empresaMapper;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional
    public ResponseEntity<ResponseDTO> save(EmpresaDTO empresaDTO) {
        log.info("Creando Empresa");
        try {
            EmpresaEntity entity = empresaMapper.dtoToEntity(empresaDTO);
            entity.setFechaCreacion(new Date());
            entity.setUsuarioCreacion(empresaDTO.getUsuarioCreacion());
            entity.setActivo(true);

            EmpresaEntity saved = empresaRepository.save(entity);
            EmpresaDTO savedDTO = empresaMapper.entityToDto(saved);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.SAVED_SUCCESSFULLY)
                    .code(HttpStatus.CREATED.value())
                    .response(savedDTO)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("Error creando la Empresa", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.SAVE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
	
	@Transactional
    public Map<String, Object> registrarEmpresa(Map<String, Object> jsonParams) {
        try {
            String plainPassword = (String) jsonParams.get("password");

            if (plainPassword != null) {
                String encodedPassword = passwordEncoder.encode(plainPassword);
                jsonParams.put("password", encodedPassword);
            }

            String jsonString = objectMapper.writeValueAsString(jsonParams);

            String sql = "SELECT * FROM public.crear_o_actualizar_empresa(CAST(:jsonData AS jsonb))"; 
            
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("jsonData", jsonString);

            Map<String, Object> rawResult = namedParameterJdbcTemplate.queryForMap(sql, parameters);

            Object wrappedValue = rawResult.get("crear_o_actualizar_empresa"); 
            if (wrappedValue instanceof PGobject pgObject && "jsonb".equals(pgObject.getType())) {
                String jsonValue = pgObject.getValue();
                return objectMapper.readValue(jsonValue, new TypeReference<Map<String, Object>>() {});
            }

            return Map.of(Constantes.ERROR_KEY, "El resultado no pudo ser procesado correctamente.");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.singletonMap(Constantes.ERROR_KEY, "Error de procesamiento JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonMap(Constantes.ERROR_KEY, "Error inesperado: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> update(EmpresaDTO empresaDTO) {
        log.info("Actualizando Empresa");
        try {
            if (empresaDTO.getId() == null || !empresaRepository.existsById(empresaDTO.getId())) {
                throw new IllegalArgumentException(Constantes.EMP_NOT_FOUND);
            }

            EmpresaEntity entity = empresaRepository.findById(empresaDTO.getId()).orElseThrow();
            empresaMapper.updateEntityFromDto(empresaDTO, entity); 
            entity.setFechaModificacion(new Date());
            entity.setUsuarioModificacion(empresaDTO.getUsuarioModificacion());

            EmpresaEntity updated = empresaRepository.save(entity);
            EmpresaDTO updatedDTO = empresaMapper.entityToDto(updated);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.UPDATED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(updatedDTO)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            log.error("Error actualizando la Empresa", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.UPDATE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar Empresa por id: {}", id);
	    try {
	        Optional<EmpresaEntity> empresa = empresaRepository.findById(id);
	        if (empresa.isPresent()) {
	        	EmpresaDTO dto = empresaMapper.entityToDto(empresa.get());
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
	        log.error("Error al buscar  Empresa por id: {}", id, e);
	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.ERROR_QUERY_RECORD_BY_ID)
	                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
	                .build();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
	    }
	}

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO> findAll() {
        log.info("Listar todos las empresas");
        try {
            var list = empresaRepository.findAll();
            var dtoList = empresaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar las empresas", e);
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
    @Transactional
    public ResponseEntity<ResponseDTO> deleteById(Integer id) {
        log.info("Inicio método para eliminar empresa por id: {}", id);
        try {
            if (!empresaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            empresaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar empresa con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
