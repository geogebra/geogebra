package org.geogebra.keyboard.web;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

/**
 * image and style resources of keyboard
 */
@Resource
public interface KeyboardResources extends ClientBundle {

	KeyboardResources INSTANCE = new KeyboardResourcesImpl();

	// ONSCREENKEYBOARD
	@Source("org/geogebra/common/icons/svg/web/keyboard/ic_more_horiz_black_24px.svg")
	SVGResource keyboard_more();

	// close button for new keyboard
	@Source("org/geogebra/common/icons/svg/common/close.svg")
	SVGResource keyboard_close_black();

	@Source("org/geogebra/common/icons/svg/web/keyboard/shift_purple.svg")
	SVGResource keyboard_shiftDown();

	@Source("org/geogebra/common/icons/svg/web/keyboard/shift_black.svg")
	SVGResource keyboard_shift();

	@Source("org/geogebra/common/icons/svg/web/keyboard/backspace.svg")
	SVGResource keyboard_backspace();

	// enter for new keyboard
	@Source("org/geogebra/common/icons/svg/web/keyboard/keyboard_enter_black.svg")
	SVGResource keyboard_enter_black();

	// left arrow for new keyboard
	@Source("org/geogebra/common/icons/svg/web/keyboard/keyboard_arrowLeft_black.svg")
	SVGResource keyboard_arrowLeft_black();

	// right arrow for new keyboard
	@Source("org/geogebra/common/icons/svg/web/keyboard/keyboard_arrowRight_black.svg")
	SVGResource keyboard_arrowRight_black();

	// up arrow for new keyboard
	@Source("org/geogebra/common/icons/svg/web/keyboard/keyboard_arrowUp_black.svg")
	SVGResource keyboard_arrowUp_black();

	// down arrow for new keyboard
	@Source("org/geogebra/common/icons/svg/web/keyboard/keyboard_arrowDown_black.svg")
	SVGResource keyboard_arrowDown_black();

	@Source("org/geogebra/common/icons/svg/web/keyboard/ic_keyboard_black_24px.svg")
	SVGResource keyboard_show_material();

	@Source("org/geogebra/common/icons/svg/web/keyboard/integral.svg")
	SVGResource integral();

	@Source("org/geogebra/common/icons/svg/web/keyboard/derivative.svg")
	SVGResource derivative();

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

	@Source("org/geogebra/common/icons/svg/web/keyboard/atomic_post.svg")
	SVGResource atomic_post();

	@Source("org/geogebra/common/icons/svg/web/keyboard/atomic_pre.svg")
	SVGResource atomic_pre();

	@Source("org/geogebra/common/icons/svg/web/keyboard/mixed_number.svg")
	SVGResource mixed_number();

	@Source("org/geogebra/common/icons/svg/web/keyboard/recurring_decimal.svg")
	SVGResource recurring_decimal();

	@Source("org/geogebra/common/icons/svg/web/keyboard/point_template.svg")
	SVGResource point_template();

	@Source("org/geogebra/common/icons/svg/web/keyboard/vector_template.svg")
	SVGResource vector_template();

	@Source("org/geogebra/common/icons/svg/web/keyboard/matrix_template.svg")
	SVGResource matrix_template();

}
