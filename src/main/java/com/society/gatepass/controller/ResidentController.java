package com.society.gatepass.controller;

import com.society.gatepass.model.GatePass;
import com.society.gatepass.model.User;
import com.society.gatepass.service.GatePassService;
import com.society.gatepass.service.QRCodeService;
import com.society.gatepass.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/resident")
public class ResidentController {

    private final GatePassService gatePassService;
    private final UserService userService;
    private final QRCodeService qrCodeService;

    public ResidentController(GatePassService gatePassService, UserService userService, QRCodeService qrCodeService) {
        this.gatePassService = gatePassService;
        this.userService = userService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User resident = getCurrentUser(authentication);
        List<GatePass> passes = gatePassService.getPassesByResident(resident);
        model.addAttribute("passes", passes);
        model.addAttribute("resident", resident);
        return "resident/dashboard";
    }

    @GetMapping("/create-pass")
    public String createPassForm(Model model) {
        GatePass gatePass = new GatePass();
        gatePass.setValidFrom(LocalDateTime.now());
        gatePass.setValidTo(LocalDateTime.now().plusHours(4));
        model.addAttribute("gatePass", gatePass);
        return "resident/create-pass";
    }

    @PostMapping("/create-pass")
    public String createPass(@Valid @ModelAttribute("gatePass") GatePass gatePass, BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            return "resident/create-pass";
        }
        User resident = getCurrentUser(authentication);
        
        if (gatePass.getValidTo().isBefore(gatePass.getValidFrom())) {
            result.rejectValue("validTo", "error.gatePass", "Expiry time must be after start time.");
            return "resident/create-pass";
        }

        GatePass savedPass = gatePassService.createGatePass(gatePass, resident);
        return "redirect:/resident/view-pass/" + savedPass.getPassToken();
    }

    @GetMapping("/view-pass/{token}")
    public String viewPass(@PathVariable("token") String token, Model model) {
        GatePass pass = gatePassService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pass token: " + token));
        
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(pass.getPassToken(), 250, 250);
        
        model.addAttribute("pass", pass);
        model.addAttribute("qrCode", qrCodeBase64);
        return "resident/view-pass";
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found."));
    }
}
