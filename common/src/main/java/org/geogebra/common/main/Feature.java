package org.geogebra.common.main;

@Deprecated // replaced by PreviewFeature
public enum Feature {
	ALL_LANGUAGES,

	LOCALSTORAGE_FILES,

	TUBE_BETA,

	IMPLICIT_SURFACES,

	LOG_AXES,

	@Deprecated
	ANALYTICS,

	/** GGB-334, TRAC-3401 */
	@Deprecated // dead code -> remove
	ADJUST_WIDGETS,

	/** SolveQuartic in CAS GGB-1635 */
	SOLVE_QUARTIC,

	/** MOB-1319 */
	@Deprecated // dead code -> remove
	MOB_NOTIFICATION_BAR_TRIGGERS_EXAM_ALERT_IOS_11,

	/** MOB-1537 */
	MOB_PREVIEW_WHEN_EDITING,

	/** AND-887 and IGR-732 */
	@Deprecated // dead code -> remove
	MOB_PROPERTY_SORT_BY,

	/** GGB-2255 */
	GEOMETRIC_DISCOVERY,

	/** G3D-343 */
	@Deprecated // dead code -> remove
	G3D_SELECT_META,
}
