// -*- mode:C++ ; compile-command: "g++ -I.. -g -c help.cc" -*-
#ifndef _GIAC_HELP_H
#define _GIAC_HELP_H
#include "first.h"
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

#include <fstream>
#include <string>
#include "vector.h"
#include <map>
#include <cstdlib>
#include <stdio.h>
#ifdef HAVE_UNISTD_H
#include <unistd.h> // For reading arguments from file
#endif
#include <cmath>
#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // true if ch is alphanumeric
  bool isalphan(char ch);

  // search if cmd_name has static describing help
  // NB: cmd_name may be localized but related is not localized
  bool has_static_help(const char * cmd_name,int lang,const char * & howto,const char * & syntax,const char * & related,const char * & examples);

  // search for s in a read-opened FILE f
  bool grep(FILE * f,const std::string & s);
  // search for s in filename
  bool grep(const std::string & filename,const std::string & s);

  extern const char default_helpfile[]; // help filename
  extern const int HELP_MAXLENSIZE ; // less than 20 lines of 80 chars
  struct localized_string {
    int language; // 1: franccais, 2: english, etc.
    std::string chaine;
    localized_string(int l,const std::string & s): language(l),chaine(s) {}
    localized_string(): language(0),chaine("") {}
  };

  inline bool operator < (const localized_string & l1,const localized_string & l2){
    return l1.language!=l2.language?l1.language<l2.language:l1.chaine<l2.chaine;
  }

  struct indexed_string {
    int index; 
    std::string chaine;
    indexed_string():index(0) {}
    indexed_string(int l,const std::string & s): index(l),chaine(s) {}
  };

  bool operator < (const indexed_string & is1,const indexed_string & is2);

  struct aide { 
    std::string cmd_name;
    std::string syntax;
    std::vector<localized_string> blabla;
    std::vector<std::string> examples;
    std::vector<localized_string> synonymes;
    std::vector<indexed_string> related;
    int language;
    aide():language(0){};
  };

  bool alpha_order(const aide & a1,const aide & a2);

  std::string printint(int i);
  int score(const std::string & s,const std::string & t);
  std::vector<aide> readhelp(const char * f_name,int & count,bool warn=true);
  void readhelp(std::vector<aide> & v,const char * f_name,int & count,bool warn);
  aide helpon(const std::string & demande,const std::vector<aide> & v,int language,int count,bool with_op=true);
  std::string writehelp(const aide & cur_aide,int language);

#ifndef RTOS_THREADX
  extern std::multimap<std::string,std::string> html_mtt,html_mall;
  extern std::vector<std::string> html_vtt,html_vall;

  // Find (recurs.) files named index.html/index.htm in the subdir directory
  // Extract couples of indexed names and corresponding URL
  // by searching a <A then HREF, removes TT
  void find_all_index(const std::string & subdir,std::multimap<std::string,std::string> & mtt,std::multimap<std::string,std::string> & mall);
  // Return all HTML nodes refered to s in mtt
  std::vector<std::string> html_help(std::multimap<std::string,std::string> & mtt,const std::string & s);
  std::string html_help_init(const char * arg,int language,bool verbose=true,bool force_rebuild=false);
#endif
  std::string xcasroot_dir(const char * arg);

  std::string localize(const std::string & s,int language);
  std::string unlocalize(const std::string & s);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_HELP_H
