package com.society.gatepass.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pass_logs")
public class PassLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gate_pass_id", nullable = false)
    private GatePass gatePass;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guard_id")
    private User guard;

    private String notes;

    public PassLog() {}

    public PassLog(GatePass gatePass, LocalDateTime entryTime, User guard, String notes) {
        this.gatePass = gatePass;
        this.entryTime = entryTime;
        this.guard = guard;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GatePass getGatePass() {
        return gatePass;
    }

    public void setGatePass(GatePass gatePass) {
        this.gatePass = gatePass;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public User getGuard() {
        return guard;
    }

    public void setGuard(User guard) {
        this.guard = guard;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
