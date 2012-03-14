module matop;

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


COMMENT

              #############################
              ####                     ####
              ####  MATRIX OPERATIONS  ####
              ####                     ####
              #############################


This module contains operations on dpmats, that correspond to module
operations on the corresponding images resp. cokernels.

END COMMENT;

symbolic procedure matop!=testdpmatlist l;
% Test l to be a list of dpmats embedded into a common free module.
  if null l then rederr"Empty DPMAT list"
  else begin scalar c,d;
    for each x in l do
        if not eqcar(x,'dpmat) then typerr(x,"DPMAT");
    c:=dpmat_cols car l; d:=dpmat_coldegs car l;
    for each x in cdr l do
      if not (eqn(c,dpmat_cols x) and equal(d,dpmat_coldegs x)) then
                rederr"Matrices don't match in the DPMAT list";
  end;

symbolic procedure matappend!* l;
% Appends rows of the dpmats in the dpmat list l.
  (begin scalar u,r;
      matop!=testdpmatlist l;
      cali!=degrees:=dpmat_coldegs car l;
      u:=dpmat_list car l; r:=dpmat_rows car l;
      for each y in cdr l do
        << u:=append(u, for each x in dpmat_list y collect
                        bas_newnumber(bas_nr x + r,x));
           r:=r + dpmat_rows y;
        >>;
      return dpmat_make(r,dpmat_cols car l,u,cali!=degrees,nil)
   end) where cali!=degrees:=cali!=degrees;

put('matappend,'psopfn,'matop!=matappend);
symbolic procedure matop!=matappend l;
% Append the dpmats in the list l.
  dpmat_2a matappend!* for each x in l collect dpmat_from_a reval x;

symbolic procedure mat2list!* m;
% Returns the ideal of all elements of m.
  if dpmat_cols m = 0 then m
  else (begin scalar x;
    x:=bas_renumber bas_zerodelete
        for i:=1:dpmat_rows m join
        for j:=1:dpmat_cols m collect
                bas_make(0,dpmat_element(i,j,m));
    return dpmat_make(length x,0,x,nil,
                if dpmat_cols m=1 then dpmat_gbtag m else nil)
    end) where cali!=degrees:=nil;

symbolic procedure matsum!* l;
% Returns the module sum of the dpmat list l.
  interreduce!* matappend!* l;

put('matsum,'psopfn,'matop!=matsum);
put('idealsum,'psopfn,'matop!=matsum);
symbolic procedure matop!=matsum l;
% Returns the sum of the ideals/modules in the list l.
  dpmat_2a matsum!* for each x in l collect dpmat_from_a reval x;

symbolic procedure matop!=idealprod2(a,b);
  if (dpmat_cols a > 0) or (dpmat_cols b > 0 ) then
                rederr"IDEALPROD only for ideals"
  else (begin scalar x;
    x:=bas_renumber
        for each a1 in dpmat_list a join
        for each b1 in dpmat_list b collect
            bas_make(0,dp_prod(bas_dpoly a1,bas_dpoly b1));
    return interreduce!* dpmat_make(length x,0,x,nil,nil)
    end) where cali!=degrees:=nil;

symbolic procedure idealprod!* l;
% Returns the product of the ideals in the dpmat list l.
 if null l then rederr"empty list in IDEALPROD"
 else if length l=1 then car l
 else begin scalar u;
    u:=car l;
    for each x in cdr l do u:=matop!=idealprod2(u,x);
    return u;
    end;

put('idealprod,'psopfn,'matop!=idealprod);
symbolic procedure matop!=idealprod l;
% Returns the product of the ideals in the list l.
  dpmat_2a idealprod!* for each x in l collect dpmat_from_a reval x;

symbolic procedure idealpower!*(a,n);
  if (dpmat_cols a > 0) or (not fixp n) or (n < 0) then
        rederr" Syntax : idealpower(ideal,integer)"
  else if (n=0) then dpmat_from_dpoly dp_fi 1
  else begin scalar w; w:=a;
  for i:=2:n do w:=matop!=idealprod2(w,a);
  return w;
  end;

symbolic operator idealpower;
symbolic procedure idealpower(m,l);
  if !*mode='algebraic then
        dpmat_2a idealpower!*(dpmat_from_a reval m,l)
  else idealpower!*(m,l);

symbolic procedure matop!=shiftdegs(d,n);
% Shift column degrees d n places.
   for each x in d collect ((car x + n) . cdr x);

symbolic procedure directsum!* l;
% Returns the direct sum of the modules in the dpmat list l.
  if null l then rederr"Empty DPMAT list"
  else (begin scalar r,c,u;
    for each x in l do
        if not eqcar(x,'dpmat) then typerr(x,"DPMAT")
        else if dpmat_cols x=0 then
                rederr"DIRECTSUM only for modules";
    c:=r:=0; % Actual column resp. row index.
    cali!=degrees:=nil;
    for each x in l do
       << cali!=degrees:=append(cali!=degrees,
                        matop!=shiftdegs(dpmat_coldegs x,c));
          u:=append(u, for each y in dpmat_list x collect
                bas_make(bas_nr y + r,dp_times_ei(c,bas_dpoly y)));
          r:=r + dpmat_rows x;
          c:=c + dpmat_cols x;
        >>;
    return dpmat_make(r,c,u,cali!=degrees,nil)
    end) where cali!=degrees:=cali!=degrees;

put('directsum,'psopfn,'matop!=directsum);
symbolic procedure matop!=directsum l;
% Returns the direct sum of the modules in the list l.
  dpmat_2a directsum!* for each x in l collect dpmat_from_a reval x;

symbolic operator deleteunits;
symbolic procedure deleteunits m;
  if !*noetherian then m
  else if !*mode='algebraic then dpmat_2a deleteunits!* dpmat_from_a m
  else deleteunits!* m;

symbolic procedure deleteunits!* m;
% Delete units from the base elements of the ideal m.
  if !*noetherian or (dpmat_cols m>0) then m
  else dpmat_make(dpmat_rows m,0,
        for each x in dpmat_list m collect
                bas_factorunits x,nil,dpmat_gbtag m);

symbolic procedure interreduce!* m;
  (begin scalar u;
  u:=red_interreduce dpmat_list m;
  return dpmat_make(length u, dpmat_cols m, bas_renumber u,
                cali!=degrees, dpmat_gbtag m)
  end)  where cali!=degrees:=dpmat_coldegs m;

symbolic operator interreduce;
symbolic procedure interreduce m;
% Interreduce m.
  if !*mode='algebraic then
        dpmat_2a interreduce!* dpmat_from_a reval m
  else interreduce!* m;

symbolic procedure gbasis!* m;
% Produce a minimal Groebner or standard basis of the dpmat m.
  if dpmat_gbtag m then m else car groeb_stbasis(m,t,nil,nil);

put('tangentcone,'psopfn,'matop!=tangentcone);
symbolic procedure matop!=tangentcone m;
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  c:=tangentcone!* c;
  return dpmat_2a c;
  end;

symbolic procedure tangentcone!* m;
% Returns the tangent cone of m, provided the term order has degrees.
% m must be a gbasis.
  if null ring_degrees cali!=basering then
        rederr"tangent cone only for degree orders defined"
  else (begin scalar b;
  b:=for each x in dpmat_list m collect
    bas_make(bas_nr x,dp_tcpart bas_dpoly x);
  return dpmat_make(dpmat_rows m,
        dpmat_cols m,b,cali!=degrees,dpmat_gbtag m);
  end)  where cali!=degrees:=dpmat_coldegs m;


symbolic procedure syzygies1!* bas;
% Returns the (not yet interreduced first) syzygy module of the dpmat
% bas.
  begin
  if cali_trace() > 0 then
    << terpri(); write" Compute syzygies"; terpri() >>;
  return third groeb_stbasis(bas,nil,nil,t);
  end;

symbolic procedure syzygies!* bas;
% Returns the interreduced syzygy basis.
  interreduce!* syzygies1!* bas;

symbolic procedure normalform!*(a,b);
% Returns {a1,r,z} with a1=z*a-r*b where the rows of the dpmat a1 are
% the normalforms of the rows of the dpmat a with respect to the
% dpmat b.
   if not(eqn(dpmat_cols a,dpmat_cols b) and
        equal(dpmat_coldegs a,dpmat_coldegs b)) then
                rederr"dpmats don't match for NORMALFORM"
   else (begin scalar a1,z,u,r;
      bas_setrelations dpmat_list b;
      a1:=for each x in dpmat_list a collect
        << u:=red_redpol(dpmat_list b,x);
           z:=bas_make(bas_nr x,dp_times_ei(bas_nr x,cdr u)).z;
           car u
        >>;
      r:=bas_getrelations a1; bas_removerelations a1;
      bas_removerelations dpmat_list b; z:=reversip z;
      a1:=dpmat_make(dpmat_rows a,dpmat_cols a,a1,cali!=degrees,nil);
      cali!=degrees:=dpmat_rowdegrees b;
      r:=dpmat_make(dpmat_rows a,dpmat_rows b,bas_neworder r,
                            cali!=degrees,nil);
      cali!=degrees:=nil;
      z:=dpmat_make(dpmat_rows a,dpmat_rows a,bas_neworder z,nil,nil);
      return  {a1,r,z};
      end)  where cali!=degrees:=dpmat_coldegs b;

symbolic procedure matop_pseudomod(a,b); car mod!*(a,b);

symbolic procedure mod!*(a,b);
% Returns the normal form of the dpoly a modulo the dpmat b and the
% corresponding unit produced during pseudo division.
  (begin scalar u;
      a:=dp_neworder a; % to be on the safe side.
      u:=red_redpol(dpmat_list b,bas_make(0,a));
      return (bas_dpoly car u) . cdr u;
  end)  where cali!=degrees:=dpmat_coldegs b;

symbolic operator mod;
symbolic procedure mod(a,b);
% True normal form as s.q. also for matrices.
  if !*mode='symbolic then rederr"only for algebraic mode"
  else begin scalar u;
    b:=dpmat_from_a reval b; a:=reval a;
    if eqcar(a,'list) then
        if dpmat_cols b>0 then rederr"entries don't match for MOD"
        else a:=makelist for each x in cdr a collect
           << u:=mod!*(dp_from_a x, b);
              {'quotient,dp_2a car u,dp_2a cdr u}
           >>
    else if eqcar(a,'mat) then
        begin a:=dpmat_from_a a;
        if dpmat_cols a neq dpmat_cols b then
                rederr"entries don't match for MOD";
        a:=for each x in dpmat_list a collect mod!*(bas_dpoly x,b);
        a:='mat.
            for each x in a collect
              << u:=dp_2a cdr x;
                 for i:=1:dpmat_cols b collect
                    {'quotient,dp_2a dp_comp(i,car x),u}
              >>
        end
    else if dpmat_cols b>0 then rederr"entries don't match for MOD"
    else << u:=mod!*(dp_from_a a, b);
            a:={'quotient,dp_2a car u,dp_2a cdr u}
          >>;
    return a;
    end;

infix mod;

symbolic operator normalform;
symbolic procedure normalform(a,b);
% Compute a normal form of the rows of a with respect to b :
%   first result = third result * a + second result * b.
  if !*mode='algebraic then
  begin scalar m;
  m:= normalform!*(dpmat_from_a reval a,dpmat_from_a reval b);
  return {'list,dpmat_2a car m, dpmat_2a cadr m, dpmat_2a caddr m}
  end
  else normalform!*(a,b);

symbolic procedure eliminate!*(m,vars);
% Returns a (dpmat) basis of the elimination module of the dpmat m
% eliminating variables contained in the var. list vars.
% It sets temporary the standard elimination term order, but doesn't
% affect the ecart, and computes a Groebner basis of m.

%  if dpmat_gbtag m and eo(vars) then dpmat_sieve(m,vars,t) else

   (begin scalar c,e,bas,v;
   c:=cali!=basering; e:=ring_ecart c;
   v:=ring_names cali!=basering;
   setring!* ring_define(v,eliminationorder!*(v,vars),'revlex,e);
   cali!=degrees:=nil; % No degrees for proper result !!
   bas:=(bas_sieve(dpmat_list
            car groeb_stbasis(dpmat_neworder(m,nil),t,nil,nil), vars)
            where !*noetherian=t);
   setring!* c; cali!=degrees:=dpmat_coldegs m;
   return dpmat_make(length bas,dpmat_cols m,bas_neworder bas,
                            cali!=degrees,nil);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic operator eliminate;
symbolic procedure eliminate(m,l);
% Returns the elimination ideal/module of m with respect to the
% variables in the list l to be eliminated.
  if !*mode='algebraic then
  begin l:=reval l;
    if not eqcar(l,'list) then typerr(l,"variable list");
    m:=dpmat_from_a m; l:=cdr l;
    return dpmat_2a eliminate!*(m,l);
  end
  else eliminate!*(m,l);

symbolic procedure matintersect!* l;
  if null l then rederr"MATINTERSECT with empty list"
  else if length l=1 then car l
  else (begin scalar c,u,v,p,size;
    matop!=testdpmatlist l;
    size:=dpmat_cols car l;
    v:=for each x in l collect make_cali_varname();
    c:=cali!=basering;
    setring!* ring_sum(c,
        ring_define(v,degreeorder!* v,'lex,for each x in v collect 1));
    cali!=degrees:=mo_degneworder dpmat_coldegs car l;
    u:=for each x in pair(v,l) collect
        dpmat_times_dpoly(dp_from_a car x,dpmat_neworder(cdr x,nil));
    p:=dp_fi 1; for each x in v do p:=dp_diff(p,dp_from_a x);
    if size=0 then p:=dpmat_from_dpoly p
    else p:=dpmat_times_dpoly(p,dpmat_unit(size,cali!=degrees));
    p:=gbasis!* matsum!* (p . u);
    p:=dpmat_sieve(p,v,t);
    setring!* c;
    cali!=degrees:=dpmat_coldegs car l;
    return dpmat_neworder(p,t);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

put('matintersect,'psopfn,'matop!=matintersect);
put('idealintersect,'psopfn,'matop!=matintersect);
symbolic procedure matop!=matintersect l;
% Returns the intersection of the submodules of a fixed free module
% in the list l.
  dpmat_2a matintersect!* for each x in l collect dpmat_from_a reval x;


% ------- Submodule property and equality test --------------

put('modequalp,'psopfn,'matop!=equalp);
% Test, whether a and b are module equal.
symbolic procedure matop!=equalp u;
  if length u neq 2 then rederr"Syntax : MODEQUALP(dpmat,dpmat) "
  else begin scalar a,b;
    intf_get first u; intf_get second u;
    if null(a:=get(first u,'gbasis)) then
        put(first u,'gbasis,a:=gbasis!* get(first u,'basis));
    if null(b:=get(second u,'gbasis)) then
        put(second u,'gbasis,b:=gbasis!* get(second u,'basis));
    if modequalp!*(a,b) then return 'yes else return 'no
    end;

symbolic procedure modequalp!*(a,b);
  submodulep!*(a,b) and submodulep!*(b,a);

put('submodulep,'psopfn,'matop!=submodulep);
% Test, whether a is a submodule of b.
symbolic procedure matop!=submodulep u;
  if length u neq 2 then rederr"Syntax : SUBMODULEP(dpmat,dpmat)"
  else begin scalar a,b;
    intf_get second u;
    if null(b:=get(second u,'gbasis)) then
        put(second u,'gbasis,b:=gbasis!* get(second u,'basis));
    a:=dpmat_from_a reval first u;
    if submodulep!*(a,b) then return 'yes else return 'no
    end;

symbolic procedure submodulep!*(a,b);
  if not(dpmat_cols a=dpmat_cols b
     and equal(dpmat_coldegs a,dpmat_coldegs b)) then
    rederr"incompatible modules in SUBMODULEP"
  else (begin
    a:=for each x in dpmat_list a collect bas_dpoly x;
    return not listtest(a,b,function matop_pseudomod)
    end) where cali!=degrees:=dpmat_coldegs a;

endmodule; % matop

end;
