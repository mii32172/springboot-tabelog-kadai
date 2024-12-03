package com.example.tabelog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>{
	public List<Restaurant> findTop3ByOrderByCreatedAtDesc();

	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	public Page<Restaurant> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);

	public Page<Restaurant> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);

	public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);

	public Page<Restaurant> findAllByOrderByPriceAsc(Pageable pageable);

	public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	public Page<Restaurant> findByCategoryIdAndNameLike(Integer id, String string, Pageable pageable);

	public Page<Restaurant> findByCategoryId(Integer id, Pageable pageable);

	public Page<Restaurant> findByPriceLessThanEqualOrderByPriceAsc(Integer string, Pageable pageable);

	public Page<Restaurant> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer string, Pageable pageable);   
     
	}



