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

  public String extractUsername(String token) {
    return extractClaim(token, claims -> claims.get("username", String.class));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    Claims claims = Jwts
      .parserBuilder()
      .setSigningKey(pemReader.getPublicKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
    log.info("Claims: {}", claims);
    return claims;
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(TOKEN_HEADER);
    if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
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
    claims.putAll(extraClaims); // Agregar campos adicionales

    return Jwts
      .builder()
      .setHeaderParam("typ", "JWT")
      .setClaims(claims)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
      .signWith(pemReader.getPrivateKey(), SignatureAlgorithm.RS512)
      .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public boolean validateClaims(Claims claims) {
    return claims.getExpiration().after(new Date());
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
