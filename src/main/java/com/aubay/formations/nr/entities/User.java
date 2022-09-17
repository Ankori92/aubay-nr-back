package com.aubay.formations.nr.entities;

import java.util.Collection;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aubay.formations.nr.enums.LangEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User entity
 *
 * @author jbureau@aubay.com
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password" /* security */, "authorities" /* roles */ })
public class User implements UserDetails {

	private static final long serialVersionUID = -3268646257064545505L;

	@Id
	@EqualsAndHashCode.Include
	private String username;

	private String password;

	@Enumerated(EnumType.STRING)
	private LangEnum preferredLang;

	private boolean enabled;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "username"))
	@Column(name = "authority")
	private List<GrantedAuthority> authorities;

	@Override
	public String getUsername() {
		return username;
	}

	public List<String> getRoles() {
		return getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	// @formatter:off
	// Not used
	@Override public boolean isAccountNonExpired() {return true;}
	@Override public boolean isAccountNonLocked() {return true;}
	@Override public boolean isCredentialsNonExpired() {return true;}
}
