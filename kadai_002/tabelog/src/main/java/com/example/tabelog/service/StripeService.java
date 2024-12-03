package com.example.tabelog.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.tabelog.entity.Role;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationRegisterForm;
import com.example.tabelog.repository.RoleRepository;
import com.example.tabelog.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	@Value("${stripe.api-key}")
	private String stripeApiKey;
	private final ReservationService reservationService;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserService userService;

	public StripeService(ReservationService reservationService, UserRepository userRepository,
			RoleRepository roleRepository, UserService userService) {
		this.reservationService = reservationService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userService = userService;
	}

	public String createStripeSession(String restaurantName, ReservationRegisterForm reservationRegisterForm,
			HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
		String requestUrl = new String(httpServletRequest.getRequestURL());
		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setPriceData(
										SessionCreateParams.LineItem.PriceData.builder()
												.setProductData(
														SessionCreateParams.LineItem.PriceData.ProductData.builder()
																.setName(restaurantName)
																.build())
												.setUnitAmount((long) reservationRegisterForm.getAmount())
												.setCurrency("jpy")
												.build())
								.setQuantity(1L)
								.build())
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(
						requestUrl.replaceAll("/restaurant/[0-9]+/reservation/confirm", "")
								+ "/reservation?reserved?sessionId={CHECKOUT_SESSION_ID}")
				.setCancelUrl(requestUrl.replace("/reservation/confirm", ""))
				.setPaymentIntentData(
						SessionCreateParams.PaymentIntentData.builder()
								.putMetadata("restaurantId", reservationRegisterForm.getRestaurantId().toString())
								.putMetadata("userId", reservationRegisterForm.getUserId().toString())
								.putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())
								.putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())
								.putMetadata("amount", reservationRegisterForm.getAmount().toString())
								.build())
				.build();
		try {
			Session session = Session.create(params);
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void expireCheckoutSession(String sessionId) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		// Checkout Sessionの期限を切らす（キャンセルする）
		Session session = Session.retrieve(sessionId);
		session.expire();
	}

	public void processSessionCompleted(Event event) {
		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
		optionalStripeObject.ifPresentOrElse(stripeObject -> {
			Session session = (Session) stripeObject;
			if (session.getMode().equals("subscription"))
				return;
			SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

			try {
				session = Session.retrieve(session.getId(), params, null);
				Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
				reservationService.create(paymentIntentObject);
			} catch (StripeException e) {
				e.printStackTrace();
			}
			System.out.println("予約一覧ページの登録処理が成功しました。");
			System.out.println("Stripe API Version: " + event.getApiVersion());
			System.out.println("stripe-java Version: " + Stripe.VERSION);
		},
				() -> {
					System.out.println("予約一覧ページの登録処理が失敗しました。");
					System.out.println("Stripe API Version: " + event.getApiVersion());
					System.out.println("stripe-java Version: " + Stripe.VERSION);
				});
	}

	public String createSubscription(User user, HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
		String requestUrl = new String(httpServletRequest.getRequestURL());

		// セッション作成
		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setQuantity(1L)
								.setPrice("price_1PrWUNIzVhtPxktW0z7zQ0n8") // あなたが作成した価格ID
								.build())
				.setMode(SessionCreateParams.Mode.SUBSCRIPTION)
				.setSuccessUrl(requestUrl + "/user/success?session_id={CHECKOUT_SESSION_ID}")
				.setCancelUrl(requestUrl.replace("/user/cancel", ""))
				.build();

		try {
			Session session = Session.create(params);
			return session.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void processSubscriptionCreated(String subscriptionId, String customerId) throws StripeException {
		Customer customer = Customer.retrieve(customerId);
		String email = customer.getEmail();
		User user = userRepository.findByEmail(email);

		user.setCustomerId(customerId);
		user.setSubscriptionId(subscriptionId);
		userRepository.save(user);
	}

	public void processSubscriptionPaymentSucceeded(String subscriptionId, String customerId) throws StripeException {
		Customer customer = Customer.retrieve(customerId);
		String email = customer.getEmail();
		User user = userRepository.findByEmail(email);

		if (user != null) {
			try {
				// ロールを有料会員に変更
				Role paidMemberRole = roleRepository.findById(2)
						.orElseThrow(() -> new RuntimeException("Role not found"));
				user.setRole(paidMemberRole);

				// ユーザー情報を更新
				userRepository.save(user);
				System.out.println("ユーザーのロールが有料会員に更新されました。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("顧客IDに対応するユーザーが見つかりませんでした。");
		}
	}

	public void cancelSubscription(String subscriptionId, String email) throws StripeException {
		Stripe.apiKey = stripeApiKey;

		SubscriptionCancelParams params = SubscriptionCancelParams.builder()
				.setProrate(true)
				.build();

		Subscription subscription = Subscription.retrieve(subscriptionId);
		subscription.cancel(params);

		User user = userRepository.findByEmail(email);
		if (user != null) {
			// ロールIDを変更（ここでは仮に「1」が無料ユーザーのロールIDとします）
			Role freeMemberRole = roleRepository.findById(1)
					.orElseThrow(() -> new RuntimeException("Role not found"));
			user.setRole(freeMemberRole);

			user.setCustomerId(null);
			user.setSubscriptionId(null); // サブスクリプションIDをクリアする
			userRepository.save(user);
		}
	}

	public String createCustomerPortalSession(String email, HttpServletRequest httpServletRequest)
			throws StripeException {
		Stripe.apiKey = stripeApiKey;
		// データベースから顧客情報を取得
		User user = userRepository.findByEmail(email);

		if (user == null || user.getCustomerId() == null) {
			throw new IllegalArgumentException("顧客が存在しないか、CustomerIdが設定されていません。");
		}

		String scheme = httpServletRequest.getScheme();
		String serverName = httpServletRequest.getServerName();
		int serverPort = httpServletRequest.getServerPort();
		String contextPath = httpServletRequest.getContextPath();

		String baseUrl = (serverPort == 80 || serverPort == 443)
				? String.format("%s://%s%s", scheme, serverName, contextPath)
				: String.format("%s://%s:%d%s", scheme, serverName, serverPort, contextPath);

		// リダイレクト先URLを設定
		String returnUrl = baseUrl + "/user?email=" + user.getEmail();

		// Stripeのカスタマーポータルセッションを作成
		com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams
				.builder()
				.setCustomer(user.getCustomerId()) // データベースから取得したCustomerIdを使用
				.setReturnUrl(returnUrl) // ポータルを退出したときのリダイレクト先URL
				.build();

		com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);

		// 生成されたポータルURLを返す
		return session.getUrl();
	}

	public void processReservationPayment(Event event) {
		// TODO 自動生成されたメソッド・スタブ
	}

	public Subscription getSubscriptionByLookupKey(String lookupKey) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Subscription getSubscriptionByCustomerId(String customerId) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}