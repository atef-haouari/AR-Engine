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

import java.text.NumberFormat;

import org.arengine.engine.Poi;
import org.arengine.engine.PoiPainter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

/**
 * The Class DefaultPainter.
 */
public class DefaultPainter implements PoiPainter {

	/* (non-Javadoc)
	 * @see org.arengine.engine.PoiPainter#draw(org.arengine.engine.Poi, android.graphics.Canvas)
	 */
	public RectF draw(Poi poi, Canvas canvas) {
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		canvas.drawBitmap(poi.getIcon(), -poi.getIcon().getWidth() / 2, -poi
				.getIcon().getHeight() / 2, mPaint);
		float widthRect = poi.getIcon().getWidth();
		float heightRect = widthRect * 0.3f;

		RectF touchableRect = new RectF(-poi.getIcon().getWidth() / 2, -poi
				.getIcon().getHeight() / 2, poi.getIcon().getWidth() / 2, poi
				.getIcon().getHeight() / 2);

		canvas.translate(0, poi.getIcon().getHeight() / 2 + 2);

		Paint painTransparent = new Paint();
		painTransparent.setColor(Color.rgb(100, 100, 100));
		painTransparent.setStyle(Style.FILL);
		painTransparent.setAlpha(50);
		canvas.drawRect(new RectF(-widthRect / 2, 0f, +widthRect / 2,
				heightRect * 1f), painTransparent);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.RED);
		textPaint.setTextSize(20);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setAntiAlias(true);

		float textHeight = textPaint.getTextSize();

		String distance = "";
		if (poi.getDistance() < 1000) {
			distance = ((int) (poi.getDistance())) + " m";
		} else {
			if (poi.getDistance()/1000 >= 10) {
				distance = ((int) (poi.getDistance()/1000)) + " km";
			} else {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				distance = nf.format(poi.getDistance()/1000) + " km";
			}
		}
		canvas.drawText(distance, 0, (heightRect / 2) + (textHeight / 2),
				textPaint);

		return touchableRect;

	}
}
