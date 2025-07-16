package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.PlazoPagoDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IPlazoPagoService {
	
ResponseEntity<ResponseDTO> save(PlazoPagoDTO plazoPagoDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);

}
