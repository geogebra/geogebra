package org.geogebra.common.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Locale;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.LatexRendererSettings;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.SymbolicEditorCommon;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class GgbScriptTest extends BaseUnitTest {

	@Test
	public void onChangeScriptSubstitute() throws ScriptError {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		add("v=(?,?)");
		GeoInputBox ib = add("ib=InputBox(v)");
		MathFieldCommon mf = new MathFieldCommon(new MetaModel(), null);
		SymbolicEditorCommon editor = new SymbolicEditorCommon(mf, getApp());
		((EuclidianViewNoGui) getApp().getActiveEuclidianView()).setSymbolicEditor(editor);
		editor.attach(ib, new Rectangle(0, 0),
				new LatexRendererSettings(0, 0, 0));
		GgbScript ggs = makeScript("label=\"%0\"", "value=\"%1\"", "valid=%2");
		ggs.run(new Event(EventType.EDITOR_KEY_TYPED, ib));
		assertThat(lookup("label"), hasValue("ib"));
		assertThat(lookup("value"), hasValue("(?,?)"));
		assertThat(lookup("valid"), hasValue("true"));
	}

	@Test
	public void scriptShouldStoreUndoOnlyWhenObjectClicked() {
		activateUndo();
		GeoElement pt = add("P=(1,1)");
		GgbScript addPoint = makeScript("(2,2)");
		pt.setClickScript(addPoint);
		add("RunClickScript(P)");
		assertThat(getApp().getKernel().getConstruction()
				.getUndoManager().undoPossible(), equalTo(false));
		pt.runClickScripts(null);
		assertThat(getApp().getKernel().getConstruction()
				.getUndoManager().undoPossible(), equalTo(true));
	}

	@Test
	public void scriptShouldNotTriggeredConcurrentModification()
			throws CircularDefinitionException {
		GeoBoolean show = add("show=true");
		GeoNumeric scriptable = add("scriptable=7");
		GeoNumeric length = add("length=4");
		GgbScript addPoint = makeScript("length=5");
		scriptable.setUpdateScript(addPoint);
		GeoList list = add("a=Sequence((k,k),k,1,length)");
		scriptable.setShowObjectCondition(show);
		list.setShowObjectCondition(show);
		getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				"SetValue(show,false)", false, TestErrorHandler.INSTANCE,
				false, foo -> {});
		assertThat(length, hasValue("5"));
	}

	@Test
	public void emptyScriptShouldNotStoreUndo() {
		activateUndo();
		GeoElement pt = add("P=(1,1)");
		GgbScript doNothing = makeScript("# do nothing");
		pt.setClickScript(doNothing);
		pt.runClickScripts(null);
		assertThat(getApp().getKernel().getConstruction()
				.getUndoManager().undoPossible(), equalTo(false));
	}

	@Test
	@Issue("APPS-5357")
	public void scriptShouldTranslateInterToIntersection() throws ScriptError {
		getApp().setLocale(Locale.UK);
		add("l1 = {1, 2}");
		add("l2 = {2, 4}");
		GgbScript listIntersection = makeScript("l3 = Intersection(l1, l2)");
		getApp().setLocale(Locale.FRENCH);
		listIntersection.run(new Event(EventType.CLICK));
		assertThat(lookup("l3"), hasValue("{2}"));
	}

	@Test
	public void scriptShouldLookupLowercase() throws ScriptError {
		getApp().setLocale(Locale.UK);
		add("f:x=y");
		GgbScript listIntersection = makeScript("setcolor(f,1,0,0)");
		listIntersection.run(new Event(EventType.CLICK));
		assertThat(lookup("f").getObjectColor(), equalTo(GColor.RED));
	}

	@Test
	@Issue("APPS-5701")
	public void commandExecuteShouldNotStoreUndoPoint() {
		activateUndo();
		GeoElement pt = add("A = (1, 1)");
		add("B = (2, 2)");
		pt.setUpdateScript(makeScript("Execute[{\"SetValue(B,B)\"}]"));
		pt.update();
		assertThat(getApp().getKernel().getConstruction()
				.getUndoManager().undoPossible(), equalTo(false));
	}

	private GgbScript makeScript(String... lines) {
		String script = String.join("\n", Arrays.asList(lines));
		return new GgbScript(getApp(), script);
	}
}
