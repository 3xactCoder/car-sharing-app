package com.example.carsharing.repository;

import com.example.carsharing.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p JOIN p.rental r WHERE r.user.id = :userId")
    Page<Payment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Payment p "
            + "WHERE p.rental.user.id = :userId AND p.status = 'PENDING'")
    boolean existsPendingPaymentByUserId(@Param("userId") Long userId);
}
