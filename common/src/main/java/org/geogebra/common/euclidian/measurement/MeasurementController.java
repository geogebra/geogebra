package org.geogebra.common.euclidian.measurement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.util.debug.Log;

public class MeasurementController {
	private final Kernel kernel;
	private Map<MeasurementToolId, MeasurementTool> tools = new HashMap<>();
	private MeasurementToolId selectedToolId = MeasurementToolId.NONE;
	private boolean toolActive = false;

	public MeasurementController(Kernel kernel) {
		this.kernel = kernel;
		addTool(MeasurementToolId.RULER, "Ruler.svg", new RulerTransformer(
				List.of(new SimpleRulerEdge(1, 2),
						new SimpleRulerEdge(3, 4))
		));

		addTool(MeasurementToolId.PROTRACTOR, "Protactor.svg", 1 - (278.86 / 296),
				new NullPenTransformer());
//		addTool(MeasurementToolId.TRIANGLE_PROTRACTOR, "TriangleProtactor.svg", 0.0,
//				new TriangleProtractorTransformer());
	}

	public GeoImage getActiveToolImage() {
		return hasSelectedTool() ? activeTool().getImage() : null;
	}

	public boolean hasSelectedTool() {
		return selectedToolId != MeasurementToolId.NONE;
	}

	public MeasurementTool activeTool() {
		return tools.get(selectedToolId);
	}


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

	private boolean isToolSelected(int mode) {
		return MeasurementToolId.byMode(mode) == selectedToolId;
	}

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

	private void addTool(MeasurementToolId id, String fileName,
			PenTransformer transformer) {
		addTool(id, fileName, null, transformer);
	}

	private void addTool(MeasurementToolId id, String fileName, Double percent,
			PenTransformer transformer) {
		add(new MeasurementTool(id, fileName, percent, transformer));
		selectTool(id);
	}

	void add(MeasurementTool tool) {
		tools.put(tool.getId(), tool);
	}

	public void clear() {
		unselect();
	}

	public void selectTool(MeasurementToolId toolId) {
		this.selectedToolId = toolId;
	}

	public PenTransformer getTransformer() {
		return activeTool().getTransformer();
	}
}
