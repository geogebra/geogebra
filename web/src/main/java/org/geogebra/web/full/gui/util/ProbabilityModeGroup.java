package org.geogebra.web.full.gui.util;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Group of toggles to switch between probability modes
 */
public class ProbabilityModeGroup extends FlowPanel implements SetLabels {
	private final Map<Integer, MyToggleButtonW> buttons;
	private final Map<Integer, String> tooltips;
	private final Localization loc;

	/**
	 *
	 * @param loc Localization
	 */
	public ProbabilityModeGroup(Localization loc) {
		super();
		this.loc = loc;
		buttons = new HashMap<>();
		tooltips = new HashMap<>();
		addStyleName("intervalPanel");
	}

	/**
	 * Add a toggle button to the group.
	 *
	 * @param mode probability mode.
	 * @param resource for the button icon.
	 * @param tooltip to show on hover.
	 */
	public void add(Integer mode, SVGResource resource, String tooltip) {
		MyToggleButtonW button = new MyToggleButtonW(resource);
		buttons.put(mode, button);
		tooltips.put(mode, tooltip);
		add(button);
	}

	/**
	 * Add a ValueChangeHandler to the whole group.
	 *
	 * @param handler to add.
	 */
	public void addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		buttons.values().forEach(button -> button.addValueChangeHandler(handler));
	}

	@Override
	public void setLabels() {
		buttons.forEach((k, v) -> v.setToolTipText(loc.getMenu(tooltips.get(k))));
	}

	/**
	 * End group after last button is added.
	 */
	public void endGroup() {
		addStyleName("groupEnd");
	}

	/**
	 * If source in the group, toggle only
	 * the corresponding button.
	 * @param source to check.
	 * @return if button was toggled or not.
	 */
	public boolean handle(Object source) {
		if (!(source instanceof  MyToggleButtonW)) {
			return false;
		}
		MyToggleButtonW sourceButton = (MyToggleButtonW) source;
		buttons.values().forEach(button -> button.setValue(button == sourceButton));
		return buttons.containsValue(sourceButton);
	}

	/**
	 *
	 * @param mode to set as toggled in the group
	 */
	public void setMode(int mode) {
		buttons.forEach((k, v) -> v.setValue(k == mode));

	}

	/**
	 *
	 * @return the mode is currently toggled,
	 * -1 for none.
	 */
	public int getValue() {
		for (Map.Entry<Integer, MyToggleButtonW> entry: buttons.entrySet()) {
			if (entry.getValue().isSelected()) {
				return entry.getKey();
			}
		}
		return -1;
	}
}