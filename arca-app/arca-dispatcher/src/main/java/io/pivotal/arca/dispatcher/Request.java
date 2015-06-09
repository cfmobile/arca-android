/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.dispatcher;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public abstract class Request<T> implements Parcelable {

	private static int sID = 0;

	private final int mIdentifier;
	private final Uri mUri;

	public Request(final Uri uri, final int identifier) {
		if (uri == null) {
			throw new IllegalArgumentException("The uri cannot be null.");
		}

		if (identifier < 1000) {
			throw new IllegalArgumentException("Custom identifiers cannot be less than 1000.");
		}

		mIdentifier = identifier;
		mUri = uri;
	}

	public Request(final Uri uri) {
		if (uri == null) {
			throw new IllegalArgumentException("The uri cannot be null.");
		}
		mIdentifier = sID++;
		mUri = uri;
	}

	public Request(final Parcel in) {
		mIdentifier = in.readInt();
		mUri = in.readParcelable(Uri.class.getClassLoader());
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(mIdentifier);
		dest.writeParcelable(mUri, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getIdentifier() {
		return mIdentifier;
	}

	public Uri getUri() {
		return mUri;
	}
}
