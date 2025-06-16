package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.TipoNovedadDTO;

public interface ITipoNovedadService {
	
    ResponseEntity<ResponseDTO> save(TipoNovedadDTO tipoNovedadDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);

}
