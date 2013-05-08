#
# Proguard config file for GeoGebra
#
#
-ignorewarnings

-injars build/geogebra.jar
-injars build/geogebra_main.jar
-injars build/geogebra_gui.jar
-injars build/geogebra_export.jar
-injars build/geogebra_algos.jar
-injars build/geogebra_cas.jar
-injars build/geogebra_3d.jar
-injars build/geogebra_usb.jar
-injars build/jlatexmath.jar
-injars build/OpenGeoProver.jar

-outjars build/temp

# libraries
-libraryjars <java.home>/lib/rt.jar
-libraryjars lib/build/lib_jsobject.jar
-libraryjars lib/build/lib_mac_extensions.jar
-libraryjars ../jogl1/lib/download/jar/jogl.jar
-libraryjars ../jogl1/lib/download/jar/gluegen-rt.jar
-libraryjars ../jogl2/lib/jogl.all.jar
-libraryjars ../jogl2/lib/gluegen-rt.jar
-libraryjars lib/jython.jar
-libraryjars lib/OpenGeoProver.jar
#-libraryjars ../java150-rt.jar

# Rhino Javascript is not obfuscated
-libraryjars build/geogebra_javascript.jar


-dontoptimize
-allowaccessmodification
-overloadaggressively

# needed for eg StringBuilder.setLength()
# see http://proguard.sourceforge.net/manual/troubleshooting.html
-dontskipnonpubliclibraryclasses

-printmapping geogebra.map 	 
#-applymapping geogebra3-2-0-0.map	 

# Keep GeoGebra application
-keep class geogebra.GeoGebra {
    public static void main(java.lang.String[]);
}

## enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep GeoGebra3D application
-keep class geogebra.GeoGebra3D {
    public static void main(java.lang.String[]);
}

# see #1389 (AbstractMethodError)
# Renderer implements GLEventListener
-keep class geogebra3D.euclidian3D.opengl.Renderer {
    public <methods>;
}

# see #1389 (AbstractMethodError)
# RendererTesselCallBack implements GLUtessellatorCallback
-keep class geogebra3D.euclidian3D.opengl.RendererTesselCallBack {
    public <methods>;
}

# Keep GeoGebra applet
-keep class geogebra.GeoGebraApplet {
    public <methods>;
}

-keep class geogebra.GeoGebraApplet3D {
    public <methods>;
}

# Keep GeoGebraAppletPreloader
-keep class geogebra.GeoGebraAppletPreloader {
    public <methods>;
}

# Keep GeoGebraPanel
-keep class geogebra.GeoGebraPanel {
    public <methods>;
}

# Keep Moodle plugin
-keep class geogebra.GeoGebraMoodleApplet {
    public <methods>;
}

# see META-INF/services
-keep class org.freehep.graphicsio.raw.RawImageWriterSpi { <methods>; }

# needed so that hoteqn can find Des12.gif, etc.
#-keep class geogebra.gui.hoteqn.SymbolLoader { <methods>; }

# JLaTeXMath uses reflection
-keep class org.scilab.forge.jlatexmath.* { <methods>; }

# MathPiper / JAS interaction
-keep class org.mathpiper.builtin.library.jas.* { <methods>; }
-keep class org.mathpiper.builtin.javareflection.* { <methods>; }
-keep class edu.jas.poly.* { <methods>; }

# called with reflection
-keep class edu.xtec.adapter.impl.AdapterImpl { <methods>; }

# Jasymca uses reflection to create functions like LambaSIN
#-keep class jasymca.Lambda* {}

# Rhino Javascript
#-keep class org.mozilla.classfile.* {  }
#-keep class org.mozilla.javascript.* { }
#-keep class org.mozilla.javascript.jdk13.* { }
#-keep class org.mozilla.javascript.jdk15.* {  }
#-keep class org.mozilla.javascript.optimizer.* { }
#-keep class org.mozilla.javascript.regexp.* {  }
#-keep class org.mozilla.javascript.serialize.* {  }
#-keep class org.mozilla.javascript.xml.* {  }

# supress foxtrot error messages
-keep class java.util.LinkedList { java.lang.Object getFirst(); }
-keep class java.lang.Object { java.lang.Object list; }

-keep class geogebra.gui.virtualkeyboard.VirtualKeyboard { public static void main(java.lang.String[]); }

## USB library uses reflection, see JavaDeviceFactory
-keep class org.concord.sensor.pseudo.PseudoSensorDevice { <methods>; }
-keep class org.concord.sensor.nativelib.NativeVernierSensorDevice { <methods>; }
-keep class org.concord.sensor.vernier.labpro.LabProSensorDevice { <methods>; }
-keep class org.concord.sensor.vernier.labquest.LabQuestSensorDevice { <methods>; }
-keep class org.concord.sensor.nativelib.NativeTISensorDevice { <methods>; }
-keep class org.concord.sensor.dataharvest.DataHarvestSensorDevice { <methods>; }
-keep class org.concord.sensor.pasco.SW500SensorDevice { <methods>; }
-keep class org.concord.sensor.pasco.AirLinkSensorDevice { <methods>; }
-keep class org.concord.sensor.pasco.PascoUsbSensorDevice { <methods>; }
-keep class org.concord.sensor.cc.CCInterface0 { <methods>; }
-keep class org.concord.sensor.cc.CCInterface1 { <methods>; }
-keep class org.concord.sensor.cc.CCInterface2 { <methods>; }

-keep class org.concord.sensor.SensorConfig { <methods>; }
-keep class org.concord.sensor.impl.ExperimentConfigImpl { <methods>; }
-keep class org.concord.sensor.ExperimentRequest { <methods>; }
-keep class org.concord.sensor.device.DeviceReader { <methods>; }

-keep class ccsd.vernier.ExperimentConfig  { <methods>; }
-keep class ccsd.vernier.NativeBridge  { <methods>; }
-keep class ccsd.vernier.NativeBridgeConstants  { <methods>; }
-keep class ccsd.vernier.NativeBridgeJNI  { <methods>; }
-keep class ccsd.vernier.SensorConfig  { <methods>; }
-keep class ccsd.vernier.SensorParam  { <methods>; }
-keep class ccsd.vernier.SWIGTYPE_p_float  { <methods>; }
-keep class ccsd.vernier.SWIGTYPE_p_p_ExperimentConfig  { <methods>; }
-keep class ccsd.vernier.SWIGTYPE_p_SensorParam  { <methods>; }
-keep class ccsd.vernier.SWIGTYPE_p_void  { <methods>; }

#####
# Plugin part
####

# So that Jython can access GeoElement methods
-keep class geogebra.plugin.jython.PythonAPI { <methods>; <fields>; }
-keep class geogebra.plugin.jython.PythonFlatAPI { <methods>; <fields>; }
-keep class geogebra.plugin.jython.CommonsMathLinearAPI { <methods>; <fields>; }

# connection to R,eg from python
# from org.rosuda.REngine.Rserve import RConnection;
# R= RConnection();
-keep class org.rosuda.REngine.Rserve.RConnection { <methods>; }
# Contributed by Virgilio Gomez-Rubio, 2012-11-26
-keep class org.rosuda.REngine.REXP { <methods>; }
-keep class org.rosuda.REngine.REXPDouble { <methods>; }
-keep class org.rosuda.REngine.REXPFactor { <methods>; }
-keep class org.rosuda.REngine.REXPInteger { <methods>; }
-keep class org.rosuda.REngine.REXPList { <methods>; }
-keep class org.rosuda.REngine.REXPLogical { <methods>; }
-keep class org.rosuda.REngine.REXPNull { <methods>; }
-keep class org.rosuda.REngine.REXPRaw { <methods>; }
-keep class org.rosuda.REngine.REXPS4 { <methods>; }
-keep class org.rosuda.REngine.REXPString { <methods>; }
-keep class org.rosuda.REngine.REXPVector { <methods>; }
-keep class org.rosuda.REngine.RList { <methods>; }
-keep class org.rosuda.REngine.Rserve.RFileOutputStream { <methods>; }
-keep class org.rosuda.REngine.Rserve.RFileInputStream { <methods>; }

# for the inner class 'Geo'
-keepattributes InnerClasses
-keep class geogebra.plugin.jython.PythonAPI$Geo { <methods>; }
-keep class geogebra.plugin.jython.PythonFlatAPI$Geo { <methods>; }
-keep class geogebra.plugin.jython.PythonFlatAPI$Expression { <methods>; }

-keep class geogebra.plugin.jython.PythonScriptInterface { <methods>; <fields>; }
# allow eg Color.Red
-keep class geogebra.awt.GColorD { <fields>; }
-keep class geogebra.common.plugin.EuclidianStyleConstants { <fields>; }
-keep class geogebra.common.plugin.GeoClass { <fields>; }
-keep class geogebra.common.plugin.Operation { <fields>; }
-keep class geogebra.common.plugin.ScriptType { <methods>; <fields>; }
-keep class geogebra.common.plugin.EventType { <methods>; <fields>; }

-keep class geogebra.plugin.GgbAPI { <methods>; }
-keep class geogebra.common.plugin.GgbAPI { <methods>; }

# so that it can be accessed from Jython
-keep class jd2xx.JD2XX { <methods>; <fields>; }
-keep class jd2xx.JD2XX$ProgramData { <methods>; <fields>; }
-keep class jd2xx.JD2XX$DeviceInfo { <methods>; <fields>; }
-keep class jd2xx.JD2XXEvent { <methods>; <fields>; }
-keep class jd2xx.JD2XXEventListener { <methods>; <fields>; }
-keep class jd2xx.JD2XXInputStream { <methods>; <fields>; }
-keep class jd2xx.JD2XXOutputStream { <methods>; <fields>; }

# so that it can be accessed from Jython
-keep class gnu.io.CommPortIdentifier { <methods>; <fields>; }
-keep class gnu.io.SerialPort { <methods>; <fields>; }

# attempt to make JNI version of Giac work
-keepclasseswithmembers class * {
    native <methods>;
}

# -keep public class * {
#    public protected *;
# }

#-keep class geogebra.gui.util.BrowserLauncher { <methods>; }
-keep class geogebra.plugin.PlugLetIF { <methods>; }
#-keep class geogebra.MyFileFilter { <methods>; }

#-keep class geogebra.Application { <methods>; }
#-keep class geogebra.kernel.Construction { <methods>; }
#-keep class geogebra.kernel.ConstructionElement { <methods>; }
#-keep class geogebra.kernel.AlgoElement { <methods>; }
#-keep class geogebra.kernel.arithmetic.Equation { <methods>; }
#-keep class geogebra.kernel.arithmetic.ExpressionNode { <methods>; }
#-keep class geogebra.kernel.arithmetic.ExpressionValue { <methods>; }
#-keep class geogebra.kernel.arithmetic.Function { <methods>; }
#-keep class geogebra.kernel.arithmetic.NumberValue { <methods>; }
#-keep class geogebra.kernel.Dilateable { <methods>; }
#-keep class geogebra.kernel.GeoBoolean { <methods>; }
#-keep class geogebra.kernel.GeoConic { <methods>; }
#-keep class geogebra.kernel.GeoCurveCartesian { <methods>; }
#-keep class geogebra.kernel.GeoDeriveable { <methods>; }
#-keep class geogebra.kernel.GeoElement { <methods>; }
#-keep class geogebra.kernel.GeoFunction { <methods>; }
#-keep class geogebra.kernel.GeoImage { <methods>; }
#-keep class geogebra.kernel.GeoLine { <methods>; }
#-keep class geogebra.kernel.GeoList { <methods>; }
#-keep class geogebra.kernel.GeoNumeric { <methods>; }
#-keep class geogebra.kernel.GeoPoint { <methods>; }
#-keep class geogebra.kernel.GeoPolygon { <methods>; }
#-keep class geogebra.kernel.GeoSegment { <methods>; }
#-keep class geogebra.kernel.GeoText { <methods>; }
#-keep class geogebra.kernel.GeoVec3D { <methods>; }
#-keep class geogebra.kernel.GeoVector { <methods>; }
#-keep class geogebra.kernel.Kernel { <methods>; }
#-keep class geogebra.kernel.Macro { <methods>; }
#-keep class geogebra.kernel.Mirrorable { <methods>; }
#-keep class geogebra.kernel.Path { <methods>; }
#-keep class geogebra.kernel.PointRotateable { <methods>; }
#-keep class geogebra.kernel.Rotateable { <methods>; }
#-keep class geogebra.kernel.Translateable { <methods>; }
#-keep class org.freehep.graphics2d.TagString { <methods>; }

