package org.geogebra.common.kernel.algos;

import static org.junit.Assert.*;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class AlgoTextCornerTest extends BaseUnitTest {

	@Test
	public void cornerInputBox() {
		add("ib=InputBox()");
		assertThat(add("Sequence(Corner(ib,k),k,1,4)"),
				hasValue("{(-3.7, 5.2), (0.22, 5.2), (0.22, 5.7), (-3.7, 5.7)}"));
	}

	@Test
	public void cornerCheckbox() {
		add("cb=Checkbox()");
		assertThat(add("Sequence(Corner(cb,k),k,1,4)"),
				hasValue("{(-4.2, 5.44), (-3.42, 5.44), (-3.42, 6.2), (-4.2, 6.2)}"));
	}

	@Test
	public void cornerButton() {
		add("btn=Button()");
		assertThat(add("Sequence(Corner(btn,k),k,1,4)"),
				hasValue("{(-4.3, 5.82), (-3.54, 5.82), (-3.54, 6.3), (-4.3, 6.3)}"));
	}

	@Test
	public void cornerDropDownList() {
		GeoList dropDown = add("dl={}");
		dropDown.setDrawAsComboBox(true);
		dropDown.updateRepaint();
		assertThat(add("Sequence(Corner(dl,k),k,1,4)"),
				hasValue("{(-4.5, 6.3), (-4.3, 6.3), (-4.3, 6.3), (-4.5, 6.3)}"));
	}

}