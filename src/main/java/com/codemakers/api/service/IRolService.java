package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.RolDTO;

/**
 * @author nicope
 * @version 1.0
 * 
 *          Esta interfaz es la capa intermedia entre la capa de presentación y
 *          la capa de acceso a datos. Esta oculta los detalles de
 *          implementación de la capa de acceso a datos.
 * 
 */

public interface IRolService {

	ResponseEntity<ResponseDTO> save(RolDTO rolDTO);
	
	ResponseEntity<ResponseDTO> findRolById(Integer id);

    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> delete(Integer id);

}
