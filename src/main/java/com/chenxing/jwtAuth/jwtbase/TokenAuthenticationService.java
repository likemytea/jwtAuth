package com.chenxing.jwtAuth.jwtbase;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.chenxing.jwtAuth.common.JSONResult;
import com.chenxing.jwtAuth.domain.GrantedAuthorityImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {
	static final long EXPIRATIONTIME = 432_000_000; // 5天
	static final String SECRET = "m@s4w#2Q"; // JWT密码
	static final String TOKEN_PREFIX = "Bearer"; // Token前缀
	static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

	// JWT生成方法
	static void addAuthentication(HttpServletResponse response, Authentication auth) throws JSONException {
		StringBuffer sb = new StringBuffer();

		Object[] authArray = auth.getAuthorities().toArray();
		for (int i = 0; i < authArray.length; i++) {
			GrantedAuthorityImpl au = (GrantedAuthorityImpl) authArray[i];
			sb.append(au.getAuthority());
			if (i != authArray.length - 1) {
				sb.append(",");
			}
		}
		// 生成JWT
		String JWT = Jwts.builder()
				// 保存权限（角色）
				.claim("authorities", sb.toString())
				// 用户名写入标题
				.setSubject(auth.getName())
				// 有效期设置
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				// 签名设置
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		// 将 JWT 写入 body
		try {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().println(JSONResult.fillResultString(0, "", JWT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// JWT验证方法
	static Authentication getAuthentication(HttpServletRequest request) {
		// 从Header中拿到token
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			// 解析 Token
			Claims claims = Jwts.parser()
					// 验签
					.setSigningKey(SECRET)
					// 去掉 Bearer
					.parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();

			// 拿用户名
			String user = claims.getSubject();

			// 得到 权限（角色）
			List<GrantedAuthority> authorities = AuthorityUtils
					.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

			// 返回验证令牌
			return user != null ? new UsernamePasswordAuthenticationToken(user, null, authorities) : null;
		}
		return null;
	}
}
