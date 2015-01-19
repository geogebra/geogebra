package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author gabor
 * 
 * MyCanvasJbutton a Canvas that used as a button
 *
 */
public class MyCJButton extends Composite implements MouseDownHandler,
        MouseUpHandler, MouseOutHandler, MouseOverHandler, HasSetIcon {	
	
	//public static final String DEFAULT_BACKGROUND_STYLE = "white";
	//private static final String DEFAULT_BORDER_STYLE = "gray";
	
	/**
	 *  Default button width for CanvasButtons
	 */
	//public static int DEFAULT_BUTTON_WIDTH = 20;
	/**
	 * Default button height for CanvasButtons
	 */
	//public static int DEFAULT_BUTTON_HEIGHT = 20;
	
	protected static int TEXT_OFFSET = 3;
	
	protected Label button;
	private Canvas tempCanvas = null;
	private Context2d tempContext = null;
	/**
	 * button width
	 */
	//protected int buttonWidth = DEFAULT_BUTTON_WIDTH;
	/**
	 * button height
	 */
	//protected int buttonHeight = DEFAULT_BUTTON_HEIGHT;
	
	//protected String backgroundStyle = DEFAULT_BACKGROUND_STYLE;
	//protected String borderStyle = DEFAULT_BORDER_STYLE;
	private boolean isEnabled;
	private String toolTipText;
	private boolean loadHandlerAllowed = false;
	private ImageOrText icon;
	private Label buttonContent;
	private boolean imageMode = false;
	
	/**
	 * 
	 * Creates a new button
	 * 
	 * @param icon as imageResource
	 */
	
	public MyCJButton(final Image image) {
		button = new Label("");
		buttonContent = new Label("");
		buttonContent.setStyleName("buttonContent");
		button.getElement().appendChild(buttonContent.getElement());
		
		//button.setWidth(buttonWidth+"px");
		//button.setHeight(buttonHeight+"px");
		button.addMouseDownHandler(this);
		button.addMouseUpHandler(this);
		button.addMouseOverHandler(this);
		button.addMouseOutHandler(this);
		
		loadHandlerAllowed = true;
		image.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {

				if (!loadHandlerAllowed)
					return;

				//button.getElement().getStyle().setBackgroundImage(image.getUrl());
				buttonContent.getElement().getStyle().setBackgroundImage(image.getUrl());
			}
		});
		RootPanel.get().add(image);
		initWidget(button);
		setStyleName("MyCanvasButton");
		isEnabled = true;
    }
	
	public Label getButtonContent() {
		return buttonContent;
	}
	
	public void setText(String text){
		button.setText("BT"+text);
	}

	/**
	 *  Creates a CanvasButton with empty image
	 */
	public MyCJButton() {
		this(new Image() {
			{
				//setWidth(DEFAULT_BUTTON_WIDTH+"px");
				//setHeight(DEFAULT_BUTTON_HEIGHT+"px");
				setUrl(AppResources.INSTANCE.empty().getSafeUri());
			}
		});
    }

	public void onMouseUp(MouseUpEvent event) {
		if (!isEnabled) {
			return;
		}

		setDownState(false);
	}

	public void onMouseDown(MouseDownEvent event) {
		if (!isEnabled) {
			return;
		}
		setDownState(true);
	}
	
	private void setDownState(boolean downState) {
	   //TODO less visible
	   if(downState){
		   this.addStyleName("selected");
	   }else{
		   this.removeStyleName("selected");
	   }
    }

	

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return button.addClickHandler(handler);
    }
	
	
	private Context2d getTempContext2D() {
	    if (tempContext == null) {
	    	tempCanvas = Canvas.createIfSupported();
	    	tempContext = tempCanvas.getContext2d();
	    }
	    return tempContext;
    }

	public void setIcon(ImageOrText icon) {
		if(this.imageMode && icon.getUrl() == null){
			return;
		}
		if(icon.getUrl() != null){
			this.imageMode = true;
		}		
		this.icon = icon;
		loadHandlerAllowed = false;
		icon.applyToLabel(buttonContent);
		setDownState(false);
	}
	
	public ImageOrText getIcon(){
		return this.icon;
	}

	/*AG tmppublic void setIcon(CanvasElement ce) {
		int cwidth = ce.getWidth();
		int cheight = ce.getHeight();
	    if (cwidth > 0 && cheight > 0) {
	    	Context2d ctx = getTempContext2D();
	    	tempCanvas.setWidth(cwidth+"px");
	    	tempCanvas.setHeight(cheight+"px");
	    	tempCanvas.setCoordinateSpaceWidth(cwidth);
	    	tempCanvas.setCoordinateSpaceHeight(cheight);
	    	ctx.drawImage(ce, 0, 0, cwidth, cheight);
	    	icon = ctx.getImageData(0, 0, cwidth, cheight);
	    	setDownState(false);
	    }
    }*/

	public Object getButton() {
	    return button;
    }

	protected boolean isEnabled() {
		return isEnabled ;
	}
	
	protected void setIsEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
		if (enabled) {
			removeStyleName("disabled");
		} else {
			addStyleName("disabled");

		}
	}
//	public void addClickHandler(ClickHandler handler) {
//		button.addClickHandler(handler);
//	}

	protected void addBlurHandler(BlurHandler handler) {
		button.addDomHandler(handler,BlurEvent.getType());
	}
	
	protected void addFocusHandler(FocusHandler handler) {
		button.addDomHandler(handler,FocusEvent.getType());
	}
	
	protected int getWidth() {
		return button.getOffsetWidth();
	}
	
	protected int getHeight() {
		return button.getOffsetHeight();
	}
	
	public void addActionListener(final ClickHandler handler) {
		button.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (!isEnabled) {
					return;
				}
				handler.onClick(event);
			}
		});
	}
	
	
	/**
	 * Sets the toolTip text
	 * 
	 * @param toolTipText
	 *            toolTip string
	 */
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}

	public void onMouseOver(MouseOverEvent event) {
		if (!isEnabled) {
			return;
		}

		App.debug("on mouseover --- MyCJButton" );
		ToolTipManagerW.sharedInstance().showToolTip(toolTipText);
	}

	public void onMouseOut(MouseOutEvent event) {
		if (!isEnabled) {
			return;
		}

		App.debug("on mouseOUT --- MyCJButton" );
		ToolTipManagerW.sharedInstance().showToolTip(null);
	}

}
