package geogebra.web.io;

public interface XmlParser {
	
	void parse(DocHandler docHandler, String xml) throws ConstructionException;
	
}
