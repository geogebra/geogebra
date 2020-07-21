package org.geogebra.web.html5.css;

import org.geogebra.web.html5.util.LessResource;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResourcesSimple extends ClientBundle {

	GuiResourcesSimple INSTANCE = GWT.create(GuiResourcesSimple.class);

	// @Source("org/geogebra/web/resources/js/zipjs/deflate.js")
	// TextResource deflateJs();

	@Source("org/geogebra/web/resources/js/rewrite_pHYs_min.js")
	TextResource rewritePHYS();

	@Source("org/geogebra/web/resources/js/canvas2svg_min.js")
	TextResource canvas2Svg();

	@Source("org/geogebra/web/resources/js/canvas2pdf_min.js")
	TextResource canvas2Pdf();

	@Source("org/geogebra/web/resources/js/xmlutil.js")
	TextResource xmlUtil();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle.png")
	ImageResource icons_play_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle_hover.png")
	ImageResource icons_play_circle_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle.png")
	ImageResource icons_play_pause_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle_hover.png")
	ImageResource icons_play_pause_circle_hover();

	@Source("org/geogebra/web/resources/js/zipjs/codecs.js")
	TextResource pakoCodecJs();

	@Source("org/geogebra/web/resources/js/zipjs/pako1.0.6_min.js")
	TextResource pakoJs();

	@Source("org/geogebra/web/resources/js/zipjs/zip-ext.js")
	TextResource dataViewJs();

	@Source("org/geogebra/web/resources/js/zipjs/zip.js")
	TextResource zipJs();

	// used by ExamUtil (eg toggleFullScreen)
	@Source("org/geogebra/web/resources/js/visibility.js")
	TextResource visibilityJs();

	@Source("org/geogebra/web/resources/js/domvas.js")
	TextResource domvas();

	@Source("org/geogebra/web/resources/js/clipboard.js")
	TextResource clipboardJs();

	@Source("org/geogebra/web/resources/css/web-styles-global.less")
	LessResource modernStyleGlobal();

	@Source("org/geogebra/web/resources/scss/ev-styles.scss")
	SassResource evStyleScss();
	
	@Source("org/geogebra/web/resources/scss/shared.scss")
	SassResource sharedStyleScss();

	@Source("org/geogebra/web/resources/scss/colors.scss")
	SassResource colorsScss();

	@Source("org/geogebra/web/resources/scss/layout.scss")
	SassResource layoutScss();

	@Source("org/geogebra/common/icons/png/view_refresh.png")
	ImageResource viewRefresh();

	@Source("org/geogebra/web/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();

	@Source("org/geogebra/web/resources/images/ggbSplash.html")
	TextResource ggbSplashHtml();

	// INFO, WARNING, QUESTION, ERROR
	@Source("org/geogebra/common/icons/png/web/dialog-error.png")
	ImageResource dialog_error();

	@Source("org/geogebra/common/icons/png/web/dialog-info.png")
	ImageResource dialog_info();

	@Source("org/geogebra/common/icons/png/web/dialog-question.png")
	ImageResource dialog_question();

	@Source("org/geogebra/common/icons/png/web/dialog-warning.png")
	ImageResource dialog_warning();

	@Source("org/geogebra/common/icons/png/web/icon-dialog-warning.png")
	ImageResource icon_dialog_warning();

	@Source("org/geogebra/common/icons/png/web/question-mark.png")
	ImageResource questionMark();

	@Source("org/geogebra/common/icons/png/web/mode_toggle_numeric.png")
	ImageResource modeToggleNumeric();

	@Source("org/geogebra/common/icons/png/web/mode_toggle_symbolic.png")
	ImageResource modeToggleSymbolic();

	// TODO we need another picture
	@Source("org/geogebra/common/icons/png/android/document_viewer.png")
	ImageResource viewSaved();

	@Source("org/geogebra/web/resources/js/promise-1.0.0.min.js")
	TextResource promiseJs();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_down.png")
	ImageResource icons_fillings_arrow_big_down();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_up.png")
	ImageResource icons_fillings_arrow_big_up();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_left.png")
	ImageResource icons_fillings_arrow_big_left();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_right.png")
	ImageResource icons_fillings_arrow_big_right();

	@Source("org/geogebra/common/icons_fillings/p18/filling_fastforward.png")
	ImageResource icons_fillings_fastforward();

	@Source("org/geogebra/common/icons_fillings/p18/filling_rewind.png")
	ImageResource icons_fillings_rewind();

	@Source("org/geogebra/common/icons_fillings/p18/filling_skipback.png")
	ImageResource icons_fillings_skipback();

	@Source("org/geogebra/common/icons_fillings/p18/filling_skipforward.png")
	ImageResource icons_fillings_skipforward();

	@Source("org/geogebra/common/icons_fillings/p18/filling_play.png")
	ImageResource icons_fillings_play();

	@Source("org/geogebra/common/icons_fillings/p18/filling_pause.png")
	ImageResource icons_fillings_pause();

	@Source("org/geogebra/common/icons_fillings/p18/filling_cancel.png")
	ImageResource icons_fillings_cancel();

	@Source("org/geogebra/web/resources/scss/reset.scss")
	SassResource reset();

	@Source("org/geogebra/web/resources/js/gifshot.image.min.js")
	TextResource gifShotJs();

	@Source("org/geogebra/web/resources/js/whammy.min.js")
	TextResource whammyJs();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/video_player_black_opacity54_360px.png")
	ImageResource mow_video_player();

	// MOW Rulings
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rulings/elementary12.svg")
	SVGResource mow_ruling_elementary12();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rulings/elementary12house.svg")
	SVGResource mow_ruling_elementary12house();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rulings/elementary34.svg")
	SVGResource mow_ruling_elementary34();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rulings/music.svg")
	SVGResource mow_ruling_music();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_play_circle_outline_black_24px.svg")
	SVGResource play_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_play_circle_outline_purple_24px.svg")
	SVGResource play_purple();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/ic_pause_circle_outline_black_24px.svg")
	SVGResource pause_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/pause_purple.svg")
	SVGResource pause_purple();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/baseline-record_voice_over-24px.svg")
	SVGResource record();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/rulings/elementary12colored.svg")
	SVGResource mow_ruling_elementary12colored();
}
