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

import java.util.List;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;

/**
 * @author Atef Haouari
 * 
 *         Sensor helper that aims to easily access to different sensors values.
 */
public class SensorsHandler implements SensorEventListener, LocationListener {

	// sensors values
	private double mCompassDirection = 0;
	private double mHorizontalDirection = 0;
	private double mVerticalDirection = 0;

	// location providers values
	private Location gpsLocation;
	private Location networkLocation;
	private boolean gpsProvider = false;

	// location providers statues
	private boolean gpsTempUnavailable = false;
	private boolean networkProvider = false;

	/**
	 * Indicate if the compass is oriented and the camera are oriented to the
	 * same direction. Usually In case of a tablet, the default orientation is
	 * landscape mode and both of camera and compass are oriented to the same
	 * direction.
	 */
	private boolean isCompassAlignedWithCamera = false;
	/**
	 * The location manager
	 */
	private LocationManager locationManager;

	/**
	 * The sonsor manager
	 */
	private SensorManager sensorMgr;

	/**
	 * Gravity sensor
	 */
	private Sensor sensorGrav;

	/**
	 * Magnetic sensor
	 */
	private Sensor sensorMag;

	/**
	 * Orientation sensor
	 */
	private Sensor sensorOrientation;

	// Raw values of sensors
	private float[] mGravity = new float[3];
	private float[] mGeomagnetic = new float[3];

	// Sensor filters
	private SensorFilter gravityFilter;
	private SensorFilter magneticFilter;

	/**
	 * actual context
	 */
	private Context mContext;

	/**
	 * The view that must be invalidated after each changes
	 */
	private View mInfoView;

	public SensorsHandler(View infoView, int defaultScreenOrientation) {
		this.mContext = infoView.getContext();
		this.mInfoView = infoView;
		if (defaultScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			isCompassAlignedWithCamera = true;
		} else {
			isCompassAlignedWithCamera = false;
		}

	}

	/**
	 * Start sensors listening.
	 */
	public void startCapteurs() {

		sensorMgr = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> sensors = sensorMgr
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sensorGrav = sensors.get(0);
		}

		sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (sensors.size() > 0) {
			sensorMag = sensors.get(0);
		}

		sensors = sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			sensorOrientation = sensors.get(0);
		}

		gravityFilter = new SensorFilter();
		magneticFilter = new SensorFilter();

		sensorMgr.registerListener(this, sensorGrav,
				SensorManager.SENSOR_DELAY_GAME);
		sensorMgr.registerListener(this, sensorMag,
				SensorManager.SENSOR_DELAY_GAME);
		sensorMgr.registerListener(this, sensorOrientation,
				SensorManager.SENSOR_DELAY_GAME);

		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1000, 10, this);
		networkLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1000, 1, this);
		gpsLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

	}

	/**
	 * Stop sensors listening.
	 */
	public void stopCapteurs() {
		sensorMgr.unregisterListener(this, sensorGrav);
		sensorMgr.unregisterListener(this, sensorMag);
		sensorMgr.unregisterListener(this, sensorOrientation);

		locationManager.removeUpdates(this);
		locationManager = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
	 * .Sensor, int)
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			gravityFilter.filter(event.values, mGravity);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magneticFilter.filter(event.values, mGeomagnetic);
		}
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
					mGeomagnetic);

			float R2[] = new float[9];
			if (!isCompassAlignedWithCamera()) {
				SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X,
						SensorManager.AXIS_MINUS_Z, R2);
			} else {
				SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z,
						SensorManager.AXIS_X, R2);

			}
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R2, orientation);
				mCompassDirection = Math.toDegrees(orientation[0]) + 180;
				if (mCompassDirection >= 360) {
					mCompassDirection = mCompassDirection - 360;
				}
				mVerticalDirection = Math.toDegrees(orientation[1]);

				mHorizontalDirection = Math.toDegrees(orientation[2]) + 90;
				if (mHorizontalDirection > 180) {
					mHorizontalDirection = -90
							- (90 - (mHorizontalDirection - 180));
				}

			}
		}
		mInfoView.invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	public void onLocationChanged(Location location) {
		if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
			gpsLocation = location;
			gpsProvider = true;
		} else if (LocationManager.NETWORK_PROVIDER.equals(location
				.getProvider())) {
			networkLocation = location;
			networkProvider = true;
		}

		mInfoView.invalidate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	public void onProviderDisabled(String provider) {
		if (LocationManager.GPS_PROVIDER.equals(provider)) {
			gpsProvider = false;
		} else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
			networkProvider = false;
		}
		mInfoView.invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	public void onProviderEnabled(String provider) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	public void onStatusChanged(String provider, int status, Bundle arg2) {
		if (LocationManager.GPS_PROVIDER.equals(provider)) {
			if (LocationProvider.AVAILABLE == status) {
				gpsProvider = true;
				gpsTempUnavailable = false;
			}
			if (LocationProvider.TEMPORARILY_UNAVAILABLE == status) {
				gpsProvider = false;
				gpsTempUnavailable = true;
			} else {
				gpsTempUnavailable = false;
				gpsProvider = false;
			}
		} else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
			if (LocationProvider.AVAILABLE == status) {
				networkProvider = true;
			} else {
				networkProvider = false;
			}
		}
		mInfoView.invalidate();

	}

	public boolean isGpsProvider() {
		return gpsProvider;
	}

	public boolean isNetworkProvider() {
		return networkProvider;
	}

	/**
	 * Returns the actual compass direction values between 0 an 359
	 * 
	 * 0/359 : the top of device oriented on the north
	 * 
	 * 90: the top of device oriented on the east
	 * 
	 * 180: the top of device oriented on the south
	 * 
	 * 170: the top of device oriented on the west
	 * 
	 * @return The actual horizontal direction
	 */
	public double getCompassDirection() {
		return mCompassDirection;
	}

	/**
	 * Returns the actual horizontal direction values between -89 an +89
	 * 
	 * 0 : screen parallel to the ground
	 * 
	 * 90: screen perpendicular to the ground and face to left
	 * 
	 * -90:: screen perpendicular to the ground and face to rigth
	 * 
	 * 
	 * @return The actual horizontal direction
	 */
	public double getHorizontalDirection() {
		return mHorizontalDirection;
	}

	/**
	 * Returns the actual vertical direction values between -179 an +179
	 * 
	 * 0 : parallel to the ground and the screen face to sky
	 * 
	 * 90: perpendicular to the ground and the top of device face to ground
	 * 
	 * -90: perpendicular to the ground and the top of device face to ground
	 * 
	 * -179/+179 : parallel to the ground and the screen face to ground
	 * 
	 * @return The actual vertical direction
	 */
	public double getVerticalDirection() {
		return mVerticalDirection;
	}

	/**
	 * Returns the actual location values
	 * 
	 * @return The current location
	 */
	public Location getCurrentLocation() {

		if ((gpsProvider || gpsTempUnavailable) && gpsLocation != null) {
			return gpsLocation;
		}
		if (networkProvider && networkLocation != null) {
			return networkLocation;
		}
		
		return networkLocation;
	}

	/**
	 * Returns the actual location provider name.
	 * 
	 * @return The location provider name
	 */
	public String getLocationProvider() {
		if ((gpsProvider || gpsTempUnavailable) && gpsLocation != null) {
			return LocationManager.GPS_PROVIDER;
		}
		if (networkProvider && networkLocation != null) {
			return LocationManager.NETWORK_PROVIDER;
		}
		return null;
	}

	/**
	 * Sheck if compass is aligned with the camera.
	 * 
	 * @return true if compass is aligned with the camera. False otherwise.
	 */
	public boolean isCompassAlignedWithCamera() {
		return isCompassAlignedWithCamera;
	}

	/**
	 * @author Atef Haouari
	 * 
	 *         Digital filter that calculates the average of last 20 values
	 */
	private class DigitalAverage {

		final int history_len = 20;
		double[] mLocHistory = new double[history_len];
		int mLocPos = 0;
		float lastAvrage = 0;

		float average(float d) {
			if ((lastAvrage == 0) || (Math.abs(d - lastAvrage) > 0.2)) {

				float avg = 0;

				mLocHistory[mLocPos] = d;

				mLocPos++;
				if (mLocPos > mLocHistory.length - 1) {
					mLocPos = 0;
				}
				for (double h : mLocHistory) {
					avg += h;
				}
				avg /= mLocHistory.length;
				lastAvrage = avg;
				return avg;
			} else {
				return lastAvrage;
			}
		}
	}

	/**
	 * @author Atef Haouari
	 * 
	 *         Digital filter that smooths value changes
	 */
	private class SensorFilter {

		DigitalAverage[] daigitalAverages = new DigitalAverage[] {
				new DigitalAverage(), new DigitalAverage(),
				new DigitalAverage() };

		private float[] lastVector = null;
		public float kFilteringFactor = 0.9f;

		public void filter(float[] inVector, float[] outVector) {

			if (lastVector == null) {
				lastVector = new float[3];
				lastVector[0] = inVector[0];
				lastVector[1] = inVector[1];
				lastVector[2] = inVector[2];
			} else {
				for (int i = 0; i < 3; i++) {
					float value = kFilteringFactor * inVector[i]
							+ (1 - kFilteringFactor) * lastVector[i];
					value = daigitalAverages[i].average(value);
					outVector[i] = value;
					lastVector[i] = value;
				}
			}
		}
	}
}
