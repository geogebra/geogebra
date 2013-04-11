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
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
#ifndef _GIAC_MATHML_H
#define _GIAC_MATHML_H

#include "first.h"
#include <string>

#ifndef NO_NAMESPACE_GIAC 
namespace giac { 
#endif // ndef NO_NAMESPACE_GIAC */

#ifndef RTOS_THREADX
  extern const char mathml_preamble[];
  extern const char mathml_end[];
  class gen;  
  gen _mathml(const gen & g,GIAC_CONTEXT);
  gen _spread2mathml(const gen & g,GIAC_CONTEXT);
  std::string spread2mathml(const matrice & m,int formule,GIAC_CONTEXT);
  std::string matrix2mathml(const matrice & m,GIAC_CONTEXT);
  std::string gen2mathml(const gen & e,GIAC_CONTEXT);
  std::string gen2mathml(const gen & e, std::string &svg,GIAC_CONTEXT);
  std::string gen2svg(const gen &e,GIAC_CONTEXT);
  std::string svg_preamble(double width_cm, double height_cm);
  std::string svg_grid();
  extern const char svg_end[];
  extern std::string svg_legend; // variable globale

#endif // RTOS_THREADX

#ifndef NO_NAMESPACE_GIAC 
} 
#endif // ndef NO_NAMESPACE_GIAC 

#endif // _GIAC_MATHML_H
