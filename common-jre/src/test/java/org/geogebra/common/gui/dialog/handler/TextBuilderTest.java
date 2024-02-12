package org.geogebra.common.gui.dialog.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.EventType;
import org.geogebra.test.TestErrorHandler;
import org.junit.Assert;
import org.junit.Test;

public class TextBuilderTest extends BaseUnitTest {

	private int eventCounter = 0;

	@Test
	public void shouldOnlyFireOneEvent() {
		TextBuilder textBuilder = new TextBuilder(getApp(), add("(1,1)"), false, true);
		getApp().getEventDispatcher().addEventListener(evt -> {
			assertThat(evt.type, equalTo(EventType.ADD));
			assertThat(evt.target.getLabelSimple(), equalTo("text1"));
			assertThat(((GeoText) evt.target).isLaTeX(), equalTo(true));
			eventCounter++;
		});
		textBuilder.createText("\"\\sqrt{2}\"", TestErrorHandler.INSTANCE,
				Assert::assertTrue);
		assertThat(eventCounter, equalTo(1));
	}
}
