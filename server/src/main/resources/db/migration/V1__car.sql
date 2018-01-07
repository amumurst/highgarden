CREATE TABLE car (
    car_id serial PRIMARY KEY,
    licence_plate VARCHAR NOT NULL,
    color VARCHAR NOT NULL
);
INSERT INTO car (licence_plate, color) VALUES ('AA00000', 'RED');
INSERT INTO car (licence_plate, color) VALUES ('AA00000', 'BLUE');