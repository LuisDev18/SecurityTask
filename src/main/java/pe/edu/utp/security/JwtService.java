package pe.edu.utp.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  private final PemReader pemReader;
  private final long accessTokenExpirationTime = 1000L * 60 * 24 * 60;
  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  public String extractUsername(String tokenOrHeader) {
    return extractClaim(tokenOrHeader, claims -> claims.get("username", String.class));
  }

  public <T> T extractClaim(String tokenOrHeader, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(tokenOrHeader);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String tokenOrHeader) {
    String token = sanitizeToken(tokenOrHeader);
    Claims claims = Jwts
      .parserBuilder()
      .setSigningKey(pemReader.getPublicKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
    log.info("Claims: {}", claims);
    return claims;
  }

  // Quita "Bearer " (con espacio) usando substring y maneja espacios extra
  private String sanitizeToken(String tokenOrHeader) {
    if (tokenOrHeader == null) {
      throw new IllegalArgumentException("JWT no puede ser null");
    }
    String t = tokenOrHeader.trim();
    // Case-insensitive por si viene "bearer ..."
    if (
      t.length() >= TOKEN_PREFIX.length() &&
      t.regionMatches(true, 0, TOKEN_PREFIX, 0, TOKEN_PREFIX.length())
    ) {
      t = t.substring(TOKEN_PREFIX.length()).trim();
    }
    if (t.isEmpty()) {
      throw new IllegalArgumentException("JWT está vacío después de quitar el prefijo Bearer");
    }
    return t;
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(TOKEN_HEADER);
    if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length()).trim();
    }
    return null;
  }

  public Claims resolveClaims(HttpServletRequest request) {
    try {
      String jwt = extractToken(request);
      if (jwt != null) {
        return extractAllClaims(jwt);
      }
      return null;
    } catch (ExpiredJwtException ex) {
      request.setAttribute("expired", ex.getMessage());
      throw ex;
    } catch (Exception ex) {
      request.setAttribute("invalid", ex.getMessage());
      throw ex;
    }
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Claims claims = Jwts.claims();
    claims.put("username", userDetails.getUsername());
    claims.put("roles", userDetails.getAuthorities().toString());
    claims.putAll(extraClaims);

    return Jwts
      .builder()
      .setHeaderParam("typ", "JWT")
      .setClaims(claims)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
      .signWith(pemReader.getPrivateKey(), SignatureAlgorithm.RS512)
      .compact();
  }

  public boolean isTokenValid(String tokenOrHeader, UserDetails userDetails) {
    final String username = extractUsername(tokenOrHeader);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(tokenOrHeader);
  }

  public boolean validateClaims(Claims claims) {
    return claims.getExpiration().after(new Date());
  }

  private boolean isTokenExpired(String tokenOrHeader) {
    return extractExpiration(tokenOrHeader).before(new Date());
  }

  private Date extractExpiration(String tokenOrHeader) {
    return extractClaim(tokenOrHeader, Claims::getExpiration);
  }
}
