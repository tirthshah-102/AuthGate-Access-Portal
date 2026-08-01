package com.society.gatepass.controller;

import com.society.gatepass.model.GatePass;
import com.society.gatepass.model.PassLog;
import com.society.gatepass.model.User;
import com.society.gatepass.service.GatePassService;
import com.society.gatepass.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/guard")
public class GuardController {

    private final GatePassService gatePassService;
    private final UserService userService;

    public GuardController(GatePassService gatePassService, UserService userService) {
        this.gatePassService = gatePassService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User guard = getCurrentUser(authentication);
        List<PassLog> logs = gatePassService.getLogsByGuard(guard);
        model.addAttribute("logs", logs);
        model.addAttribute("guard", guard);
        return "guard/dashboard";
    }

    @GetMapping("/verify")
    public String verifyPass(@RequestParam("token") String token, Model model) {
        Optional<GatePass> passOpt = gatePassService.findByToken(token);
        if (passOpt.isEmpty()) {
            model.addAttribute("error", "Invalid or Unknown Pass Code: " + token);
            return "guard/verify";
        }

        GatePass pass = passOpt.get();
        model.addAttribute("pass", pass);

        boolean expired = pass.isExpired();
        boolean notYetActive = pass.isNotYetActive();
        String status = pass.getStatus();

        boolean canCheckIn = "ACTIVE".equals(status) && !expired && !notYetActive;
        boolean canCheckOut = "CHECKED_IN".equals(status);
        boolean alreadyUsed = "CHECKED_OUT".equals(status);

        model.addAttribute("expired", expired);
        model.addAttribute("notYetActive", notYetActive);
        model.addAttribute("canCheckIn", canCheckIn);
        model.addAttribute("canCheckOut", canCheckOut);
        model.addAttribute("alreadyUsed", alreadyUsed);

        return "guard/verify";
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam("token") String token, @RequestParam(value = "notes", required = false) String notes, Authentication authentication) {
        GatePass pass = gatePassService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        User guard = getCurrentUser(authentication);
        
        try {
            gatePassService.checkIn(pass, guard, notes);
        } catch (Exception e) {
            return "redirect:/guard/verify?token=" + token + "&error=" + e.getMessage();
        }
        
        return "redirect:/guard/dashboard?success=checkedin";
    }

    @PostMapping("/check-out")
    public String checkOut(@RequestParam("token") String token, @RequestParam(value = "notes", required = false) String notes, Authentication authentication) {
        GatePass pass = gatePassService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        User guard = getCurrentUser(authentication);

        try {
            gatePassService.checkOut(pass, guard, notes);
        } catch (Exception e) {
            return "redirect:/guard/verify?token=" + token + "&error=" + e.getMessage();
        }

        return "redirect:/guard/dashboard?success=checkedout";
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Current guard user not found."));
    }
}
