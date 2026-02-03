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

package org.geogebra.web.full.gui.properties.ui.tabs;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.objects.ObjectEventProperty;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentTextArea;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Factory for building a tabbed UI to edit object event scripts and optional
 * global JavaScript in GeoGebra Web.
 * <p>
 * For each supported {@link EventType} of the target object, a tab is created
 * that contains:
 * </p>
 * <ul>
 *   <li>an optional language selector (GGBScript / JavaScript) if JavaScript is
 *       enabled</li>
 *   <li>a text area for the script body</li>
 * </ul>
 * If JavaScript is enabled application-wide, an additional "Global JavaScript"
 * tab is appended that allows editing the kernel's library JavaScript.
 */
public class ScriptTabFactory {
	private static final String KEY_SCRIPT_LANGUAGE = "ScriptLanguage";
	private static final String KEY_SCRIPT_CONTENT = "ScriptContent";
	private final AppW app;
	private final LocalizationW loc;
	private final ScriptEditor scriptEditor;
	private final boolean jsEnabled;
	private final List<String> localizedScriptTypeNames;

	/**
	 * Binds a script-editing UI (text area and optional controls) to an
	 * {@link ObjectEventProperty}. Concrete subclasses implement the behavior
	 * for event-specific tabs and the global JavaScript tab.
	 */
	private abstract static class TabBinding {
		final ComponentTextArea area;
		final ScriptTab scriptTab;

		/**
		 * Creates a binding with a text area wired to apply its content on blur
		 * and key-up.
		 * @param app {@link AppW}
		 * @param scriptTab {@link ScriptTab}
		 */
		public TabBinding(AppW app, ScriptTab scriptTab) {
			this.scriptTab = scriptTab;
			this.area = new ComponentTextArea(app.getLocalization(), KEY_SCRIPT_CONTENT);
			addTextAreaHandlers();
		}

		private void addTextAreaHandlers() {
			area.addBlurHandler(event -> applyText());
			area.addKeyUpHandler(event -> applyText());
		}

		/**
		 * Pushes the current text from the UI into the underlying property.
		 * Implementations decide the exact target.
		 */
		void applyText() {
			scriptTab.setScriptText(area.getText());
		}

		/**
		 * Builds the panel that will be placed inside a tab.
		 *
		 * @return the panel containing the binding's UI controls
		 */
		abstract FlowPanel createPanel();

		/**
		 * Activates the binding prior to showing the tab. Implementations
		 * typically load the current value from the property into the UI and
		 * adjust auxiliary controls (e.g., language selector).
		 */
		abstract void activate();
	}

	/**
	 * Binding for event-specific tabs: provides a language selector (when JS is
	 * enabled) and a text area for the script body of a particular event.
	 */
	private static final class EventTabBinding extends TabBinding {
		private final ComponentDropDown dropDown;

		/**
		 * @param app application
		 * @param scriptTab {@link ScriptTab} representing scripts for multiple events
		 * @param scriptTypeNames localized names for script types (GGBScript / JavaScript)
		 */
		public EventTabBinding(AppW app, ScriptTab scriptTab,
				List<String> scriptTypeNames) {
			super(app, scriptTab);
			int defaultIndex = scriptTypeIndex();
			if (scriptTab.isJsEnabled()) {
				dropDown = new ComponentDropDown(app, KEY_SCRIPT_LANGUAGE,
						scriptTypeNames, defaultIndex);
				dropDown.addChangeHandler(() -> scriptTab.setScriptType(
						ScriptType.values()[dropDown.getSelectedIndex()]));
			} else {
				dropDown = null;
			}
		}

		@Override
		FlowPanel createPanel() {
			FlowPanel panel = new FlowPanel();
			if (dropDown != null) {
				panel.add(dropDown);
			}
			panel.add(area);
			return panel;
		}

		@Override
		void activate() {
			if (dropDown != null) {
				dropDown.setSelectedIndex(scriptTypeIndex());
			}
			area.setContent(scriptTab.getScriptText());
		}

		private int scriptTypeIndex() {
			return ScriptType.GGBSCRIPT.equals(scriptTab.getScriptType()) ? 0 : 1;
		}
	}

	/**
	 * Binding for the "Global JavaScript" tab. Displays and edits the kernel's
	 * library JavaScript.
	 */
	private static final class GlobalTabBinding extends TabBinding {
		private final Kernel kernel;

		public GlobalTabBinding(AppW app, ScriptTab scriptTab) {
			super(app, scriptTab);
			kernel = app.getKernel();
		}

		@Override
		FlowPanel createPanel() {
			FlowPanel panel = new FlowPanel();
			panel.add(area);
			return panel;
		}

		@Override
		void activate() {
			area.setContent(kernel.getLibraryJavaScript());
		}
	}

	/**
	 * Creates a new factory.
	 * @param app see {@link AppW}
	 * @param scriptEditor event property that exposes available events and script state
	 */
	public ScriptTabFactory(AppW app, ScriptEditor scriptEditor) {
		this.app = app;
		loc = app.getLocalization();
		this.scriptEditor = scriptEditor;
		jsEnabled = app.getEventDispatcher().availableTypes().contains(ScriptType.JAVASCRIPT);
		localizedScriptTypeNames = getLocalizedScriptTypeNames();
	}

	private List<String> getLocalizedScriptTypeNames() {
		return Arrays.stream(ScriptType.values()).map(v -> loc.getMenu(v.getName()))
				.collect(Collectors.toList());
	}

	private TabData createEventTabData(ScriptTab scriptTab, TabBinding binding) {
		return new TabData(loc.getMenu(scriptTab.getPropertyName()), binding.createPanel());
	}

	/**
	 * Creates the full tabbed component for editing all supported event scripts
	 * (one tab per event) and, if enabled, the global JavaScript.
	 * <p>
	 * The returned component initializes its first tab immediately and switches
	 * content when the selected tab changes.
	 * </p>
	 *
	 * @return the configured {@link ComponentTab}
	 */
	public ComponentTab create() {
		final List<TabData> tabData = new ArrayList<>();
		final List<TabBinding> bindings = new ArrayList<>();

		for (int index = 0; index < scriptEditor.count(); index++) {
			ScriptTab scriptTab = scriptEditor.getScriptTab(index);
			if (scriptTab == null) {
				return null;
			}

			scriptTab.setJsEnabled(jsEnabled);

			if (scriptTab.isEnabled()) {
				TabBinding binding;
				if (scriptTab.getPropertyName().startsWith("Global")) {
					binding = new GlobalTabBinding(app, scriptTab);
				} else {
					binding = new EventTabBinding(app, scriptTab,
							localizedScriptTypeNames);
				}
				tabData.add(createEventTabData(scriptTab, binding));
				bindings.add(binding);
			}
		}

		ComponentTab tabs = new ComponentTab(app, "Scripting", tabData.toArray(new TabData[0]));
		tabs.addTabChangedListener(evt -> bindings.get(tabs.getSelectedTabIdx())
				.activate());
		if (!tabData.isEmpty()) {
			tabs.switchToTab(0);
		}
		return tabs;
	}
}