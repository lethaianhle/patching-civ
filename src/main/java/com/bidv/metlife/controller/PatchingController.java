package com.bidv.metlife.controller;

import com.bidv.metlife.service.PatchingService;
import com.bidv.metlife.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatchingController {

    @Autowired
    private PatchingService patchingService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/patching")
    public Response patching() {
        return patchingService.patching();
    }

}
