package com.military.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenditures", indexes = {
    @Index(name = "idx_expenditures_base_date", columnList = "base_id, expenditure_date"),
    @Index(name = "idx_expenditures_equipment", columnList = "equipment_id")
})
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expenditure_code", nullable = false, unique = true, length = 60)
    private String expenditureCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "expenditure_date", nullable = false)
    private LocalDate expenditureDate;

    @Column(nullable = false, length = 120)
    private String reason;

    @Column(length = 255)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authorized_by_user_id")
    private User authorizedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Expenditure() {}

    public Expenditure(String expenditureCode, Base base, Equipment equipment, Integer quantity,
                       LocalDate expenditureDate, String reason, String remarks, User authorizedBy) {
        this.expenditureCode = expenditureCode;
        this.base = base;
        this.equipment = equipment;
        this.quantity = quantity;
        this.expenditureDate = expenditureDate;
        this.reason = reason;
        this.remarks = remarks;
        this.authorizedBy = authorizedBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpenditureCode() {
        return expenditureCode;
    }

    public void setExpenditureCode(String expenditureCode) {
        this.expenditureCode = expenditureCode;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpenditureDate() {
        return expenditureDate;
    }

    public void setExpenditureDate(LocalDate expenditureDate) {
        this.expenditureDate = expenditureDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(User authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
