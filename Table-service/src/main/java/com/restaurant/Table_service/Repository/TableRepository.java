package com.restaurant.Table_service.Repository;

import com.restaurant.Table_service.Entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Tables, Long> {
	List<Tables> findByStatus(Tables.Status status);
}
