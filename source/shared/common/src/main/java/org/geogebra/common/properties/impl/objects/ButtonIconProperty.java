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

import java.util.Arrays;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.IconStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;
import org.jspecify.annotations.Nullable;

/** {@code Property} responsible for choosing a preset icon for a button. */
public class ButtonIconProperty extends AbstractEnumeratedProperty<ButtonIconProperty.ButtonIcon>
		implements IconsEnumeratedProperty<ButtonIconProperty.ButtonIcon>, GeoElementDependentProperty {
	/** Preset icons that can be displayed on a button. */
	public enum ButtonIcon {
		PLAY(ICON_BUTTON_PLAY, "play.svg"),
		PAUSE(ICON_BUTTON_PAUSE, "pause.svg"),
		STOP(ICON_BUTTON_STOP, "stop.svg"),
		FAST_REWIND(ICON_BUTTON_FAST_REWIND, "fast_rewind.svg"),
		FAST_FORWARD(ICON_BUTTON_FAST_FORWARD, "fast_forward.svg"),
		SKIP_PREVIOUS(ICON_BUTTON_SKIP_PREVIOUS, "skip_previous.svg"),
		SKIP_NEXT(ICON_BUTTON_SKIP_NEXT, "skip_next.svg"),
		LOOP(ICON_BUTTON_LOOP, "loop.svg"),
		REPLAY(ICON_BUTTON_REPLAY, "replay.svg"),
		UNDO(ICON_BUTTON_UNDO, "undo.svg"),
		REDO(ICON_BUTTON_REDO, "redo.svg"),
		ARROW_UP(ICON_BUTTON_ARROW_UP, "arrow_up.svg"),
		ARROW_DOWN(ICON_BUTTON_ARROW_DOWN, "arrow_down.svg"),
		ARROW_BACK(ICON_BUTTON_ARROW_BACK, "arrow_back.svg"),
		ARROW_FORWARD(ICON_BUTTON_ARROW_FORWARD, "arrow_forward.svg"),
		REMOVE(ICON_BUTTON_REMOVE, "remove.svg"),
		ADD(ICON_BUTTON_ADD, "add.svg"),
		CHECK_MARK(ICON_BUTTON_CHECK_MARK, "check_mark.svg"),
		CLOSE(ICON_BUTTON_CLOSE, "close.svg"),
		ZOOM_OUT(ICON_BUTTON_ZOOM_OUT, "zoom_out.svg"),
		ZOOM_IN(ICON_BUTTON_ZOOM_IN, "zoom_in.svg"),
		ZOOM_TO_FIT(ICON_BUTTON_ZOOM_TO_FIT, "zoom_to_fit.svg"),
		CENTER_VIEW(ICON_BUTTON_CENTER_VIEW, "center_view.svg"),
		HELP(ICON_BUTTON_HELP, "help.svg"),
		SETTINGS(ICON_BUTTON_SETTINGS, "settings.svg");

		private final PropertyResource resource;
		final String fileName;

		ButtonIcon(PropertyResource resource, String fileName) {
			this.resource = resource;
			this.fileName = fileName;
		}

		private static ButtonIcon fromImagePath(String imagePath, ImageManager imageManager) {
			return Arrays.stream(values())
					.filter(icon -> imagePath.equals(imageManager.getButtonIconPath(icon.fileName)))
					.findFirst()
					.orElse(null);
		}
	}

	private final AbstractGeoElementDelegate delegate;
	private final ImageManager imageManager;

	/**
	 * Constructs the property.
	 * @param localization localization for property labels
	 * @param imageManager image manager for resolving preset button icons
	 * @param element button element to configure
	 * @throws NotApplicablePropertyException if not filled by image
	 */
	public ButtonIconProperty(
			Localization localization, ImageManager imageManager, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, null);
		this.imageManager = imageManager;
		delegate = new IconStylePropertyDelegate(element);
		setValues(Arrays.asList(ButtonIcon.values()));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return Arrays.stream(ButtonIcon.values())
				.map(icon -> icon.resource)
				.toArray(PropertyResource[]::new);
	}

	@Override
	public @Nullable String[] getToolTipLabels() {
		return null;
	}

	@Override
	public @Nullable ButtonIcon getValue() {
		return ButtonIcon.fromImagePath(delegate.getElement().getImageFileName(), imageManager);
	}

	@Override
	protected void doSetValue(ButtonIcon value) {
		String imagePath =
				imageManager.applyButtonIcon(value.fileName, delegate.getElement().getKernel());
		delegate.getElement().setFillImage(imagePath);
		delegate.getElement().updateVisualStyleRepaint(GProperty.COMBINED);
	}

	static boolean isButtonIconPath(String imagePath, ImageManager imageManager) {
		return ButtonIcon.fromImagePath(imagePath, imageManager) != null;
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
