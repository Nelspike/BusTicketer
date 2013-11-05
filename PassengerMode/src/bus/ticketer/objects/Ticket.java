package bus.ticketer.objects;

import java.util.Date;

import android.graphics.Bitmap;

public class Ticket {
	private Date creationDate;
	private Date validationDate;
	private int busID, ticketID;
	private Bitmap QRCode;
	
	public Ticket() {
		
	}
	
	public Ticket(int id) {
		this.setTicketID(id);
	}
	
	public Ticket(Date creationDate) {
		this.setCreationDate(creationDate);
	}

	public Date getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getBusID() {
		return busID;
	}

	public void setBusID(int busID) {
		this.busID = busID;
	}

	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public Bitmap getQRCode() {
		return QRCode;
	}

	public void setQRCode(Bitmap qRCode) {
		QRCode = qRCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.ticketID == ((Ticket) obj).ticketID;
	}
}
