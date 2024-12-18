package com.example.tabelog.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tabelog.entity.Category;
import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationInputForm;
import com.example.tabelog.repository.CategoryRepository;
import com.example.tabelog.repository.FavoriteRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.repository.ReviewRepository;
import com.example.tabelog.security.UserDetailsImpl;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

	@Autowired
	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	private final CategoryRepository categoryRepository;


	public RestaurantController(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,
			FavoriteRepository favoriteRepository, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteRepository = favoriteRepository;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<Restaurant> restaurantPage;

		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc(
						"%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);

		return "restaurants/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model,
			@PageableDefault(page = 0, size = 6, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		Page<Review> reviewPage = reviewRepository.findByRestaurantId(id, pageable);


		if (userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			List<Review> userHasReviews = reviewRepository.findByUserIdAndRestaurantId(user.getId(), id);
			boolean notFavoriteExists = !favoriteRepository.favoriteJudge(restaurant, user);

			if (!notFavoriteExists) {
				Favorite favorite = favoriteRepository.findByRestaurantIdAndUserId(restaurant.getId(), user.getId());

				if (favorite != null) {
					// 最初のエントリを取得する（重複を排除したい場合）
					model.addAttribute("favorite", favorite);
				}
			}
			model.addAttribute("notFavoriteExists", notFavoriteExists);
			model.addAttribute("userHasReviews", !userHasReviews.isEmpty());
		} else {
			List<Review> userHasReviews = reviewRepository.findByRestaurantId(id);
			model.addAttribute("userHasReviews", userHasReviews.isEmpty());
		}
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reservationInputForm", new ReservationInputForm());

		model.addAttribute("reviewPage", reviewPage);

		return "restaurants/show";
	}
	
	@GetMapping("/category/{id}")
	public String category(@PathVariable("id") Integer id,
			@RequestParam(value = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {

		Category category = categoryRepository.getReferenceById(id);
		Page<Restaurant> restaurantPage;
		if (keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantRepository.findByCategoryIdAndNameLike(id, "%" + keyword + "%", pageable);
		} else {
			restaurantPage = restaurantRepository.findByCategoryId(id, pageable);
		}
		model.addAttribute("category", category);
		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);

		return "restaurants/category";
	}
}
