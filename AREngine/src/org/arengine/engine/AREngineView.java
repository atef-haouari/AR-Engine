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

import java.util.Arrays;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Atef Haouari
 * 
 *         This view draws the projection of the poi. The method on draw is
 *         called when sensors values changed.
 */
public final class AREngineView extends View implements OnTouchListener {

	/**
	 * Indicates if the view is touched.
	 */
	private boolean touched = false;

	/**
	 * The x of the touched point.
	 */
	private float touchedX = 0;

	/**
	 * The y of the touched point.
	 */
	private float touchedY = 0;

	/**
	 * The projector
	 */
	private Projector projector;

	/**
	 * Gets the projector
	 * 
	 * @return The projector
	 */
	protected Projector getProjector() {
		return projector;
	}

	/**
	 * Sets the projector
	 * 
	 * @param projector
	 *            The projector
	 */
	protected void setProjector(Projector projector) {
		this.projector = projector;
	}

	/**
	 * the AREngineActivity
	 */
	private AREngineActivity arEngineActivity;

	/**
	 * Constructor of the AREngineView
	 * 
	 * @param aREngineActivity
	 *            the AREngineActivity
	 */
	protected AREngineView(AREngineActivity arEngineActivity) {
		super(arEngineActivity, null);
		this.arEngineActivity = arEngineActivity;
		setOnTouchListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		if (projector != null) {
			// Refresh first the projection values
			projector.refresh(Arrays.asList(arEngineActivity.getPoiList()),
					getWidth(), getHeight(),
					arEngineActivity.isFilterDistanceEnabled(),
					arEngineActivity.getMaxDistanceFilter());

			// Draw the custom overlays
			for (CustomOverlay overlay : arEngineActivity.getOverlays()) {
				int savedCount = canvas.save();
				overlay.draw(canvas, projector);
				canvas.restoreToCount(savedCount);
			}

			// Do translation an rotation calculated by the projector
			canvas.rotate((float) projector.getRotation());
			canvas.translate((float) projector.getTranslationX(),
					(float) projector.getTranslationY());

			// Draw the poi projections
			Location curLocation = projector.getCurLocation();
			PoiPainter poiPainter = arEngineActivity.getPoiPainter();

			if (poiPainter == null) {
				poiPainter = arEngineActivity.getDefaultPoiPainter();
			}

			if (curLocation != null && poiPainter != null) {
				for (Poi poi : projector.getPoiList()) {
					int count = canvas.save();

					// Go to the projection point and call the PoiPainter to
					// draw
					// it.
					canvas.translate(poi.getProjectionPoint().x,
							poi.getProjectionPoint().y);
					int countLocal = canvas.save();
					RectF touchableRect = poiPainter.draw(poi, canvas);

					canvas.restoreToCount(countLocal);
					// When the event is touched
					if (touched && touchableRect != null) {
						Matrix matrix = canvas.getMatrix();
						RectF touchableRectMapped = new RectF();
						matrix.mapRect(touchableRectMapped, touchableRect);
						// call the action onTouch if a poi is touched
						if (touchableRectMapped.contains(touchedX, touchedY)) {
							if (arEngineActivity.getPoiTouchListener() != null) {
								arEngineActivity.getPoiTouchListener().onTouch(
										poi);
							}
							touched = false;
						}
					}

					canvas.restoreToCount(count);

				}
			}

		}
		touched = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	public boolean onTouch(View v, MotionEvent event) {

		touchedX = event.getRawX();
		touchedY = event.getRawY();
		this.touched = true;
		// redraw the force the PoiTouchListener verification
		this.invalidate();
		return false;
	}

}
