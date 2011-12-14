package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElement;

public interface MacroKernelInterface {

	void setContinuous(boolean b);

	void setGlobalVariableLookup(boolean b);

	void loadXML(String macroConsXML) throws Exception;

	Construction getConstruction();

	AbstractKernel getParentKernel();

	void addReservedLabel(String label);

	GeoElement lookupLabel(String label);

}
