package org.geogebra.keyboard.web;

import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * image and style resources of keyboard
 */
@SuppressWarnings("javadoc")
public interface KeyboardResources extends ClientBundle {

	KeyboardResources INSTANCE = GWT.create(KeyboardResources.class);

	// ONSCREENKEYBOARD
	@Source("org/geogebra/common/icons/svg/web/keyboard/ic_more_horiz_black_24px.svg")
	SVGResource keyboard_more();

	@Source("org/geogebra/common/icons/svg/web/keyboard/ic_more_horiz_purple_24px.svg")
	SVGResource keyboard_more_purple();

	// close button for new keyboard
	@Source("org/geogebra/common/icons/svg/common/close.svg")
	SVGResource keyboard_close_black();

	@Source("org/geogebra/common/icons/svg/web/keyboard/ic_close_purple_24px.svg")
	SVGResource keyboard_close_purple();

	@Source("org/geogebra/common/icons/png/keyboard/shift_purple.png")
	ImageResource keyboard_shiftDown();

	@Source("org/geogebra/common/icons/png/keyboard/shift_black.png")
	ImageResource keyboard_shift();

	@Source("org/geogebra/common/icons/png/keyboard/backspace.png")
	ImageResource keyboard_backspace();

	// enter for new keyboard
	@Source("org/geogebra/common/icons/png/keyboard/keyboard_enter_black.png")
	ImageResource keyboard_enter_black();

	// left arrow for new keyboard
	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowLeft_black.png")
	ImageResource keyboard_arrowLeft_black();

	// right arrow for new keyboard
	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowRight_black.png")
	ImageResource keyboard_arrowRight_black();

	// up arrow for new keyboard
	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowUp_black.png")
	ImageResource keyboard_arrowUp_black();

	// down arrow for new keyboard
	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowDown_black.png")
	ImageResource keyboard_arrowDown_black();

	@Source("org/geogebra/common/icons/svg/web/keyboard/ic_keyboard_black_24px.svg")
	SVGResource keyboard_show_material();

	@Source("org/geogebra/keyboard/css/keyboard-styles.scss")
	SassResource keyboardStyle();

	@Source("org/geogebra/keyboard/css/greek-font.css")
	SassResource greekFonts();

	@Source("org/geogebra/common/icons/png/keyboard/integral.png")
	ImageResource integral();

	@Source("org/geogebra/common/icons/png/keyboard/d_dx.png")
	ImageResource derivative();

	@Source("org/geogebra/common/icons/svg/web/keyboard/square.svg")
	SVGResource square();

	@Source("org/geogebra/common/icons/svg/web/keyboard/power.svg")
	SVGResource power();

	@Source("org/geogebra/common/icons/svg/web/keyboard/sqrt.svg")
	SVGResource sqrt();

	@Source("org/geogebra/common/icons/svg/web/keyboard/fraction.svg")
	SVGResource fraction();

	@Source("org/geogebra/common/icons/svg/web/keyboard/inverse.svg")
	SVGResource inverse();

	@Source("org/geogebra/common/icons/svg/web/keyboard/abs.svg")
	SVGResource abs();

	@Source("org/geogebra/common/icons/svg/web/keyboard/log.svg")
	SVGResource log();

	@Source("org/geogebra/common/icons/svg/web/keyboard/e_power.svg")
	SVGResource e_power();

	@Source("org/geogebra/common/icons/svg/web/keyboard/ten_power.svg")
	SVGResource ten_power();

	@Source("org/geogebra/common/icons/svg/web/keyboard/n_root.svg")
	SVGResource n_root();

	@Source("org/geogebra/common/icons/svg/web/keyboard/subscript.svg")
	SVGResource subscript();

	@Source("org/geogebra/common/icons/svg/web/keyboard/ceil.svg")
	SVGResource ceil();

	@Source("org/geogebra/common/icons/svg/web/keyboard/floor.svg")
	SVGResource floor();

	@Source("org/geogebra/common/icons/svg/web/keyboard/definite_integral.svg")
	SVGResource definite_integral();

	@Source("org/geogebra/common/icons/svg/web/keyboard/lim.svg")
	SVGResource lim();

	@Source("org/geogebra/common/icons/svg/web/keyboard/product.svg")
	SVGResource product();

	@Source("org/geogebra/common/icons/svg/web/keyboard/sum.svg")
	SVGResource sum();

	@Source("org/geogebra/common/icons/svg/web/keyboard/vector.svg")
	SVGResource vector();
}
