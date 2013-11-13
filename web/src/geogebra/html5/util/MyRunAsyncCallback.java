package geogebra.html5.util;


/**
 * @author gabor
 * to Mimic RunAsync behaviour
 * always success! 
 */
public interface MyRunAsyncCallback {
	 /**
	   * Called when, for some reason, the necessary code cannot be loaded. For
	   * example, the web browser might no longer have network access.
	   */
	  void onFailure(Throwable reason);

	  /**
	   * Called once the necessary code for it has been loaded.
	   */
	  void onSuccess();
}
