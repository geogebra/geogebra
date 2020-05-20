package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.CommandProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class CreateSliderActionTest {

	private CreateSliderAction createSliderAction;

	private AppWFull app;
	private CommandProcessor commandProcessor;
	private GeoNumeric numeric;
	private GeoAngle angle;

	@Before
	public void setUp() {
		init();
		createSliderAction = new CreateSliderAction();
		numeric = commandProcessor.process("4.669");
		angle = commandProcessor.process("4.669°");
	}

	private void init() {
		if (app == null) {
			app = AppMocker.mockCas(getClass());
		}
		if (commandProcessor == null) {
			commandProcessor = new CommandProcessor(app);
		}
	}

	@Test
	public void execute() {
		createSliderAction.execute(numeric, app);
		assertThat(numeric.isSliderable(), is(true));
		assertThat(numeric.getLabelSimple(), is("a"));
	}

	@Test
	public void isAvailable() {
		checkAvailableFor(numeric);
		checkAvailableFor(angle);
	}

	private void checkAvailableFor(GeoElement element) {
		assertThat(createSliderAction.isAvailable(element), is(true));
		createSliderAction.execute(element, app);
		assertThat(createSliderAction.isAvailable(element), is(false));
	}
}