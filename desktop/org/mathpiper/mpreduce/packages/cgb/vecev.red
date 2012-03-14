% ----------------------------------------------------------------------
% $Id: vecev.red 84 2009-02-07 07:53:22Z thomas-sturm $
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
   fluid '(vecev_rcsid!* vecev_copyright!*);
   vecev_rcsid!* := "$Id: vecev.red 84 2009-02-07 07:53:22Z thomas-sturm $";
   vecev_copyright!* := "Copyright (c) 1999-2009 A. Dolzmann and T. Sturm"
>>;

module ev;
% Implementation of exponent vectors based on standard lisp vectors.

fluid '(dip_vars!* dip_sortevcomp!* ev_ub!* kord!* dip_sortmode!*);

procedure ev_init();
   ev_ub!* := length dip_vars!* - 1;

procedure ev_divides!?(ev1,ev2);
   ev_mtest!?(ev2,ev1);

procedure ev_sdivp(ev1,ev2);
   ev1 neq ev2 and ev_divides!?(ev1,ev2);

procedure ev_revgradlexcomp(e1,e2);
   % Exponent vector reverse graduated lex compare. The exponent
   % vectors e1 and e2 are in reverse graduated lex ordering.
   % evRevGradLexcomp(e1,e2) returns the digit 0 if exponent vector e1
   % is equal exponent vector e2, the digit 1 if e1 is greater than
   % e2, else the digit -1.
   begin scalar r; integer i,j,tdeg1,tdeg2;
      r := t;
      i := ev_ub!*;
      while r and not(i #< 0) do
         if not(getv(e1,i) #= getv(e2,i)) then
            r := nil
         else
            i := i #- 1;
      if r then
	 return 0;
      while not(j #> i) do <<
	 tdeg1 := tdeg1 #+ getv(e1,j);
	 tdeg2 := tdeg2 #+ getv(e2,j);
         j := j #+ 1
      >>;
      if tdeg1 #> tdeg2 then
	 return 1;
      if tdeg1 #< tdeg2 then
	 return -1;
      if getv(e1,i) #> getv(e2,i) then
         return -1;
      return 1
   end;

procedure ev_mtest!?(e1,e2);
   % Exponent vector multiple test. e1 and e2 are compatible exponent
   % vectors. vevmtest?(e1,e2) returns a boolean expression. True if
   % exponent vector e1 is a multiple of exponent vector e2, else
   % false.
   begin scalar r; integer i;
      r := t;
      while r and not(i #> ev_ub!*) do
         if getv(e1,i) #< getv(e2,i) then
            r := nil
         else
            i := i #+ 1;
      return r
   end;

procedure ev_2a(e);
   begin scalar r,w,sv; integer i;
      sv := dip_vars!*;
      while not(i #> ev_ub!*) do <<
         w := getv(e,i);
         if w #= 1 then
            r := car sv . r
         else if w #> 1 then
            r := {'expt,car sv,w} . r;
         sv := cdr sv;
         i := i #+ 1
      >>;
      return reversip r
   end;

procedure ev_2f(ev,vars);
   begin scalar r,w,sv; integer i;
      sv := dip_vars!*;
      while not(i #> ev_ub!*) do <<
         w := getv(ev,i);
         if not(w #= 0) then
            multf(r,car sv .** w .* 1 .+ nil);
         sv := cdr sv;
         i := i #+ 1
      >>;
      return r
   end;

procedure ev_lcm(e1,e2);
   % Exponent vector least common multiple. e1 and e2 are exponent
   % vectors. ev_lcm(e1,e2) computes the least common multiple of the
   % exponent vectors e1 and e2, and returns an exponent vector.
   begin scalar s; integer i;
      s := mkvect ev_ub!*;
      while not(i #> ev_ub!*) do <<
         putv(s,i,max!#(getv(e1,i),getv(e2,i)));
         i := i #+ 1
      >>;
      return s
   end;

procedure min!#(a,b);
   if a #< b then a else b;

procedure ev_zero();
   begin scalar v; integer i;
      v := mkvect ev_ub!*;
      while not(i #> ev_ub!*) do <<
         putv(v,i,0);
         i := i #+ 1
      >>;
      return v
   end;

procedure ev_zero!?(ev);
   begin scalar r; integer i;
      r := t;
      while r and not(i #> ev_ub!*) do
         if not(getv(ev,i) #= 0) then
            r := nil
         else
            i := i #+ 1;
      return r
   end;

procedure ev_compless!?(e1,e2);
   ev_comp(e2,e1) #= 1;

procedure ev_comp(e1,e2);
   % Exponent vector compare. e1, e2 are exponent vectors in some
   % order. Evcomp(e1,e2) returns the digit 0 if exponent vector e1 is
   % equal exponent vector e2, the digit 1 if e1 is greater than e2,
   % else the digit -1. This function is assigned a value by the
   % ordering mechanism, so is dummy for now. IDapply would be better
   % here, but is not within standard LISP!
   apply(dip_sortevcomp!*,{e1,e2});

procedure ev_insert(ev,v,dg,vars);
   % f to dip conversion: Insert the "dg" into the ev in the place of
   % variable v.
   begin scalar vv;
      vv := ev_cpv ev;
      putv(vv,ev_ub!* - length memq(v,vars) + 1,dg);
      return vv
   end;

procedure ev_cpv(v);
   begin scalar vv; integer i;
      vv := mkvect ev_ub!*;
      while not(i #> ev_ub!*) do <<
         putv(vv,i,getv(v,i));
         i := i #+ 1
      >>;
      return vv
   end;

procedure ev_tdeg(u);
   % calculate the total degree of u.
   begin integer x,i;
      while not(i #> ev_ub!*) do <<
         x := getv(u,i) #+ x;
         i := i #+ 1
      >>;
      return x
   end;

procedure ev_dif(e1,e2);
   begin scalar s; integer i;
      s := mkvect ev_ub!*;
      while not(i #> ev_ub!*) do <<
         putv(s,i,getv(e1,i) #- getv(e2,i));
         i := i #+ 1
      >>;
      return s
   end;

procedure ev_sum(e1,e2);
   begin scalar s; integer i;
      s := mkvect ev_ub!*;
      while not(i #> ev_ub!*) do <<
         putv(s,i,getv(e1,i) #+ getv(e2,i));
         i := i #+ 1
      >>;
      return s
   end;

procedure ev_disjointp(e1,e2);
   % nonconstructive test of lcm(e1,e2) = e1 + e2 equivalent: no
   % matches of nonzero elements.
   begin scalar r; integer i;
      r := t;
      while r and not(i #> ev_ub!*) do
         if not(getv(e1,i) #=0) and not(getv(e2,i) #=0) then
            r := nil
         else
            i := i #+ 1;
      return r
   end;

procedure ev_member(ev,evl);
   ev member evl;

procedure ev_identify(oev,nev);
   nev;

endmodule;  % vecev

end;  % of file
