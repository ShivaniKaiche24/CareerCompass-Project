package com.careercompass_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

	  @GetMapping("/")
	    public String home() {
	        return "Career Compass Running";
	    }
	  @GetMapping("/ping")
	    public String ping() {
	        System.out.println("PING ENDPOINT HIT");
	        return "pong";
	    }
}
