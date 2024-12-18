package com.example.tabelog.controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationInputForm;
import com.example.tabelog.form.ReservationRegisterForm;
import com.example.tabelog.repository.FavoriteRepository;
import com.example.tabelog.repository.ReservationRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.repository.ReviewRepository;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.ReservationService;
import com.example.tabelog.service.StripeService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReservationController {

	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final ReservationService reservationService;
	private final StripeService stripeService;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;

	public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
			ReservationService reservationService, StripeService stripeService, ReviewRepository reviewRepository,
			FavoriteRepository favoriteRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.reservationService = reservationService;
		this.stripeService = stripeService;
		this. reviewRepository=reviewRepository;
		this.favoriteRepository=favoriteRepository;
	}

	//予約一覧ページへの遷移
	@GetMapping("/reservations")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		model.addAttribute("reservationPage", reservationPage);

		return "reservations/index";
	}

	//予約する
	@GetMapping("/restaurants/{id}/reservations/input")
	public String input(@PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReservationInputForm reservationInputForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,Model model,
			Pageable pageable,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl
			) {
		
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		Page<Review> reviewPage = reviewRepository.findByRestaurantId(id, pageable);
		
		
		 // チェックイン日時を解析する
	    String checkinDateTimeStr = reservationInputForm.getFromCheckinDateToCheckoutDate();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    LocalDateTime checkinDateTime;
	    try {
	        checkinDateTime = LocalDateTime.parse(checkinDateTimeStr, formatter);
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "予約日時の形式が正しくありません。");
	        return "restaurants/show";
	    }

	    // 現在の日時を取得
	    LocalDateTime now = LocalDateTime.now();

	    // 過去日時かどうかをチェック
	    if (checkinDateTime.isBefore(now)) {
	    	
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
			model.addAttribute("reservationInputForm", new ReservationInputForm());

			model.addAttribute("reviewPage", reviewPage);

			
			model.addAttribute("restaurant", restaurant);
	        model.addAttribute("errorMessage", "過去の日時での予約はできません。");
	        return "restaurants/show";
	    }
	    
		
		String checkin_date = reservationInputForm.getFromCheckinDateToCheckoutDate();
		String time = checkin_date.split(" ")[1];
		
		String open_time = restaurant.getOpenTime();
		String open = open_time.split("-")[0];
		String close = open_time.split("-")[1];
		
		if(time.compareTo(open) == -1 || time.compareTo(close) != -1){

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
			model.addAttribute("reservationInputForm", new ReservationInputForm());

			model.addAttribute("reviewPage", reviewPage);

			
			model.addAttribute("restaurant", restaurant);
			
			model.addAttribute("errorMessage", "予約時間が営業時間外です。");
			return "restaurants/show";
			
		}
		
		String closingDay = restaurant.getClosingDay();
		LocalDateTime checkinDateTime2 = LocalDateTime.parse(checkinDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		DayOfWeek closingDayOfWeek;
		switch(closingDay) {
		    case "日曜日":
		        closingDayOfWeek = DayOfWeek.SUNDAY;
		        break;
		    case "月曜日":
		        closingDayOfWeek = DayOfWeek.MONDAY;
		        break;
		    case "火曜日":
		        closingDayOfWeek = DayOfWeek.TUESDAY;
		        break;
		    case "水曜日":
		        closingDayOfWeek = DayOfWeek.WEDNESDAY;
		        break;
		    case "木曜日":
		        closingDayOfWeek = DayOfWeek.THURSDAY;
		        break;
		    case "金曜日":
		        closingDayOfWeek = DayOfWeek.FRIDAY;
		        break;
		    case "土曜日":
		        closingDayOfWeek = DayOfWeek.SATURDAY;
		        break;
		    default:
		        // 定休日情報が無効な場合の処理
		        return "restaurants/show";
		}

		DayOfWeek reservationDayOfWeek = checkinDateTime.getDayOfWeek();
		if (reservationDayOfWeek == closingDayOfWeek) {
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
			model.addAttribute("reservationInputForm", new ReservationInputForm());

			model.addAttribute("reviewPage", reviewPage);

			
			model.addAttribute("restaurant", restaurant);
		    // 定休日の場合のエラーメッセージを設定
		    model.addAttribute("errorMessage", "定休日に予約はできません。");
		    return "restaurants/show";
		}

		
		
		
		// 営業時間をビューに渡す
		model.addAttribute("openTime", restaurant.getOpenTime().toString());


		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			model.addAttribute("errorMessage", "予約内容に不備があります。");
			return "restaurants/show";
		}

		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);

		return "redirect:/restaurants/{id}/reservations/confirm";
	}

	//決済
	@GetMapping("/restaurants/{id}/reservations/confirm")
	public String confirm(@PathVariable(name = "id") Integer id,
			@ModelAttribute ReservationInputForm reservationInputForm,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			HttpServletRequest httpServletRequest,
			Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();

		//チェックイン日時と人数を取得
		String checkinDate = reservationInputForm.getFromCheckinDateToCheckoutDate();
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();

		// 料金を計算する
		Integer price = restaurant.getPrice();
		Integer amount = reservationService.calculateAmount(price, numberOfPeople);

		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(restaurant.getId(), user.getId(),
				checkinDate.toString(), reservationInputForm.getNumberOfPeople(), amount);

		String sessionId = stripeService.createStripeSession(restaurant.getName(), reservationRegisterForm,
				httpServletRequest);

		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);
		model.addAttribute("sessionId", sessionId);

		return "reservations/confirm";
	}

	@GetMapping("/reservations/{reservationId}/delete")
	public String delete(@PathVariable Integer reservationId, RedirectAttributes redirectAttributes) {
		try {
			// 予約を削除する
			Reservation reservation = reservationService.findById(reservationId);
			if (reservation != null) {
				// StripeのCheckout Sessionをキャンセルする
				if (reservation.getSessionId() != null && !reservation.getSessionId().isEmpty()) {
					stripeService.expireCheckoutSession(reservation.getSessionId());
				}
				// 予約を削除
				reservationService.delete(reservationId);
				redirectAttributes.addFlashAttribute("successMessage", "予約がキャンセルされました。");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "予約が見つかりませんでした。");
			}
		} catch (StripeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "支払いのキャンセルに失敗しました。");
			e.printStackTrace();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessgae", "予約の削除に失敗しました。");
			e.printStackTrace();
		}

		return "redirect:/reservations?reserved";
	}

	/*
	@PostMapping("/houses/{id}/reservations/create")
	public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {
	 reservationService.create(reservationRegisterForm);
	 
	 return "redirect:/reservations?reserved";
	}
	*/
}