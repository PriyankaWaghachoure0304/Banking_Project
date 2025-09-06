package com.sdp.serialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.sdp.model.Notification;

public class Deserialize {
	
	

	@SuppressWarnings("unchecked")
	public List<Notification> loadMessage() {
		
		 try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("notification5.txt"))) {
			 
			 return (List<Notification>) ois.readObject();
	        } catch (IOException | ClassNotFoundException e) {
	            e.printStackTrace();
	        }
		 return new ArrayList<>();
	}
}
