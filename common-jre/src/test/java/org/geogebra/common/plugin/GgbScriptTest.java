package org.geogebra.common.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.LatexRendererSettings;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.SymbolicEditorCommon;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
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
	public void emptyScriptShouldNotStoreUndo() {
		activateUndo();
		GeoElement pt = add("P=(1,1)");
		GgbScript doNothing = makeScript("# do nothing");
		pt.setClickScript(doNothing);
		pt.runClickScripts(null);
		assertThat(getApp().getKernel().getConstruction()
				.getUndoManager().undoPossible(), equalTo(false));
	}

	private GgbScript makeScript(String... lines) {
		String script = String.join("\n", Arrays.asList(lines));
		return new GgbScript(getApp(), script);
	}
}
