package geogebra.web.html5;

/**
 * @author gabor
 *	callback for Ajax Errors
 */
public interface AjaxError {
	/**
	 * Error code to run
	 * @param ErrorMSG errorMessage to show
	 */
	public void onError(String ErrorMSG);
}
