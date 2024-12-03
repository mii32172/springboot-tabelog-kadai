package com.example.tabelog.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReviewEditForm;
import com.example.tabelog.form.ReviewRegisterForm;
import com.example.tabelog.repository.ReviewRepository;
import com.example.tabelog.security.UserDetailsImpl;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;

	}
	
	@Transactional
	public void create(Restaurant restaurant, ReviewRegisterForm reviewRegisterForm, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Review review = new Review();
		User user = userDetailsImpl.getUser();
		
		review.setName(user.getName());
		review.setStar(reviewRegisterForm.getStar());
		review.setExplanation(reviewRegisterForm.getExplanation());
		review.setRestaurant(restaurant);
		review.setUser(user);
		
		reviewRepository.save(review);
	}
	
	@Transactional
	public void update(Integer id,ReviewEditForm reviewEditForm,  @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		User user = userDetailsImpl.getUser();
		
		review.setName(user.getName());
		review.setStar(reviewEditForm.getStar());
		review.setExplanation(reviewEditForm.getExplanation());
		review.setUser(user);
		
		reviewRepository.save(review);
	}

}