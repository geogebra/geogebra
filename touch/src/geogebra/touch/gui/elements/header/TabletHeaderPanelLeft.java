package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.common.util.NormalizerMinimal;
import geogebra.html5.main.StringHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.dialogs.InfoDialog;
import geogebra.touch.gui.dialogs.InfoDialog.InfoType;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends HorizontalPanel {
	private final Kernel kernel;
	private final TouchApp app;
	private final TouchModel touchModel;
	private final TabletGUI tabletGUI;
	private final TabletHeaderPanel headerPanel;
	private final InfoDialog infoDialog;

	private Runnable newConstruction;
	private Runnable showOpenDialog;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final FastButton newButton = new StandardButton(
			LafIcons.document_new());
	private final FastButton openButton = new StandardButton(
			LafIcons.document_open());
	private final FastButton saveButton = new StandardButton(
			LafIcons.document_save());
	private final FastButton shareButton = new StandardButton(
			LafIcons.document_share());

	/**
	 * Generates the Buttons for the left HeaderPanel.
	 */
	public TabletHeaderPanelLeft(final TouchApp app,
			final TouchModel touchModel, final TabletHeaderPanel headerPanel) {
		this.app = app;
		this.kernel = app.getKernel();
		this.tabletGUI = (TabletGUI) app.getTouchGui();
		this.touchModel = touchModel;
		this.headerPanel = headerPanel;

		this.infoDialog = new InfoDialog(this.app, touchModel.getGuiModel(),
				InfoType.SaveChanges);

		this.initNewButton();
		this.initOpenButton();
		this.initSaveButton();

		this.add(this.newButton);
		this.add(this.openButton);
		this.add(this.saveButton);

		if (TouchEntryPoint.getLookAndFeel().isShareSupported()) {
			this.initShareButton();
			this.add(this.shareButton);
		}
	}

	protected void enableDisableSave() {
		if (this.app.isSaved()) {
			this.saveButton.addStyleName("disabled");
			this.saveButton.setEnabled(false);
		} else {
			this.saveButton.removeStyleName("disabled");
			this.saveButton.setEnabled(true);
		}
	}

	private void initNewButton() {
		this.newConstruction = new Runnable() {
			@Override
			public void run() {
				runNew();
			}
		};

		this.newButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onNew();
			}
		});
	}

	protected void onNew() {
		this.infoDialog.setCallback(this.newConstruction);
		this.infoDialog.showIfNeeded(this.app);
	}

	protected void runNew() {
		this.app.getEuclidianView1().setPreview(null);
		this.touchModel.resetSelection();
		this.touchModel.getGuiModel().closeOptions();
		this.kernel.getApplication().getGgbApi().newConstruction();
		this.app.setDefaultConstructionTitle();
		this.tabletGUI.resetMode();
		this.app.setSaved();
		this.tabletGUI.updateViewSizes();
	}

	private void initOpenButton() {

		this.showOpenDialog = new Runnable() {
			@Override
			public void run() {
				runOpen();
			}
		};

		this.openButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onOpen();
			}
		});
	}

	protected void runOpen() {
		this.touchModel.getGuiModel().closeOptions();
		TouchEntryPoint.showBrowseGUI();
	}

	protected void onOpen() {
		this.infoDialog.setCallback(this.showOpenDialog);
		this.infoDialog.showIfNeeded(this.app);
	}

	private void initSaveButton() {
		this.saveButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onSave();
			}
		});
		this.enableDisableSave();
	}

	protected void onSave() {
		this.touchModel.getGuiModel().closeOptions();

		if (this.app.isDefaultFileName()
				&& this.app.getConstructionTitle().equals(
						this.tabletGUI.getConstructionTitle())) {
			this.tabletGUI.editTitle();
		} else {
			this.app.getFileManager().saveFile(this.app);
		}
	}

	private void initShareButton() {
		this.shareButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onShare();
			}
		});
	}

	protected void onShare() {
		this.app.getGgbApi().getBase64(new StringHandler() {

			@Override
			public void handle(final String s) {
				handleString(s);
			}
		});
	}

	protected void handleString(final String s) {
		String name = this.app.getConstructionTitle();
		if (name != null) {
			name = NormalizerMinimal.transformStatic(name, false).replaceAll(
					"[^\\w.-_]", "");
		}
		if ("".equals(name)) {
			name = "construction";
		}
		this.share(s, name);
	}

	public native void share(String ggbBase64, String name) /*-{
		if (!$wnd.android) {
			return;
		}
		$wnd.android.share(ggbBase64, name);
	}-*/;

	public void setLabels() {
		this.infoDialog.setLabels();
	}
}