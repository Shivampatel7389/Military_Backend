package com.military.mams.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BaseRequest {

    @NotBlank(message = "Base name is required")
    private String name;

    @NotBlank(message = "Base code is required")
    private String code;

    @NotBlank(message = "Location is required")
    private String location;

    private String status = "ACTIVE";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
