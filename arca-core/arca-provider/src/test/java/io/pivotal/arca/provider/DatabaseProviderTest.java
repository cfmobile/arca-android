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
package io.pivotal.arca.provider;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ProviderTestCase2;

import junit.framework.Assert;

import java.util.ArrayList;

import io.pivotal.arca.provider.DatabaseConfiguration.DefaultDatabaseConfiguration;
import io.pivotal.arca.provider.DatabaseProviderTest.TestDatabaseProvider;
import io.pivotal.arca.provider.DatabaseProviderTest.TestDatabaseProvider.Uris;

public class DatabaseProviderTest extends ProviderTestCase2<TestDatabaseProvider> {

	public DatabaseProviderTest() {
		super(TestDatabaseProvider.class, TestDatabaseProvider.AUTHORITY);
	}

	public void testDatabaseOpened() {
		final TestDatabaseProvider provider = getProvider();
		final SQLiteDatabase database = provider.getDatabase();
		assertTrue(database.isOpen());
	}

	public void testDatabaseClosed() {
		final TestDatabaseProvider provider = getProvider();
		final SQLiteDatabase database = provider.getDatabase();
		provider.closeDatabase();
		assertTrue(!database.isOpen());
	}

	public void testDatasetIsGivenTheDatabase() {
		final TestDatabaseProvider provider = getProvider();
		final Dataset dataset = provider.getDatasetOrThrowException(Uris.URI_1);
		final SQLiteDatabase database = ((TestTable1) dataset).getDatabase();
		assertNotNull(database);
	}

	public void testDatabaseApplyBatchNullOperations() {
		try {
			getProvider().applyBatch(null);
			Assert.fail();
		} catch (final Exception e) {
			assertNotNull(e);
		}
	}

	public void testDatabaseApplyBatchEmptyOperations() {
		try {
			final ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			getProvider().applyBatch(operations);
		} catch (final Exception e) {
			Assert.fail();
		}
	}

	public void testDatabaseHasDefaultConfiguration() {
		final DatabaseProvider provider = new DatabaseProvider() {
			@Override
			public boolean onCreate() {
				return true;
			}
		};
		final DatabaseConfiguration config = provider.onCreateDatabaseConfiguration();
		assertNotNull(config);
	}

	// ==================================

	public static class TestDatabaseProvider extends DatabaseProvider {

		public static final String AUTHORITY = "com.test.authority";

		private static final class Paths {
			public static final String PATH_1 = "path1";
			public static final String PATH_2 = "path2";
		}

		public static final class Uris {
			public static final Uri URI_1 = Uri.parse("content://" + AUTHORITY + "/" + Paths.PATH_1);
			public static final Uri URI_2 = Uri.parse("content://" + AUTHORITY + "/" + Paths.PATH_2);
		}

		@Override
		public DatabaseConfiguration onCreateDatabaseConfiguration() {
			return new TestDatabaseConfiguration(getContext());
		}

		@Override
		public boolean onCreate() {
			registerDataset(AUTHORITY, Paths.PATH_1, TestTable1.class);
			registerDataset(AUTHORITY, Paths.PATH_2, TestTable2.class);
			return true;
		}

		@Override
		public Dataset getDatasetOrThrowException(final Uri uri) {
			return super.getDatasetOrThrowException(uri);
		}

		@Override
		public SQLiteDatabase getDatabase() {
			return super.getDatabase();
		}

		@Override
		public void closeDatabase() {
			super.closeDatabase();
		}
	}

	public static class TestDatabaseConfiguration extends DefaultDatabaseConfiguration {

		public TestDatabaseConfiguration(final Context context) {
			super(context);
		}

		@Override
		public String getDatabaseName() {
			return "test.db";
		}

		@Override
		public int getDatabaseVersion() {
			return 1;
		}
	}

	public static class TestTable1 extends SQLiteTable {

		public static interface Columns {
            @Column(Column.Type.TEXT)
            public static final String ID = "id";
		}

		@Override
		public SQLiteDatabase getDatabase() {
			return super.getDatabase();
		}
	}

	public static class TestTable2 implements Dataset {

		@Override
		public Uri insert(final Uri uri, final ContentValues values) {
			return null;
		}

		@Override
		public int bulkInsert(final Uri uri, final ContentValues[] values) {
			return 0;
		}

		@Override
		public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
			return 0;
		}

		@Override
		public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
			return 0;
		}

		@Override
		public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
			return null;
		}
	}
}
