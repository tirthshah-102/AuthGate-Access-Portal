package com.society.gatepass;

import com.society.gatepass.model.GatePass;
import com.society.gatepass.model.User;
import com.society.gatepass.repository.GatePassRepository;
import com.society.gatepass.repository.UserRepository;
import com.society.gatepass.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GatePassRepository gatePassRepository;
    private final UserService userService;

    public DataInitializer(UserRepository userRepository, GatePassRepository gatePassRepository, UserService userService) {
        this.userRepository = userRepository;
        this.gatePassRepository = gatePassRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Seed Users
            User admin = new User("admin", "admin123", "System Administrator", null, "1234567890", "ROLE_ADMIN");
            User guard = new User("guard", "guard123", "Security Gate Guard", null, "0987654321", "ROLE_GUARD");
            User resident1 = new User("resident1", "pass123", "Tirth Resident", "A-101", "9876543210", "ROLE_RESIDENT");
            User resident2 = new User("resident2", "pass123", "Amit Resident", "B-202", "8765432109", "ROLE_RESIDENT");

            userService.createUser(admin);
            userService.createUser(guard);
            userService.createUser(resident1);
            userService.createUser(resident2);

            // Seed Gate Passes
            GatePass activePass = new GatePass();
            activePass.setPassToken("active-pass-token");
            activePass.setVisitorName("Alice Green");
            activePass.setVisitorPhone("9998887776");
            activePass.setVisitorVehicleNo("MH-12-AB-1234");
            activePass.setPurpose("Social Visit");
            activePass.setValidFrom(LocalDateTime.now().minusHours(1));
            activePass.setValidTo(LocalDateTime.now().plusHours(4));
            activePass.setStatus("ACTIVE");
            activePass.setResident(resident1);
            activePass.setCreatedAt(LocalDateTime.now());
            gatePassRepository.save(activePass);

            GatePass expiredPass = new GatePass();
            expiredPass.setPassToken("expired-pass-token");
            expiredPass.setVisitorName("Bob Brown");
            expiredPass.setVisitorPhone("8887776665");
            expiredPass.setVisitorVehicleNo("DL-3C-CD-5678");
            expiredPass.setPurpose("Delivery");
            expiredPass.setValidFrom(LocalDateTime.now().minusHours(6));
            expiredPass.setValidTo(LocalDateTime.now().minusHours(2));
            expiredPass.setStatus("ACTIVE");
            expiredPass.setResident(resident2);
            expiredPass.setCreatedAt(LocalDateTime.now());
            gatePassRepository.save(expiredPass);

            System.out.println("=== Database seeded successfully with Admin, Guard, Residents, and Test Passes ===");
        }
    }
}
