package com.society.gatepass.repository;

import com.society.gatepass.model.GatePass;
import com.society.gatepass.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatePassRepository extends JpaRepository<GatePass, Long> {
    Optional<GatePass> findByPassToken(String passToken);
    List<GatePass> findByResidentOrderByCreatedAtDesc(User resident);
    long countByStatus(String status);
}
