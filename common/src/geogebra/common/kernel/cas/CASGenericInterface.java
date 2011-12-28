package geogebra.common.kernel.cas;

import geogebra.common.kernel.geos.GeoElement;

import java.util.Set;

public interface CASGenericInterface {

	String translateAssignment(String casLabel, String body);

	String translateFunctionDeclaration(String casLabel, String params,
			String body);

	String getTranslatedCASCommand(String string);

	public Set<String> getAvailableCommandNames();

	String toAssignment(GeoElement geoElement);

}
