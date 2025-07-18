package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.DeudaClienteDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IDeudaClienteService {
    
	ResponseEntity<ResponseDTO> save(DeudaClienteDTO deudaClienteDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
    
    ResponseEntity<ResponseDTO> updateDeuda(DeudaClienteDTO deudaClienteDTO);

}
