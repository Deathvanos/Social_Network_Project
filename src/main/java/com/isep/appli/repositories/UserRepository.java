package com.isep.appli.repositories;

import com.isep.appli.dbModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);
	List<User> findByFirstName(String firstName);
	List<User> findByLastName(String lastName);
	List<User> findByFirstNameAndLastName(String firstName, String lastName);

}

