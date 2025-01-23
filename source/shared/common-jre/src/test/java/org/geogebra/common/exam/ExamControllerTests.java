package org.geogebra.common.exam;

import static org.geogebra.common.contextmenu.InputContextMenuItem.Help;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Text;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.kernel.commands.Commands;
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
                        .isFeatureRestricted(ExamFeatureRestriction.DATA_TABLE_REGRESSION)),
                // command restrictions
                () -> assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative)),
                // TODO commandArgumentFilters
                // expression restrictions
                () -> assertNull(evaluate("true || false")),
                // context menu restrictions
                () -> assertEquals(
                        List.of(Text, Help),
                        contextMenuFactory.makeInputContextMenu(true)),
                // geo element property filters
                () -> assertNull(geoElementPropertiesFactory.createShowObjectProperty(
                        app.getLocalization(),
                        List.of(new GeoPoint(app.getKernel().getConstruction())))),
                // construction element setup
                () -> assertEquals(GColor.RED, evaluate("(1, 1)")[0].getFillColor()));

        examController.finishExam();
        assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
        examController.exitExam();
        assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
        assertFalse(examController
                .isFeatureRestricted(ExamFeatureRestriction.DATA_TABLE_REGRESSION));
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
        examController.finishExam();
        examController.exitExam();
    }
}
