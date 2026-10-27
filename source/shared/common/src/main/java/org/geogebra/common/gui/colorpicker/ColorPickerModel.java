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

package org.geogebra.common.gui.colorpicker;

import static org.geogebra.common.main.GeoGebraColorConstants.ACCENT_700;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_BLUE_BACKGROUND;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_ORANGE_BACKGROUND;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_ORANGE_PRIMARY;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_RED_BACKGROUND;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_RED_PRIMARY;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_TEAL_BACKGROUND;
import static org.geogebra.common.main.GeoGebraColorConstants.APPLET_TEAL_PRIMARY;
import static org.geogebra.common.main.GeoGebraColorConstants.MEBIS_PURPLE_100;
import static org.geogebra.common.main.GeoGebraColorConstants.MEBIS_PURPLE_300;
import static org.geogebra.common.main.GeoGebraColorConstants.MEBIS_PURPLE_A400;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_0;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_100;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_200;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_300;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_400;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_500;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_700;
import static org.geogebra.common.main.GeoGebraColorConstants.NEUTRAL_900;
import static org.geogebra.common.main.GeoGebraColorConstants.PURPLE_100;
import static org.geogebra.common.main.GeoGebraColorConstants.PURPLE_600;
import static org.geogebra.common.main.GeoGebraColorConstants.PURPLE_700;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * View model of the color picker for one editing session of a {@link ColorProperty}.
 * <p>
 * Changes are applied to the property immediately, between {@code beginSetValue()} and
 * {@code endSetValue()}; the latter is called by {@link #finish()}. To store an undo point,
 * attach an {@link org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver} to the
 * property.
 */
public final class ColorPickerModel {

	/** RGB channel of the color picker's RGB text fields. */
	public enum Channel {
		RED,
		GREEN,
		BLUE
	}

	/** The default palette (4 rows of 8 colors, in row order) */
	public static final List<GColor> PALETTE = List.of(
			NEUTRAL_900,
			NEUTRAL_700,
			NEUTRAL_500,
			NEUTRAL_400,
			NEUTRAL_300,
			NEUTRAL_200,
			NEUTRAL_100,
			NEUTRAL_0,
			MEBIS_PURPLE_A400,
			PURPLE_700,
			GColor.newColorRGB(0x143E92),
			APPLET_TEAL_PRIMARY,
			GColor.newColorRGB(0xF1C232),
			APPLET_ORANGE_PRIMARY,
			APPLET_RED_PRIMARY,
			GColor.newColorRGB(0x670000),
			MEBIS_PURPLE_300,
			PURPLE_600,
			GColor.newColorRGB(0x1565C0),
			ACCENT_700,
			GColor.newColorRGB(0xFFCC02),
			GColor.newColorRGB(0xE07415),
			GColor.newColorRGB(0xD32F2F),
			GColor.newColorRGB(0x9E4848),
			MEBIS_PURPLE_100,
			PURPLE_100,
			APPLET_BLUE_BACKGROUND,
			APPLET_TEAL_BACKGROUND,
			GColor.newColorRGB(0xFFF2CC),
			APPLET_ORANGE_BACKGROUND,
			APPLET_RED_BACKGROUND,
			GColor.newColorRGB(0xE4D2D2));

	private final ColorProperty property;
	private final ColorHistory history;
	private final MutableState<@NonNull HsvColor> color;
	private final MutableState<@NonNull Boolean> editorExpanded = new MutableState<>(false);
	/** Last color applied to the property; {@code null} forces the next change through. */
	private @Nullable GColor appliedColor;

	private boolean sessionStarted;
	private boolean liveSlotStarted;
	private boolean finished;

	/**
	 * Adds the property's current color to the history.
	 * @param property the color property to edit
	 * @param history the app's color history
	 */
	public ColorPickerModel(@NonNull ColorProperty property, @NonNull ColorHistory history) {
		this.property = property;
		this.history = history;
		GColor current = property.getValue();
		GColor initial = current == null ? GColor.BLACK : current;
		color = new MutableState<>(HsvColor.fromGColor(initial, null));
		if (current != null) {
			history.add(current);
		}
		appliedColor = property.hasUniformValue() ? current : null;
	}

	/** @return the current color, the picker's source of truth */
	public @NonNull State<@NonNull HsvColor> getColor() {
		return color;
	}

	/** @return the "Your colors" history, newest first */
	public @NonNull State<@NonNull List<GColor>> getYourColors() {
		return history.getColors();
	}

	/** @return whether the custom color editor is expanded */
	public @NonNull State<@NonNull Boolean> getEditorExpanded() {
		return editorExpanded;
	}

	/** @return the localized name of the edited property, for the page title */
	public @NonNull String getTitle() {
		return property.getName();
	}

	/**
	 * Selects a color of the default palette.
	 * @param swatch the selected color
	 */
	public void selectColor(@NonNull GColor swatch) {
		applyRgb(swatch, true);
	}

	/**
	 * Selects a color of the "Your colors" history, without changing the history.
	 * @param swatch the selected color
	 */
	public void selectYourColor(@NonNull GColor swatch) {
		applyRgb(swatch, false);
	}

	/**
	 * Sets the hue from the hue slider.
	 * @param hue hue in degrees
	 */
	public void setHue(double hue) {
		apply(color.get().withHue(hue), true);
	}

	/**
	 * Sets saturation and value from the saturation/value plane.
	 * @param saturation saturation in [0, 1]
	 * @param value value in [0, 1]
	 */
	public void setSaturationValue(double saturation, double value) {
		apply(color.get().withSaturationAndValue(saturation, value), true);
	}

	/**
	 * Commits the hex text field. Invalid input is ignored.
	 * @param text the entered text
	 */
	public void commitHex(@NonNull String text) {
		String hex = text.trim();
		GColor parsed = GColor.parseHexColor(hex.startsWith("#") ? hex : "#" + hex);
		if (parsed != null) {
			applyRgb(parsed, true);
		}
	}

	/**
	 * Commits one RGB text field. Invalid input is ignored.
	 * @param channel the edited channel
	 * @param text the entered text
	 */
	public void commitRgb(@NonNull Channel channel, @NonNull String text) {
		Integer parsed = GColor.parseChannel(text);
		if (parsed == null) {
			return;
		}
		GColor current = color.get().toGColor();
		int red = channel == Channel.RED ? parsed : current.getRed();
		int green = channel == Channel.GREEN ? parsed : current.getGreen();
		int blue = channel == Channel.BLUE ? parsed : current.getBlue();
		applyRgb(GColor.newColor(red, green, blue), true);
	}

	/** Expands the custom color editor. */
	public void expandEditor() {
		editorExpanded.set(true);
	}

	/**
	 * Ends the editing session and commits the color to the history. Must be called when the
	 * picker is closed; later calls to this model are ignored.
	 */
	public void finish() {
		if (finished) {
			return;
		}
		finished = true;
		if (sessionStarted) {
			property.endSetValue();
		}
		if (liveSlotStarted) {
			history.commitLiveSlot();
		}
	}

	/** Keeps the current HSV color if the RGB is unchanged, so the handles do not move. */
	private void applyRgb(GColor rgb, boolean feedsLiveSlot) {
		HsvColor current = color.get();
		GColor currentRgb = current.toGColor();
		apply(rgb.equals(currentRgb) ? current : HsvColor.fromGColor(rgb, current), feedsLiveSlot);
	}

	private void apply(HsvColor next, boolean feedsLiveSlot) {
		if (finished) {
			return;
		}
		color.set(next);
		GColor nextColor = next.toGColor();
		if (nextColor.equals(appliedColor)) {
			return;
		}
		if (!sessionStarted) {
			sessionStarted = true;
			property.beginSetValue();
		}
		appliedColor = nextColor;
		property.setValue(nextColor);
		if (!feedsLiveSlot) {
			return;
		}
		if (liveSlotStarted) {
			history.updateLiveSlot(nextColor);
		} else {
			liveSlotStarted = true;
			history.beginLiveSlot(nextColor);
		}
	}
}
