package org.geogebra.common.gui.toolcategorization;

public interface ToolsProvider {

	ToolCollection getAvailableTools();

	void addToolsFilter(ToolCollectionFilter filter);
	void removeToolsFilter(ToolCollectionFilter filter);
}
