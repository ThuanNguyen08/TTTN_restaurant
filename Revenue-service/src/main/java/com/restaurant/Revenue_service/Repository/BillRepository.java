package com.restaurant.Revenue_service.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.Revenue_service.Entity.Bill;
import com.restaurant.Revenue_service.Entity.Bill.BillStatus;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
	List<Bill> findByUserId(Long userId);

	List<Bill> findByStatus(BillStatus status);

	List<Bill> findByStatusAndPaidAtBetween(BillStatus status, LocalDateTime start, LocalDateTime end);

}
