package bus.ticketer.objects;

import java.util.Date;

public class Ticket {
	private Date creationDate;
	private Date validationDate;
	private int busID;
	
	public Ticket() {
		
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
	
	
}
