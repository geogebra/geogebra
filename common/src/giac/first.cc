/* -*- compile-command: "g++ -g -c -I.. first.cc -DHAVE_CONFIG_H -DIN_GIAC  -DGIAC_CHECK_NEW" -*-
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
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
int init_gmp_memory::refcount = 0;
init_gmp_memory init_gmp_memory_instance;

#ifdef HAVE_LIBGC
#include <new>
#define GC_DEBUG
#include <gc_cpp.h>
void* operator new(std::size_t size)
{
	return GC_MALLOC_UNCOLLECTABLE( size );
}
  
void operator delete(void* obj)
{
	GC_FREE(obj);
}
  
void* operator new[](std::size_t size)
{
	return GC_MALLOC_UNCOLLECTABLE(size);
}
  
void operator delete[](void* obj)
{
	GC_FREE(obj);
}

static void* RS_gmpalloc(size_t a)
{
	return GC_malloc_atomic(a);
}

static void* RS_gmprealloc(void* old_p, size_t old_size, size_t new_size)
{
	void* tmp = GC_realloc(old_p, new_size);
	return tmp;
}

static void RS_gmpfree(void * old_p,size_t old_size)
{

}

init_gmp_memory::init_gmp_memory()
{
	if (refcount++ == 0)
		mp_set_memory_functions(RS_gmpalloc, RS_gmprealloc, RS_gmpfree);
}

init_gmp_memory::~init_gmp_memory()
{
	if (--refcount == 0) {
		// XXX: do I need to clean up something here?
	}
}

#else
init_gmp_memory::init_gmp_memory() { }
init_gmp_memory::~init_gmp_memory() { }
 
#include <new>
#include <cstdlib>
#include <stdexcept>

  
#ifdef GIAC_CHECK_NEW

#include <iostream>

unsigned long giac_allocated = 0;
void* operator new(std::size_t size)
{
  std::cerr << giac_allocated << " + " << size << std::endl;
  giac_allocated += size;
  void * p =  std::malloc(size);  
  if(!p) {
    std::bad_alloc ba;
    throw ba;
  }
  return p;
}
  
void* operator new[](std::size_t size)
{
  std::cerr << giac_allocated << " + [] " << size << std::endl;
  giac_allocated += size;
  void * p =  std::malloc(size);  
  if(!p) {
    std::bad_alloc ba;
    throw ba;
  }
  return p;
}
  
void operator delete[](void* obj)
{
  free(obj);
}
#endif

#endif

