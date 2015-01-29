package com.example.lock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class Setting extends Activity {

	public static final String PREFS_NAME = "MyPrefsFile.txt";
	private ToggleButton toggleButton1; 
	EditText pass , email;
	SharedPreferences sh_Pref;
	Editor toEdit;	
	Button save;
	Button reset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);	
		save = (Button)findViewById(R.id.button1);
		pass = (EditText)findViewById(R.id.editText1);
		reset = (Button)findViewById(R.id.button3);
		email = (EditText)findViewById(R.id.editText2);
		toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
		final DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		sh_Pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); 
		save.setEnabled(sh_Pref.getBoolean("saveButton", true));
		pass.setEnabled(sh_Pref.getBoolean("saveText", true));
		email.setEnabled(sh_Pref.getBoolean("emailText", true));	 
		toggleButton1.setChecked(sh_Pref.getBoolean("toggleButton", false));
		reset.setEnabled(sh_Pref.getBoolean("resetButton", false));
	

		save.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				db.save( pass.getText().toString(),email.getText().toString());	
				save.setEnabled(false);
				pass.setEnabled(false);
				email.setEnabled(false);
				reset.setEnabled(true);
				toggleButton1.setChecked(true);		
				Intent i = new Intent(Setting.this,MainActivity.class);
				startActivity(i);	
				finish();
			}
		});

		reset.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				db.reset();	
				save.setEnabled(true);
				pass.setEnabled(true);
				email.setEnabled(true);	 
				reset.setEnabled(false);
				toggleButton1.setChecked(false);
			}
		});
		
		toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		          System.out.println("I am checked");
		        } else {
		           System.out.println("I am not");
		        }
		    }
		});
	}

	@Override
	protected void onPause(){
		super.onPause();
		sh_Pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); 
		toEdit = sh_Pref.edit();
		boolean sButton = save.isEnabled();
		boolean stext = pass.isEnabled();
		boolean etext = email.isEnabled();
		boolean rButton = reset.isEnabled();
		boolean toggle = toggleButton1.isChecked();
		toEdit.putBoolean("saveButton", sButton);
		toEdit.putBoolean("resetButton", rButton);
		toEdit.putBoolean("saveText", stext);
		toEdit.putBoolean("emailText", etext);
		toEdit.putBoolean("toggleButton", toggle);
		toEdit.commit();
	}

	@Override
	protected void onStop(){
		super.onStop();
		finish();
	}

	

}
