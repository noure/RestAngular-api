package fr.example.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.example.springboot.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post>{

}
