package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.example.lock.MainActivity;

public class lockScreenReceiver extends BroadcastReceiver {
	public static boolean wasScreenOn = true;
	SharedPreferences sh_Pref;
	public static final String PREFS_NAME = "MyPrefsFile.txt";

	@Override
	public void onReceive(Context context, Intent intent) {
		sh_Pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			wasScreenOn = false;		
			Editor toedit = sh_Pref.edit();
			toedit.putBoolean("Unlock", false);
			toedit.commit();
			Intent intent11 = new Intent(context, MainActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent11);
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			wasScreenOn = true;
			Editor toedit = sh_Pref.edit();
			toedit.putBoolean("Unlock", false);
			toedit.commit();
			Intent intent11 = new Intent(context, MainActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Editor toedit = sh_Pref.edit();
			toedit.putBoolean("Unlock", false);
			toedit.commit();
			Intent intent11 = new Intent(context, MainActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent11);
		}

	}

}
