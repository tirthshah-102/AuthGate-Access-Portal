package com.society.gatepass.repository;

import com.society.gatepass.model.GatePass;
import com.society.gatepass.model.PassLog;
import com.society.gatepass.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassLogRepository extends JpaRepository<PassLog, Long> {
    Optional<PassLog> findFirstByGatePassAndExitTimeIsNull(GatePass gatePass);
    List<PassLog> findByOrderByEntryTimeDesc();
    List<PassLog> findByGuardOrderByEntryTimeDesc(User guard);
}
