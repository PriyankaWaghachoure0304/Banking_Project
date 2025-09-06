
package com.sdp.digitalsignpdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.zkoss.zk.ui.Executions;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

public class DigitalSignetureWithPdf {

    public static byte[] signPdf(byte[] pdfData) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        String keystorePath= Executions.getCurrent().getDesktop().getWebApp().getRealPath("/WEB-INF/resource/sdp.keystore");
        
        String keystorePassword = "123456";
        String alias = "sdp";

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, keystorePassword.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader tempReader = new PdfReader(new ByteArrayInputStream(pdfData));
        int totalPages = tempReader.getNumberOfPages();
        tempReader.close();

        byte[] currentPdf = pdfData;

        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {

            PdfReader reader = new PdfReader(new ByteArrayInputStream(currentPdf));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfStamper stamper = PdfStamper.createSignature(reader, baos, '\0', null, true);
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setReason("Document Digitally Signed by SDP Horizon");
            appearance.setLocation("India");
            
            String imgPath= Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Images/1000064228.png");
            appearance.setSignatureGraphic(Image.getInstance(imgPath));

            Rectangle rect = new Rectangle(20, 20, 180, 70); // Adjust position and size
            appearance.setVisibleSignature(rect, pageNo, "sig_" + pageNo);
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);

            ExternalDigest digest = new BouncyCastleDigest();
            ExternalSignature signature = new PrivateKeySignature(privateKey, "SHA256", "BC");
            MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0,
                    MakeSignature.CryptoStandard.CMS);

            stamper.close();
            reader.close();

            currentPdf = baos.toByteArray();
        }

        return currentPdf;
    }
}
