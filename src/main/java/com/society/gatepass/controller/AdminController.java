package com.society.gatepass.controller;

import com.society.gatepass.model.User;
import com.society.gatepass.model.PassLog;
import com.society.gatepass.service.GatePassService;
import com.society.gatepass.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final GatePassService gatePassService;

    public AdminController(UserService userService, GatePassService gatePassService) {
        this.userService = userService;
        this.gatePassService = gatePassService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> residents = userService.getAllResidents();
        List<PassLog> logs = gatePassService.getAllLogs();
        long activePasses = gatePassService.getActivePassCount();
        long checkedInVisitors = gatePassService.getCheckedInCount();

        model.addAttribute("totalResidents", residents.size());
        model.addAttribute("activePassesCount", activePasses);
        model.addAttribute("checkedInCount", checkedInVisitors);
        model.addAttribute("logs", logs);

        return "admin/dashboard";
    }

    @GetMapping("/residents")
    public String residentsPage(Model model) {
        List<User> residents = userService.getAllResidents();
        model.addAttribute("residents", residents);
        
        User newResident = new User();
        newResident.setRole("ROLE_RESIDENT");
        model.addAttribute("newResident", newResident);
        
        return "admin/residents";
    }

    @PostMapping("/residents")
    public String addResident(@Valid @ModelAttribute("newResident") User resident, BindingResult result, Model model) {
        if (result.hasErrors()) {
            List<User> residents = userService.getAllResidents();
            model.addAttribute("residents", residents);
            return "admin/residents";
        }
        
        if (userService.findByUsername(resident.getUsername()).isPresent()) {
            result.rejectValue("username", "error.resident", "Username already exists.");
            List<User> residents = userService.getAllResidents();
            model.addAttribute("residents", residents);
            return "admin/residents";
        }
        
        resident.setRole("ROLE_RESIDENT");
        userService.createUser(resident);
        
        return "redirect:/admin/residents?success=added";
    }

    @GetMapping("/residents/delete/{id}")
    public String deleteResident(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/residents?success=deleted";
    }

    @GetMapping("/logs")
    public String allLogs(Model model) {
        List<PassLog> logs = gatePassService.getAllLogs();
        model.addAttribute("logs", logs);
        return "admin/logs";
    }
}
