CREATE TABLE IF NOT EXISTS routes (
    id               UUID         NOT NULL PRIMARY KEY,
    origin_iata      VARCHAR(3)   NOT NULL REFERENCES airports (iata_code),
    destination_iata VARCHAR(3)   NOT NULL REFERENCES airports (iata_code),
    airline_icao     VARCHAR(3)   NOT NULL REFERENCES airlines (icao_code),
    distance_km      INTEGER      NOT NULL CHECK (distance_km > 0),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_route_segment UNIQUE (origin_iata, destination_iata, airline_icao)
);

CREATE INDEX IF NOT EXISTS idx_routes_origin      ON routes (origin_iata);
CREATE INDEX IF NOT EXISTS idx_routes_destination ON routes (destination_iata);
CREATE INDEX IF NOT EXISTS idx_routes_airline     ON routes (airline_icao);
