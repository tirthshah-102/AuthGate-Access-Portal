package com.society.gatepass.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (roles.contains("ROLE_GUARD")) {
                return "redirect:/guard/dashboard";
            } else if (roles.contains("ROLE_RESIDENT")) {
                return "redirect:/resident/dashboard";
            }
        }
        return "redirect:/login";
    }
}
