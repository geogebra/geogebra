package geogebra.web.helper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class CodeSplitCallback<T> implements AsyncCallback<T> {


	public void onFailure(Throwable reason) {
		Window.alert("Code splitting failure: " + reason.getMessage() + "\nTry reloading the page.");
	}

	
	public abstract void onSuccess(T result);

}
