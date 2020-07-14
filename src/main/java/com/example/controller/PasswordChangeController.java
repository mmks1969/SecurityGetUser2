package com.example.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.domain.model.AppUserDetails;
import com.example.domain.model.PasswordForm;
import com.example.service.UserDetailsServiceImpl;

@Controller
public class PasswordChangeController {
	
	@Autowired
	UserDetailsServiceImpl service;
	
	// パスワード変更画面の表示
	@GetMapping("/password/change")
	public String getPasswordChange(Model model, @ModelAttribute PasswordForm form) {
		return "password_change";
	}
	
	// パスワード変更
	@PostMapping("/password/change")
	public String postPasswordChange(Model model
			, @ModelAttribute PasswordForm form
			, @AuthenticationPrincipal AppUserDetails user) throws ParseException{
		
		service.updatePasswordDate(user.getUserId(), form.getPassword());
		
		return "home";
		
	}
	

}
