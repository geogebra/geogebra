module sl2psl; % Definitions of functions in PSL but not SL.

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


% Some of these are already in the standard REDUCE now.

deflist('((fixp 1) (numberp 1) (floatp 1) (evenp 1) (oddp 1)
          (stringp 1) (idp 1) (ordp 2) (nordp 2) (equal 2)
                              (geq 2) (leq 2)),'number!-of!-args);

%symbolic procedure lastcar l;
% if atom l then l else
% if atom cdr l then car l else car lastpair cdr l;

symbolic procedure lconc(l1,l2);
% Both arguments are lists l1 is a list of the type
% ((a b c ... f) f)
% Useful for concatenating lists from right to left without copying.
% l1 may be nil to start with.
% REQUIRED FOR FUTURE RELEASE
 if null l1 then rplacd(list l2,lastpair l2) else
 if null car l1 then rplacd(rplaca(l1,l2),l2) else
 <<rplacd(cdr l1 ,l2); rplacd(l1, lastpair l2)>>;

symbolic procedure tconc(l,elm);
 <<elm:=cons(elm,nil);
   if null l then nconc(list elm,elm) else
   if null car l then  rplacd(rplaca(l,elm),elm) else
    <<rplacd(cdr l,elm);rplacd(l,elm)>>
 >>;

symbolic procedure adjoin(elm,st);
 % elm is any object, st is a set.
 if member(elm,st) then st else cons(elm,st);

symbolic procedure list2set u;
% Eliminates redundant elements .
% Replaces !:mkset u of the old ASSIST package.
 if null u then nil else if member(car u,cdr u) then list2set cdr u
 else car u . list2set cdr u;


symbolic procedure delqip1(elm,l);
 if not atom cdr l then
   if elm eq cadr l then rplacd(l,cddr l) else
    delqip1(elm,cdr l);

symbolic procedure delqip(elm,l);
 % Deletes elm from l without copying l.
 % This is the good definition given by Arthur Norman.
 % Used in the function SYMMETRIZE.
 if atom l then l else
 if elm eq car l then cdr l else
        <<delqip1(elm,l);l>>;

endmodule;

end;
