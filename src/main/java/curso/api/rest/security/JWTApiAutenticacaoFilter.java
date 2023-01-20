package curso.api.rest.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*Filtro onde todas as requisições serão capturadas para autenticar*/
@Component
public class JWTApiAutenticacaoFilter extends OncePerRequestFilter {
	
	
    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		/*Estabelece a autenticação para a requisição*/
		
		Authentication authentication = new JWTTokenAutenticacaoService().getAuthentication((HttpServletRequest) request);
		
		/*Coloca o processo de autenticação no spring security*/
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		/*Continua o processo*/
		chain.doFilter(request, response);
		
	}
}
