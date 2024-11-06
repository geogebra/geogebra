package org.geogebra.common.main;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal (preview) features that are visible only in test builds.
 *
 * @apiNote When an internal feature is released, the corresponding enum case has to be removed,
 * together with any if/guard statements.
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
public enum PreviewFeature {

	ALL_LANGUAGES,
	RESOURCES_API_BETA,
	IMPLICIT_SURFACES,
	/** MOB-1537 */
	MOB_PREVIEW_WHEN_EDITING,
	/** SolveQuartic in CAS GGB-1635 */
	SOLVE_QUARTIC,
	/** TRAC-4845 */
	LOG_AXES,
	/** GGB-2255 */
	GEOMETRIC_DISCOVERY,

	/** APPS-4961 */
	CVTE_EXAM,
	/** APPS-4867 */
	MMS_EXAM,
	/** APPS-5641 */
	IB_EXAM,
	/** APPS-5740 */
	REALSCHULE_EXAM;

	/**
	 * Global flag to activate feature previews.
	 *
	 * @apiNote Set the {@code true} at run time (early in the app startup code) to enable
	 * feature previews in test builds.
	 */
	public static boolean enableFeaturePreviews = false;
	
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
	 * Checks whether a preview feature is available.
	 * @param previewFeature A preview feature.
	 * @return {@code true} iff the preview feature's {@code isEnabled} flag is {@code true} and
	 * {@link #enableFeaturePreviews} is {@code true} as well; {@code false} otherwise.
	 */
	public static boolean isAvailable(PreviewFeature previewFeature) {
		return enableFeaturePreviews && previewFeature.isEnabled;
	}
}
