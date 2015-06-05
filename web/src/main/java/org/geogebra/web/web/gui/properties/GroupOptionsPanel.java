package org.geogebra.web.web.gui.properties;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.dialog.options.model.GroupModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.main.Localization;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GroupOptionsPanel implements IOptionPanel {
	private List<IOptionPanel> panels;
	private String titleId;
	private FlowPanel mainWidget;
	private FlowPanel group;
	private Label titleLabel;
	private Localization loc;
	private GroupModel model;

	public GroupOptionsPanel(final String title, Localization loc,
			GroupModel model) {
		super();
		this.titleId = title;
		this.loc = loc;
		titleLabel = new Label("misc");
		mainWidget = new FlowPanel();
		group = new FlowPanel();
		mainWidget.add(titleLabel);
		mainWidget.add(group);
		this.model = model;
		titleLabel.setStyleName("panelTitle");
		group.setStyleName("optionsPanelIndent");
		
		panels = new ArrayList<IOptionPanel>();
		setTitleLabel();
	}

	public void add(IOptionPanel panel) {
		group.add(panel.getWidget());
		panels.add(panel);
	}

	public void setLabels() {
		setTitleLabel();
		for (IOptionPanel panel: panels) {
			panel.setLabels();
		}

	}

	protected void setTitleLabel() {
	    titleLabel.setText(loc.getMenu(titleId));
    }

	public Object updatePanel(Object[] geos) {
		boolean result = false;
		for (IOptionPanel panel: panels) {
			result = panel.updatePanel(geos) != null || result;
		}
		return result ? this : null;
	}

	public Widget getWidget() {
		return mainWidget;
	}

	public void setWidget(Widget widget) {
	}

	public OptionsModel getModel() {
		return model;
	}

}
