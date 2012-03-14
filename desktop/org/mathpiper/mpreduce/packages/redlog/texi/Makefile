# ----------------------------------------------------------------------
# $Id: Makefile,v 1.16 2006/11/25 11:39:18 sturm Exp $
# ----------------------------------------------------------------------
# Copyright (c) 1996 
# Andreas Dolzmann and Thomas Sturm, Universitaet Passau
# ----------------------------------------------------------------------
# $Log: Makefile,v $
# Revision 1.16  2006/11/25 11:39:18  sturm
# makeinfo can produce html meanwhile. It produces better results than
# texi2html. There are some odd constructions with @ifhtml/@ifnothtml at
# the beginning of redlog.texi. For now this allows to automatically obtain
# something nice for the Web page.
#
# Revision 1.15  2006/06/18 10:29:37  sturm
# Use -Pcmz for dvips.
#
# Revision 1.14  2003/04/17 13:34:56  sturm
# Quantifier elimination should work.
#
# Revision 1.13  2002/01/18 12:39:04  sturm
# Do not split info file.
#
# Revision 1.12  1999/04/11 12:50:31  sturm
# Removed reference to program documentation for now.
#
# Revision 1.11  1999/04/01 11:30:11  sturm
# Changed options for texi2html.
#
# Revision 1.10  1999/03/22 16:48:07  sturm
# Overworked chapter "Format and Handling of Formulas". There is only "Other
# Stuff" left in chapter "Miscellaneous" now.
#
# Revision 1.9  1999/03/19 08:29:29  sturm
# Added .tar.Z and .uu target for redlog.
#
# Revision 1.8  1999/03/18 13:44:57  dolzmann
# Adapted the path to dvips.
#
# Revision 1.7  1997/08/18 17:30:12  sturm
# Changes due to renaming:
#    "rl.red" -> "redlog.red"
#    "rl.tst" -> "redlog.tst"
#    "rl.texi" -> "redlog.texi".
#
# Revision 1.6  1996/10/20 15:53:24  dolzmann
# Added *.info-* to the rm statement in target veryclean.
#
# Revision 1.5  1996/10/09 14:45:50  sturm
# Modified target rl.html.
#
# Revision 1.4  1996/10/09 09:44:46  sturm
# Added targets "rl-mip.ps" and "rl-mip-submit.ps".
#
# Revision 1.3  1996/10/06 15:08:09  sturm
# Added target rl.ps.
#
# Revision 1.2  1996/09/30 11:27:50  dolzmann
# Remove more auxiliary files with make clean.
#
# Revision 1.1  1996/09/26 12:16:30  sturm
# Initial check-in.
#
# ----------------------------------------------------------------------
DVIPS=dvips

rlsrc.texi:
	$$redlog/util/mksrcdoc

rlsrc.dvi: rlsrc.texi
	texi2dvi rlsrc.texi

rlsrc.info: rlsrc.texi
	makeinfo -o rlsrc.info rlsrc.texi

rlsrc.html: rlsrc.texi
	mv rlsrc.texi rlsrc.texi.origorig
	sed "s/@top/@unnumbered/" rlsrc.texi.origorig > rlsrc.texi
	texi2html -expandinfo -menu -split_node rlsrc.texi
	mv -f rlsrc.texi.origorig rlsrc.texi

redlog.texi:
	$redlog/util/mkdoc

redlog.dvi: redlog.texi
	texi2dvi redlog.texi

redlog.ps: redlog.dvi
	$(DVIPS) -Pcmz redlog.dvi -o

redlog.ps.Z: redlog.ps
	compress -v redlog.ps

redlog.uu: redlog.ps.Z
	uuencode redlog.ps.Z redlog.ps.Z > redlog.uu

rl-mip.texi: redlog.texi
	/bin/rm -f rl-mip.texi
	echo "\magnification=\magstep1" > rl-mip.texi
	cat redlog.texi >> rl-mip.texi

rl-mip.dvi: rl-mip.texi
	texi2dvi rl-mip.texi

rl-mip-submit.ps: rl-mip.dvi
	$(DVIPS) rl-mip.dvi -o rl-mip-submit.ps

rl-mip.ps: rl-mip-submit.ps
	ps2mip rl-mip-submit.ps > rl-mip.ps

redlog.info: redlog.texi
	makeinfo --no-split redlog.texi

redlog.html: redlog.texi
#	mv redlog.texi redlog.texi.origorig
#	sed -e '/@heading Acknowledgments/i\
#@unnumbered READ THIS FIRST' redlog.texi.origorig > redlog.texi
#	texi2html -split_node -menu -expandinfo redlog.texi
#	mv -f redlog.texi.origorig redlog.texi
	makeinfo --html --ifinfo --no-split redlog.texi

clean:
	/bin/rm -f *~ *.aux *.log *.cp *.cps *.fn *.fns *.ilg *.ind *.ky *.pg *.s *.sw *.sws *.toc *.tp *.tps *.vr *.vrs *.efs *.ef *.mo *.mos

veryclean: clean
	/bin/rm -f *.dvi *.ps *.info *.info-* rlsrc.texi *.html rl-mip.* rl-mip-submit.ps
