package pe.edu.utp.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;

  @Value("${spring.security.disabled:false}")
  private boolean disabledSecurity;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults());

    http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    if (disabledSecurity) {
      log.warn("SYSTEM: HTTP Security rules are disabled");
      http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
      return http.build();
    }

    http.authorizeHttpRequests(
            authorize ->
                    authorize
                            .requestMatchers(
                                    // **Aquí está el cambio para resolver el error de compilación**
                                    AntPathRequestMatcher.antMatcher("/ws/**"),
                                    AntPathRequestMatcher.antMatcher("/actuator/health"),
                                    AntPathRequestMatcher.antMatcher("/usuarios-register"),
                                    AntPathRequestMatcher.antMatcher("/usuarios/login"),
                                    AntPathRequestMatcher.antMatcher("/error"),
                                    // Las siguientes líneas también necesitan el AntPathRequestMatcher
                                    AntPathRequestMatcher.antMatcher("/signup/**"),
                                    AntPathRequestMatcher.antMatcher("/institutions"),
                                    AntPathRequestMatcher.antMatcher("/v3/**"),
                                    AntPathRequestMatcher.antMatcher("/doc/swagger-ui/**")
                            )
                            .permitAll()
                            .anyRequest()
                            .authenticated());

    http.authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true); // importante para JWT en WebSocket

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
