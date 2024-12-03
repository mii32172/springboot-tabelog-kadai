package com.example.tabelog.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.FavoriteRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.security.UserDetailsImpl;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;
	
	public FavoriteService(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
	}
	
	/*お気に入り登録*/
	@Transactional
	public void add(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Integer houseId) {
		Favorite favorite = new Favorite();
		User user = userDetailsImpl.getUser();
		Restaurant restaurant =restaurantRepository.getReferenceById(houseId);
		
		favorite.setRestaurant(restaurant);
		favorite.setUser(user);
		
		favoriteRepository.save(favorite);
	}
	
	/*お気に入り解除 */
	@Transactional
	public void delete(Integer restaurantId, Integer userId) {
		Favorite favorite = favoriteRepository.findByRestaurantIdAndUserId(restaurantId, userId);
		if (favorite != null) {
			favoriteRepository.delete(favorite);
		}
	}
	
	

}