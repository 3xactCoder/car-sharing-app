package com.example.carsharing.service.impl;

import com.example.carsharing.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalScheduledService {
    private final RentalService rentalService;

    // Runs every day at 09:00 AM (zone: Europe/Kyiv)
    @Scheduled(cron = "0 0 9 * * *", zone = "Europe/Kyiv")
    public void scheduleOverdueRentalsNotification() {
        log.info("Executing scheduled task: checking for overdue rentals...");
        rentalService.checkOverdueRentals();
    }
}
