/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
