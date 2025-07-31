package pe.edu.utp.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.converter.UsuarioConverter;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.dto.UsuarioRequestDto;
import pe.edu.utp.dto.UsuarioResponseDto;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.service.UsuarioService;
import pe.edu.utp.util.ApiResponse;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final UsuarioConverter converter;

  @GetMapping("/usuarios")
  public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> findAll(
    @RequestParam(value = "email", required = false) String email,
    @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
    @RequestParam(value = "limit", required = false, defaultValue = "10") int pageSize
  ) throws Exception {
    Pageable pagina = PageRequest.of(pageNumber, pageSize);
    List<Usuario> registros = usuarioService.findAll(pagina);
    List<UsuarioResponseDto> registrosDTO = converter.fromEntity(registros);
    return ApiResponse.ok("Users retrieved successfully", registrosDTO).toResponseEntity();
  }

  @PostMapping("/usuarios-register")
  public ResponseEntity<ApiResponse<UsuarioResponseDto>> create(
    @RequestBody UsuarioRequestDto usuario
  ) {
    Usuario registro = usuarioService.save(usuario);
    return ApiResponse
      .created("User created successfully", converter.fromEntity(registro))
      .toResponseEntity();
  }

  @PutMapping(value = "/usuarios/{id}")
  public ResponseEntity<ApiResponse<UsuarioResponseDto>> update(
    @PathVariable("id") int id,
    @RequestBody UsuarioRequestDto usuario
  ) {
    Usuario registro = usuarioService.update(usuario, id);
    if (registro == null) {
      return ResponseEntity.notFound().build();
    }
    return ApiResponse
      .ok("User updated successfully", converter.fromEntity(registro))
      .toResponseEntity();
  }

  @DeleteMapping(value = "/usuarios/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") int id) {
    usuarioService.delete(id);
    return ApiResponse.noContent().toResponseEntity();
  }

  @PostMapping(value = "/usuarios/login")
  public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {
    LoginResponseDto response = usuarioService.login(request);
    return ApiResponse.ok("Login successfully", response).toResponseEntity();
  }
}
