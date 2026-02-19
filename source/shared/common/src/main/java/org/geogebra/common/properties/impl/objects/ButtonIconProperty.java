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

import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ADD;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ARROW_BACK;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ARROW_DOWN;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ARROW_FORWARD;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ARROW_UP;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_CENTER_VIEW;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_CHECK_MARK;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_CLOSE;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_FAST_FORWARD;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_FAST_REWIND;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_HELP;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_LOOP;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_PAUSE;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_PLAY;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_REDO;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_REMOVE;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_REPLAY;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_SETTINGS;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_SKIP_NEXT;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_SKIP_PREVIOUS;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_STOP;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_UNDO;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ZOOM_IN;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ZOOM_OUT;
import static org.geogebra.common.properties.PropertyResource.ICON_BUTTON_ZOOM_TO_FIT;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.IconStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Icon style
 */
public class ButtonIconProperty extends AbstractEnumeratedProperty<String>
		implements IconsEnumeratedProperty<String> {
	private static final PropertyResource[] icons = {
			ICON_BUTTON_PLAY, ICON_BUTTON_PAUSE, ICON_BUTTON_STOP, ICON_BUTTON_FAST_REWIND,
			ICON_BUTTON_FAST_FORWARD, ICON_BUTTON_SKIP_PREVIOUS, ICON_BUTTON_SKIP_NEXT,
			ICON_BUTTON_LOOP, ICON_BUTTON_REPLAY, ICON_BUTTON_UNDO, ICON_BUTTON_REDO,
			ICON_BUTTON_ARROW_UP, ICON_BUTTON_ARROW_DOWN, ICON_BUTTON_ARROW_BACK,
			ICON_BUTTON_ARROW_FORWARD, ICON_BUTTON_REMOVE, ICON_BUTTON_ADD, ICON_BUTTON_CHECK_MARK,
			ICON_BUTTON_CLOSE, ICON_BUTTON_ZOOM_OUT, ICON_BUTTON_ZOOM_IN, ICON_BUTTON_ZOOM_TO_FIT,
			ICON_BUTTON_CENTER_VIEW, ICON_BUTTON_HELP, ICON_BUTTON_SETTINGS};
	private static final List<String> iconNames = List.of("play.svg", "pause.svg", "stop.svg",
			"fast_rewind.svg", "fast_forward.svg", "skip_previous.svg", "skip_next.svg",
			"loop.svg", "replay.svg", "undo.svg", "redo.svg", "arrow_up.svg", "arrow_down.svg",
			"arrow_back.svg", "arrow_forward.svg", "remove.svg", "add.svg", "check_mark.svg",
			"close.svg", "zoom_out.svg", "zoom_in.svg", "zoom_to_fit.svg", "center_view.svg",
			"help.svg", "settings.svg");
	private final AbstractGeoElementDelegate delegate;

	/**
	 * List of default icons for buttons.
	 * @param localization {@link Localization}
	 * @param element button
	 * @throws NotApplicablePropertyException if not filled by image
	 */
	public ButtonIconProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, null);
		delegate = new IconStylePropertyDelegate(element);
		setValues(iconNames);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	public String getValue() {
		return delegate.getElement().getImageFileName();
	}

	@Override
	protected void doSetValue(String value) {
		delegate.getElement().setFillImage(value);
		delegate.getElement().updateVisualStyleRepaint(GProperty.COMBINED);
	}
}
