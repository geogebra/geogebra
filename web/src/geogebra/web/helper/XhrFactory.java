package geogebra.web.helper;

import geogebra.web.main.Application;

public class XhrFactory {
	
	public static RequestTemplate getSupportedXhr() {
		if (isArrayBufferSupported()) {
			return new RequestTemplateXhr2();
		}
		return new RequestTemplateXhr();
	}

	private static native boolean isArrayBufferSupported()  /*-{
	    if (typeof $wnd.ArrayBuffer != "undefined") {
	    	return true;
	    }
	    return false;
    }-*/;

}