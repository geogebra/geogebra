package geogebra.web.html5;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;

public class Dom extends DOM {
	
	public static native NodeList<Element> getElementsByClassName(String className) /*-{
			return $doc.getElementsByClassName(className);
	}-*/;

}
