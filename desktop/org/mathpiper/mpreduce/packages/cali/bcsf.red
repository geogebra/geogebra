module bcsf;

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


            #######################
            #                     #
            #  BASE COEFFICIENTS  #
            #                     #
            #######################


These base coefficients are standard forms.

A list of REPLACEBY rules may be supplied with the setrules command
that will be applied in an additional simplification process.

This rules list is a list of s.f. pairs, where car should replace cdr.

END COMMENT;

% Standard is :

!*hardzerotest:=nil;

symbolic operator setrules;
symbolic procedure setrules m; setrules!* cdr reval m;

symbolic procedure setrules!* m;
  begin scalar r; r:=ring_names cali!=basering;
  m:=for each x in m collect
        if not eqcar(x,'replaceby) then
                typerr(makelist m,"rules list")
        else (numr simp second x . numr simp third x);
  for each x in m do
    if domainp car x or member(mvar car x,r) then
        rederr"no substitution for ring variables allowed";
  put('cali,'rules,m);
  return getrules();
  end;

symbolic operator getrules;
symbolic procedure getrules();
  makelist for each x in get('cali,'rules) collect
        list('replaceby,prepf car x,prepf cdr x);

symbolic procedure bc!=simp u;
  (if r0 then
  begin scalar r,c; integer i;
  i:=0; r:=r0;
  while r and (i<1000) do
  << c:=qremf(u,caar r);
     if null car c then r:=cdr r
     else
     << u:=addf(multf(car c,cdar r),cdr c);
        i:=i+1; r:=r0;
     >>;
  >>;
  if (i<1000) then return u
  else rederr"recursion depth of bc!=simp too high"
  end
  else u) where r0:=get('cali,'rules);

symbolic procedure  bc_minus!? u; minusf u;

symbolic procedure  bc_zero!? u;
    if (null u or u=0) then t
    else if !*hardzerotest and pairp u then
        null bc!=simp numr simp prepf u
    else nil;

symbolic procedure  bc_fi a; if a=0 then nil else a;

symbolic procedure  bc_one!? u; (u = 1);

symbolic procedure  bc_inv u;
% Test, whether u is invertible. Return the inverse of u or nil.
  if (u=1) or (u=-1) then u
  else begin scalar v; v:=qremf(1,u);
    if cdr v then return nil else return car v;
    end;

symbolic procedure  bc_neg u; negf u;

symbolic procedure  bc_prod (u,v); bc!=simp multf(u,v);

symbolic procedure  bc_quot (u,v);
  (if null cdr w then bc!=simp car w else typerr(v,"denominator"))
  where w=qremf(u,v);

symbolic procedure  bc_sum (u,v); addf(u,v);

symbolic procedure  bc_diff(u,v); addf(u,negf v);

symbolic procedure  bc_power(u,n); bc!=simp exptf(u,n);

symbolic procedure  bc_from_a u; bc!=simp numr simp!* u;

symbolic procedure  bc_2a u; prepf u;

symbolic procedure  bc_prin u;
% Prints a base coefficient in infix form
   ( if domainp u then
         if dmode!*='!:mod!: then prin2 prepf u
         else printsf u
     else << write"("; printsf u; write")" >>) where !*nat=nil;

symbolic procedure bc_divmod(u,v); % Returns quot . rem.
  qremf(u,v);

symbolic procedure bc_gcd(u,v); gcdf!*(u,v);

symbolic procedure bc_lcm(u,v);
    car bc_divmod(bc_prod(u,v),bc_gcd(u,v));

endmodule; % bcsf

end;
