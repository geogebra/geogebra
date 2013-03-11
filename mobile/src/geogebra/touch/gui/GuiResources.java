package geogebra.touch.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle
{
	GuiResources INSTANCE = GWT.create(GuiResources.class);

	@Source("css/mathquill.css")
	TextResource mathquillCss();

	// for saving - not needed yet, because now we use xml-Strings
	// @Source("geogebra/resources/js/zipjs/dataview.js")
	// TextResource dataViewJs();
	//
	// @Source("geogebra/resources/js/zipjs/zip.js")
	// TextResource zipJs();
	//
	// @Source("geogebra/resources/js/downloadggb.js")
	// TextResource downloadggbJs();
	//
	// @Source("geogebra/resources/js/zipjs/deflate.js")
	// TextResource deflateJs();
	//
	// @Source("geogebra/resources/js/zipjs/inflate.js")
	// TextResource inflateJs();
	//
	// @Source("geogebra/resources/js/zipjs/base64.js")
	// TextResource base64Js();
	//
	// @Source("geogebra/resources/js/zipjs/arraybuffer.js")
	// TextResource arrayBufferJs();

}