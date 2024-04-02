package org.geogebra.common.euclidian.measurement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;

/**
 * Class to handle the various measurement tools.
 */
public final class MeasurementController {
	private final CreateToolImage toolImageFactory;
	private final Map<Integer, MeasurementTool> tools = new HashMap<>();
	private int selectedMode = -1;

	/**
	 *
	 * @param toolImageFactory interface to create the image of the tool.
	 */
	public MeasurementController(CreateToolImage toolImageFactory) {
		this.toolImageFactory = toolImageFactory;
		addTool(MeasurementToolId.RULER, "Ruler.svg", null);
		addTool(MeasurementToolId.PROTRACTOR, "Protractor.svg", 1 - (278.86 / 296));
		addTool(MeasurementToolId.TRIANGLE_PROTRACTOR, "TriangleProtractor.svg", 0.0);
	}

	private void addTool(MeasurementToolId id, String fileName, Double percent) {
		add(new MeasurementTool(id, fileName, percent, toolImageFactory, createTransformer(id)));
	}

	private PenTransformer createTransformer(MeasurementToolId id) {
		List<MeasurementToolEdge> edges = id.getEdges();
		return edges != null ? new MeasurementToolTransformer(this, edges)
				: NullPenTransformer.get();
	}

	void add(MeasurementTool tool) {
		tools.put(tool.getId().getMode(), tool);
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
		return selectedMode > 0;
	}

	/**
	 *
	 * @return the currently active measurement tool if any.
	 */
	public MeasurementTool activeTool() {
		return tools.get(selectedMode);
	}

	/**
	 * Shows/hides the the measurement tool specified by the mode.
	 * @param mode of the measurement tool
	 */
	public void toggleActiveTool(int mode) {
		if (mode == selectedMode) {
			unselect();
		} else {
			if (tools.containsKey(mode)) {
				selectTool(mode);
				refreshTool();
			}
		}
	}

	/**
	 * Hides the active measurement tool.
	 */
	public void unselect() {
		MeasurementTool tool = activeTool();
		if (tool != null) {
			tool.remove();
		}
		selectTool(-1);
	}

	private void refreshTool() {
		MeasurementTool tool = activeTool();
		tool.refresh();
	}

	/**
	 * Selects the measurement tool given by its id.
	 * @param mode of tool to select.
	 */
	public void selectTool(int mode) {
		this.selectedMode = mode;
	}

	/**
	 * Apply tool to a new point of penstroke: straight lines with ruler etc.
	 *
	 * @param view {@link EuclidianView}
	 * @param newPoint to transform by the tool.
	 * @param previewPoints the existing preview points of penstroke.
	 * @return the transformed new point.
	 */
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

	/**
	 *
	 * @param geo to check if it is the active tool.
	 * @param view {@link EuclidianView}
	 *
	 * @return the rotation center of the active tool.
	 */
	public GPoint2D getActiveToolCenter(GeoElement geo, EuclidianView view) {
		MeasurementTool tool = activeTool();
		if (tool == null || geo != tool.getImage() || !tool.hasRotationCenter()) {
			return null;
		}

		return tool.getRotationCenter(view);
	}

	/**
	 *
	 * @return if has an active tool with image.
	 */
	public boolean hasActiveToolImage() {
		return getActiveToolImage() != null;
	}

	/**
	 * Removes tool specified by mode.
	 * @param mode of tool to remove
	 */
	public void removeTool(Integer mode) {
		MeasurementTool tool = tools.get(mode);
		GeoImage toolImage = tool != null ? tool.getImage() : null;
		if (toolImage != null) {
			toolImage.remove();
		}
		unselect();
	}

	String getToolName(int mode) {
		MeasurementTool tool = tools.get(mode);
		return tool != null ? tool.toString() : "";
	}
}
