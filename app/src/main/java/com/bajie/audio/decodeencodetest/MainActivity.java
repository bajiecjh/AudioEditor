/*
 * Copyright (C) 2016 Martin Storsjo
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

package com.bajie.audio.decodeencodetest;

import android.os.Bundle;
import android.app.Activity;

import com.bajie.audio.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        new Thread() {
            public void run() {
                ExtractDecodeEditEncodeMuxTest test = new ExtractDecodeEditEncodeMuxTest();
                test.setContext(MainActivity.this);
                try {
                    test.testExtractDecodeEditEncodeMuxAudioVideo();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }.start();
    }
}
