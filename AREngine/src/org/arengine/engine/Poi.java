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

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author Atef Haouari
 * 
 *         This class represents the POI (Points Of Interest) which AREngine
 *         aims to display. These Poi properties are basic. Developers are free
 *         to extend this class in to enrich
 * 
 */
public class Poi {

	/**
	 * The poi title
	 */
	private String title;

	/**
	 * the poi icon
	 */
	private Bitmap icon;

	/**
	 * The poi location: longitude, latitude an altitude.
	 */
	private Location location;

	/**
	 * contains the poi coordinates on the projection view. this attribute is
	 * calculated by the Projector.
	 */
	private PointF projectionPoint;

	/**
	 * Contains the distance (in meter) separating the user location and the
	 * poi. This attribute is calculated by the refrechDistanceAndAngle. Only
	 * the read of this property is allowed through the getter.
	 */
	private double distance = 0;

	/**
	 * Contains the angle (in degree) the user location (center), the north and
	 * the poi location.
	 */
	private double angleWithNorth = 0;

	/**
	 * Contains the angle (in degree) the user location (center), the ground and
	 * the poi location (based on altitude)
	 */
	private double angleWithGround = 0;

	/**
	 * 
	 * The poi constructor without altitude
	 * 
	 * @param title
	 * @param icon
	 * @param longitude
	 * @param latitude
	 */
	public Poi(String title, Bitmap icon, double longitude, double latitude) {
		this.title = title;
		this.icon = icon;
		location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		projectionPoint = new PointF(0, 0);

	}

	/**
	 * The poi constructor with altitude
	 * 
	 * @param title
	 * @param icon
	 * @param longitude
	 * @param latitude
	 * @param altitude
	 */
	public Poi(String title, Bitmap icon, double longitude, double latitude,
			double altitude) {
		this(title, icon, longitude, latitude);
		location.setAltitude(altitude);

	}

	/**
	 * Calculate the distance, the angleWithNorth and angleWithGround. Calculate
	 * the. Only the projector might call this method.
	 * 
	 * @param userLocation
	 */
	protected void refrechDistanceAndAngle(Location userLocation) {
		angleWithNorth = userLocation.bearingTo(location);
		distance = userLocation.distanceTo(location);
		double diffAltitude = getLocation().getAltitude();
		if (userLocation.hasAltitude()) {
			diffAltitude = getLocation().getAltitude()
					- userLocation.getAltitude();

		}
		double angleRadian = Math.asin(diffAltitude / distance);
		angleWithGround = Math.toDegrees(angleRadian);
	}

	/**
	 * Sets the new projection values. Only the projector might call this
	 * method.
	 * 
	 * @param xPoi
	 * @param yPoi
	 */
	protected void setProjectionPoint(float xPoi, float yPoi) {
		projectionPoint.x = xPoi;
		projectionPoint.y = yPoi;

	}

	/**
	 * 
	 * Gets the projection point. Only the projector might call this method.
	 * 
	 * @return the projection point.
	 */
	protected PointF getProjectionPoint() {
		return projectionPoint;
	}

	/**
	 * Gets the poi title
	 * 
	 * @return the poi title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the poi title
	 * 
	 * @return the poi title
	 */
	public Bitmap getIcon() {
		return icon;
	}

	/**
	 * Gets the poi location
	 * 
	 * @return the poi location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the poi distance in meter
	 * 
	 * @return the poi distance in meter
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Gets the angle with north in degree
	 * 
	 * @return the angle with north
	 */
	public double getAngleWithNorth() {
		return angleWithNorth;
	}

	/**
	 * Gets the angle with ground in degree
	 * 
	 * @return the angle with ground
	 */
	public double getAngleWithGround() {
		return angleWithGround;
	}

}
