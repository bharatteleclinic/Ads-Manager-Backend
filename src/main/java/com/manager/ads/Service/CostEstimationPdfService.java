// package com.manager.ads.Service;

// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;

// import org.springframework.stereotype.Service;

// import com.google.zxing.BarcodeFormat;
// import com.google.zxing.WriterException;
// import com.google.zxing.client.j2se.MatrixToImageWriter;
// import com.google.zxing.common.BitMatrix;
// import com.google.zxing.qrcode.QRCodeWriter;
// import com.itextpdf.text.Document;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.FontFactory;
// import com.itextpdf.text.Image;
// import com.itextpdf.text.Paragraph;
// import com.itextpdf.text.pdf.PdfWriter;
// import com.manager.ads.Entity.Campaign;
// import com.manager.ads.Entity.CampaignPayment;

// @Service
// public class CostEstimationPdfService {

//     // Updated method: return PDF bytes instead of writing to file
//     public byte[] generateCostEstimation(Campaign campaign, CampaignPayment payment) throws Exception {
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();
//         Document document = new Document();
//         PdfWriter.getInstance(document, baos);
//         document.open();

//         Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
//         Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

//         // Header
//         document.add(new Paragraph("Bharat Teleclinic Ads Manager", headerFont));
//         document.add(new Paragraph("COST ESTIMATION / INVOICE", headerFont));
//         document.add(new Paragraph(" "));

//         // --- Client Info ---
//         document.add(new Paragraph("Client Details", headerFont));
//         document.add(new Paragraph("Name: " + campaign.getUser().getFname() + " " + campaign.getUser().getLname(), normalFont));
//         document.add(new Paragraph("Email: " + campaign.getUser().getEmail(), normalFont));
//         document.add(new Paragraph("Phone: " + campaign.getUser().getNumber(), normalFont));
//         document.add(new Paragraph(" "));

//         // --- Campaign Info ---
//         document.add(new Paragraph("Campaign Details", headerFont));
//         document.add(new Paragraph("Title: " + campaign.getTitle(), normalFont));
//         document.add(new Paragraph("Description: " + campaign.getDescription(), normalFont));
//         document.add(new Paragraph("Ad Type: " + campaign.getAdsType(), normalFont));
//         document.add(new Paragraph("Brand Category: " + campaign.getBrandCategory(), normalFont));
//         document.add(new Paragraph("Objective: " + campaign.getObjective(), normalFont));
//         document.add(new Paragraph("Devices: " + campaign.getDeviceCount(), normalFont));
//         document.add(new Paragraph("Created At: " + campaign.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
//         document.add(new Paragraph(" "));

//         // --- Cost Breakdown ---
//         double base = campaign.getTotalPrice();
//         double gst = base * 0.18;
//         double total = base + gst;

//         document.add(new Paragraph("Cost Breakdown", headerFont));
//         document.add(new Paragraph("Base Cost: ₹" + base));
//         document.add(new Paragraph("GST (18%): ₹" + gst));
//         document.add(new Paragraph("Total Payable: ₹" + total));
//         document.add(new Paragraph(" "));

//         // --- Payment Info ---
//         document.add(new Paragraph("Payment Details", headerFont));
//         document.add(new Paragraph("Razorpay Order ID: " + payment.getRazorpayOrderId()));
//         document.add(new Paragraph("Payment Status: " + payment.getStatus()));
//         document.add(new Paragraph("Amount: ₹" + payment.getAmount()));
//         document.add(new Paragraph("Created At: " + payment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
//         document.add(new Paragraph(" "));

//         // --- QR Code ---
//         document.add(new Paragraph("Scan to Pay", headerFont));
//         Image qrImage = Image.getInstance(generatePaymentQr(payment.getRazorpayOrderId(), total));
//         qrImage.scaleAbsolute(150, 150);
//         document.add(qrImage);

//         // --- Footer ---
//         document.add(new Paragraph(" "));
//         document.add(new Paragraph("Thank you for choosing Bharat Teleclinic Ads Manager!", normalFont));

//         document.close();
//         return baos.toByteArray();  // return PDF as byte array
//     }

//     private byte[] generatePaymentQr(String razorpayOrderId, double amount) throws WriterException, IOException {
//         String paymentLink = "https://rzp.io/i/" + razorpayOrderId; // dynamic Razorpay order pay link
//         QRCodeWriter qrCodeWriter = new QRCodeWriter();
//         BitMatrix bitMatrix = qrCodeWriter.encode(paymentLink, BarcodeFormat.QR_CODE, 200, 200);
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();
//         MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
//         return baos.toByteArray();
//     }
// }


// package com.manager.ads.Service;

// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;

// import org.springframework.stereotype.Service;

// import com.google.zxing.BarcodeFormat;
// import com.google.zxing.WriterException;
// import com.google.zxing.client.j2se.MatrixToImageWriter;
// import com.google.zxing.common.BitMatrix;
// import com.google.zxing.qrcode.QRCodeWriter;
// import com.itextpdf.text.BaseColor;
// import com.itextpdf.text.Document;
// import com.itextpdf.text.Element;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.FontFactory;
// import com.itextpdf.text.Image;
// import com.itextpdf.text.PageSize;
// import com.itextpdf.text.Paragraph;
// import com.itextpdf.text.Phrase;
// import com.itextpdf.text.Rectangle;
// import com.itextpdf.text.pdf.PdfPCell;
// import com.itextpdf.text.pdf.PdfPTable;
// import com.itextpdf.text.pdf.PdfWriter;
// import com.manager.ads.Entity.Campaign;
// import com.manager.ads.Entity.CampaignPayment;

// @Service
// public class CostEstimationPdfService {

//     public byte[] generateCostEstimation(Campaign campaign, CampaignPayment payment) throws Exception {
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();
//         Document document = new Document(PageSize.A4, 36, 36, 50, 50);
//         PdfWriter.getInstance(document, baos);
//         document.open();

//         // === Colors and Fonts ===
//         BaseColor primaryColor = new BaseColor(25, 118, 210); // Deep blue
//         BaseColor gray = new BaseColor(240, 240, 240);
//         Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
//         Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);
//         Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
//         Font smallFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);

//         // === Header Bar ===
//         PdfPTable headerTable = new PdfPTable(1);
//         headerTable.setWidthPercentage(100);
//         PdfPCell headerCell = new PdfPCell(new Phrase("Bharat Teleclinic Ads Manager", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.WHITE)));
//         headerCell.setBackgroundColor(primaryColor);
//         headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//         headerCell.setPadding(10);
//         headerCell.setBorder(Rectangle.NO_BORDER);
//         headerTable.addCell(headerCell);
//         document.add(headerTable);

//         document.add(new Paragraph("COST ESTIMATION / INVOICE", titleFont));
//         document.add(new Paragraph(" "));
        
//         // === Client Info ===
//         PdfPTable clientTable = new PdfPTable(2);
//         clientTable.setWidthPercentage(100);
//         clientTable.setSpacingBefore(10);
//         clientTable.addCell(makeCell("Client Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
//         clientTable.addCell(makeCell("Name:", normalFont, null, 1, Element.ALIGN_LEFT));
//         clientTable.addCell(makeCell(campaign.getUser().getFname() + " " + campaign.getUser().getLname(), normalFont, null, 1, Element.ALIGN_LEFT));
//         clientTable.addCell(makeCell("Email:", normalFont, null, 1, Element.ALIGN_LEFT));
//         clientTable.addCell(makeCell(campaign.getUser().getEmail(), normalFont, null, 1, Element.ALIGN_LEFT));
//         clientTable.addCell(makeCell("Phone:", normalFont, null, 1, Element.ALIGN_LEFT));
//         clientTable.addCell(makeCell(campaign.getUser().getNumber(), normalFont, null, 1, Element.ALIGN_LEFT));
//         document.add(clientTable);

//         document.add(new Paragraph(" "));

//         // === Campaign Info ===
//         PdfPTable campaignTable = new PdfPTable(2);
//         campaignTable.setWidthPercentage(100);
//         campaignTable.setSpacingBefore(10);
//         campaignTable.addCell(makeCell("Campaign Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell("Title:", normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell(campaign.getTitle(), normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell("Description:", normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell(campaign.getDescription(), normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell("Ad Type:", normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell(campaign.getAdsType(), normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell("Brand Category:", normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell(campaign.getBrandCategory(), normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell("Devices:", normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell(String.valueOf(campaign.getDeviceCount()), normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell("Created At:", normalFont, null, 1, Element.ALIGN_LEFT));
//         campaignTable.addCell(makeCell(campaign.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont, null, 1, Element.ALIGN_LEFT));
//         document.add(campaignTable);

//         document.add(new Paragraph(" "));

//         // === Cost Breakdown ===
//         double base = campaign.getTotalPrice() != null ? campaign.getTotalPrice() : 0.0;
//         double gst = base * 0.18;
//         double total = base + gst;

//         PdfPTable costTable = new PdfPTable(2);
//         costTable.setWidthPercentage(100);
//         costTable.addCell(makeCell("Cost Breakdown", sectionFont, gray, 2, Element.ALIGN_LEFT));
//         costTable.addCell(makeCell("Base Cost", normalFont, null, 1, Element.ALIGN_LEFT));
//         costTable.addCell(makeCell("₹" + base, normalFont, null, 1, Element.ALIGN_RIGHT));
//         costTable.addCell(makeCell("GST (18%)", normalFont, null, 1, Element.ALIGN_LEFT));
//         costTable.addCell(makeCell("₹" + gst, normalFont, null, 1, Element.ALIGN_RIGHT));
//         costTable.addCell(makeCell("Total Payable", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, primaryColor), gray, 1, Element.ALIGN_LEFT));
//         costTable.addCell(makeCell("₹" + total, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, primaryColor), gray, 1, Element.ALIGN_RIGHT));
//         document.add(costTable);

//         document.add(new Paragraph(" "));

//         // === Payment Info ===
//         PdfPTable paymentTable = new PdfPTable(2);
//         paymentTable.setWidthPercentage(100);
//         paymentTable.addCell(makeCell("Payment Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell("Razorpay Order ID:", normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell(payment.getRazorpayOrderId(), normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell("Status:", normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell(payment.getStatus(), normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell("Amount:", normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell("₹" + payment.getAmount(), normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell("Created At:", normalFont, null, 1, Element.ALIGN_LEFT));
//         paymentTable.addCell(makeCell(payment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont, null, 1, Element.ALIGN_LEFT));
//         document.add(paymentTable);

//         document.add(new Paragraph(" "));

//         // === QR Code ===
//         document.add(new Paragraph("Scan to Pay", sectionFont));
//         Image qrImage = Image.getInstance(generatePaymentQr(payment.getRazorpayOrderId(), total));
//         qrImage.scaleAbsolute(120, 120);
//         qrImage.setAlignment(Element.ALIGN_CENTER);
//         document.add(qrImage);

//         document.add(new Paragraph(" "));
//         document.add(new Paragraph("Thank you for choosing Bharat Teleclinic Ads Manager!", smallFont));
//         document.add(new Paragraph("Generated on: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), smallFont));

//         document.close();
//         return baos.toByteArray();
//     }

//     private PdfPCell makeCell(String text, Font font, BaseColor bgColor, int colspan, int align) {
//         PdfPCell cell = new PdfPCell(new Phrase(text, font));
//         cell.setPadding(6);
//         cell.setColspan(colspan);
//         cell.setHorizontalAlignment(align);
//         if (bgColor != null) cell.setBackgroundColor(bgColor);
//         cell.setBorderColor(new BaseColor(220, 220, 220));
//         return cell;
//     }

//     private byte[] generatePaymentQr(String razorpayOrderId, double amount) throws WriterException, IOException {
//         String paymentLink = "https://rzp.io/i/" + razorpayOrderId;
//         QRCodeWriter qrCodeWriter = new QRCodeWriter();
//         BitMatrix bitMatrix = qrCodeWriter.encode(paymentLink, BarcodeFormat.QR_CODE, 200, 200);
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();
//         MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
//         return baos.toByteArray();
//     }
// }


package com.manager.ads.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.CampaignPayment;

@Service
public class CostEstimationPdfService {

    public byte[] generateCostEstimation(Campaign campaign, CampaignPayment payment) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        PdfWriter.getInstance(document, baos);
        document.open();

        // === Colors and Fonts ===
        BaseColor primaryColor = new BaseColor(25, 118, 210); // Deep blue
        BaseColor gray = new BaseColor(245, 245, 245);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);

        // === Header Bar ===
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        PdfPCell headerCell = new PdfPCell(new Phrase("Bharat Teleclinic Ads Manager",
                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.WHITE)));
        headerCell.setBackgroundColor(primaryColor);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(12);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(headerCell);
        document.add(headerTable);

        document.add(new Paragraph("COST ESTIMATION / INVOICE", titleFont));
        document.add(new Paragraph(" "));

        // === Client Info ===
        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.setSpacingBefore(10);
        clientTable.addCell(makeCell("Client Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
        clientTable.addCell(makeCell("Name:", normalFont, null, 1, Element.ALIGN_LEFT));
        clientTable.addCell(makeCell(campaign.getUser().getFname() + " " + campaign.getUser().getLname(), normalFont, null, 1, Element.ALIGN_LEFT));
        clientTable.addCell(makeCell("Email:", normalFont, null, 1, Element.ALIGN_LEFT));
        clientTable.addCell(makeCell(campaign.getUser().getEmail(), normalFont, null, 1, Element.ALIGN_LEFT));
        clientTable.addCell(makeCell("Phone:", normalFont, null, 1, Element.ALIGN_LEFT));
        clientTable.addCell(makeCell(campaign.getUser().getNumber(), normalFont, null, 1, Element.ALIGN_LEFT));
        document.add(clientTable);

        document.add(new Paragraph(" "));

        // === Campaign Info ===
        PdfPTable campaignTable = new PdfPTable(2);
        campaignTable.setWidthPercentage(100);
        campaignTable.setSpacingBefore(10);
        campaignTable.addCell(makeCell("Campaign Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Title:", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getTitle(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Description:", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getDescription(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Ad Type:", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getAdsType(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Brand Category:", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getBrandCategory(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Devices:", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(String.valueOf(campaign.getDeviceCount()), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Created At:", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont, null, 1, Element.ALIGN_LEFT));
        document.add(campaignTable);

        document.add(new Paragraph(" "));

        // === Cost Breakdown ===
        double base = campaign.getTotalPrice() != null ? campaign.getTotalPrice() : 0.0;
        double gst = base * 0.18;
        double total = base + gst;

        PdfPTable costTable = new PdfPTable(2);
        costTable.setWidthPercentage(100);
        costTable.addCell(makeCell("Cost Breakdown", sectionFont, gray, 2, Element.ALIGN_LEFT));
        costTable.addCell(makeCell("Base Cost", normalFont, null, 1, Element.ALIGN_LEFT));
        costTable.addCell(makeCell("₹" + base, normalFont, null, 1, Element.ALIGN_RIGHT));
        costTable.addCell(makeCell("GST (18%)", normalFont, null, 1, Element.ALIGN_LEFT));
        costTable.addCell(makeCell("₹" + gst, normalFont, null, 1, Element.ALIGN_RIGHT));
        costTable.addCell(makeCell("Total Payable", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, primaryColor), gray, 1, Element.ALIGN_LEFT));
        costTable.addCell(makeCell("₹" + total, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, primaryColor), gray, 1, Element.ALIGN_RIGHT));
        document.add(costTable);

        document.add(new Paragraph(" "));

        // === Payment Info ===
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.addCell(makeCell("Payment Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Razorpay Order ID:", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(payment.getRazorpayOrderId(), normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Status:", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(payment.getStatus(), normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Amount:", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("₹" + payment.getAmount(), normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Created At:", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(payment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont, null, 1, Element.ALIGN_LEFT));
        document.add(paymentTable);

        document.add(new Paragraph(" "));

        // === QR Code Section ===
        PdfPTable qrCard = new PdfPTable(1);
        qrCard.setWidthPercentage(60);
        qrCard.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCard.setSpacingBefore(20);
        qrCard.setSpacingAfter(15);

        PdfPCell qrHeader = new PdfPCell(new Phrase("Scan to Pay", sectionFont));
        qrHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrHeader.setPaddingBottom(8);
        qrHeader.setBorder(Rectangle.BOTTOM);
        qrHeader.setBorderColor(primaryColor);
        qrCard.addCell(qrHeader);

        Image qrImage = Image.getInstance(generatePaymentQr(payment.getRazorpayOrderId(), total));
        qrImage.scaleAbsolute(130, 130);
        PdfPCell qrCell = new PdfPCell(qrImage, true);
        qrCell.setBorder(Rectangle.BOX);
        qrCell.setBorderColor(new BaseColor(220, 220, 220));
        qrCell.setPadding(10);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCard.addCell(qrCell);

        document.add(qrCard);

        // === Footer ===
        document.add(new Paragraph("Thank you for choosing Bharat Teleclinic Ads Manager!", smallFont));
        document.add(new Paragraph("Generated on: " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), smallFont));

        document.close();
        return baos.toByteArray();
    }

    private PdfPCell makeCell(String text, Font font, BaseColor bgColor, int colspan, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(align);
        if (bgColor != null) cell.setBackgroundColor(bgColor);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        return cell;
    }

    private byte[] generatePaymentQr(String razorpayOrderId, double amount) throws WriterException, IOException {
        String paymentLink = "https://rzp.io/i/" + razorpayOrderId;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(paymentLink, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }
}


