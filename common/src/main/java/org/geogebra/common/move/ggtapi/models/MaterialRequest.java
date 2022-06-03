package org.geogebra.common.move.ggtapi.models;

/**
 * For Generating a JSON String for specific GeoGebratube API Requests
 * 
 * @author Matthias Meisinger
 */
public class MaterialRequest {

	public enum Order {
		id, title, type, description, timestamp, author, language, featured, likes,
		relevance, privacy, created;
	}

}
