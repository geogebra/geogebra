package geogebra.html5.gui.view.algebra;

import geogebra.common.main.SelectionManager;
import geogebra.html5.gui.view.algebra.Marble.GeoContainer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;

public class GroupHeader extends FlowPanel{
	
	private InlineLabelTreeItem il;
	public GroupHeader(SelectionManager selection, TreeItem parent, String strlab, SafeUri showUrl,SafeUri hiddenUrl) {
		this.setWidth("100%");
		
		this.setStyleName("elemHeading");
		
		this.add(il = new InlineLabelTreeItem(selection,parent,strlab));
		add(new OpenButton(showUrl,hiddenUrl,parent));
	}
	
	public class OpenButton extends SimplePanel
	{
		private SafeUri showUrl, hiddenUrl;
		private GeoContainer gc;
		
		public OpenButton(SafeUri showUrl,SafeUri hiddenUrl,final TreeItem ti){
			this.showUrl = showUrl;
			this.hiddenUrl = hiddenUrl;
			addDomHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					boolean open = ti.getState();
					ti.setState(!open);
					setChecked(!open);
				}
			}, ClickEvent.getType());
			setChecked(true);
			this.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		}

		/**
		 * set background-images via HTML
		 */
		public void setImage(String text)
		{
			//String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
			String html = "<img src=\"" + text + "\" style=\"margin-right: 5px;\">";
			this.getElement().setInnerHTML(html);
		}

		public void setChecked(boolean value)
		{
			if (value)
			{
				setImage(showUrl.asString());
			}
			else
			{
				setImage(hiddenUrl.asString());
			}
		}

		public void setEnabled(boolean euclidianShowable) {
		    // TODO Auto-generated method stub
		    
	    }

	}

	public void setText(String string) {
	    il.setText(string);
	    
    }
}
