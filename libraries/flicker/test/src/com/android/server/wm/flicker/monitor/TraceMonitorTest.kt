/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.server.wm.flicker.monitor

import androidx.test.platform.app.InstrumentationRegistry
import com.android.server.wm.flicker.FlickerRunResult
import com.android.server.wm.flicker.helpers.getDefaultFlickerOutputDir
import com.google.common.io.Files
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Test
import java.nio.file.Path

abstract class TraceMonitorTest<T : TraceMonitor> {
    lateinit var savedTrace: Path
    abstract fun getMonitor(outputDir: Path): T
    abstract fun assertTrace(traceData: ByteArray)
    abstract fun getTraceFile(result: FlickerRunResult): Path?

    private val traceMonitor by lazy {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val outputDir = getDefaultFlickerOutputDir(instrumentation)
        getMonitor(outputDir)
    }

    @After
    fun teardown() {
        traceMonitor.stop()
        if (::savedTrace.isInitialized) {
            savedTrace.toFile().delete()
        }
    }

    @Test
    @Throws(Exception::class)
    fun canStartLayersTrace() {
        traceMonitor.start()
        Truth.assertThat(traceMonitor.isEnabled).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun canStopLayersTrace() {
        traceMonitor.start()
        Truth.assertThat(traceMonitor.isEnabled).isTrue()
        traceMonitor.stop()
        Truth.assertThat(traceMonitor.isEnabled).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun captureLayersTrace() {
        traceMonitor.start()
        traceMonitor.stop()
        val builder = FlickerRunResult.Builder(0)
        traceMonitor.save("capturedTrace", builder)
        savedTrace = getTraceFile(builder.build()) ?: error("Could not find saved trace file")
        val testFile = savedTrace.toFile()
        Truth.assertThat(testFile.exists()).isTrue()
        val calculatedChecksum = TraceMonitor.calculateChecksum(savedTrace)
        Truth.assertThat(calculatedChecksum).isEqualTo(traceMonitor.checksum)
        val trace = Files.toByteArray(testFile)
        Truth.assertThat(trace.size).isGreaterThan(0)
        assertTrace(trace)
    }
}