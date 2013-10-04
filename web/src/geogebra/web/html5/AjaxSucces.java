package geogebra.web.html5;

/**
 * @author gabor
 *
 *	Succes for ajax callbacks
 */
public interface AjaxSucces {
	/**
	 * code for succes to run
	 * @param responseText The success responsetext
	 */
	public void onSuccess(String responseText);
}
