module removecm;  % Routines to remove constant factors from expresions.

% Author: James H. Davenport.

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


fluid '(intvar);

% New improved REMOVECOMMOMMULTIPLES routines.
% These routines replace a straightforward pair with GCDF instead of
% CMGCDF and its associates.  The saving is large in complicated
% expressions (in the "general point of order 7" calculations, they
% exceeded 90% in some cases, being 1.5 secs as opposed to > 15 secs.).
% They are about 1K larger, but this seems a small price to pay.

exports removecmsq; % removeconstantsf;
imports ordop,addf,gcdn,gcdf,gcdk,involvesf,dependsp,makemainvar,quotf;

symbolic procedure removecmsq sq;
(removecmsf numr sq) ./ (removecmsf denr sq);

symbolic procedure removecmsf sf;
if atom sf or not ordop(mvar sf,intvar) or not involvesf(sf,intvar)
  then if sf
    then 1
    else nil
  else if null red sf
    then if dependsp(mvar sf,intvar)
      then (lpow sf .* removecmsf lc sf) .+ nil
      else removecmsf lc sf
    else begin
      scalar u,v;
      % The general principle here is to find a (non-INTVAR-depending)
      % coefficient of a purely INTVAR-depending monomial, and then
      % perform a g.c.d. to discover that factor of this which is a CM.
      u:=sf;
      while (v:=involvesf(u,intvar)) do u:=lc makemainvar(u,v);
      if u iequal 1
        then return sf;
      return quotf(sf,cmgcdf(sf,u))
      end;

symbolic procedure cmgcdf(sf,u);
if numberp u
  then if atom sf
    then if null sf
      then u
      else gcdn(sf,u)
    else if u = 1
      then 1
      else cmgcdf(red sf,cmgcdf(lc sf,u))
  else if atom sf
    then gcdf(sf,u)
    else if mvar u eq mvar sf
      then if ordop(intvar,mvar u)
        then gcdf(sf,u)
        else cmgcdf2(sf,u)
      else if ordop(mvar sf,mvar u)
        then cmgcdf(red sf,cmgcdf(lc sf,u))
        else cmgcdf(u,sf);

symbolic procedure remove!-maxdeg(sf,var);
if atom sf
  then 0
  else if mvar sf eq var
    then ldeg sf
    else if ordop(var,mvar sf)
      then 0
      else max(remove!-maxdeg(lc sf,var),remove!-maxdeg(red sf,var));

symbolic procedure cmgcdf2(sf,u);
% SF and U have the same MVAR, but INTVAR comes somewhere
% down in SF.  Therefore we can do better than a straight
% GCDK, or even a straight MAKEMAINVAR.
begin
  scalar n;
  n:=remove!-maxdeg(sf,intvar);
  if n = 0
    then return gcdf(sf,u);
    % Doesn't actually depend on INTVAR.
loop:
  if u = 1
    then return 1;
  u:=gcdf(u,collectterms(sf,intvar,n));
  n:=isub1 n;
  if n < 0
    then return u
    else go loop
  end;

symbolic procedure collectterms(sf,var,n);
if atom sf
  then if n = 0
    then sf
    else nil
  else if mvar sf eq var
    then if ldeg sf = n
      then lc sf
      else if ldeg sf > n
        then collectterms(red sf,var,n)
        else nil
    else if ordop(var,mvar sf)
      then if n = 0
        then sf
        else nil
      else begin
        scalar v,w;
        v:=collectterms(lc sf,var,n);
        w:=collectterms(red sf,var,n);
        if null v
          then return w
          else return addf(w,(lpow sf .* v) .+ nil)
        end;

% symbolic procedure removeconstantsf sf;
% % Very simple version for now.
% begin
%   scalar u;
%   if null sf
%     then return nil
%     else if atom sf
%       then return 1;
%   while (null red sf) and (remove!-constantp mvar sf) do
%     sf:=lc sf;
%   u:=remove!-const!-content sf;
%   if u = 1
%     then return sf
%     else return quotf!*(sf,u)
%   end;

symbolic procedure remove!-constantp pf;
if numberp pf
  then t
  else if atom pf
    then nil
    else if car pf eq 'sqrt
      then remove!-constantp argof pf
      else if (car pf eq 'expt) or (car pf eq 'quotient)
        then (remove!-constantp argof pf)
             and (remove!-constantp caddr pf)
        else nil;

symbolic procedure remove!-const!-content sf;
if numberp sf
  then sf
  else if null red sf
    then if remove!-constantp mvar sf
      then (lpow sf .* remove!-const!-content lc sf) .+ nil
      else remove!-const!-content lc sf
    else begin
      scalar u;
      u:=remove!-const!-content lc sf;
      if u = 1
        then return u;
      return gcdf(u,remove!-const!-content red sf)
      end;

endmodule;

end;
