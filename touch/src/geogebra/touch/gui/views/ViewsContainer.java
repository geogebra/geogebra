package geogebra.touch.gui.views;

import geogebra.html5.gui.ResizeListener;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * 
 * A {@link ScrollPanel} containing all existing views (algebra, graphics, worksheets, options)
 *
 */
public class ViewsContainer extends ScrollPanel implements ResizeListener {
	
	public enum View {
		Algebra(0), Graphics(1), Worksheets(2), Options(3);
		
		private int index;
		
		private View(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return this.index;
		}
	}
		
	private HorizontalPanel content;
	private View currentView;
	private int scrollOffset;
	
	public ViewsContainer() {
		this.setPixelSize(Window.getClientWidth(),  TouchEntryPoint.getLookAndFeel().getCanvasHeight());
		this.setStyleName("viewContainer");
		this.scrollOffset = Window.getClientWidth();
		
		this.content = new HorizontalPanel();
		this.add(this.content);
		
		//$(this.content).css(CSS.LEFT.with(Length.px(0)));
	}

	/**
	 * Adds a new view
	 * @param view
	 */
	public void addView(FlowPanel view) {
		this.content.add(view);
	}
	
	public void addView(EuclidianViewPanel view) {
		this.content.add(view);
	}
	
	public void setCurrentView(View view) {
		this.currentView = view;
	}
	
	public View getCurrentView() {
		return this.currentView;
	}

	/**
	 * scroll to the given {@link View view}
	 * @param view {@link View}
	 */
	public void scrollTo(View view) {
		animateScroll(view.getIndex()*this.scrollOffset);
		this.currentView = view;
	}

	private boolean toggle = false;
	
	/**
	 * uses gwt-query to animate scrolling
	 * @param to scrollPosition in pixel
	 */
	private void animateScroll(int to) {
		//$(this.content).animate("{left:'-" + to + "px'}", 300, EasingCurve.swing);
	}
	
	public native void abc(String s) /*-{
	console.log(s);
}-*/;

	private View getView(int index) {
		for (View view : View.values()) {
			if (view.getIndex() == index) {
				return view;
			}
		}
		return null;
	}
	
	private void swipe(boolean toLeft) {
		int switchTo = toLeft ? 1 : -1;
		View view = getView(this.currentView.getIndex() + switchTo);
		if (view != null) {
			scrollTo(view);
		}
	}
	
	@Override
	public void scrollToLeft() {
		swipe(true);
	}
	
	@Override
	public void scrollToRight() {
		swipe(false);
	}
	
	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(),  Window.getClientHeight()-43);
		this.scrollOffset = Window.getClientWidth();
	}
}
