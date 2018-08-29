package com.chenxing.jwtAuth.jwtbase;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.chenxing.jwtAuth.common.JSONResult;
import com.chenxing.jwtAuth.domain.AccountCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	/**
	 * 登录时需要验证时候调用
	 * 
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {

		// JSON反序列化成 AccountCredentials
		AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);

		// 返回一个验证令牌
		return getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
	}

	// 验证成功后调用
	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		try {
			TokenAuthenticationService.addAuthentication(res, auth.getName());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 验证失败后调用，这里直接灌入500错误返回，由于同一JSON返回，HTTP就都返回200了
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.getOutputStream()
					.println(JSONResult.fillResultString(500, "Internal Server Error!!!", JSONObject.NULL));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
