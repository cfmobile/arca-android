/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.dispatcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.pivotal.arca.broadcaster.ArcaBroadcastManager;

public class ErrorBroadcasterTest extends AndroidTestCase {

	private static final Uri URI = Uri.parse("content://empty");

	private static final int ERROR_CODE = 100;
	private static final String ERROR_MESSAGE = "error message";

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final Looper looper = getContext().getMainLooper();
		final TestBroadcastHandler handler = new TestBroadcastHandler(looper);

		ArcaBroadcastManager.initialize(getContext(), handler);
	}

	public void testErrorBroadcast() {
		final AssertionLatch latch = new AssertionLatch(1);
		final ErrorReceiver receiver = new ErrorReceiver(new ErrorListener() {

			@Override
			public void onRequestError(final io.pivotal.arca.dispatcher.Error error) {
				latch.countDown();

				assertEquals(ERROR_CODE, error.getCode());
				assertEquals(ERROR_MESSAGE, error.getMessage());
			}
		});
		receiver.register(URI);
		ErrorBroadcaster.broadcast(getContext(), URI, ERROR_CODE, ERROR_MESSAGE);
		latch.assertComplete();
	}

	public void testErrorFromIntent() {
		final Error error = new Error(ERROR_CODE, ERROR_MESSAGE);
		final Intent intent = new Intent(URI.toString());
		intent.putExtra(ErrorBroadcaster.Extras.ERROR, error);
		final Error actual = ErrorBroadcaster.getError(intent);
		assertEquals(error, actual);
	}

	public void testNullErrorFromIntent() {
		final Error error = ErrorBroadcaster.getError(null);
		assertNull(error);
	}

	// ============================================

	private static class TestBroadcastHandler extends ArcaBroadcastManager.BroadcastHandler {

		public TestBroadcastHandler(final Looper looper) {
			super(looper);
		}

		@Override
		public boolean sendMessageAtTime(final Message msg, final long uptimeMillis) {
			handleMessage(msg);
			return true;
		}
	}

	// ============================================

	public class AssertionLatch extends CountDownLatch {

		public AssertionLatch(final int count) {
			super(count);
		}

		@Override
		public void countDown() {
			final long count = getCount();
			if (count == 0) {
				Assert.fail("This latch has already finished.");
			} else {
				super.countDown();
			}
		}

		public void assertComplete() {
			try {
				Assert.assertTrue(await(0, TimeUnit.SECONDS));
			} catch (final InterruptedException e) {
				Assert.fail();
			}
		}
	}

}
