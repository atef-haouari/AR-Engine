package org.arengine.samples.worldplaces;

import java.io.FileOutputStream;
import java.io.IOException;

import org.arengine.engine.CaptureCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SaveToFileCaptureCallback implements CaptureCallback {
	
	private Context context;

	public SaveToFileCaptureCallback(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void onCapture(Bitmap bitmap) {
		try {
			
			String filePath = Environment.getExternalStorageDirectory()+ "/capture.jpg";
			FileOutputStream out = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			Toast.makeText(context, "Captue saved at " + filePath,
					Toast.LENGTH_SHORT).show();
			
		} catch (IOException e) {
			Log.e("WorldPlacesSampleActivity", "Captue Problem", e);
		}

	}
}
