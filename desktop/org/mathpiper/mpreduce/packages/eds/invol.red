module invol;

% Cartan characters, reduced characters, involution test

% Author: David Hartley

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


fluid '(!*edsverbose !*edsdebug !*edssloppy !*genpos !*ranpos);


put('characters,'psopfn,'chareval);

symbolic procedure chareval u;
   if length u < 1 or length u > 2 then
      rerror(eds,000,"Wrong number of arguments to characters")
   else if edsp car(u := revlis u) then
      makelist characters(car u,if cdr u then !*a2sys cadr u)
   else
      makelist characterstab !*a2tab u;


symbolic procedure characterstab u;
   % u:tab -> characterstab:list of int
   countchars tp1 foreach r in cdr u collect
      foreach c in r collect c; % copy u because tp1 destroys it


symbolic procedure characters(s,x);
   % s:eds, x:sys -> characters:list of int
   % Have to protect call since kernel ordering changes
   edscall characters1(s,x);


symbolic procedure characters1(s,x);
   % s:eds, x:sys -> characters1:list of int
   begin scalar prl,x,ind,q,sp;
   if not normaledsp s then
      rerror(eds,000,"System not in normal form");
   if scalarpart eds_sys s then
      rerror(eds,000,"Characters with 0-forms not yet implemented");
   s := closure s;
   if quasilinearp s then
      s := car tmpind changeposition lineargenerators s
   else if null x then
      rerror(eds,000,
             "Integral element required for nonlinear EDS characters")
   else
      s := car tmpind changeposition linearise(s,x);
   if not normaledsp s or scalarpart eds_sys s then
      errdhh "Result from tmpind has 0-forms or is not in normal form";
   prl := prlkrns s; ind := indkrns s;
   q := foreach f in nonpfaffpart eds_sys s join
           if f := linearpart(f,prl) then {f};
   x := reversip foreach w on reverse ind collect
           foreach f in q join
              foreach c in ordcomb(cdr w,degreepf f - 2) join
                 if c := xcoeff(f,car w . c) then {c};
   % Get characters from tableau
   x := reverse countchars x;
   % Get last character from s(p) = n - (p + s(0) + s(1) + ... + s(n-1))
   sp := length edscob s - (length ind + length pfaffpart eds_sys s +
                  foreach si in cdr x sum si);
   % Compare the two, since we have them
   if sp neq car x then
      edsverbose("Cauchy characteristics detected from characters",
                 nil,nil);
   return reverse(sp . cdr x);
   end;


symbolic procedure changeposition s;
   % s:eds -> changeposition:eds
   % Transform system to general or random position, depending on
   % switches. Ordering of lists is arranged so that transforms are
   % lower triangular, since this should suffice.  Derivatives are not
   % updated, so s should be closed first if necessary.
   % NB Kernel order changed.
   if !*genpos then
      begin scalar x,new;
      new := for i:=1:length eds_ind s collect
                mkform!*(intern gensym(),1);
      x := reversip foreach k in new collect !*k2pf k;
      x := foreach l on x collect
              zippf(l,(1 ./ 1) . for i:=2:length l collect
                                                !*k2q intern gensym());
      x := pair(indkrns s,reverse x);
      edsdebug("Transformation to general position",x,'xform);
      return xformeds0(s,x,new);
      end
   else if !*ranpos then
      begin scalar x,y,k,new;
      new := for i:=1:length eds_ind s collect
                mkform!*(intern gensym(),1);
      x := reversip foreach k in new collect !*k2pf k;
      k := updkordl lpows x;
      for i:=1:length eds_ind s do
         begin scalar f;
                   while null(f := xreduce(f,y)) do
               f := zippf(x,ranlistsq(length eds_ind s,10,5));
            y := f . y;
         end;
      setkorder k;
      x := pair(indkrns s,foreach f in y collect xreorder f);
      edsdebug("Transformation to random position",x,'xform);
      return xformeds0(s,x,new);
      end
   else s;


symbolic procedure ranlistsq(n,p,m);
   % n,p,m:int -> ranlist:list of sq
   % Produces a list of n random numbers between -m and m, with
   % at most p non-zero elements.
   begin scalar u,v;
   p := min2(n,p);
   u := for i:=1:p collect simpatom(random(2*m+1) - m);
   while length v < p do
      (if not(x memq v) then v := x . v) where x = 1 + random n;
   u := pair(v,u);
   return for i:=1:n collect
                 if v := atsoc(i,u) then cdr v else nil ./ 1;
   end;


symbolic procedure countchars x;
   % x:list of list of pf -> list of int
   % all pf are 1-forms
   begin scalar p,ri,si;
   foreach r in x do
      begin
      p := weak_xautoreduce append(r,p);
      ri := length p . ri;
      end;
   while cdr ri do
      <<si := (car ri - cadr ri) . si;
        ri := cdr ri>>;
   return car ri . si;
   end;


symbolic procedure involutionchk(s0,s1);
   % s0,s1:eds -> involutionchk:bool
   % s1 is prolongation of s0
   cartantest(characters(s0,eds_sys s1),
                     length edscob s1 - length edscob s0);


symbolic procedure cartantest(c,d);
   % c:list of int, d:int
   % c is list of characters, d is dimension of solution variety
   begin integer m;
   c := reverse c;
   foreach k on c do
      m := length k*car k + m;
   if d > m then errdhh {"Inconsistency in Cartan's test:",reverse c,d}
   else return d = m;
   end;


put('involutive,'psopfn,'involutiveeval);

symbolic procedure involutiveeval s;
   % s:{eds} -> involutiveeval:0 or 1
   if edsp(s := reval car s) then
      if knowntrueeds(s,'involutive) or
               not knownfalseeds(s,'involutive) and
               edscall involutive s then 1 else 0
   else typerr(s,'eds);


symbolic procedure involutive s;
   % s:eds -> involutive:bool
   knowntrueeds(s,'involutive) or
      not knownfalseeds(s,'involutive) and
      begin scalar s0,s1,flg;
      s0 := closure s;
      if semilinearp s0 then
         flg := cartantest(characters(s0,nil),
                           dimgrassmannvariety(s0,nil))
                 and null grassmannvarietytorsion s0
      else
      << s1 := prolong s0;
         while s1 and (caar s1='prolonged) and involutionchk(s0,cdar s1)
            do s1 := cdr s1;
               flg := null s1 >>;
      if flg
      then <<flagtrueeds(s,'involutive); return t>>;
      %%% We mustn't flag involutive FALSE since it might be an accident
      %%% of the integral flag and not a property of the system we want
      %%% to immortalise.
      % else <<flagfalseeds(s,'involutive); return nil>>;
   end;


put('involution,'rtypefn,'quoteeds);
put('involution,'edsfn,'involutioneds);

symbolic procedure involutioneds s;
   % s:eds -> involutioneds:xeds
   if not edsp s then typerr(s,'eds)
   else mkxeds makelist
      foreach x in edscall involution s collect
          if null car x then {'involution,cdr x}
          else cdr x;


symbolic procedure involution s;
   % s:eds -> involution:list of tag.eds
   % where tag = t if eds is involutive
   %             nil if prolongation failed
   % NEEDS WORK!!!
   begin scalar r,s0;
   s0 := closure s;
   if semilinearp s0 and
      cartantest(characters(s0,nil),dimgrassmannvariety(s0,nil)) and
      null grassmannvarietytorsion s0
   then return {t.s};
   foreach s1 in edscall prolong s0 do
      if car s1 = 'inconsistent then nil
      else if car s1 = 'failed then r := union({nil . cdr s1},r)
      else if car s1 = 'prolonged and involutionchk(s0,cdr s1) then
              r := union({t.s},r)
      else r := union(edscall involution cdr s1,r);
   return r;
   end;

endmodule;

end;
