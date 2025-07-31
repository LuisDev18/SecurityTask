package pe.edu.utp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.dto.ArticuloResponseDto;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.service.ArticuloService;
import pe.edu.utp.util.ApiResponse;

@RestController
@RequestMapping("/articulos")
@RequiredArgsConstructor
@Slf4j
public class ArticuloController {

  private final ArticuloService articuloService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ArticuloResponseDto>>> getAll(

      @RequestParam(value = "marca", required = false) String marca,
      @RequestParam(value = "categoria", required = false) String categoria,
      @RequestParam(value = "precioMin", required = false) Double precioMin,
      @RequestParam(value = "precioMax", required = false) Double precioMax,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    List<ArticuloResponseDto> articulos;
    if (marca !=null || categoria != null || precioMin != null || precioMax != null) {
      articulos = articuloService.findByCategoriaAndMarcaAndPrecio(categoria, marca, precioMin, precioMax, pageable);
    } else {
       articulos = articuloService.findAll(pageable);
    }
    return ApiResponse.ok(articulos).toResponseEntity();
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ApiResponse<ArticuloResponseDto>> findById(@PathVariable("id") Integer id) {
    log.info("Obteniendo articulo con ID: {}", id);
    var response = articuloService.findById(id);
    return ApiResponse.ok(response).toResponseEntity();
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Articulo>> create(@Valid @RequestBody ArticuloDto articuloDto) {
    Articulo registro = articuloService.save(articuloDto);
    return ApiResponse.created("Articulo creado exitosamente", registro).toResponseEntity();
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Articulo> update(
      @PathVariable("id") Integer id, @Valid @RequestBody ArticuloDto articuloDto) {
    Articulo articuloUpdate = articuloService.update(articuloDto, id);
    return ResponseEntity.ok(articuloUpdate);
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<ApiResponse<Articulo>> partialUpdate(@PathVariable("id") Integer id, @RequestBody Map<String, Object> fields) {
    var articuloUpdate = articuloService.partialUpdate(id, fields);
    return ApiResponse.ok(articuloUpdate).toResponseEntity();
  }


  @DeleteMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> delete(@PathVariable("id") Integer id)
      throws NoDataFoundException {
    articuloService.delete(id);
    return ResponseEntity.ok(null);
  }
}
