package com.sdp.serialization;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.sdp.model.Notification;

public class Serialize {


	
	public void saveMessage(List<Notification> notifications) {
		
		
		
		 try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("notification5.txt"))) {
	           
			 	
	            oos.writeObject(notifications);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
}
