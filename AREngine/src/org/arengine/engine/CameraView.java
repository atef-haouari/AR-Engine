/**
 *
 *Copyright (C) 2012 Atef Haouari - VEEUP
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU Lesser General Public
 *License as published by the Free Software Foundation; either
 *version 3 of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *Lesser General Public License for more details.
 *
 *You should have received a copy of the GNU Lesser General Public
 *License along with this program; if not, write to the Free Software
 *Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA* 
 */
package org.arengine.engine;

import java.io.ByteArrayOutputStream;

import org.arengine.devices.CameraManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The Class CameraView.
 *
 * @author Atef Haouari
 * 
 * The view holder of camera preview
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	/** The surface holder of the surface view. */
	private SurfaceHolder mHolder;

	/** The camera manager instance. */
	private CameraManager mCameraManager;

	/**
	 * Instantiates a new camera view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mCameraManager = new CameraManager(mHolder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
	 * )
	 */
	public void surfaceCreated(SurfaceHolder holder) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
	 * SurfaceHolder)
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCameraManager.stopCamera();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
	 * , int, int, int)
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		mCameraManager.startCamera();
	}

	/**
	 * Returns the camera manager.
	 *
	 * @return the camera manager
	 */
	public CameraManager getCameraManager() {
		return mCameraManager;
	}

	/**
	 * Capture.
	 *
	 * @return the bitmap
	 */
	public Bitmap capture() {
		byte[] data = mCameraManager.getLastPreview();

		int imageFormat = mCameraManager.getmCamera().getParameters()
				.getPreviewFormat();
		if (imageFormat == ImageFormat.NV21 || imageFormat == ImageFormat.YUY2) {
			// Rect rect = new Rect(0, 0, mCameraManager.getmCamera()
			// .getParameters().getPreviewSize().width, mCameraManager
			// .getmCamera().getParameters().getPreviewSize().height);
			YuvImage img = new YuvImage(data, imageFormat, mCameraManager
					.getmCamera().getParameters().getPreviewSize().width,
					mCameraManager.getmCamera().getParameters()
							.getPreviewSize().height, null);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// YuvImage yuvImage = new YuvImage(data, PictureFormat.NV21, width,
			// height, null);
			img.compressToJpeg(new Rect(0, 0, mCameraManager.getmCamera()
					.getParameters().getPreviewSize().width, mCameraManager
					.getmCamera().getParameters().getPreviewSize().height), 50,
					out);
			byte[] imageBytes = out.toByteArray();
			Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0,
					imageBytes.length);
			return image;
		}

		else if (imageFormat == ImageFormat.JPEG
				|| imageFormat == ImageFormat.RGB_565) {
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		}

		return null;
	}

}
