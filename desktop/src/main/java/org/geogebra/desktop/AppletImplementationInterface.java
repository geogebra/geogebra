package org.geogebra.desktop;

import org.geogebra.common.plugin.JavaScriptAPI;

public interface AppletImplementationInterface {

	public void dispose();

	public void initInBackground();

	public JavaScriptAPI getGgbApi();

	public String getPNGBase64(double exportScale, boolean transparent,
			double dPI, boolean copyToClipboard);

	public String evalGeoGebraCAS(String cmdString);

	public void evalXML(String xmlString);

	public void setXML(String xml);

	public void openFile(String strURL);

	public void reset();
}
