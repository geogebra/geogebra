package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;
import org.junit.Before;
import org.junit.Test;

public class MeasureControllerTest extends BaseUnitTest {
	private MeasurementController controller;
	private MeasurementTool ruler;
	private Construction cons;

	@Before
	public void setUp() {
		Kernel kernel = getKernel();
		cons = kernel.getConstruction();
		controller = new MeasurementController(this::createToolImage);
		ruler = newTool(MeasurementToolId.RULER);
		ruler.refresh();
		controller.add(ruler);
		controller.add(newTool(MeasurementToolId.PROTRACTOR));
		controller.add(newTool(MeasurementToolId.TRIANGLE_PROTRACTOR));
	}

	private GeoImage createToolImage(int mode, String fileName) {
		GeoImage image = new GeoImage(cons);
		image.setLabel(controller.getToolName(mode));
		return image;
	}

	private MeasurementTool newTool(MeasurementToolId id) {
		MeasurementTool tool = new MeasurementTool(id, "", 0.0,
				this::createToolImage, NullPenTransformer.get());
		tool.refresh();
		return tool;
	}

	@Test
	public void testUnselect() {
		controller.unselect();
		assertFalse(controller.hasSelectedTool());
	}

	@Test
	public void testSelectRuler() {
		assertToolSelected(MODE_RULER);
	}

	private void assertToolSelected(int mode) {
		controller.selectTool(mode);
		String name = controller.getToolName(mode);
		GeoImage toolImage = controller.getActiveToolImage();
		assertEquals(name, toolImage.getLabelSimple());
		assertTrue(name + " should be in construction",
				cons.isInConstructionList(toolImage));
	}

	@Test
	public void testSelectProtractor() {
		assertToolSelected(MODE_PROTRACTOR);
	}

	@Test
	public void testSelectTriangleProtractor() {
		assertToolSelected(MODE_TRIANGLE_PROTRACTOR);
	}

	@Test
	public void testToggleRulerOnOff() {
		controller.selectTool(MODE_RULER);
		controller.toggleActiveTool(MODE_RULER);
		assertNull(controller.getActiveToolImage());
	}

	@Test
	public void testUnselectRuler() {
		controller.selectTool(MODE_RULER);
		controller.unselect();
		assertFalse("Ruler is in construction", cons.isInConstructionList(ruler.getImage()));
	}

}
