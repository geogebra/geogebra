# -*- mode:Makefile -*-
# giac in javascript with emcc
# -s EXPORTED_FUNCTIONS="['_caseval']": export function
# then from javascript console
# caseval = Module.cwrap('caseval', 'string', ['string']) type number (inc. pointer) or string
# caseval = Module.cwrap('_ZN4giac7casevalEPKc', 'string', ['string'])
# Module['noExitRuntime']=true
# --closure 1 run closure compiler, this is long
# --pre-js: add javascript code
# --compression lzma/lzma-native,lzma/lzma-decoder.js,LZMA.decompress
# lzma/lzma-native < file_to_compress > compressed_file
# -s options are in emscripten src/settings.js
PREC = -s PRECISE_I32_MUL=1 -DGIAC_GGB 
CXXFLAGS = $(PREC) -I. -I..  -DHAVE_CONFIG_H -DIN_GIAC -DGIAC_GENERIC_CONSTANTS -DNO_STDEXCEPT # --llvm-opts 2 -v
CFLAGS =  $(PREC) -I. -I.. -v  # -pg
LDFLAGS = $(PREC) -O2 -v #--closure 1
LDJSFLAGS = $(PREC) -O2 -v -s EXPORTED_FUNCTIONS="['__ZN4giac7casevalEPKc']" # -s TOTAL_MEMORY=24000000
CXX=emcc
CC=emcc
GIACOBJS = sym2poly.o gausspol.o threaded.o maple.o ti89.o mathml.o moyal.o misc.o permu.o quater.o desolve.o input_parser.o symbolic.o index.o modpoly.o modfactor.o ezgcd.o derive.o solve.o intg.o intgab.o risch.o lin.o series.o subst.o vecteur.o csturm.o tex.o global.o ifactor.o alg_ext.o gauss.o isom.o help.o plot.o plot3d.o rpn.o prog.o pari.o cocoa.o TmpLESystemSolver.o TmpFGLM.o unary.o usual.o identificateur.o gen.o input_lexer.o tinymt32.o
LIBS = libmpfr.a libgmp.a
giacggb.js:	$(GIACOBJS)
	$(CXX) $(LDJSFLAGS) $(GIACOBJS) $(LIBS) -o giacggb.js --closure 1
#	lzma/lzma-native < giacggb.js > giacggb.js.compress
#	cp giacggb.js /shared/ggbjs/giac.js
#	scp giacggb.js malherbe:public_html/giac
icas.html:	$(GIACOBJS) icas.o
	$(CXX) $(LDFLAGS) $(GIACOBJS) $(LIBS) icas.o -o icas.html
clean:
	rm -f $(GIACOBJS) test.o icas.o
.cc.o:
	$(CXX) $(CXXFLAGS) -c $<

