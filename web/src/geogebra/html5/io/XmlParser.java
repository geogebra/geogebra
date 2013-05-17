package geogebra.html5.io;

import geogebra.common.io.DocHandler;


public interface XmlParser {
	
	void parse(DocHandler docHandler, String xml) throws Exception;
	
}
