 const stripe = Stripe('pk_test_51QDKjnHp1iv0Lmvp6Q3BhIOsp97QkAdHTP3YFZDYOYxRWqD8dM4AKDvrQeQXaimcBcUqdTeV2yEivRhPuOicGZct00uESgjun9');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });
 
  document.addEventListener('DOMContentLoaded', async () => {
  let searchParams = new URLSearchParams(window.location.search);
  if (searchParams.has('session_id')) {
    const session_id = searchParams.get('session_id');
    document.getElementById('session-id').setAttribute('value', session_id);
  }
});

document.getElementById('cancelButton').addEventListener('click', function() {
    fetch('/cancel-subscription', {
        method: 'POST'
    })
    .then(response => response.text())
    .then(data => alert(data))
    .catch(error => alert('Error: ' + error));
});
