package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Before;
import org.junit.Test;

public class MeasureControllerTest extends BaseUnitTest {
	private MeasurementController controller;
	private MeasurementTool ruler;
	private Construction cons;

	@Before
	public void setUp() {
		getApp().setConfig(new AppConfigNotes());
		Kernel kernel = getKernel();
		cons = kernel.getConstruction();
		controller = new MeasurementController(this::createToolImage);
		ruler = controller.getTool(MeasurementToolId.RULER);
	}

	private GeoImage createToolImage(int mode, String fileName) {
		return MeasurementToolTransformerTest.createToolImage(mode, fileName, getConstruction(),
				getApp().getActiveEuclidianView());
	}

	@Test
	public void testUnselect() {
		controller.unselect();
		assertFalse(controller.hasSelectedTool());
	}

	@Test
	public void testSelectRuler() {
		assertToolSelected(MODE_RULER, "Ruler.svg");
	}

	@Test
	public void testRotationCenter() {
		controller.toggleActiveTool(MODE_RULER);
		GPoint2D rotationCenter = controller.getActiveToolCenter(
				controller.getActiveToolImage(), getApp().getActiveEuclidianView());
		assertEquals(109.51, rotationCenter.getX(), .1);
		assertEquals(260, rotationCenter.getY(), .1);
	}

	private void assertToolSelected(int mode, String filename) {
		controller.toggleActiveTool(mode);
		GeoImage toolImage = controller.getActiveToolImage();
		assertEquals(filename, toolImage.getImageFileName());
		assertTrue(filename + " should be in construction",
				cons.isInConstructionList(toolImage));
	}

	@Test
	public void testSelectProtractor() {
		assertToolSelected(MODE_PROTRACTOR, "Protractor.svg");
	}

	@Test
	public void testSelectTriangleProtractor() {
		assertToolSelected(MODE_TRIANGLE_PROTRACTOR, "TriangleProtractor.svg");
	}

	@Test
	public void testToggleRulerOnOff() {
		controller.selectTool(MODE_RULER);
		controller.toggleActiveTool(MODE_RULER);
		assertNull(controller.getActiveToolImage());
	}

	@Test
	public void testUnselectRuler() {
		controller.toggleActiveTool(MODE_RULER);
		controller.unselect();
		assertFalse("Ruler is in construction", cons.isInConstructionList(ruler.getImage()));
	}

}
