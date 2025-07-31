package pe.edu.utp.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Value("${spring.security.disabled:false}")
  private boolean disabledSecurity;

  /*
   * Sprign security detecta un bean UserDetailsService y un PasswordEncoder,
   * y automaticamente va a configurar una instancia de DaoAuthenticationProvider
   * interno
   * que utiliza el UserDetailsService y el PasswordEncoder.
   * Luego crea una instancia de un ProviderManager, q registra al
   * DaoAuthenticationProvider
   * y lo devuelve como AuthenticationManager.
   */
  @Bean
  public AuthenticationManager authenticationManager(
    AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configure(http));
    http.csrf(AbstractHttpConfigurer::disable);
    http.authorizeHttpRequests(authorize ->
      authorize
        .requestMatchers(
          "/actuator/health",
          "/usuarios-register",
          "/usuarios/login",
          "/signup/**",
          "/v3/**",
          "/doc/swagger-ui/**"
        )
        .permitAll()
        .anyRequest()
        .authenticated()
    );

    http.sessionManagement(sessionManagement ->
      sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
  }
}
