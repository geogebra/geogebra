package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Adds a panel with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public abstract class GCheckMarkPanel {

	private FlowPanel itemPanel;
	private boolean checked;
	private String text;
	private Image checkImg;
	private ScheduledCommand cmd;

	/**
	 * @param text
	 *            Title
	 * @param checkUrl
	 *            image of check mark
	 * @param checked
	 *            initial value.
	 * @param cmd
	 *            The command to run.
	 */
	public GCheckMarkPanel(String text, SVGResource checkUrl, boolean checked,
			final ScheduledCommand cmd) {
		this.setText(text);
		this.setCmd(cmd);
		checkImg = new NoDragImage(checkUrl, 24, 24);
		checkImg.addStyleName("checkImg");
		itemPanel = new FlowPanel();
		itemPanel.addStyleName("checkMarkMenuItem");
		createContents();
		setChecked(checked);
	}

	/**
	 * Creates the contents goes along with the check mark.
	 */
	protected abstract void createContents();

	/**
	 * Updates the contents goes along with the check mark.
	 */
	protected abstract void updateContents();

	/**
	 * Sets the item checked/unchecked.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setChecked(boolean value) {
		checked = value;
		updateGUI();
		updateContents();
	}

	private void updateGUI() {
		if (itemPanel == null) {
			return;
		}

		itemPanel.clear();
		HTML html = new HTML(getText());
		itemPanel.add(html);
		if (checked) {
			itemPanel.add(checkImg);
		}
	}

	/**
	 * 
	 * @return true if item is checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @return checkbox action
	 */
	public ScheduledCommand getCmd() {
		return cmd;
	}

	/**
	 * @param cmd
	 *            checkbox action
	 */
	public void setCmd(ScheduledCommand cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return checkbox label
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            checkbox label
	 */
	public void setText(String text) {
		this.text = text;
		updateGUI();
	}

	/**
	 * @return panel
	 */
	public FlowPanel getPanel() {
		return itemPanel;
	}

	/**
	 * @return HTML content
	 */
	public String getHTML() {
		return itemPanel.toString();
	}
}
