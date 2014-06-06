package giu.pdfWrapper;

import giu.fbalance.BalanceEntry;
import giu.fbalance.Patient;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdfjet.A4;
import com.pdfjet.Align;
import com.pdfjet.Border;
import com.pdfjet.Cell;
import com.pdfjet.Color;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.Table;

import android.content.Context;
import android.os.Environment;

public class GiulioPDFjetWriter {

	private static float PAD=3f;
	public static String writePdf(ArrayList<BalanceEntry> inList, ArrayList<BalanceEntry> outList, Patient p, Context c) throws FileNotFoundException, Exception{


		String path=(Environment.getExternalStorageDirectory()+"/"+p.getName()+".pdf");


		PDF pdf = new PDF(
				new BufferedOutputStream(
						new FileOutputStream(path)));

		Font f1 = new Font(pdf, CoreFont.HELVETICA_BOLD);
		Font f2 = new Font(pdf, CoreFont.HELVETICA);
		f1.setSize(7f);
		f2.setSize(7f);

		Page page = new Page(pdf, A4.PORTRAIT);

		Table inTable = new Table();
		Table outTable = new Table();

		// REPLACED:
		// table.setCellMargin(10f);

		List<List<Cell>> tableData = new ArrayList<List<Cell>>();

		List<Cell> row = null;
		Cell cell = null;

		int in_entries=inList.size();
		int out_entries=outList.size();

		
		row=new ArrayList<Cell>();
		cell=new Cell(f1);
		cell.setNoBorders();
		cell.setTopPadding(PAD);
		cell.setBottomPadding(PAD);
		cell.setLeftPadding(PAD);
		cell.setRightPadding(PAD);
		cell.setTextAlignment(Align.CENTER);
		cell.setText("IN");
		cell.setColSpan(3);
		row.add(cell);
		tableData.add(row);
		
		for(int i=0;i<in_entries;i++){
			row=new ArrayList<Cell>();

			cell=new Cell(f1);
			cell.setNoBorders();
			cell.setTopPadding(PAD);
			cell.setBottomPadding(PAD);
			cell.setLeftPadding(PAD);
			cell.setRightPadding(PAD);
			cell.setBorder(Border.LEFT,true);
			cell.setTextAlignment(Align.LEFT);
			cell.setText(inList.get(i).getEntryName());
			if(i==0)
				cell.setBorder(Border.TOP,true);
			else if(i==in_entries-1)
				cell.setBorder(Border.BOTTOM,true);
			row.add(cell);

			cell=new Cell(f1);
			cell.setNoBorders();
			cell.setTopPadding(PAD);
			cell.setBottomPadding(PAD);
			cell.setLeftPadding(PAD);
			cell.setRightPadding(PAD);
			cell.setTextAlignment(Align.CENTER);
			cell.setText(String.format("%6.2f",inList.get(i).getAmount())+" ml");
			if(i==0)
				cell.setBorder(Border.TOP,true);
			else if(i==in_entries-1)
				cell.setBorder(Border.BOTTOM,true);
			row.add(cell);

			cell=new Cell(f1);
			cell.setNoBorders();
			cell.setTopPadding(PAD);
			cell.setBottomPadding(PAD);
			cell.setLeftPadding(PAD);
			cell.setRightPadding(PAD);
			cell.setBorder(Border.RIGHT,true);
			cell.setTextAlignment(Align.RIGHT);
			cell.setText(inList.get(i).getTimeString());
			if(i==0)
				cell.setBorder(Border.TOP,true);
			else if(i==in_entries-1)
				cell.setBorder(Border.BOTTOM,true);
			row.add(cell);

			tableData.add(row);

		}

		inTable.setData(tableData);
		inTable.setCellBordersWidth(0.2f);
		inTable.setLocation(70f, 30f);
		inTable.drawOn(page);

		tableData = new ArrayList<List<Cell>>();

		
		
//		row=new ArrayList<Cell>();
//		cell=new Cell(f1);
//		cell.setNoBorders();
//		cell.setTopPadding(PAD);
//		cell.setBottomPadding(PAD);
//		cell.setLeftPadding(PAD);
//		cell.setRightPadding(PAD);
//		cell.setTextAlignment(Align.CENTER);
//		cell.setText("OUT");
//		cell.setColSpan(3);
//		row.add(cell);
//		tableData.add(row);
		
		for(int i=0;i<out_entries;i++){
			row=new ArrayList<Cell>();

			cell=new Cell(f1);
			cell.setNoBorders();
			cell.setTopPadding(PAD);
			cell.setBottomPadding(PAD);
			cell.setLeftPadding(PAD);
			cell.setRightPadding(PAD);
			cell.setBorder(Border.LEFT,true);
			cell.setTextAlignment(Align.LEFT);
			cell.setText(outList.get(i).getEntryName());
			if(i==0)
				cell.setBorder(Border.TOP,true);
			else if(i==out_entries-1)
				cell.setBorder(Border.BOTTOM,true);
			row.add(cell);

			cell=new Cell(f1);
			cell.setNoBorders();
			cell.setTopPadding(PAD);
			cell.setBottomPadding(PAD);
			cell.setLeftPadding(PAD);
			cell.setRightPadding(PAD);
			cell.setTextAlignment(Align.CENTER);
			cell.setText(String.format("%6.2f",outList.get(i).getAmount())+" ml");
			if(i==0)
				cell.setBorder(Border.TOP,true);
			else if(i==out_entries-1)
				cell.setBorder(Border.BOTTOM,true);
			row.add(cell);

			cell=new Cell(f1);
			cell.setNoBorders();
			cell.setBorder(Border.RIGHT,true);
			cell.setTopPadding(PAD);
			cell.setBottomPadding(PAD);
			cell.setLeftPadding(PAD);
			cell.setRightPadding(PAD);
			cell.setTextAlignment(Align.RIGHT);
			cell.setText(outList.get(i).getTimeString());
			if(i==0)
				cell.setBorder(Border.TOP,true);
			else if(i==out_entries-1)
				cell.setBorder(Border.BOTTOM,true);
			row.add(cell);

			tableData.add(row);

		}

		outTable.setData(tableData);
		outTable.setCellBordersWidth(0.2f);
		outTable.setLocation(page.getWidth()-outTable.getWidth()-70f, 30f);
		outTable.drawOn(page);


		//        for (int i = 0; i < 5; i++) {
		//            row = new ArrayList<Cell>();
		//            for (int j = 0; j < 5; j++) {
		//                if (i == 0) {
		//                    cell = new Cell(f1);
		//                }
		//                else {
		//                    cell = new Cell(f2);
		//                }
		//                cell.setNoBorders();
		//
		//                // WITH:
		//                cell.setTopPadding(10f);
		//                cell.setBottomPadding(10f);
		//                cell.setLeftPadding(10f);
		//                cell.setRightPadding(10f);
		//
		//                cell.setText("Hello " + i + " " + j);
		//                if (i == 0) {
		//                    cell.setBorder(Border.TOP, true);
		//                    cell.setUnderline(true);
		//                    cell.setUnderline(false);
		//                }
		//                if (i == 4) {
		//                    cell.setBorder(Border.BOTTOM, true);
		//                }
		//                if (j == 0) {
		//                    cell.setBorder(Border.LEFT, true);
		//                }
		//                if (j == 4) {
		//                    cell.setBorder(Border.RIGHT, true);
		//                }
		//
		//                if (i == 2 && j == 2) {
		//                    cell.setBorder(Border.TOP, true);
		//                    cell.setBorder(Border.BOTTOM, true);
		//                    cell.setBorder(Border.LEFT, true);
		//                    cell.setBorder(Border.RIGHT, true);
		//
		//                    cell.setColSpan(3);
		//                    cell.setBgColor(Color.darkseagreen);
		//                    cell.setLineWidth(1f);
		//                    cell.setTextAlignment(Align.RIGHT);
		//                }
		//
		//                row.add(cell);
		//            }
		//            tableData.add(row);
		//        }

		//        inTable.setData(tableData);
		//        inTable.setCellBordersWidth(0.2f);
		//        inTable.setLocation(70f, 30f);
		//        inTable.drawOn(page);

		// Must call this method before drawing the table again.
		//        inTable.resetRenderedPagesCount();
		//        inTable.setLocation(70f, 200f);
		//        inTable.drawOn(page);

		pdf.close();

		return path;


	}
}
