function validatePasswords() {
    const newPassword = document.querySelector('input[name="newPassword"]').value;
    const confirmPassword = document.querySelector('input[name="confirmPassword"]').value;
    const errorMessage = document.getElementById('error-message');

    // パスワードの長さチェック
    if (newPassword.length < 8 || newPassword.length > 16 || confirmPassword.length < 8 || confirmPassword.length > 16) {
        errorMessage.textContent = 'パスワードは8文字以上16文字以下でお願いします。';
        return false; // フォーム送信を中止
    }

    // 確認用パスワードの一致確認
    if (newPassword !== confirmPassword) {
        errorMessage.textContent = '新しいパスワードと確認用パスワードが一致しません。';
        return false; // フォーム送信を中止
    }

    errorMessage.textContent = ''; // エラーメッセージをクリア
    return true; // フォーム送信を許可
}