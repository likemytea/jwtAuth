package com.chenxing.jwtAuth.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chenxing.jwtAuth.common.JSONResult;

@RestController
public class UserController {
	// // 根目录映射 Get访问方式 直接返回一个字符串
	// @RequestMapping("/")
	// Map<String, String> hello() {
	// // 返回map会变成JSON key value方式
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("content", "hello freewolf狼~");
	// return map;
	// }

	// 路由映射到/users
	@RequestMapping(value = "/users", produces = "application/json;charset=UTF-8")
	public String usersList() throws JSONException {

		List<String> users = new ArrayList<String>() {
			{
				add("freewolf");
				add("tom");
				add("jerry");
			}
		};

		return JSONResult.fillResultString(0, "", users);
	}

	@RequestMapping(value = "/hello", produces = "application/json;charset=UTF-8")
	public String hello() throws JSONException {
		ArrayList<String> users = new ArrayList<String>() {
			{
				add("hello");
			}
		};
		return JSONResult.fillResultString(0, "", users);
	}

	@RequestMapping(value = "/world", produces = "application/json;charset=UTF-8")
	public String world() throws JSONException {
		ArrayList<String> users = new ArrayList<String>() {
			{
				add("world");
			}
		};
		return JSONResult.fillResultString(0, "", users);
	}

}