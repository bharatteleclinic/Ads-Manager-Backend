package com.manager.ads.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.CampaignPayment;
import com.manager.ads.Entity.ConsultationDevice;
import com.manager.ads.Repository.CampaignPaymentRepository;

@Service
public class InvoiceService {

    @Autowired
    private CampaignPaymentRepository paymentRepo;

    public byte[] generateInvoicePDF(Long paymentId) throws Exception {
        CampaignPayment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Campaign campaign = payment.getCampaign();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        // === Colors and Fonts ===
        BaseColor primaryColor = new BaseColor(25, 118, 210);
        BaseColor gray = new BaseColor(245, 245, 245);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);

        // === HEADER ===
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        PdfPCell headerCell = new PdfPCell(new Phrase("BHARAT TELECLINIC - PAYMENT INVOICE",
                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.WHITE)));
        headerCell.setBackgroundColor(primaryColor);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(12);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(headerCell);
        document.add(headerTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Invoice ID: INV-" + paymentId, normalFont));
        document.add(new Paragraph("Generated On: " +
                payment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")), normalFont));
        document.add(new Paragraph(" "));
        document.add(new LineSeparator());
        document.add(new Paragraph(" "));

        // === CAMPAIGN DETAILS ===
        PdfPTable campaignTable = new PdfPTable(2);
        campaignTable.setWidthPercentage(100);
        campaignTable.setSpacingBefore(10f);
        campaignTable.setSpacingAfter(10f);

        campaignTable.addCell(makeCell("Campaign Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Title", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getTitle(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Type", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getType(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Description", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getDescription(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Brand Category", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getBrandCategory(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Ads Type", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(campaign.getAdsType(), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Device Count", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell(String.valueOf(campaign.getDeviceCount()), normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("Total Estimated Price", normalFont, null, 1, Element.ALIGN_LEFT));
        campaignTable.addCell(makeCell("₹ " + campaign.getTotalPrice(), normalFont, null, 1, Element.ALIGN_LEFT));
        document.add(campaignTable);

        // === PAYMENT DETAILS ===
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setSpacingBefore(10f);
        paymentTable.setSpacingAfter(10f);
        paymentTable.addCell(makeCell("Payment Details", sectionFont, gray, 2, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Payment ID", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(payment.getRazorpayPaymentId() != null ? payment.getRazorpayPaymentId() : "N/A",
                normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Order ID", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(payment.getRazorpayOrderId(), normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Status", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(payment.getStatus(), normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell("Amount Paid", normalFont, null, 1, Element.ALIGN_LEFT));
        paymentTable.addCell(makeCell(String.format("₹ %.2f", payment.getAmount()), normalFont, null, 1, Element.ALIGN_LEFT));
        document.add(paymentTable);

        // === DEVICE DETAILS ===
        if (campaign.getSelectedDevices() != null && !campaign.getSelectedDevices().isEmpty()) {
            PdfPTable deviceTable = new PdfPTable(4);
            deviceTable.setWidthPercentage(100);
            deviceTable.setSpacingBefore(10f);
            deviceTable.addCell(makeCell("Device Serial", sectionFont, gray, 1, Element.ALIGN_CENTER));
            deviceTable.addCell(makeCell("Model Number", sectionFont, gray, 1, Element.ALIGN_CENTER));
            deviceTable.addCell(makeCell("Enterprise ID", sectionFont, gray, 1, Element.ALIGN_CENTER));
            deviceTable.addCell(makeCell("Location", sectionFont, gray, 1, Element.ALIGN_CENTER));

            for (ConsultationDevice device : campaign.getSelectedDevices()) {
                deviceTable.addCell(makeCell(device.getDeviceSerial(), normalFont, null, 1, Element.ALIGN_CENTER));
                deviceTable.addCell(makeCell(device.getModelNumber(), normalFont, null, 1, Element.ALIGN_CENTER));
                deviceTable.addCell(makeCell(String.valueOf(device.getEnterpriseId()), normalFont, null, 1, Element.ALIGN_CENTER));
                deviceTable.addCell(makeCell(device.getLocation(), normalFont, null, 1, Element.ALIGN_CENTER));
            }
            document.add(deviceTable);
        }

        // === FOOTER ===
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph(
                "Thank you for advertising with Bharat Teleclinic!\nThis is a computer-generated invoice.",
                smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    // === Helper for cells ===
    private PdfPCell makeCell(String text, Font font, BaseColor bgColor, int colspan, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(align);
        if (bgColor != null)
            cell.setBackgroundColor(bgColor);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        return cell;
    }
}
