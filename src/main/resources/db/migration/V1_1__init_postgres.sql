create table if not exists "KiteWeather"
(
    "location" VARCHAR(64) primary key,
    "timestamp" timestamp not null,
    "windSpeed" varchar(64) not null,
    "windSpeedUnit" varchar(64) not null,
    "waveHeight" varchar(64) not null,
    "waveHeightUnit" varchar(64) not null,
    "windDirection" varchar(64) not null,
    "windDirectionUnit" varchar(64) not null
)