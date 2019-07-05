package org.geogebra.common.kernel.commands;

import java.util.Locale;

import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;

public class AlgebraTest extends Assert {
	/**
	 * @return test app
	 */
	public static AppDNoGui createApp() {
		AppDNoGui app2 = new AppDNoGui(new LocalizationD(3), false);
		app2.setLanguage(Locale.US);

		// make sure x=y is a line, not plane
		app2.getGgbApi().setPerspective("1");
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app2.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
		return app2;
	}

}
