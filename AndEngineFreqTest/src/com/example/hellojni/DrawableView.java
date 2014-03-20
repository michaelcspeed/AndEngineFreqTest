package com.example.hellojni;

import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;

import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.example.hellojni.PitchDetector.FreqResult;

public class DrawableView extends View {

	private FreqResult fr_;
	private Handler handler_;
	private Timer timer_;

	private void initView() {

		fr_ = new FreqResult();

		// UI update cycle.
		handler_ = new Handler();
		timer_ = new Timer();
		timer_.schedule(new TimerTask() {
			public void run() {
				handler_.post(new Runnable() {
					public void run() {
						invalidate();
					}
				});
			}
		}, UI_UPDATE_MS, UI_UPDATE_MS);
	}

	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public DrawableView(Context context) {
		super(context);

		initView();
	}

	/**
	 * Construct object, initializing with any attributes we understand from a
	 * layout file. These attributes are defined in
	 * SDK/assets/res/any/classes.xml.
	 * 
	 * @see android.view.View#View(android.content.Context,
	 *      android.util.AttributeSet)
	 */

	public DrawableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

	}

	private final static int UI_UPDATE_MS = 50;
	public double FFTPerSecond = 0;

	protected void drawDebug(Canvas canvas) {
		final int alpha = 255;
		Paint paint = new Paint();
		paint.setARGB(alpha, 200, 200, 250);
		paint.setTextSize(10);
		int skip = ((HelloJni) getContext()).pd_.skip;
		canvas.drawText("FFT per second: " + FFTPerSecond, 30, 200, paint); // +
																			// " skip: "
																			// +
																			// skip,
	}

	protected void onDraw(Canvas canvas) {
		final int MARGIN = 20;
		final int effective_height = canvas.getHeight() - 4 * MARGIN;
		final int effective_width = canvas.getWidth() - 2 * MARGIN;

		final Rect histogramRect = new Rect(MARGIN, effective_height * 60 / 100
				+ 2 * MARGIN, effective_width + MARGIN, effective_height
				- MARGIN);

		Histogram.drawHistogram(canvas, histogramRect, fr_);
		if (fr_.isPitchDetected) {
			PitchDrawings.drawPitchPrecision(canvas, fr_.bestFrequency);
			PitchDrawings.drawCurrentFrequency(canvas, 30, 50,
					fr_.bestFrequency);
		}

		drawDebug(canvas);
	}

	public void setDetectionResults(FreqResult fr) {
		fr_ = fr;
	}

}