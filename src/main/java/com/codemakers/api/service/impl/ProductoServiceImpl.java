package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemakers.api.service.IProductoService;
import com.codemakers.commons.dtos.ProductoDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.ProductoEntity;
import com.codemakers.commons.maps.ProductoMapper;
import com.codemakers.commons.repositories.ProductoRepository;
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
public class ProductoServiceImpl implements IProductoService {

	private final ProductoRepository productoRepository;
	private final ProductoMapper productoMapper;
	
	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> save(ProductoDTO productoDTO) {
	    log.info("Guardar/Actualizar Producto ");
	    try {
	        boolean isUpdate = productoDTO.getId() != null && productoRepository.existsById(productoDTO.getId());
	        ProductoEntity entity;

	        if (isUpdate) {
	            entity = productoRepository.findById(productoDTO.getId()).orElseThrow();
	            productoMapper.updateEntityFromDto(productoDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(productoDTO.getUsuarioModificacion());
	        } else {
	            entity = productoMapper.dtoToEntity(productoDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(productoDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }

	        ProductoEntity saved = productoRepository.save(entity);
	        ProductoDTO savedDTO = productoMapper.entityToDto(saved);

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
	        log.error("Error guardando la cuenta", e);
	        ResponseDTO errorResponse = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.SAVE_ERROR)
	                .code(HttpStatus.BAD_REQUEST.value())
	                .build();

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	    }
	}
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDTO> findByEnterpriseId(Integer idEmpresa) {
	    log.info("Buscar producto por id de empresa: {}", idEmpresa);
	    try {
	        List<ProductoEntity> producto= productoRepository.findByEmpresa_Id(idEmpresa);

	        if (producto.isEmpty()) {
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(false)
	                    .message("No se encontraron productos para la empresa con id " + idEmpresa)
	                    .code(HttpStatus.NOT_FOUND.value())
	                    .build();
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
	        }

	        List<ProductoDTO> dtoList = productoMapper.listEntityToDtoList(producto);

	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(true)
	                .message(Constantes.CONSULTED_SUCCESSFULLY)
	                .code(HttpStatus.OK.value())
	                .response(dtoList)
	                .build();

	        return ResponseEntity.ok(responseDTO);
	    } catch (Exception e) {
	        log.error("Error al buscar productos por id de empresa: {}", idEmpresa, e);
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
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar producto por id: {}", id);
	    try {
	        Optional<ProductoEntity> producto = productoRepository.findById(id);
	        if (producto.isPresent()) {
	        	ProductoDTO dto = productoMapper.entityToDto(producto.get());
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
	        log.error("Error al buscar producto por id: {}", id, e);
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
        log.info("Listar todos los productos");
        try {
            var list = productoRepository.findAll();
            var dtoList = productoMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los productos", e);
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
        log.info("Inicio método para eliminar producto por id: {}", id);
        try {
            if (!productoRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            productoRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar cuenta con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
