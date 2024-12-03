let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
 
 flatpickr('#fromCheckinDateToCheckoutDate', {
   mode: "single",
   locale: 'ja',
   minDate: 'today',
   maxDate: maxDate,
   enableTime: true,
   dateFormat: "Y-m-d H:i",
   time_24hr: true
 });       