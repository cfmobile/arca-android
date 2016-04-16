package io.pivotal.arca.threading;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class HashedStack<T> implements Iterable<T> {
	private Node<T> mHead = null;
	private Node<T> mTail = null;
	private final Map<T, Node<T>> mNodeMap = new HashMap<T, Node<T>>();

	private static class Node<T> {
		T data;
		private Node<T> next = null;
		private Node<T> previous = null;

		Node(final T data) {
			this.data = data;
		}

		public Node<T> getNext() {
			return next;
		}

		public void setNext(final Node<T> next) {
			this.next = next;
		}

		public Node<T> getPrevious() {
			return previous;
		}

		public void setPrevious(final Node<T> previous) {
			this.previous = previous;
		}
	}

	public synchronized void clear() {
		mHead = null;
		mTail = null;
		mNodeMap.clear();
	}

	public synchronized boolean contains(final Object object) {
		return mNodeMap.containsKey(object);
	}

	public synchronized boolean isEmpty() {
		return mHead == null;
	}

	@Override
	public synchronized Iterator<T> iterator() {
		return new Iterator<T>() {
			private Node<T> current = mHead;

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public T next() {
				if (current == null) {
					throw new NoSuchElementException("No more elements inside of the iterator.");
				}

				final T data = current.data;
				current = current.getNext();
				return data;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public synchronized boolean remove(final T object) {
		final Node<T> node = mNodeMap.remove(object);
		if (node == null) {
			return false;
		}

		Node<T> previous, next;
		previous = node.getPrevious();
		next = node.getNext();

		if (previous != null) {
			previous.setNext(next);
		}
		if (next != null) {
			next.setPrevious(previous);
		}

		if (previous == null) {
			mHead = next;
		}

		if (next == null) {
			mTail = previous;
		}

		return true;
	}

	public synchronized int size() {
		return mNodeMap.size();
	}

	public synchronized boolean push(final T e) {
		Node<T> node;
		if (mNodeMap.containsKey(e)) {
			node = mNodeMap.get(e);

			/*
			 * TODO: This "remove" call is not high performance in this case. We
			 * want the node removed from the queue but not the map.
			 */
			remove(e);

			node.next = null;
			node.previous = null;
		} else {
			node = new Node<T>(e);
		}

		if (mTail == null) {
			mHead = node;
			mTail = node;
		} else {
			mTail.setNext(node);
			node.setPrevious(mTail);
			mTail = node;
		}
		mNodeMap.put(e, node);
		return true;
	}

	public synchronized T pop() {
		T data = null;
		if (mTail != null) {
			data = mTail.data;
			mTail = mTail.previous;
			if (mTail != null) {
				mTail.next = null;
			}
			mNodeMap.remove(data);
		}
		return data;
	}

	public synchronized void bump(final T e) {
		if (remove(e))
			push(e);
	}

	public T peek() {
		T data = null;
		if (mTail != null) {
			data = mTail.data;
		}
		return data;
	}
}
