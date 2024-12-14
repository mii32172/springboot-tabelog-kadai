package com.example.tabelog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.FavoriteRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.FavoriteService;

@Controller
public class FavoriteController {

	@Autowired
	private final RestaurantRepository restaurantRepository;
	private final FavoriteService favoriteService;
	private final FavoriteRepository favoriteRepository;
	
	public FavoriteController(RestaurantRepository restaurantRepository, FavoriteService favoriteService, FavoriteRepository favoriteRepository) {
		this.restaurantRepository = restaurantRepository;
		this.favoriteService = favoriteService;
		this.favoriteRepository = favoriteRepository;
	}
	
	 /*お気に入り一覧への遷移*/
	@GetMapping("/favorites")
	public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
	User user = userDetailsImpl.getUser();
	Page<Favorite> favoritePage = favoriteRepository.findByUser(user, pageable);
	
	model.addAttribute("favoritePage", favoritePage);
	
	return "favorites/index";
	}
	
	/*お気に入りの追加*/
	@PostMapping("/restaurants/{restaurantId}/favorites/add")
	public String add(@PathVariable(name = "restaurantId") Integer restaurantId, 
	                  Model model, 
	                  @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                  RedirectAttributes redirectAttributes) {
	    favoriteService.add(userDetailsImpl, restaurantId);
	    redirectAttributes.addFlashAttribute("notFavorite", false);
	return "redirect:/restaurants/{restaurantId}"; 	
	}
	
	/*お気に入りの解除*/
	@PostMapping("/restaurants/{restaurantId}/favorites/{id}/delete")
	public String delete(@PathVariable(name = "restaurantId") Integer restaurantId, 
	                     @PathVariable(name = "id") Integer id, 
	                     Model model, 
	                     @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                     RedirectAttributes redirectAttributes) {
	    favoriteService.delete(restaurantId, userDetailsImpl.getUser().getId());
	    redirectAttributes.addFlashAttribute("notFavorite", true);
		
	return "redirect:/restaurants/{restaurantId}";
	}
}