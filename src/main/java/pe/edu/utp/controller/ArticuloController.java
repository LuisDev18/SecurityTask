package pe.edu.utp.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.service.ArticuloService;

@RestController
@RequestMapping("/articulos")
@RequiredArgsConstructor
@Slf4j
public class ArticuloController {

  private final ArticuloService articuloService;

  @GetMapping
  public ResponseEntity<List<ArticuloDto>> getAll(

      @RequestParam(value = "marca", required = false, defaultValue = "") String marca,
      @RequestParam(value = "categoria", required = false, defaultValue = "") String categoria,
      @RequestParam(value = "precioMin", required = false, defaultValue = "0") Double precioMin,
      @RequestParam(value = "precioMax", required = false, defaultValue = "0") Double precioMax,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    List<ArticuloDto> articulos;
    if (marca == null || categoria == null || precioMin == null || precioMax == null) {
      articulos = articuloService.findAll(pageable);
    } else {
      articulos = articuloService.findByCategoriaAndMarcaAndPrecio(categoria, marca, precioMin, precioMax, pageable);
    }
    return ResponseEntity.ok(articulos);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> findById(@PathVariable("id") int id) {
    log.info("Obteniendo articulo con ID: {}", id);
    var response = articuloService.findById(id);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<Articulo> create(@Valid @RequestBody ArticuloDto articuloDto) {
    Articulo registro = articuloService.save(articuloDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(registro);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Articulo> update(
      @PathVariable("id") int id, @Valid @RequestBody ArticuloDto articuloDto) {
    Articulo articuloUpdate = articuloService.update(articuloDto, id);
    return ResponseEntity.ok(articuloUpdate);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> delete(@PathVariable("id") int id)
      throws NoDataFoundException {
    articuloService.delete(id);
    return ResponseEntity.ok(null);
  }
}
