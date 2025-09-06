package com.sdp.chatbot;


import java.util.HashMap;
import java.util.Map;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ChatbotServer {
    public void mainServer() {
        Map<String, String> qaMap = new HashMap<>();
        qaMap.put("1", "Minimum balance is ₹0000 for savings account.");
        qaMap.put("2", "You can apply via net banking or nearest branch.");
        qaMap.put("3", "UPI daily limit is ₹1,00,000.");
        qaMap.put("4", "Go to login page → Forgot password → Follow steps.");
        qaMap.put("5", "Call customer care or use mobile app to block card.");
        qaMap.put("6", "FD rate is 6.5% per annum.");
        qaMap.put("7", "Send SMS 'BAL' to 12345 from registered number.");
        qaMap.put("8", "Visit branch with ID proof to update mobile number.");
        qaMap.put("9", "Apply via online portal or visit nearest branch.");
        qaMap.put("10", "NEFT: 8AM-7PM | RTGS: 7AM-6PM (Mon-Sat).");

        
        qaMap.put("What is the minimum balance in savings account?".toLowerCase(),
                qaMap.get("1"));
        qaMap.put("How to apply for a debit card?".toLowerCase(),
                qaMap.get("2"));
        qaMap.put("What are UPI transaction limits?".toLowerCase(),
                qaMap.get("3"));
        qaMap.put("How to reset internet banking password?".toLowerCase(),
                qaMap.get("4"));
        qaMap.put("How to block a lost debit card?".toLowerCase(),
                qaMap.get("5"));
        qaMap.put("What is the rate of interest on fixed deposit?".toLowerCase(),
                qaMap.get("6"));
        qaMap.put("How to check account balance via SMS?".toLowerCase(),
                qaMap.get("7"));
        qaMap.put("How to update mobile number?".toLowerCase(),
                qaMap.get("8"));
        qaMap.put("How to apply for personal loan?".toLowerCase(),
                qaMap.get("9"));
        qaMap.put("What is NEFT/RTGS timing?".toLowerCase(),
                qaMap.get("10"));
        
        
        try (ZContext context = new ZContext();
        		ZMQ.Socket responder = context.createSocket(SocketType.REP);
        		) {
            
            responder.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                String request = responder.recvStr();

                String key = request.trim().toLowerCase();
                String response = qaMap.getOrDefault(key, "Invalid input. Please choose from the 10 questions.");
                responder.send(response);
            }
        }
    }
}