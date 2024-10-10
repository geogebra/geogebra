package org.geogebra.web.full.javax.swing;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.main.App;
import org.geogebra.common.main.undo.UpdateContentActionStore;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Menu item that acts like a toolbar.
 */
public class InlineTextToolbar implements FastClickHandler {
	private final App app;
	private List<HasTextFormat> formatters;
	private FlowPanel panel;
	private ToggleButton subScriptBtn;
	private ToggleButton superScriptBtn;
	private ToggleButton bulletListBtn;
	private ToggleButton numberedListBtn;

	/**
	 * Constructor of special context menu item holding the
	 * list and sub/superscript toggle buttons
	 * @param formatters the formatters.
	 *
	 */
	public InlineTextToolbar(List<HasTextFormat> formatters, App app) {
		this.formatters = formatters;
		this.app = app;

		createGui();
		setTooltips();
	}

	/**
	 * Creates the toolbar gui
	 */
	protected void createGui() {
		panel = new FlowPanel();
		createSubscriptBtn();
		createSuperscriptBtn();
		createBulletListBtn();
		createNumberedListBtn();
		updateState();
	}

	private void createSubscriptBtn() {
		subScriptBtn = createButton(MaterialDesignResources.INSTANCE.format_subscript());
		add(subScriptBtn);
	}

	private void createSuperscriptBtn() {
		superScriptBtn = createButton(MaterialDesignResources.INSTANCE.format_superscript());
		add(superScriptBtn);
	}

	private void createBulletListBtn() {
		bulletListBtn = createButton(MaterialDesignResources.INSTANCE.bulletList());
		add(bulletListBtn);
	}

	private void createNumberedListBtn() {
		numberedListBtn = createButton(MaterialDesignResources.INSTANCE.numberedList());
		add(numberedListBtn);
	}

	private void updateState() {
		subScriptBtn.setSelected("sub".equals(getScriptFormat()));
		superScriptBtn.setSelected("super".equals(getScriptFormat()));
		bulletListBtn.setSelected("bullet".equals(getListStyle()));
		numberedListBtn.setSelected("number".equals(getListStyle()));
	}

	private ToggleButton createButton(SVGResource resource) {
		ToggleButton button = new ToggleButton(resource);
		button.addFastClickHandler(this);
		return button;
	}

	protected String getScriptFormat() {
		if (formatters.isEmpty()) {
			return "";
		}

		String format = formatters.get(0).getFormat("script", "normal");
		if (formatters.size() == 1) {
			return format;
		}

		for (HasTextFormat formatter : formatters) {
			if (!format.equals(formatter.getFormat("script", "normal"))) {
				return "";
			}
		}

		return format;
	}

	protected String getListStyle() {
		if (formatters.isEmpty()) {
			return "";
		}

		String listStyle = getListStyle(formatters.get(0));
		if (formatters.size() == 1) {
			return listStyle;
		}

		for (HasTextFormat formatter : formatters) {
			if (!listStyle.equals(getListStyle(formatter))) {
				return "";
			}
		}
		return listStyle;
	}

	private String getListStyle(HasTextFormat formatter) {
		return formatter.getListStyle() != null
				? formatter.getListStyle()
				: "";
	}

	public void add(Widget widget) {
		panel.add(widget);
	}

	@Override
	public void onClick(Widget source) {
		if (subScriptBtn == source) {
			setSubscript(subScriptBtn.isSelected());
		} else if (superScriptBtn == source) {
			setSuperscript(superScriptBtn.isSelected());
		} else if (bulletListBtn == source) {
			switchListTo("bullet");
		} else if (numberedListBtn == source) {
			switchListTo("number");
		}

		updateState();
	}

	private void setSubscript(Boolean value) {
		formatScript("sub", value);
	}

	private void setSuperscript(Boolean value) {
		formatScript("super", value);
	}

	private void formatScript(String type, Boolean value) {
		ArrayList<GeoInline> geosToStore = new ArrayList();
		for (HasTextFormat formatter : formatters) {
			geosToStore.add(formatter.getInline());
		}

		UpdateContentActionStore store = new UpdateContentActionStore(geosToStore);
		for (HasTextFormat formatter : formatters) {
			formatter.format("script", value ? type : "none");
		}
		if (store.needUndo()) {
			store.storeUndo();
		}
	}

	private void switchListTo(String listType) {
		for (HasTextFormat formatter : formatters) {
			formatter.switchListTo(listType);
		}
	}

	/**
	 * Sets the tooltips
	 */
	protected void setTooltips() {
		subScriptBtn.setTitle(app.getLocalization().getMenu("Subscript"));
		superScriptBtn.setTitle(app.getLocalization().getMenu("Superscript"));
		bulletListBtn.setTitle(app.getLocalization().getMenu("bulletList"));
		numberedListBtn.setTitle(app.getLocalization().getMenu("numberedList"));
	}

	/**
	 *
	 * @return the toolbar as a menu item
	 */
	public FlowPanel getItem() {
		return panel;
	}
}