package org.geogebra.common.kernel;


import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.geos.GeoImage;

public class MeasurementController {
	private final Kernel kernel;
	private Map<MeasurementToolId, MeasurementTool> tools = new HashMap<>();
	private MeasurementToolId selectedToolId;
	private boolean toolActive = false;

	public MeasurementController(Kernel kernel) {
		this.kernel = kernel;
	}

	public GeoImage getActiveToolImage() {
		return hasSelectedTool() ? activeTool().getImage() : null;
	}

	public boolean hasSelectedTool() {
		return selectedToolId != MeasurementToolId.NONE;
	}

	private MeasurementTool activeTool() {
		return tools.get(selectedToolId);
	}


	public void toggleActiveTool(int mode, String fileName) {
		MeasurementTool tool = activeTool();
		if (tool != null) {
			unselect(tool);
		} else {
			select(mode, fileName);
		}
	}

	private void unselect(MeasurementTool tool) {
		tool.remove();
		selectedToolId = MeasurementToolId.NONE;
	}

	private void select(int mode, String fileName) {
		selectedToolId = MeasurementToolId.byMode(mode) ;
		setTool(selectedToolId,
				kernel.getApplication().getActiveEuclidianView()
						.addMeasurementTool(mode, fileName));
	}

	public void setTool(MeasurementToolId toolId, GeoImage image) {
		tools.put(toolId, new MeasurementTool(image));
	}

	public void clear() {
		MeasurementTool tool = activeTool();
		if (tool != null) {
			tool.remove();
		}
		tools.clear();
	}

	public void selectTool(MeasurementToolId toolId) {
		this.selectedToolId = toolId;
	}
}
