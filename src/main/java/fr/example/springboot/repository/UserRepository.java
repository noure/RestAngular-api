package fr.example.springboot.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.example.springboot.domain.User;

/**
 * @author Nour LABIHI
 *
 */

public interface UserRepository extends JpaRepository<User, Serializable>, JpaSpecificationExecutor<User> {

	User findByUsername(String userName);
	
}
