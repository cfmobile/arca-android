package io.pivotal.arca.adapters;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ModernItemAdapterTest extends AndroidTestCase {

	public void testItemCursorAdapterNullCursorHasNoResults() {
		final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
		assertFalse(adapter.hasResults());
	}

	public void testItemCursorAdapterEmptyCursorHasNoResults() {
		final MatrixCursor cursor = new MatrixCursor(new String[] { "_id" });
		final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
		adapter.swapCursor(cursor);
		assertFalse(adapter.hasResults());
	}

	public void testItemCursorAdapterCursorHasResults() {
		final Cursor cursor = createCursor(new String[] { "_id" });
		final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
		adapter.swapCursor(cursor);
		assertTrue(adapter.hasResults());
	}

	public void testItemCursorAdapterViewType() {
		final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
		assertEquals(1, adapter.getViewTypeCount());
	}

	public void testItemCursorAdapterDefaultViewBinding() {
		final TextView child1 = new TextView(getContext());
		child1.setId(R.id.test_id_1);
		final FrameLayout container = new FrameLayout(getContext());
		container.addView(child1);
		final String[] columns = new String[] { "_id" };
		final Cursor cursor = createCursor(columns);
		final Collection<Binding> bindings = createBindings(columns);
		final ModernItemAdapter adapter = new ModernItemAdapter(getContext(), bindings);
		adapter.bindView(container, getContext(), cursor);
		assertEquals("default_test", child1.getText());
	}

	public void testItemCursorAdapterCustomViewBinding() {
		final TextView child1 = new TextView(getContext());
		child1.setId(R.id.test_id_1);
		final FrameLayout container = new FrameLayout(getContext());
		container.addView(child1);
		final String[] columns = new String[] { "_id" };
		final Cursor cursor = createCursor(columns);
		final Collection<Binding> bindings = createBindings(columns);
		final ModernItemAdapter adapter = new ModernItemAdapter(getContext(), bindings);
		adapter.setViewBinder(new TestViewBinder());
		adapter.bindView(container, getContext(), cursor);
		assertEquals("custom_test", child1.getText());
	}

	public void testItemCursorAdapterCannotBindView() {
		try {
			final View child1 = new View(getContext());
			child1.setId(R.id.test_id_1);
			final FrameLayout container = new FrameLayout(getContext());
			container.addView(child1);
			final String[] columns = new String[] { "_id" };
			final Cursor cursor = createCursor(columns);
			final Collection<Binding> bindings = createBindings(columns);
			final ModernItemAdapter adapter = new ModernItemAdapter(getContext(), bindings);
			adapter.bindView(container, getContext(), cursor);
			Assert.fail();
		} catch (final IllegalStateException e) {
			assertNotNull(e);
		}
	}

	public void testItemCursorAdapterGetViewThrowsException() {
		try {
			final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
			adapter.getView(0, null, null);
			Assert.fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testItemCursorAdapterGetDropDownViewThrowsException() {
		try {
			final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
			adapter.getDropDownView(0, null, null);
			Assert.fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testItemCursorAdapterNewViewThrowsException() {
		try {
			final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
			adapter.newView(null, null, null);
			Assert.fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testItemCursorAdapterNewDropDownViewThrowsException() {
		try {
			final ModernItemAdapter adapter = new ModernItemAdapter(null, null);
			adapter.newDropDownView(null, null, null);
			Assert.fail();
		} catch (final UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	private static Collection<Binding> createBindings(final String[] columns) {
		final Collection<Binding> bindings = new ArrayList<Binding>();
		bindings.add(new Binding(R.id.test_id_1, columns[0]));
		return bindings;
	}

	private static MatrixCursor createCursor(final String[] columns) {
		final MatrixCursor cursor = new MatrixCursor(columns);
		cursor.addRow(new String[] { "default_test" });
		cursor.moveToFirst();
		return cursor;
	}

	public class TestViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
			((TextView) view).setText("custom_test");
			return true;
		}
	}

}
