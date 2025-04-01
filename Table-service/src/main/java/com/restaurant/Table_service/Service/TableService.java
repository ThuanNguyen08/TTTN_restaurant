package com.restaurant.Table_service.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.Table_service.DTO.TableRequest;
import com.restaurant.Table_service.DTO.TableResponse;
import com.restaurant.Table_service.Entity.Tables;
import com.restaurant.Table_service.Exception.InvalidTableStatusException;
import com.restaurant.Table_service.Exception.ResourceNotFoundException;
import com.restaurant.Table_service.Repository.TableRepository;


@Service
public class TableService {
	private final TableRepository tableRepository;

	public TableService(TableRepository tableRepository) {
		this.tableRepository = tableRepository;
	}

	public List<TableResponse> getAllTables() {
		return tableRepository.findAll().stream().map(TableResponse::fromEntity).collect(Collectors.toList());
	}

	public TableResponse getTableById(Long id) {
		Tables table = tableRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));
		return TableResponse.fromEntity(table);
	}

	public List<TableResponse> getTablesByStatus(Tables.Status status) {
		return tableRepository.findByStatus(status).stream().map(TableResponse::fromEntity)
				.collect(Collectors.toList());
	}

	@Transactional
	public TableResponse createTable(TableRequest request) {
		Tables table = new Tables(request.getName(), request.getStatus(), request.getDescription());
		return TableResponse.fromEntity(tableRepository.save(table));
	}

	@Transactional
	public TableResponse updateTable(Long id, TableRequest request) {
		Tables existingTable = tableRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));

		existingTable.setName(request.getName());
		existingTable.setStatus(request.getStatus());
		existingTable.setDescription(request.getDescription());

		return TableResponse.fromEntity(tableRepository.save(existingTable));
	}

	@Transactional
	public TableResponse updateTableStatus(Long id, Tables.Status status) {
		if (status == null) {
	        throw new InvalidTableStatusException("Trạng thái bàn không được để trống");
	    }
		
		Tables existingTable = tableRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));

		existingTable.setStatus(status);
		return TableResponse.fromEntity(tableRepository.save(existingTable));
	}

	@Transactional
	public void deleteTable(Long id) {
		Tables existingTable = tableRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));
		tableRepository.delete(existingTable);
	}
}