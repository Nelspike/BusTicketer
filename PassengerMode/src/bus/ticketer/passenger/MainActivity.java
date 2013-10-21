package bus.ticketer.passenger;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.*;
import android.widget.*;

import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.utils.*;
import bus.ticketer.utils.SpinnerAdapter;

public class MainActivity extends Activity {

	private String password = "";
	private String toFile = "";
	private FileHandler fHandler = new FileHandler("client.txt", toFile);
	private RESTFunction currentFunction;

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch(currentFunction) {
				case CREATE_CLIENT:
					handleCreation(msg);
					break;
				case LOGIN_CLIENT:
					handleLogin(msg);
					break;
				default:
					break;
			}
		}
	};
	
    @SuppressLint("InlinedApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_NoActionBar);
        setContentView(R.layout.activity_main);
        
        handleInitialization();
        
        TextView text = (TextView) findViewById(R.id.terms_textbox);
        text.setMovementMethod(new ScrollingMovementMethod());
        
        String[] cardTypes = { "Visa", "Visa Electron", "Master Card", "Meastro",
        						"American Express", "Multibanco" };
        
        int[] images = { R.drawable.ico_home_visa, R.drawable.ico_home_visa_electron, R.drawable.ico_home_master_card, R.drawable.ico_home_maestro,
        					R.drawable.ico_home_american_express, R.drawable.ico_home_multibanco};
        
        final Spinner spinner = (Spinner) findViewById(R.id.cc_type_spinner);
        spinner.setAdapter(new SpinnerAdapter(MainActivity.this, R.layout.spinner_cc_choice_box, cardTypes, images));
        
        Button registerButton = (Button) findViewById(R.id.splash_register_button);
        registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
				public void onClick(View v) {
					registerAction(spinner);
				}
		});
    }
    
    private void registerAction(Spinner cardSpinner) {
    	EditText nameBox = (EditText) findViewById(R.id.splash_namebox);
    	EditText passBox = (EditText) findViewById(R.id.splash_passbox);
    	EditText cardBox = (EditText) findViewById(R.id.splash_cardbox);
    	EditText validityMonthBox = (EditText) findViewById(R.id.splash_validity_month);
    	EditText validityYearBox = (EditText) findViewById(R.id.splash_validity_year);
    	
    	
    	String name = nameBox.getText().toString();
    	password = passBox.getText().toString();
    	String cardNumber = cardBox.getText().toString();
    	String validity = validityMonthBox.getText().toString() + "/" + validityYearBox.getText().toString();
    	String cardType = cardSpinner.getSelectedItem().toString();
    	
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("nib", cardNumber));
		params.add(new BasicNameValuePair("pass", password));
		params.add(new BasicNameValuePair("cardType", cardType));
		params.add(new BasicNameValuePair("validity", validity));

		currentFunction = RESTFunction.CREATE_CLIENT;
		ConnectionThread dataThread = new ConnectionThread("http://192.168.0.136:81/client/create/", Method.POST,params, threadConnectionHandler);
		dataThread.start();
    }
    
    public void handleInitialization() {
        ArrayList<String> fileContents = fHandler.readFromFile();
        
        if(fileContents.size() != 0) {
        	
    		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
    		params.add(new BasicNameValuePair("name", fileContents.get(0)));
    		params.add(new BasicNameValuePair("pass", fileContents.get(1)));
        	
    		currentFunction = RESTFunction.LOGIN_CLIENT;
        	ConnectionThread dataThread = new ConnectionThread("http://192.168.0.136:81/client/login/", Method.POST, params, threadConnectionHandler);
    		dataThread.start();
        }
    }

	private void handleCreation(Message msg) {
		JSONObject received = (JSONObject) msg.obj;
		
		try {
			toFile += received.getString("name") + '\n';
			toFile += password + '\n';
			toFile += received.getString("id");
		} catch (JSONException e) {
			dialogYesNoShowing("Registration Error", "An unexpected error has ocurred upon registering. Register again?");	
		}
		
		fHandler.setToWrite(toFile);
		fHandler.writeToFile();
		
		registrationSuccess();
	}
    
	private void handleLogin(Message msg) {
		JSONObject received = (JSONObject) msg.obj;
		
		try {
			received.getString("error");
			dialogYesNoShowing("Incorrect Login!", "Your login info does not exist in the server. Register again?");			
		}
		catch(JSONException jsonExp) {
	        Intent intent = new Intent(MainActivity.this, CentralActivity.class);
	        startActivity(intent);
		}
	}
	
	private void dialogYesNoShowing(String title, String text){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle(title);

		alertDialogBuilder
		.setMessage(text)
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				fHandler.deleteFile();
				MainActivity.this.recreate();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				finish();
	            System.exit(0);
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private void registrationSuccess() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle("Registration succeeded!");

		alertDialogBuilder
		.setMessage("Enjoy Bus Ticketer!")
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
		        Intent intent = new Intent(MainActivity.this, CentralActivity.class);
		        startActivity(intent);
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
