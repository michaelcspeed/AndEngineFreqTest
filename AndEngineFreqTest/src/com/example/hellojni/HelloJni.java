/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import java.lang.Thread;
import java.util.HashMap;

import com.example.hellojni.DrawableView;
import com.example.hellojni.PitchDetector;
import com.example.hellojni.PitchDetector.FreqResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

public class HelloJni extends Activity {
        
        public DrawableView tv_;
        public Thread pitch_detector_thread_;
        public PitchDetector pd_;
        public GuiPitchListener gpl_;
        
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);
                tv_ = (DrawableView) findViewById(R.id.drawview);
                
                // manually invoking the view and thus ignoring layout/main.xml
                //tv_ = new DrawableView(this);
                //setContentView(tv_);
        }
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
           // MenuInflater inflater = getMenuInflater();
           // inflater.inflate(R.menu.tuner_menu, menu);
            return true;
        }
        
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
               /* switch (item.getItemId()) {
                case R.id.project_page:
                        Intent i = new Intent(Intent.ACTION_VIEW);  
                        i.setData(Uri.parse("http://code.google.com/p/androidtuner/"));  
                        startActivity(i);
                        break;
                case R.id.recalibrate_noise:
                        pd_.resetNoiseLevel();
                        break;
                }*/
                return true;
        }
        
        public class GuiPitchListener implements PitchDetector.PitchListener {
                private HelloJni parent_;
                private Handler handler_;
                
                public GuiPitchListener(HelloJni parent, Handler handler) {
                        parent_ = parent;
                        handler_ = handler;                     
                }
                
                public void PostToUI(final FreqResult fr) {
                        handler_.post(new Runnable() {
                                public void run() {
                                        parent_.ShowPitchDetectionResult(fr);
                                        tv_.FFTPerSecond = parent_.pd_.FFTPerSecond; 
                                }
                        });
                }

                private void ShowError(final String msg) {
                        handler_.post(new Runnable() {
                                public void run() {
                                        new AlertDialog.Builder(parent_).setTitle("Android Tuner Error")
                                                        .setMessage(msg)
                                                        .setTitle("Error")
                                                        .setPositiveButton("OK", null)
                                                        .show();
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
        
        @Override
        public void onStart() {
                super.onStart();
                gpl_ = new GuiPitchListener(this, new Handler());
                pd_ = new PitchDetector(gpl_);
                pitch_detector_thread_ = new Thread(pd_);
                pitch_detector_thread_.start();
                
                setKeepScreenOn(this, true);
        }

        @Override
        public void onStop() {
                super.onStop();
                pitch_detector_thread_.interrupt();
                setKeepScreenOn(this, false);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        public void ShowPitchDetectionResult(FreqResult fr) {
                tv_.setDetectionResults(fr);
        }
        
        
        
        public void setKeepScreenOn(Activity activity, boolean keepScreenOn) {
                if (keepScreenOn) {
                        activity.getWindow().addFlags(
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                        activity.getWindow().clearFlags(
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
        }
        
        
}
