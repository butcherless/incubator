CREATE TABLE IF NOT EXISTS airports (
    iata_code    VARCHAR(3)   NOT NULL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    country_code VARCHAR(2)   NOT NULL REFERENCES countries (code),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_airports_country ON airports (country_code);
