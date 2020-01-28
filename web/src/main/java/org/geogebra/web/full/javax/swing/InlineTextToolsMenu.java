package org.geogebra.web.full.javax.swing;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.InlineTextFormatter;
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
public class InlineTextToolsMenu extends AriaMenuItem implements ValueChangeHandler<Boolean>,
		SetLabels {
	private final App app;
	private final Localization localization;
	private FlowPanel panel;
	private MyToggleButtonW subScript;
	private MyToggleButtonW superScript;
	private GeoInlineText geoInlineText;
	private InlineTextFormatter formatter;
	private DrawInlineText drawInlineText;

	/**
	 * Constructor
	 * @param drawInlineText the drawable.
	 */
	public InlineTextToolsMenu(DrawInlineText drawInlineText) {
		super();
		this.drawInlineText = drawInlineText;
		this.geoInlineText = (GeoInlineText) drawInlineText.getGeoElement();
		app = geoInlineText.getKernel().getApplication();
		localization = app.getLocalization();
		formatter = new InlineTextFormatter(app);
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
	}

	private void createSubscript() {
		subScript = createButton(MaterialDesignResources.INSTANCE.format_subscript());
		subScript.setSelected("sub".equals(getScriptFormat()));
		add(subScript);
	}

	private String getScriptFormat() {
		return drawInlineText.getFormat("script", "normal");
	}

	private void createSuperscript() {
		superScript = createButton(MaterialDesignResources.INSTANCE.format_superscript());
		superScript.setSelected("super".equals(getScriptFormat()));
		add(superScript);
	}

	private MyToggleButtonW createButton(SVGResource resource) {
		MyToggleButtonW button = new MyToggleButtonW(new NoDragImage(resource, 24));
		button.addValueChangeHandler(this);
		return button;
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
	}

	private void setSubscript(Boolean value) {
		superScript.setSelected(false);
		formatScript("sub", value);
	}

	protected void formatScript(String type, Boolean value) {
		formatter.formatInlineText(geoInlineText, "script", value ? type : "none");
		app.storeUndoInfo();
	}

	private void setSuperscript(Boolean value) {
		subScript.setSelected(false);
		formatScript("super", value);
	}

	@Override
	public void setLabels() {
		subScript.setToolTipText(localization.getMenuDefault("mow.subscript", "Subscript"));
		superScript.setToolTipText(localization.getMenuDefault("mow.superscript", "Superscript"));
	}
}
