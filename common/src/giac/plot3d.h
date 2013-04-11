/* -*- mode:C++ ; compile-command: "g++ -I.. -g -c plot3d.cc" -*- */
#ifndef _GIAC_PLOT3D_H
#define _GIAC_PLOT3D_H
/*
 *  Copyright (C) 2000 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#include <stdexcept>
#include <cmath>
#include <cstdlib>
#include "gen.h"
#include <stdio.h>
// #include <stdiostream.h>
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif
#ifdef HAVE_SYS_TIME_H
#include <sys/time.h>
#else
#define clock_t int
#define clock() 0
#endif
#ifndef HAVE_NO_SYS_RESOURCE_WAIT_H
#include <sys/resource.h>
#include <sys/wait.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  extern const unary_function_ptr * const  at_sphere ;
  extern const unary_function_ptr * const  at_plan ;
  gen hypersurface(const gen & args,const gen & equation,const gen & vars);
  gen do_point3d(const gen & g);
  bool is3d(const giac::gen & g);
  bool perpendiculaire_commune(const gen & d1,const gen & d2,gen & M, gen & N,vecteur & n,GIAC_CONTEXT);
  gen similitude3d(const vecteur & centrev,const gen & angle,const gen & rapport,const gen & b,int symrot,GIAC_CONTEXT);
  gen hypersphere_equation(const gen & g,const vecteur & xyz);
  vecteur interpolyedre(const vecteur & p,const gen & bb,GIAC_CONTEXT);
  vecteur interdroitehyperplan(const gen & a,const gen &b,GIAC_CONTEXT);
  vecteur interhyperplan(const gen & p1,const gen & p2,GIAC_CONTEXT);
  vecteur interhypersurfacecurve(const gen & a,const gen &b,GIAC_CONTEXT); 
  vecteur inter2hypersurface(const gen & a,const gen &b,GIAC_CONTEXT);
  // a hyperplan, b hypersphere
  vecteur interplansphere(const gen & a,const gen & b,GIAC_CONTEXT);
  vecteur remove_face(const vecteur & face,const vecteur & v,GIAC_CONTEXT);
  gen hyperplan2hypersurface(const gen & g);
  gen hypersphere2hypersurface(const gen & g);
  gen hypersphere_equation(const gen & g,const vecteur & xyz);
  vecteur hypersphere_parameq(const gen & g,const vecteur & st);
  gen hypersurface_equation(const gen & g,const vecteur & xyz,GIAC_CONTEXT);

  bool hyperplan_normal_point(const gen & g,vecteur & n,vecteur & P);

  vecteur rand_3d();

  // Given a 3-d vector n, find 2 vectors normal to n
  bool normal3d(const gen & n,vecteur & v1,vecteur & v2);
  gen plotparam3d(const gen & f,const vecteur & vars,double function_xmin,double function_xmax, double function_ymin, double function_ymax,double function_zmin,double function_zmax,double function_umin,double function_umax,double function_vmin,double function_vmax,bool clrplot,bool f_autoscale,const vecteur & attributs,double ustep,double vstep,const gen & eq,const vecteur & eqvars,GIAC_CONTEXT);

  gen _plot3d(const gen & args,GIAC_CONTEXT);
  extern const alias_type alias_at_plot3d;
  extern const unary_function_ptr * const  at_plot3d ;

  extern const unary_function_ptr * const  at_hypersurface;
  extern const unary_function_ptr * const  at_hyperplan;
  gen hypersurface(const gen & args,const gen & equation,const gen & vars);
  gen _hypersurface(const gen & args,GIAC_CONTEXT);
  gen hypersurface_equation(const gen & g,const vecteur & xyz,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hypersphere;
  vecteur hyperplan_normal(const gen & g);
  gen _hyperplan(const gen & args,GIAC_CONTEXT);
  gen _plan(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hyperplan;
  gen _cone(const gen & args,GIAC_CONTEXT);
  gen _demi_cone(const gen & args,GIAC_CONTEXT);
  gen _cylindre(const gen & args,GIAC_CONTEXT);
  gen _perpendiculaire_commune(const gen & args,GIAC_CONTEXT);
  gen _polyedre(const gen & args,GIAC_CONTEXT);
  gen _prisme(const gen & args,GIAC_CONTEXT);
  gen _parallelepipede(const gen & args,GIAC_CONTEXT);
  gen _pyramide(const gen & args,GIAC_CONTEXT);
  gen _tetraedre_centre(const gen & args,GIAC_CONTEXT);
  gen _cube(const gen & args,GIAC_CONTEXT);
  gen _cube_centre(const gen & args,GIAC_CONTEXT);
  gen _octaedre(const gen & args,GIAC_CONTEXT);
  gen _icosaedre(const gen & args,GIAC_CONTEXT);
  gen _dodecaedre(const gen & args,GIAC_CONTEXT);
  gen _aretes(const gen & args,GIAC_CONTEXT);
  gen _faces(const gen & args,GIAC_CONTEXT);

  gen _hypersphere(const gen & args,GIAC_CONTEXT);
  gen _sphere(const gen & args,GIAC_CONTEXT);
  gen _quadrique(const gen & args,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_hypersphere;
  gen plotparam3d(const gen & f,const vecteur & vars,double function_xmin,double function_xmax, double function_ymin, double function_ymax,double function_zmin,double function_zmax,double function_umin,double function_umax,double function_vmin,double function_vmax,bool densityplot,bool f_autoscale,const vecteur & attributs,double ustep,double vstep,const gen & eq,const vecteur & eqvars,GIAC_CONTEXT);
  gen plotimplicit(const gen& f_orig,const gen&x,const gen & y,const gen & z,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,int nxstep,int nystep,int nzstep,double eps,const vecteur & attributs,bool unfactored,const context * contextptr);

  bool est_cospherique(const gen & a,const gen & b,const gen & c,const gen & d,const gen & f,GIAC_CONTEXT);
  gen _est_cospherique(const gen & args,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_PLOT3D_H
