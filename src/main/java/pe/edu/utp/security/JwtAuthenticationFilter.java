package pe.edu.utp.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CustomerUserDetailsService customerUserDetailsService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      String jwt = jwtService.extractToken(request);
      if (jwt == null) {
        log.info("JWT token is missing for request: {}", request.getRequestURI());
        filterChain.doFilter(request, response); // Pasa la solicitud si no hay JWT
        return; // Importante: retornar después de pasar
      }

      // Opcional: Si el usuario ya está autenticado, no procesar el JWT de nuevo
      if (
        SecurityContextHolder.getContext().getAuthentication() != null &&
        SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
      ) {
        log.debug(
          "User already authenticated in SecurityContext. Skipping JWT processing for {}.",
          request.getRequestURI()
        );
        filterChain.doFilter(request, response); // Pasa la solicitud si ya está autenticado
        return;
      }

      Claims claims = jwtService.resolveClaims(request); // Esto puede lanzar ExpiredJwtException, etc.

      if (claims != null && jwtService.validateClaims(claims)) {
        String username = jwtService.extractUsername(jwt);
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug(
          "User '{}' authenticated via JWT with roles: {}. Proceeding with filter chain.",
          username,
          userDetails.getAuthorities()
        );
      } else {
        // Si el token no es válido (ej. claims null o no validan), pero no lanzó una excepción,
        // loguear y opcionalmente denegar o simplemente pasar sin autenticar.
        log.warn(
          "Invalid JWT token or claims for {}. Not authenticating.",
          request.getRequestURI()
        );
        // Podrías poner un error 401/403 aquí si un token _debe_ ser válido para cualquier ruta no permitAll
        // response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // objectMapper.writeValue(response.getWriter(), Map.of("error", "Invalid JWT Token"));
        // return;
      }

      // ¡ESTA ES LA LÍNEA CLAVE PARA LAS SOLICITUDES CON TOKEN VÁLIDO!
      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException ex) {
      log.error("JWT token expired for request {}: {}", request.getRequestURI(), ex.getMessage());
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("error", "JWT Token expired");
      response.setStatus(HttpStatus.UNAUTHORIZED.value()); // Un 401 es más apropiado para token expirado
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(response.getWriter(), errorDetails);
    } catch (Exception e) { // Captura cualquier otra excepción relacionada con el JWT (firma inválida, etc.)
      log.error(
        "Error processing JWT token for request {}: {}",
        request.getRequestURI(),
        e.getMessage()
      );
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("error", "Invalid JWT token");
      response.setStatus(HttpStatus.FORBIDDEN.value()); // O 401 Unauthorized
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(response.getWriter(), errorDetails);
    }
  }
}
