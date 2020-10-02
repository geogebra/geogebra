package org.geogebra.web.full.gui.view.algebra.contextmenu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.AddLabelItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.CreateSliderItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DuplicateInputItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.RemoveSliderItem;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AlgebraMenuItemCollectionCASTest {

	private AlgebraMenuItemCollectionCAS itemCollection;

	@Before
	public void setUp() {
		AppWFull app = AppMocker.mockCas(getClass());
		itemCollection = new AlgebraMenuItemCollectionCAS(app.getAlgebraView());
	}

	@Test
	public void testActionOrder() {
		int indexOfAddLabel = indexOf(AddLabelItem.class);
		int indexOfCreateSlider = indexOf(CreateSliderItem.class);
		int indexOfRemoveSlider = indexOf(RemoveSliderItem.class);
		int indexOfDuplicate = indexOf(DuplicateInputItem.class);

		boolean isCreateSliderItemInPlace =
				indexOfAddLabel < indexOfCreateSlider && indexOfCreateSlider < indexOfDuplicate;
		boolean isRemoveSliderItemInPlace =
				indexOfAddLabel < indexOfRemoveSlider && indexOfRemoveSlider < indexOfDuplicate;
		boolean areSliderItemsInPlace = isCreateSliderItemInPlace && isRemoveSliderItemInPlace;
		assertThat(areSliderItemsInPlace, is(true));
	}

	private int indexOf(Class clazz) {
		int i = 0;
		for (MenuItem item : itemCollection) {
			if (clazz.isInstance(item)) {
				return i;
			}
			i++;
		}
		return -1;
	}
}