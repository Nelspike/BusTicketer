package bus.ticketer.passenger;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.*;

import bus.ticketer.utils.SpinnerAdapter;

public class MainActivity extends Activity {

    @SuppressLint("InlinedApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_NoActionBar);
        setContentView(R.layout.activity_main);
        
        TextView text = (TextView) findViewById(R.id.terms_textbox);
        text.setMovementMethod(new ScrollingMovementMethod());
        
        String[] cardTypes = { "Visa", "Visa Electron", "Master Card", "Meastro",
        						"American Express", "Multibanco" };
        int[] images = { R.drawable.ico_home_visa};
        
        Spinner spinner = (Spinner) findViewById(R.id.cc_type_spinner);
        spinner.setAdapter(new SpinnerAdapter(MainActivity.this, R.layout.spinner_cc_choice_box, cardTypes, images));
        
        Intent i = new Intent(MainActivity.this, CentralActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
