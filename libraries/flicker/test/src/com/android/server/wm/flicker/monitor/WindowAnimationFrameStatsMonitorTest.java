/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.server.wm.flicker.monitor;

import android.app.Instrumentation;
import android.platform.helpers.IAppHelper;

import androidx.test.filters.FlakyTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.android.server.wm.flicker.StandardAppHelper;
import com.android.server.wm.flicker.helpers.AutomationUtilsKt;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Contains {@link WindowAnimationFrameStatsMonitor} tests. To run this test: {@code atest
 * FlickerLibTest:WindowAnimationFrameStatsMonitorTest}
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FlakyTest
public class WindowAnimationFrameStatsMonitorTest {
    private WindowAnimationFrameStatsMonitor mWindowAnimationFrameStatsMonitor;
    private Instrumentation mInstrumentation;

    @Before
    public void setup() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mWindowAnimationFrameStatsMonitor = new WindowAnimationFrameStatsMonitor(mInstrumentation);
        AutomationUtilsKt.wakeUpAndGoToHomeScreen(UiDevice.getInstance(mInstrumentation));
    }

    @Test
    public void captureWindowAnimationFrameStats() {
        mWindowAnimationFrameStatsMonitor.start();
        IAppHelper webViewBrowserHelper =
                new StandardAppHelper(
                        mInstrumentation,
                        /* packageName */ "org.chromium.webview_shell",
                        /* launcherName */ "WebView Browser Tester");
        webViewBrowserHelper.open();
        webViewBrowserHelper.exit();
        mWindowAnimationFrameStatsMonitor.stop();
    }
}
