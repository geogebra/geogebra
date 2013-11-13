package geogebra.html5.util;

/**
 * @author gabor
 * 
 * swapped, if we don't need runasync in the code.  Must be set in module xml with deferred binding
 *
 */
public class SingleScriptRunAsync extends RunAsync {

	@Override
	public void runAsyncCallback(MyRunAsyncCallback callback) {
		callback.onSuccess();
	}

}
