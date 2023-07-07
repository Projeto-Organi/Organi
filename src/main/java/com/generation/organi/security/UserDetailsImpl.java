package com.generation.organi.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.organi.model.Usuario;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private String userEmail;
	private String password;
	private List<GrantedAuthority> authorities;
	
	public UserDetailsImpl(Usuario user) {
		this.userEmail = user.getEmail();
		this.password = user.getSenha();
	}
	
	public UserDetailsImpl() { }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	//Não estamos utilizando, foi substituído pelo getUserEmail
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
