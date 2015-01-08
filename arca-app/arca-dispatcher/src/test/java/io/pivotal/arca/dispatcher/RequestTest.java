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
package io.pivotal.arca.dispatcher;

import android.net.Uri;
import android.test.AndroidTestCase;

import io.pivotal.arca.dispatcher.Request;

import junit.framework.Assert;

public class RequestTest extends AndroidTestCase {

	public void testRequestWithNullUriThrowsException() {
		try {
			new TestRequest(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testRequestWithNullUriAndIdentifierThrowsException() {
		try {
			new TestRequest(null, -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testRequestWithIdentifierLessThan1000ThrowsException() {
		try {
			new TestRequest(Uri.EMPTY, 999);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testRequestWithIdentifierof1000orMoreSucceeds() {
		assertNotNull(new TestRequest(Uri.EMPTY, 1000));
	}

	public void testRequestWithDefaultIdentifierSucceeds() {
		assertNotNull(new TestRequest(Uri.EMPTY));
	}

	private static final class TestRequest extends Request<Object> {

		public TestRequest(final Uri uri) {
			super(uri);
		}

		public TestRequest(final Uri uri, final int identifier) {
			super(uri, identifier);
		}

	}

}
