package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.TestEvent;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseControllerTest {
	private static AppCommon3D app;
	private static EuclidianController ec;

	@Before
	public void clear() {
		reset();
	}

	/**
	 * Setup the app
	 */
	@BeforeClass
	public static void setup() {
		app = AppCommonFactory.create3D();
		ec = app.getActiveEuclidianView().getEuclidianController();
	}

	/**
	 * @param mode
	 *            app mode
	 */
	protected void setMode(int mode) {
		app.setMode(mode);
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
		TestEvent evt = new TestEvent(x, y, type);
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
	protected static void dragStart(int x, int y) {
		TestEvent evt = new TestEvent(x, y);
		ec.setDraggingDelay(0);
		ec.wrapMousePressed(evt);
	}

	/**
	 * Finish a drag
	 * 
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	protected static void dragEnd(int x, int y) {
		TestEvent evt = new TestEvent(x, y);
		ec.wrapMouseDragged(evt, true);
		ec.wrapMouseDragged(evt, true);
		ec.wrapMouseReleased(evt);
	}

	/**
	 * Reset the app
	 */
	protected static void reset() {
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
	 * @return application
	 */
	protected AppCommon getApp() {
		return app;
	}

	/**
	 * Add object to the construction using a command
	 * 
	 * @param cmd
	 *            command
	 */
	protected void add(String cmd) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(cmd, false);
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
		for (String label : app.getGgbApi().getAllObjectNames()) {
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

	/**
	 * @param label
	 *            label
	 * @return construction element
	 */
	protected GeoElement lookup(String label) {
		return app.getKernel().lookupLabel(label);
	}

}
