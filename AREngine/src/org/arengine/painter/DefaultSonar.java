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
package org.arengine.painter;

import org.arengine.engine.CustomOverlay;
import org.arengine.engine.Poi;
import org.arengine.engine.Projector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * The Class DefaultSonar.
 */
public class DefaultSonar implements CustomOverlay {

	/**
	 * Instantiates a new default sonar.
	 */
	public DefaultSonar() {

	}

	/* (non-Javadoc)
	 * @see org.arengine.engine.CustomOverlay#draw(android.graphics.Canvas, org.arengine.engine.Projector)
	 */
	public void draw(Canvas canvas, Projector projector) {

		int radius = (int) (projector.getViewHeight() * 0.25f);
		double maxLength = 0;
		for (Poi poi : projector.getPoiList()) {
			if (poi.getDistance() > maxLength) {
				maxLength = poi.getDistance();
			}
		}
		double pixelByLength = (radius / (maxLength * 1.2f));

		int x = (int) (projector.getViewWidth() - (1.5 * radius));
		int y = (int) (projector.getViewHeight() / 2);

		float angleView = (float) projector.getAngleHorizontalDynamic();
		float directionUser = (float) projector.getCompassDirection();
		float rotationUser = (float) projector.getHorizontalDirection();

		Paint contour = new Paint();
		contour.setAntiAlias(true);
		int green = Color.rgb(0, 100, 0);
		contour.setColor(green);
		contour.setStyle(Style.STROKE);
		contour.setStrokeWidth(2);

		Paint pointPaint = new Paint();
		pointPaint.setAntiAlias(true);
		int greenPoint = Color.rgb(0, 150, 0);
		pointPaint.setColor(greenPoint);
		pointPaint.setStrokeWidth(4);

		canvas.save();
		canvas.translate(x, y);
		canvas.rotate(-(180 - rotationUser));

		Path pathVisible = new Path();
		pathVisible.setLastPoint(0, 0);
		pathVisible.arcTo(new RectF(-radius, -radius, radius, radius),
				90 - angleView / 2, angleView);
		pathVisible.lineTo(0, 0);

		Path pathNonVisible = new Path();
		pathNonVisible.setLastPoint(0, 0);
		pathNonVisible.arcTo(new RectF(-radius, -radius, radius, radius),
				90 + angleView / 2, 360 - angleView);
		pathNonVisible.lineTo(0, 0);

		Paint paintvisible = new Paint();
		paintvisible.setStyle(Style.FILL);

		paintvisible.setColor(greenPoint);
		paintvisible.setAlpha(90);
		canvas.drawPath(pathVisible, paintvisible);

		paintvisible.setColor(green);
		paintvisible.setAlpha(140);
		canvas.drawPath(pathNonVisible, paintvisible);

		canvas.drawCircle(0, 0, radius, contour);

		canvas.rotate(-directionUser);
		if ((projector.getCurLocation() != null)) {
			for (Poi poi : projector.getPoiList()) {
				Point point = getPointFromAngle(poi.getAngleWithNorth(),
						poi.getDistance(), radius, pixelByLength);
				canvas.drawPoint(point.x, point.y, pointPaint);
			}
		}

	}

	/**
	 * Gets the point from angle.
	 *
	 * @param angle the angle
	 * @param length the length
	 * @param radius the radius
	 * @param pixelByLength the pixel by length
	 * @return the point from angle
	 */
	private Point getPointFromAngle(double angle, double length, int radius,
			double pixelByLength) {
		angle = Math.toRadians(angle);
		double lengthPixel;
		if (length == -1) {
			lengthPixel = radius;
		} else {
			lengthPixel = length * pixelByLength;
		}
		double x = Math.sin(angle) * lengthPixel;
		double y = Math.cos(angle) * lengthPixel;
		return new Point(-(int) x, (int) y);

	}
}
