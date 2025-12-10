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

package org.geogebra.common.main;

/**
 * Captures and provides access to the set of GeoGebra views
 * that were open when the application was first loaded.
 *
 * <p>
 * If the toolbar is hidden, only these initially open views
 * may be toggled via their keyboard shortcuts.
 * </p>
 *
 * <p>
 * Call {@link #store()} once immediately after loading the GeoGebra file
 * (before any view-toggling shortcuts are applied). If {@code store()} is not
 * called, implementations should assume all views are toggleable by default
 * in classic apps, and should default to the first tab in unbundled apps.
 * </p>
 */
public interface InitialViewState {

	/**
	 * Captures which views were open at startup so that only those
	 * may be toggled via keyboard shortcuts when the toolbar is hidden.
	 *
	 * <p>
	 * Call this exactly once immediately after the app or file is initialized,
	 * before any view toggle shortcuts run. If you don't call it:
	 * </p>
	 * <ul>
	 *   <li>Classic apps should treat all views as toggleable.</li>
	 *   <li>Unbundled apps should treat the first/default tab as active.</li>
	 * </ul>
	 */
	void store();

	/**
	 * @return {@code true} if the Algebra view was open at the time {@link #store()} was called
	 */
	boolean hasAlgebra();

	/**
	 * @return {@code true} if the CAS view was open at the time {@link #store()} was called
	 */
	boolean hasCas();

	/**
	 * @return {@code true} if the Table of Values view was open at the time {@link #store()}
	 * was called
	 */
	boolean hasTableOfValues();

	/**
	 * @return {@code true} if the Spreadsheet view was open at the time {@link #store()}
	 * was called
	 */
	boolean hasSpreadsheet();

	/**
	 * @return {@code true} if the Construction Protocol view was open at the time {@link #store()}
	 * was called
	 */
	boolean hasConstructionProtocol();

	/**
	 * @return {@code true} if the Probability view was open at the time {@link #store()} was called
	 */
	boolean hasProbability();

	/**
	 * @return {@code true} if the Settings view is allowed to open.
	 */
	boolean hasProperties();

	/**
	 * @return {@code true} if Graphics view 1 was open at the time {@link #store()} was called
	 */
	boolean hasGraphicsView1();

	/**
	 * @return {@code true} if Graphics view 2 was open at the time {@link #store()} was called
	 */
	boolean hasGraphicsView2();
}
