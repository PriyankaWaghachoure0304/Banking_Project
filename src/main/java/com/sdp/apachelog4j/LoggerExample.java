package com.sdp.apachelog4j;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class LoggerExample {

		
		private static final Logger logger = (Logger) LogManager.getLogger(LoggerExample.class);
		
		public static  Logger getLogger() {
			
			return logger;
			
		}

		private LoggerExample() {
			super();
		}

		
	
}
