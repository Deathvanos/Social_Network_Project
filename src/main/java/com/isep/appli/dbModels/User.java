package com.isep.appli.dbModels;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	// id email enabled first_name is_admin last_name password username

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(columnDefinition="tinyint(1) default 0")
	private Boolean enabled;

	@Column(columnDefinition="tinyint(1) default 0")
	private Boolean isAdmin;
	
	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", username=" + username + ", email=" + email
				+ ", password=" + password + ", isAdmin=" + isAdmin + ", enabled=" + enabled
				+ "]";
	}
	
	
}
