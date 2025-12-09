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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal (preview) features that are visible only in test builds.
 *
 * @apiNote When an internal feature is released, the corresponding enum case has to be removed,
 * together with any if/guard statements.
 */
public enum PreviewFeature {

	ALL_LANGUAGES,
	RESOURCES_API_BETA,
	IMPLICIT_SURFACES,
	/** APPS-5763 */
	IMPLICIT_PLOTTER,
	/** MOB-1537 */
	MOB_PREVIEW_WHEN_EDITING,
	/** SolveQuartic in CAS GGB-1635 */
	SOLVE_QUARTIC,
	/** TRAC-4845 */
	LOG_AXES,
	/** GGB-2255 */
	GEOMETRIC_DISCOVERY,
	/** APPS-5641 */
	IB_EXAM,
	/**
	 * APPS-6759
	 */
	SETTINGS_VIEW,
	/**
	 * MOW-1762
	 */
	TEST_FONT;

	/**
	 * Global flag to activate preview features.
	 *
	 * @apiNote Set the {@code true} at run time (early in the app startup code) to enable
	 * preview features in test builds.
	 */
	@SuppressFBWarnings("MS_PKGPROTECT")
	public static boolean enablePreviewFeatures = false;

	/**
	 * Whether a preview feature is enabled (the default), or not. The latter case can be used
	 * during development, i.e., when an internal feature is being worked on, but not yet considered
	 * ready for internal preview.
	 */
	private final boolean isEnabled;

	PreviewFeature() {
		this(true);
	}

	PreviewFeature(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Enables preview features. May be called at startup.
	 */
	public static void setPreviewFeaturesEnabled(boolean enabled) {
		enablePreviewFeatures = enabled;
	}

	/**
	 * Checks whether a preview feature is available.
	 * @param previewFeature A preview feature.
	 * @return {@code true} iff the preview feature's {@code isEnabled} flag is {@code true} and
	 * {@link #enablePreviewFeatures} is {@code true} as well; {@code false} otherwise.
	 */
	public static boolean isAvailable(PreviewFeature previewFeature) {
		return enablePreviewFeatures && previewFeature.isEnabled;
	}
}
