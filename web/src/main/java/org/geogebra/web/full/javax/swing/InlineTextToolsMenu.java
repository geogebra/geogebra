package org.geogebra.web.full.javax.swing;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
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
public class InlineTextToolsMenu extends AriaMenuItem implements ValueChangeHandler<Boolean> {

	private final App app;
	private final Localization localization;

	private DrawInlineText drawInlineText;

	private FlowPanel panel;
	private MyToggleButtonW subScript;
	private MyToggleButtonW superScript;

	/**
	 * Constructor
	 * @param drawInlineText the drawable.
	 */
	public InlineTextToolsMenu(DrawInlineText drawInlineText, App app) {
		super();
		this.drawInlineText = drawInlineText;
		this.app = app;

		localization = app.getLocalization();

		addStyleName("mowMenuToolbar");
		panel = new FlowPanel();
		panel.addStyleName("content");
		createGui();
		setLabels();
	}

	private void createGui() {
		createSubscript();
		createSuperscript();
		setWidget(panel);
		updateState();
	}

	private void createSubscript() {
		subScript = createButton(MaterialDesignResources.INSTANCE.format_subscript());
		add(subScript);
	}

	private void createSuperscript() {
		superScript = createButton(MaterialDesignResources.INSTANCE.format_superscript());
		add(superScript);
	}

	private void updateState() {
		subScript.setSelected("sub".equals(getScriptFormat()));
		superScript.setSelected("super".equals(getScriptFormat()));
	}

	private MyToggleButtonW createButton(SVGResource resource) {
		MyToggleButtonW button = new MyToggleButtonW(new NoDragImage(resource, 24));
		button.addValueChangeHandler(this);
		button.addStyleName("mowToolButton");
		return button;
	}

	private String getScriptFormat() {
		return drawInlineText.getFormat("script", "normal");
	}

	@Override
	public void add(Widget widget) {
		panel.add(widget);
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (event.getSource() == subScript) {
			setSubscript(event.getValue());
		} else if (event.getSource() == superScript) {
			setSuperscript(event.getValue());
		}

		updateState();
	}

	private void setSubscript(Boolean value) {
		superScript.setSelected(false);
		formatScript("sub", value);
	}

	private void setSuperscript(Boolean value) {
		subScript.setSelected(false);
		formatScript("super", value);
	}

	private void formatScript(String type, Boolean value) {
		drawInlineText.format("script", value ? type : "none");
		app.storeUndoInfo();
	}

	private void setLabels() {
		subScript.setToolTipText(localization.getMenu("Subscript"));
		superScript.setToolTipText(localization.getMenu("Superscript"));
	}
}
