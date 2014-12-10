package geogebra.phone.gui.container.panel.simple;

import static com.google.gwt.query.client.GQuery.$;
import geogebra.phone.gui.container.panel.Panel;
import geogebra.phone.gui.view.StyleBarPanel;
import geogebra.phone.gui.view.View;
import geogebra.web.gui.laf.GLookAndFeel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.query.client.css.CSS;
import com.google.gwt.query.client.css.Length;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.EasingCurve;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class SwipePanel extends SimplePanel implements Panel {

	private HorizontalPanel content;
	private int scrollOffset;

	private List<View> views;

	private View activeView;

	public SwipePanel() {
		setStyleName("viewContainer");
		scrollOffset = Window.getClientWidth();
		content = new HorizontalPanel();
		add(content);

		$(content).css(CSS.LEFT.with(Length.px(0)));

		views = new ArrayList<View>();
	}

	public void addView(View view) {
		views.add(view);
		content.add(createViewPanelWithStylebar(view));
		if (views.size() == 1) {
			activeView = view;
		}
	}

	public void removeView(View view) {
		content.remove(view.getViewPanel());
		views.remove(view);
		if (views.size() == 0) {
			activeView = null;
		}
	}

	public void showView(View view) {
		int viewIndex = getViewIndex(view);
		if (viewIndex < 0) {
			return;
		}
		animateScroll(viewIndex * this.scrollOffset);
		updateSize();
	}

	private void animateScroll(int to) {
		// FIXME
		$(this.content).animate("{left:'-" + to + "px'}", 300,
				EasingCurve.swing);
	}

	private int getViewIndex(View view) {
		for (int i = 0; i < views.size(); i++) {
			if (views.get(i) == view) {
				return i;
			}
		}
		return -1;
	}

	public void onResize() {
		this.scrollOffset = Window.getClientWidth();
		updateSize();
		for (int i = 0; i < views.size(); i++) {
			views.get(i).getViewPanel().onResize();
		}
	}

	private void updateSize() {
		int height = Window.getClientHeight()
				- GLookAndFeel.PHONE_HEADER_HEIGHT;
		int width = Window.getClientWidth();
		Style style = getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setPropertyPx("width", width);
		style.setPropertyPx("height", height);
		style.setProperty("zoom", "1");
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.HIDDEN);
	}

	private com.google.gwt.user.client.ui.Panel createViewPanelWithStylebar(View view) {
		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.add(view.getViewPanel());
		StyleBarPanel styleBarPanel = view.getStyleBarPanel();
		if (styleBarPanel != null) {
			// TODO add button to open close stylebar
			// TODO specify location of the stylebar
			absolutePanel.add(styleBarPanel);
		}
		return absolutePanel;
	}
}
