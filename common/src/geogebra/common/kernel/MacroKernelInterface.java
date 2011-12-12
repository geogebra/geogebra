package geogebra.common.kernel;

public interface MacroKernelInterface {

	void setContinuous(boolean b);

	void setGlobalVariableLookup(boolean b);

	void loadXML(String macroConsXML) throws Exception;

	Construction getConstruction();

	AbstractKernel getParentKernel();

}
