package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.common.util.NormalizerMinimal;
import geogebra.html5.main.StringHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InfoDialog;
import geogebra.touch.gui.dialogs.InfoDialog.InfoType;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends HorizontalPanel {
    Kernel kernel;
    TouchApp app;
    TouchModel touchModel;
    TabletGUI tabletGUI;
    TabletHeaderPanel headerPanel;

    InfoDialog infoDialog;

    private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
    private final StandardImageButton newButton = new StandardImageButton(LafIcons.document_new());
    private final StandardImageButton openButton = new StandardImageButton(LafIcons.document_open());
    private final StandardImageButton saveButton = new StandardImageButton(LafIcons.document_save());
    private final StandardImageButton shareButton = new StandardImageButton(LafIcons.document_share());

    /**
     * Generates the Buttons for the left HeaderPanel.
     */
    public TabletHeaderPanelLeft(TabletGUI tabletGUI, TouchApp app, TouchModel touchModel, TabletHeaderPanel headerPanel) {
	this.app = app;
	this.kernel = app.getKernel();

	this.tabletGUI = tabletGUI;
	this.touchModel = touchModel;
	this.headerPanel = headerPanel;

	this.infoDialog = new InfoDialog(this.app, touchModel.getGuiModel(), InfoType.SaveChanges, tabletGUI);

	this.initNewButton();
	this.initOpenButton();
	this.initSaveButton();

	this.add(this.newButton);
	this.add(this.openButton);
	this.add(this.saveButton);

	if (TouchEntryPoint.getLookAndFeel().supportsShare()) {
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
	final Runnable newConstruction = new Runnable() {
	    @Override
	    public void run() {
		TabletHeaderPanelLeft.this.app.getEuclidianView1().setPreview(null);
		TabletHeaderPanelLeft.this.touchModel.resetSelection();
		TabletHeaderPanelLeft.this.touchModel.getGuiModel().closeOptions();
		TabletHeaderPanelLeft.this.kernel.getApplication().getGgbApi().newConstruction();
		TabletHeaderPanelLeft.this.app.setDefaultConstructionTitle();
		TabletHeaderPanelLeft.this.tabletGUI.resetMode();
		TabletHeaderPanelLeft.this.kernel.notifyRepaint();
		TabletHeaderPanelLeft.this.app.setSaved();
	    }
	};

	this.newButton.addDomHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		TabletHeaderPanelLeft.this.infoDialog.setCallback(newConstruction);
		TabletHeaderPanelLeft.this.infoDialog.showIfNeeded(TabletHeaderPanelLeft.this.app);
	    }
	}, ClickEvent.getType());
    }

    private void initOpenButton() {

	final Runnable showOpenDialog = new Runnable() {
	    @Override
	    public void run() {
		TabletHeaderPanelLeft.this.touchModel.getGuiModel().closeOptions();
		TouchEntryPoint.showBrowseGUI();
	    }
	};

	this.openButton.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		event.preventDefault();
		TabletHeaderPanelLeft.this.infoDialog.setCallback(showOpenDialog);
		TabletHeaderPanelLeft.this.infoDialog.showIfNeeded(TabletHeaderPanelLeft.this.app);
	    }
	}, ClickEvent.getType());
    }

    private void initSaveButton() {
	this.saveButton.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		event.preventDefault();
		TabletHeaderPanelLeft.this.touchModel.getGuiModel().closeOptions();

		if (TabletHeaderPanelLeft.this.app.isDefaultFileName()
			&& TabletHeaderPanelLeft.this.app.getConstructionTitle().equals(TabletHeaderPanelLeft.this.tabletGUI.getConstructionTitle())) {
		    TabletHeaderPanelLeft.this.tabletGUI.editTitle();
		} else {
		    TabletHeaderPanelLeft.this.app.getFileManager().saveFile(TabletHeaderPanelLeft.this.app);
		}
	    }
	}, ClickEvent.getType());
	this.enableDisableSave();
    }

    private void initShareButton() {
	this.shareButton.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		TabletHeaderPanelLeft.this.app.getGgbApi().getBase64(new StringHandler() {

		    @Override
		    public void handle(String s) {
			String name = TabletHeaderPanelLeft.this.app.getConstructionTitle();
			if (name != null) {
			    name = NormalizerMinimal.transformStatic(name, false).replaceAll("[^\\w.-_]", "");
			}
			if ("".equals(name)) {
			    name = "construction";
			}
			this.share(s, name);
		    }

		    public native void share(String ggbBase64, String name) /*-{
									    		if(!$wnd.android){
									    		return;
									    		}
									    		$wnd.android.share(ggbBase64, name);
									    		}-*/;
		});
	    }
	}, ClickEvent.getType());

    }

    public void setLabels() {
	this.infoDialog.setLabels();
    }

    /**
     * Sets the title in the {@link TabletHeaderPanel tabletHeader}
     * 
     * @param title
     */
    @Override
    public void setTitle(String title) {
	TouchEntryPoint.getLookAndFeel().setTitle(title);
    }
}