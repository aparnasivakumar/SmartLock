package com.example.lock;

import java.util.List;

import receiver.lockScreenReceiver;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

public class MyService extends Service {
	BroadcastReceiver mReceiver;
	SharedPreferences sh_Pref;
	public static final String PREFS_NAME = "MyPrefsFile.txt";
	ActivityManager am;
	List< ActivityManager.RunningTaskInfo > taskInfo ;
	boolean unlock;
	Intent mLockPhone;
	IntentFilter filter;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		@SuppressWarnings("deprecation")
		KeyguardManager.KeyguardLock k1;
		KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		 am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		k1 = km.newKeyguardLock("IN");
		k1.disableKeyguard();
		mLockPhone = new Intent(this, MainActivity.class);
		mLockPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF); 
		super.onCreate();

	}


	@Override
	public void onStart(final Intent intent,final int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		sh_Pref = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		unlock = sh_Pref.getBoolean("Unlock", false);
	
		// get the info from the currently running task
		taskInfo = am.getRunningTasks(1);
		final String className = taskInfo.get(0).topActivity.getClassName();
		
		if(!className.equals("com.example.lock.MainActivity") && 
		   !className.equals("com.android.phone.InCallScreen")){	 
			 if(!unlock){
				 startActivity(mLockPhone);
			 }
		}

		mReceiver = new lockScreenReceiver(); 
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
