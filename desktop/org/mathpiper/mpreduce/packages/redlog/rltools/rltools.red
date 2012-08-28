% ----------------------------------------------------------------------
% $Id: rltools.red 1587 2012-03-02 08:03:58Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 A. Dolzmann, T. Sturm, 2010 T. Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

lisp <<
   fluid '(rltools_rcsid!* rltools_copyright!*);
   rltools_rcsid!* := "$Id: rltools.red 1587 2012-03-02 08:03:58Z thomas-sturm $";
   rltools_copyright!* := "(c) 1995-2009 A. Dolzmann, T. Sturm, 2010 T. Sturm"
>>;

module rltools;
% Redlog tools.

create!-package('(rltools ioto lto sfto),nil);

fluid '(!*rlbrkcxk);

exports ioto_prin2,ioto_tprin2,ioto_prin2t,ioto_tprin2t,ioto_prtmsg,
   ioto_cterpri,ioto_cplu,ioto_realtime,ioto_flush,ioto_datestamp,
   lto_insert,lto_insertq,lto_mergesort,lto_catsoc,lto_natsoc,lto_cassoc,
   lto_nconcn,lto_alunion,lto_almerge,lto_sconcat2,lto_sconcat,lto_at2str,
   delq,delqip,adjoin,sfto_dcontentf,sfto_dprpartf,sfto_sqfpartf,
   sfto_ucontentf,sfto_uprpartf,sfto_tsqsumf,sfto_sqfdecf,sfto_pdecf,
   sfto_updecf,sfto_decdegf,sfto_reorder,sfto_groebnerf,sfto_preducef,
   sfto_greducef,sfto_gcdf!*,sfto_gcdf,sfto_sqrtf,sfto_monfp,sfto_sqfpartz,
   sfto_zdeqn,sfto_zdgtn,sfto_zdgen;

imports groebner,groebnr2;

load!-package 'assert;
on1 'assert;

!#if (and (memq 'psl lispsystem!*) (not (getd 'modulep)))
   fluid '(!*lower loadextentions!*);

   procedure modulep(u);
      begin scalar found,ld,le,!*lower;
      	 !*lower := t;
      	 ld := loaddirectories!*;
      	 while ld and not found do <<
	    le := loadextensions!*;
	    while le and not found do <<
	       if filep bldmsg("%w%w%w",first ld,u,car first le) then
	       	  found := cdr first le;
	       	  le := rest le
	    >>;
	    ld := rest ld
      	 >>;
      	 return not null found
      end;
!#endif

if 'csl memq lispsystem!* or 'psl memq lispsystem!* then <<
   if modulep 'groebner then
      load!-package 'groebner;
   if modulep 'groebnr2 then
      load!-package 'groebnr2
>>;

!#if (memq 'psl lispsystem!*)
   fluid '(out!*);

   procedure meminfo();
      begin scalar bit,hs,hsb,cpgcp,w;
      	 if not memq('psl,lispsystem!*) then
	    return nil;
      	 prin2 "               address of nil: 0x";
      	 flushbuffer out!*;
      	 channelflush out!*;
      	 (bit := 4 * unixputn nil) where output=nil;
      	 terpri();
      	 prin2 "                address range: ";
      	 prin2 bit;
      	 prin2t " bit";
      	 hs := set_heap_size nil;
      	 prin2 "           binding stack size: ";
      	 prin2 bndstksize;
      	 prin2t " Lisp items";
      	 prin2 "                     heapsize: ";
      	 prin2 meminfocomma(hs,'!,);
      	 prin2 " Lisp items";
      	 hsb := (if eqn(bit,64) then 8 else 4) * hs;
      	 w := meminfoscale hsb;
      	 prin2 "                               ";
      	 prin2 car w;
      	 prin2 " ";
      	 prin2t cdr w;
      	 w := meminfoiscale hsb;
      	 prin2 "                               ";
      	 prin2 car w;
      	 prin2 " ";
      	 prin2t cdr w;
      	 prin2 "                     GC model: ";
      	 cpgcp := getd 'copyfromstaticheap;
      	 prin2t if cpgcp then "stop-and-copy" else "mark-and-sweep";
      	 if cpgcp then <<
	    hsb := 2 * hsb;
	    prin2 " memory allocation by 2 heaps: ";
      	    w := meminfoscale hsb;
      	    prin2 car w;
      	    prin2 " ";
      	    prin2t cdr w;
      	    w := meminfoiscale hsb;
      	    prin2 "                               ";
      	    prin2 car w;
      	    prin2 " ";
      	    prin2t cdr w;
      	 >>
      end;
!#endif

!#if (memq 'csl lispsystem!*)
   procedure meminfo();
      begin scalar bit;
      	 if not memq('csl,lispsystem!*) then
	    return nil;
      	 bit := if memq('sixty!-four,lispsystem!*) then 64 else 32;
      	 prin2 "address range: ";
      	 prin2 bit;
      	 prin2t " bit";
      end;
!#endif

procedure meminfoscale(n);
   if n >= 10^9 then
      (float(n)/10^9) . "GB"
   else if n >= 10^6 then
      (float(n)/10^6) . "MB"
   else if n >= 10^3 then
      (float(n)/10^3) . "kB"
   else
      n . "B";

procedure meminfoiscale(n);
   if n >= 2^30 then
      (float(n)/2^30) . "GiB"
   else if n >= 2^20 then
      (float(n)/2^20) . "MiB"
   else if n >= 2^10 then
      (float(n)/2^10) . "kiB"
   else
      n . "B";

procedure meminfocomma(n,comma);
   begin scalar l; integer c;
      l := '(!");
      for each d on reversip explode n do <<
	 l := car d . l;
	 c := c+1;
	 if cdr d and eqn(c,3) then <<
	    c := 0;
	    l := comma . l
	 >>
      >>;
      return compress('!" . l)
   end;

!#if (memq 'psl lispsystem!*)
   fluid '(symbolfilename!*);

   procedure rltools_trunk();
      rltools_dotdotx(symbolfilename!*,4);
!#endif

!#if (memq 'csl lispsystem!*)
   procedure rltools_trunk();
      compress('!" . append(explodec !@reduce, '(!/ !")));
!#endif

procedure rltools_dotdotx(s,n);
   if eqn(n,0) then
      s
   else
      rltools_dotdotx(rltools_dotdot s,n-1);

procedure rltools_dotdot(s);
   begin scalar w;
      w := cdr reversip explode s;
      if eqcar(w,'!/) then w := cdr w;
      repeat w := cdr w until eqcar(w,'!/);
      return compress reversip('!" . w)
   end;

procedure rltools_lpvarl(u);
   if idp u then
      {u}
   else if pairp u then
      for each v in cdr u join
 	 rltools_lpvarl v;

endmodule;  % [rltools]

end;  % of file
