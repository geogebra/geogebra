package org.geogebra.web.full.gui.view.algebra.contextmenu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.AddLabelAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.CreateSliderAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.RemoveSliderAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class AlgebraMenuItemCollectionCASTest {

	private AlgebraMenuItemCollectionCAS itemCollection;

	@Before
	public void setUp() {
		AppWFull app = AppMocker.mockCas(getClass());
		itemCollection = new AlgebraMenuItemCollectionCAS(app.getAlgebraView());
	}

	@Test
	public void testActionOrder() {
		int indexOfAddLabel = indexOf(AddLabelAction.class);
		int indexOfCreateSlider = indexOf(CreateSliderAction.class);
		int indexOfRemoveSlider = indexOf(RemoveSliderAction.class);
		int indexOfDuplicate = indexOf(DuplicateAction.class);

		boolean isCreateSliderItemInPlace =
				indexOfAddLabel < indexOfCreateSlider && indexOfCreateSlider < indexOfDuplicate;
		boolean isRemoveSliderItemInPlace =
				indexOfAddLabel < indexOfRemoveSlider && indexOfRemoveSlider < indexOfDuplicate;
		boolean areSliderItemsInPlace = isCreateSliderItemInPlace && isRemoveSliderItemInPlace;
		assertThat(areSliderItemsInPlace, is(true));
	}

	private int indexOf(Class clazz) {
		int i = 0;
		for (MenuAction item : itemCollection) {
			if (clazz.isInstance(item)) {
				return i;
			}
			i++;
		}
		return -1;
	}
}