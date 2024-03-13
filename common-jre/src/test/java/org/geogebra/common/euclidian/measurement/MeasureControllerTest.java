package org.geogebra.common.euclidian.measurement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianConstants;
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
		controller = new MeasurementController(kernel, this::createToolImage);
		ruler = newTool(MeasurementToolId.RULER);
		ruler.refresh();
		controller.add(ruler);
		controller.add(newTool(MeasurementToolId.PROTRACTOR));
		controller.add(newTool(MeasurementToolId.TRIANGLE_PROTRACTOR));
	}

	private GeoImage createToolImage(int mode, String fileName) {
		GeoImage image = new GeoImage(cons);
		image.setLabel(MeasurementToolId.byMode(mode).toString());
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
		assertToolSelected(MeasurementToolId.RULER);
	}

	private void assertToolSelected(MeasurementToolId id) {
		controller.selectTool(id);
		String name = id.toString();
		GeoImage toolImage = controller.getActiveToolImage();
		assertEquals(name, toolImage.getLabelSimple());
		assertTrue(name + " is not in construction",
				cons.isInConstructionList(toolImage));
	}

	@Test
	public void testSelectProtractor() {
		assertToolSelected(MeasurementToolId.PROTRACTOR);
	}

	@Test
	public void testSelectTriangleProtractor() {
		assertToolSelected(MeasurementToolId.TRIANGLE_PROTRACTOR);
	}

	@Test
	public void testToggleRulerOnOff() {
		controller.selectTool(MeasurementToolId.RULER);
		controller.toggleActiveTool(EuclidianConstants.MODE_RULER);
		assertNull(controller.getActiveToolImage());
	}

	@Test
	public void testUnselectRuler() {
		controller.selectTool(MeasurementToolId.RULER);
		controller.unselect();
		assertFalse("Ruler is in construction", cons.isInConstructionList(ruler.getImage()));
	}

}
