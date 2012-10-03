package org.arengine.samples.worldplaces;

import org.arengine.engine.Poi;
import org.arengine.engine.PoiPainter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class TextOnlyPoiPainter implements PoiPainter {

	private Paint paint;

	TextOnlyPoiPainter() {
		paint = new Paint();
		paint.setTextSize(50);
		paint.setColor(Color.GREEN);
		paint.setAntiAlias(true);
	}

	@Override
	public RectF draw(Poi poi, Canvas canvas) {
		canvas.drawText(poi.getTitle(), 0, 0,paint);
		paint.measureText(poi.getTitle());

		Rect touchableRect = new Rect();
		paint.getTextBounds(poi.getTitle(), 0, poi.getTitle().length(), touchableRect);

		return new RectF(touchableRect);
	}

}
