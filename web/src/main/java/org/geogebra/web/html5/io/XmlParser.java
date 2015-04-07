package org.geogebra.web.html5.io;

import org.geogebra.common.io.DocHandler;

public interface XmlParser {

	void parse(DocHandler docHandler, String xml) throws Exception;

}
