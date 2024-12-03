package com.example.tabelog.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.tabelog.entity.Category;
import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.repository.CategoryRepository;
import com.example.tabelog.repository.FavoriteRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.security.UserDetailsImpl;

@Controller
public class TopPageController {
	private final RestaurantRepository restaurantRepository;
	private final FavoriteRepository favoriteRepository;
	private final CategoryRepository categoryRepository;

	public TopPageController(RestaurantRepository restaurantRepository, FavoriteRepository favoriteRepository,
			CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.favoriteRepository = favoriteRepository;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping("/")
	public String index(Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		List<Restaurant> newRestaurant = restaurantRepository.findTop3ByOrderByCreatedAtDesc();
		// ログインしている場合のみお気に入りを取得
		if (userDetailsImpl != null) {
			int user = userDetailsImpl.getUser().getId(); // UserDetailsImpl から User オブジェクトを取得し、user_id を取り出す

			// user_id に基づいてお気に入りを取得
			List<Favorite> newFavorite = favoriteRepository.findTop3ByUserIdOrderByCreatedAtDesc(user);
			model.addAttribute("newFavorite", newFavorite);
		} else {
			// ログインしていない場合は、お気に入りを表示しない
			model.addAttribute("newFavorite", Collections.emptyList());
		}
		List<Category> category = categoryRepository.findAll();

		model.addAttribute("newRestaurant", newRestaurant);
		model.addAttribute("category", category);
		return "index";
	}
}