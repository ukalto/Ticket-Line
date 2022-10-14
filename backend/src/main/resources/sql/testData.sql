DELETE FROM APPLICATION_USER;
ALTER TABLE APPLICATION_USER ALTER COLUMN id RESTART WITH 1;

DELETE FROM LOCATION;
ALTER TABLE LOCATION ALTER COLUMN id RESTART WITH 1;

DELETE FROM SEATING_PLAN;
ALTER TABLE SEATING_PLAN ALTER COLUMN id RESTART WITH 1;

delete from invoice;
alter table invoice alter column invoice_number restart with 1;

/*password: password*/
INSERT INTO APPLICATION_USER (ID,EMAIL, PASSWORD, TYPE, MEMBER_SINCE, FAILED_AUTHENTICATION_ATTEMPTS, IS_BLOCKED)
VALUES (-1,'superadmin@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_SUPER_ADMINISTRATOR','2022-05-01 00:00:01', 0, false),
       (-2,'admin@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_ADMINISTRATOR','2022-05-01 00:00:01', 0, false),
       (-3,'user1@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-4,'user2@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-5,'user3@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-6,'user4@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-7,'user5@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-8,'user6@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-9,'user7@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-10,'user8@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-11,'user9@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-12,'user10@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-13,'user11@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-14,'testAdmin@email.com', '', 'ROLE_ADMINISTRATOR','2022-05-01 00:00:01', 0, false),
       (-15,'testUser@email.com', '', 'ROLE_USER','2022-05-01 00:00:01', 0, false),
       (-16,'testUserAccess@gmail.com', '$2a$10$MEqWzACbedAea.oXs9Zt2uYhG.L0Adxswvpn6CzOL48Npre32bmiO', 'ROLE_USER','2022-05-01 00:00:01', 0, false);


INSERT INTO LOCATION (id, name, country, town, street, postal_code)
VALUES
       (-1, 'Cineplexx Donau Zentrum', 'Austria', 'Vienna', 'Wagramstrasse 79', '1220'),
       (-2, 'Cineplexx Graz', 'Austria', 'Graz', 'Alte Poststrasse 79', '8055'),
       (-3, 'Actors Studio', 'Austria', 'Vienna', 'Turchlauben 13', '1010'),
       (-4, 'Apollo', 'Austria', 'Vienna', 'Gumpendorferstraße 63', '1060');


INSERT INTO seating_plan (id, name, located_in, capacity)
VALUES (-1, 'testroom', -1, 150);

INSERT INTO seating_plan_sector (number, seating_plan_id, name, color, capacity, type)
VALUES (-1, -1,'testsector', '#002901', 50, 'standing');

INSERT INTO seating_plan_sector (number, seating_plan_id, name, color, capacity, type)
VALUES (-2, -1,'testsector', '#002901', 100, 'standing 2');

INSERT INTO EVENT_CATEGORY (id, display_name)
VALUES (-1,'Sport'),
       (-2,'Music'),
       (-3,'Comedy'),
       (-4,'Thriller');

INSERT INTO ARTIST (id, first_name, last_name, artist_name)
VALUES (-1,'','','50 Cent'),
       (-2,'Max','Peter',''),
       (-3,'','','Ronaldinho'),
       (-4,'','','Eminem'),
       (-5,'','','John Cena');

INSERT INTO EVENT (id, title, category_id, duration, description)
VALUES (-1,'50 Cent Show',-2, 30000, 'Today we are going ham!!!'),
       (-2,'50 Cent Show',-2, 30000, 'Today the show will be increased by 1 hour in length!!!'),
       (-3,'Ronaldinho Freestyle Show',-1, 10000, 'Today Ronaldinho will show you his best skills!'),
       (-4,'Max Peters comedy Show',-3, 3600, 'Fun is granted'),
       (-5,'Eminem',-2, 3000, 'Eminems debut Tour');

INSERT INTO ARTIST_PERFORMANCE(ARTIST_ID, EVENT_ID)
VALUES (-1,-1),
       (-1,-2),
       (-3,-3),
       (-2,-4),
       (-4,-5);

insert into event_showing (id, event_id, occurs_on, performed_at)
values
  (-1, -1, (current_timestamp()+111111), -2),
  (-2, -1, (current_timestamp()+111111), -2),
  (-3, -2, (current_timestamp()+111111), -2),
  (-4, -2, (current_timestamp()+111111), -1),
  (-5, -3, (current_timestamp()+111111), -4)
;

insert into booking (id, booked_at, booked_by, event_showing_id, is_cancelled, cost, secret)
  values (-1, current_timestamp(), -1, -1, false, 23.00, 'verysecret')
;

insert into booking_non_seat(booking_id, seating_plan_id, seating_plan_sector, amount)
  values (-1, -1, -1, 3)
;

insert into booking_non_seat(booking_id, seating_plan_id, seating_plan_sector, amount)
  values (-1, -1, -2, 3)
;

insert into booking (id, booked_at, booked_by, event_showing_id, is_cancelled, cost, secret)
  values (-2, current_timestamp(), -2, -2, false, 14.00, 'verysecret')
;

insert into booking_non_seat(booking_id, seating_plan_id, seating_plan_sector, amount)
  values (-2, -1, -1, 1)
;

insert into booking_non_seat(booking_id, seating_plan_id, seating_plan_sector, amount)
  values (-2, -1, -2, 1)
;
insert into booking (id, booked_at, booked_by, event_showing_id, is_cancelled, cost, secret)
  values (-3, current_timestamp(), -3, -4, false, 32.50, 'secret')
;

insert into booking_non_seat(booking_id, seating_plan_id, seating_plan_sector, amount)
  values (-3, -1, -1, 11)
;

insert into booking_non_seat(booking_id, seating_plan_id, seating_plan_sector, amount)
  values (-3, -1, -2, 21)
;

insert into invoice (invoice_number, booking_id, invoice_type, purchased_at)
  values (-1, -1, 'PURCHASE', current_timestamp())
;
