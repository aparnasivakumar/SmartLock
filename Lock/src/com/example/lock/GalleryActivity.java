package com.example.lock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GalleryActivity extends Activity implements SurfaceHolder.Callback {

	String emailId;
	private ImageView iv_image;
	private SurfaceView sv;
	private int cameraId = 0;
	private Bitmap bmp;
	private SurfaceHolder sHolder;
	private Camera mCamera;
	private Parameters parameters;

	/** Called when the activity is first created. */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);	
		iv_image = (ImageView) findViewById(R.id.imageView);
		sv = (SurfaceView) findViewById(R.id.surfaceView);		
		System.out.println("he he he");
		sHolder = sv.getHolder();
		sHolder.addCallback(this);	
		DatabaseHandler db = new DatabaseHandler(
				getApplicationContext());
		emailId= db.getEmail(1);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		parameters = mCamera.getParameters();
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		Camera.PictureCallback mCall = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {				
				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				iv_image.setImageBitmap(bmp);
				FileOutputStream outStream = null;
				try {

					String extStorageDirectory = Environment
							.getExternalStorageDirectory().toString();
					System.out.println(extStorageDirectory);
					String file = "/" + extStorageDirectory + "/Image"
							+ System.currentTimeMillis() + ".jpg";

					outStream = new FileOutputStream(file);
					outStream.write(data);
					outStream.close();

					new SendEmailAsyncTask().execute(new String[] {file});

					Intent i = new Intent(GalleryActivity.this,
							MainActivity.class);
					GalleryActivity.this.startActivity(i);
					finish();
				} catch (FileNotFoundException e) {
					Log.d("CAMERA", e.getMessage());
				} catch (IOException e) {
					Log.d("CAMERA", e.getMessage());
				}
			}
		};
		mCamera.takePicture(null, null, mCall);	
	}

	class SendEmailAsyncTask extends AsyncTask <String, Void, Boolean> {		 
		@Override
		protected Boolean doInBackground(String...file) {	    
			Mail m = new Mail("asurion91@gmail.com", "asurion123");
			String[] toArr = {'"' + emailId +'"' };
			m.setTo(toArr);
			m.setFrom("asurion91@gmail.com");
			m.setSubject("Lock Application Alert");
			m.setBody("A photo of the person trying to unlock your phone unsuccessfully has been attached to the mail. Photo was taken using front camera where possible");
			try {
				m.addAttachment(file[0]);
				if (m.send()) {
					deleteFile(file[0]);		
				} else {
					Toast.makeText(GalleryActivity.this,
							"Email was not sent.", Toast.LENGTH_LONG)
							.show();		
				}
			} catch (Exception e) {
				notification(file[0]);
				Log.e("MailApp", "Could not send email", e);
				return false;
			}
			return true; 
		}
	}


	public void notification(String file){

		File filepath = new File(file);
		System.out.println(filepath);
		NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);	
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(filepath), "image/*");
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		mBuilder.setContentTitle("New Photo of Unlocker ");
		mBuilder.setContentText("Navigate to see who tried to unlock your phone unsuccessfully");
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setContentIntent(pIntent);
		NotificationManager notificationManager = 
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, mBuilder.build()); 
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
			.show();
		} else {
			cameraId = findFrontFacingCamera();
			if (cameraId < 0) {
				Toast.makeText(this, "No front facing camera found.",
						Toast.LENGTH_LONG).show();
			}
		}
		mCamera = Camera.open(cameraId);
		try {
			System.out.println("First Stand");
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				Log.v("MyActivity", "Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		System.out.println("destroyed");
	}

	public boolean deleteFile(String inputFile) {
		try {
			new File(inputFile).delete();
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
			return false;
		}
		return true;
	}

}