package org.geogebra.common.main.undo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Test;

public class UndoManagerTest extends BaseUnitTest {

	@Test
	public void undoDeletionGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().setUndoActive(true);
		GeoPoint pt = add("A=(1,1)");
		getApp().storeUndoInfo();
		getApp().getSelectionManager().addSelectedGeo(pt);
		AppState initial = getCheckpoint();
		getApp().deleteSelectedObjects(false);
		assertThat(getCheckpoint(), not(initial));
		assertThat(getConstruction().isEmpty(), is(true));
		getKernel().undo();
		assertThat(lookup("A"), hasValue("(1, 1)"));
	}

	@Test
	public void undoDeletionNotes() {
		getApp().setConfig(new AppConfigNotes());
		getApp().setUndoActive(true);
		GeoPoint pt = add("A=(1,1)");
		getApp().getSelectionManager().addSelectedGeo(pt);
		AppState initial = getCheckpoint();
		getApp().deleteSelectedObjects(false);
		assertThat(getCheckpoint(), is(initial));
		assertThat(getConstruction().isEmpty(), is(true));
		getKernel().undo();
		assertThat(lookup("A"), hasValue("(1, 1)"));
	}

	@Test
	public void testRemoveActions() {
		getApp().setUndoActive(true);
		GeoElement pt = add("A=(1,1)");
		getConstruction().getUndoManager().storeAddGeo(pt);
		GeoElement pt2 = add("B=(1,2)");
		getConstruction().getUndoManager().storeAddGeo(pt2);
		pt2.remove();
		getConstruction().getUndoManager().removeActionsWithLabel("B");
		assertThat(getUndoManager().undoPossible(), is(true));
		getKernel().undo();
		assertThat(getUndoManager().undoPossible(), is(false));
		assertThat(getUndoManager().redoPossible(), is(true));
		assertThat(lookup("A"), nullValue());
		getKernel().redo();
		assertThat(lookup("A"), hasValue("(1, 1)"));
	}

	private UndoManager getUndoManager() {
		return getConstruction().getUndoManager();
	}

	private AppState getCheckpoint() {
		return getConstruction().getUndoManager().getCheckpoint(null).getAppState();
	}
}
