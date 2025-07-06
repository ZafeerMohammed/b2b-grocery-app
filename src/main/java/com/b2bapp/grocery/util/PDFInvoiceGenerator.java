//package com.b2bapp.grocery.util;
//
//import com.b2bapp.grocery.model.Order;
//import com.b2bapp.grocery.model.OrderItem;
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;
//
//import java.io.ByteArrayOutputStream;
//import java.io.OutputStream;
//import java.time.format.DateTimeFormatter;
//
//public class PDFInvoiceGenerator {
//
//    public static byte[] generateInvoicePdf(Order order) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            Document document = new Document();
//            PdfWriter.getInstance(document, baos);
//
//            document.open();
//
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
//            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
//
//            document.add(new Paragraph("Invoice", titleFont));
//            document.add(new Paragraph("Order ID: " + order.getId(), bodyFont));
//            document.add(new Paragraph("Order Date: " +
//                    order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")), bodyFont));
//            document.add(new Paragraph("Retailer: " + order.getRetailer().getName() +
//                    " (" + order.getRetailer().getEmail() + ")", bodyFont));
//            document.add(Chunk.NEWLINE);
//
//            PdfPTable table = new PdfPTable(4);
//            table.setWidthPercentage(100);
//            table.addCell("Product");
//            table.addCell("Quantity");
//            table.addCell("Unit Price");
//            table.addCell("Subtotal");
//
//            for (OrderItem item : order.getItems()) {
//                table.addCell(item.getProduct().getName());
//                table.addCell(String.valueOf(item.getQuantity()));
//                table.addCell("₹" + item.getPriceAtPurchase());
//                double subtotal = item.getQuantity() * item.getPriceAtPurchase();
//                table.addCell("₹" + subtotal);
//            }
//
//            document.add(table);
//            document.add(Chunk.NEWLINE);
//
//            Paragraph total = new Paragraph("Total Amount: ₹" + order.getTotalAmount(), bodyFont);
//            total.setAlignment(Element.ALIGN_RIGHT);
//            document.add(total);
//
//            document.close();
//            return baos.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate invoice PDF", e);
//        }
//    }
//}


//  OPTION 2
package com.b2bapp.grocery.util;

import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class PDFInvoiceGenerator {

    public static byte[] generateInvoicePdf(Order order) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // Company Header
            Paragraph company = new Paragraph("B2B Grocery Solutions", titleFont);
            company.setAlignment(Element.ALIGN_CENTER);
            document.add(company);

            Paragraph companyDetails = new Paragraph("GSTIN: 29ABCDE1234F2Z5 | support@groceryapp.com", bodyFont);
            companyDetails.setAlignment(Element.ALIGN_CENTER);
            document.add(companyDetails);
            document.add(Chunk.NEWLINE);

            // Invoice title and details
            document.add(new Paragraph("INVOICE", titleFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Invoice #: INV-" + order.getId().toString().substring(0, 8).toUpperCase(), bodyFont));
            document.add(new Paragraph("Order ID: " + order.getId(), bodyFont));
            document.add(new Paragraph("Order Date: " +
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")), bodyFont));
            document.add(new Paragraph("Retailer: " + order.getRetailer().getName() +
                    " (" + order.getRetailer().getEmail() + ")", bodyFont));
            document.add(Chunk.NEWLINE);

            // Table setup
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{3, 1, 2, 2});

            // Table headers
            Stream.of("Product", "Quantity", "Unit Price", "Subtotal").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(columnTitle, headerFont));
                header.setBackgroundColor(Color.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(5);
                table.addCell(header);
            });

            // Table rows
            for (OrderItem item : order.getItems()) {
                table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(), bodyFont)));

                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), bodyFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);

                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("₹%.2f", item.getPriceAtPurchase()), bodyFont));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);

                double subtotal = item.getQuantity() * item.getPriceAtPurchase();
                PdfPCell subtotalCell = new PdfPCell(new Phrase(String.format("₹%.2f", subtotal), bodyFont));
                subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(subtotalCell);
            }

            document.add(table);

            // Total amount
            Paragraph total = new Paragraph("Total Amount: ₹" + String.format("%.2f", order.getTotalAmount()), bodyFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(10);
            document.add(total);

            document.add(Chunk.NEWLINE);

            // Footer message
            Paragraph thankYou = new Paragraph("Thank you for shopping with us!", bodyFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            thankYou.setSpacingBefore(20);
            document.add(thankYou);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
}
