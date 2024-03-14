package org.geogebra.common.euclidian.measurement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoImage;

/**
 * Class to handle the various measurement tools.
 */
public final class MeasurementController {
	private final CreateToolImage toolImageF;
	private final Map<MeasurementToolId, MeasurementTool> tools = new HashMap<>();
	private MeasurementToolId selectedToolId = MeasurementToolId.NONE;

	/**
	 *
	 * @param toolImageF interface to create the image of the tool.
	 */
	public MeasurementController(CreateToolImage toolImageF) {
		this.toolImageF = toolImageF;
		addTool(MeasurementToolId.RULER, "Ruler.svg", null);
		addTool(MeasurementToolId.PROTRACTOR, "Protactor.svg", 1 - (278.86 / 296));
		addTool(MeasurementToolId.TRIANGLE_PROTRACTOR, "TriangleProtactor.svg", 0.0);
	}

	private void addTool(MeasurementToolId id, String fileName, Double percent) {
		add(new MeasurementTool(id, fileName, percent, toolImageF, createTransformer(id)));
		selectTool(id);
	}

	private PenTransformer createTransformer(MeasurementToolId id) {
		List<MeasurementToolEdge> edges = id.getEdges();
		return edges != null ? new MeasurementToolTransformer(this, edges)
				: NullPenTransformer.get();
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
	 * @param view {@link EuclidianView}
	 * @param bounds of the tool image
	 * @return the rotation center of the active tool.
	 */
	public GPoint2D getActiveToolCenter(EuclidianView view, GRectangle2D bounds) {
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

	/**
	 *
	 * @return if has an active tool with image.
	 */
	public boolean hasActiveToolImage() {
		return getActiveToolImage() != null;
	}

	/**
	 * Removes tool specified by toolId.
	 * @param toolId to remove
	 */
	public void removeTool(MeasurementToolId toolId) {
		selectTool(toolId);
		GeoImage toolImage = getActiveToolImage();
		if (toolImage != null) {
			toolImage.remove();
		}
		clear();
	}
}
