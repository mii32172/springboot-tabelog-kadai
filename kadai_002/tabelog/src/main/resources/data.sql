-- 有料会員のStripe顧客IDを設定
--UPDATE users 
--SET customer_id = 'cus_premium_member_12345' 
--WHERE email = 'hanako.samurai@example.com';



-- categoriesテーブル
INSERT IGNORE INTO categories (id, name) VALUES(1, "カフェ");
INSERT IGNORE INTO categories (id, name) VALUES(2, "焼肉");
INSERT IGNORE INTO categories (id, name) VALUES(3, "寿司");
INSERT IGNORE INTO categories (id, name) VALUES(4, "中華");
INSERT IGNORE INTO categories (id, name) VALUES(5, "丼物");
INSERT IGNORE INTO categories (id, name) VALUES(6, "和食");

-- restaurantsテーブル
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (1, 1, 'SAMURAICAFE', 'house01.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '09:00-23:00',  '1000-2000',  '450-0001', '名古屋市北区X-XX-XX', '012-345-678', '木曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (2, 1, '侍喫茶', 'house02.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '09:00-22:00',  '1000-2000',  '450-0002', '名古屋市南区X-XX-XX', '012-345-678', '金曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (3, 2, '侍焼肉', 'house03.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '17:00-23:00',  '2000-3000',  '450-0003', '名古屋市東区X-XX-XX', '012-345-678', '土曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (4, 2, 'サムライ炎', 'house04.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '16:00-23:00',  '2000-3000',  '450-0004', '名古屋市西区X-XX-XX', '012-345-678', '日曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (5, 3, 'サムライ寿司', 'house05.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '10:00-24:00',  '3000-4000',  '450-0005', '名古屋市千種区X-XX-XX', '012-345-678', '月曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (6, 3, 'サムライ握り', 'house06.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '11:30-23:00',  '3000-4000',  '450-0006', '名古屋市中村区X-XX-XX', '012-345-678', '火曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (7, 4, 'ラーメンSAMURAI', 'house07.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '12:00-25:00',  '1000-2000',  '450-0007', '名古屋市中区X-XX-XX', '012-345-678', '水曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (8, 4, 'サムライ亭', 'house08.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '11:00-26:00',  '1000-2000',  '450-0008', '名古屋市昭和区X-XX-XX', '012-345-678', '木曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (9, 5, 'SAMURAI屋', 'house09.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '10:00-23:00',  '1000-2000',  '450-0009', '名古屋市瑞穂区X-XX-XX', '012-345-678', '金曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (10, 5, 'サムライ丼', 'house10.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '09:00-22:00',  '1000-2000',  '450-0010', '名古屋市熱田区X-XX-XX', '012-345-678', '土曜日');
INSERT IGNORE INTO restaurants (id, category_id, name, image_name, description, open_time, price,  postal_code, address, phone_number, closing_day) VALUES (11, 6, '和食侍', 'e3bbd41a-ee44-4942-bedf-7b2a50c2393a.jpg', '最寄り駅から徒歩10分の場所にありいい立地である。',  '12:00-20:00',  '2000-4000',  '450-0011', '名古屋市名東区X-XX-XX', '012-345-678', '日曜日');


-- rolesテーブル
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_FREE');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_PAID');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_ADMIN');



-- usersテーブル
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (1, '侍 太郎', 'サムライ タロウ', 'taro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 1, true);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (2, '侍 花子', 'サムライ ハナコ', 'hanako.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 2, true);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (3, '侍 義勝', 'サムライ ヨシカツ', 'yoshikatsu.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 3, true);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (4, '侍 幸美', 'サムライ サチミ', 'sachimi.samurai@example.com', 'password', 2, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (5, '侍 雅', 'サムライ ミヤビ', 'miyabi.samurai@example.com', 'password', 1, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (6, '侍 正保', 'サムライ マサヤス', 'masayasu.samurai@example.com', 'password', 2, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (7, '侍 真由美', 'サムライ マユミ', 'mayumi.samurai@example.com', 'password', 1, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (8, '侍 安民', 'サムライ ヤスタミ', 'yasutami.samurai@example.com', 'password', 2, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (9, '侍 章緒', 'サムライ アキオ', 'akio.samurai@example.com', 'password', 1, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (10, '侍 祐子', 'サムライ ユウコ', 'yuko.samurai@example.com', 'password', 2, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (11, '侍 秋美', 'サムライ アキミ',  'akimi.samurai@example.com', 'password', 1, false);
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES (12, '侍 信平', 'サムライ シンペイ', 'shinpei.samurai@example.com', 'password', 2, false);

-- reservationsテーブル
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, number_of_people,amount) VALUES (1, 1, 2, '2023-04-01 13:00', 2, 2000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, number_of_people, amount) VALUES (2, 2, 2, '2023-04-01 17:30', 3, 3000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, number_of_people, amount) VALUES (3, 4, 2, '2023-04-01 18:00', 4, 8000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, number_of_people, amount) VALUES (4, 6, 2, '2023-04-01 20:00', 1, 3000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, number_of_people, amount) VALUES (5, 7, 2, '2023-04-01 15:00', 2, 2000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, number_of_people, amount) VALUES (6, 9, 2, '2023-04-01 13:00', 3, 3000);


-- reviewsテーブル
INSERT IGNORE INTO reviews (id, name, star, explanation, restaurant_id, user_id) VALUES (1, '侍 幸美', 5, '静かでゆっくりできて最高でした。', 1, 4);
INSERT IGNORE INTO reviews (id, name, star, explanation, restaurant_id, user_id) VALUES (2, '侍 花子', 3, 'まあまあでした。', 1, 2);
INSERT IGNORE INTO reviews (id, name, star, explanation, restaurant_id, user_id) VALUES (3, '侍 正保', 4, 'コーヒーがおいしかったです。', 1, 6);
INSERT IGNORE INTO reviews (id, name, star, explanation, restaurant_id, user_id) VALUES (4, '侍 安民', 5, '静かでゆっくりできて最高でした。', 1, 8);
INSERT IGNORE INTO reviews (id, name, star, explanation, restaurant_id, user_id) VALUES (5, '侍 裕子', 5, '静かでゆっくりできて最高でした。', 1, 10);
INSERT IGNORE INTO reviews (id, name, star, explanation, restaurant_id, user_id) VALUES (6, '侍 信平', 5, '静かでゆっくりできて最高でした。', 1, 12);


-- favoritesテーブル
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (1, 1, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (2, 2, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (3, 3, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (4, 4, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (5, 5, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (6, 6, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (7, 7, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (8, 8, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (9, 9, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (10, 10, 2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (11, 11, 2);