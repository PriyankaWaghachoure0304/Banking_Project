package com.sdp.pdfclass;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import com.sdp.model.TransferPojo;
import com.sdp.model.User;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PdfGenerator {
	
	
	private PdfGenerator() {
		super();
	}

	public static byte[] generateTransactionHistoryPdf(List<TransferPojo> list) throws FileNotFoundException, JRException  {
		InputStream reportStream = Thread.currentThread()
		        .getContextClassLoader()
		        .getResourceAsStream("reports/transaction_history.jrxml");

        if (reportStream == null) {
            throw new FileNotFoundException("JRXML file not found in reports folder");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        User user = (User) Sessions.getCurrent().getAttribute("userDetails");
        String imgPath= Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Images/1000064228.png");
        
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fullname", user.getFullName());
        parameters.put("logoPath",imgPath );
        
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

		return out.toByteArray();
	}
}