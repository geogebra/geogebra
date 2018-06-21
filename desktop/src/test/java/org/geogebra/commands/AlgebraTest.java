package org.geogebra.commands;

import java.util.Arrays;
import java.util.Locale;
import java.util.TreeSet;

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
		if (!errorStore.getErrors().contains(string2)) {
			fail(string + ":" + errorStore.getErrors());
		}
	}

	protected static void dummySyntaxesShouldFail(String cmdName,
			String[] syntaxLines, App app) {
		TreeSet<Integer> available = new TreeSet<>();
		for (String line : syntaxLines) {
			int args = line.split(",").length;
			available.add(args);
			StringBuilder withArgs = new StringBuilder(cmdName).append("(");
			for (int i = 0; i < args - 1; i++) {
				withArgs.append("space,");
			}
			withArgs.append("space)");
			if (args > 0 && !"Delete".equals(cmdName)
					&& !"ConstructionStep".equals(cmdName)
					&& !"Text".equals(cmdName) && !"LaTeX".equals(cmdName)
					&& !"RunClickScript".equals(cmdName)
					&& !"RunUpdateScript".equals(cmdName)
					&& !"Defined".equals(cmdName)
					&& !"StopLogging".equals(cmdName)
					&& !"AreEqual".equals(cmdName)
					&& !"AreCongruent".equals(cmdName)
					&& !"Textfield".equals(cmdName)
					&& !"SetViewDirection".equals(cmdName)
					&& !"GetTime".equals(cmdName)
					&& !"CopyFreeObject".equals(cmdName)
					&& !"SetActiveView".equals(cmdName)
					&& !"Name".equals(cmdName)
					&& !"SelectObjects".equals(cmdName)
					&& !"Dot".equals(cmdName) && !"Cross".equals(cmdName)
					&& !"SetConstructionStep".equals(cmdName)
					&& !"TableText".equals(cmdName) && !"Q1".equals(cmdName)
					&& !"Q3".equals(cmdName) && !"SetValue".equals(cmdName)) {

				shouldFail(withArgs.toString(), "arg", app);
			}
		}
		if (syntaxLines.length > 0 && !AlgebraTest.mayHaveZeroArgs(cmdName)) {
			shouldFail(cmdName + "()", "Illegal number of arguments: 0", app);
		}

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
