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
