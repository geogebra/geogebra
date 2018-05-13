# This is tested on Linux only.

cmake_minimum_required(VERSION 3.10)
project(minigiac)

# TODO: Add gmp/mpfr detection

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_FLAGS "-fpermissive -std=c++0x")

include_directories(src/giac/headers)

add_definitions(-DHAVE_NO_HOME_DIRECTORY -DHAVE_SYSCONF -DGIAC_GGB -DIN_GIAC -DHAVE_LIB_PTHREAD
        -DGIAC_GENERIC_CONSTANTS -DHAVE_UNISTD_H -DHAVE_SYS_TIMES_H -DHAVE_SYS_TIME_H -DTIMEOUT
        -DHAVE_MPFR_1 -DHAVE_LIBMPFR -DVERSION="1.2.3")

add_executable(minigiac
        src/giac/cpp/alg_ext.cc
        src/giac/cpp/cocoa.cc
        src/giac/cpp/csturm.cc
        src/giac/cpp/derive.cc
        src/giac/cpp/desolve.cc
        src/giac/cpp/ezgcd.cc
        src/giac/cpp/freeglut_stroke_roman.c
        src/giac/cpp/gauss.cc
        src/giac/cpp/gausspol.cc
        src/giac/cpp/gen.cc
        src/giac/cpp/global.cc
        src/giac/cpp/help.cc
        src/giac/cpp/identificateur.cc
        src/giac/cpp/ifactor.cc
        src/giac/cpp/index.cc
        src/giac/cpp/input_lexer.cc
        src/giac/cpp/input_parser.cc
        src/giac/cpp/intg.cc
        src/giac/cpp/intgab.cc
        src/giac/cpp/isom.cc
        src/giac/cpp/lin.cc
        src/giac/cpp/maple.cc
        src/giac/cpp/mathml.cc
        src/giac/cpp/misc.cc
        src/giac/cpp/modfactor.cc
        src/giac/cpp/modpoly.cc
        src/giac/cpp/moyal.cc
        src/giac/cpp/opengl.cc
        src/giac/cpp/pari.cc
        src/giac/cpp/permu.cc
        src/giac/cpp/plot.cc
        src/giac/cpp/plot3d.cc
        src/giac/cpp/prog.cc
        src/giac/cpp/quater.cc
        src/giac/cpp/risch.cc
        src/giac/cpp/rpn.cc
        src/giac/cpp/series.cc
        src/giac/cpp/solve.cc
        src/giac/cpp/sparse.cc
        src/giac/cpp/subst.cc
        src/giac/cpp/sym2poly.cc
        src/giac/cpp/symbolic.cc
        src/giac/cpp/tex.cc
        src/giac/cpp/threaded.cc
        src/giac/cpp/ti89.cc
        src/giac/cpp/tinymt32.cc
        src/giac/cpp/TmpFGLM.cpp
        src/giac/cpp/TmpLESystemSolver.cpp
        src/giac/cpp/unary.cc
        src/giac/cpp/usual.cc
        src/giac/cpp/vecteur.cc
        src/giac/headers/android/gmp.h
        src/giac/headers/android/mpfr.h
        src/giac/headers/alg_ext.h
        src/giac/headers/cocoa.h
        src/giac/headers/config.h
        src/giac/headers/csturm.h
        src/giac/headers/derive.h
        src/giac/headers/desolve.h
        src/giac/headers/dispatch.h
        src/giac/headers/ezgcd.h
        src/giac/headers/fac_table.h
        src/giac/headers/fib_table.h
        src/giac/headers/first.h
        src/giac/headers/fits_s.h
        src/giac/headers/fits_u.h
        src/giac/headers/fraction.h
        src/giac/headers/gauss.h
        src/giac/headers/gausspol.h
        src/giac/headers/gen.h
        src/giac/headers/gen_inverse.h
        src/giac/headers/giac.h
        src/giac/headers/giacintl.h
        src/giac/headers/giacPCH.h
        src/giac/headers/global.h
        src/giac/headers/gmp-mparam.h
        src/giac/headers/gmp.h
        src/giac/headers/gmp_replacements.h
        src/giac/headers/gmpxx.h
        src/giac/headers/help.h
        src/giac/headers/identificateur.h
        src/giac/headers/ieee_floats.h
        src/giac/headers/ifactor.h
        src/giac/headers/index.h
        src/giac/headers/input_lexer.h
        src/giac/headers/input_parser.h
        src/giac/headers/intg.h
        src/giac/headers/intgab.h
        src/giac/headers/isom.h
        src/giac/headers/lexer.h
        src/giac/headers/lexer_tab_int.h
        src/giac/headers/lin.h
        src/giac/headers/longlong.h
        src/giac/headers/maple.h
        src/giac/headers/mathml.h
        src/giac/headers/misc.h
        src/giac/headers/modfactor.h
        src/giac/headers/modpoly.h
        src/giac/headers/monomial.h
        src/giac/headers/moyal.h
        src/giac/headers/mp_bases.h
        src/giac/headers/mparam.h
        src/giac/headers/mpf2mpfr.h
        src/giac/headers/mpfr-gmp.h
        src/giac/headers/mpfr-impl.h
        src/giac/headers/mpfr-intmax.h
        src/giac/headers/mpfr-longlong.h
        src/giac/headers/mpfr-thread.h
        src/giac/headers/mpfr.h
        src/giac/headers/opengl.h
        src/giac/headers/pari.h
        src/giac/headers/path.h
        src/giac/headers/permu.h
        src/giac/headers/plot.h
        src/giac/headers/plot3d.h
        src/giac/headers/poly.h
        src/giac/headers/prog.h
        src/giac/headers/quater.h
        src/giac/headers/risch.h
        src/giac/headers/rpn.h
        src/giac/headers/series.h
        src/giac/headers/solve.h
        src/giac/headers/sparse.h
        src/giac/headers/static.h
        src/giac/headers/static_extern.h
        src/giac/headers/static_help.h
        src/giac/headers/static_lexer.h
        src/giac/headers/static_lexer_.h
        src/giac/headers/subst.h
        src/giac/headers/sym2poly.h
        src/giac/headers/symbolic.h
        src/giac/headers/tex.h
        src/giac/headers/threaded.h
        src/giac/headers/ti89.h
        src/giac/headers/tinymt32.h
        src/giac/headers/tinymt32_license.h
        src/giac/headers/TmpFGLM.H
        src/giac/headers/TmpLESystemSolver.H
        src/giac/headers/trialdivtab.h
        src/giac/headers/unary.h
        src/giac/headers/usual.h
        src/giac/headers/vecteur.h
        src/giac/headers/vector.h
        src/minigiac/cpp/minigiac.cc)

target_link_libraries(minigiac gmp mpfr)
