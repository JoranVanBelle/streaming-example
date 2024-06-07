create table if not exists "kite_weather"
(
    "location" VARCHAR(64) primary key,
    "timestamp" timestamp not null,
    "wind_speed" numeric(12,6) not null,
    "wind_speed_unit" varchar(64) not null,
    "wave_height" numeric(12,6) not null,
    "wave_height_unit" varchar(64) not null,
    "wind_direction" numeric(12,6) not null,
    "wind_direction_unit" varchar(64) not null
)