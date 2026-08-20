-- ReserveIt appointment billing/payment phase.
-- Use this when managing an existing production database with explicit migrations.
-- New installations are handled by Hibernate schema generation.

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS price NUMERIC(12,2) NOT NULL DEFAULT 0;

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP NULL;

UPDATE appointments
SET price = 0
WHERE price IS NULL;

UPDATE appointments
SET payment_status = 'PENDING'
WHERE payment_status IS NULL;
