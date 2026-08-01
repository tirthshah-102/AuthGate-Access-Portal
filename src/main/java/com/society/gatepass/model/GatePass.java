package com.society.gatepass.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "gate_passes")
public class GatePass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String passToken; // Unique UUID string for QR scanning

    @NotBlank
    @Column(nullable = false)
    private String visitorName;

    @NotBlank
    @Column(nullable = false)
    private String visitorPhone;

    private String visitorVehicleNo; // Optional

    @NotBlank
    @Column(nullable = false)
    private String purpose;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime validFrom;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime validTo;

    @Column(nullable = false)
    private String status; // ACTIVE, CHECKED_IN, CHECKED_OUT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public GatePass() {}

    public GatePass(String passToken, String visitorName, String visitorPhone, String visitorVehicleNo, String purpose, LocalDateTime validFrom, LocalDateTime validTo, String status, User resident) {
        this.passToken = passToken;
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
        this.visitorVehicleNo = visitorVehicleNo;
        this.purpose = purpose;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
        this.resident = resident;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassToken() {
        return passToken;
    }

    public void setPassToken(String passToken) {
        this.passToken = passToken;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }

    public String getVisitorVehicleNo() {
        return visitorVehicleNo;
    }

    public void setVisitorVehicleNo(String visitorVehicleNo) {
        this.visitorVehicleNo = visitorVehicleNo;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getResident() {
        return resident;
    }

    public void setResident(User resident) {
        this.resident = resident;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to check if the pass is currently valid based on time
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(validTo);
    }

    public boolean isNotYetActive() {
        return LocalDateTime.now().isBefore(validFrom);
    }
}
