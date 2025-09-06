package com.sdp.chatbot;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ChatClientUIController extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;
	@Wire
	private Button openBot;
	@Wire
	private Button sendBtn;
	@Wire
	private Div chatContainer;
	@Wire
	private Listbox chatList;
	@Wire
	private Textbox userInput;

	
	private transient ZMQ.Socket requester;

	private String[] questions = { "1. What is the minimum balance in savings account?",
			"2. How to apply for a debit card?", "3. What are UPI transaction limits?",
			"4. How to reset internet banking password?", "5. How to block a lost debit card?",
			"6. What is the rate of interest on fixed deposit?", "7. How to check account balance via SMS?",
			"8. How to update mobile number?", "9. How to apply for personal loan?", "10. What is NEFT/RTGS timing?" };

	@SuppressWarnings("resource")
	@Override
	public void doAfterCompose(Div comp) throws Exception {
  ZContext context=new ZContext();
		super.doAfterCompose(comp);

		showQuestions();
		new Thread(() -> {
			ChatbotServer botServer = new ChatbotServer();
			botServer.mainServer();
		}, "ChatBotServer-Thread").start();

		requester = context.createSocket(SocketType.REQ);
		requester.connect("tcp://localhost:5555");
	}

	@Listen("onClick = #sendBtn")
	public void sendMessage() {
		String msg = "   " + userInput.getValue().trim();
		if (!msg.isEmpty()) {
			sendToBot(msg);
			userInput.setValue("");
		}
	}

	private void sendToBot(String message1) {
		String message;
		if (message1 == null || message1.isEmpty())
			return;
		else {
			message = message1.substring(3);
		}

		appendMessage("You", message1);

		try {
			requester.send(message);
			String reply = requester.recvStr();
			appendMessage("Bot", reply);

			if (reply.startsWith("Invalid input")) {
				showQuestions();
			}
		} catch (Exception e) {
			appendMessage("Bot", "⚠️ Error: Chat server not available.");
			e.printStackTrace();
		}
	}

	@Wire
	private Vlayout chatArea;

	private void appendMessage(String sender, String message) {
		Div row = new Div();
		row.setSclass("chat-row " + ("You".equals(sender) ? "right" : "left"));

		Div bubble = new Div();
		if ("You".equals(sender)) {
			bubble.setSclass("bubble bubble-user");
			bubble.appendChild(new Label(message));
		} else if ("Bot".equals(sender)) {
			bubble.setSclass("bubble bubble-bot");
			bubble.appendChild(new Label(message));

			Label footer = new Label("✨ Answered by AI");
			footer.setSclass("chat-footer");
			bubble.appendChild(footer);
		} else {
			bubble.setSclass("bubble bubble-question");
			Label qLabel = new Label(message);
			bubble.appendChild(qLabel);

			bubble.addEventListener(Events.ON_CLICK, evt -> 
				sendToBot(message)
			);

		}

		row.appendChild(bubble);
		chatArea.appendChild(row);

		Clients.scrollIntoView(row);
	}

	private void showQuestions() {
		appendMessage("Bot", "Please select one of the following questions:");
		for (String q : questions) {
			appendMessage("", q);
		}
	}

}