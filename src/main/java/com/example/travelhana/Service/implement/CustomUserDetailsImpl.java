package com.example.travelhana.Service.implement;

import com.example.travelhana.Service.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetailsImpl implements CustomUserDetails {

	private final String username;
	private final String password;
	private final String pattern;
	private final String salt;
	private final Collection<? extends GrantedAuthority> authorities;
	private final boolean accountNonExpired;
	private final boolean accountNonLocked;
	private final boolean credentialsNonExpired;
	private final boolean enabled;

	public CustomUserDetailsImpl(String username, String password, String pattern, String salt,
	                             Collection<? extends GrantedAuthority> authorities,
	                             boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired,
	                             boolean enabled) {
		this.username = username;
		this.password = password;
		this.pattern = pattern;
		this.salt = salt;
		this.authorities = authorities;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.enabled = enabled;
	}

	@Override
	public String getSalt() {
		return salt;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}

