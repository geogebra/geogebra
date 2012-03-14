% ----------------------------------------------------------------------
% $Id: smartev.red 84 2009-02-07 07:53:22Z thomas-sturm $
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
   fluid '(smartev_rcsid!* smartev_copyright!*);
   smartev_rcsid!* := "$Id: smartev.red 84 2009-02-07 07:53:22Z thomas-sturm $";
   smartev_copyright!* := "Copyright (c) 1999-2009 A. Dolzmann and T. Sturm"
>>;

module smartev;
% Implementation of exponent vectors additionally tagged with the total
% degree in a lazy manner.

fluid '(dip_vars!* dip_sortevcomp!* dip_sortmode!* gb_tr2count!* !*trgroebr
   !*groebstat dip_evlist!*);

load!-package 'dp;

smacro procedure sm_lmake(e);
   {e,nil,nil};

smacro procedure sm_make(e,td,bs);
   {e,td,bs};

smacro procedure sm_e(se);
   car se;

smacro procedure sm_td(se);
   cadr se;

smacro procedure sm_std(se);
   cadr se or (cadr se := ev_tdeg1 car se);

smacro procedure sm_settd(se,td);
   cadr se := td;

smacro procedure sm_bs(se);
   caddr se;

procedure ev_init();
   ;

procedure ev_sdivp(ev1,ev2);
   sm_e ev1 neq sm_e ev2 and ev_divides!?(ev1,ev2);

procedure ev_divides!?(ev1,ev2);
   ev_mtest!?(ev2,ev1);

procedure ev_revgradlexcomp(e1,e2);
   % Exponent vector reverse graduated lex compare. The exponent
   % vectors e1 and e2 are in reverse graduated lex ordering.
   % evRevGradLexcomp(e1,e2) returns the digit 0 if exponent vector e1
   % is equal exponent vector e2, the digit 1 if e1 is greater than
   % e2, else the digit -1.
   begin scalar td1,td2;
      td1 := sm_td e1;
      td2 := sm_td e2;
      if td1 and td2 then <<
         if td1 #> td2 then
            return 1;
         if td1 #< td2 then
            return -1
      >>;
      return ev_revgradlexcomp1(sm_e e1,sm_e e2,e1,e2)
   end;

procedure ev_revgradlexcomp1(e1,e2,se1,se2);
   begin scalar te1,te2;
      if null e1 then
               return 0;
      if car e1 #= car e2 then
         return ev_revgradlexcomp1(cdr e1, cdr e2,se1,se2);
      te1 := sm_std se1;
      te2 := sm_std se2;
      if te1 #= te2 then
          return ev_invlexcomp(e1,e2);
      if te1 #> te2 then
          return 1;
      return -1
   end;

procedure ev_invlexcomp(e1,e2);
   % Exponent vector inverse lexicographical compare. No term order!
   begin scalar n;
      if null e1 then
         return 0;
      if car e1 #= car e2 then
          return ev_invlexcomp(cdr e1,cdr e2);  % sic!
      n := ev_invlexcomp(cdr e1,cdr e2);
      if not (n #= 0) then
          return n;
      if car e2 #= car e1 then
          return 0;
      if car e2 #> car e1 then
          return 1;
      return -1
   end;

procedure ev_mtest!?(e1,e2);
   if sm_td e1 and sm_td e2 and sm_td e1 #< sm_td e2 then <<
%      ioto_prin2 "*";
      nil
   >> else <<
%      ioto_prin2 ".";
      ev_mtest!?1(sm_e e1,sm_e e2)
   >>;

procedure ev_mtest!?1(e1,e2);
   % Exponent vector multiple test. e1 and e2 are compatible exponent
   % vectors. vevmtest?(e1,e2) returns a boolean expression. True if
   % exponent vector e1 is a multiple of exponent vector e2, else
   % false.
   begin scalar r;
      r := t;
      while e1 and r do <<
         if car e1 #< car e2 then
            e1 := r := nil
         else <<
            e1 := cdr e1;
            e2 := cdr e2
         >>
      >>;
      return r
   end;

procedure ev_2a(e);
   % Returns list of prefix equivalents of exponent vector e.
   ev_2a1(sm_e e,dip_vars!*);

procedure ev_2a1(u,v);
   if null u then
      nil
   else if car u #= 0 then
      ev_2a1(cdr u,cdr v)
   else if car u #= 1 then
      car v . ev_2a1(cdr u,cdr v)
   else
      {'expt,car v,car u} . ev_2a1(cdr u,cdr v);

procedure ev_2f(ev,vars);
   ev_2f1(sm_e ev,vars);

procedure ev_2f1(ev,vars);
   if null ev then
      1
   else if car ev #= 0 then
      ev_2f1(cdr ev,cdr vars)
   else
      multf(car vars .** car ev .* 1 .+ nil,ev_2f1(cdr ev,cdr vars));

procedure ev_lcm(e1,e2);
   sm_lmake ev_lcm1(sm_e e1,sm_e e2);

procedure ev_lcm1(e1,e2);
   % Exponent vector least common multiple. e1 and e2 are exponent
   % vectors. ev_lcm(e1,e2) computes the least common multiple of the
   % exponent vectors e1 and e2, and returns an exponent vector.
   begin scalar x;
      while e1 do <<
         x := (if car e1 #> car e2 then car e1 else car e2) . x;
         e1 := cdr e1;
          e2 := cdr e2
      >>;
      return reversip x
   end;

procedure ev_zero();
   sm_make(for each x in dip_vars!* collect 0,0,nil);

procedure ev_zero!?(ev);
   sm_td ev #= 0 or ev_zero!?1 sm_e ev;

procedure ev_zero!?1(ev);
   null ev or eqcar(ev,0) and ev_zero!?1 cdr ev;

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
   sm_lmake ev_insert1(sm_e ev,v,dg,vars);

procedure ev_insert1(ev,v,dg,vars);
   % f to dip conversion: Insert the "dg" into the ev in the place of
   % variable v.
   if null ev or null vars then
      nil
   else if car vars eq v then
      dg . cdr ev
   else
      car ev . ev_insert1(cdr ev,v,dg,cdr vars);

procedure ev_tdeg(u);
   sm_std u;

procedure ev_tdeg1(u);
   % calculate the total degree of u.
   begin integer x;
      while u do <<
         x := car u #+ x;
          u := cdr u
      >>;
      return x
   end;

procedure ev_dif(e1,e2);
   if sm_td e1 and sm_td e2 then
      sm_make(ev_dif1(sm_e e1,sm_e e2),sm_td e1 #- sm_td e2,nil)
   else
      sm_lmake ev_dif1(sm_e e1,sm_e e2);

procedure ev_dif1(e1,e2);
   begin scalar s;
      while e1 do <<
         s := (car e1 #- car e2) . s;
         e1 := cdr e1;
         e2 := cdr e2
      >>;
      return reversip s
   end;

procedure ev_sum(e1,e2);
   if sm_td e1 and sm_td e2 then
      sm_make(ev_sum1(sm_e e1,sm_e e2),sm_td e1 #+ sm_td e2,nil)
   else
      sm_lmake ev_sum1(sm_e e1,sm_e e2);

procedure ev_sum1(e1,e2);
   begin scalar s;
      while e1 do <<
         s := (car e1 #+ car e2) . s;
         e1 := cdr e1;
         e2 := cdr e2
      >>;
      return reversip s
   end;

procedure ev_disjointp(e1,e2);
   ev_disjointp1(sm_e e1,sm_e e2);

procedure ev_disjointp1(e1,e2);
   % nonconstructive test of lcm(e1,e2) = e1 + e2 equivalent: no
   % matches of nonzero elements.
   if null e1 then
      t
   else if (car e1 neq 0) and (car e2 neq 0) then
      nil
   else
      ev_disjointp1(cdr e1,cdr e2);

procedure ev_member(a,l);
   assoc(car a,l);

procedure ev_sdivp(ev1,ev2);
   sm_e ev1 neq sm_e ev2 and ev_divides!?(ev1,ev2);

procedure ev_identify(oev,nev);
   <<
      if not sm_td nev and sm_td oev then
               sm_settd(nev,sm_td oev);
      nev
   >>;

endmodule;  % smartev

end;  % of file
