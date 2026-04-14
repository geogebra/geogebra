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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;

/**
 * {@code Property} responsible for providing the possible script events the given element can emit
 * and for selecting one for scripting.
 */
public class ScriptEventSelectionProperty extends AbstractNamedEnumeratedProperty<ScriptEvent> {
	private final List<ScriptEvent> availableScriptEvents;
	private ScriptEvent selectedScriptEvent;

	/** Events for which scripts can be defined. */
	public enum ScriptEvent {
		OnClick(EventType.CLICK, "OnClick"),
		OnUpdate(EventType.UPDATE, "OnUpdate"),
		OnDragEnd(EventType.DRAG_END, "OnDragEnd"),
		OnChange(EventType.EDITOR_KEY_TYPED, "OnChange"),
		GlobalJavascript(EventType.LOAD_PAGE, "GlobalJavaScript");

		final EventType eventType;
		final String translationKey;

		ScriptEvent(EventType eventType, String translationKey) {
			this.eventType = eventType;
			this.translationKey = translationKey;
		}
	}

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating property names
	 * @param geoElement the element whose enabled events determine the available script events
	 * @param jsEnabled whether JavaScript is enabled in the app
	 */
	public ScriptEventSelectionProperty(Localization localization, GeoElement geoElement,
			boolean jsEnabled) {
		super(localization, "Script");
		this.availableScriptEvents = buildEnabledScriptEvents(geoElement, jsEnabled);
		this.selectedScriptEvent = availableScriptEvents.get(0);
	}

	@Override
	public @Nonnull List<ScriptEvent> getValues() {
		return availableScriptEvents;
	}

	@Override
	public String[] getValueNames() {
		return availableScriptEvents.stream()
				.map(scriptEvent -> scriptEvent.translationKey)
				.toArray(String[]::new);
	}

	@Override
	protected void doSetValue(ScriptEvent scriptEvent) {
		this.selectedScriptEvent = scriptEvent;
	}

	@Override
	public ScriptEvent getValue() {
		return selectedScriptEvent;
	}

	private static List<ScriptEvent> buildEnabledScriptEvents(GeoElement geoElement,
			boolean jsEnabled) {
		List<ScriptEvent> enabled = new ArrayList<>();
		if (geoElement.canHaveClickScript()) {
			enabled.add(ScriptEvent.OnClick);
		}
		if (geoElement.canHaveUpdateScript()) {
			enabled.add(ScriptEvent.OnUpdate);
		}
		EuclidianViewInterfaceCommon euclidianView = SelectionManager.getViewOf(
				geoElement, geoElement.getApp());
		boolean isDraggable = !geoElement.isLocked() && (geoElement.isPointerChangeable()
				|| geoElement.isMoveable() || geoElement.hasMoveableInputPoints(euclidianView));
		if (isDraggable && !geoElement.isGeoBoolean()) {
			enabled.add(ScriptEvent.OnDragEnd);
		}
		if (geoElement.isGeoInputBox()) {
			enabled.add(ScriptEvent.OnChange);
		}
		if (jsEnabled) {
			enabled.add(ScriptEvent.GlobalJavascript);
		}
		return enabled;
	}
}
