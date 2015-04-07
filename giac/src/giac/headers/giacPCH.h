#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
/*
 *  Copyright (C) 2000,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

using namespace std;
#include <cmath>
#include <stdexcept>
#include <cstdlib>
#include <map>
#include <errno.h>
#include "gen.h"
#include "static_extern.h"
#include "identificateur.h"
#include "symbolic.h"
#include "poly.h"
#include "usual.h"
#include "series.h"
#include "sym2poly.h"
#include "moyal.h"
#include "subst.h"
#include "gausspol.h"
#include "identificateur.h"
#include "ifactor.h"
#include "prog.h"
#include "rpn.h"
#include "plot.h"
#include "pari.h"
#include "tex.h"
#include "unary.h"
#include "intg.h"
#include "ti89.h"
#include "solve.h"
#include "alg_ext.h"
#include "lin.h"
#include "derive.h"
#include "series.h"
#include "misc.h"
#include "derive.h"
#include "desolve.h"
#include "ezgcd.h"
#include "gauss.h"
#include "help.h"
#include "maple.h"
#include "mathml.h"
#include "ifactor.h"
#include "index.h"
#include "intg.h"
#include "intgab.h"

//#include "windows.h"



#include "global.h"
#include "permu.h"
#include "plot3d.h"
#include "risch.h"
#include "solve.h"

#include "symbolic.h"
//#include "threaded.h"
#include "vecteur.h"
