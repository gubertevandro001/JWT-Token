package curso.api.rest.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.models.Usuario;
import curso.api.rest.repositories.UsuarioRepository;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	/*Apenas um exemplo se fosse um relatório em PDF*/
	@GetMapping(value = "/{id}/relatoriopdf", produces = "application/pdf")
	public ResponseEntity<Usuario> relatorio(@PathVariable (value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		/*o retorno seria um relatório*/
		return ResponseEntity.ok().body(usuario.get());
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable (value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return ResponseEntity.ok().body(usuario.get());
	}
	
	/*@CrossOrigin(origins = "https://www.jdevtreinamento.com.br")*/
	@GetMapping(value = "/", produces = "application/json" )
	public ResponseEntity<List<Usuario>> usuario() {
		List<Usuario> list = usuarioRepository.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@PostMapping(value = "/")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		String cryptSenha = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(cryptSenha);
		Usuario user = usuarioRepository.save(usuario);
		return ResponseEntity.ok().body(user);
	}
	
	@PutMapping(value = "/")
	public ResponseEntity<Usuario> alterar(@RequestBody Usuario usuario) {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if(!userTemporario.getSenha().equals(usuario.getSenha())) {
			String cryptSenha = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(cryptSenha);
		}
		Usuario user = usuarioRepository.save(usuario);
		return ResponseEntity.ok().body(user);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Void> excluir(@PathVariable (value = "id") Long id) {
		usuarioRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
