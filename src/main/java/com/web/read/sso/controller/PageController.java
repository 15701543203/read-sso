package com.web.read.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class PageController {

	@RequestMapping("/register")
	public String showRegister() {
		System.out.println("register");
		return "register";
	}

	
	@RequestMapping("/showLogin")
	public String showLogin(String redirect, Model model) {
		System.out.println("login" + redirect);
		model.addAttribute("redirect", redirect);
		return "login";
	}

}

