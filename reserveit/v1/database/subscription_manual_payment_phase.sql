-- ReserveIt: manual clinic subscription collection
-- No online payment gateway is used. SUPER_ADMIN marks each monthly fee as PAID/PENDING.

CREATE TABLE IF NOT EXISTS subscription_payments (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinics(id),
    subscription_plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    billing_month DATE NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_at TIMESTAMP NULL,
    marked_by_user_id BIGINT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_subscription_payment_clinic_month UNIQUE (clinic_id, billing_month)
);

CREATE INDEX IF NOT EXISTS idx_subscription_payment_month
    ON subscription_payments (billing_month);

CREATE INDEX IF NOT EXISTS idx_subscription_payment_clinic
    ON subscription_payments (clinic_id);

-- Backfill the current month for clinics that already have a subscription.
INSERT INTO subscription_payments
    (clinic_id, subscription_plan_id, billing_month, amount, paid, created_at)
SELECT
    cs.clinic_id,
    cs.subscription_plan_id,
    date_trunc('month', cs.start_date)::date,
    sp.price_monthly,
    FALSE,
    CURRENT_TIMESTAMP
FROM clinic_subscriptions cs
JOIN subscription_plans sp ON sp.id = cs.subscription_plan_id
WHERE NOT EXISTS (
    SELECT 1
    FROM subscription_payments p
    WHERE p.clinic_id = cs.clinic_id
      AND p.billing_month = date_trunc('month', cs.start_date)::date
);

-- If an earlier version created marked_by instead of marked_by_user_id,
-- keep the schema aligned with SubscriptionPayment.markedBy.
ALTER TABLE subscription_payments
    ADD COLUMN IF NOT EXISTS marked_by_user_id BIGINT NULL REFERENCES users(id);

-- Ensure subscription history timestamps are always populated.
UPDATE subscription_history
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE subscription_history
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE subscription_history
    ALTER COLUMN created_at SET NOT NULL;
