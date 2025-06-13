package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.EstadoDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IEstadoService {
	
	ResponseEntity<ResponseDTO> save(EstadoDTO estadoDTO);
    ResponseEntity<ResponseDTO> findById(Integer id);
    ResponseEntity<ResponseDTO> findAll();
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
