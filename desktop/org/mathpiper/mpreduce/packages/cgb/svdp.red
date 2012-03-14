% ----------------------------------------------------------------------
% $Id: svdp.red 84 2009-02-07 07:53:22Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1999-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(svdp_rcsid!* svdp_copyright!*);
   svdp_rcsid!* := "$Id: svdp.red 84 2009-02-07 07:53:22Z thomas-sturm $";
   svdp_copyright!* := "Copyright (c) 1999-2009 A. Dolzmann and T. Sturm"
>>;

module svdp;
% Sound (small) vdp.
% Implementation of vdp's containing only three fields for the dip, the
% vdp number and the sugar.

load!-package 'dp;

fluid '(!*gsugar vdp_pcount!*);

procedure vdp_lbc(u);
   cadar u;

procedure vdp_evlmon(u);
   caar u;

procedure vdp_poly(u);
   car u;

procedure vdp_zero!?(u);
   null car u;

procedure vdp_number(f);
   cadr f;

procedure vdp_sugar(f);
   if car f then cddr f else 0;

procedure vdp_unit!?(p);
   car p and ev_zero!? caar p;

procedure vdp_tdeg(u);
   dip_tdeg car u;

procedure vdp_fdip(u);
   u . ('invalid . 'invalid);

procedure vdp_appendmon(vdp,coef,vev);
   % Add a monomial to the end of a vdp (vev remains unchanged).
   if null car vdp then
      vdp_fmon(coef,vev)
   else if bc_zero!? coef then
      vdp
   else
      vdp_fdip dip_appendmon(vdp_poly vdp,coef,vev);

procedure vdp_nconcmon(vdp,coef,vev);
   if null car vdp then
      vdp_fmon(coef,vev)
   else if bc_zero!? coef then
      vdp
   else
      vdp_fdip dip_nconcmon(vdp_poly vdp,coef,vev);

procedure vdp_bcquot(p,c);
   begin scalar r;
      r := vdp_fdip dip_bcquot(vdp_poly p,c);
      if !*gsugar then
 	 vdp_setsugar(r,vdp_sugar p);
      return r
   end;

procedure vdp_content(p);
   dip_contenti vdp_poly p;

procedure vdp_content1(d,c);
   dip_contenti1(vdp_poly d,c);

procedure vdp_length(f);
   dip_length vdp_poly f;

procedure vdp_bcprod(p,b);
   begin scalar r;
      r := vdp_fdip dip_bcprod(vdp_poly p,b);
      if !*gsugar then
 	 vdp_setsugar(r,vdp_sugar p);
      return r
   end;

procedure vdp_cancelmev(p,vev);
   begin scalar r;
      r := vdp_fdip dip_cancelmev(vdp_poly p,vev);
      if !*gsugar then
 	 vdp_setsugar(r,vdp_sugar p);
      return r
   end;

procedure vdp_sum(d1,d2);
   begin scalar r;
      r := vdp_fdip dip_sum(vdp_poly d1,vdp_poly d2);
      if !*gsugar then
	 % 	 vdp_setsugar(r,max(vdp_sugar d1,vdp_sugar d2));
 	 vdp_setsugar(r,max!#(vdp_sugar d1,vdp_sugar d2));
      return r
   end;

procedure max!#(a,b);
   if a #> b then a else b;

procedure vdp_prod(d1,d2);
   begin scalar r;
      r := vdp_fdip dip_prod(vdp_poly d1,vdp_poly d2);
      if !*gsugar then
 	 vdp_setsugar(r,vdp_sugar d1 #+ vdp_sugar d2);
      return r
   end;

procedure vdp_zero();
   vdp_fdip nil;

procedure vdp_mred(u);
   begin scalar r;
      r := vdp_fdip dip_mred vdp_poly u;
      if !*gsugar then
	 vdp_setsugar(r,vdp_sugar u);
      return r
   end;

procedure vdp_condense(f);
   dip_condense vdp_poly f;

procedure vdp_setsugar(p,s);
   <<
      cddr p := s;
      p
   >>;

procedure vdp_setnumber(p,n);
   <<
      cadr p := n;
      p
   >>;

procedure vdp_fmon(coef,vev);
   begin scalar r;
      r := vdp_fdip dip_fmon(coef,vev);
      if !*gsugar then
 	 vdp_setsugar(r,ev_tdeg vev);
      return r
   end;

procedure vdp_2a(u);
   dip_2a vdp_poly u;

procedure vdp_2f(u);
   dip_2f vdp_poly u;

procedure vdp_init(vars);
   % Initializing vdp-dip polynomial package.
   dip_init vars;

procedure vdp_cleanup();
   dip_cleanup();

procedure vdp_f2vdp(u);
   vdp_fdip dip_f2dip u;

procedure vdp_enumerate(f);
   % f is a temporary result. Prepare it for medium range storage and
   % assign a number.
   if vdp_zero!? f or vdp_number f then
      f
   else
      vdp_setnumber(f,vdp_pcount!* := vdp_pcount!* #+ 1);

procedure vdp_simpcont(p);
   begin scalar q;
      q := vdp_poly p;
      if null q then
 	 return p;
      return vdp_fdip dip_simpcont q
   end;

procedure vdp_lsort(pl);
   % Distributive polynomial list sort. pl is a list of distributive
   % polynomials. vdplsort(pl) returns the sorted distributive
   % polynomial list of pl.
   sort(pl,function vdp_evlcomp);

procedure vdp_evlcomp(p1,p2);
   dip_evlcomp(vdp_poly p1,vdp_poly p2);

procedure vdp_ilcomb1(v1,c1,t1,v2,c2,t2);
   begin scalar r;
      r := vdp_fdip dip_ilcomb1(vdp_poly v1,c1,t1,vdp_poly v2,c2,t2);
      if !*gsugar then
 	 vdp_setsugar(r,max!#(vdp_sugar v1 #+ ev_tdeg t1,vdp_sugar v2 #+ ev_tdeg t2));
      return r
   end;

procedure vdp_ilcomb1r(v1,c1,v2,c2,t2);
   begin scalar r;
      r := vdp_fdip dip_ilcomb1r(vdp_poly v1,c1,vdp_poly v2,c2,t2);
      if !*gsugar then
 	 vdp_setsugar(r,max!#(vdp_sugar v1,vdp_sugar v2 #+ ev_tdeg t2));
      return r
   end;

procedure vdp_make(vbc,vev,form);
   rederr "vdp_make not supported by fast vdp";

procedure vdp_putprop(poly,prop,val);
   rederr "vdp_putrop not supported by fast vdp";

procedure vdp_getprop(poly,prop);
   rederr "vdp_getprop not supported by fast vdp";

endmodule;  % svdp

end;  % of file
