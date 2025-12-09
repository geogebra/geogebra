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

package org.geogebra.common.properties.impl.objects;

import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * Property for configuring per-object event scripts in the UI. It exposes the
 * available {@link EventType} options for a {@link GeoElement} and manages the
 * selected event's script and {@link ScriptType}, as well as access to app-wide
 * (global) JavaScript.
 */
public class ElementObjectEventProperty extends AbstractProperty implements ObjectEventProperty {
	private final GeoElement geo;
	private final App app;
	private final @Nonnull EventType eventType;

	public final static Map<EventType, String> eventNames = Map.of(
			EventType.CLICK, "OnClick",
				EventType.UPDATE, "OnUpdate",
				EventType.DRAG_END, "OnDragEnd",
				EventType.EDITOR_KEY_TYPED, "OnChange",
				EventType.LOAD_PAGE, "GlobalJavaScript");
	private ScriptType scriptType = ScriptType.GGBSCRIPT;
	private boolean jsEnabled;

	/**
	 * Creates an {@code ElementObjectEventProperty} for the given element.
	 *
	 * @param localization localization for property name and labels
	 * @param geo the geo whose events are configured
	 */
	public ElementObjectEventProperty(Localization localization, GeoElement geo, EventType type) {
		super(localization, eventNames.get(type));
		this.geo = geo;
		app = geo.getApp();
		eventType = type;
		Script script = geo.getScript(eventType);
		scriptType = script != null ? script.getType() : ScriptType.GGBSCRIPT;
	}

	private boolean isDraggable(GeoElement geo) {
		EuclidianViewInterfaceCommon view = SelectionManager.getViewOf(geo, app);
		return !geo.isLocked() && (geo.isPointerChangeable() || geo.isMoveable()
				|| geo.hasMoveableInputPoints(view));
	}

	@Override
	public ScriptType getScriptType() {
		return scriptType;
	}

	@Override
	public void setScriptType(ScriptType value) {
		scriptType = value;
	}

	@Override
	public String getScriptText() {
		if (eventType == EventType.LOAD_PAGE) {
			return app.getKernel().getLibraryJavaScript();
		}
		Script script = geo.getScript(eventType);
		return script == null ? "" : script.getText();
	}

	@Override
	public void setScriptText(String text) {
		if (eventType == EventType.LOAD_PAGE) {
			app.getKernel().setLibraryJavaScript(text);
			return;
		}

		Script script = app.createScript(scriptType, text, true);
		geo.setScript(script, eventType);
	}

	/**
	 * Returns a display name for the given event type used in the UI.
	 *
	 * @param eventType the event type
	 * @return a display name, or {@code "unknown"} if not mapped
	 */
	public static String objectEventName(EventType eventType) {
		return eventNames.getOrDefault(eventType, "unknown");
	}

	@Override
	public boolean isEnabled() {
		switch (eventType) {
		case CLICK: return geo.canHaveClickScript();
		case UPDATE: return geo.canHaveUpdateScript();
		case EDITOR_KEY_TYPED: return geo.isGeoInputBox();
		case DRAG_END: return isDraggable(geo) && !geo.isGeoBoolean();
		case LOAD_PAGE: return jsEnabled;
		default: return false;
		}
	}

	@Override
	public void setJsEnabled(boolean jsEnabled) {
		this.jsEnabled = jsEnabled;
	}

	@Override
	public boolean isJsEnabled() {
		return jsEnabled;
	}
}
