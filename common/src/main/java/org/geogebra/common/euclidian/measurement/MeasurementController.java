package org.geogebra.common.euclidian.measurement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;

/**
 * Class to handle the various measurement tools.
 */
public class MeasurementController {
	private final Kernel kernel;
	private final CreateToolImage toolImageF;
	private Map<MeasurementToolId, MeasurementTool> tools = new HashMap<>();
	private MeasurementToolId selectedToolId = MeasurementToolId.NONE;

	/**
	 *
	 * @param kernel {@link Kernel}
	 */
	public MeasurementController(Kernel kernel, CreateToolImage toolImageF) {
		this.kernel = kernel;
		this.toolImageF = toolImageF;
		addTool(MeasurementToolId.RULER, "Ruler.svg", null);
		addTool(MeasurementToolId.PROTRACTOR, "Protactor.svg", 1 - (278.86 / 296));
		addTool(MeasurementToolId.TRIANGLE_PROTRACTOR, "TriangleProtactor.svg", 0.0);
	}

	private void addTool(MeasurementToolId id, String fileName, Double percent) {
		add(new MeasurementTool(id, fileName, percent, toolImageF));
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
		MeasurementToolId toolId = MeasurementToolId.byMode(mode);
		if (toolId == selectedToolId) {
			unselect();
		} else {
			MeasurementToolId id = toolId;
			if (tools.containsKey(id)) {
				selectTool(id);
				refreshTool();
			}
		}
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

	private void refreshTool() {
		MeasurementTool tool = activeTool();
		tool.refresh();
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

	public boolean applyTransformer(EuclidianView view, GPoint newPoint,
			List<GPoint> previewPoints) {
		PenTransformer transformer = getTransformer();
		transformer.reset(view, previewPoints);
		if (transformer.isActive() && previewPoints.size() > 1) {
			transformer.updatePreview(newPoint);
			return true;
		}
		return false;
	}

	private PenTransformer getTransformer() {
		MeasurementTool tool = activeTool();
		return tool != null ? tool.getTransformer() : NullPenTransformer.get();
	}

	public GPoint2D activeToolCenter(EuclidianView view, GRectangle2D bounds) {
		MeasurementTool tool = activeTool();
		return tool != null && tool.hasRotationCenter()
				? tool.getRotationCenter(view)
				: calculateRotationCenter(bounds);
	}

	private GPoint2D calculateRotationCenter(GRectangle2D bounds) {
		double x = bounds.getMinX() + bounds.getWidth() / 2;
		double y = bounds.getMinY()  + bounds.getHeight() / 2;
		return new GPoint2D(x, y);
	}

	public GeoImage getToolImage(MeasurementToolId toolId) {
		selectTool(toolId);
		return getActiveToolImage();
	}

	public boolean hasActiveToolImage() {
		return getActiveToolImage() != null;
	}
}
