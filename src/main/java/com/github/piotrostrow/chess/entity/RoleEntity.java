package com.github.piotrostrow.chess.entity;

import com.github.piotrostrow.chess.security.Role;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class RoleEntity {

	@Id
	@GeneratedValue
	private Long id;

	private Role role;

	@ManyToMany(mappedBy = "roles")
	private Set<UserEntity> users;

	public RoleEntity() {
	}

	public RoleEntity(Role role) {
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Set<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Set<UserEntity> users) {
		this.users = users;
	}
}
