package com.example.tabelog.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Category;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.form.RestaurantEditForm;
import com.example.tabelog.form.RestaurantRegisterForm;
import com.example.tabelog.repository.CategoryRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.service.RestaurantService;

@Controller
@RequestMapping("/admin")

public class AdminController {
	private final RestaurantRepository restaurantRepository;
	private final RestaurantService restaurantService;
	private final CategoryRepository categoryRepository;

	public AdminController(RestaurantRepository restaurantRepository, RestaurantService restaurantService,
			CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.restaurantService = restaurantService;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	public String top() {
		return "admin/adminTop";
	}

	@GetMapping("/restaurants")
	public String restaurant(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Restaurant> restaurantPage;

		if (keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			restaurantPage = restaurantRepository.findAll(pageable);
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);

		return "admin/restaurants/index";
	}

	@GetMapping("/restaurants/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);

		model.addAttribute("restaurant", restaurant);

		return "admin/restaurants/show";
	}

	@GetMapping("/restaurants/register")
	public String register(Model model) {
		List<Category> category = categoryRepository.findAll();// カテゴリのリストを取得
		model.addAttribute("category", category);
		model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
		return "admin/restaurants/register";
	}

	//店舗登録
	@PostMapping("/restaurants/create")
	public String create(@ModelAttribute @Validated RestaurantRegisterForm restaurantRegisterForm,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			List<Category> category = categoryRepository.findAll(); // エラーがある場合、カテゴリリストを再度取得

			model.addAttribute("category", category);

			return "admin/restaurants/register";
		}

		restaurantService.create(restaurantRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");

		return "redirect:/admin/restaurants";
	}

	//店舗情報編集ページに遷移
	@GetMapping("/restaurants/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		String imageName = restaurant.getImageName();
		RestaurantEditForm restaurantEditForm = new RestaurantEditForm(
				restaurant.getId(),
				restaurant.getName(),
				null,
				restaurant.getCategory(),
				restaurant.getDescription(),
				restaurant.getOpenTime(),
				restaurant.getPrice(),
				restaurant.getPostalCode(),
				restaurant.getAddress(),
				restaurant.getPhoneNumber(),
				restaurant.getClosingDay());

		// カテゴリ一覧をモデルに追加
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("restaurantEditForm", restaurantEditForm);

		model.addAttribute("imageName", imageName);

		return "admin/restaurants/edit";
	}

	//店舗情報更新
	@PostMapping("/restaurants/{id}/update")
	public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/restaurants/edit";
		}

		restaurantService.update(restaurantEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");

		return "redirect:/admin/restaurants";
	}

	//店舗情報削除
	@PostMapping("/restaurants/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		restaurantRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");

		return "redirect:/admin/restaurants";
	}
}
