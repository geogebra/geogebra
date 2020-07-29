package org.geogebra.web.full.javax.swing;

import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Menu item that acts like a toolbar.
 *
 * @author laszlo
 */
public class InlineTextToolbar implements ValueChangeHandler<Boolean> {

	private AriaMenuItem item;
	private final App app;
	private List<HasTextFormat> formatters;
	private FlowPanel panel;
	private MyToggleButtonW subScriptBtn;
	private MyToggleButtonW superScriptBtn;
	private MyToggleButtonW bulletListBtn;
	private MyToggleButtonW numberedListBtn;

	/**
	 * Constructor of special context menu item holding the
	 * list and sub/superscript toggle buttons
	 * @param formatters the formatters.
	 *
	 */
	public InlineTextToolbar(List<HasTextFormat> formatters, AriaMenuItem item, App app) {
		this.formatters = formatters;
		this.item = item;
		this.app = app;

		createGui();
		setTooltips();
	}

	/**
	 * Creates the toolbar gui
	 */
	protected void createGui() {
		item.setStyleName("inlineTextToolbar");
		panel = new FlowPanel();
		createSubscriptBtn();
		createSuperscriptBtn();
		createBulletListBtn();
		createNumberedListBtn();
		item.setWidget(panel);
		updateState();
	}

	/**
	 * Set item content as text
	 * @param text to set
	 */
	protected void setContent(String text) {
		item.setContent(text, false);
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

	private MyToggleButtonW createButton(SVGResource resource) {
		MyToggleButtonW button = new MyToggleButtonW(new NoDragImage(resource, 24));
		button.addValueChangeHandler(this);
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
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (subScriptBtn.equals(event.getSource())) {
			setSubscript(event.getValue());
		} else if (superScriptBtn.equals(event.getSource())) {
			setSuperscript(event.getValue());
		} else if (bulletListBtn.equals(event.getSource())) {
			switchListTo("bullet");
		} else if (numberedListBtn.equals(event.getSource())) {
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
		for (HasTextFormat formatter : formatters) {
			formatter.format("script", value ? type : "none");
		}
		app.storeUndoInfo();
	}

	private void switchListTo(String listType) {
		for (HasTextFormat formatter : formatters) {
			formatter.switchListTo(listType);
		}
		app.storeUndoInfo();
	}

	/**
	 * Sets the tooltips
	 */
	protected void setTooltips() {
		subScriptBtn.setToolTipText(app.getLocalization().getMenu("Subscript"));
		superScriptBtn.setToolTipText(app.getLocalization().getMenu("Superscript"));
		bulletListBtn.setToolTipText(app.getLocalization().getMenu("bulletList"));
		numberedListBtn.setToolTipText(app.getLocalization().getMenu("numberedList"));
	}

	/**
	 *
	 * @return the toolbar as a menu item
	 */
	public AriaMenuItem getItem() {
		return item;
	}
}