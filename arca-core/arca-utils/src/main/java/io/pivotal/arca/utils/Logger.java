/* 
 * Copyright (C) 2014 Pivotal Software, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.arca.utils;

import android.os.Looper;
import android.util.Log;

import java.util.Locale;

public enum Logger {
	INSTANCE;

	public static interface Level {
		public static final int ERROR = 0;
		public static final int WARNING = 1;
		public static final int INFO = 2;
		public static final int DEBUG = 3;
		public static final int VERBOSE = 4;
	}

	private static int sLogLevel = Level.VERBOSE;

	private static final String UI_THREAD = "UI";
	private static final String BG_THREAD = "BG";

	private static boolean sIsDebuggable = false;
	private static String sTagName = "Logger";

	private Logger() {
	}

    public static void trace() {
        setup(true, "TRACE");
    }

	public static void setup(final boolean isDebuggable, final String tagName) {
		sIsDebuggable = isDebuggable;
		sTagName = tagName;
	}

	public static void i(final String message, final Object... objects) {
		if (sIsDebuggable && sLogLevel >= Level.INFO) {
			final String formattedString = formatMessage(message, objects);
			Log.i(sTagName, formattedString);
		}
	}

	public static void info(final String message, final Object... objects) {
		i(message, objects);
	}

	public static void w(final String message, final Object... objects) {
		if (sIsDebuggable && sLogLevel >= Level.WARNING) {
			final String formattedString = formatMessage(message, objects);
			Log.w(sTagName, formattedString);
		}
	}

	public static void warning(final String message, final Object... objects) {
		w(message, objects);
	}

	public static void v(final String message, final Object... objects) {
		if (sIsDebuggable && sLogLevel >= Level.VERBOSE) {
			final String formattedString = formatMessage(message, objects);
			Log.v(sTagName, formattedString);
		}
	}

	public static void verbose(final String message, final Object... objects) {
		v(message, objects);
	}

	public static void d(final String message, final Object... objects) {
		if (sIsDebuggable && sLogLevel >= Level.DEBUG) {
			final String formattedString = formatMessage(message, objects);
			Log.d(sTagName, formattedString);
		}
	}

	public static void debug(final String message, final Object... objects) {
		d(message, objects);
	}

	public static void e(final String message, final Object... objects) {
		if (sIsDebuggable && sLogLevel >= Level.ERROR) {
			final String formattedString = formatMessage(message, objects);
			Log.e(sTagName, formattedString);
		}
	}

	public static void error(final String message, final Object... objects) {
		e(message, objects);
	}

	public static void ex(final String message, final Throwable tr) {
		final String stackTrace = getMessageFromThrowable(tr);
		final String formattedMessage = formatMessage(message, new Object[] {});
		final String formattedString = String.format("%s : %s", formattedMessage, stackTrace);
		Log.w(sTagName, formattedString);
	}

	private static String getMessageFromThrowable(final Throwable tr) {
		final String stackTrace = Log.getStackTraceString(tr);
		if (stackTrace == null || stackTrace.length() == 0) {
			return tr.getLocalizedMessage();
		} else {
			return stackTrace;
		}
	}

	public static void exception(final String message, final Throwable tr) {
		ex(message, tr);
	}

	public static void ex(final Throwable tr) {
		ex("", tr);
	}

	public static void exception(final Throwable tr) {
		ex(tr);
	}

	// ========================================

	private static boolean isUiThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	private static String formatMessage(final String message, final Object... objects) {
		if (objects.length > 0) {
			return formatMessage(String.format(Locale.getDefault(), message, objects));
		} else {
			return formatMessage(message);
		}
	}

	private static String formatMessage(final String message) {
		final String threadInfo = formatThreadInfo();
		final String stackTraceInfo = formatStackTraceInfo();

		return String.format(Locale.getDefault(), "*%s* [%s] %s", threadInfo, stackTraceInfo, message);
	}

	private static String formatThreadInfo() {
		final String threadName = isUiThread() ? UI_THREAD : BG_THREAD;
		final long threadId = Thread.currentThread().getId();

		return String.format(Locale.getDefault(), "%s:%d", threadName, threadId);
	}

	private static String formatStackTraceInfo() {
		final StackTraceElement element = getCallingStackTraceElement();
		final String className = element.getClassName();
		final String methodName = element.getMethodName();
		final int lineNumber = element.getLineNumber();

		return String.format(Locale.getDefault(), "%s:%s:%d", className, methodName, lineNumber);
	}

	private static StackTraceElement getCallingStackTraceElement() {

		final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		final String loggerClassName = INSTANCE.getClass().getName();

		boolean foundLogger = false;
        for (final StackTraceElement element : elements) {
            // Scan down the list until we find the Logger itself
            if (!foundLogger) {
                if (element.getClassName().equalsIgnoreCase(loggerClassName)) {
                    foundLogger = true;
                }
                continue;
            }

            // After finding the Logger, look for the first class that isn't the
            // logger -- that's the class that called the Logger!
            if (!element.getClassName().equalsIgnoreCase(loggerClassName)) {
                return element;
            }
        }
		return null;
	}
}