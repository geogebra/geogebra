module substns;

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


exports xsubstitutep,xsubstitutesq,substitutevec,substitutesq,subzero,
        subzero2,pvarsub;


symbolic procedure xsubstitutep(pf,slist);
simp xsubstitutep2(pf,slist);


symbolic procedure xsubstitutep2(pf,slist);
if null slist
  then pf
  else xsubstitutep2(subst(rfirstsubs slist,
                           lfirstsubs slist,
                           pf),
                     cdr slist);


symbolic procedure xsubstitutesq(sq,slist);
substitutesq(substitutesq(sq,basicplace slist),extenplace slist);


symbolic procedure substitutevec(v,slist);
for i:=0:upbv v do
  putv(v,i,substitutesq(getv(v,i),slist));


symbolic procedure substitutesq(sq,slist);
begin
  scalar list2,nm;
  list2:=nil;
  while slist do <<
    if cdar slist iequal 0
      then <<
        if list2
          then sq:=substitutesq(sq,reversip list2);
        list2:=nil;
        sq:=subzero(sq,caar slist) >>
      else if not (caar slist = cdar slist)
        then if assoc(caar slist,list2)
          then list2:=for each u in list2 collect
                  (car u).subst(cdar slist,caar slist,cdr u)
          else list2:=(car slist).list2;
        % don't bother with the null substitution.
    slist:=cdr slist >>;
  list2:=reversip list2;
  if null list2
    then return sq;
  nm:=algint!-subf(numr sq,list2);
  if numr nm
    then nm:=!*multsq(nm,invsq algint!-subf(denr sq,list2));
  return nm
  end;

% standard interface.
symbolic procedure subzero(exprn,var);
begin
  scalar top;
  top:=subzero2(numr exprn,var);
  if null numr top
    then return nil ./ 1;
  return !*multsq(top,!*invsq subzero2(denr exprn,var))
  end;


symbolic procedure subzero2(sf,var);
if not involvesf(sf,var)
  then sf ./ 1
  else if var eq mvar sf
    then subzero2(red sf,var)
    else if ordop(var,mvar sf)
      then sf ./ 1
      else begin
        scalar u,v;
        if dependsp(mvar sf,var)
          then <<
            u:=simp subst(0,var,mvar sf);
            if numr u
              then u:=!*exptsq(u,ldeg sf) >>
          else u:=((lpow sf .* 1) .+ nil) ./ 1;
        if null numr u
          then return subzero2(red sf,var);
        v:=subzero2(lc sf,var);
        if null numr v
          then return subzero2(red sf,var);
        return !*addsq(subzero2(red sf,var),
                       !*multsq(u,v))
        end;



symbolic procedure pvarsub(f,u,v);
% Changes u to v in polynomial f. No proper substitutions at all.
if atom f
  then f
  else if mvar f equal u
    then addf(multf(lc f,!*p2f mksp(v,ldeg f)),
              pvarsub(red f,u,v))
    else if ordop(u,mvar f)
      then f
      else addf(multf(pvarsub(lc f,u,v),!*p2f lpow f),
                pvarsub(red f,u,v));

endmodule;

end;
