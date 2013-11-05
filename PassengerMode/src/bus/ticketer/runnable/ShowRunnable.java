package bus.ticketer.runnable;

import java.util.ArrayList;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import bus.ticketer.listeners.RadioGroupListener;
import bus.ticketer.listeners.ValidationListener;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.BusTimer;
import bus.ticketer.utils.BusUtils;

public class ShowRunnable implements Runnable {

	private BusTimer cTimer;
	private View view;
	private BusTicketer app;
	private Activity context;
	
	public ShowRunnable(BusTicketer app, Activity context, View view) {
		this.app = app;
		this.context = context;
		this.view = view;
	}
	
	@Override
	public void run() {
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.ticket_radio);
		final TextView ticketsText = (TextView) view.findViewById(R.id.show_ticket_amount);
		final TextView timerText = (TextView) view.findViewById(R.id.ticket_timer);
		final SparseArray<ArrayList<Ticket>> tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		final Button validationButton = (Button) view.findViewById(R.id.ticket_validate);

		if(app.isSuccessValidity())
			cTimer = BusUtils.initializeTimer(context, timerText, validationButton, radioGroup);
		
		/*ImageView qr = (ImageView) view.findViewById(R.id.qr_code_holder);
		
		Ticket one = ((BusTicketer) context.getApplicationContext()).getTickets().get(1).get(0);
		
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(one.getTicketID()+"", null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 450);
		Bitmap bitmap = null;
		try {
			bitmap = qrCodeEncoder.encodeAsBitmap();
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		one.setQRCode(bitmap);
		
		qr.setImageBitmap(bitmap);*/
		
		if(((BusTicketer) context.getApplicationContext()).isWaitingValidation()) {
			for (int i = 0; i < radioGroup.getChildCount(); i++)
				radioGroup.getChildAt(i).setEnabled(false);
			timerText.setText("Waiting validation...");
			validationButton.setEnabled(false);
		}
		else if(((BusTicketer) context.getApplicationContext()).isSuccessValidity()) {
			cTimer.start();
			validationButton.setEnabled(false);
			((BusTicketer) context.getApplicationContext()).setTimerOn(true);
		}
		else {
			radioGroup.check(R.id.t1_radio);
			radioGroup.setOnCheckedChangeListener(new RadioGroupListener(context.getApplicationContext(),ticketsText));
			timerText.setText("No ticket Validated");	
			ticketsText.setText(tickets.get(1).size() + " tickets");
			validationButton.setOnClickListener(new ValidationListener(radioGroup, context));
		}		
	}
}
