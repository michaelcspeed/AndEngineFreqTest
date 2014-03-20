package com.example.hellojni;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.hellojni.PitchDetector.FreqResult;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */
public class LineExample extends SimpleBaseGameActivity {

	public Thread pitch_detector_thread_;
	public PitchDetector pd_;
	public GuiPitchListener gpl_;
	double FFTPerSecond = 0;
	FreqResult fr;

	// ===========================================================
	// Constants
	// ===========================================================

	/*
	 * Initializing the Random generator produces a comparable result over
	 * different versions.
	 */
	private static final long RANDOM_SEED = 1234567890;

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private static final int LINE_COUNT = 100;
	
	Handler h = new Handler();

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {

	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		gpl_ = new GuiPitchListener(this, h);
		pd_ = new PitchDetector(gpl_);
		pitch_detector_thread_ = new Thread(pd_);
		pitch_detector_thread_.start();

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void ShowPitchDetectionResult(FreqResult fr) {
		Log.d("music!", fr.bestFrequency + "Hz!!!!!!");
		this.setDetectionResults(fr);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private void setDetectionResults(FreqResult fr) {
		this.fr = fr;
	}

	public class GuiPitchListener implements PitchDetector.PitchListener {
		private LineExample parent_;
		private Handler handler_;

		public GuiPitchListener(LineExample parent, Handler handler) {

			parent_ = parent;
			handler_ = handler;
		}

		public void PostToUI(final FreqResult fr) {
			handler_.post(new Runnable() {
				public void run() {
					parent_.ShowPitchDetectionResult(fr);
					FFTPerSecond = pd_.FFTPerSecond;
				}
			});
		}

		private void ShowError(final String msg) {
			handler_.post(new Runnable() {
				public void run() {
					Log.d("music!", "Error!!! " + msg);
				}
			});
		}

		@Override
		public void onAnalysis(FreqResult fr) {
			PostToUI(fr);
		}

		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub
			ShowError(error);
		}
	}

}