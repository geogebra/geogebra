package org.geogebra.web.full.main;

import static org.geogebra.common.io.layout.DockPanelData.TabIds.*;
import static org.geogebra.common.main.App.VIEW_ALGEBRA;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.InitialViewState;

public class UnbundledInitialViewState implements InitialViewState {

	private final AppWFull app;
	private final boolean toolbarVisible;
	private final boolean allowStyleBar;
	private DockPanelData.TabIds initialTabId;

	/**
	 * Constructs an {@link UnbundledInitialViewState} for web environments
	 * where only one view can be visible at a time.
	 *
	 * @param app             the full web application instance
	 * @param toolbarVisible  {@code true} if the toolbar is visible at startup;
	 *                        when {@code true}, no view‐restriction snapshot is taken
	 * @param allowStyleBar   {@code true} if the Properties (style) view may be
	 *                        toggled when the toolbar is hidden
	 */
	public UnbundledInitialViewState(AppWFull app, boolean toolbarVisible, boolean allowStyleBar) {
		this.app = app;
		this.toolbarVisible = toolbarVisible;
		this.allowStyleBar = allowStyleBar;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p><strong>Unbundled:</strong> record only the active tab ID,
	 * since only one view can be visible at a time.</p>
	 */
	@Override
	public void store() {
		if (toolbarVisible) {
			return;
		}

		DockPanelData dpd = app.getGuiManager().getLayout().getDockManager()
						.getPanel(VIEW_ALGEBRA).createInfo();
		initialTabId = dpd.getTabId();
	}

	@Override
	public boolean hasAlgebra() {
		return isToggleable(ALGEBRA);
	}

	private boolean isToggleable(DockPanelData.TabIds id) {
		if (toolbarVisible) {
			return true;
		}
		return id.equals(initialTabId);
	}

	@Override
	public boolean hasCas() {
		return app.isUnbundledCas() && isToggleable(ALGEBRA);
	}

	@Override
	public boolean hasTableOfValues() {
		return isToggleable(TABLE);
	}

	@Override
	public boolean hasSpreadsheet() {
		return isToggleable(SPREADSHEET);
	}

	@Override
	public boolean hasConstructionProtocol() {
		return false; // not supported
	}

	@Override
	public boolean hasProbability() {
		return false; // not supported
	}

	@Override
	public boolean hasProperties() {
		return allowStyleBar;
	}

	@Override
	public boolean hasGraphicsView1() {
		return true; // unbundled has always a graphic view
	}

	@Override
	public boolean hasGraphicsView2() {
		return false; // not supported
	}
}
