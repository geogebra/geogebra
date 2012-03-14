module element;

% Generate a random integral element

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


Comment. At present, the Cartan-Kaehler construction is used, as by
Wahlquist, to reduce the problem to linear algebra. This fails for
non-involutive systems.

endcomment;


put('integral_element,'rtypefn,'quotelist);
put('integral_element,'listfn,'intelteval);

symbolic procedure intelteval(u,v);
   % u:{eds}, v:bool -> intelteval:list of prefix
   if length u neq 1 then
      rerror(eds,000,"Wrong number of arguments to integral_element")
   else if not edsp(u := reval car u) then typerr(u,"EDS")
   else !*sys2a1(edscall intelt u,v);


symbolic procedure intelt s;
   % s:eds -> intelt:sys
   % Produce an arbitrary integral element of s using the Cartan-Kaehler
   % construction.
   begin scalar g,v,a,h,z;
   s := closure s;
   g := gbsys s;
   % reduction in next lines ok since lpows g = prlkrns s
   v := foreach f in nonpfaffpart eds_sys s join
                 if f := xreduce(f,eds_sys g) then {f};
   % get polar systems
   h := reversip foreach w on reverse indkrns s collect
           foreach f in v join
              foreach c in ordcomb(cdr w,degreepf f - 1) join
                 if c := xcoeff(f,car w . c) then {lc c};
   % get graded variable list
   a := v := {};
   foreach w in indkrns s do
   << v := setdiff(foreach f in eds_sys g collect
                    mvar numr lc xcoeff(f,{w}),a) . v;
      a := append(car v,a) >>;
   v := reverse v;
   % solve polar systems
   foreach x in pair(h,v) do
   << v := cdr x;
      x := foreach f in car x join if numr(f := subsq(f,z)) then {f};
      edsdebug("Polar system",x,'sq);
      z := append(edsransolve(x,v),z) >>;
   return foreach f in eds_sys g collect pullbackpf(f,z);
   end;


symbolic procedure edsransolve(x,v);
   % x:list of sq, v:list of kernel -> edsransolve:map
   begin
   x := edssolve(x,v);
   if null x then
      rerror(eds,000,"Singular system in integral_element");
   if length x > 1 or null caar x then
      rerror(eds,000,"Bad system in integral_element");
   x := car cdr car x; % get the map part of first solution
   v := setdiff(v,foreach m in x collect car m);
   edsverbose({length v,"free variables"},nil,nil);
   v := foreach c in v collect c . sparserandom 5;
   x := nconc(pullbackmap(x,v),v);
   edsdebug("Solution",x,'map);
   return x;
   end;


symbolic procedure sparserandom n;
   if random 100 < 0 then 0 else random(2*n+1)-n;

endmodule;

end;
