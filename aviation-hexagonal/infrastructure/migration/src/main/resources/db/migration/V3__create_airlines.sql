CREATE TABLE IF NOT EXISTS airlines (
    icao_code    VARCHAR(3)   NOT NULL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    country_code VARCHAR(2)   NOT NULL REFERENCES countries (code),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_airlines_country ON airlines (country_code);
