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

package org.geogebra.common.util.debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AnalyticsTest {

	private long time = 0;
	
	@AfterEach
	public void tearDown() {
		Analytics.setInstance(null);
		Analytics.resetToolCreationTracking();
	}

	@Test
	public void shouldTrackDurationAndUseCountAcrossStickyCreations() {
		TestAnalytics analytics = new TestAnalytics();
		Analytics.setTimeSupplier(() -> time);
		Analytics.setInstance(analytics);

		time = 1000;
		Analytics.logToolSelected("Segment");
		time = 1450;
		Analytics.logToolCreated();
		time = 1730;
		Analytics.logToolCreated();

		assertEquals(3, analytics.events.size());
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_SELECTED, Map.of(
						Analytics.Param.TOOL_NAME, "Segment")),
				analytics.events.get(0));
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_CREATED, Map.of(
						Analytics.Param.TOOL_NAME, "Segment",
						Analytics.Param.DURATION_MS, 450L,
						Analytics.Param.USE_COUNT, 1)),
				analytics.events.get(1));
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_CREATED, Map.of(
						Analytics.Param.TOOL_NAME, "Segment",
						Analytics.Param.DURATION_MS, 280L,
						Analytics.Param.USE_COUNT, 2)),
				analytics.events.get(2));

	}

	@Test
	public void shouldResetUseCountWhenToolChanges() {
		TestAnalytics analytics = new TestAnalytics();
		Analytics.setTimeSupplier(() -> time);
		Analytics.setInstance(analytics);

		time = 100;
		Analytics.logToolSelected("Segment");
		time = 250;
		Analytics.logToolCreated();
		time = 500;
		Analytics.logToolSelected("Circle");
		time = 900;
		Analytics.logToolCreated();

		assertEquals(4, analytics.events.size());
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_SELECTED, Map.of(
						Analytics.Param.TOOL_NAME, "Segment")),
				analytics.events.get(0));
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_CREATED, Map.of(
						Analytics.Param.TOOL_NAME, "Segment",
						Analytics.Param.DURATION_MS, 150L,
						Analytics.Param.USE_COUNT, 1)),
				analytics.events.get(1));
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_SELECTED, Map.of(
						Analytics.Param.TOOL_NAME, "Circle")),
				analytics.events.get(2));
		assertEquals(new LoggedEvent(
						Analytics.Event.TOOL_CREATED, Map.of(
						Analytics.Param.TOOL_NAME, "Circle",
						Analytics.Param.DURATION_MS, 400L,
						Analytics.Param.USE_COUNT, 1)),
				analytics.events.get(3));
	}

	@Test
	public void shouldIgnoreCreationWithoutSelectedTool() {
		TestAnalytics analytics = new TestAnalytics();
		Analytics.setInstance(analytics);

		Analytics.logToolCreated();
		assertTrue(analytics.events.isEmpty());
	}

	private static final class TestAnalytics extends Analytics {
		private final List<LoggedEvent> events = new ArrayList<>();

		@Override
		protected void recordEvent(String name, Map<String, Object> params) {
			events.add(new LoggedEvent(name, params));
		}

		@Override
		protected void setDefaultEventParametersInternal(@Nonnull Map<String, Object> params) {
		}
	}

	private record LoggedEvent(String name, Map<String, Object> params) {
	}
}
