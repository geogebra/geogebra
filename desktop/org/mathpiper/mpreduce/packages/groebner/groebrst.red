module groebrst;

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


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% restrictions for polynomials during Groebner base calculation
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure groebtestrestriction (h,arg);
     if groebrestriction!* = 'nonnegative then groebnonneg(h,arg)
         else
     if groebrestriction!* = 'positive then groebpos(h,arg)
         else
     if groebrestriction!* = 'izeropoint then groebzero(h,arg)
         else
           rerror(groebnr2,9,
                  "Groebner: general restrictions not yet implemented");

symbolic procedure groebnonneg(h,arg);
    % test if h is a polynomial which can have the value zero with
    % only nonnegative variable settings.
     begin scalar x,break,vev1,vevl,problems,problems1,r;
        if vdpzero!? h then return nil
            else
        if vevzero!? vdpevlmon h then goto finish;
                 % first test the coefficients
        if vdpredZero!? h then return nil;   % simple monomial
        break := nil;
        x := h;
        while not vdpzero!? x and not break do
           <<vev1 := vdpevlmon x;
             if not vbcplus!? vdpLbc x then break := t;
             if not break then x := vdpred x>>;
        if break then return nil;     % at least one negative coeff
        if vevzero!? vev1 then goto finish; % nonneg. solution imposs.
                       % homogenous polynomial: find combinations of
                       % variables which are solutions
        x := h;
        vev1 := vdpevlmon x;
        vevl := vevsplit(vev1);
        problems := for each x in vevl collect list x;
        x := vdpred x;
        while not vdpzero!? x do
        << vev1 := vdpevlmon x;
           vevl := vevsplit(vev1);
           problems1 := nil;
           for each e in vevl do
             for each p in problems do
               <<r := if not member(e,p) then e . p else p;
                 problems1 := union(list r, problems1)>>;
           problems := problems1;
           x := vdpred x >>;
        problems :=             % lift vevs to polynomials
          for each p in problems collect
             for each e in p collect
               vdpfmon(a2vbc 1,e);
                                % rule out problems contained in others
        for each x in problems do
            for each y in problems do
               if not eq(x,y) and subset!?(x,y) then
                   problems := delete (y,problems);

                                % rule out some by cdr
        problems1 := nil;
        while problems do
          <<if vdpDisjoint!? (car problems,arg)
               then problems1 := car problems . problems1;
            problems := cdr problems >>;
  finish:
                    groebmess24(h,problems1,arg);
        return
         if null problems1 then 'icancel
            else 'restriction . problems1 end;

symbolic procedure groebpos(h,dummy);
    % test if h is a polynomial which can have the value zero with
    % only positive (nonnegative and nonzero) variable settings.
     begin scalar x,break,vev1;
        dummy := nil;
        if vdpzero!? h then return nil
            else
        if vevzero!? vdpevlmon h then return nil;
                     % a simple monomial can never have pos. zeros
        if vdpredzero!? h then return groebposcancel(h);
        break := nil;
        x := h;
                     % test coefficients
        while not vdpzero!? x and not break do
           <<vev1 := vdpevlmon x;
             if not vbcplus!? vdpLbc x then break := t;
             if not break then x := vdpred x>>;
        if not break then return groebPosCancel(h);
        if not groebposvevaluate h then groebPosCancel(h);
        return nil end;

symbolic procedure groebposvevaluate h; <<h := nil; t>>;
    % test if a polynomial can become zero under user restrictions
    % here a dummy to be rplaced elsewhere

symbolic procedure groebzero(h,dummy);
   begin scalar l;
     dummy := nil;
     l:=vdplastmon h;
     if l and vevzero!? cdr l then return groebPosCancel h;
     return nil end;

symbolic procedure groebposcancel(h);
       <<groebmess24(h,nil,nil); 'cancel>>;

endmodule;;end;
