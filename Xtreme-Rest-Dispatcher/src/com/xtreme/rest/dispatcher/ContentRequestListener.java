package com.xtreme.rest.dispatcher;

public interface ContentRequestListener<T> {
	public void onRequestComplete(ContentResult<T> result);
}