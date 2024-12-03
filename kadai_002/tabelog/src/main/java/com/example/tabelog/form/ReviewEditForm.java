package com.example.tabelog.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {

	@NotNull
	private Integer id;
	
	@NotNull(message = "評価をつけてください。")
	@Max(value = 5)
	private Integer star;
	
	@NotBlank(message = "コメントをつけてください。")
	private String explanation;
}