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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

public final class ExamControllerTests extends BaseExamTests {

	// State & duration

	@Test
	public void testPrepareExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
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
		setInitialApp(SuiteSubApp.GRAPHING);
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
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.VLAANDEREN, null);
		assertEquals(ExamState.ACTIVE, examController.getState());
	}

	@Test
	public void testFinishExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		examController.finishExam();

		assertAll(
				() -> assertNotNull(examController.getStartDate()), // started
				() -> assertNotNull(examController.getFinishDate()), // ended
				() -> assertNotNull(examController.getExamSummary(
						app.getConfig(), app.getLocalization())),
				() -> assertEquals(ExamState.FINISHED, examController.getState()),
				() -> assertEquals(List.of(
						ExamState.PREPARING,
						ExamState.ACTIVE,
						ExamState.FINISHED), examStates));
	}

	@Test
	public void testExitExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
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
		setInitialApp(SuiteSubApp.CAS);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null); // doesn't allow CAS
		assertAll(
				() -> assertEquals(SuiteSubApp.GRAPHING, currentSubApp),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testNonRestrictedSubApp() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		assertAll(
				() -> assertEquals(SuiteSubApp.GRAPHING, currentSubApp),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testRestrictions() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(new TestExamRestrictions(ExamType.VLAANDEREN));
		examController.startExam(ExamType.VLAANDEREN, null);

		assertAll(
				// feature restrictions
				() -> assertTrue(examController
						.isFeatureRestricted(ExamFeatureRestriction.HIDE_SPECIAL_POINTS)),
				// command restrictions
				() -> assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative)),
				// TODO commandArgumentFilters
				// expression restrictions
				() -> assertNull(evaluate("true || false")),
				// context menu restrictions
				() -> assertEquals(
						List.of(Text, Help),
						contextMenuFactory.makeInputContextMenu(true)));

		examController.finishExam();
		assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
		examController.exitExam();
		assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
		assertFalse(examController
				.isFeatureRestricted(ExamFeatureRestriction.HIDE_SPECIAL_POINTS));
	}

	@Test
	public void testSwitchToRestrictedSubApp() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();

		examController.setExamRestrictionsForTesting(
				ExamRestrictions.forExamType(ExamType.BAYERN_CAS)); // only allows CAS app
		examController.startExam(ExamType.BAYERN_CAS, null);
		assertAll(
				() -> assertEquals(SuiteSubApp.CAS, currentSubApp),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testRestrictionsWhenSwitchingApps() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));

		switchApp(SuiteSubApp.GEOMETRY);
		assertAll(
				// restrictions should be reverted
				// on the previous (Graphing app) command dispatcher...
				() -> assertTrue(previousCommandDispatcher
						.isAllowedByCommandFilters(Commands.Derivative)),
				// ...and applied to the new (Geometry app) command dispatcher
				() -> assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative)),
				() -> assertNotNull(activeMaterial));
	}

	@Test
	public void testFeatureRestrictionsWhenSwitchingApps() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.CVTE, null);
		// effects from ExamRestrictables should be applied on current app/kernel/etc
		assertNull(app.getKernel().getSurds());

		Kernel previousKernel = app.getKernel();
		switchApp(SuiteSubApp.GEOMETRY);

		// effects from ExamRestrictables should be reverted on previous app/kernel/etc
		assertNotNull(previousKernel.getSurds());
		// effects from ExamRestrictables should be applied on new app/kernel/etc
		assertNull(app.getKernel().getSurds());
	}

	@Test
	public void testLanguagePropertyDisabledDuringExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.GENERIC, null);

		GlobalScope.examController = examController;
		LanguageProperty languageProperty = new LanguageProperty(app, app.getLocalization());
		assertFalse(languageProperty.isEnabled()); // should be disabled during exam
	}

	@Test
	public void testToolsExcludedDuringExam() {
		setInitialApp(SuiteSubApp.GEOMETRY);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(new TestExamRestrictions(ExamType.GENERIC));
		examController.startExam(ExamType.GENERIC, null);

		assertFalse(app.getAvailableTools().contains(EuclidianConstants.MODE_POINT));
	}

	@Test
	public void testCommandArgumentFilter() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(new TestExamRestrictions(ExamType.GENERIC));
		examController.startExam(ExamType.GENERIC, null);

		assertNull(evaluate("Max(1, 2)"));
		assertThat(errorAccumulator.getErrorsSinceReset(),
				containsString("Illegal number of arguments"));
		errorAccumulator.resetError();
	}

	@Test
	public void testSyntaxFilter() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(
				new TestExamRestrictions(ExamType.GENERIC));
		examController.startExam(ExamType.GENERIC, null);

		AutocompleteProvider provider = new AutocompleteProvider(app, false);
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
		setInitialApp(SuiteSubApp.GRAPHING);

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
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(
				new TestExamRestrictions(ExamType.GENERIC));
		examController.startExam(ExamType.GENERIC, null);

		GeoPoint point = (GeoPoint) evaluateGeoElement("(1,2)");
		GeoConic conic = (GeoConic) evaluateGeoElement("(x^2)/2+(y^2)/2=2");
		GeoLine line = (GeoLine) evaluateGeoElement("y=2x+5");

		assertAll(
				() -> assertNull(algoDispatcher.tangent(new String[]{}, point, conic)),
				() -> assertNull(algoDispatcher.tangent(new String[]{}, line, conic)),
				() -> assertNotNull(algoDispatcher.commonTangents(new String[]{}, conic, conic))
		);
	}

	@Test
	public void testVisibilityRestrictions() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(new TestExamRestrictions(ExamType.GENERIC));
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
						app.getLocalization(), List.of(allowedGeoElement))),

				() -> assertFalse(restrictedGeoElement.isEuclidianVisible()),
				() -> assertFalse(restrictedGeoElement.isEuclidianToggleable()),
				() -> assertNull(geoElementPropertiesFactory.createShowObjectProperty(
						app.getLocalization(), List.of(restrictedGeoElement))));
	}

	@Test
	public void testRestrictedVisibilityInEuclidianViewAfterEditingUnrestrictedInput() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(new TestExamRestrictions(ExamType.GENERIC));
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
						app.getLocalization(), List.of(geoElement))));

		editGeoElement(geoElement, "f(x) = x > 2");

		assertAll(
				() -> assertTrue(VisibilityRestriction.isVisibilityRestricted(geoElement,
						visibilityRestrictions)),
				() -> assertFalse(geoElement.isEuclidianVisible()),
				() -> assertFalse(geoElement.isEuclidianToggleable()),
				() -> assertNull(geoElementPropertiesFactory.createShowObjectProperty(
						app.getLocalization(), List.of(geoElement))));
	}

	@Test
	public void testSyntaxHelperIsUnrestrictedAfterExamMode() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.setExamRestrictionsForTesting(new TestExamRestrictions(ExamType.GENERIC));
		examController.startExam(ExamType.GENERIC, null);
        assertTrue(autocompleteProvider.getCompletions("NDerivative").findAny().isEmpty());
		examController.finishExam();
		examController.exitExam();
		assertFalse(autocompleteProvider.getCompletions("NDerivative").findAny().isEmpty());
	}
}
