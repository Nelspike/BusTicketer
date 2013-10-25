package bus.ticketer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import android.os.Environment;

public class PDFWriter {
	
	private String PDFName, ticketType, username;
	private byte[] QRCode;
	private static Font catFont = new Font(Font.FontFamily.HELVETICA, 18,Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
	private Document document;
	private FileOutputStream fileOutStream;
	private boolean edit;
	
	public PDFWriter(String name, String ticket, String username, byte[] QR, boolean edit) {
		PDFName = name;
		ticketType = ticket;
		this.username = username;
		QRCode = QR;
		document = new Document();
		this.edit = edit;
		getFileStream();
		try {
			PdfWriter.getInstance(document, fileOutStream);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void createFile() {
		document.open();
		addMetaData();
		
		try {
			addTicketInfo();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		if(edit){
			try {
				editFile();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		
		document.close();
	}
	
	private void addMetaData() {
		document.addTitle(PDFName);
		document.addSubject("BusTicketer Ticket");
		document.addKeywords("Ticket, BusTicketer");
		document.addAuthor("Bus Ticketer");
		document.addCreator("Bus Ticketer");
	}

	
	private void addTicketInfo() throws DocumentException {
		Paragraph ticketInfo = new Paragraph();

		addEmptyLine(ticketInfo, 1);
		
		ticketInfo.add(new Paragraph(ticketType + " Ticket", catFont));

		addEmptyLine(ticketInfo, 1);

		ticketInfo.add(new Paragraph(
				"Ticket bought by: " + username, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				smallBold));
		addEmptyLine(ticketInfo, 3);

		if(QRCode != null) {
			try {
				ticketInfo.add(Image.getInstance(QRCode));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		document.add(ticketInfo);
	}
	
	public void getFileStream() {
		File directory = getAlbumStorageDir("BusTicketer");
		File file = new File(directory, PDFName);

		fileOutStream = null;
		try {
			fileOutStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private File getAlbumStorageDir(String filename) {
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS),
				filename);
		if (!file.mkdirs()) {
		}
		return file;
	}
	
	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) 
			paragraph.add(new Paragraph(" "));
	}
	
	public void editFile() throws IOException, DocumentException {
		Paragraph ticketInfo = new Paragraph();

		addEmptyLine(ticketInfo, 1);

		ticketInfo.add(new Paragraph(
				"Ticket validated by: " + username + ", on: " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				smallBold));

		document.add(ticketInfo);
	}
}
