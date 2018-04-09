// -*- mode:C++ ; compile-command: "g++ -I.. -g -c mathml.cc" -*-
/*
 *  Copyright (C) 2003 J.P.Branchard
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef _GIAC_MATHML_H
#define _GIAC_MATHML_H

#include "first.h"
#include <string>

#ifndef NO_NAMESPACE_GIAC 
namespace giac { 
#endif // ndef NO_NAMESPACE_GIAC */

#ifndef GIAC_HAS_STO_38
  // k is reduced modulo 126
  void arc_en_ciel(int k,int & r,int & g,int & b);

  extern const char mathml_preamble[];
  extern const char mathml_end[];
  class gen;  
  extern const unary_function_ptr * const  at_mathml;
  gen _mathml(const gen & g,GIAC_CONTEXT);
  gen _svg(const gen & g,GIAC_CONTEXT);
  gen _spread2mathml(const gen & g,GIAC_CONTEXT);
  std::string spread2mathml(const matrice & m,int formule,GIAC_CONTEXT);
  std::string matrix2mathml(const matrice & m,GIAC_CONTEXT);
  std::string gen2mathml(const gen & e,GIAC_CONTEXT);
  std::string gen2mathml(const gen & e, std::string &svg,GIAC_CONTEXT);
  std::string gen2svg(const gen &e,GIAC_CONTEXT,bool withpreamble=false);
  //std::string gen2svg(const gen &e,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT);
  std::string gen2svg(const gen &e,double xmin,double xmax,double ymin,double ymax,double ysurx,GIAC_CONTEXT,bool withpreamble=false);
  std::string svg_preamble_pixel(const gen & g,double width_cm, double height_cm,bool xml=true);
  std::string svg_preamble(double width_cm, double height_cm,bool xml=true);
  std::string svg_preamble_pixel(const gen & g,double svg_width_cm, double svg_height_cm,double xmin,double xmax,double ymin,double ymax,bool ortho,bool xml);
  std::string svg_preamble(double svg_width_cm, double svg_height_cm,double xmin,double xmax,double ymin,double ymax,bool ortho,bool xml);
  std::string svg_grid();
  std::string svg_grid(double xmin,double xmax,double ymin,double ymax);
  std::string svg_grid(double xmin,double xmax,double ymin,double ymax,const plot_attr & p);
  extern const char svg_end[];
  std::string ingen2mathml(const gen & g,bool html5,GIAC_CONTEXT);
  // greek letter support 
  std::string idnt2mathml(const std::string & s0);
  std::string idnt2mathml_(const std::string & s0);

#endif // RTOS_THREADX

#ifndef NO_NAMESPACE_GIAC 
} 
#endif // ndef NO_NAMESPACE_GIAC 

#endif // _GIAC_MATHML_H
