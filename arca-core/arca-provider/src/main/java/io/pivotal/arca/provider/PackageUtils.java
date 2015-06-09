/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.provider;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {

	public static int getVersionCode(final Context context) {
		final String packageName = getPackageName(context);
		return getVersionCode(context, packageName);
	}

	public static int getVersionCode(final Context context, final String packageName) {
		try {
			return context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (final NameNotFoundException e) {
			return -1;
		}
	}

	public static String getPackageName(final Context context) {
		return context.getPackageName();
	}

}
