package geogebra.touch.gui.elements.header;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;

public class WorksheetHeaderPanel extends AuxiliaryHeaderPanel implements
		WorksheetHeader {

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final FastButton editButton = new StandardButton(
			LafIcons.document_edit());
	private Material material;
	private final TabletGUI tabletGUI;
	private final TouchApp app;

	public WorksheetHeaderPanel(final AppWeb app, final TabletGUI tabletGUI) {

		super(app.getLocalization());
		this.tabletGUI = tabletGUI;
		this.app = (TouchApp) app;

		super.backButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onGoBack();
			}
		});

		this.rightPanel.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onEdit();
			}
		});
	}

	void onGoBack() {
		this.tabletGUI.restoreEuclidian(TouchEntryPoint.getWorksheetGUI()
				.getContentPanel());
		TouchEntryPoint.goBack();
	}

	void onEdit() {
		if (this.material != null) {
			this.tabletGUI.restoreEuclidian(TouchEntryPoint.getWorksheetGUI()
					.getContentPanel());
			TouchEntryPoint.allowEditing(true);
			this.app.getGgbApi().startEditing();
			//this.app.getFileManager().getMaterial(this.material, this.app);
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
