package com.example.Visa.Dto;

public class UpdateDto {

	private String status;

	public UpdateDto(String status) {
		super();
		this.status = status;
	}

	UpdateDto() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
