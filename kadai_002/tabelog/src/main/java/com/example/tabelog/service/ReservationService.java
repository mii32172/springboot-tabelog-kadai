package com.example.tabelog.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.Restaurant;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.ReservationRepository;
import com.example.tabelog.repository.RestaurantRepository;
import com.example.tabelog.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	//追加
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public LocalDate parseCheckinDate(String dateString) {
        try {
            return LocalDate.parse(dateString, dateFormatter);
        } catch (DateTimeParseException e) {
            // Handle exception, possibly by logging and throwing a custom exception
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }
	
	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		
	}
	//予約
	@Transactional
	public void create(Map<String, String> paymentIntentObject) {
		Reservation reservation = new Reservation();
		
		 Integer restaurantId = Integer.valueOf(paymentIntentObject.get("restaurantId"));
         Integer userId = Integer.valueOf(paymentIntentObject.get("userId"));
        
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		User user = userRepository.getReferenceById(userId);
		LocalDate checkinDate = parseCheckinDate(paymentIntentObject.get("checkinDate"));
		Integer numberOfPeople = Integer.valueOf(paymentIntentObject.get("numberOfPeople"));        
        Integer amount = Integer.valueOf(paymentIntentObject.get("amount"));
        
        reservation.setRestaurant(restaurant);
        reservation.setUser(user);
        reservation.setCheckinDate(checkinDate);
         reservation.setNumberOfPeople(numberOfPeople);
         reservation.setAmount(amount);
        

        
        reservationRepository.save(reservation);
    }    
  
   
	
    // 価格を計算する
    public Integer calculateAmount(Integer price, Integer numberOfPeople) {
 
        int amount = price * numberOfPeople;
        return amount;
    }    
    
    public Reservation findById(Integer reservationId) {
		return reservationRepository.findById(reservationId)
				.orElse(null);
	}

	public void delete(Integer reservationId) {
		reservationRepository.deleteById(reservationId);
	}
}