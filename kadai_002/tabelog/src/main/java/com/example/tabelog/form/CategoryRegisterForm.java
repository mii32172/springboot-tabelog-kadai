package com.example.tabelog.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRegisterForm {
	@NotNull
	private String name;
}