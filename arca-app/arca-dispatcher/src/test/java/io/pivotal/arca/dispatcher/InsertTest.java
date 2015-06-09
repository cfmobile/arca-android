/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.dispatcher;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcel;
import android.test.AndroidTestCase;

import io.pivotal.arca.dispatcher.Insert;

import java.util.Arrays;

public class InsertTest extends AndroidTestCase {

	public void testInsertParcelableDescribeContents() {
		final Uri uri = Uri.parse("content://empty");
		final ContentValues values = new ContentValues();
		final Insert request = new Insert(uri, values);
		assertEquals(0, request.describeContents());
	}

	public void testInsertParcelableCreatorArray() {
		final Insert[] request = Insert.CREATOR.newArray(1);
		assertEquals(1, request.length);
	}

	public void testInsertParcelableCreator() {
		final Uri uri = Uri.parse("content://empty");
		final ContentValues[] values = { new ContentValues() };

		final Insert request = new Insert(uri, values);

		final Parcel parcel = Parcel.obtain();
		request.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);

		final Insert parceled = Insert.CREATOR.createFromParcel(parcel);
		assertEquals(uri, parceled.getUri());
		assertTrue(Arrays.deepEquals(values, parceled.getContentValues()));

		parcel.recycle();
	}
}
