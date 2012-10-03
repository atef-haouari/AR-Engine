package org.arengine.samples.worldplaces;

import org.arengine.engine.CustomOverlay;
import org.arengine.engine.Projector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class InfoOverlay implements CustomOverlay {

	private Paint paint = new Paint();

	public InfoOverlay() {

		paint.setAntiAlias(true);
		paint.setTextSize(20);
		paint.setColor(Color.CYAN);
	}

	@Override
	public void draw(Canvas canvas, Projector projector) {
		canvas.drawText("Powred by AREngine", 0, 20, paint);
	}

}
