package org.geogebra.common.main;

import java.util.List;

/**
 * Classic implementation of InitialViewState.
 * <p>
 * In Classic, multiple views may be open simultaneously.
 * This class snapshots which views were open at startup (excluding the event dispatcher)
 * and then allows only those views to be toggled when the toolbar is hidden.
 * The Properties view is controlled solely by the allowStyleBar flag.
 * </p>
 */
public class ClassicInitialViewState implements InitialViewState {
	private final App app;
	private List<Integer> initialViewIds;
	private final boolean toolbarVisible;
	private final boolean allowStyleBar;

	/**
	 * @param app the GeoGebra application instance
	 * @param toolbarVisible true if the toolbar is visible at startup, in which case no restrictions apply
	 * @param allowStyleBar true if the Properties (style) view may be toggled when toolbar is hidden
	 */
	public ClassicInitialViewState(App app, boolean toolbarVisible, boolean allowStyleBar) {
		this.app = app;
		this.toolbarVisible = toolbarVisible;
		this.allowStyleBar = allowStyleBar;
		initialViewIds = List.of();
	}

	@Override
	public void store() {
		if (toolbarVisible) {
			// No restrictions when toolbar visible
			return;
		}
		initialViewIds = app.kernel.getToggleableViewIds();
	}

	/**
	 * Shared checks for GUI availability, used by most views
	 */
	private boolean hasFullGUI() {
		return app.isUsingFullGui() && app.getGuiManager() != null;
	}

	/**
	 * Returns true if toolbarVisible or the view was open at startup
	 */
	private boolean isToggleable(int viewId) {
		return toolbarVisible || initialViewIds.contains(viewId);
	}

	@Override
	public boolean hasAlgebra() {
		return isToggleable(App.VIEW_ALGEBRA)
				&& !app.isWhiteboardActive()
				&& hasFullGUI();
	}

	@Override
	public boolean hasCas() {
		return isToggleable(App.VIEW_CAS)
				&& hasFullGUI()
				&& app.supportsView(App.VIEW_CAS)
				&& !app.isUnbundledOrWhiteboard();
	}

	@Override
	public boolean hasTableOfValues() {
		// no separate Table of Values in classic
		return false;
	}

	@Override
	public boolean hasSpreadsheet() {
		return isToggleable(App.VIEW_SPREADSHEET);
	}

	@Override
	public boolean hasConstructionProtocol() {
		return isToggleable(App.VIEW_CONSTRUCTION_PROTOCOL)
				&& hasFullGUI()
				&& !app.isUnbundledOrWhiteboard();
	}

	@Override
	public boolean hasProbability() {
		return isToggleable(App.VIEW_PROBABILITY_CALCULATOR)
				&& hasFullGUI()
				&& (!app.isUnbundledOrWhiteboard() || app.getConfig().hasDistributionView());
	}

	@Override
	public boolean hasProperties() {
		return allowStyleBar && hasFullGUI();
	}

	@Override
	public boolean hasGraphicsView1() {
		return isToggleable(App.VIEW_EUCLIDIAN)
				&& app.getGuiManager() != null
				&& !app.isUnbundledOrWhiteboard();
	}

	@Override
	public boolean hasGraphicsView2() {
		return isToggleable(App.VIEW_EUCLIDIAN2)
				&& !app.isUnbundledOrWhiteboard()
				&& app.getGuiManager() != null;
	}
}
