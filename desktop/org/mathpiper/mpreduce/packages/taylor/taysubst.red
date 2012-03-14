module TaySubst;

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


%*****************************************************************
%
%      Interface to the substitution functions
%
%*****************************************************************


exports subsubtaylor$

imports

% from the REDUCE kernel:
        addsq, denr, depends, domainp, eqcar, exptsq, invsq, lc, ldeg,
        mkrn, multsq, mvar, nlist, nth, numr, prepsq, red,
        replace!-nth!-nth, reval, reversip, simp!*, simprn, sort,
        subeval1, subs2!*, subsq, subtrsq, typerr,

% from the header module:
        make!-Taylor!*, set!-TayCfSq, TayCfPl, TayCfSq, TayCoeffList,
        TayFlags, Taylor!:, Taylor!-error, TayVars, TayMakeCoeff,
        TayOrig, TayTemplate, TayTpElNext, TayTpElOrder, TayTpElPoint,
        TayTpElVars,

% from module Tayintro:
        constant!-sq!-p, delete!-nth, delete!-nth!-nth, replace!-nth,
        Taylor!-error, Taylor!-error!*, var!-is!-nth,

% from module Tayutils:
       enter!-sorted, rat!-kern!-pow;


fluid '(!*taylorkeeporiginal);

put ('taylor!*, 'subfunc, 'subsubtaylor);

symbolic procedure subsubtaylor(l,v);
  begin scalar x,clist,delete_list,tp,pl;
    clist := for each u in TayCoeffList v collect
               TayMakeCoeff(TayCfPl u,subsq(TayCfSq u,l));
    tp := TayTemplate v;
    %
    % Substitute in expansion point
    %
    tp := for each quartet in tp collect
            {TayTpElVars quartet,
             reval subeval1(l,TayTpElPoint quartet),
             TayTpElOrder quartet,
             TayTpElNext quartet};
    pl := for each quartet in tp collect
            nlist(nil,length TayTpElVars quartet);
    %
    % Make x the list of substitutions of Taylor variables.
    %
    for each p in l do
      if car p member TayVars v
        %
        % The replacement of a Taylor variable must again be
        % a kernel.  If it is a constant, we have to delete it
        % from the list of Taylor variables.  Actually the main
        % problem is to distinguish kernels that are constant
        % expressions (e.g. sin (acos (4))) from others.
        %
        then begin scalar temp;
         temp := simp!* cdr p;
         if constant!-sq!-p temp
          then begin scalar about,ll,w,y,z; integer pos,pos1;
            %
            % Determine the position of the variable
            %
            w := var!-is!-nth(tp,car p);
            pos := car w;
            pos1 := cdr w;
            if not null nth(nth(pl,pos),pos1)
              then Taylor!-error('invalid!-subst,
                            "multiple substitution for same variable");
            pl := replace!-nth!-nth(pl,pos,pos1,0);
            %
            % Calculate the difference (new_variable - expansion_point)
            %
            about := TayTpElPoint nth(tp,pos);
            if about eq 'infinity
              then if null numr temp
                then Taylor!-error!*('zero!-denom,"Taylor Substitution")
               else temp := invsq temp
             else temp := subtrsq(temp,simp!* about);
            %
            % Adjust for already deleted
            %
            foreach pp in delete_list do
              if car pp < pos then pos := pos - 1;
            delete_list := (pos . pos1) . delete_list;
            %
            % Substitute in every coefficient
            %
            Taylor!:
            for each cc in clist do begin scalar exponent;
              w := nth(TayCfPl cc,pos);
              w := if null cdr w then delete!-nth(TayCfPl cc,pos)
                    else delete!-nth!-nth(TayCfPl cc,pos,pos1);
              exponent := nth(nth(TayCfPl cc,pos),pos1);
              z := if exponent = 0 then TayCfSq cc
                     else if exponent < 0 and null numr temp
                      then Taylor!-error!*('zero!-denom,
                                         "Taylor Substitution")
                     else multsq(TayCfSq cc,exptsq(temp,exponent));
              y := assoc(w,ll);
              if y then set!-TayCfSq(y,subs2!* addsq(TayCfSq y,z))
               else if not null numr (z := subs2!* z)
                then ll := TayMakeCoeff(w,z) . ll
             end;
            %
            % Delete zero coefficients
            %
            clist := nil;
            while ll do <<
              if not null numr TayCfSq car ll
                then clist := enter!-sorted(car ll,clist);
              ll := cdr ll>>;
          end
         else if not (denr temp = 1 and
                      (temp := rat!-kern!-pow(numr temp,t)))
          then typerr({'replaceby,car p,cdr p},
                      "Taylor substitution")
         else begin scalar w,expo; integer pos,pos1;
           expo := cdr temp;
           temp := car temp;
           for each el in delete(car p,TayVars v) do
             if depends(temp,el)
               then Taylor!-error('invalid!-subst,
                                  {"dependent variables",cdr p,el});
           if not (expo = 1) then <<
             w := var!-is!-nth(tp,car p);
             pos := car w;
             pos1 := cdr w;
             if not null nth(nth(pl,pos),pos1)
               then Taylor!-error('invalid!-subst,
                            "different powers in homogeneous template");
             pl := replace!-nth!-nth(pl,pos,pos1,expo)>>;
           x := (car p . temp) . x
         end
        end;
   for each pp in sort(delete_list,function sortpred) do
      <<if null cdr TayTpElVars u
          then <<tp := delete!-nth(tp,car pp);
                 pl := delete!-nth(pl,car pp)>>
         else <<tp := replace!-nth(tp,car pp,
                                   {delete!-nth(TayTpElVars u,cdr pp),
                                    TayTpElPoint u,
                                    TayTpElOrder u,
                                    TayTpElNext u});
                pl := delete!-nth!-nth(pl,car pp,cdr pp)>>>>
          where u := nth(tp,car pp);
    if null tp
      then return if null clist then 0 else prepsq TayCfSq car clist;
    x := reversip x;
    pl := check!-pl pl;
    if null pl then Taylor!-error('invalid!-subst,
                            "different powers in homogeneous template");
    return if pl = nlist(1,length tp)
             then make!-Taylor!*(clist,sublis(x,tp),
                        if !*taylorkeeporiginal and TayOrig v
                          then subsq(TayOrig v,l)
                         else nil,
                        TayFlags v)
            else make!-Taylor!*(change!-coefflist(clist,pl),
                        change!-tp(sublis(x,tp),pl),
                        if !*taylorkeeporiginal and TayOrig v
                          then subsq(TayOrig v,l)
                         else nil,
                        TayFlags v)
  end;

symbolic procedure sortpred(u,v);
   car u > car v or car u = car v and cdr u > cdr v;

symbolic procedure check!-pl pl;
  Taylor!:
   if null pl then nil
    else ((if n=0 then check!-pl cdr pl
            else if n and n<0 then nil
            else n . check!-pl cdr pl)
           where n := check!-pl0(car car pl,cdr car pl));

symbolic procedure check!-pl0(n,nl);
   if null nl then n else n=car nl and check!-pl0(n,cdr nl);

symbolic procedure change!-coefflist(cflist,pl);
   for each cf in cflist collect
     TayMakeCoeff(change!-pl(TayCfPl cf,pl),TayCfSq cf);

symbolic procedure change!-tp(tp,pl);
   if null tp then nil
    else (if null car pl then car tp
           else Taylor!:{TayTpElVars car tp,
                         TayTpElPoint car tp,
                         TayTpElOrder car tp * car pl,
                         TayTpElNext car tp * car pl})
        . cdr tp;

symbolic procedure change!-pl(pl,pl0);
  if null pl then nil
   else (if null car pl0 then car pl
          else for each n in car pl collect Taylor!:(car pl0 * n))
        . change!-pl(cdr pl,cdr pl0);

endmodule;

end;
