package com.hz.dxf.controller;

import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;

import com.google.gson.GsonBuilder;
import com.hz.dxf.entity.Message;
import com.hz.dxf.entity.User;
import com.hz.dxf.tool.CookieUtil;
import com.hz.dxf.websocket.MyWebSocketHandler;

/***
 * 大屏终端
 * 
 * @author aly
 *
 */
@Controller
@RequestMapping("/terminal")
public class DxfController {

	@Resource
	MyWebSocketHandler handler;

	Map<Long, User> users = new HashMap<Long, User>();

	// 模拟一些数据
	@ModelAttribute
	public void setReqAndRes() {
		User u1 = new User();
		u1.setId(1L);
		u1.setName("张三");
		users.put(u1.getId(), u1);

		User u2 = new User();
		u2.setId(2L);
		u2.setName("李四");
		users.put(u2.getId(), u2);
	}

	// 用户登录
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ModelAndView doLogin(User user, HttpServletRequest request) {
		System.out.println("user.getId()===" + user.getId());
		request.getSession().setAttribute("uid", user.getId());
		request.getSession().setAttribute("name",
				users.get(user.getId()).getName());
		return new ModelAndView("redirect:screen");
	}

	// 大屏页面
	@RequestMapping(value = "screen", method = RequestMethod.GET)
	public ModelAndView doScreen(HttpServletRequest request) {
		CookieUtil cookie = new CookieUtil(request, null, -1);
		String clientID = cookie.getCookieValue("CLIENTID");
		System.out.println("************CLIENTID*******" + clientID);
		request.getSession().setAttribute("cid", clientID);
		request.getSession().setAttribute("name", "");
		return new ModelAndView("screen");
	}

	// 跳转到发布广播页面
	@RequestMapping(value = "broadcast", method = RequestMethod.GET)
	public ModelAndView broadcast() {
		return new ModelAndView("broadcast");
	}

	// 发布系统广播（群发）
	@ResponseBody
	@RequestMapping(value = "broadcast", method = RequestMethod.POST)
	public void broadcast(String text) throws IOException {
		Message msg = new Message();
		msg.setDate(new Date());
		msg.setFrom(-1L);
		msg.setFromName("系统广播");
		msg.setTo(0L);
		msg.setText(text);
		handler.broadcast(new TextMessage(new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
	}

}