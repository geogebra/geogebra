package geogebra.touch.gui.elements.header;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class WorksheetHeaderPanel extends AuxiliaryHeaderPanel implements
		WorksheetHeader {

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final StandardImageButton editButton = new StandardImageButton(
			LafIcons.document_edit());
	private Material material;
	private final TabletGUI tabletGUI;
	private final TouchApp app;

	public WorksheetHeaderPanel(final AppWeb app, final TabletGUI tabletGUI) {

		super("", app.getLocalization());
		this.tabletGUI = tabletGUI;
		this.app = (TouchApp) app;

		super.backPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				onGoBack();
			}
		}, ClickEvent.getType());

		this.rightPanel.add(this.editButton);
		this.editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onEdit();
			}
		});
	}

	protected void onGoBack() {
		this.tabletGUI.restoreEuclidian(TouchEntryPoint.getWorksheetGUI()
				.getContentPanel());
		TouchEntryPoint.goBack();
	}

	protected void onEdit() {
		if (this.material != null) {
			this.tabletGUI.restoreEuclidian(TouchEntryPoint.getWorksheetGUI()
					.getContentPanel());
			TouchEntryPoint.allowEditing(true);
			this.app.getFileManager().getMaterial(this.material, this.app);
			TouchEntryPoint.showTabletGUI();
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
	}

	@Override
	public void setMaterial(final Material m) {
		this.setText(m.getTitle());
		this.material = m;
	}
}
