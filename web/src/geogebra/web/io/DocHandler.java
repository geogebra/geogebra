package geogebra.web.io;

import java.util.Map;

public interface DocHandler {

	void startElement(String tagName, Map<String, String> attributes);
	void endElement(String tagName);

	void startDocument();
	void endDocument();

}
