# Run it with JRuby
# Don't forget to generate unobfuscated jars.

require 'rubygems'
require 'irb'

# Where the jar files are.
JAR_DIR= File.join(File.dirname(__FILE__), '..', 'build', 'unsigned', 'unpacked')

%w{geogebra.jar
   geogebra_main.jar
   geogebra_properties.jar
   geogebra_gui.jar
}.each do |jar|
  require File.join(JAR_DIR, jar)
end

# Just some constants.
App = Java::GeogebraMain::Application
DefaultApp = Java::GeogebraMain::DefaultApplication
DropTarget = Java::JavaAwtDnd::DropTarget
GgbFrame = Java::GeogebraGuiApp::GeoGebraFrame


# Taken from GeoGebra codebase.
@wnd = GgbFrame.new
@app = DefaultApp.new([].to_java(:string), @wnd, true)

@app.gui_manager.init_menubar

# Init GUI
@wnd.application=@app
@wnd.content_pane.add @app.build_application_panel
@wnd.drop_target = DropTarget.new(@wnd, Java::GeogebraGui::FileDropTargetListener.new(@app))
@wnd.add_window_focus_listener @wnd
@wnd.java_send :updateAllTitles

#GeoSetConstructionOrder
def @app.gsco
  kernel.construction.geo_set_construction_order
end

puts 'GeoGebraFrame is stored in @wnd and Application in @app.'
puts 'Remember gsco. If you want the GeoGebra window, just type:'
puts '@wnd.visible = true'
IRB.start
