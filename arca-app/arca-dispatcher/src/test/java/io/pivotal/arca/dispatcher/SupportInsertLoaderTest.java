/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.dispatcher;

import android.test.AndroidTestCase;

public class SupportInsertLoaderTest extends AndroidTestCase {

	public void testErrorResult() {
		final io.pivotal.arca.dispatcher.Error error = new Error(100, "message");
		final SupportInsertLoader loader = new SupportInsertLoader(getContext(), null, null);
		final InsertResult result = loader.getErrorResult(error);

		assertTrue(result.hasError());
		assertEquals(error, result.getError());
	}

}
