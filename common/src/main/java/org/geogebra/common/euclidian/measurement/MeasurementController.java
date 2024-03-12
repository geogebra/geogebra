package org.geogebra.common.euclidian.measurement;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.util.debug.Log;

/**
 * Class to handle the various measurement tools.
 */
public class MeasurementController {
	private final Kernel kernel;
	private Map<MeasurementToolId, MeasurementTool> tools = new HashMap<>();
	private MeasurementToolId selectedToolId = MeasurementToolId.NONE;
	private boolean toolActive = false;

	/**
	 *
	 * @param kernel {@link Kernel}
	 */
	public MeasurementController(Kernel kernel) {
		this.kernel = kernel;
		addTool(MeasurementToolId.RULER, "Ruler.svg", null);
		addTool(MeasurementToolId.PROTRACTOR, "Protactor.svg", 1 - (278.86 / 296));
		addTool(MeasurementToolId.TRIANGLE_PROTRACTOR, "TriangleProtactor.svg", 0.0);
	}

	private void addTool(MeasurementToolId id, String fileName, Double percent) {
		add(new MeasurementTool(id, fileName, percent));
		selectTool(id);
	}

	void add(MeasurementTool tool) {
		tools.put(tool.getId(), tool);
	}

	/**
	 *
	 * @return the image of the currently active measurement tool if any.
	 */
	public GeoImage getActiveToolImage() {
		return hasSelectedTool()
				? activeTool().getImage()
				: null;
	}

	/**
	 *
	 * @return if there is a selected measurement tool.
	 */
	public boolean hasSelectedTool() {
		return selectedToolId != MeasurementToolId.NONE;
	}

	/**
	 *
	 * @return the currently active measurement tool if any.
	 */
	public MeasurementTool activeTool() {
		return tools.get(selectedToolId);
	}

	/**
	 * Shows/hides the the measurement tool specified by the mode.
	 * @param mode of the measurement tool
	 */
	public void toggleActiveTool(int mode) {
		if (isToolSelected(mode)) {
			unselect();
		} else {
			MeasurementToolId id = MeasurementToolId.byMode(mode);
			if (tools.containsKey(id)) {
				selectTool(id);
				refreshTool(id);
			}
		}
	}

	/**
	 *
	 * @param mode to check
	 * @return if the measurement tool that belongs to the given mode is selected.
	 */
	private boolean isToolSelected(int mode) {
		return MeasurementToolId.byMode(mode) == selectedToolId;
	}

	/**
	 * Hides the active measurement tool.
	 */
	void unselect() {
		MeasurementTool tool = activeTool();
		if (tool != null) {
			tool.remove();
		}
		selectedToolId = MeasurementToolId.NONE;
	}

	private void refreshTool(MeasurementToolId id) {
		MeasurementTool tool = activeTool();

		if (tool == null) {
			Log.error("No such tool: " + tool);
		}

		tool.refresh(kernel.getApplication().getActiveEuclidianView()::addMeasurementTool);
	}

	/**
	 * ??? Clears all the measurement tools previously added
	 */
	public void clear() {
		unselect();
	}

	/**
	 * Selects the measurement tool given by its id.
	 * @param toolId to select.
	 */
	public void selectTool(MeasurementToolId toolId) {
		this.selectedToolId = toolId;
	}

	/**
	 *
	 * @return the pen transformer of the active measurement tool.
	 */
	public PenTransformer getTransformer() {
		return activeTool().getTransformer();
	}
}
