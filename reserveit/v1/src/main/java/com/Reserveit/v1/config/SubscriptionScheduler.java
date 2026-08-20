package com.Reserveit.v1.config;

import com.Reserveit.v1.service.ClinicSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final ClinicSubscriptionService subscriptionService;

    /** Runs hourly so expiration is handled even if nobody opens the dashboard. */
    @Scheduled(cron = "${app.subscription.expiration-cron:0 0 * * * *}")
    public void processSubscriptionLifecycle() {
        try {
            subscriptionService.expireSubscriptions();
            subscriptionService.markExpiringSubscriptions();
        } catch (Exception ex) {
            log.error("Subscription lifecycle check failed.", ex);
        }
    }
}
