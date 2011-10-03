package geogebra.main.settings;

import java.util.LinkedList;

/**
 * Settings for the layout manager.
 * 
 * @author Florian Sonner
 */
public class LayoutSettings extends AbstractSettings {
	/**
	 * Show the title bar of views. If disabled, the style bar is always visible.
	 */
	private boolean showTitleBar;
	
	/**
	 * Ignore the layout of newly loaded documents. Useful for users who wish to keep
	 * their preferred screen layout.
	 */
	private boolean ignoreDocumentLayout;
	
	/**
	 * Allow the style bar.
	 */
	private boolean allowStyleBar;
	
	/**
	 * Default values.
	 */
	public LayoutSettings() {
		showTitleBar = true;
		allowStyleBar = true;
		ignoreDocumentLayout = false;
	}

	public LayoutSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	/**
	 * @return the showTitleBar
	 */
	public boolean showTitleBar() {
		return showTitleBar;
	}

	/**
	 * @param showTitleBar the showTitleBar to set
	 */
	public void setShowTitleBar(boolean showTitleBar) {
		if(this.showTitleBar != showTitleBar) {
			this.showTitleBar = showTitleBar;
			settingChanged();
		}
	}

	/**
	 * @return the ignoreDocumentLayout
	 */
	public boolean isIgnoringDocumentLayout() {
		return ignoreDocumentLayout;
	}

	/**
	 * @param ignoreDocumentLayout the ignoreDocumentLayout to set
	 */
	public void setIgnoreDocumentLayout(boolean ignoreDocumentLayout) {
		if(this.ignoreDocumentLayout != ignoreDocumentLayout) {
			this.ignoreDocumentLayout = ignoreDocumentLayout;
			settingChanged();
		}
	}

	/**
	 * @return the allowStyleBar
	 */
	public boolean isAllowingStyleBar() {
		return allowStyleBar;
	}

	/**
	 * @param allowStyleBar the allowStyleBar to set
	 */
	public void setAllowStyleBar(boolean allowStyleBar) {
		if(this.allowStyleBar != allowStyleBar) {
			this.allowStyleBar = allowStyleBar;
			settingChanged();
		}
	}	
}
