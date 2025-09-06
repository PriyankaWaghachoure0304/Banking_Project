package com.sdp.digitalsignpdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfPasswordProtector {
	
	

	private PdfPasswordProtector() {
		super();
	}

	public static byte[] protectPdf(byte[] signedPdf, String password) throws IOException, DocumentException  {
		PdfReader reader = new PdfReader(new ByteArrayInputStream(signedPdf));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfStamper stamper = new PdfStamper(reader, baos);

		stamper.setEncryption(password.getBytes(),
				password.getBytes(), 
				PdfWriter.ALLOW_PRINTING, 
				PdfWriter.ENCRYPTION_AES_256 
		);

		stamper.close();
		reader.close();
		return baos.toByteArray();
	}
}
