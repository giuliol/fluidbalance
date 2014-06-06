package giu.pdfWrapper;

import giu.fbalance.BalanceEntry;
import giu.fbalance.Patient;
import giu.fbalance.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class GiulioWriter {

	private static void addMetaData(Document document) {
		document.addTitle("Fluid Balance");
		document.addSubject("Fluid Balance");
		document.addKeywords("Fluid Balance");
		document.addAuthor("Giulio Luzzati");
		document.addCreator("Fluid Balance");
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
	
	
	private static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 50f,
			Font.BOLDITALIC);
	private static Font smallFont = new Font(Font.FontFamily.HELVETICA, 8,
			Font.NORMAL);
	private static Font bigBold = new Font(Font.FontFamily.HELVETICA, 18f,
			Font.BOLDITALIC,BaseColor.DARK_GRAY);
	private static Font italic = new Font(Font.FontFamily.HELVETICA, 12,
			Font.ITALIC,BaseColor.DARK_GRAY);

	private static void addTitlePage(Document document, Patient p)
			throws DocumentException {
		Paragraph preface = new Paragraph();

		Paragraph title=new Paragraph("Fluid Balance", titleFont);

		title.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
		preface.add(title);

		try {
			// get input stream
			InputStream ims = mainContext.getAssets().open("icon.png");
			Bitmap bmp = BitmapFactory.decodeStream(ims);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			Image image = Image.getInstance(stream.toByteArray());
			image.setAbsolutePosition((float)PageSize.A4.getWidth()*0.02f,(float) PageSize.A4.getHeight()*0.84f);
			document.add(image);
		}
		catch(IOException ex)
		{
		}
		
		titleFont.setStyle(Font.ITALIC);
		addEmptyLine(preface, 3);

		String pdata=(resources.getString(R.string.name)+": "+p.getName()+"\n"+resources.getString(R.string.weight)+": "+String.format("%2.1f",p.getWeight())+" kg\n"+resources.getString(R.string.fasting_since)+" "+fast_start_string);
		pdata.concat(" - "+resources.getString(R.string.thirst)+": "+thirst);
		Paragraph patientData=new Paragraph(pdata,italic);

		preface.add(patientData);
		addEmptyLine(preface,2);
		document.add(preface);
	}


	private static final float W1=2;
	private static final float W2=1;
	private static final float W3=1;

	private static final float GRAYFILL=0.85f;

	
	 static class HeaderFooter extends PdfPageEventHelper {
	        /** Alternating phrase for the header. */
	        /** Current page number (will be reset for every chapter). */
	        int pagenumber;
	 
	        /**
	         * Initialize one of the headers.
	         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
	         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
	         */
	        public void onOpenDocument(PdfWriter writer, Document document) {
	        }
	 
	        /**
	         * Initialize one of the headers, based on the chapter title;
	         * reset the page number.
	         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(
	         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document, float,
	         *      com.itextpdf.text.Paragraph)
	         */
	        public void onChapter(PdfWriter writer, Document document,
	                float paragraphPosition, Paragraph title) {
	            pagenumber = 1;
	        }
	 
	        /**
	         * Increase the page number.
	         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
	         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
	         */
	        public void onStartPage(PdfWriter writer, Document document) {
	            pagenumber++;
	        }
	 
	        /**
	         * Adds the header and the footer.
	         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
	         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
	         */
	        public void onEndPage(PdfWriter writer, Document document) {
	            Rectangle rect = writer.getBoxSize("art");

	            int pageCount=document.getPageNumber();
	            ColumnText.showTextAligned(writer.getDirectContent(),
	                    Element.ALIGN_CENTER, new Phrase(String.format("FluidBalance Android app - pg %d/%d", pagenumber,pageCount)),
	                    (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
	        }
	    }
	 
	public static PdfPTable createTable(ArrayList<BalanceEntry> inList, ArrayList<BalanceEntry> outList, Patient p) {
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell;
		Phrase inPhrase=new Phrase("IN",bigBold);
		inPhrase.setFont(bigBold);

		Phrase outPhrase=new Phrase("OUT",bigBold);

		cell = new PdfPCell(inPhrase);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(10f);
		table.addCell(cell);

		cell = new PdfPCell(outPhrase);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(10f);
		table.addCell(cell);

		PdfPTable in = new PdfPTable(3);
		try {
			in.setWidths(new float[]{W1, W2, W3});
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		double inTot, outTot;
		inTot=0;
		outTot=0;

		boolean flip=false;
		for(BalanceEntry b : inList){
			inTot+=b.getAmount();

			if(flip)
				flip=false;
			else
				flip=true;
			cell = new PdfPCell(new Phrase(b.getEntryName(),smallFont));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(PdfPCell.NO_BORDER);
			if(flip)
				cell.setGrayFill(GRAYFILL);
			in.addCell(cell);

			cell = new PdfPCell(new Phrase(String.format("%6.2f",b.getAmount())+" ml",smallFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(PdfPCell.NO_BORDER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			if(flip)
				cell.setGrayFill(GRAYFILL);
			in.addCell(cell);

			cell = new PdfPCell(new Phrase(b.getTimeString(),smallFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(PdfPCell.NO_BORDER);
			if(flip)
				cell.setGrayFill(GRAYFILL);
			in.addCell(cell);
		}

		PdfPTable out = new PdfPTable(3);
		try {
			out.setWidths(new float[]{W1, W2, W3});
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		for(BalanceEntry b : outList){
			outTot+=b.getAmount();
			if(flip)
				flip=false;
			else
				flip=true;

			cell = new PdfPCell(new Phrase(b.getEntryName(),smallFont));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(PdfPCell.NO_BORDER);
			if(flip)
				cell.setGrayFill(GRAYFILL);
			out.addCell(cell);

			cell = new PdfPCell(new Phrase(String.format("%6.2f",b.getAmount())+" ml",smallFont));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			if(flip)
				cell.setGrayFill(GRAYFILL);
			cell.setBorder(PdfPCell.NO_BORDER);
			out.addCell(cell);

			if(!b.isFrozen()){
			cell = new PdfPCell(new Phrase(b.getTimeString(),smallFont));
			}
			else{
				cell = new PdfPCell(new Phrase(b.getTimeString()+"-"+b.getFrozenTimeString(),smallFont));
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(PdfPCell.NO_BORDER);
			if(flip)
				cell.setGrayFill(GRAYFILL);
			out.addCell(cell);
		}

		table.addCell(in);
		table.addCell(out);

		cell=new PdfPCell(new Phrase(resources.getString(R.string.total)+" "+ String.format("%6.2f", inTot)+" ml"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(10);

		table.addCell(cell);
		//       
		//        
		cell=new PdfPCell(new Phrase(resources.getString(R.string.total)+" "+ String.format("%6.2f", outTot)+" ml"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(10);

		table.addCell(cell);

		cell=new PdfPCell(new Phrase(resources.getString(R.string.total)+" "+ String.format("%6.2f", inTot-outTot)+" ml"));
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(10);
		cell.setGrayFill(GRAYFILL);
		table.addCell(cell);

		cell=new PdfPCell(new Phrase(resources.getString(R.string.updated)+" "+p.getLastUpdatedString(),smallFont));
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(10);
		table.addCell(cell);
		return table;
	}


	private static Context mainContext=null;
	private static Resources resources=null;
	private static String fast_start_string=null;
	private static double thirst=0;

	public static String writePdf(ArrayList<BalanceEntry> inList, ArrayList<BalanceEntry> outList, Patient p, Context c){

		mainContext=c;
		resources=mainContext.getResources();
		fast_start_string=outList.get(0).getTimeString();

		thirst=outList.get(0).getAmount();
//		outList.remove(0);
		String path;
		Document document = new Document();
        HeaderFooter event = new HeaderFooter();

		try {
			path=(Environment.getExternalStorageDirectory()+"/"+p.getName()+".pdf");
			PdfWriter writer =PdfWriter.getInstance(document, new FileOutputStream(path));
			writer.setPageEvent(event);
	        writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));
			document.open();
//			addHeader(writer.getDirectContent());
			addMetaData(document);
			addTitlePage(document,p);
			document.add(createTable(inList,outList,p));
			document.close();

			return path;
			
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}

}
