module edsequiv;

% Check if EDS structures are equivalent

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


fluid '(xtruncate!*);


infix equiv;
precedence equiv,equal;

symbolic operator equiv;
symbolic procedure equiv(u,v);
   if cfrmp u then cfrmp v and equalcfrm(u,v)
   else if edsp u then edsp v and edscall equaleds(u,v)
   else rerror(eds,000,"Don't know how to test equivalence");


symbolic procedure equalcfrm(m,n);
   % m,n:cfrm -> equalcfrm:bool
   equall(cfrm_cob m,cfrm_cob n) and
   equall(cfrm_crd m,cfrm_crd n) and
   equaldrv(cfrm_drv m,cfrm_drv n) and
   equalrsx(cfrm_rsx m,cfrm_rsx n);


symbolic procedure equall(u,v);
   % u,v:list -> equall:bool
   (length u = length v) and subsetp(u,v);


symbolic procedure equaldrv(d1,d2);
   % d1,d2:drv -> equaldrv:bool
   equall(d1,d2) or
   equall(foreach r in d1 collect cadr r,
                foreach r in d2 collect cadr r) and
   equall(foreach r in d1 collect resimp simp!* caddr r,
                foreach r in d2 collect resimp simp!* caddr r);


symbolic procedure equalrsx(r1,r2);
   % r1,r2:rsx -> equalrsx:bool
   equall(r1,r2) or
   equall(foreach r in r1 collect absf numr simp!* r,
                foreach r in r2 collect absf numr simp!* r);


symbolic procedure equaleds(s1,s2);
   % s1,s2:eds -> equaleds:bool
   equalcfrm(eds_cfrm s1,eds_cfrm s2) and
   equivsys(eds_sys s1,eds_sys s2) and
   equivsys(eds_ind s1,eds_ind s2);


symbolic procedure equivsys(p,q);
   % p,q:sys -> equivsys:bool
   % Assumes background coframing set up correctly.
   equall(p := xreordersys p,q := xreordersys q) or
   begin scalar p1,q1,g,xtruncate!*; integer d;
   p1 := foreach f in setdiff(p,q) join
      if f := xreduce(f,q) then {f};
   q1 := foreach f in setdiff(q,p) join
      if f := xreduce(f,p) then {f};
   if null p1 and null q1 then return t;
   if scalarpart p1 or scalarpart q1 then
      rerror(eds,000,"Can't compare systems with 0-forms");
   if p1 then
   << d := 0; foreach f in p1 do d := max(d,degreepf f);
      xtruncate!* := d; g := xidealpf q;
      p1 := foreach f in p1 join
               if f := xreduce(f,g) then {f}>>;
   if p1 then return nil;
   if q1 then
   << d := 0; foreach f in q1 do d := max(d,degreepf f);
      xtruncate!* := d; g := xidealpf p;
      q1 := foreach f in q1 join
               if f := xreduce(f,g) then {f}>>;
   return null q1;
   end;

endmodule;

end;
