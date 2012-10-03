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

package org.arengine.devices;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * The Class CameraManager.
 * 
 * @author Atef Haouari
 * 
 * 
 *         Camera helper to start and stop camera preview.
 */

public class CameraManager {

	/** The m camera. */
	private Camera mCamera;

	/** The m holder. */
	private SurfaceHolder mHolder;

	/** The m preview running. */
	boolean mPreviewRunning = false;

	/** The horizontal view angle. */
	private float horizontalViewAngle = 54.8f;

	/** The vertical view angle. */
	private float verticalViewAngle = 42.5f;

	/** The last preview. */
	private byte[] lastPreview;

	/**
	 * Instantiates a new camera manager.
	 * 
	 * @param holder
	 *            the holder
	 */
	public CameraManager(SurfaceHolder holder) {

		this.mHolder = holder;

	}

	/**
	 * Initialize camera parameters.
	 * 
	 */
	private void initCamera() {

		mCamera = Camera.open();

		try {
			mCamera.setPreviewDisplay(mHolder);
			Camera.Parameters parameters = mCamera.getParameters();

			List<Size> sizes = parameters.getSupportedPreviewSizes();

			Size optimalSize = getMaxPreviewSize(sizes);

			parameters.setPreviewSize(optimalSize.width, optimalSize.height);

			mCamera.setParameters(parameters);

			if (parameters.getHorizontalViewAngle() > 0) {
				horizontalViewAngle = parameters.getHorizontalViewAngle();
			}
			if (parameters.getVerticalViewAngle() > 0) {
				verticalViewAngle = parameters.getVerticalViewAngle();
			}

		} catch (IOException exception) {
			Log.e(CameraManager.class.getName(), "Fail to open camera",
					exception);
		}

	}

	/**
	 * Gets the m camera.
	 * 
	 * @return the m camera
	 */
	public Camera getmCamera() {
		return mCamera;
	}

	/**
	 * Start camera previewing.
	 */
	public void startCamera() {
		if (mCamera == null) {
			initCamera();
		}
		if (!mPreviewRunning) {
			mCamera.startPreview();
			mCamera.setPreviewCallback(new PreviewCallback() {

				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					lastPreview = data;

				}
			});
		}
		mPreviewRunning = true;
	}

	/**
	 * Gets the last preview.
	 * 
	 * @return the last preview
	 */
	public byte[] getLastPreview() {
		return lastPreview;
	}

	/**
	 * Stop camera previewing.
	 */
	public void stopCamera() {
		if (mPreviewRunning) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
		}
		mPreviewRunning = false;
		mCamera = null;
	}

	/**
	 * Return the horizontal angle view of the camera in degree.
	 * 
	 * @return horizontal angle view
	 */
	public float getHorizontalViewAngle() {
		return horizontalViewAngle;
	}

	/**
	 * Return the vertical angle view of the camera in degree.
	 * 
	 * @return vertical angle view
	 */
	public float getVerticalViewAngle() {
		return verticalViewAngle;
	}

	/**
	 * Calculate the biggest preview format.
	 * 
	 * @param sizes
	 *            sizes list
	 * @return the biggest size
	 */
	private Size getMaxPreviewSize(List<Size> sizes) {

		if (sizes == null || sizes.size() == 0) {
			return null;
		}
		int maxSize = 0;
		int maxSizeIndex = -1;
		for (int i = 0; i < sizes.size(); i++) {
			Size size = sizes.get(i);
			int widthOrheight = Math.max(size.width, size.height);
			if (maxSizeIndex == -1) {
				maxSize = widthOrheight;
				maxSizeIndex = i;
			} else {
				if (widthOrheight > maxSize) {
					maxSize = widthOrheight;
					maxSizeIndex = i;
				}
			}

		}
		return sizes.get(maxSizeIndex);

	}
}
