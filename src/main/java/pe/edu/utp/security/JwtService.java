package pe.edu.utp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  private final PemReader pemReader;
  private final long accessTokenExpirationTime = 1000L * 60 * 24;
  private final long refreshTokenExpirationTime = 1000L * 60 * 60 * 24;

  public String extractUsername(String token) {
    return extractClaim(token, claims -> claims.get("username", String.class));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    Claims claims = Jwts.parserBuilder()
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

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Claims claims = Jwts.claims();
    claims.put("username", userDetails.getUsername());
    claims.put("roles", userDetails.getAuthorities().toString());
    claims.putAll(extraClaims); // Agregar campos adicionales

    return Jwts.builder()
        .setHeaderParam("typ", "JWT")
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
        .signWith(pemReader.getPrivateKey(), SignatureAlgorithm.RS512)
        .compact();
  }

  public String generateRefreshToken(UserDetails userDetails) {
    Claims claims = Jwts.claims();
    claims.put("username", userDetails.getUsername());

    return Jwts.builder()
        .setHeaderParam("typ", "JWT")
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
        .signWith(pemReader.getPrivateKey(), SignatureAlgorithm.RS512)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
