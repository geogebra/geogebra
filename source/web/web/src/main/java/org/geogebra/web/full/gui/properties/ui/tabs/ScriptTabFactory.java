package org.geogebra.web.full.gui.properties.ui.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.objects.ObjectAllEventsProperty;
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
 * <ul>
 *   <li>a text area for the script body</li>
 *   <li>an optional language selector (GGBScript / JavaScript) if JavaScript is
 *       enabled</li>
 * </ul>
 * If JavaScript is enabled application-wide, an additional "Global JavaScript"
 * tab is appended that allows editing the kernel's library JavaScript.
 * </p>
 */
public class ScriptTabFactory {
	private static final String KEY_SCRIPT_LANGUAGE = "ScriptLanguage";
	private static final String KEY_SCRIPT_CONTENT = "ScriptContent";
	private final AppW app;
	private final LocalizationW loc;
	private final ObjectAllEventsProperty property;
	private final boolean jsEnabled;
	private final List<String> localizedScriptTypeNames;

	/**
	 * Binds a script-editing UI (text area and optional controls) to an
	 * {@link ObjectEventProperty}. Concrete subclasses implement the behavior
	 * for event-specific tabs and the global JavaScript tab.
	 */
	private abstract static class TabBinding {
		final ComponentTextArea area;
		final ObjectEventProperty property;

		/**
		 * Creates a binding with a text area wired to apply its content on blur
		 * and key-up.
		 *
		 * @param app
		 *            application
		 * @param property
		 *            the object event property to bind to
		 */
		public TabBinding(AppW app, ObjectEventProperty property) {
			this.property = property;
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
			property.setScriptText(area.getText());
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
		 * @param property event property representing scripts for multiple events
		 * @param scriptTypeNames localized names for script types (GGBScript / JavaScript)
		 */
		public EventTabBinding(AppW app, ObjectEventProperty property,
				List<String> scriptTypeNames) {
			super(app, property);
			int defaultIndex = scriptTypeIndex();
			if (property.isJsEnabled()) {
				dropDown = new ComponentDropDown(app, KEY_SCRIPT_LANGUAGE,
						scriptTypeNames, defaultIndex);

				dropDown.addChangeHandler(() -> property.setScriptType(
						ScriptType.values()[dropDown.getSelectedIndex()])
				);
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
			area.setContent(property.getScriptText());
		}

		private int scriptTypeIndex() {
			return ScriptType.GGBSCRIPT.equals(property.getScriptType()) ? 0 : 1;
		}
	}

	/**
	 * Binding for the "Global JavaScript" tab. Displays and edits the kernel's
	 * library JavaScript.
	 */
	private static final class GlobalTabBinding extends TabBinding {

		private final Kernel kernel;

		public GlobalTabBinding(AppW app, ObjectEventProperty property) {
			super(app, property);
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
	 *
	 * @param app
	 *            application
	 * @param property
	 *            event property that exposes available events and script state
	 */
	public ScriptTabFactory(AppW app, ObjectAllEventsProperty property) {
		this.app = app;
		loc = app.getLocalization();
		this.property = property;
		jsEnabled = app.getScriptManager().isJsEnabled();
		localizedScriptTypeNames = getLocalizedScriptTypeNames();
	}

	private List<String> getLocalizedScriptTypeNames() {
		return Arrays.stream(ScriptType.values()).map(
						v -> loc.getMenu(v.getName()))
				.collect(Collectors.toList());
	}

	private TabData createEventTabData(ObjectEventProperty eventType, TabBinding binding) {
		return new TabData(
				loc.getMenu(eventType.getRawName()),
				binding.createPanel());

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

		for (ObjectEventProperty property: property.getProps()) {
			property.setJsEnabled(jsEnabled);

			if (!property.isEnabled()) {
				continue;
			}

			TabBinding binding;
			if (property.getRawName().startsWith("Global")) {
				binding = new GlobalTabBinding(app, property);
			} else {
				binding = new EventTabBinding(app, property,
						localizedScriptTypeNames);
			}
			tabData.add(createEventTabData(property, binding));
			bindings.add(binding);
		}

		ComponentTab tabs = new ComponentTab(loc, tabData.toArray(new TabData[0]));
		tabs.addTabChangedListener(evt -> bindings.get(tabs.getSelectedTabIdx())
				.activate());
		if (!tabData.isEmpty()) {
			tabs.switchToTab(0);
		}
		return tabs;
	}
}