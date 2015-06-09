/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.adapters;

import android.database.MatrixCursor;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.TextView;

public class ViewBinderTest extends AndroidTestCase {

	public void testViewBinderSucceedsBindingTextView() {
		final String[] columns = new String[] { "_id" };
		final MatrixCursor cursor = createCursor(columns);
		final Binding binding = createBinding(columns, cursor);
		final TextView textView = new TextView(getContext());
		final ViewBinder.DefaultViewBinder binder = new ViewBinder.DefaultViewBinder();
		binder.setViewValue(textView, cursor, binding);
		assertEquals("test", textView.getText());
	}

	public void testViewBinderSucceedsBindingView() {
		final String[] columns = new String[] { "_id" };
		final MatrixCursor cursor = createCursor(columns);
		final Binding binding = createBinding(columns, cursor);
		final View view = new TextView(getContext());
		final ViewBinder.DefaultViewBinder binder = new ViewBinder.DefaultViewBinder();
		final boolean bound = binder.setViewValue(view, cursor, binding);
		assertTrue(bound);
	}

	public void testViewBinderFailsBindingView() {
		final String[] columns = new String[] { "_id" };
		final MatrixCursor cursor = createCursor(columns);
		final Binding binding = createBinding(columns, cursor);
		final View view = new View(getContext());
		final ViewBinder.DefaultViewBinder binder = new ViewBinder.DefaultViewBinder();
		final boolean bound = binder.setViewValue(view, cursor, binding);
		assertFalse(bound);
	}

	private static Binding createBinding(final String[] columns, final MatrixCursor cursor) {
		final Binding binding = new Binding(0, columns[0]);
		binding.findColumnIndex(cursor);
		return binding;
	}

	private static MatrixCursor createCursor(final String[] columns) {
		final MatrixCursor cursor = new MatrixCursor(columns);
		cursor.addRow(new String[] { "test" });
		cursor.moveToFirst();
		return cursor;
	}

}
