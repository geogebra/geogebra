module sort;  % A simple sorting routine.

% Author: Arthur C. Norman.

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


symbolic procedure sort(l,pred);
   % Sort the list l according to the given predicate.  If l is a list
   % of numbers then the predicate "lessp" will sort the list into
   % ascending order.  The predicate should be a strict inequality,
   % i.e. it should return NIL if the two items compared are equal.  As
   % implemented here SORT just calls STABLE-SORT, but as a matter of
   % style any use where the ordering of incomparable items in the
   % output matters ought to use STABLE!-SORT directly, thereby
   % allowing the replacement of this code with a faster non-stable
   % method. (Note: the previous REDUCE sort function also happened to
   % be stable, so this code should give exactly the same results for
   % all calls where the predicate is self-consistent and never has
   % both pred(a,b) and pred(b,a) true).
   stable!-sortip(append(l, nil), pred);

symbolic procedure stable!-sort(l,pred);
   % Sorts a list, as SORT, but if two items x and y in the input list
   % satisfy neither pred(x,y) nor pred(y,x) [i.e. they are equal so far
   % as the given ordering predicate is concerned] this function
   % guarantees that they will appear in the output list in the same
   % order that they were in the input.
   stable!-sortip(append(l, nil), pred);

symbolic procedure stable!-sortip(l, pred);
   % As stable!-sort, but over-writes the input list to make the output.
   % It is not intended that people should call this function directly:
   % it is present just as the implementation of the main sort
   % procedures defined above.
   begin scalar l1,l2,w;
      if null l then return l;    % Input list of length 0
      l1 := l;
      l2 := cdr l;
      if null l2 then return l;   % Input list of length 1
      % Now I have dealt with the essential special cases of lists of
      % length 0 and 1 (which do not need sorting at all).  Since it
      % possibly speeds things up just a little I will now have some
      % fairly ugly code that makes special cases of lists of length 2.
      % I could easily have special code for length 3 lists here (and
      % include it, but commented out), but at present my measurements
      % suggest that the speed improvement that it gives is minimal and
      % the increase in code bulk is large enough to give some pain.
      l := cdr l2;
      if null l then <<           % Input list of length 2
         if apply2(pred, car l2, car l1) then <<
            l := car l1;
            rplaca(l1, car l2);
            rplaca(l2, l) >>;
       return l1 >>;
      % Now I will check to see if the list is in fact in order already
      % Doing so will have a cost - but sometimes that cost will be
      % repaid when I am able to exit especially early.  The result of
      % all this is that I will have a best case behaviour with linear
      % cost growth for inputs that are initially in the correct order,
      % while my average and worst-case costs will increase by a
      % constant factor.
      l := l1;
      % In the input list is NOT already in order then I expect that
      % this loop will exit fairly early, and so will not contribute
      % much to the total cost.  If it exits very late then probably in
      % the next recursion down the first half of the list will be
      % found to be already sorted, and again I have a chance to win.
      while l2 and not apply2(pred, car l2, car l) do
         <<l := l2; l2 := cdr l2 >>;
      if null l2 then return l1;
      l2 := l1;
      l := cddr l2;
      while l and cdr l do << l2 := cdr l2; l := cddr l >>;
      l := l2;
      l2 := cdr l2;
      rplacd(l, nil);
      % The two sub-lists are then sorted.
      l1 := stable!-sortip(l1, pred);
      l2 := stable!-sortip(l2, pred);
      % Now I merge the sorted fragments, giving priority to item from
      % the earlier part of the original list.
      l := w := list nil;
      while l1 and l2 do <<
         if apply2(pred, car l2, car l1) then <<
            rplacd(w, l2); w := l2; l2 := cdr l2 >>
         else <<rplacd(w, l1); w := l1; l1 := cdr l1>>>>;
      if l1 then l2 := l1;
      rplacd(w,l2);
      return cdr l
  end;

symbolic procedure idsort u;
   % lexicographically sort list of ids.
   sort(u,function idcompare);

symbolic procedure idcompare(u,v);
   % compare lexicographical ordering of two ids.
   idcomp1(explode2 u,explode2 v);

symbolic procedure idcomp1(u,v);
   if null u then t
    else if null v then nil
    else if car u eq car v then idcomp1(cdr u,cdr v)
    else orderp(car u,car v);

% Comparison functions and special cases for sorting.

symbolic procedure lesspcar(a,b); car a < car b;

symbolic procedure lesspcdr(a,b); cdr a < cdr b;

symbolic procedure lessppair(a,b);
    if car a = car b then cdr a<cdr b else car a<car b;

symbolic procedure greaterpcdr(a,b); cdr a > cdr b;

symbolic procedure lesspcdadr(a,b); cdadr a < cdadr b;

symbolic procedure lesspdeg(a,b);
   if domainp b then nil else if domainp a then t else ldeg a<ldeg b;

symbolic procedure ordopcar(a,b); ordop(car a,car b);

symbolic procedure orderfactors(a,b);
   if cdr a = cdr b then ordp(car a,car b) else cdr a < cdr b;

symbolic procedure sort!-factors l;
   % Sort factors as found into some sort of standard order.  The order
   % used here is more or less random, but will be self-consistent.
   sort(l,function orderfactors);

endmodule;

end;
