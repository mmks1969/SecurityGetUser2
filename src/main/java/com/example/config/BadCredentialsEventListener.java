package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.domain.model.AppUserDetails;
import com.example.service.UserDetailsServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BadCredentialsEventListener {
	
	@Autowired
	private UserDetailsServiceImpl service;
	
	@EventListener
	public void onBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
		
		log.info("BadCredentialsEvent Start");
		
		// 存在しないユーザ名の場合は無視
		if(event.getException().getClass().equals(UsernameNotFoundException.class)) {
			log.info("ユーザーが存在しません。");
			return;
		}
		
		// ユーザーＩＤの取得
		String userId = event.getAuthentication().getName();
		
		// ユーザー情報の取得
		AppUserDetails user = (AppUserDetails)service.loadUserByUsername(userId);
		
		// ログイン失敗回数を１増やす
		int loginMissTime = user.getLoginMissTimes() + 1;
		
		// 失敗委回数を更新する
		service.updateUnlock(userId, loginMissTime);
		log.info("BadCredentialsEvent End");
	}

}
