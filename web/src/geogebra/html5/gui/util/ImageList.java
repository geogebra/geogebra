package geogebra.html5.gui.util;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageList extends FocusWidget implements HasChangeHandlers, HasValue<Integer>, MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	public class UnorderedListWidget extends ComplexPanel
	{
		private static final int SELECTED_NONE = -1;
		private int selectedIndex;

		public UnorderedListWidget()
		{
			setElement(Document.get().createULElement());
			selectedIndex = SELECTED_NONE;
		}

		public void setId(String id)
		{
			// Set an attribute common to all tags
			getElement().setId(id);
		}

		public void setClassName(String className)
		{
			// Set an attribute common to all tags
			getElement().setClassName(className);
		}

		public void setDir(String dir)
		{
			// Set an attribute specific to this tag
			((UListElement) getElement().cast()).setDir(dir);
		}

		@Override
		public void add(Widget w)
		{
			// ComplexPanel requires the two-arg add() method
			super.add(wrapWidget(w), getElement());
		}

		private LiPanel wrapWidget(Widget w) {
			LiPanel li = new LiPanel();
			li.add(w);
			return li;
		}

		@Override
		public void clear() {
			try {
				// doLogicalClear();
			} finally {
				// Remove all existing child nodes.
				Node child = getElement().getFirstChild();
				while (child != null) {
					getElement().removeChild(child);
					child = getElement().getFirstChild();
				}
			}
		}

		public void insert(Widget w, int beforeIndex) {
			insert(wrapWidget(w), getElement(), beforeIndex, true);
		}
		
		public void setSelectedIndex(int index) {
			if (index > getWidgetCount()) {
				return;
			}
			
			if (selectedIndex != SELECTED_NONE) {
				getWidget(selectedIndex).getElement().setClassName("");
			}
			selectedIndex = index;
			
			getWidget(selectedIndex).getElement().setClassName("selected");
			
		}
		
		public int getSelectedIndex() {
			return selectedIndex;
		}
		
	}
	private UnorderedListWidget ul;
	public ImageList() {
		ul =  new UnorderedListWidget();
		ul.setId("imageList");
		setElement(ul.getElement());
		addMouseDownHandler(this);
		addMouseUpHandler(this);
	}

	public void setIcons(List<String> urls) { 

		for (String url: urls) {
			Image img = new Image(url);
			ul.add(img);
		}
	}
	
	public void setSelectedIndex(int index) {
		ul.setSelectedIndex(index);
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Integer> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub

	}

	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub

	}

	public void onMouseDown(MouseDownEvent event) {
		int x = event.getClientX();
		int y = event.getClientY();
		
		for(int i = 0; i < ul.getWidgetCount(); i++) {
			Widget w = ul.getWidget(i);
			int left = w.getAbsoluteLeft();
			int top = w.getAbsoluteTop();
			if (x > left && x < left + w.getOffsetWidth() &&
					y > top && y < top + w.getOffsetHeight()) {
				ul.setSelectedIndex(i);
				break;
			}
		}
	}

	public Integer getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(Integer value) {
		// TODO Auto-generated method stub

	}

	public void setValue(Integer value, boolean fireEvents) {
		// TODO Auto-generated method stub

	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}
	private static class LiPanel extends ComplexPanel implements InsertPanel {

		protected LiPanel() {
			setElement(Document.get().createLIElement());
		}

		@Override
		public void add(Widget w) {
			add(w, getElement());
		}

		@Override
		public void clear() {
			try {
				// doLogicalClear();
			} finally {
				// Remove all existing child nodes.
				Node child = getElement().getFirstChild();
				while (child != null) {
					getElement().removeChild(child);
					child = getElement().getFirstChild();
				}	// TODO Auto-generated method stub
				
			}
		}

		public void insert(Widget w, int beforeIndex) {
			insert(w, getElement(), beforeIndex, true);
		}
	}
}
