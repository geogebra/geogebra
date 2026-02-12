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

package org.geogebra.common.ownership;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.main.App;

/**
 * <p>
 * <b>Mobile use case:</b> {@code GlobalScope} has one {@code SuiteScope} holding one or more
 * {@code App}s.
 * </p>
 * <p>
 * <b>Web use case:</b> {@code GlobalScope} has one ore more {@code SuiteScope}s, each holding one
 * {@code App}.
 * </p>
 * @apiNote invariants in this design that must hold true at runtime:
 * <ul>
 * <li>The host app must set up one {@code SuiteScope} per suite instance.</li>
 * <li>Any {@code App} instances created must be registered with exactly one
 * {@code SuiteScope}.</li>
 * </ul>
 */
public final class GlobalScope {

	private static final Set<SuiteScope> suiteScopes = new HashSet<>();

	/**
	 * A suite (host) app must register a new suite scope early during startup (before any
	 * {@link App} instances are created). Any {@code App} instances created by the suite instance
	 * must be registered with the {@link SuiteScope}.
	 * @return A new {@link SuiteScope} for this suite instance.
	 */
	public static @Nonnull SuiteScope registerNewSuiteScope() {
		SuiteScope suiteScope = new SuiteScope();
        suiteScopes.add(suiteScope);
		return suiteScope;
    }

	/**
	 * Unregister a (previously registered) {@link SuiteScope}.
	 * @param suiteScope A {@code SuiteScope}. May be {@code null}.
	 */
	public static void unregisterSuiteScope(@CheckForNull SuiteScope suiteScope) {
		if (suiteScope == null) {
			return;
		}
		suiteScopes.remove(suiteScope);
	}

	/**
	 * Get the {@link SuiteScope} for an {@link App} instance (a suite sub-app in case of suite,
	 * or the single app for standalone apps).
	 * @param app A suite sub-app. This app must have been registered at creation with the
	 * suite instance's {@link SuiteScope}.
	 * @return The {@link SuiteScope} for this app instance, or {@code null} if the app instance
	 * has not been registered with a {@link SuiteScope}.
	 */
	public static @CheckForNull SuiteScope getSuiteScope(App app) {
		for (SuiteScope suiteScope : suiteScopes) {
			if (suiteScope.apps.contains(app)) {
				return suiteScope;
			}
		}
		return null;
	}

	// -- Helpers --

	/**
	 * A shorthand for {@code getSuiteScope(app).examController}, with a {@code null} check
	 * in case no suite scope has been set up for the app.
	 * @param app the current app
	 * @return the {@code ExamController} for the current app / suite scope, or {@code null}
	 * if no suite scope has been set up for this app.
	 */
	public static @CheckForNull ExamController getExamController(App app) {
		SuiteScope suiteScope = getSuiteScope(app);
		return suiteScope != null ? suiteScope.examController : null;
	}

	/**
	 * A shorthand for {@code getSuiteScope(app).examController.isExamActive()}, with a
	 * {@code null} check in case no suite scope has been set up for the app.
	 * @param app the current app
	 * @return whether an exam is currently active for the given app.
	 */
	public static boolean isExamActive(App app) {
		SuiteScope suiteScope = getSuiteScope(app);
		return suiteScope != null && suiteScope.examController.isExamActive();
	}

	/**
	 * Prevent instantiation.
	 */
	private GlobalScope() {
	}
}
