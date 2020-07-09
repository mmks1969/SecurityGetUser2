package com.example.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	// パスワードエンコーダーのBean定義
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// データソース
	@Autowired
	private DataSource dataSource;
	
	// ユーザーIDとパスワードを取得するSQL分
	private static final String USER_SQL = "SELECT user_id,password,enabled FROM m_user WHERE user_id = ?";
	
	// ユーザーのロールを取得するSQL分
	private static final String ROLE_SQL = "SELECT "
										+ "U.user_id, R.role_name "
										+ "FROM m_user U INNER JOIN t_user_role UR ON U.user_id = UR.user_id "
										+ "		INNER JOIN m_role R ON UR.role_id = R.role_id "
										+ "WHERE U.user_id = ?;";
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 直リンクの禁止＆ログイン不要ページの設定
		http
			.authorizeRequests()
				.antMatchers("/login").permitAll()		// アクセス許可
				.anyRequest().authenticated();			// それ以外は直リンク禁止
		
		// ログイン処理の実装
		http
			.formLogin()
				.loginProcessingUrl("/login")	// ログイン処理のパス
				.loginPage("/login")	// ログインページの指定
				.failureUrl("/login")	// ログイン失敗時の遷移先
				.usernameParameter("userId")	// ログインページのユーザーID
				.passwordParameter("password")	// ログインページのパスワード
				.defaultSuccessUrl("/home", true);	// ログイン成功時の遷移先
				
		
		// ログアウト処理
		http
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login");
				
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		// ログイン処理時のユーザー情報を、DBから取得する
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(USER_SQL)
			.authoritiesByUsernameQuery(ROLE_SQL)
			.passwordEncoder(passwordEncoder());
	}
	
}
