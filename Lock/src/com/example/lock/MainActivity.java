package com.example.lock;

import java.util.Calendar;

import com.example.lock.GalleryActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String PREFS_NAME = "MyPrefsFile.txt";
	KeyguardManager.KeyguardLock k1;
	SharedPreferences sh_Pref;
	final Context context = this;
	private static int attempts = 0;
	private boolean isRequiredToggle;
	private boolean isRequiredReset;
	Intent intent;


	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		this.getWindow().setType(
				WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG
				| WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onAttachedToWindow();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sh_Pref = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);	
		isRequiredToggle = sh_Pref.getBoolean("toggleButton", false);
		isRequiredReset = sh_Pref.getBoolean("resetButton", false);
		if ((isRequiredToggle) && (isRequiredReset)) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_main);

			try {
				
				Calendar cal = Calendar.getInstance();
				intent = new Intent(this, MyService.class);
				PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
				AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				alarm.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 3000, pintent); 		
				startService(new Intent(getBaseContext(), MyService.class));
				StateListener phoneStateListener = new StateListener();
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				telephonyManager.listen(phoneStateListener,
						PhoneStateListener.LISTEN_CALL_STATE);
				show_Dialog();
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("I am Paused by home screen");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_POWER)
				|| (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				|| (keyCode == KeyEvent.KEYCODE_CAMERA)) {

			return true;
		}
		if ((keyCode == KeyEvent.KEYCODE_HOME)) {

			Toast.makeText(this, "You pressed the home button!",
					Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_POWER
				|| (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
			return false;
		}
		if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

			return false;
		}
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
	}

	class StateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:			
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("call Activity off hook");				
			    finish();
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("Done");			
				break;
			}
		}
	};

	public void show_Dialog() {

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		final EditText userInput = new EditText(this);
		alertDialogBuilder.setView(userInput);
		userInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
		alertDialogBuilder.setTitle("Authenticate");
		alertDialogBuilder.setMessage("Enter your secret pin");
		
		alertDialogBuilder.setCancelable(false)
		.setNegativeButton("Unlock",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				/** DO THE METHOD HERE WHEN PROCEED IS CLICKED */
				String user_text = (userInput.getText()).toString();

				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());
				String passwordToken = db.getPassword(1);
				/** CHECK FOR USER'S INPUT **/
				if (user_text.equals(passwordToken)) {
					dialog.dismiss();
					Log.d(user_text,
							"HELLO THIS IS THE MESSAGE CAUGHT :)");
					Editor toedit = sh_Pref.edit();
					toedit.putBoolean("Unlock", true);
					toedit.apply();
					finish();
				} else {
					if (attempts != 2) {
						attempts++;
						show_Dialog();
					} else {
						dialog.dismiss();
						Intent i = new Intent(getApplicationContext(),
								GalleryActivity.class);
						startActivity(i);
						attempts = 0;
					}
				}
			}
		})
		
		.setPositiveButton("EmergencyCall",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {			
				Intent callIntent = new Intent(Intent.ACTION_CALL);
	            callIntent.setData(Uri.parse("tel:911"));
	            startActivity(callIntent);
			}
		});

		final AlertDialog alertDialog = alertDialogBuilder.create();

		userInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					alertDialog
					.getWindow()
					.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		alertDialog.show();
	}

}
