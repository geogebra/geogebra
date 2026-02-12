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

package org.geogebra.cas;

import static org.geogebra.test.matcher.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashSet;
import java.util.Locale;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.CASTestLogger;
import org.junit.AfterClass;
import org.junit.Before;

public class BaseCASIntegrationTest {
	static public boolean silent = false;

	protected Kernel kernel;
	private AppDNoGui app;

	/**
	 * Logs all tests which don't give the expected but a valid result.
	 */
	static CASTestLogger logger;

	ArbitraryConstantRegistry arbconst;

	/**
	 * Create app and CAS.
	 */
	@Before
	public void setupCas() {
		SuiteScope suiteScope = GlobalScope.registerNewSuiteScope();

		app = new AppDNoGui(new LocalizationD(3), false);

		if (silent) {
			Log.setLogger(null);
		}

		// Set language to something else than English to test automatic
		// translation.
		app.setLanguage(Locale.GERMANY);
		// app.fillCasCommandDict();

		kernel = app.getKernel();
		arbconst = new ArbitraryConstantRegistry(
				new GeoCasCell(kernel.getConstruction()));
		logger = new CASTestLogger();

		// Setting the general timeout to 9 seconds. Feel free to change this.
		kernel.getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(9000);
	}

	/**
	 * Handles the logs about test warnings.
	 */
	@AfterClass
	public static void handleLogs() {
		if (!silent) {
			logger.handleLogs();
		}
	}

	protected void t(String input, String expectedResult,
			String... validResults) {
		ta(false, input, expectedResult, validResults);
	}

	void ta(boolean keepInput, String input,
			String expectedResult, String... validResults) {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		ta(f, keepInput, input, expectedResult, validResults);
	}

	/**
	 * Evaluates input in given CAS cell.
	 * If keepInput is false, it behaves like the default CAS mode ({@code Evaluate}).
	 * If keepInput is true, it simulates evaluation with {@code Keepinput} mode.
	 * 
	 * <p>
	 * Note: Direct calls to ta are "Not Recommended". Use {@link #t} and {@link #tk} instead.
	 * </p>
	 * 
	 * @param f
	 *            CAS cell
	 * @param keepInput
	 *            whether to use KeepInput mode
	 * @param input
	 *            The input.
	 * @param expectedResult
	 *            The expected result.
	 * @param validResults
	 *            Valid, but undesired results.
	 */
	protected void ta(GeoCasCell f, boolean keepInput, String input,
			String expectedResult, String... validResults) {
		String result;
		f.setInput(input);
		if (keepInput) {
			f.setEvalCommand("Keepinput");
		}
		processCasCell(f);

		boolean includesNumericCommand = false;
		HashSet<Command> commands = new HashSet<>();

		f.getInputVE().traverse(CommandCollector.getCollector(commands));

		if (!commands.isEmpty()) {
			for (Command cmd : commands) {
				String cmdName = cmd.getName();
				// Numeric used
				includesNumericCommand = includesNumericCommand
						|| ("Numeric".equals(cmdName)
								&& cmd.getArgumentNumber() > 1);
			}
		}

		result = f.getValue() != null
				? f.getValue()
						.toString(includesNumericCommand
								? StringTemplate.testNumeric
								: StringTemplate.testTemplate)
				: f.getOutput(StringTemplate.testTemplate);
		if (f.getValue() != null
				&& f.getValue().unwrap() instanceof GeoElement) {
			result = f.getValue().unwrap()
					.toValueString(StringTemplate.testTemplate);
		}

		assertThat(result, equalToIgnoreWhitespaces(logger, input,
				expectedResult, validResults));
	}

	protected void processCasCell(GeoCasCell f) {
		if (!f.hasVariablesOrCommands()) {
			kernel.getConstruction().addToConstructionList(f, false);
			f.computeOutput();
			f.setLabelOfTwinGeo();
		} else {
			kernel.getConstruction().removeFromConstructionList(f);
			KernelCAS.dependentCasCell(f);
		}
	}

	/**
	 * @param input
	 *            input
	 * @param expectedResult
	 *            preferred result
	 * @param validResults
	 *            alternative results
	 */
	void tk(String input, String expectedResult,
			String... validResults) {
		ta(true, input, expectedResult, validResults);
	}

	protected AppDNoGui getApp() {
		return app;
	}

	protected GeoElement lookup(String label) {
		return getApp().getKernel().lookupLabel(label);
	}
}
