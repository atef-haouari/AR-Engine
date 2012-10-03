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

import java.util.ArrayList;
import java.util.List;

import org.arengine.devices.SensorsHandler;
import org.arengine.painter.DefaultPainter;
import org.arengine.painter.DefaultSonar;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * The Class AREngineActivity.
 * 
 * @author Atef Haouari
 * 
 *         The main Activity that contains the CameraView and the AREngineView.
 * 
 *         By default, the CameraView and AREngineView fill all the screen. If a
 *         custom layout is specified (trough overriding getCustomLayoutId), the
 *         the CameraView and AREngineView will fill the frame layout with id
 *         org.arengine.R.id.arEngineContent.
 * 
 *         Also by default, only the default sonar is drawn as a custom overlay
 *         and a basic painter is user.
 * 
 *         override this activity the create their own augmented reality
 *         activity.
 */
public class AREngineActivity extends Activity {

	/** The Constant DEFAULT_MAX_DISTANCE_FILTER. */
	public static final double DEFAULT_MAX_DISTANCE_FILTER = 10000;

	/** The Constant AR_VIEW_TAG. */
	public static final String AR_VIEW_TAG = "AR_VIEW";

	/** The capteur handler. */
	private SensorsHandler capteurHandler;

	/** The overlays. */
	private List<CustomOverlay> overlays = new ArrayList<CustomOverlay>();

	/** The poi list. */
	private List<Poi> poiList = new ArrayList<Poi>();

	/** The poi touch listener. */
	private OnPoiTouchListener poiTouchListener;

	/** The poi painter. */
	private PoiPainter poiPainter;

	/** The max distance filter. */
	private double maxDistanceFilter = DEFAULT_MAX_DISTANCE_FILTER;

	/** The is filter distance. */
	private boolean isFilterDistance = false;

	/** The ar engine view. */
	private AREngineView arEngineView;

	/** The ar engine content. */
	private FrameLayout arEngineContent = null;

	/** The camera view. */
	private CameraView cameraView;

	/** The captue images. */
	private ImageView captueImages = null;

	/** The sonar. */
	private DefaultSonar sonar = null;

	private PoiPainter defaultPoiPainter = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	final protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int defaultScreenOrientation = checkDefaultScreenOrientation();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int layoutId = getCustomLayoutId();
		if (layoutId != -1) {
			setContentView(layoutId);
			arEngineContent = (FrameLayout) ((FrameLayout) this
					.findViewById(android.R.id.content))
					.findViewWithTag(AR_VIEW_TAG);
			if (arEngineContent == null) {
				throw new IllegalStateException(
						"Must contain FrameLayout with tag AR_VIEW_TAG");
			}
		} else {
			arEngineContent = new FrameLayout(this);
			setContentView(arEngineContent);
		}

		LayoutParams layoutParams = new LayoutParams();
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = LayoutParams.MATCH_PARENT;

		cameraView = new CameraView(this, null);
		cameraView.setKeepScreenOn(true);
		cameraView.setLayoutParams(layoutParams);
		arEngineContent.addView(cameraView);

		captueImages = new ImageView(this);
		captueImages.setScaleType(ScaleType.FIT_XY);
		captueImages.setLayoutParams(layoutParams);
		arEngineContent.addView(captueImages);

		arEngineView = new AREngineView(this);
		arEngineView.setKeepScreenOn(true);
		arEngineView.setLayoutParams(layoutParams);

		capteurHandler = new SensorsHandler(arEngineView,
				defaultScreenOrientation);
		Projector projector = new Projector(capteurHandler,
				cameraView.getCameraManager());

		arEngineView.setProjector(projector);
		arEngineContent.addView(arEngineView);

		sonar = new DefaultSonar();

		defaultPoiPainter = new DefaultPainter();

		onInitAREngine();

		onAddOverlays();

		onAddPoiList();

	}

	/**
	 * On init ar engine.
	 */
	public void onInitAREngine() {

	}

	/**
	 * On add poi list.
	 */
	public void onAddPoiList() {

	}

	/**
	 * On add overlays.
	 */
	public void onAddOverlays() {

	}

	/**
	 * Adds the sonar overlay.
	 */
	public void addSonarOverlay() {
		this.addOverlay(sonar);
	}

	/**
	 * Removes the sonar.
	 */
	public void removeSonarOverlay() {
		this.removeOverlay(sonar);
	}

	/**
	 * Gets the custom layout id.
	 * 
	 * @return the custom layout id
	 */
	public int getCustomLayoutId() {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		capteurHandler.startCapteurs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStart();
		capteurHandler.stopCapteurs();
	}

	/**
	 * Adds the overlay.
	 * 
	 * @param overlay
	 *            the overlay
	 */
	public void addOverlay(CustomOverlay overlay) {
		overlays.add(overlay);
	}

	/**
	 * Removes the overlay.
	 * 
	 * @param overlay
	 *            the overlay
	 */
	public void removeOverlay(CustomOverlay overlay) {
		overlays.remove(overlay);
	}

	/**
	 * Adds the poi.
	 * 
	 * @param poi
	 *            the poi
	 */
	public void addPoi(Poi poi) {
		poiList.add(poi);
	}

	/**
	 * Removes the poi.
	 * 
	 * @param poi
	 *            the poi
	 */
	public void removePoi(Poi poi) {
		poiList.remove(poi);
	}

	/**
	 * Gets the overlays.
	 * 
	 * @return the overlays
	 */
	public CustomOverlay[] getOverlays() {
		return overlays.toArray(new CustomOverlay[0]);
	}

	/**
	 * Gets the poi list.
	 * 
	 * @return the poi list
	 */
	public Poi[] getPoiList() {
		return poiList.toArray(new Poi[0]);
	}

	/**
	 * Gets the poi touch listener.
	 * 
	 * @return the poi touch listener
	 */
	public OnPoiTouchListener getPoiTouchListener() {
		return poiTouchListener;
	}

	/**
	 * Sets the poi touch listener.
	 * 
	 * @param poiTouchListener
	 *            the new poi touch listener
	 */
	public void setPoiTouchListener(OnPoiTouchListener poiTouchListener) {
		this.poiTouchListener = poiTouchListener;
	}

	/**
	 * Gets the poi painter.
	 * 
	 * @return the poi painter
	 */
	public PoiPainter getPoiPainter() {
		return poiPainter;
	}

	/**
	 * Gets the default poi painter.
	 * 
	 * @return the default poi painter
	 */
	public PoiPainter getDefaultPoiPainter() {
		return defaultPoiPainter;
	}

	/**
	 * Sets the poi painter.
	 * 
	 * @param poiPainter
	 *            the new poi painter
	 */
	public void setPoiPainter(PoiPainter poiPainter) {
		this.poiPainter = poiPainter;
	}

	/**
	 * Check default screen orientation.
	 * 
	 * @return the int
	 */
	private int checkDefaultScreenOrientation() {
		int lastOrientation = getRequestedOrientation();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		Display display;
		display = getWindow().getWindowManager().getDefaultDisplay();
		int rotation = display.getOrientation();
		int width = 0;
		int height = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
		case Surface.ROTATION_180:
			width = display.getWidth();
			height = display.getHeight();
			break;
		case Surface.ROTATION_90:
		case Surface.ROTATION_270:
			width = display.getHeight();
			height = display.getWidth();
			break;
		default:
			break;
		}
		setRequestedOrientation(lastOrientation);
		if (width > height) {
			return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		} else {
			return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		}
	}

	/**
	 * Gets the max distance filter.
	 * 
	 * @return the max distance filter
	 */
	public double getMaxDistanceFilter() {
		return maxDistanceFilter;
	}

	/**
	 * Sets the max distance filter.
	 * 
	 * @param maxDistanceFilter
	 *            the new max distance filter
	 */
	public void setMaxDistanceFilter(double maxDistanceFilter) {
		this.maxDistanceFilter = maxDistanceFilter;
	}

	/**
	 * Checks if is filter distance enabled.
	 * 
	 * @return true, if is filter distance enabled
	 */
	public boolean isFilterDistanceEnabled() {
		return isFilterDistance;
	}

	/**
	 * Enable filter distance.
	 */
	public void enableFilterDistance() {
		isFilterDistance = true;
	}

	/**
	 * Disable filter distance.
	 */
	public void disableFilterDistance() {
		isFilterDistance = false;
	}

	/**
	 * Captrue.
	 * 
	 * @param callback
	 *            the callback
	 * @return the bitmap
	 */
	public void captrue(final CaptureCallback callback) {
		new Thread((Runnable) new CaptrueHandler(this, callback)).start();

	}

	/**
	 * The Class CaptrueHandler.
	 */
	private static class CaptrueHandler extends Handler implements Runnable {

		/** The capture bitmap. */
		private Bitmap captureBitmap;
		/** The capture drawble. */
		private Drawable captureDrawble;

		/** The activity. */
		private AREngineActivity activity;

		/** The callback. */
		private CaptureCallback callback;

		/**
		 * Instantiates a new captrue handler.
		 * 
		 * @param activity
		 *            the activity
		 * @param callback
		 *            the callback
		 */
		public CaptrueHandler(AREngineActivity activity,
				CaptureCallback callback) {
			this.activity = activity;
			this.callback = callback;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			activity.captueImages.setImageDrawable(captureDrawble);
			activity.captueImages.setVisibility(View.VISIBLE);
			Canvas canvas = new Canvas(captureBitmap);
			activity.arEngineContent.draw(canvas);
			activity.captueImages.setVisibility(View.GONE);
			if (callback != null) {
				callback.onCapture(captureBitmap);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			captureBitmap = Bitmap.createBitmap(activity.cameraView.getWidth(),
					activity.cameraView.getHeight(), Bitmap.Config.ARGB_8888);
			captureDrawble = new BitmapDrawable(activity.cameraView.capture());
			if (activity.captueImages.getDrawable() != null) {
				((BitmapDrawable) activity.captueImages.getDrawable())
						.getBitmap().recycle();
			}
			sendEmptyMessage(1);

		}

	}

}