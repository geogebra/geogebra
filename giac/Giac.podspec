Pod::Spec.new do |s|
  s.name = 'Giac'
  s.version = '1.0'
  s.summary = 'Giac library'
  s.platform = :ios, '9.0'
  s.exclude_files = 'src/giac/headers/gmp.h'
  s.source_files = 'src/giac/cpp/*', 'src/giac/headers/*', 'src/giac/headers/android/gmp.h'
  s.public_header_files = 'src/giac/headers/*', 'src/giac/headers/android/gmp.h'

  s.requires_arc = false
  s.authors = 'Bernard Parisse', 'GeoGebra Team'
  s.homepage = 'http://dev.geogebra.org'
  s.source = { :path => 'src' }
  s.license = 'GeoGebra License'
  s.compiler_flags = '-Wno-c++11-narrowing', '-DIN_GIAC', '-DGIAC_GENERIC_CONSTANTS', '-DHAVE_CONFIG_H', '-DGIAC_GGB', '-DHAVE_UNISTD_H', '-DHAVE_SYS_TIMES_H', '-DHAVE_SYS_TIME_H', '-DANDROID_INCLUDE', '-DDONT_USE_LIBLAPLACK'
  s.pod_target_xcconfig = { 'USE_HEADERMAP' => 'NO' }
  s.user_target_xcconfig = { 'USE_HEADERMAP' => 'NO' }

  s.subspec 'gmp' do |gmp|
  	gmp.vendored_libraries = 'src/jni/prebuilt/ios/all/libgmp.a'
  	gmp.libraries = 'gmp'
  end

  s.subspec 'mpfr' do |mpfr|
  	mpfr.vendored_libraries = 'src/jni/prebuilt/ios/all/libmpfr.a'
  	mpfr.libraries = 'mpfr'
  end
end