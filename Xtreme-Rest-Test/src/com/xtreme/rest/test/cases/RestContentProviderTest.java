package com.xtreme.rest.test.cases;

import java.util.Map.Entry;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.xtreme.rest.test.mock.TestContentProvider;
import com.xtreme.rest.test.mock.TestTable1;
import com.xtreme.rest.test.mock.TestTable2;
import com.xtreme.rest.test.mock.TestView;

public class RestContentProviderTest extends ProviderTestCase2<TestContentProvider> {

	private static final int WRONG_ITEM_ID = 1239183702;

	private SQLiteDatabase mDatabase;

	public RestContentProviderTest() {
		super(TestContentProvider.class, TestContentProvider.AUTHORITY);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mDatabase = getProvider().getDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		getProvider().destroyDatabase();
	}

	public void testInitilization() {
		assertNotNull(mDatabase);
	}

	public void testTableQuery() {
		assertTableIsEmpty();
		
		final ContentValues[] values = TestTable1.CONTENT_ARRAY;
		
		bulkInsertIntoTable(values);
		
		final ContentValues testValue = values[0];
		final int testId = testValue.getAsInteger(TestTable1.Columns.ID);

		// ======== TEST QUERY ITEM ID ==============
		
		final Cursor idCursor = queryItemFromTable(testId);
		assertEquals(1, idCursor.getCount());
		assertTrue(idCursor.moveToFirst());
		assertValuesEqualsCursor(testValue, idCursor);
		idCursor.close();

		// ======== TEST QUERY WRONG ITEM ID ==============
		
		final Cursor wrongIdCursor = queryItemFromTable(WRONG_ITEM_ID);
		assertEquals(0, wrongIdCursor.getCount());
		wrongIdCursor.close();
		
		// ======== TEST QUERY ALL ITEMS ==============
		
		final Cursor allCursor = queryAllItemsFromTable();
		assertEquals(values.length, allCursor.getCount());

		for (int i = 0; i < values.length; i++) {
			assertTrue(allCursor.moveToPosition(i));
			assertValuesEqualsCursor(values[i], allCursor);
		}

		allCursor.close();
	}

	public void testTableInsert() {
		assertTableIsEmpty();

		final ContentValues values = TestTable1.CONTENT_VALUES;

		// ======== TEST INSERT ITEM ==============
		
		final Uri firstUri = insertIntoTable(values);
		final long firstId = ContentUris.parseId(firstUri);
		assertTrue(firstId > 0);

		final Cursor firstCursor = queryAllItemsFromTable();
		assertEquals(1, firstCursor.getCount());
		assertTrue(firstCursor.moveToFirst());
		assertCursorEqualsValues(firstCursor, values);
		firstCursor.close();
		
		// ======== TEST INSERT SAME ITEM ==============
		
		/**
		 * Note: this results in only one item left in 
		 * the database because of our unique constraint. 
		 */
		
		final Uri secondUri = insertIntoTable(values);
		final long secondId = ContentUris.parseId(secondUri);
		assertTrue(secondId > 0);
		
		final Cursor secondCursor = queryAllItemsFromTable();
		assertEquals(1, secondCursor.getCount());
		assertTrue(secondCursor.moveToFirst());
		assertCursorEqualsValues(secondCursor, values);
		secondCursor.close();
	}

	public void testTableBulkInsert() {
		assertTableIsEmpty();

		final ContentValues[] values = TestTable1.CONTENT_ARRAY;

		// ======== TEST BULK INSERT ITEMS ==============
		
		final long firstCount = bulkInsertIntoTable(values);
		assertEquals(values.length, firstCount);

		final Cursor firstCursor = queryAllItemsFromTable();
		assertEquals(values.length, firstCursor.getCount());

		for (int i = 0; i < values.length; i++) {
			assertTrue(firstCursor.moveToPosition(i));
			assertCursorEqualsValues(firstCursor, values[i]);
		}

		firstCursor.close();
		
		// ======== TEST BULK INSERT SAME ITEMS ==============
		
		/**
		 * Note: this results in the same items left in 
		 * the database because of our unique constraint. 
		 */
		
		final long secondCount = bulkInsertIntoTable(values);
		assertEquals(values.length, secondCount);

		final Cursor secondCursor = queryAllItemsFromTable();
		assertEquals(values.length, secondCursor.getCount());

		for (int i = 0; i < values.length; i++) {
			assertTrue(secondCursor.moveToPosition(i));
			assertCursorEqualsValues(secondCursor, values[i]);
		}

		secondCursor.close();
	}

	public void testTableUpdate() {
		assertTableIsEmpty();

		final ContentValues[] originalValues = TestTable1.CONTENT_ARRAY;
		final ContentValues fullyUpdatedValues = TestTable1.UPDATE_VALUES_FULL;
		final ContentValues partiallyUpdatedValues = TestTable1.UPDATE_VALUES_PARTIAL;
		
		final ContentValues testValue = originalValues[0];
		final int testId = testValue.getAsInteger(TestTable1.Columns.ID);
		
		// ======== TEST NO ITEM TO UPDATE ==============
		
		final long emptyUpdatedCount = updateItemFromTable(partiallyUpdatedValues, testId);
		assertEquals(0, emptyUpdatedCount);
		
		bulkInsertIntoTable(originalValues);
		
		// ======== TEST PARTIAL UPDATE OF 1 ITEM ==============
		
		final long itemPartiallyUpdatedCount = updateItemFromTable(partiallyUpdatedValues, testId);
		assertEquals(1, itemPartiallyUpdatedCount);

		final Cursor itemPartiallyUpdateCursor = queryItemFromTable(testId);
		assertTrue(itemPartiallyUpdateCursor.moveToFirst());
		assertCursorEqualsValues(itemPartiallyUpdateCursor, partiallyUpdatedValues);
		itemPartiallyUpdateCursor.close();

		// ======== TEST FULL UPDATE OF 1 ITEM ==============
		
		final long itemFullyUpdatedCount = updateItemFromTable(fullyUpdatedValues, testId);
		assertEquals(1, itemFullyUpdatedCount);

		final Cursor itemFullyUpdatedCursor = queryItemFromTable(testId);
		assertTrue(itemFullyUpdatedCursor.moveToFirst());
		assertCursorEqualsValues(itemFullyUpdatedCursor, fullyUpdatedValues);
		itemFullyUpdatedCursor.close();
		
		// ======== TEST PARTIAL UPDATE OF ALL ITEMS ==============

		final long allPartiallyUpdatedCount = updateAllItemsFromTable(partiallyUpdatedValues);
		assertEquals(originalValues.length, allPartiallyUpdatedCount);

		final Cursor allPartiallyUpdatedCursor = queryAllItemsFromTable();
		for (int i = 0; i < originalValues.length; i++) {
			assertTrue(allPartiallyUpdatedCursor.moveToPosition(i));
			assertCursorEqualsValues(allPartiallyUpdatedCursor, partiallyUpdatedValues);
		}
		
		allPartiallyUpdatedCursor.close();
		
		// ======== TEST FULL UPDATE OF ALL ITEMS ==============
		
		/**
		 * Note: this results in only one item left in 
		 * the database because of our unique constraint. 
		 */

		final long allFullyUpdatedCount = updateAllItemsFromTable(fullyUpdatedValues);
		assertEquals(originalValues.length, allFullyUpdatedCount);
		
		final Cursor allFullyUpdatedCursor = queryAllItemsFromTable();
		assertEquals(1, allFullyUpdatedCursor.getCount());
		assertTrue(allFullyUpdatedCursor.moveToFirst());
		assertCursorEqualsValues(allFullyUpdatedCursor, fullyUpdatedValues);
		allFullyUpdatedCursor.close();
	}

	public void testTableDelete() {
		assertTableIsEmpty();

		final ContentValues[] originalValues = TestTable1.CONTENT_ARRAY;

		final ContentValues testValue = originalValues[0];
		final int testId = testValue.getAsInteger(TestTable1.Columns.ID);
		
		// ======== TEST DELETE WHEN EMPTY ==============
		
		final long emptyDeleted = deleteItemFromTable(testId);
		assertEquals(0, emptyDeleted);
		
		bulkInsertIntoTable(originalValues);
		
		// ======== TEST DELETE ITEM ID ==============
		
		final long idDeletedCount = deleteItemFromTable(testId);
		assertEquals(1, idDeletedCount);

		final Cursor idCursor = queryItemFromTable(testId);
		assertEquals(0, idCursor.getCount());
		idCursor.close();
		
		// ======== TEST DELETE WRONG ITEM ID ==============
		
		final long wrongIdDeletedCount = deleteItemFromTable(WRONG_ITEM_ID);
		assertEquals(0, wrongIdDeletedCount);
		
		// ======== TEST DELETE ALL ITEMS ==============
		
		final long deleteCount = deleteAllItemsFromTable();
		assertEquals((originalValues.length - idDeletedCount), deleteCount);

		assertTableIsEmpty();
	}

	
	// ================================================

	
	
	public void testViewQuery() {
		assertViewIsEmpty();
		
		final ContentValues[] values1 = TestTable1.CONTENT_ARRAY;
		final ContentValues[] values2 = TestTable2.CONTENT_ARRAY;

		bulkInsertIntoTable(TestContentProvider.Uris.TEST_TABLE1, values1);
		bulkInsertIntoTable(TestContentProvider.Uris.TEST_TABLE2, values2);
		
		final ContentValues[] values = TestView.CONTENT_ARRAY;

		final ContentValues testValue = values[0];
		final int testId = testValue.getAsInteger(TestTable1.Columns.ID);

		// ======== TEST QUERY ITEM ID ==============
		
		final Cursor idCursor = queryItemFromView(testId);
		assertEquals(1, idCursor.getCount());
		assertTrue(idCursor.moveToFirst());
		assertValuesEqualsCursor(testValue, idCursor);
		idCursor.close();
		
		// ======== TEST QUERY WRONG ITEM ID ==============
		
		final Cursor wrongIdCursor = queryItemFromView(WRONG_ITEM_ID);
		assertEquals(0, wrongIdCursor.getCount());
		wrongIdCursor.close();
		
		// ======== TEST QUERY ALL ITEMS ==============
		
		final Cursor allCursor = queryAllItemsFromView();
		assertEquals(values.length, allCursor.getCount());

		for (int i = 0; i < values.length; i++) {
			assertTrue(allCursor.moveToPosition(i));
			assertValuesEqualsCursor(values[i], allCursor);
		}

		allCursor.close();
	}

	public void testViewInsertThrowsException() {
		try {
			insertContentValuesIntoView();
			fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testViewBulkInsertThrowsException() {
		try {
			bulkInsertContentArrayIntoView();
			fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testViewDeleteThrowsException() {
		try {
			deleteAllItemsFromView();
			fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testViewUpdateThrowsException() {
		try {
			updateAllItemsFromView();
			fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	
	// ================================================

	
	private MockContentResolver getContentResolver() {
		return getMockContentResolver();
	}

	private void assertTableIsEmpty() {
		final Cursor cursor = queryAllItemsFromTable();
		assertEquals(0, cursor.getCount());
		cursor.close();
	}

	private Cursor queryAllItemsFromTable() {
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}

	private Cursor queryItemFromTable(final long id) {
		final String where = TestTable1.Columns.ID + " = ?";
		final String[] whereArgs = new String[] { String.valueOf(id) };
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final Cursor cursor = getContentResolver().query(uri, null, where, whereArgs, null);
		return cursor;
	}

	private Uri insertIntoTable(final ContentValues values) {
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final Uri inserted = getContentResolver().insert(uri, values);
		return inserted;
	}

	private long bulkInsertIntoTable(final ContentValues[] values) {
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		return bulkInsertIntoTable(uri, values);
	}
	
	private long bulkInsertIntoTable(final Uri uri, final ContentValues[] values) {
		final long count = getContentResolver().bulkInsert(uri, values);
		return count;
	}

	private long updateAllItemsFromTable(final ContentValues values) {
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final long count = getContentResolver().update(uri, values, null, null);
		return count;
	}

	private long updateItemFromTable(final ContentValues values, final int id) {
		final String where = TestTable1.Columns.ID + " = ?";
		final String[] whereArgs = new String[] { String.valueOf(id) };
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final long count = getContentResolver().update(uri, values, where, whereArgs);
		return count;
	}

	private long deleteAllItemsFromTable() {
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final long count = getContentResolver().delete(uri, null, null);
		return count;
	}

	private long deleteItemFromTable(final int id) {
		final String where = TestTable1.Columns.ID + " = ?";
		final String[] whereArgs = new String[] { String.valueOf(id) };
		final Uri uri = TestContentProvider.Uris.TEST_TABLE1;
		final long count = getContentResolver().delete(uri, where, whereArgs);
		return count;
	}
	
	
	// ================================================
	
	
	private void assertViewIsEmpty() {
		final Cursor cursor = queryAllItemsFromView();
		assertEquals(0, cursor.getCount());
		cursor.close();
	}

	private Cursor queryAllItemsFromView() {
		final Uri uri = TestContentProvider.Uris.TEST_VIEW;
		final Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}
	
	private Cursor queryItemFromView(final int id) {
		final String where = TestTable1.Columns.ID + " = ?";
		final String[] whereArgs = new String[] { String.valueOf(id) };
		final Uri uri = TestContentProvider.Uris.TEST_VIEW;
		final Cursor cursor = getContentResolver().query(uri, null, where, whereArgs, null);
		return cursor;
	}

	private Uri insertContentValuesIntoView() {
		final Uri uri = TestContentProvider.Uris.TEST_VIEW;
		final Uri inserted = getContentResolver().insert(uri, null);
		return inserted;
	}

	private long bulkInsertContentArrayIntoView() {
		final Uri uri = TestContentProvider.Uris.TEST_VIEW;
		final long count = getContentResolver().bulkInsert(uri, null);
		return count;
	}

	private long deleteAllItemsFromView() {
		final Uri uri = TestContentProvider.Uris.TEST_VIEW;
		final long count = getContentResolver().delete(uri, null, null);
		return count;
	}

	private long updateAllItemsFromView() {
		final Uri uri = TestContentProvider.Uris.TEST_VIEW;
		final long count = getContentResolver().update(uri, null, null, null);
		return count;
	}

	
	// ================================================
	
	
	private static void assertValuesEqualsCursor(final ContentValues values, final Cursor cursor) {
		for (final String columnName : cursor.getColumnNames()) {
			if (columnName.contains(BaseColumns._ID)) {
				final long actual = cursor.getLong(cursor.getColumnIndex(columnName));
				assertTrue(actual >= 0);
			} else {
				final String expected = values.getAsString(columnName);
				final String actual = cursor.getString(cursor.getColumnIndex(columnName));
				assertEquals(expected, actual);
			}
		}
	}

	private static void assertCursorEqualsValues(final Cursor cursor, final ContentValues values) {
		for (final Entry<String, Object> entry : values.valueSet()) {
			final String columnName = entry.getKey();
			final String expected = String.valueOf(entry.getValue());
			final String actual = cursor.getString(cursor.getColumnIndex(columnName));
			assertEquals(expected, actual);
		}
	}

}