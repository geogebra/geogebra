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
#ifndef _GIAC_TEX_H
#define _GIAC_TEX_H
#include "first.h"
#include <string>
#include "vector.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  // dimension of the LaTeX output figures default 12 cm x 12 cm
  extern double horiz_latex; 
  extern double vert_latex;  
  extern const char tex_preamble[];
  extern const char tex_end[];
  extern const char mbox_begin[];
  extern const char mbox_end[];
  class gen;

  bool is_clipped(double a,double xmin,double xmax,double b,double ymin,double ymax,double c,double & x0,double &y0,double & x1,double &y1);

  bool clip_line(double x1,double y1,double x2,double y2,double xmin,double ymin,double xmax,double ymax,int mode,double & xa,double & ya,double & xb,double & yb);

  std::string get_path(const std::string & s);
  std::string remove_path(const std::string &);

  void evalfdouble2reim(const gen & a,gen & e,gen & f0,gen & f1,GIAC_CONTEXT);
  void autoscaleminmax(std::vector<double> & v,double & m,double & M);
  // return true if g has a circle inside so that we orthonormalize
  bool autoscaleg(const gen & g,std::vector<double> & vx,std::vector<double> & vy,std::vector<double> & vz,GIAC_CONTEXT);

  std::string gen2tex(const gen & e,GIAC_CONTEXT);
  // convert _, &, {, } to \_ \& \{ \}
  std::string translate_underscore(const std::string & s); 
  int greek2tex(const std::string & s,std::string & texs,bool mathmode);

  std::string spread2tex(const matrice & m,int formule,GIAC_CONTEXT); // was formule=1 by default
  gen graph2tex(const gen & args,const vecteur & v,GIAC_CONTEXT);
  int graph2tex(FILE * file,const vecteur & v,double X1,double X2,double Y1,double Y2,double xunit,double yunit,const char * filename,bool logo,GIAC_CONTEXT);
  int graph2tex(FILE * file,const vecteur & v,double X1,double X2,double Y1,double Y2,double Unit,const char * filename,bool logo,GIAC_CONTEXT);
  int graph2tex(const std::string &s,const vecteur & v,double X1,double X2,double Y1,double Y2,double xunit,double yunit,bool logo,GIAC_CONTEXT);
  int graph2tex(const std::string &s,const vecteur & v,double X1,double X2,double Y1,double Y2,double Unit,bool logo,GIAC_CONTEXT);
  gen _graph2tex(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_graph2tex ;
  gen _graph3d2tex(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_graph3d2tex ;
  gen _latex(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_latex ;
  extern const unary_function_ptr * const  at_TeX ;
  std::string get_path(const std::string & st);
  std::string remove_path(const std::string & st);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_TEX_H
