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
 
package org.geogebra.desktop.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Test;

public class CallJavaScriptTest {

	@Test
	public void testAlert() {
		AppDNoGui app = new AppDNoGui(new LocalizationCommonUTF(3), true);
		CallJavaScript.evalGlobalScript(app);
		((ScriptManagerD) app.getScriptManager()).evalJavaScript(
				"alert(1);alert(undefined,2);ggbApplet.evalCommand('a=42')");
		assertThat(app.getKernel().lookupLabel("a").evaluateDouble(), equalTo(42.0));
	}
}
