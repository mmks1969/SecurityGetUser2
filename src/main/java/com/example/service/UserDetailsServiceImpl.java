package com.example.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.domain.repository.LoginUserRepository;

@Component("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private LoginUserRepository repository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		// リポジトリー（DAO)からUserDetailsを取得
		UserDetails user = repository.selectOne(username);
		
		return user;
	}
	
	// パスワードを更新する
	public void updatePasswordDate(String userId, String password) throws ParseException {
		
		// パスワード暗号化
		String encryptPass = encoder.encode(password);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date date = sdf.parse("2099/12/31");
		
		// リポジトリーからパスワードを更新
		repository.updatePassword(userId, encryptPass, date);
	}
	

}


























