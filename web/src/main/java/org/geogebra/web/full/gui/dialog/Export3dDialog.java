package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.kernel.View;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Dialog for export 3D
 *
 */
public class Export3dDialog extends OptionDialog
		implements Export3dDialogInterface, SetLabels {

	private String extension;
	private Runnable onExportButtonPressed;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            app
	 * @param view
	 *            exported view
	 * @param extension
	 *            format extension
	 */
	public Export3dDialog(final AppW app, final View view,
			String extension) {
		super(app.getPanel(), app, false);
		this.extension = extension;
		buildGui();
		setPrimaryButtonEnabled(true);
		this.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.getKernel().detach(view);
				app.unregisterPopup(Export3dDialog.this);
				app.hideKeyboard();
			}
		});
		setGlassEnabled(true);
	}

	private void buildGui() {
		addStyleName("tableOfValuesDialog");
		FlowPanel contentPanel = new FlowPanel();
		buildButtonPanel(contentPanel);
		add(contentPanel);
		setLabels();
	}

	private void buildButtonPanel(FlowPanel root) {
		root.add(getButtonPanel());
	}

	@Override
	protected void processInput() {
		hide();
		if (onExportButtonPressed != null) {
			onExportButtonPressed.run();
		}
	}

	@Override
	public void setLabels() {
		getCaption()
				.setText(app.getLocalization().getPlain("DownloadAsA",
						extension.toUpperCase()));
		updateButtonLabels("Download");
	}

	@Override
	public void show(Runnable exportAction) {
		this.onExportButtonPressed = exportAction;
		((AppW) app).registerPopup(this);
		super.show();
		centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
	}

}
