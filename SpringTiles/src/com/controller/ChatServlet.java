package com.controller;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.annotation.WebServlet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/chat") // 指定客戶端連接地址
public class ChatServlet {
	private static final long serialVersionUID = 1L;
	private static final String GUEST_PREFIX = "Guest";
	private static final AtomicInteger connectionIds = new AtomicInteger(0);
	private static final Set<ChatServlet> connections = new CopyOnWriteArraySet<ChatServlet>();
	private final String nickname;
	private Session session;

	public ChatServlet() {
		nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
	}

	@OnOpen
	public void start(Session session) {
		this.session = session;
		connections.add(this);
		String message = String.format("* %s %s", nickname, "has joined.");
		broadcast(message); // 廣播用戶加入消息
	}

	@OnClose
	public void end() {
		connections.remove(this);
		String message = String.format("* %s %s", nickname, "has disconnected.");
		broadcast(message); // 廣播用戶推出消息
	}

	@OnMessage
	public void incoming(String message) {
		// Never trust the client
		String filteredMessage = String.format("%s: %s", nickname, message.toString());
		broadcast(filteredMessage); // 廣播發送內容
	}

	@OnError
	public void onError(Throwable t) throws Throwable {
		t.printStackTrace();
	}

	private static void broadcast(String msg) {
		for (ChatServlet client : connections) {
			try {
				synchronized (client) {
					client.session.getBasicRemote().sendText(msg);// 給每個人發送消息
				}
			} catch (IOException e) {
				connections.remove(client);
				try {
					client.session.close();
				} catch (IOException e1) {
					// Ignore
				}
				String message = String.format("* %s %s", client.nickname, "has been disconnected.");
				broadcast(message);
			}
		}
	}
}