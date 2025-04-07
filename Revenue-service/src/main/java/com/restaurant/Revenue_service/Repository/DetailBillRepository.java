package com.restaurant.Revenue_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.Revenue_service.Entity.DetailBill;

@Repository
public interface DetailBillRepository extends JpaRepository<DetailBill, Long> {
	List<DetailBill> findByBillId(Long billId);
}
