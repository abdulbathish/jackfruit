package io.mosip.iiitb.ondemandtemplate.jackfruit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OnDemandTemplateExtractionController {
    // @Autowired
    // private OnDemandTemplateSetupService onDemandTemplateSetupService;

    @GetMapping("/ondemand")
    public ResponseEntity<String> ondemandTemplateExtraction(@RequestParam String id) {
        return ResponseEntity.ok(id);
    }
}
