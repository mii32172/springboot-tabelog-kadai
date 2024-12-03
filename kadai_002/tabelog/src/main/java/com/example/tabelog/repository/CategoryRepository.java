package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	Page<Category> findByNameLike(String string, Pageable pageable);

	Category getCategoryById(Integer categoryId);
	
	
}