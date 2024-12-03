package com.example.tabelog.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoriteRegisterForm {

	@NotNull
	private Integer restaurantId;
	
	@NotNull
	private Integer userId;
	
}