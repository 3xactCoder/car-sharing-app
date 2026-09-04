package com.example.carsharing.repository;

import com.example.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE "
            + "(:userId IS NULL OR r.user.id = :userId) AND "
            + "(:isActive IS NULL OR "
            + "(:isActive = true AND r.actualReturnDate IS NULL) OR "
            + "(:isActive = false AND r.actualReturnDate IS NOT NULL))")
    List<Rental> findByUserIdAndIsActive(
            @Param("userId") Long userId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    List<Rental> findAllByUserIdAndActualReturnDateIsNull(Long userId, Pageable pageable);

    List<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Long userId, Pageable pageable);

    List<Rental> findAllByUserId(Long userId, Pageable pageable);

    List<Rental> findAllByActualReturnDateIsNull(Pageable pageable);

    List<Rental> findAllByActualReturnDateIsNotNull(Pageable pageable);

    @Query("SELECT r FROM Rental r JOIN FETCH r.car JOIN FETCH r.user "
            + "WHERE r.actualReturnDate IS NULL AND r.returnDate <= :currentDate")
    List<Rental> findOverdueRentals(@Param("currentDate") LocalDate currentDate);
}
