package com.society.gatepass.service;

import com.society.gatepass.model.GatePass;
import com.society.gatepass.model.PassLog;
import com.society.gatepass.model.User;
import com.society.gatepass.repository.GatePassRepository;
import com.society.gatepass.repository.PassLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GatePassService {

    private final GatePassRepository gatePassRepository;
    private final PassLogRepository passLogRepository;

    public GatePassService(GatePassRepository gatePassRepository, PassLogRepository passLogRepository) {
        this.gatePassRepository = gatePassRepository;
        this.passLogRepository = passLogRepository;
    }

    public GatePass createGatePass(GatePass pass, User resident) {
        pass.setPassToken(UUID.randomUUID().toString());
        pass.setStatus("ACTIVE");
        pass.setResident(resident);
        pass.setCreatedAt(LocalDateTime.now());
        return gatePassRepository.save(pass);
    }

    public Optional<GatePass> findByToken(String token) {
        return gatePassRepository.findByPassToken(token);
    }

    public List<GatePass> getPassesByResident(User resident) {
        return gatePassRepository.findByResidentOrderByCreatedAtDesc(resident);
    }

    public List<GatePass> getAllPasses() {
        return gatePassRepository.findAll();
    }

    public List<PassLog> getAllLogs() {
        return passLogRepository.findByOrderByEntryTimeDesc();
    }

    public List<PassLog> getLogsByGuard(User guard) {
        return passLogRepository.findByGuardOrderByEntryTimeDesc(guard);
    }

    public void checkIn(GatePass pass, User guard, String notes) {
        if (!"ACTIVE".equals(pass.getStatus())) {
            throw new IllegalStateException("Pass is not active. Status: " + pass.getStatus());
        }
        if (pass.isExpired()) {
            throw new IllegalStateException("Pass has expired.");
        }
        if (pass.isNotYetActive()) {
            throw new IllegalStateException("Pass is not yet active.");
        }

        pass.setStatus("CHECKED_IN");
        gatePassRepository.save(pass);

        PassLog log = new PassLog(pass, LocalDateTime.now(), guard, notes);
        passLogRepository.save(log);
    }

    public void checkOut(GatePass pass, User guard, String notes) {
        if (!"CHECKED_IN".equals(pass.getStatus())) {
            throw new IllegalStateException("Pass is not checked in. Status: " + pass.getStatus());
        }

        pass.setStatus("CHECKED_OUT");
        gatePassRepository.save(pass);

        PassLog log = passLogRepository.findFirstByGatePassAndExitTimeIsNull(pass)
                .orElse(new PassLog(pass, LocalDateTime.now().minusHours(1), guard, "Retroactive entry created at checkout"));
        
        log.setExitTime(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            log.setNotes(log.getNotes() == null ? notes : log.getNotes() + " | " + notes);
        }
        passLogRepository.save(log);
    }

    public long getActivePassCount() {
        return gatePassRepository.countByStatus("ACTIVE");
    }

    public long getCheckedInCount() {
        return gatePassRepository.countByStatus("CHECKED_IN");
    }
}
