package geogebra.touch.gui.elements.customkeys;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class CustomKeysPanel extends FlowPanel {
	public enum CustomKey {
		plus("+"), minus("\u2212"), times("\u2217", "*"), divide("/"), squared(
				"\u00B2"), power("^"), degree("\u00B0"), pi("\u03C0"), leftpar(
				"("), rightpar(")"), leftbracket("["), rightbracket("]"), leftbrace(
				"{"), rightbrace("}"), equals("=");

		String s;
		private String replace;

		CustomKey(final String s) {
			this.s = s;
			this.replace = "";
		}

		CustomKey(final String s, final String replace) {
			this.s = s;
			this.replace = replace;
		}

		public String getReplace() {
			return this.replace;
		}

		@Override
		public String toString() {
			return this.s;
		}
	}

	private final HorizontalPanel buttonContainer = new HorizontalPanel();

	private final List<CustomKeyListener> listeners;

	public CustomKeysPanel() {
		super();

		this.listeners = new ArrayList<CustomKeyListener>();
		this.setStyleName("customKeyPanel");

		for (final CustomKey k : CustomKey.values()) {
			final Button b = new Button();
			b.setText(k.toString());

			b.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					CustomKeysPanel.this.fireClickEvent(k);
				}
			}, ClickEvent.getType());

			// Specific styles for last button
			final CustomKey lastKey = CustomKey.values()[CustomKey.values().length - 1];
			if (k == lastKey) {
				b.setStyleName("last");
			}

			this.buttonContainer.add(b);
		}

		this.add(this.buttonContainer);
	}

	public void addCustomKeyListener(final CustomKeyListener l) {
		if (!this.listeners.contains(l)) {
			this.listeners.add(l);
		}
	}

	void fireClickEvent(final CustomKey key) {
		for (final CustomKeyListener c : this.listeners) {
			c.onCustomKeyPressed(key);
		}
	}
}