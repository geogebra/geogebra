package geogebra.touch.gui.laf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

interface AppleResources extends DefaultResources {

	static AppleResources INSTANCE = GWT.create(AppleResources.class);

	@Override
	@Source("icons/png/iOS7/algebra_hidden.png")
	ImageResource algebra_hidden();

	@Override
	@Source("icons/png/iOS7/algebra_shown.png")
	ImageResource algebra_shown();
	
	// Dialogs

	@Override
	@Source("icons/png/android/arrow_go_next.png")
	ImageResource arrow_go_next();

	@Override
	@Source("icons/png/android/arrow_go_previous.png")
	ImageResource arrow_go_previous();

	@Override
	@Source("icons/png/iOS7/menu_back.png")
	ImageResource back();

	@Override
	@Source("icons/png/iOS7/button_cancel.png")
	ImageResource dialog_cancel();

	// Header

	@Override
	@Source("icons/png/iOS7/button_trashcan.png")
	ImageResource dialog_trash();

	@Override
	@Source("icons/png/iOS7/document_edit.png")
	ImageResource document_edit();

	@Override
	@Source("icons/png/iOS7/document_new.png")
	ImageResource document_new();

	@Override
	@Source("icons/png/iOS7/document_open.png")
	ImageResource document_open();

	@Override
	@Source("icons/png/iOS7/document_save.png")
	ImageResource document_save();

	// GeoGebraTube View

	@Override
	@Source("icons/png/iOS7/document_share.png")
	ImageResource document_share();

	@Override
	@Source("icons/png/iOS7/document_view.png")
	ImageResource document_viewer();

	@Override
	@Source("icons/png/empty.png")
	ImageResource icon_fx();

	@Override
	@Source("icons/png/android/icon_question.png")
	ImageResource icon_question();

	@Override
	@Source("icons/png/android/icon_warning.png")
	ImageResource icon_warning();

	@Override
	@Source("icons/png/iOS7/menu_edit_redo.png")
	ImageResource redo();

	@Override
	@Source("icons/png/iOS7/search.png")
	ImageResource search();

	@Override
	@Source("icons/png/iOS7/menu_edit_undo.png")
	ImageResource undo();

	@Override
	@Source("icons/png/iOS7/elem_radioButtonActive.png")
	ImageResource radioButtonActive();

	@Override
	@Source("icons/png/iOS7/elem_radioButtonInactive.png")
	ImageResource radioButtonInactive();
	
	@Override
	@Source("icons/png/iOS7/arrow_dockbar_triangle_left.png")
	ImageResource triangle_left();
	
	// Stylingbar
	
	@Override
	@Source("icons/png/iOS7/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource show_or_hide_the_axes();

	@Override
	@Source("icons/png/iOS7/stylingbar_graphicsview_show_or_hide_the_grid.png")
	ImageResource show_or_hide_the_grid();
}
