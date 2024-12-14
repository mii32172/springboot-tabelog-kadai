package com.example.tabelog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
    Page<Favorite> findByUser(User user, Pageable pageable);

    default boolean favoriteJudge(Restaurant restaurant, User user) {
		return findByRestaurantIdAndUserId(restaurant.getId(), user.getId()) != null;
	}

	List<Favorite> findTop3ByOrderByCreatedAtDesc();

	Favorite findByRestaurantIdAndUserId(Integer restaurantId, Integer userId);

	List<Favorite> findTop3ByUserIdOrderByCreatedAtDesc(int user);

}