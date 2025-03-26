package com.restaurant.Table_service.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tables")
public class Tables {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	public enum Status {
		AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
	}

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Tables() {
	}
	
	public Tables(String name, Status status, String description) {
	    this.name = name;
	    this.status = status;
	    this.description = description;
	}


	public Tables(Long id, String name, Status status, String description, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.description = description;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
