package org.geogebra.web.full.main;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class NotesApiTest {

	@Before
	public void assertions() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void getContentShouldNotTriggerEvents() {
		AppW app = AppMocker
				.mockApplet(new AppletParameters("notes"));
		app.getActiveEuclidianView().getSettings().setBackgroundType(BackgroundType.POLAR);
		ArrayList<String> events = new ArrayList<>();
		app.getEventDispatcher().addEventListener(evt -> events.add(evt.getType().name()));
		app.getGgbApi().getPageContent("main");
		assertEquals(Collections.emptyList(), events);
	}
}
