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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RELATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.javax.swing.RelationPane.RelationRow;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.kernel.RelationNumerical.Report.RelationCommand;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.plugin.EventType;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/** Tests relation selection and the real numerical reports at the platform dialog boundary. */
class RelationToolTest extends BaseToolTest {
	private Factory platform;
	private RelationPane pane;
	private boolean desktop;
	private int waitCursors;
	private int defaultCursors;
	private boolean waitCursorActive;
	private final List<String> subtitles = new ArrayList<>();
	private final List<RelationRow[]> dialogs = new ArrayList<>();

	@Override
	protected void setupClassicApp() {
		platform = Mockito.mock(Factory.class);
		pane = Mockito.mock(RelationPane.class);
		Mockito.when(platform.newRelationPane(Mockito.anyString())).thenAnswer(invocation -> {
			subtitles.add(invocation.getArgument(0));
			return pane;
		});
		Mockito.doAnswer(invocation -> {
					dialogs.add(invocation.getArgument(1));
					assertSame(getApp(), invocation.getArgument(2));
					assertEquals(
							getApp().getLocalization().getCommand("Relation"), invocation.getArgument(0));
					return null;
				})
				.when(pane)
				.showDialog(Mockito.anyString(), Mockito.any(), Mockito.any());
		AppCommon3D app =
				new AppCommon3D(
						new LocalizationCommonUTF(3), new AwtFactoryCommon(), new AppConfigDefault()) {
					@Override
					public @NonNull Factory getFactory() {
						return platform;
					}

					@Override
					public boolean isDesktop() {
						return desktop;
					}

					@Override
					public void setWaitCursor() {
						waitCursors++;
						waitCursorActive = true;
					}

					@Override
					public void setDefaultCursor() {
						defaultCursors++;
						waitCursorActive = false;
					}
				};
		try (MockedStatic<AppCommonFactory> factory = Mockito.mockStatic(AppCommonFactory.class)) {
			factory
					.when(() -> AppCommonFactory.create3D(Mockito.any(AppConfig.class)))
					.thenReturn(app);
			super.setupClassicApp();
		}
	}

	@Test
	void enteringModeAndClickingEmptyCanvasShouldNotOpenDialogOrCreateObjects() {
		Set<GeoElement> before = getConstructionSnapshot();

		setMode(MODE_RELATION);
		moveMouseRW(1, -1);
		clickRW(1, -1);

		assertEquals(MODE_RELATION, getApp().getMode());
		assertTrue(dialogs.isEmpty());
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void enteringRelationModeShouldClearPreviousSelection() {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=(3,-2)");
		getApp().getSelectionManager().addSelectedGeo(a);
		getApp().getSelectionManager().addSelectedGeo(b);

		setMode(MODE_RELATION);

		assertTrue(dialogs.isEmpty());
		assertTrue(getApp().getSelectionManager().getSelectedGeos().isEmpty());
		clickRW(1, -1);
		assertTrue(dialogs.isEmpty());
		clickRW(3, -2);
		assertDialog("A and B");
	}

	@Test
	void twoClicksShouldComparePointsAndClearSelectionWithoutChangingConstruction() {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=(3,-2)");
		Set<GeoElement> before = getConstructionSnapshot();
		setMode(MODE_RELATION);
		clickRW(1, -1);
		assertTrue(dialogs.isEmpty());
		assertEquals(List.of(a), getApp().getSelectionManager().getSelectedGeos());
		assertEquals(before, getConstructionSnapshot());

		moveMouseRW(3, -2);
		assertTrue(dialogs.isEmpty());
		clickRW(3, -2);

		assertDialog("A and B");
		assertSingleReport(plain("AandBareNotEqual", a, b), false);
		assertTrue(getApp().getSelectionManager().getSelectedGeos().isEmpty());
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void clickingSamePointTwiceShouldDeselectItRatherThanCompareItWithItself() {
		add("A=(1,-1)");
		add("B=(3,-2)");
		setMode(MODE_RELATION);
		clickRW(1, -1);
		resetMouseLocation();
		clickRW(1, -1);
		assertTrue(dialogs.isEmpty());

		clickRW(3, -2);
		assertTrue(dialogs.isEmpty());
		clickRW(1, -1);

		assertDialog("B and A");
	}

	@Test
	void leavingModeShouldDiscardIncompleteComparison() {
		add("A=(1,-1)");
		add("B=(3,-2)");
		setMode(MODE_RELATION);
		clickRW(1, -1);

		setMode(MODE_MOVE);
		setMode(MODE_RELATION);
		clickRW(3, -2);

		assertTrue(dialogs.isEmpty());
		clickRW(1, -1);
		assertDialog("B and A");
	}

	@Test
	void completedComparisonsShouldNotReusePreviousSelection() {
		add("A=(1,-1)");
		add("B=(3,-2)");
		add("C=(5,-1)");
		add("D=(7,-2)");
		setMode(MODE_RELATION);
		clickRW(1, -1);
		clickRW(3, -2);

		clickRW(5, -1);
		assertEquals(1, dialogs.size());
		clickRW(7, -2);

		assertEquals(List.of("A and B", "C and D"), subtitles);
		assertEquals(2, dialogs.size());
	}

	@Test
	void clickingLineThenIncidentPointShouldReportIncidence() {
		GeoElement line = add("g:y=-x");
		GeoElement point = add("A=(1,-1)");
		setMode(MODE_RELATION);
		clickRW(3, -3);
		clickRW(1, -1);

		assertDialog("g and A");
		assertSingleReport(plain("AliesOnB", point, line), true);
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = '|',
			value = {
				"A=(1,-1)|B=(3,-2)|AandBareNotEqual|false",
				"A=(1,-1)|B=(1,-1)|AandBareEqual|true",
				"a=2|b=2|AandBareEqual|true",
				"a=2|b=3|AandBareNotEqual|false",
				"g:y=-x|h:y=-x-2|AandBareParallel|true",
				"g:y=-x|h:y=x-2|AandBarePerpendicular|true",
				"g:y=-x|h:y=-x|AandBareEqual|true",
				"g:y=-x|h:y=-2x|AIntersectsWithB|false",
				"v=(1,-1)|w=(2,-2)|AandBareLinearlyDependent|false",
				"p={1,2}|q={1,2}|AandBareEqual|false",
				"p={1,2}|q={1,3}|AandBareNotEqual|false",
				"f(x)=x^2|g(x)=x*x|AandBareEqual|false",
				"A=(1,-1)|g:y=-x|AliesOnB|true",
				"A=(1,-1)|g:y=-x-1|AdoesNotLieOnB|false",
				"g:x=2|c:x^2+y^2=4|AisaTangentToB|true",
				"g:x=3|c:x^2+y^2=4|ADoesNotIntersectWithB|false",
				"f(x)=x^2|g(x)=x+1|AandBareNotEqual|false",
				"v=(1,-1)|w=(1,-1)|AandBareEqual|true",
				"v=(1,-1)|w=(1,-2)|AandBareLinearlyIndependent|false",
				"c:x^2+y^2=4|d:x^2+y^2=4|AandBareEqual|false",
				"c:x^2+y^2=4|d:(x-1)^2+y^2=4|AIntersectsWithB|false",
				"c:x^2+y^2=4|d:(x-10)^2+y^2=4|ADoesNotIntersectWithB|false",
				"A=(1,-1)|p=Polygon((1,-1),(3,-1),(3,-3),(1,-3))|AliesOnThePerimeterOfB|false",
				"A=(2,-2)|p=Polygon((1,-1),(3,-1),(3,-3),(1,-3))|AdoesNotLieOnThePerimeterOfB|false",
				"p=Polygon((1,-1),(3,-1),(1,-3))|q=Polygon((4,-1),(6,-1),(4,-3))|AhasTheSameAreaAsB|true",
				"p=Polygon((1,-1),(3,-1),(1,-3))|q=Polygon((4,-1),(6,-1),(4,-4))|AdoesNothaveTheSameAreaAsB|false"
			})
	void chosenObjectsShouldReachNumericalComparison(
			@NonNull String first, @NonNull String second, @NonNull String reportKey, boolean symbolic) {
		GeoElement a = add(first);
		GeoElement b = add(second);
		Set<GeoElement> before = getConstructionSnapshot();

		compareChosen(a, b);

		assertEquals(1, dialogs.size());
		assertSingleReport(plain(reportKey, a, b), symbolic);
		assertEquals(before, getConstructionSnapshot());
	}

	@ParameterizedTest
	@ValueSource(ints = {2, 3, 4})
	void rectangleShouldCompareSelectedPoints(int count) {
		GeoElement[] points = new GeoElement[count];
		for (int i = 0; i < count; i++) {
			points[i] = add((char) ('A' + i) + "=(" + (i + 1) + "," + (-i - 1) + ")");
		}
		Set<GeoElement> before = getConstructionSnapshot();
		setMode(MODE_RELATION);

		dragRW(0.5, -0.5, count + 0.5, -count - 0.5);

		assertEquals(1, dialogs.size());
		assertSingleReport(
				count == 2
						? plain("AandBareNotEqual", points)
						: groupPlain("TheFollowingAreCollinearA", points),
				count == 3);
		assertEquals(before, getConstructionSnapshot());
		assertTrue(getApp().getSelectionManager().getSelectedGeos().isEmpty());
	}

	@Test
	void rectangleWithSinglePointShouldNotOpenComparison() {
		add("A=(2,-2)");
		setMode(MODE_RELATION);

		dragRW(1, -1, 3, -3);

		assertTrue(dialogs.isEmpty());
		assertEquals(Set.of("A"), getObjectNames());
	}

	@Test
	void numericalComparisonShouldDispatchFirstRowWithoutCreatingUndoAction() {
		add("A=(1,-1)");
		add("B=(3,-2)");
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		List<String> reports = new ArrayList<>();
		getApp().getEventDispatcher().addEventListener(event -> {
			if (event.type == EventType.RELATION_TOOL) {
				reports.add(event.argument);
			}
		});
		setMode(MODE_RELATION);

		clickRW(1, -1);
		clickRW(3, -2);

		assertEquals(List.of(dialogs.get(0)[0].getInfo()), reports);
		assertFalse(getKernel().undoPossible());
	}

	@ParameterizedTest
	@MethodSource("pointGroups")
	void rectangleShouldReportPointRelations(
			@NonNull String[] definitions, @NonNull String key, boolean symbolic) {
		GeoElement[] points = Arrays.stream(definitions).map(this::add).toArray(GeoElement[]::new);
		Set<GeoElement> before = getConstructionSnapshot();
		setMode(MODE_RELATION);

		dragRW(0.5, -0.5, 5, -5);

		assertEquals(1, dialogs.size());
		assertSingleReport(groupPlain(key, points), symbolic);
		assertEquals(before, getConstructionSnapshot());
	}

	private static @NonNull Stream<Arguments> pointGroups() {
		return Stream.of(
				Arguments.of(
						new String[] {"A=(2,-2)", "B=(2,-2)", "C=(2,-2)"}, "TheFollowingAreEqualA", false),
				Arguments.of(
						new String[] {"A=(1,-1)", "B=(2,-2)", "C=(3,-3)"}, "TheFollowingAreCollinearA", true),
				Arguments.of(
						new String[] {"A=(1,-1)", "B=(2,-2)", "C=(2,-3)"}, "TheFollowingAreNotEqualA", false),
				Arguments.of(
						new String[] {"A=(2,-2)", "B=(2,-2)", "C=(2,-2)", "D=(2,-2)"},
						"TheFollowingAreEqualA",
						false),
				Arguments.of(
						new String[] {"A=(1,-1)", "B=(2,-2)", "C=(3,-3)", "D=(4,-4)"},
						"TheFollowingAreCollinearA",
						false),
				Arguments.of(
						new String[] {"A=(1,-1)", "B=(3,-1)", "C=(1,-3)", "D=(3,-3)"},
						"TheFollowingAreConcyclicA",
						true),
				Arguments.of(
						new String[] {"A=(1,-1)", "B=(3,-1)", "C=(1,-3)", "D=(4,-4)"},
						"TheFollowingAreNotEqualA",
						false));
	}

	@Test
	@Issue("APPS-7920")
	void rectangleShouldIgnoreConsecutiveNonPointObjects() {
		add("s=Segment((1,-1),(2,-1))");
		add("t=Segment((1,-3),(2,-3))");
		GeoElement a = add("A=(3,-1)");
		GeoElement b = add("B=(3,-2)");
		GeoElement c = add("C=(3,-3)");
		Set<GeoElement> before = getConstructionSnapshot();
		setMode(MODE_RELATION);

		dragRW(0.5, -0.5, 3.5, -3.5);

		assertDialog("A, B and C");
		assertSingleReport(groupPlain("TheFollowingAreCollinearA", a, b, c), true);
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void oneSelectedObjectShouldNotTriggerReport() {
		GeoElement a = add("a=2");

		compareChosen(a);

		assertTrue(dialogs.isEmpty());
		assertEquals(List.of(a), getApp().getSelectionManager().getSelectedGeos());
	}

	@Test
	void unsupportedPairShouldReportComparisonFailureWithoutMoreButton() {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("b=2");

		compareChosen(a, b);

		assertEquals(1, dialogs.size());
		assertSingleReport(plain("AandBcannotBeCompared", a, b), false);
	}

	@ParameterizedTest
	@CsvSource({"2,0", "2,1", "3,0", "3,1", "3,2", "4,0", "4,1", "4,2", "4,3"})
	void undefinedInputShouldReportItsLabelWithoutSymbolicCallback(int count, int index) {
		GeoElement[] points = new GeoElement[count];
		for (int i = 0; i < count; i++) {
			points[i] = add((char) ('A' + i) + "=(" + (i + 1) + "," + (-i - 1) + ")");
		}
		points[index].setUndefined();
		Set<GeoElement> before = getConstructionSnapshot();

		compareChosen(points);

		assertEquals(1, dialogs.size());
		assertSingleReport(plain("AisNotDefined", points[index]), false);
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void numericalComparisonShouldOfferAndCompleteSymbolicEqualityCheck() {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=A");
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		compareChosen(a, b);
		RelationRow initial = dialogs.get(0)[0];
		assertNotNull(initial.getCallback());
		Set<GeoElement> before = getConstructionSnapshot();

		RelationRow expanded = initial.getCallback().getExpandedRow(0);

		assertTrue(expanded.getInfo().contains(initial.getInfo()));
		String alwaysTrue = getApp().getLocalization().getMenuDefault("AlwaysTrue", "(always true)");
		String numericalOnly =
				getApp().getLocalization().getMenuDefault("CheckedNumerically", "(checked numerically)");
		assertTrue(
				expanded.getInfo().contains(alwaysTrue) || expanded.getInfo().contains(numericalOnly),
				expanded.getInfo());
		assertNull(expanded.getCallback());
		assertEquals(before, getConstructionSnapshot());
		assertFalse(getKernel().undoPossible());
		assertFalse(waitCursorActive);
	}

	@Test
	void positiveReportsShouldSortBeforeNegativeReportsAndDispatchFirstRow() {
		GeoElement s = add("s=Segment((1,-1),(3,-1))");
		GeoElement t = add("t=Segment((5,-1),(5,-4))");
		List<String> events = new ArrayList<>();
		getApp().getEventDispatcher().addEventListener(event -> {
			if (event.type == EventType.RELATION_TOOL) {
				events.add(event.argument);
			}
		});
		Set<GeoElement> before = getConstructionSnapshot();

		compareChosen(s, t);
		assertEquals(before, getConstructionSnapshot());

		RelationRow[] rows = dialogs.get(0);
		assertEquals(2, rows.length);
		assertEquals(plain("AandBarePerpendicular", s, t), rows[0].getInfo());
		assertNotNull(rows[0].getCallback());
		assertNull(rows[1].getCallback());
		assertEquals(List.of(rows[0].getInfo()), events);
	}

	@ParameterizedTest
	@CsvSource({
		"'',CheckedNumerically",
		"1,AlwaysTrue",
		"0,FalseInGeneral",
		"2,TrueOnPartsFalseOnParts"
	})
	void moreCallbackShouldFormatProofOutcomeAndRemoveCallback(
			@NonNull String result, @NonNull String messageKey) {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=A");
		compareChosen(a, b);
		RelationRow initial = dialogs.get(0)[0];
		Relation callback = Mockito.spy(initial.getCallback());
		Mockito.doReturn(new String[] {result})
				.when(callback)
				.getNDGConditions(RelationCommand.AreEqual);
		Set<GeoElement> before = getConstructionSnapshot();

		RelationRow expanded = callback.getExpandedRow(0);

		assertTrue(expanded.getInfo().startsWith("<html>" + initial.getInfo()));
		assertTrue(expanded.getInfo().contains(getApp().getLocalization().getMenu(messageKey)));
		assertTrue(expanded.getInfo().endsWith("</html>"));
		assertNull(expanded.getCallback());
		assertEquals(before, getConstructionSnapshot());
	}

	@ParameterizedTest
	@MethodSource("proofConditions")
	void moreCallbackShouldFormatConditions(@NonNull String[] proof, @NonNull String expectedText) {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=A");
		compareChosen(a, b);
		Relation callback = Mockito.spy(dialogs.get(0)[0].getCallback());
		Mockito.doReturn(proof).when(callback).getNDGConditions(RelationCommand.AreEqual);
		Set<GeoElement> before = getConstructionSnapshot();

		RelationRow expanded = callback.getExpandedRow(0);

		assertTrue(expanded.getInfo().contains(expectedText), expanded.getInfo());
		assertTrue(expanded.getInfo().endsWith("</html>"));
		assertNull(expanded.getCallback());
		if ("1".equals(proof[0]) && !"\u2026".equals(proof[1])) {
			assertTrue(expanded.getInfo().contains("<ul>"));
			for (int i = 1; i < proof.length; i++) {
				assertTrue(expanded.getInfo().contains(proof[i]), expanded.getInfo());
			}
		}
		assertFalse(waitCursorActive);
		assertEquals(before, getConstructionSnapshot());
	}

	private static @NonNull Stream<Arguments> proofConditions() {
		return Stream.of(
				Arguments.of(new String[] {"1", "A != B"}, "A != B"),
				Arguments.of(new String[] {"1", "A != B", "B != C"}, "A != B and"),
				Arguments.of(new String[] {"1", "\u2026"}, "not degenerate"),
				Arguments.of(new String[] {"2", "A != B"}, "true on parts, false on parts"));
	}

	@Test
	@Issue("APPS-7920")
	void failingSymbolicCheckShouldRestoreCursorAndAllowRetry() {
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=A");
		compareChosen(a, b);
		Relation callback = Mockito.spy(dialogs.get(0)[0].getCallback());
		IllegalStateException failure = new IllegalStateException("Prover unavailable");
		Mockito.doAnswer(invocation -> {
					assertTrue(waitCursorActive);
					throw failure;
				})
				.when(callback)
				.getNDGConditions(RelationCommand.AreEqual);
		int waits = waitCursors;
		int defaults = defaultCursors;
		Set<GeoElement> before = getConstructionSnapshot();

		assertSame(
				failure, assertThrows(IllegalStateException.class, () -> callback.getExpandedRow(0)));
		assertEquals(before, getConstructionSnapshot());

		assertEquals(waits + 1, waitCursors);
		assertEquals(defaults + 1, defaultCursors);
		assertFalse(waitCursorActive);
		Mockito.doReturn(new String[] {"1"}).when(callback).getNDGConditions(RelationCommand.AreEqual);
		assertNull(callback.getExpandedRow(0).getCallback());
		assertEquals(defaults + 2, defaultCursors);
		assertFalse(waitCursorActive);
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void desktopRowsShouldIncludeNumericalMarkerAndHtmlWrapper() {
		desktop = true;
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=A");

		compareChosen(a, b);

		RelationRow row = dialogs.get(0)[0];
		assertTrue(row.getInfo().startsWith("<html>"));
		assertTrue(row.getInfo().endsWith("</html>"));
		assertTrue(row.getInfo().contains("checked numerically"));
		assertNotNull(row.getCallback());
	}

	@Test
	void equalLengthParallelSegmentsShouldKeepCallbacksAlignedWithSortedRows() {
		GeoElement s = add("s=Segment((1,-1),(3,-1))");
		GeoElement t = add("t=Segment((1,-3),(3,-3))");

		compareChosen(s, t);

		RelationRow[] rows = dialogs.get(0);
		List<String> expected = Stream.of(
						plain("AhasTheSameLengthAsB", s, t), plain("AandBareParallel", s, t))
				.sorted()
				.toList();
		assertEquals(expected, Arrays.stream(rows).map(RelationRow::getInfo).toList());
		Relation callback = Mockito.spy(rows[0].getCallback());
		Mockito.doReturn(new String[] {"1"})
				.when(callback)
				.getNDGConditions(Mockito.any(RelationCommand.class));
		for (int i = 0; i < rows.length; i++) {
			assertNotNull(rows[i].getCallback());
			Mockito.clearInvocations(callback);
			RelationRow expanded = callback.getExpandedRow(i);
			assertTrue(expanded.getInfo().contains(rows[i].getInfo()));
			assertNull(expanded.getCallback());
			RelationCommand command = rows[i].getInfo().equals(plain("AhasTheSameLengthAsB", s, t))
					? RelationCommand.AreCongruent
					: RelationCommand.AreParallel;
			Mockito.verify(callback).getNDGConditions(command);
			Mockito.verify(callback).getNDGConditions(Mockito.any(RelationCommand.class));
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {3, 4})
	void unsupportedGroupShouldReportFailureWithoutSymbolicCallback(int count) {
		GeoElement[] numbers = new GeoElement[count];
		for (int i = 0; i < count; i++) {
			numbers[i] = add((char) ('a' + i) + "=" + (i + 1));
		}

		compareChosen(numbers);

		assertSingleReport(getApp().getLocalization().getMenu("ComparisonNotPossible"), false);
	}

	@Test
	void indexedLabelsShouldBeFormattedInSubtitleAndReport() {
		GeoElement a = add("A_1=(1,-1)");
		GeoElement b = add("B_2=(3,-2)");

		compareChosen(a, b);

		assertDialog(
				"A<sub><font size=\"-1\">1</font></sub> and " + "B<sub><font size=\"-1\">2</font></sub>");
		assertSingleReport(plain("AandBareNotEqual", a, b), false);
	}

	@Test
	void desktopExpansionShouldReplaceNumericalMarkerWithoutNestingHtml() {
		desktop = true;
		GeoElement a = add("A=(1,-1)");
		GeoElement b = add("B=A");
		compareChosen(a, b);
		RelationRow initial = dialogs.get(0)[0];
		assertNotNull(initial.getCallback());
		Relation callback = Mockito.spy(initial.getCallback());
		Mockito.doReturn(new String[] {"1"}).when(callback).getNDGConditions(RelationCommand.AreEqual);
		Set<GeoElement> before = getConstructionSnapshot();

		RelationRow expanded = callback.getExpandedRow(0);
		assertEquals(before, getConstructionSnapshot());

		assertTrue(expanded.getInfo().startsWith("<html>"));
		assertEquals(-1, expanded.getInfo().indexOf("<html>", 6));
		assertTrue(expanded.getInfo().endsWith("</html>"));
		assertTrue(expanded.getInfo().contains("always true"));
		assertFalse(expanded.getInfo().contains("checked numerically"));
		assertNull(expanded.getCallback());
		assertFalse(waitCursorActive);
	}

	private @NonNull String groupPlain(@NonNull String key, @NonNull GeoElement... geos) {
		List<String> labels = Arrays.stream(geos)
				.map(
						geo -> "<strong>" + GeoElement.indicesToHTML(geo.getLabelSimple(), false) + "</strong>")
				.toList();
		String list = String.join(", ", labels.subList(0, labels.size() - 1)) + " and "
				+ labels.get(labels.size() - 1);
		return getApp().getLocalization().getPlain(key, list);
	}

	private void compareChosen(@NonNull GeoElement... elements) {
		setMode(MODE_RELATION);
		// Supply chosen hits for overlapping or non-canvas objects; picking is tested separately.
		if (elements.length > 2) {
			Hits hits = new Hits();
			hits.addAll(Arrays.asList(elements));
			ec.addSelectedGeo(hits, 4, true, false);
			ec.processMode(hits, false, false);
			return;
		}
		Arrays.stream(elements).forEach(element -> {
			Hits hits = new Hits();
			hits.add(element);
			ec.processMode(hits, false, false);
		});
	}

	private void assertDialog(@NonNull String subtitle) {
		assertEquals(List.of(subtitle), subtitles);
		assertEquals(1, dialogs.size());
	}

	private void assertSingleReport(@NonNull String expected, boolean symbolic) {
		assertFalse(dialogs.isEmpty(), "Expected a relation dialog");
		RelationRow[] rows = dialogs.get(dialogs.size() - 1);
		assertEquals(1, rows.length);
		assertEquals(expected, rows[0].getInfo());
		if (symbolic) {
			assertNotNull(rows[0].getCallback());
		} else {
			assertNull(rows[0].getCallback());
		}
	}

	private @NonNull String plain(@NonNull String key, @NonNull GeoElement... geos) {
		String[] labels = Arrays.stream(geos)
				.map(
						geo -> "<strong>" + GeoElement.indicesToHTML(geo.getLabelSimple(), false) + "</strong>")
				.toArray(String[]::new);
		return getApp().getLocalization().getPlain(key, labels);
	}
}
