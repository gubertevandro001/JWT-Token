package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.models.Usuario;
import curso.api.rest.repositories.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/* Tempo de validade do token, 2 dias nesse caso */
	private static final long EXPIRATION_TIME = 172800000;

	/* Senha única para compor a autenticação e ajudar na segurança */
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/* Prefixo padrão de Token */
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/* Gerando Token de autenticação e adicionando ao cabeçalho e resposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		/* Montagem do Token */
		String JWT = Jwts.builder().setSubject(username) /* Adiciona o usuário */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de expiração */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /* Compactação e geração de senha */

		/* Junta token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; /* Bearer husadfhqerwhfuiqewfhewhfhsahfqashiofd */

		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Authorization: Bearer husadfhqerwhfuiqewfhewhfhsahfqashiofd */

		/* Escreve token como resposta no corpo do http */
		response.getWriter().write("{\"Authorization:\": \""+token+"\"}");
	}

	/* Retorna o usuário validado com token ou caso não seja válido retorna null */
	public Authentication getAuthentication(HttpServletRequest request) {

		/* Pega o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {

			/* Faz a validação do token do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET) /* Bearer husadfhqerwhfuiqewfhewhfhsahfqashiofd */
					.parseClaimsJws(token.replace(TOKEN_PREFIX, "")) /* husadfhqerwhfuiqewfhewhfhsahfqashiofd */
					.getBody().getSubject(); /* João Silva */

			if (user != null) {

				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				if (usuario != null) {

					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
							usuario.getAuthorities());

				}

			}
		}
		return null; /* Não autorizado */
	}
}
