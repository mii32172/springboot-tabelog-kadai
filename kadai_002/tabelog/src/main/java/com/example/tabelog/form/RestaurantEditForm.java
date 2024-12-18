package com.example.tabelog.form;

import org.springframework.web.multipart.MultipartFile;

import com.example.tabelog.entity.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RestaurantEditForm {
	@NotNull
	private Integer id;

	@NotBlank(message = "店舗名を入力してください。")
	private String name;

	private MultipartFile imageFile;

	@NotNull(message = "カテゴリを入力してください。")
	private Category category;

	@NotBlank(message = "説明を入力してください。")
	private String description;

	@NotBlank(message = "営業時間を入力してください。")
	private String openTime;

	@NotNull(message = "価格を入力してください。")
	@Min(value = 1, message = "価格は1円以上に設定してください。")
	private Integer price;

	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;

	@NotBlank(message = "住所を入力してください。")
	private String address;

	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;

	@NotBlank(message = "定休日を入力してください。")
	private String closingDay;

}
