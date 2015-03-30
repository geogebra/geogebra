package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.view.algebra.MathKeyboardListener;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.util.keyboard.OnScreenKeyBoard;

public abstract class AppWFull extends AppW {

	protected AppWFull(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(ae, dimension, laf);
	}

	@Override
	public void showKeyboard(MathKeyboardListener textField) {
		getAppletFrame().showKeyBoard(true, textField, false);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void showKeyboard(MathKeyboardListener textField, boolean forceShow) {
		getAppletFrame().showKeyBoard(true, textField, forceShow);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void updateKeyBoardField(MathKeyboardListener field) {
		OnScreenKeyBoard.setInstanceTextField(this, field);
	}

	@Override
	public void hideKeyboard() {
		getAppletFrame().showKeyBoard(false, null, false);
	}

	public void showStartTooltip() {
		if (articleElement.getDataParamShowStartTooltip()) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
			        getPlain("NewToGeoGebra") + "<br/>"
			                + getPlain("ClickHereToGetHelp"),
			        GeoGebraConstants.QUICKSTART_URL, ToolTipLinkType.Help,
			        this);
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}
}
