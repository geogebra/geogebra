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

package org.geogebra.web.html5.main.general;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

/**
 * Icons for the top bar.
 */
@Resource
public interface DefaultGeneralIconResources extends ClientBundle {

	DefaultGeneralIconResources INSTANCE = new DefaultGeneralIconResourcesImpl();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dialog/open_in_new.svg")
	SVGResource new_tab();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_delete_black_24px.svg")
	SVGResource delete();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_more_vert_black_24px.svg")
	SVGResource more();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/ic_crop_black_24px.svg")
	SVGResource crop();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/format_color_reset-24px.svg")
	SVGResource no_color();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_check_black_24px.svg")
	SVGResource check_mark();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_content_cut_black_24px.svg")
	SVGResource cut();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_content_copy_black_24px.svg")
	SVGResource copy();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_content_paste_black_24px.svg")
	SVGResource paste();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_lock_outline_black_24px.svg")
	SVGResource lock();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_settings_black_24px.svg")
	SVGResource settings();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/arrow_drop_right_black.svg")
	SVGResource arrow_right();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/arrow_drop_left_black.svg")
	SVGResource arrow_left();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/add.svg")
	SVGResource plus();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/control_point_duplicate.svg")
	SVGResource duplicate();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rename_box_black_24px.svg")
	SVGResource rename();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/fontawesome/scissors.svg")
	SVGResource scissors_fontawesome();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/table_heading_column.svg")
	SVGResource table_heading_column();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/fontawesome/table-heading-column.svg")
	SVGResource table_heading_column_fontawesome();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/table_heading_row.svg")
	SVGResource table_heading_row();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/fontawesome/table-heading-row.svg")
	SVGResource table_heading_row_fontawesome();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_subscript.svg")
	SVGResource x_2();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/fontawesome/x-2.svg")
	SVGResource x_2_fontawesome();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_superscript.svg")
	SVGResource x_square();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/fontawesome/x-square.svg")
	SVGResource x_square_fontawesome();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_list_bulleted-24px.svg")
	SVGResource bullet_list();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/format_list_numbered-24px.svg")
	SVGResource numbered_list();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/chart_line.svg")
	SVGResource line_chart();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/chart_bar.svg")
	SVGResource bar_chart();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/chart_pie.svg")
	SVGResource pie_chart();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/rotate_right.svg")
	SVGResource rotate_arrow();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/fontawesome/arrow-rotate-right.svg")
	SVGResource rotate_arrow_fontawesome();
}
