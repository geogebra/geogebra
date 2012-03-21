package geogebra.gui.inputfield;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * Editor kit for styled text fields. Extends StyledEditorKit so that text is
 * vertically centered in its container.
 * 
 * adapted from article by Stanislav Lapitsky:
 * http://java-sl.com/wrap.html
 * 
 * @author G. Sturr
 * 
 */
public class StyledTextFieldEditorKit extends StyledEditorKit {

	private static final long serialVersionUID = 1L;

	@Override
	public ViewFactory getViewFactory() {
		return new StyledViewFactory();
	}

	/**
	 * Ensures that CenteredBoxView will be used.
	 */
	static class StyledViewFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new LabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new NoWrapParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new CenteredBoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}

			return new LabelView(elem);
		}

	}
}

/**
 * Extends ParagraphView to prevent line wrapping.
 */
class NoWrapParagraphView extends ParagraphView {
	public NoWrapParagraphView(Element elem) {
		super(elem);
	}

	public void layout(int width, int height) {
		super.layout(Short.MAX_VALUE, height);
	}

	public float getMinimumSpan(int axis) {
		return super.getPreferredSpan(axis);
	}
}

/**
 * Extends BoxView so that contents are centered along a given axis.
 */
class CenteredBoxView extends BoxView {
	/**
	 * @param elem
	 * @param axis
	 */
	public CenteredBoxView(Element elem, int axis) {

		super(elem, axis);
	}

	@Override
	protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
			int[] spans) {

		super.layoutMajorAxis(targetSpan, axis, offsets, spans);
		int textBlockHeight = 0;
		int offset = 0;

		for (int i = 0; i < spans.length; i++) {

			textBlockHeight = spans[i];
		}
		offset = (targetSpan - textBlockHeight) / 2;
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] += offset;
		}

	}
}
