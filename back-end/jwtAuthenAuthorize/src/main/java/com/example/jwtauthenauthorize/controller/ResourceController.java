package com.example.jwtauthenauthorize.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
public class ResourceController {

	@RequestMapping("/hellostudent")
	public String getUser() throws MessagingException {
		return "Hello sahathat";
	}
	
	@RequestMapping("/helloadmin")
	public String getAdmin() throws MessagingException {
		return "Hello Admin";
	}

}
