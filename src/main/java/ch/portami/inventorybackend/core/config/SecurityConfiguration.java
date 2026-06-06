package ch.portami.inventorybackend.core.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Web security and CORS configuration.
 *
 * <p><strong>Deliberately open:</strong> every request is permitted with no authentication, CSRF
 * protection is disabled, and form login is turned off. This is intentional for the deployment model
 * of this tool — a single small felt business running it on a trusted local network with a couple of
 * users and no external exposure. If the application is ever reachable from an untrusted network this
 * configuration must be revisited (add authentication, re-enable CSRF, and tighten CORS).
 */
@Configuration
public class SecurityConfiguration {

    /**
     * Builds the application's single security filter chain: CSRF disabled, CORS driven by
     * {@link #corsConfigurationSource()}, all requests permitted, and form login disabled. See the
     * class-level note on why this is intentionally permissive.
     *
     * @param http the security builder provided by Spring Security
     * @return the configured filter chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth.anyRequest()
                                               .permitAll())
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * CORS policy for the front-end: allows any {@code http://localhost:*} origin (any port) with
     * credentials, the common HTTP verbs, and any header. Suitable for a locally hosted client; widen
     * the allowed origins if the UI is served from elsewhere.
     *
     * @return the CORS configuration source applied to all paths
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}