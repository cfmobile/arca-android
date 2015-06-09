/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.service;

import android.content.Context;

import io.pivotal.arca.service.Task;
import io.pivotal.arca.threading.Identifier;

public class TestTask extends Task<String> {

	public static interface Messages extends Task.Messages {
	}

	private final Identifier<?> mIdentifier;
	private final Exception mNetworkingException;
	private final Exception mProcessingException;
	private final String mNetworkResult;
	private Task<?> mDependency;

	public TestTask(final Identifier<?> identifier) {
		this(identifier, null, null, null);
	}

	public TestTask(final Identifier<?> identifier, final Task<?> task) {
		this(identifier, null, null, null);
		mDependency = task;
	}

	public TestTask(final Identifier<?> identifier, final String networkResult) {
		this(identifier, networkResult, null, null);
	}

	public TestTask(final Identifier<?> identifier, final String networkResult, final Exception networkingException, final Exception processingException) {
        if (identifier == null) {
            throw new IllegalStateException("Identifier cannot be null.");
        }
		mIdentifier = identifier;
		mNetworkResult = networkResult;
		mNetworkingException = networkingException;
		mProcessingException = processingException;
	}

	@Override
	public Identifier<?> onCreateIdentifier() {
		return mIdentifier;
	}

	@Override
	public String onExecuteNetworking(final Context context) throws Exception {

		if (mNetworkingException != null) {
			throw mNetworkingException;
		}

		if (mDependency != null) {
			addDependency(mDependency);
		}

		return mNetworkResult;
	}

	@Override
	public void onExecuteProcessing(final Context context, final String data) throws Exception {
		if (mProcessingException != null) {
			throw mProcessingException;
		}
	}
}
