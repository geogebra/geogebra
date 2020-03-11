package org.geogebra.web.full.javax.swing;

import org.geogebra.common.euclidian.draw.DrawInlineText;
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
public class InlineTextToolbar extends AriaMenuItem implements ValueChangeHandler<Boolean> {
	private final App app;
	private DrawInlineText drawInlineText;
	private FlowPanel panel;
	private MyToggleButtonW subScriptBtn;
	private MyToggleButtonW superScriptBtn;
	private MyToggleButtonW bulletListBtn;
	private MyToggleButtonW numberedListBtn;

	/**
	 * Constructor of special context menu item holding the
	 * list and sub/superscript toggle buttons
	 * @param drawInlineText the drawable.
	 */
	public InlineTextToolbar(DrawInlineText drawInlineText, App app) {
		super();
		this.drawInlineText = drawInlineText;
		this.app = app;

		createGui();
		setLabels();
	}

	private void createGui() {
		setStyleName("inlineTextToolbar");
		panel = new FlowPanel();
		createSubscriptBtn();
		createSuperscriptBtn();
		createBulletListBtn();
		createNumberedListBtn();
		setWidget(panel);
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

	private MyToggleButtonW createButton(SVGResource resource) {
		MyToggleButtonW button = new MyToggleButtonW(new NoDragImage(resource, 24));
		button.addValueChangeHandler(this);
		return button;
	}

	private String getScriptFormat() {
		return drawInlineText.getFormat("script", "normal");
	}

	private String getListStyle() {
		return drawInlineText.getListStyle();
	}

	@Override
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
			drawInlineText.switchListTo("bullet");
		} else if (numberedListBtn.equals(event.getSource())) {
			drawInlineText.switchListTo("number");
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
		drawInlineText.format("script", value ? type : "none");
		app.storeUndoInfo();
	}

	private void setLabels() {
		subScriptBtn.setToolTipText(app.getLocalization().getMenu("Subscript"));
		superScriptBtn.setToolTipText(app.getLocalization().getMenu("Superscript"));
		bulletListBtn.setToolTipText(app.getLocalization().getMenu("bulletList"));
		numberedListBtn.setToolTipText(app.getLocalization().getMenu("numberedList"));
	}
}
