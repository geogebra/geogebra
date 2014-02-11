package geogebra.touch.gui.laf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

interface AndroidResources extends DefaultResources {

	static AndroidResources INSTANCE = GWT.create(AndroidResources.class);

	// Dialogs

	@Override
	@Source("icons/png/android/arrow_go_next.png")
	ImageResource arrow_go_next();

	@Override
	@Source("icons/png/android/arrow_go_previous.png")
	ImageResource arrow_go_previous();

	@Override
	@Source("icons/png/android/menu_back.png")
	ImageResource back();

	@Override
	@Source("icons/png/android/button_cancel.png")
	ImageResource dialog_cancel();

	// Header

	@Override
	@Source("icons/png/android/button_trashcan.png")
	ImageResource dialog_trash();

	@Override
	@Source("icons/png/android/document_edit.png")
	ImageResource document_edit();

	@Override
	@Source("icons/png/android/document-new.png")
	ImageResource document_new();

	@Override
	@Source("icons/png/android/document-open.png")
	ImageResource document_open();

	@Override
	@Source("icons/png/android/document-save.png")
	ImageResource document_save();
	
	// Stylingbar
	
	@Override
	@Source("icons/png/android/stylingbar_graphicsview_standardview.png")
	ImageResource standardView();
	
	@Override
	@Source("icons/png/android/stylingbar_graphicsview_point_capturing.png")
	ImageResource pointCapturing();

	// GeoGebraTube View

	@Override
	@Source("icons/png/android/document_share.png")
	ImageResource document_share();

	@Override
	@Source("icons/png/android/document_viewer.png")
	ImageResource document_viewer();

	@Override
	@Source("icons/png/android/icon_fx.png")
	ImageResource icon_fx();

	@Override
	@Source("icons/png/android/icon_question.png")
	ImageResource icon_question();

	@Override
	@Source("icons/png/android/icon_warning.png")
	ImageResource icon_warning();

	@Override
	@Source("icons/png/android/menu_edit_redo.png")
	ImageResource redo();

	@Override
	@Source("icons/png/android/document-open.png")
	ImageResource search();

	@Override
	@Source("icons/png/android/menu_edit_undo.png")
	ImageResource undo();

}
