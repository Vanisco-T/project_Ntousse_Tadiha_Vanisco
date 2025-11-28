package org.formation.projet_ntousse_tadiha_vanisco.controllers;


import org.formation.projet_ntousse_tadiha_vanisco.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public Map<String, Object> faireAudit() {
        return auditService.faireAudit();
    }
}
