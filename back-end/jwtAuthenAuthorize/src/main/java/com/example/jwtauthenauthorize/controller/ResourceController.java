package com.example.jwtauthenauthorize.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
	
	@RequestMapping("/hellostudent")
	public String getUser()
	{
		return "Hello User";
	}
	
	@RequestMapping("/helloadmin")
	public String getAdmin()
	{
		return "Hello Admin";
	}

}
