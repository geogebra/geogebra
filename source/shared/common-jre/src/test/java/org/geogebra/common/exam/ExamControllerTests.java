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

package org.geogebra.common.exam;

import static org.geogebra.common.contextmenu.InputContextMenuItem.Help;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Text;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class ExamControllerTests extends BaseExamTestSetup implements ExamControllerDelegate {
	private final List<ExamState> examStates = new ArrayList<>();
	private Material activeMaterial;
	private boolean didRequestClearApps = false;
	private boolean didRequestClearClipboard = false;
	private CommandDispatcher previousCommandDispatcher;

	private void switchApp(SuiteSubApp subApp) {
		// keep references so that we can check if restrictions have been reverted correctly
		previousCommandDispatcher = getCommandDispatcher();
		examController.unregisterRestrictable(getApp());
		activeMaterial = null;
		setupApp(subApp);
	}

	@BeforeEach
	public void examControllerTestSetup() {
		examController.setDelegate(this);
		examController.addListener(examStates::add);
	}

	// State & duration

	@Test
	public void testPrepareExam() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(ExamState.IDLE, examController.getState());
		examController.prepareExam();

		assertAll(
				() -> assertNull(examController.getStartDate()), // not yet started
				() -> assertNull(examController.getFinishDate()), // not yet ended
				() -> assertEquals(ExamState.PREPARING, examController.getState()),
				() -> assertEquals(List.of(ExamState.PREPARING), examStates),
				() -> assertFalse(didRequestClearApps),
				() -> assertFalse(didRequestClearClipboard),
				() -> assertNull(activeMaterial));
	}

	@Test
	public void testStartExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);

		assertAll(
				() -> assertNotNull(examController.getStartDate()), // started
				() -> assertNull(examController.getFinishDate()), // not yet ended
				() -> assertEquals(ExamState.ACTIVE, examController.getState()),
				() -> assertEquals(List.of(ExamState.PREPARING, ExamState.ACTIVE), examStates),
				() -> assertTrue(didRequestClearApps),
				() -> assertTrue(didRequestClearClipboard),
				() -> assertNotNull(activeMaterial));

	}

	@Test
	public void testStartExamWithoutActiveContext() {
		examController.prepareExam();
		assertThrows(IllegalStateException.class,
				() -> examController.startExam(ExamType.GENERIC, null),
				"starting exam without calling setActiveContext() should throw");
	}

	// start exam without calling prepare() first (e.g., in crash recovery)
	@Test
	public void testStartExamWithoutPrepare() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.VLAANDEREN, null);
		assertEquals(ExamState.ACTIVE, examController.getState());
	}

	@Test
	public void testFinishExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		examController.finishExam();

		assertAll(
				() -> assertNotNull(examController.getStartDate()), // started
				() -> assertNotNull(examController.getFinishDate()), // ended
				() -> assertNotNull(examController.getExamSummary(
						getApp().getConfig(), getApp().getLocalization())),
				() -> assertEquals(ExamState.FINISHED, examController.getState()),
				() -> assertEquals(List.of(
						ExamState.PREPARING,
						ExamState.ACTIVE,
						ExamState.FINISHED), examStates));
	}

	@Test
	public void testExitExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		examController.finishExam();
		examController.exitExam();

		assertAll(
				() -> assertNull(examController.getStartDate()),
				() -> assertNull(examController.getFinishDate()),
				// back to initial state
				() -> assertEquals(ExamState.IDLE, examController.getState()),
				() -> assertEquals(List.of(
						ExamState.PREPARING,
						ExamState.ACTIVE,
						ExamState.FINISHED,
						ExamState.IDLE), examStates),
				() -> assertTrue(didRequestClearApps),
				() -> assertTrue(didRequestClearClipboard),
				() -> assertNull(activeMaterial));
	}

	// Restrictions

	@Test
	public void testRestrictedSubApp() {
		setupApp(SuiteSubApp.CAS);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null); // doesn't allow CAS
		assertAll(
				() -> assertEquals(SuiteSubApp.GRAPHING, getCurrentSubApp()),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testNonRestrictedSubApp() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		assertAll(
				() -> assertEquals(SuiteSubApp.GRAPHING, getCurrentSubApp()),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testRestrictions() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.VLAANDEREN, null);

		assertAll(
				// feature restrictions
				() -> assertTrue(examController
						.isFeatureRestricted(ExamFeatureRestriction.HIDE_SPECIAL_POINTS)),
				// command restrictions
				() -> assertFalse(getCommandDispatcher()
						.isAllowedByCommandFilters(Commands.Derivative)),
				// TODO commandArgumentFilters
				// expression restrictions
				() -> assertNull(evaluate("true || false")),
				// context menu restrictions
				() -> assertEquals(
						List.of(Text, Help),
						contextMenuFactory.makeInputContextMenu(true)));

		examController.finishExam();
		assertFalse(getCommandDispatcher().isAllowedByCommandFilters(Commands.Derivative));
		examController.exitExam();
		assertTrue(getCommandDispatcher().isAllowedByCommandFilters(Commands.Derivative));
		assertFalse(examController.isFeatureRestricted(ExamFeatureRestriction.HIDE_SPECIAL_POINTS));
	}

	@Test
	public void testSwitchToRestrictedSubApp() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();

		examController.startExam(ExamType.BAYERN_CAS, null);
		assertAll(
				() -> assertEquals(SuiteSubApp.CAS, getCurrentSubApp()),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testRestrictionsWhenSwitchingApps() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		assertFalse(getCommandDispatcher().isAllowedByCommandFilters(Commands.Derivative));

		switchApp(SuiteSubApp.GEOMETRY);
		assertAll(
				// restrictions should be reverted
				// on the previous (Graphing app) command dispatcher...
				() -> assertTrue(previousCommandDispatcher
						.isAllowedByCommandFilters(Commands.Derivative)),
				// ...and applied to the new (Geometry app) command dispatcher
				() -> assertFalse(getCommandDispatcher()
						.isAllowedByCommandFilters(Commands.Derivative)),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testFeatureRestrictionsWhenSwitchingApps() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.CVTE, null);
		// effects from ExamRestrictables should be applied on current app/kernel/etc
		assertNull(getKernel().getSurds());

		Kernel previousKernel = getKernel();
		switchApp(SuiteSubApp.GEOMETRY);

		// effects from ExamRestrictables should be reverted on previous app/kernel/etc
		assertNotNull(previousKernel.getSurds());
		// effects from ExamRestrictables should be applied on new app/kernel/etc
		assertNull(getKernel().getSurds());
	}

	@Test
	public void testLanguagePropertyDisabledDuringExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.GENERIC, null);

		LanguageProperty languageProperty =
				new LanguageProperty(getApp(), getApp().getLocalization());
		assertFalse(languageProperty.isEnabled()); // should be disabled during exam
	}

	@Test
	public void testToolsExcludedDuringExam() {
		setupApp(SuiteSubApp.GEOMETRY);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);

		assertFalse(getApp().getAvailableTools().contains(EuclidianConstants.MODE_POINT));
	}

	@Test
	public void testCommandArgumentFilter() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);

		assertNull(evaluate("Max(1, 2)"));
		assertThat(errorAccumulator.getErrorsSinceReset(),
				containsString("Illegal number of arguments"));
		errorAccumulator.resetError();
	}

	@Test
	public void testSyntaxFilter() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);

		AutocompleteProvider provider = new AutocompleteProvider(getApp(), false);
		Optional<AutocompleteProvider.Completion> completion =
				provider.getCompletions("Max").filter(it -> it.getCommand().equals("Max"))
						.findFirst();
		assertAll(
				() -> assertTrue(completion.isPresent()),
				() -> assertEquals(1, completion.get().syntaxes.size()));
	}

	@Test
	@Issue("APPS-5912")
	public void testCommandRestrictionsWhenStartingDifferentExams() {
		setupApp(SuiteSubApp.GRAPHING);

		examController.prepareExam();
		examController.startExam(ExamType.GENERIC, null);
		assertAll(
				() -> assertNotNull(evaluate("f(x) = x")),
				() -> assertNotNull(evaluate("Derivative(f)")));
		examController.finishExam();
		examController.exitExam();

		examController.prepareExam();
		examController.startExam(ExamType.IB, null);
		assertAll(
				() -> assertNotNull(evaluate("f(x) = x")),
				() -> assertNull(evaluate("Derivative(f)")));
		errorAccumulator.resetError();
		examController.finishExam();
		examController.exitExam();
	}

	@Test
	public void testAlgoDispatcherDisabledAlgorithms() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);

		GeoPoint point = evaluateGeoElement("(1,2)");
		GeoConic conic = evaluateGeoElement("(x^2)/2+(y^2)/2=2");
		GeoLine line = evaluateGeoElement("y=2x+5");

		AlgoDispatcher algoDispatcher = getKernel().getAlgoDispatcher();
		assertAll(
				() -> assertNull(algoDispatcher.tangent(new String[]{}, point, conic)),
				() -> assertNull(algoDispatcher.tangent(new String[]{}, line, conic)),
				() -> assertNotNull(algoDispatcher.commonTangents(new String[]{}, conic, conic)));
	}

	@Test
	public void testVisibilityRestrictions() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);
		Set<VisibilityRestriction> visibilityRestrictions =
				TestExamRestrictions.createVisibilityRestrictions();

		GeoElement allowedGeoElement = evaluateGeoElement("x = 1");
		assertFalse(VisibilityRestriction.isVisibilityRestricted(allowedGeoElement,
				visibilityRestrictions));

		GeoElement restrictedGeoElement = evaluateGeoElement("(1, 2)");
		assertTrue(VisibilityRestriction.isVisibilityRestricted(restrictedGeoElement,
				visibilityRestrictions));

		assertAll(
				() -> assertTrue(allowedGeoElement.isEuclidianVisible()),
				() -> assertTrue(allowedGeoElement.isEuclidianToggleable()),
				() -> assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
						getApp().getLocalization(), List.of(allowedGeoElement))),

				() -> assertFalse(restrictedGeoElement.isEuclidianVisible()),
				() -> assertFalse(restrictedGeoElement.isEuclidianToggleable()),
				() -> assertNull(geoElementPropertiesFactory.createShowObjectProperty(
						getApp().getLocalization(), List.of(restrictedGeoElement))));
	}

	@Test
	public void testRestrictedVisibilityInEuclidianViewAfterEditingUnrestrictedInput() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);
		Set<VisibilityRestriction> visibilityRestrictions =
				TestExamRestrictions.createVisibilityRestrictions();

		GeoElement geoElement = evaluateGeoElement("f(x) = x");

		assertAll(
				() -> assertFalse(VisibilityRestriction.isVisibilityRestricted(geoElement,
						visibilityRestrictions)),
				() -> assertTrue(geoElement.isEuclidianVisible()),
				() -> assertTrue(geoElement.isEuclidianToggleable()),
				() -> assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
						getApp().getLocalization(), List.of(geoElement))));

		editGeoElement(geoElement, "f(x) = x > 2");

		assertAll(
				() -> assertTrue(VisibilityRestriction.isVisibilityRestricted(geoElement,
						visibilityRestrictions)),
				() -> assertFalse(geoElement.isEuclidianVisible()),
				() -> assertFalse(geoElement.isEuclidianToggleable()),
				() -> assertNull(geoElementPropertiesFactory.createShowObjectProperty(
						getApp().getLocalization(), List.of(geoElement))));
	}

	@Test
	public void testSyntaxHelperIsUnrestrictedAfterExamMode() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsFactory(TestExamRestrictions::new);
		examController.startExam(ExamType.GENERIC, null);
		assertTrue(autocompleteProvider.getCompletions("NDerivative").findAny().isEmpty());
		examController.finishExam();
		examController.exitExam();
		assertFalse(autocompleteProvider.getCompletions("NDerivative").findAny().isEmpty());
	}

	@Issue("APPS-6698")
	@Test
	public void testRestrictionsOnlyAppliedOnce() {
		setupApp(SuiteSubApp.CAS); // note: disabled subapp, will cause app switch on exam start
		TestExamRestrictions restrictions = new TestExamRestrictions(ExamType.GENERIC);
		examController.setExamRestrictionsFactory(examType -> restrictions);

		examController.prepareExam();
		examController.startExam(ExamType.GENERIC, null);
		assertEquals(1, restrictions.appliedCount);
	}

	// ExamControllerDelegate

	@Override
	public void examClearApps() {
		activeMaterial = null;
		didRequestClearApps = true;
	}

	@Override
	public void examClearClipboard() {
		didRequestClearClipboard = true;
	}

	@Override
	public void examSetActiveMaterial(@CheckForNull Material material) {
		activeMaterial = material;
	}

	@Override
	public @CheckForNull Material examGetActiveMaterial() {
		return activeMaterial;
	}

	@Override
	public @CheckForNull SuiteSubApp examGetCurrentSubApp() {
		return getCurrentSubApp();
	}

	@Override
	public void examSwitchSubApp(@Nonnull SuiteSubApp subApp) {
		if (!subApp.equals(getCurrentSubApp())) {
			switchApp(subApp);
		}
	}

	private SuiteSubApp getCurrentSubApp() {
		AppConfig config = getApp().getConfig();
		String appCode = Objects.equals(config.getAppCode(), GeoGebraConstants.SUITE_APPCODE)
				? config.getSubAppCode() : config.getAppCode();
		if (appCode != null) {
			return SuiteSubApp.forCode(appCode);
		}
		return null;
	}
}
