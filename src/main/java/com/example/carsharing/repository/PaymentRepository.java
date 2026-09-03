package com.example.carsharing.repository;

import com.example.carsharing.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p JOIN p.rental r WHERE r.user.id = :userId")
    List<Payment> findAllByUserId(@Param("userId") Long userId);
}
