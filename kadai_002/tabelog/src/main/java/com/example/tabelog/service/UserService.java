package com.example.tabelog.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Role;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.SignupForm;
import com.example.tabelog.form.UserEditForm;
import com.example.tabelog.repository.RoleRepository;
import com.example.tabelog.repository.UserRepository;
import com.example.tabelog.repository.VerificationTokenRepository;





@Service

public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender mailSender;
	
	public UserService(UserRepository userRepository, RoleRepository roleRepository, VerificationTokenRepository verificationTokenRepository,
			PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailSender = mailSender;
		
	}
	
	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_FREE");
		
		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);
		
		return userRepository.save(user);
		
	}
	
	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());
		
		user.setName(userEditForm.getName());
		user.setFurigana(userEditForm.getFurigana());
		user.setEmail(userEditForm.getEmail());
		
		userRepository.save(user);
	}
	
	//メールアドレスが登録済みかどうかチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}
	
	//パスワードとパスワード(確認用)の入力値が一致するかどうかチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}
	
	//ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}
	
	//メールアドレスが変更されたかどうかチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}
	
	
	//新しく追加
	public void save(User user) {
		userRepository.save(user);
	}

	public User getCurrentUser() {
		// TODO 自動生成されたメソッド・スタブ
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Transactional
	public void updateRoleToFree(User user) {
		// IDが2のRole（ROLE_FREE）を取得
		Role freeMemberRole = roleRepository.findById(1)
				.orElseThrow(() -> new RuntimeException("Role not found"));

		// ユーザーのロールを更新
		user.setRole(freeMemberRole);

		// ユーザーの情報をデータベースに保存
		userRepository.save(user);
	}

	// ユーザーをメールアドレスで検索
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void sendPasswordResetEmail(String email, String resetUrl) {
		// メール送信ロジック
		String subject = "パスワード再設定";
		String body = "以下のリンクをクリックしてパスワードを再設定してください: " + resetUrl;
		// 実際のメール送信処理
		// メールメッセージの作成
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email); // 送信先
		message.setSubject(subject); // 件名
		message.setText(body); // 本文

		// メールの送信
		mailSender.send(message);
	}

	// パスワードを更新するメソッド
	@Transactional
	public void updatePassword(User user, String newPassword) {
		// パスワードをハッシュ化して保存
		String encodedPassword = passwordEncoder.encode(newPassword);
		System.out.println("Encoded password: " + encodedPassword);
		user.setPassword(encodedPassword);
		userRepository.save(user);
		System.out.println("Password updated for user: " + user.getName()); // デバッグ用ログ
	}

	public boolean checkPassword(User user, String currentPassword) {
		boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
		System.out.println("Password match: " + matches); // デバッグ用ログ
		return matches;
	}

	@Transactional
	public void deleteUser(User user) {
		verificationTokenRepository.deleteByUserId(user.getId());
		userRepository.delete(user);
	}

}

