package org.geogebra.commands;

import java.util.Arrays;
import java.util.Locale;

import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgebraTest extends Assert {
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

	protected static void shouldFail(String string, String string2, App app) {
		ErrorAccumulator errorStore = new ErrorAccumulator();
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(string, false,
						errorStore, false, null);
		assertTrue(errorStore.getErrors().contains(string2));
	}

	public static String unicode(String theSpline) {
		return theSpline.replace("^2", Unicode.SUPERSCRIPT_2 + "")
				.replace("^3", Unicode.SUPERSCRIPT_3 + "")
				.replace("^4", Unicode.SUPERSCRIPT_4 + "")
				.replace("^-1",
						Unicode.SUPERSCRIPT_MINUS + "" + Unicode.SUPERSCRIPT_1)
				.replace("deg", Unicode.DEGREE_STRING);
	}

	static boolean mayHaveZeroArgs(String cmdName) {
		return Arrays
				.asList(new String[] { "DataFunction", "AxisStepX", "AxisStepY",
						"Button", "StartLogging", "StopLogging", "StartRecord",
						"ConstructionStep", "StartAnimation", "ShowAxes",
						"ShowGrid", "SetActiveView", "ZoomIn",
						"SetViewDirection", "ExportImage", "Random",
						"Textfield", "GetTime", "UpdateConstruction",
						"SelectObjects", "Turtle", "Function", "Checkbox" })
				.contains(cmdName);
	}
}
