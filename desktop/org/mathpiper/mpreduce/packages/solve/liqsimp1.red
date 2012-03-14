module liqsimp1; % interval simplifcation level 1 by
                 % inequality propagation.

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


fluid'(liqsimp1bounds!*);

symbolic procedure liqsimp1!-maxmin w;
 % W is a list of forms {x , l , u} where the interval [l,u]
 % has been assigned to the variable x. l and u may be formal
 % expressions dominated by an operator MAX or MIN and including
 % variables of the following intervals. I try to simplify the
 % bounds as far as possible by computing inequality chains.
  (liqsimp1!-maxmin1 w) where liqsimp1bounds!*=nil;


symbolic procedure liqsimp1!-maxmin1 w;
 begin scalar x,l,u,r;
   x:=caaar w; l:=cadar w; u:=caddar w;
   if cdr w then % bottom reached?
   << r:=liqsimp1!-maxmin1 cdr w;
      l:=liqsimp1!-reducecases(l);
      u:=liqsimp1!-reducecases(u);
   >>;
   liqsimp1bounds!* :=
        {x , liqsimp1!-maxmin1leaf(l),
             liqsimp1!-maxmin1leaf(u)}
       . liqsimp1bounds!*;
   return {caar w,l,u} . r;
 end;

symbolic procedure liqsimp1!-reducecases(w);
 % M=t: upper bound, M=nil: lower bound.
  begin scalar op,l,tst,d,u;
    if atom w or not memq(car w,'(max min)) then return w;
    op:=car w;
    l:=for each u in cdr w collect u . simp u;
      % check whether an element is covered by another one.
    for each e in l do
    <<tst := nil;
      for each d in l do if d neq e and not tst then
      <<
           % Can I prove that (with op=MAX) e <= d?
           % I compute u=d-e and check the thest u>= 0 down
           % the bounds for the other variables.
       u:=subtrsq(cdr d,cdr e);
       tst:=liqsimp1!-check(u,liqsimp1bounds!*,op);
      >>;
           % then delete it.
      if tst then l:=delete(e,l);
    >>;
      % collect the surviving elements.
    l:=for each u in l collect car u;
    return if cdr l then op. l else car l;
  end;

symbolic procedure liqsimp1!-check(u,bounds,m);
   if m='min then liqsimp1!-check1(negsq u,bounds) else
     liqsimp1!-check1(u,bounds);

symbolic procedure liqsimp1!-check1(u,bounds);
       % On this level I check whether u>=0 is true.
   if domainp numr u then not minusf numr u or null numr u else
   if null bounds then nil else
   if mvar numr u neq caar bounds
       then liqsimp1!-check1(u,cdr bounds) else
    begin scalar x,c,r,d,tst,bds;
     x:=caar bounds;
       % U = c*x + r, car bounds has lower and upper limits for x;
       % U >= 0 is then equivalent to
       %          c >= 0 replace x by lower bounds
       %      or
       %          c <= 0 replace x by upper bounds.
       %
     d:=!*f2q denr u; c:=!*f2q lc numr u; r:=!*f2q red numr u;
     if liqsimp1!-check1(c,bounds) then % C>=0
         bds:=cadar bounds else bds:=caddar bounds;
     for each b in bds do
       tst:=tst
           or liqsimp1!-check1(addsq(multsq(c,b),r),cdr bounds);
      return tst;
   end;

symbolic procedure liqsimp1!-maxmin1leaf(q);
   if q='infinity or q='(minus infinity) then nil else
   for each w in
     (if pairp q and car q memq '(max min) then cdr q else {q})
       collect simp w;

endmodule;

end;
