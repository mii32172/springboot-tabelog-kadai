package com.example.tabelog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.Review;
import com.example.tabelog.form.ReviewEditForm;
import com.example.tabelog.form.ReviewRegisterForm;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.repository.ReviewRepository;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.ReviewService;

@Controller
@RequestMapping("restaurants/{restaurantId}/reviews")
public class ReviewController {
	private final ReviewService reviewService;
	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;

	public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository,
			RestaurantRepository houseReository) {
		this.reviewService = reviewService;
		this.reviewRepository = reviewRepository;
		this.restaurantRepository = houseReository;
	}

	//レビュー一覧へ遷移するためのメソッド
	@GetMapping
	public String review(@PathVariable(name = "restaurantId") Integer restaurantId, Model model,
			@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		Page<Review> reviewPage = reviewRepository.findByRestaurantId(restaurantId, pageable);
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);

		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewPage", reviewPage);

		return "reviews/review";
	}

	//レビュー登録フォームに遷移するためのメソッド
	@GetMapping("/registerform")
	public String register(@PathVariable(name = "restaurantId") Integer houseId, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(houseId);
		Review review = new Review();

		model.addAttribute("restaurant", restaurant);
		model.addAttribute("review", review);
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());

		return "reviews/reviewRegister";
	}

	//レビューの作成するためにサービスクラスの処理をビューに渡すメソッド
	@PostMapping("/create")
	public String create(@PathVariable(name = "restaurantId") Integer restaurantId,
			@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		Review review = new Review();

		

		if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
		    model.addAttribute("review", review);
			model.addAttribute("errorMessage", "投稿内容に不備があります。");
			return "reviews/reviewRegister";
		}

		reviewService.create(restaurant, reviewRegisterForm, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");

		return "redirect:/restaurants/{restaurantId}";
	}

	//レビューの編集ページに遷移するためのメソッド
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "restaurantId") Integer restaurantId, @PathVariable(name = "id") Integer id,
			Model model) {
		Review review = reviewRepository.getReferenceById(id);
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getStar(), review.getExplanation());

		model.addAttribute("review", review);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewEditForm", reviewEditForm);

		return "reviews/edit";
	}

	//レビューの更新するための処理をビューに渡すためのメソッド
	@PostMapping("/{id}/update")
	public String update(@PathVariable(name = "restaurantId") Integer restaurantId, @PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			Model model) {
		Review review = reviewRepository.getReferenceById(id);
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);

		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			model.addAttribute("review", review);

			return "reviews/edit";
		}

		reviewService.update(id, reviewEditForm, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "レビューの内容を編集しました。");

		return "redirect:/restaurants/{restaurantId}"; // 編集したら店舗詳細ページへ戻す
	}

	//レビューを削除するためのメソッド
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "restaurantId") Integer houseId, @PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		reviewRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

		return "redirect:/restaurants/{restaurantId}"; // 編集したら民宿詳細ページへ戻す
	}

}