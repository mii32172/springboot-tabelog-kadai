package com.example.tabelog.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer>{

	Page<Review> findByRestaurantId(Integer restaurantId, Pageable pageable);

	 List<Review> findByUserIdAndRestaurantId(Integer userId, Integer restaurantId);
	 
	 
    //Pageだけでいい？いるのか不明
	List<Review> findByRestaurantId(Integer restaurantId);
	
	Page<Review> findByNameLike(String keyword, Pageable pageable);

}