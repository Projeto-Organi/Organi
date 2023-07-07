package com.generation.organi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.organi.model.Usuario;
import com.generation.organi.model.UsuarioLogin;
import com.generation.organi.repository.UsuarioRepository;
import com.generation.organi.security.JwtService;


@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired 
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public Optional<Usuario> cadastrarEmail (Usuario email) {
		
		if(usuarioRepository.findByEmail(email.getEmail()).isPresent())
			return Optional.empty();
		
		email.setSenha(criptografarSenha(email.getSenha()));
		
		return Optional.of(usuarioRepository.save(email));
	}
	
	public Optional<Usuario> atualizarEmail(Usuario email) {
		
		if(usuarioRepository.findById(email.getId()).isPresent()) {
			
			Optional<Usuario> buscaUsuario = usuarioRepository.findByEmail(email.getEmail());
			
			if ( (buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != email.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe", null);
			
			email.setSenha(criptografarSenha(email.getSenha()));
			
			return Optional.ofNullable(usuarioRepository.save(email));
		}
		return Optional.empty();
	}
	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
		
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getEmail(), usuarioLogin.get().getSenha());
				
		Authentication authentication = authenticationManager.authenticate(credenciais);
		
		if(authentication.isAuthenticated()) {
			
			Optional<Usuario> email = usuarioRepository.findByEmail(usuarioLogin.get().getEmail());
			
			if(email.isPresent()) {
				
				usuarioLogin.get().setId(email.get().getId());
			usuarioLogin.get().setNome(email.get().getNome());
			usuarioLogin.get().setFoto(email.get().getFoto());
			usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getEmail()));
            usuarioLogin.get().setSenha("");
            
            return usuarioLogin;
			}
		}
		
		return Optional.empty();
	}
	
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(senha);
	}
	
	private String gerarToken(String email) {
		return "Bearer " + jwtService.generateToken(email);
	}
	
	

}
