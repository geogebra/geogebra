package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.TestEvent;
import org.junit.Before;

public class BaseEuclidianControllerTest extends BaseUnitTest {

	private EuclidianController ec;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	/**
	 * Set up the controller
	 */
	@Before
	public void setUpController() {
		ec = getApp().getActiveEuclidianView().getEuclidianController();
		reset();
	}

	/**
	 * @param mode
	 *            app mode
	 */
	protected void setMode(int mode) {
		getApp().setMode(mode);
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	protected void click(int x, int y) {
		click(x, y, null);
	}

	protected void click(int x, int y, PointerEventType type) {
		TestEvent evt = new TestEvent(x, y, type, false);
		ec.wrapMousePressed(evt);
		ec.wrapMouseReleased(evt);
	}

	/**
	 * Start a drag
	 * 
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	protected void dragStart(int x, int y, boolean right) {
		TestEvent evt = new TestEvent(x, y, null, right);
		ec.setDraggingDelay(0);
		ec.wrapMousePressed(evt);
	}

	protected void dragStart(int x, int y) {
		dragStart(x, y, false);
	}

	/**
	 * Finish a drag
	 * 
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	protected void dragEnd(int x, int y, boolean right) {
		TestEvent evt = new TestEvent(x, y, null, right);
		ec.wrapMouseDragged(evt, true);
		ec.wrapMouseDragged(evt, true);
		ec.wrapMouseReleased(evt);
	}

	protected void pointerRelease(int x, int y) {
		TestEvent evt = new TestEvent(x, y, null, false);
		ec.wrapMouseReleased(evt);
	}

	protected void drag(int x, int y) {
		TestEvent evt = new TestEvent(x, y, null, false);
		ec.wrapMouseDragged(evt, true);
	}

	protected void dragEnd(int x, int y) {
		dragEnd(x, y, false);
	}

	/**
	 * Reset the app
	 */
	protected void reset() {
		AppCommon app = getApp();
		app.getKernel().clearConstruction(true);
		app.initDialogManager(true);
		app.getActiveEuclidianView().clearView();
		app.getSettings().beginBatch();
		app.getActiveEuclidianView().getSettings().reset();
		app.getActiveEuclidianView().getSettings().setShowAxes(false, false);

		app.getActiveEuclidianView().getSettings().setCoordSystem(0, 0, 50, 50,
				true);
		app.getActiveEuclidianView().getSettings()
				.setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_OFF);
		app.getSettings().endBatch();
		ec.setLastMouseUpLoc(null);
	}

	/**
	 * Reset last mouse location in the controller
	 */
	protected void resetMouseLocation() {
		ec.setLastMouseUpLoc(null);
	}

	/**
	 * @param desc
	 *            expected definitions of all visible objects in construction
	 *            order
	 */
	protected final void checkContent(String... desc) {
		checkContentWithVisibility(true, desc);
	}

	/**
	 * @param desc
	 *            expected definitions of all hidden objects in construction
	 *            order
	 */
	protected final void checkHiddenContent(String... desc) {
		checkContentWithVisibility(false, desc);
	}

	/**
	 * @param visible
	 *            visibility filter
	 * @param desc
	 *            expected definitions of all hidden objects in construction
	 *            order
	 */
	protected void checkContentWithVisibility(boolean visible, String... desc) {
		int i = 0;
		for (String label : getApp().getGgbApi().getAllObjectNames()) {
			GeoElement geo = lookup(label);
			if (geo.isEuclidianVisible() == visible) {
				assertTrue(
						"Extra element: "
								+ geo.toString(StringTemplate.editTemplate),
						i < desc.length);

				assertEquals(desc[i],
						geo.toString(StringTemplate.editTemplate));
				i++;
			}
		}
		assertEquals(desc.length, i);
	}

	protected void checkContentLabels(String... labels) {
		assertEquals(Arrays.asList(labels),
				Arrays.asList(getApp().getGgbApi().getAllObjectNames()));
	}

	protected GeoImage createImage() {
		GeoImage img = new GeoImage(getApp().getKernel().getConstruction());
		img.setImageFileName("foo.png", 50, 50);
		return img;
	}

}
