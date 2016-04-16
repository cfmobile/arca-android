package io.pivotal.arca.monitor;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Delete;
import io.pivotal.arca.dispatcher.DeleteResult;
import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.InsertResult;
import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.dispatcher.RequestExecutor;
import io.pivotal.arca.dispatcher.Result;
import io.pivotal.arca.dispatcher.Update;
import io.pivotal.arca.dispatcher.UpdateResult;
import io.pivotal.arca.monitor.RequestMonitor.Flags;

public interface ArcaExecutor extends RequestExecutor {

	public void setRequestMonitor(final RequestMonitor monitor);

	public RequestMonitor getRequestMonitor();

	public static class DefaultArcaExecutor extends DefaultRequestExecutor implements ArcaExecutor {

		private RequestMonitor mMonitor;
		private final Context mContext;

		public DefaultArcaExecutor(final ContentResolver resolver, final Context context) {
			super(resolver);
			mContext = context;
		}

		@Override
		public void setRequestMonitor(final RequestMonitor verifier) {
			mMonitor = verifier;
		}

		@Override
		public RequestMonitor getRequestMonitor() {
			return mMonitor;
		}

		@Override
		public QueryResult execute(final Query request) {
			final RequestMonitor monitor = getRequestMonitor();

			int flags = Flags.DATA_VALID;

			if (monitor != null) {
				flags = flags | monitor.onPreExecute(mContext, request);
			}

			final QueryResult result = super.execute(request);

			if (monitor != null) {
				flags = flags | monitor.onPostExecute(mContext, request, result);
			}

			return (QueryResult) appendFlags(result, flags);
		}

		@Override
		public UpdateResult execute(final Update request) {
			final RequestMonitor monitor = getRequestMonitor();

			int flags = Flags.DATA_VALID;

			if (monitor != null) {
				flags = flags | monitor.onPreExecute(mContext, request);
			}

			final UpdateResult result = super.execute(request);

			if (monitor != null) {
				flags = flags | monitor.onPostExecute(mContext, request, result);
			}

			return (UpdateResult) appendFlags(result, flags);
		}

		@Override
		public InsertResult execute(final Insert request) {
			final RequestMonitor monitor = getRequestMonitor();

			int flags = Flags.DATA_VALID;

			if (monitor != null) {
				flags = flags | monitor.onPreExecute(mContext, request);
			}

			final InsertResult result = super.execute(request);

			if (monitor != null) {
				flags = flags | monitor.onPostExecute(mContext, request, result);
			}

			return (InsertResult) appendFlags(result, flags);
		}

		@Override
		public DeleteResult execute(final Delete request) {
			final RequestMonitor monitor = getRequestMonitor();

			int flags = Flags.DATA_VALID;

			if (monitor != null) {
				flags = flags | monitor.onPreExecute(mContext, request);
			}

			final DeleteResult result = super.execute(request);

			if (monitor != null) {
				flags = flags | monitor.onPostExecute(mContext, request, result);
			}

			return (DeleteResult) appendFlags(result, flags);
		}

		private static Result<?> appendFlags(final Result<?> result, int flags) {
			result.setIsValid((flags & Flags.DATA_INVALID) == 0);
			result.setIsSyncing((flags & Flags.DATA_SYNCING) != 0);
			return result;
		}

	}
}
