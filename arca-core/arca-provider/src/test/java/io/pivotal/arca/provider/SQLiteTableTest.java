package io.pivotal.arca.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;

public class SQLiteTableTest extends AndroidTestCase {

	private static final Uri URI = Uri.EMPTY;
	private static final ContentValues VALUES = new ContentValues();
	private static final TestSQLiteTable TABLE = new TestSQLiteTable();
	private static final Collection<SQLiteDataset> DATASETS = new ArrayList<SQLiteDataset>();

	static {
		VALUES.put("id", "test");
	}

	static {
		DATASETS.add(TABLE);
	}

	private SQLiteDatabase mDatabase;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		closeDatabase();
		deleteDatabase();
	}

	public void testSQLiteTableQuery() {
		testSQLiteTableInsert();

		TABLE.setDatabase(mDatabase);
		final Cursor cursor = TABLE.query(URI, null, null, null, null);
		assertEquals(1, cursor.getCount());
		cursor.getColumnIndexOrThrow("id");
		cursor.close();
	}

	public void testSQLiteTableUpdate() {
		testSQLiteTableInsert();

		TABLE.setDatabase(mDatabase);
		final int updated = TABLE.update(URI, VALUES, null, null);
		assertEquals(1, updated);
	}

	public void testSQLiteTableInsert() {
		TABLE.setDatabase(mDatabase);
		final Uri uri = TABLE.insert(URI, VALUES);
		assertNotNull(uri);
	}

	public void testSQLiteTableBulkInsert() {
		TABLE.setDatabase(mDatabase);
		final ContentValues[] values = new ContentValues[] { VALUES };
		final int inserted = TABLE.bulkInsert(URI, values);
		assertEquals(1, inserted);
	}

	public void testSQLiteTableDelete() {
		testSQLiteTableInsert();

		TABLE.setDatabase(mDatabase);
		final int deleted = TABLE.delete(URI, null, null);
		assertEquals(1, deleted);
	}

	public void testSQLiteTableQueryThrowsExceptionWithoutDatabase() {
		try {
			TABLE.setDatabase(null);
			TABLE.query(null, null, null, null, null);
			Assert.fail();
		} catch (final IllegalStateException e) {
			assertEquals("Database is null.", e.getLocalizedMessage());
		}
	}

	public void testSQLiteTableUpdateThrowsExceptionWithoutDatabase() {
		try {
			TABLE.setDatabase(null);
			TABLE.update(null, null, null, null);
			Assert.fail();
		} catch (final IllegalStateException e) {
			assertEquals("Database is null.", e.getLocalizedMessage());
		}
	}

	public void testSQLiteTableInsertThrowsExceptionWithoutDatabase() {
		try {
			TABLE.setDatabase(null);
			TABLE.insert(null, null);
			Assert.fail();
		} catch (final IllegalStateException e) {
			assertEquals("Database is null.", e.getLocalizedMessage());
		}
	}

	public void testSQLiteTableBulkInsertThrowsExceptionWithoutDatabase() {
		try {
			TABLE.setDatabase(null);
			TABLE.bulkInsert(null, null);
			Assert.fail();
		} catch (final IllegalStateException e) {
			assertEquals("Database is null.", e.getLocalizedMessage());
		}
	}

	public void testSQLiteTableDeleteThrowsExceptionWithoutDatabase() {
		try {
			TABLE.setDatabase(null);
			TABLE.delete(null, null, null);
			Assert.fail();
		} catch (final IllegalStateException e) {
			assertEquals("Database is null.", e.getLocalizedMessage());
		}
	}

	// ====================================

	public void createDatabase() {
		final DatabaseConfiguration config = new DatabaseConfiguration.DefaultDatabaseConfiguration(getContext());
		final DatabaseHelper helper = DatabaseHelper.create(getContext(), config, DATASETS);
		mDatabase = helper.getWritableDatabase();
	}

	public void deleteDatabase() {
		final DatabaseConfiguration config = new DatabaseConfiguration.DefaultDatabaseConfiguration(getContext());
		getContext().deleteDatabase(config.getDatabaseName());
	}

	public void closeDatabase() {
		if (mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	// ====================================

	public static final class TestSQLiteTable extends SQLiteTable {

		public static interface Columns {
            @Column(Column.Type.TEXT)
            public static final String ID = "id";
		}

		@Override
		public void setDatabase(final SQLiteDatabase db) {
			super.setDatabase(db);
		}
	}
}
