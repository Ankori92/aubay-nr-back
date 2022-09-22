package com.aubay.formations.nr.entities;

import java.util.Collection;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User entity
 *
 * @author jbureau@aubay.com
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password" /* security */, "authorities" /* roles */ })
public class User implements UserDetails {

	private static final long serialVersionUID = -3268646257064545505L;

	@Id
	private String username;

	private String password;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "username"))
	@Column(name = "authority")
	private List<GrantedAuthority> authorities;

	public User() {
	}

	public User(final String username, final String password, final Employee employee,
			final List<GrantedAuthority> authorities) {
		this.username = username;
		this.password = password;
		this.employee = employee;
		this.authorities = authorities;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public Employee getEmployee() {
		return employee;
	}

	public List<String> getRoles() {
		return getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	// @formatter:off
	// Not used
	@Override public boolean isEnabled() {return true;}
	@Override public boolean isAccountNonExpired() {return true;}
	@Override public boolean isAccountNonLocked() {return true;}
	@Override public boolean isCredentialsNonExpired() {return true;}
}
