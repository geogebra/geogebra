package org.geogebra.common.euclidian;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class NotesSelectionTest extends BaseEuclidianControllerTest {

	@Test
	public void selectionRectangleShouldSelectPartOfStroke() {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(200, 100);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 50);
		dragEnd(250, 150);
		assertSelected(lookup("stroke1"));
	}

	@Test
	public void selectionRectangleShouldSelectPartOfMoreStrokes() {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(200, 100);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(220, 100);
		dragEnd(300, 100);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 50);
		dragEnd(250, 150);
		assertSelected(lookup("stroke1"), lookup("stroke2"));
	}

	@Test
	@Issue("MOW-1744")
	public void tabToNextGeoShouldHideBoundingBox() {
		getApp().setConfig(new AppConfigNotes());
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		dragStart(50, 50);
		dragEnd(150, 150);
		assertNotNull(getApp().getActiveEuclidianView().getBoundingBox());
		getApp().getSelectionManager().selectNextGeo();
		assertNull(getApp().getActiveEuclidianView().getBoundingBox());
	}

	private void assertSelected(GeoElement... geos) {
		assertArrayEquals(getApp().getSelectionManager().getSelectedGeos().toArray(),
				geos);
	}
}