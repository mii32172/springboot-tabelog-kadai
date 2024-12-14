package com.example.tabelog.form;

import java.time.LocalDate;

//import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
    @NotBlank(message = "チェックイン日時を選択してください。")
    private String fromCheckinDateToCheckoutDate;    
    
    @NotNull(message = "予約人数を入力してください。")
    @Min(value = 1, message = "予約人数は1人以上に設定してください。")
    private Integer numberOfPeople; 
    
    
    // チェックイン日を取得する 12/12追加
    public LocalDate getCheckinDate() {
        String[] checkinDateAndCheckoutDate = getFromCheckinDateToCheckoutDate().split(" から ");
        return LocalDate.parse(checkinDateAndCheckoutDate[0]);
    }

   
}