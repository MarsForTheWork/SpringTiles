<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>Apache Tomcat WebSocket Examples: Chat</title>
<style type="text/css">
input#chat {
	width: 410px
}

#console-container {
	width: 400px;
}

#console {
	border: 1px solid #CCCCCC;
	border-right-color: #999999;
	border-bottom-color: #999999;
	height: 170px;
	overflow-y: scroll;
	padding: 5px;
	width: 100%;
}

#console p {
	padding: 0;
	margin: 0;
}
</style>
</head>
<body>
	<div>
		<p>
			<input type="text" placeholder="type and press enter to chat"
				id="chat" />
		</p>
		<div id="console-container">
			<div id="console"></div>
		</div>
	</div>
</body>
<script type="text/javascript">
	/**
	 *  指定要連接的websocket地址
	 *  如果使用https,則使用wss://
	 **/
	var socket = new WebSocket('ws://' + window.location.host
			+ '/SpringTiles/websocket/chat');

	//連接與服務器的連接
	socket.onopen = function() {
		showMsg('Info: WebSocket connection opened.');
		document.getElementById('chat').onkeydown = function(event) {
			if (event.keyCode == 13) {
				sendMsg();
			}
		};
	};
	//斷開與服務器的連接
	socket.onclose = function() {
		document.getElementById('chat').onkeydown = null;
		showMsg('Info: WebSocket closed.');
	};
	//與服務器之間的通信
	socket.onmessage = function(message) {
		showMsg(message.data);
	};
	//顯示消息
	function showMsg(message) {
		var console = document.getElementById('console');
		var p = document.createElement('p');
		p.style.wordWrap = 'break-word';
		p.innerHTML = message;
		console.appendChild(p);
		while (console.childNodes.length > 25) {
			console.removeChild(console.firstChild);
		}
		console.scrollTop = console.scrollHeight;
	}
	//發送消息
	function sendMsg() {
		var message = document.getElementById('chat').value;
		if (message != '') {
			socket.send(message);
			document.getElementById('chat').value = '';
		}
	}
</script>