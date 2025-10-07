package org.geogebra.web.full.gui.properties.ui.panel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class IconButtonPanel extends FlowPanel implements SetLabels {
	private final AppW appW;
	private Label label;
	private final String labelKey;
	private List<IconButton> iconButtonList;

	/**
	 * Created an icon button panel
	 * @param appW application
	 * @param property {@link IconsEnumeratedProperty}
	 */
	public IconButtonPanel(AppW appW, IconsEnumeratedProperty<?> property) {
		this.appW = appW;
		labelKey = property.getRawName();
		buildGUI(property);
	}

	private void buildGUI(IconsEnumeratedProperty<?> property) {
		addStyleName("iconButtonPanel");
		label = new Label(property.getName());
		add(label);

		FlowPanel iconPanel = new FlowPanel();
		iconPanel.addStyleName("iconPanel");
		PropertyResource[] icons = property.getValueIcons();
		String[] labels = property.getLabels();
		int idx = 0;
		int selectedIdx = property.getIndex();
		iconButtonList = new ArrayList<>();
		for (PropertyResource icon: icons) {
			String label = labels != null && labels[idx] != null ? labels[idx] : "";
			IconButton btn = new IconButton(appW, null,
					new ImageIconSpec(PropertiesIconAdapter.getIcon(icon)), label);
			btn.setActive(selectedIdx == idx);
			iconPanel.add(btn);
			iconButtonList.add(btn);
			final int index = idx;
			btn.addClickHandler(appW.getGlobalHandlers(),
					(w) -> {
						property.setIndex(index);
						iconButtonList.forEach(iconButton -> iconButton.setActive(false));
						btn.setActive(true);
					});
			idx++;
		}

		add(iconPanel);
	}

	@Override
	public void setLabels() {
		label.setText(appW.getLocalization().getMenu(labelKey));
		iconButtonList.forEach(IconButton::setLabels);
	}
}
