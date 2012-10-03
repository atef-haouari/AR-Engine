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

import org.arengine.devices.CameraManager;
import org.arengine.devices.SensorsHandler;

import android.graphics.Point;
import android.location.Location;

/**
 * @author Atef Haouari
 * 
 *         Calculate projections values such as translations an rotations values
 *         needed to draw overlays on projection view.
 */
public final class Projector {

	/**
	 * Contains the max distance (in meter) to filter poi. If the filter is active, only
	 * poi which distance less than maxDistanceFilter this will be shown.
	 * 
	 */
	private double maxDistanceFilter;

	/**
	 * Indicates if poi are filtered on distance.
	 */
	private boolean isFilterDistance = false;

	/**
	 * The sensor handler
	 */
	private SensorsHandler mSensorsHandler;

	/**
	 * The camera manager
	 */
	private CameraManager mCamaraManager;

	/**
	 * The real view width in pixel
	 */
	private float mViewWidth;

	/**
	 * The real view height in pixel
	 */
	private float mViewHeight;

	// sensors values
	private double mHorizontalDirection;
	private double mVerticalDirection;
	private double mCompassDirection;

	/**
	 * When we rotate the device, the width of the screen changes until it
	 * becomes the height the virtual window width
	 */
	private double dynamicWidth;

	/**
	 * When we rotate the device, the height of the screen changes until it
	 * becomes the width the virtual window height
	 */
	private double dynamicHeight;

	/**
	 * As width and height changes, the center's coordinates also changes
	 */
	private Point centerPointDynamic;

	/**
	 * When we rotate the device, the horizontal angle view of the screen
	 * changes until it becomes the vertical angle view.
	 */
	private double angleHorizontalDynamic;

	/**
	 * When we rotate the device, the vertical angle view of the screen changes
	 * until it becomes the horizontal angle view.
	 */
	private double angleVerticalDynamic;

	/**
	 * Translation X value needed to show the projection view.
	 */
	private double translationX;

	/**
	 * Translation Y value needed to show the projection view.
	 */
	private double translationY;

	/**
	 * Rotation value needed to show the projection view.
	 */
	private double rotation;

	/**
	 * Ration pixel/degree on horizontal line
	 */
	private double pixByDegHorizontal;

	/**
	 * Ration pixel/degree on vertical line
	 */
	private double pixByDegVertical;

	/**
	 * Poi list
	 */
	private List<Poi> poiList = new ArrayList<Poi>();

	protected Projector(SensorsHandler capteurHandler,
			CameraManager cameraManager) {
		this.mSensorsHandler = capteurHandler;
		this.mCamaraManager = cameraManager;
	}

	/**
	 * Refresh sensors values
	 */
	private final void loadValues() {
		mCompassDirection = mSensorsHandler.getCompassDirection();
		mHorizontalDirection = mSensorsHandler.getHorizontalDirection();
		mVerticalDirection = mSensorsHandler.getVerticalDirection();

	}

	/**
	 * Calculate projection values
	 */
	private void calculateProjectionValues() {

		double angleRadian = Math.toRadians(mHorizontalDirection);
		double cosAngle = Math.abs((Math.cos(angleRadian)));

		double sinAngle = Math.abs(Math.sin(angleRadian));

		double dynamicWidth = this.mViewWidth * cosAngle + this.mViewHeight
				* sinAngle;
		double dynamicHeight = this.mViewHeight * cosAngle + this.mViewWidth
				* sinAngle;

		double centerPointDynamicX = dynamicWidth / 2;
		double centerPointDynamicY = dynamicHeight / 2;

		double ouvertureCameraWidth = mCamaraManager.getHorizontalViewAngle();
		double ouvertureCameraHeight = mCamaraManager.getVerticalViewAngle();
		double angleHorizontalDynamic = ouvertureCameraWidth * cosAngle
				+ ouvertureCameraHeight * sinAngle;

		double angleVerticalDynamic = ouvertureCameraHeight * cosAngle
				+ ouvertureCameraWidth * sinAngle;

		double rotation = mHorizontalDirection;

		double translationX;
		double translationY;

		if (rotation < 0 && rotation >= -90) {
			translationX = -(double) ((mViewHeight * sinAngle));
			translationY = 0;

		} else if (rotation >= -180 && rotation < -90) {

			translationX = -(float) ((mViewHeight * sinAngle) + (mViewWidth * cosAngle));
			translationY = -(float) (mViewHeight * cosAngle);

		} else if (rotation <= 180 && rotation > 90) {
			translationX = -(float) ((mViewWidth * cosAngle));
			translationY = -(float) ((mViewHeight * cosAngle) + (mViewWidth * sinAngle));

		} else {

			translationX = 0;
			translationY = -(mViewWidth * sinAngle);

		}

		double pixByDegHorizontal = (dynamicWidth / angleHorizontalDynamic);
		double pixByDegVertical = (dynamicHeight / angleVerticalDynamic);

		double negativePositifCompassDirection = mCompassDirection;
		if (negativePositifCompassDirection >= 180) {
			negativePositifCompassDirection = negativePositifCompassDirection - 360;
		}

		translationX += centerPointDynamicX
				- (negativePositifCompassDirection * pixByDegHorizontal);
		translationY += centerPointDynamicY
				+ (mVerticalDirection * pixByDegVertical);

		this.dynamicWidth = dynamicWidth;
		this.dynamicHeight = dynamicHeight;
		this.centerPointDynamic = new Point((int) centerPointDynamicX,
				(int) centerPointDynamicY);
		this.angleHorizontalDynamic = angleHorizontalDynamic;
		this.angleVerticalDynamic = angleVerticalDynamic;
		this.pixByDegHorizontal = pixByDegHorizontal;
		this.pixByDegVertical = pixByDegVertical;
		this.translationX = translationX;
		this.translationY = translationY;
		this.rotation = rotation;

	}

	/**
	 * 
	 * Refresh all values
	 * 
	 * @param allPoiList
	 *            list of Poi to show
	 * @param widthView
	 *            the real width of the view
	 * @param heightView
	 *            the real height of the view
	 * @param isFilterDistance
	 * @param maxDistanceFilter
	 */
	final void refresh(List<Poi> allPoiList, int widthView, int heightView,
			boolean isFilterDistance, double maxDistanceFilter) {
		this.maxDistanceFilter = maxDistanceFilter;
		this.isFilterDistance = isFilterDistance;
		this.mViewWidth = widthView;
		this.mViewHeight = heightView;
		loadValues();
		calculateProjectionValues();
		poiList.clear();
		if (getCurLocation() != null) {
			for (Poi poi : allPoiList) {
				poi.refrechDistanceAndAngle(getCurLocation());
				if (!isFilterDistance
						|| (isFilterDistance && (poi.getDistance() <= maxDistanceFilter))) {

					float xPoi = (float) (poi.getAngleWithNorth() * pixByDegHorizontal);
					float yPoi = (float) (poi.getAngleWithGround() * pixByDegVertical);
					poi.setProjectionPoint(xPoi, -yPoi);
					poiList.add(poi);
				}
			}
		}
	}

	public float getHorizontalViewAngle() {
		return mCamaraManager.getHorizontalViewAngle();
	}

	public float getVerticalViewAngle() {
		return mCamaraManager.getVerticalViewAngle();
	}

	public double getDynamicWidth() {
		return dynamicWidth;
	}

	public double getDynamicHeight() {
		return dynamicHeight;
	}

	public Point getCenterPointDynamic() {
		return centerPointDynamic;
	}

	/**
	 * Returns the actual compass direction values between -180 and 180
	 * 
	 * 0 : the top of device oriented on the north
	 * 
	 * 90: the top of device oriented on the east
	 * 
	 * 180/-180: the top of device oriented on the south
	 * 
	 * -90: the top of device oriented on the west
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
	 * Translation X value needed to show the projection view.
	 * 
	 * @return Translation X
	 */
	public double getTranslationX() {
		return translationX;
	}

	/**
	 * Translation Y value needed to show the projection view.
	 * 
	 * @return Translation Y
	 */
	public double getTranslationY() {
		return translationY;
	}

	/**
	 * Rotation value needed to show the projection view.
	 * 
	 * @return Rotation
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * 
	 * When we rotate the device, the horizontal angle view of the screen
	 * changes until it becomes the vertical angle view.
	 * 
	 * @return angleHorizontalDynamic
	 */
	public double getAngleHorizontalDynamic() {
		return angleHorizontalDynamic;
	}

	/**
	 * When we rotate the device, the vertical angle view of the screen changes
	 * until it becomes the horizontal angle view.
	 * 
	 * @return angleVerticalDynamic
	 */
	public double getAngleVerticalDynamic() {
		return angleVerticalDynamic;
	}

	/**
	 * Ration pixel/degree on horizontal line
	 * 
	 * @return The pixel/degree on horizontal line
	 */
	public double getPixByDegHorizontal() {
		return pixByDegHorizontal;
	}

	/**
	 * Ration pixel/degree on vertical line
	 * 
	 * @return The pixel/degree on vertical line
	 */
	public double getPixByDegVertical() {
		return pixByDegVertical;
	}

	/**
	 * Returns the actual location provider name.
	 * 
	 * @return The location provider name
	 */
	public String getLocationProvider() {
		return mSensorsHandler.getLocationProvider();
	}

	/**
	 * Returns the actual location values
	 * 
	 * @return The current location
	 */
	public Location getCurLocation() {
		return mSensorsHandler.getCurrentLocation();
	}

	/**
	 * Returns the real view width in pixel
	 * 
	 * @return the real view width in pixel
	 */
	public float getViewWidth() {
		return mViewWidth;
	}

	/**
	 * Returns the real view height in pixel
	 * 
	 * @return the real view height in pixel
	 */
	public float getViewHeight() {
		return mViewHeight;
	}

	/**
	 * Returns the actual poi list to show
	 * 
	 * @return the actual poi list to show
	 */
	public List<Poi> getPoiList() {
		return poiList;
	}

	/**
	 * The max distance to filter poi. If the filter is active, only poi which
	 * distance less than maxDistanceFilter this will be shown.
	 * 
	 * @return the max distance filter
	 */
	public double getMaxDistanceFilter() {
		return maxDistanceFilter;
	}

	/**
	 * Indicates whether the distance filter is activated
	 * 
	 * @return whether the distance filter is activated
	 */
	public boolean isFilterDistanceEnabled() {
		return isFilterDistance;
	}

}
