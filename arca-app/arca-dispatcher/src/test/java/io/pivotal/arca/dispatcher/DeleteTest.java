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
import android.os.Parcel;
import android.test.AndroidTestCase;

import io.pivotal.arca.dispatcher.Delete;

import java.util.Arrays;

public class DeleteTest extends AndroidTestCase {

	public void testDeleteParcelableDescribeContents() {
		final Uri uri = Uri.parse("content://empty");
		final Delete request = new Delete(uri);
		assertEquals(0, request.describeContents());
	}

	public void testDeleteParcelableCreatorArray() {
		final Delete[] request = Delete.CREATOR.newArray(1);
		assertEquals(1, request.length);
	}

	public void testDeleteParcelableCreator() {
		final Uri uri = Uri.parse("content://empty");
		final String where = "test = ?";
		final String[] whereArgs = { "true" };

		final Delete request = new Delete(uri);
		request.setWhere(where, whereArgs);

		final Parcel parcel = Parcel.obtain();
		request.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);

		final Delete parceled = Delete.CREATOR.createFromParcel(parcel);
		assertEquals(uri, parceled.getUri());
		assertEquals(where, parceled.getWhereClause());
		assertTrue(Arrays.deepEquals(whereArgs, parceled.getWhereArgs()));

		parcel.recycle();
	}
}
