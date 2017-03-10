Pod::Spec.new do |s|
  s.name = 'Giac'
  s.version = '1.0'
  s.summary = 'Giac library'
  s.platform = :ios, '9.0'
  
  s.requires_arc = false
  s.authors = 'Bernard Parisse', 'GeoGebra Team'
  s.homepage = 'http://dev.geogebra.org'
  s.source = { :path => 'src' }
  s.license = 'GeoGebra License'
  
  s.source_files = 'headers/*.hpp'
  s.vendored_libraries = 'libs/*.a'
end