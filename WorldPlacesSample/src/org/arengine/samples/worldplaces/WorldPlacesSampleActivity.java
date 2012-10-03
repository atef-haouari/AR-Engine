package org.arengine.samples.worldplaces;

import org.arengine.engine.AREngineActivity;
import org.arengine.engine.CaptureCallback;
import org.arengine.engine.OnPoiTouchListener;
import org.arengine.engine.Poi;
import org.arengine.engine.PoiPainter;

import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WorldPlacesSampleActivity extends AREngineActivity {

	@Override
	public void onAddPoiList() {

		this.addPoi(new Poi("Eiffel tower", BitmapFactory.decodeResource(
				getResources(), R.drawable.eiffel_tower_icon), 2.294527,
				48.858272));
		this.addPoi(new Poi("Colosseum", BitmapFactory.decodeResource(
				getResources(), R.drawable.colosseum_icon), 12.492181,
				41.890303));
		this.addPoi(new Poi("Statut of liberty", BitmapFactory.decodeResource(
				getResources(), R.drawable.liberty_icon), -74.04464, 40.689272));
		this.addPoi(new Poi("Egypt pyramids", BitmapFactory.decodeResource(
				getResources(), R.drawable.egypt_icon), 31.1325, 29.977662));
		this.addPoi(new Poi("Taj Mahal", BitmapFactory.decodeResource(
				getResources(), R.drawable.tajmahal_icon), 78.042159, 27.175196));
		this.addPoi(new Poi("Big Ben", BitmapFactory.decodeResource(
				getResources(), R.drawable.bigben_icon), -0.124635, 51.500768));

	}

	@Override
	public void onInitAREngine() {
		// enableFilterDistance();
		// setMaxDistanceFilter(20000);

		this.setPoiTouchListener(new OnPoiTouchListener() {

			public void onTouch(Poi poi) {
				Toast.makeText(WorldPlacesSampleActivity.this, poi.getTitle(),
						Toast.LENGTH_SHORT).show();
			}
		});

		final CaptureCallback captureCallBack = new SaveToFileCaptureCallback(
				this);
		Button captureButton = ((Button) findViewById(R.id.capture_button));
		captureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				captrue(captureCallBack);
			}
		});

		ToggleButton textOnlyToggle = ((ToggleButton) findViewById(R.id.textOnly_toggle));
		final PoiPainter textOnlyPoiPainter = new TextOnlyPoiPainter();
		textOnlyToggle
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean checked) {
						if (checked) {
							setPoiPainter(textOnlyPoiPainter);
						} else {
							setPoiPainter(null);
						}

					}
				});

		ToggleButton showSonarToggle = ((ToggleButton) findViewById(R.id.showSonar_toggle));

		showSonarToggle
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean checked) {
						if (checked) {
							addSonarOverlay();
						} else {
							removeSonarOverlay();
						}

					}
				});

	}

	@Override
	public int getCustomLayoutId() {
		return R.layout.main;
	}

	@Override
	public void onAddOverlays() {
		addOverlay(new InfoOverlay());
	}
}