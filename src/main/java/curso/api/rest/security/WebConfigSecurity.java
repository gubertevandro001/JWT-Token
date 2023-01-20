package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.services.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
/* Mapeia URL, endereços, autoriza ou bloqueia acessos a URL */
public class WebConfigSecurity {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	private AuthenticationConfiguration auth;

	
	@Autowired
	public WebConfigSecurity(ImplementacaoUserDetailsService userDetailsService) {
	        this.implementacaoUserDetailsService = userDetailsService;
	 }

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		/* Ativando a proteção contra usuário que não estão validados por token */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				/* Ativando a permissão para acesso a página inicial do sistema */
				.disable().authorizeHttpRequests().requestMatchers("/").permitAll().requestMatchers("/index").permitAll()
				/* URL de logout - redireciona após o user deslogar do sistema */
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
				/* Mapeia URL de logout e invalida o usuario */
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager(auth)), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticatorFilter(), UsernamePasswordAuthenticationFilter.class);

		/* Filtra requisições de login para autenticação */
		/*
		 * Filtra demais requisições para verificar a presença do token jwt no header
		 * http
		 */

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();

	}

	@Bean
	public JWTApiAutenticacaoFilter jwtAuthenticatorFilter() {
		return new JWTApiAutenticacaoFilter();
	}
	

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
		builder.userDetailsService(implementacaoUserDetailsService).passwordEncoder(passwordEncoder());
	}
}
