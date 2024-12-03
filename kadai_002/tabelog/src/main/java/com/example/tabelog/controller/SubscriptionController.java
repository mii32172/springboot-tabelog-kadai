package com.example.tabelog.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.tabelog.entity.User;
import com.example.tabelog.repository.RoleRepository;
import com.example.tabelog.repository.UserRepository;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.StripeService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SubscriptionController {
	private final StripeService stripeService;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public SubscriptionController(StripeService stripeService, UserRepository userRepository,
			RoleRepository roleRepository) {
		this.stripeService = stripeService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@GetMapping("/subsc")
	public String subsc(HttpServletRequest httpServletRequest,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			Model model) {
		User user = userDetailsImpl.getUser(); // 現在のユーザー情報を取得するメソッド

		// サブスクリプション作成のためのセッションIDを生成
		String sessionId = stripeService.createSubscription(user, httpServletRequest);

		// セッションIDをモデルに追加
		model.addAttribute("sessionId", sessionId);
		return "user/subscription";
	}

	@GetMapping("/subsc/user/success")
	public String success(@RequestParam("session_id") String sessionId, RedirectAttributes redirectAttributes) {
		// 成功した場合の処理
		redirectAttributes.addFlashAttribute("successMessage", "サブスクリプション支払いが成功しました。画面を更新するには一度ログアウトし、再度ログインをお願いします。");
		return "redirect:/user"; // 成功メッセージページにリダイレクト
	}

	@GetMapping("/user/cancel")
	public String cancel(RedirectAttributes redirectAttributes) {
		// キャンセルした場合の処理
		redirectAttributes.addFlashAttribute("errorMessage", "サブスクリプション支払いが失敗しました。");
		return "redirect:/user"; // キャンセルページにリダイレクト
	}

	@GetMapping("/withdrawal")
	public String withdrawal(Model model) {
		return "user/withdrawal";
	}

	@PostMapping("/cancel-subscription")
	public String cancelSubscription(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes) {
		User user = userDetailsImpl.getUser();
		String subscriptionId = user.getSubscriptionId();
		String email = user.getEmail();
		if (subscriptionId != null && !subscriptionId.isEmpty()) {
			try {
				// サブスクリプションのキャンセル
				stripeService.cancelSubscription(subscriptionId, email);
				redirectAttributes.addFlashAttribute("successMessage",
						"サブスクリプションがキャンセルされました。画面を更新するには一度ログアウトし、再度ログインをお願いします。");
			} catch (Exception e) {
				e.printStackTrace();
				redirectAttributes.addFlashAttribute("errorMessage", "サブスクリプションのキャンセルに失敗しました。");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "サブスクリプションIDが見つかりません。");
		}

		return "redirect:/user";

	}

	@GetMapping("/customer/portal")
	public RedirectView redirectToCustomerPortal(@RequestParam("email") String email,
			HttpServletRequest httpServletRequest) throws StripeException {
		// 顧客のメールアドレスを使ってポータルリンクを生成
		String portalUrl = stripeService.createCustomerPortalSession(email, httpServletRequest);

		// ポータルURLにリダイレクト
		return new RedirectView(portalUrl);
	}
}