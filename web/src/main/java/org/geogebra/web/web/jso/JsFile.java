package org.geogebra.web.web.jso;

public class JsFile extends JsBlob {

	protected JsFile() {
	}

	/**
	 * The name of the file. There are numerous file name variations on
	 * different systems; this is merely the name of the file, without path
	 * information.
	 * 
	 * @return name of the file
	 */
	public final native String getName() /*-{
		return this.name;
	}-*/;

	/**
	 * The last modified date of the file as valid HTML5 date string or the
	 * empty string if it is not known.
	 * 
	 * @return last modified date or the empty string
	 */
	public final native String getLastModifiedDate() /*-{
		return this.lastModifiedDate;
	}-*/;

}
