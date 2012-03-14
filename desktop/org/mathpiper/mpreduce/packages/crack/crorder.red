%********************************************************************
module crackorder$
%********************************************************************
%
% Name:          crorder.red
% Description:   Multiple orderings support
% Author:        Arrigo
%
% $Id: crorder.red,v 1.21 1998/06/08 14:38:18 arrigo Exp $
%
% !FIXME! codep is better substituted by getd as codep works only with
% compiled code, oops.
%
% !FIXME! apply is going to be used as follows:
% apply(caddr(getv(orderings_,[ordering_number])), {eq});
% to get the ordering function.

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


%
% make_orderings(fcts, vars) creates a vector containing all
% possibile orderings of the given fcts and vars
%
symbolic procedure make_orderings(fcts, vars)$
begin scalar fctsl, varsl, ordsl, fn, j$
   if print_ and tr_orderings then
      <<
         terpri()$ write "make_orderings("$
         write fcts, ",", vars$
            write ")"
      >>$
   if not pairp(vars) or not vars then
      <<
         % Thomas has some situations in which there are no
         % variables but only functions, we accordingly create
         % a single ordering
         ordsl := mkvect(1)$
               putv(ordsl,0,list(list(nil),fcts,'default_ordering_function))$
         return ordsl$
      >>$
   if not pairp(fcts) then
      <<
         terpri()$ write "confused! expected list of functions"$
         % !FIXME! Same as above
         return(nil)
      >>$
   %
   % OK, so we now actually create two lists and then turn them into
   % our "ordering" structure which is a vector
   % [ (perm v, perm f, ordering_function)
   % ...
   %   (perm v, perm f, ordering_function) nil ]
   %
   % Purpose of the various bits is:
   %
   %       v                       ordered variables list
   %       f                       ordered functions list
   %       ordering_function       a function which, given a list of
   %                               derivs will order them for the current
   %                               ordering
   %
   % !FIXME! Do we want the trailing nil?
   %
   if simple_orderings then <<
      ordsl := mkvect(1)$
      putv(ordsl,0,list(vars,fcts,'default_ordering_function))$
   >>
   else <<
      varsl := permu(vars)$
      fctsl := permu(fcts)$
      ordsl := mkvect(length(varsl)*length(fctsl))$
      j := 0$
      for each v in varsl do
               for each fn in fctsl do <<
            putv(ordsl,j,list(v, fn, 'default_ordering_function))$
            j := add1 j$
               >>$
   >>$
   % Done
   if tr_orderings then
      <<
         terpri()$
         write "END - make_orderings(): "$
         terpri()$ write ordsl$
      >>$
   return ordsl$
end$

%
% permu() is TW's list of permutations generator
%
symbolic procedure permu(li)$
% generates a list of permutations of the elements of li
if length(li)=1 then list(li)
                else for each x in li join
                     for each y in permu(delete(x,li)) collect cons(x,y)$


%
% default_ordering_function(p,i) is the default function which is placed
% in each of the entries of the orderings_ vector and acts as a
% default unless the user decides to place something different. This
% allows us to have all the standard orderings and to support
% user-defined ones via different ordering_functions.
%
% p      -       list of derivatives to be sorted in the format used by
%               the decoupling routines, i.e. ( (f_1 . power)
%               (f_2 . power) (f_3 . power) ),
% i      -       ordering w.r.t. which we want to work
%

symbolic procedure default_ordering_function(p,i)$
begin scalar ordered_p, fl_from_order, vl_from_order$
   if print_ and tr_orderings then
      <<
         terpri()$ write "default_ordering_function("$
         write p,",",i$
            write ")"
      >>$
   vl_from_order := car(getv(orderings_,i))$
   fl_from_order := cadr(getv(orderings_,i))$
   if tr_orderings then
      <<
         terpri()$ write "variables list from ordering ",i," is :",
            vl_from_order$
         terpri()$ write "functions list from ordering ",i," is :",
            fl_from_order
      >>$
   %
   % This one-liner should do the trick
   %
   ordered_p := sort_derivs(p, fl_from_order, vl_from_order);
   if tr_orderings then
      <<
         terpri()$ write "ordered: ",ordered_p
      >>$
   return ordered_p$
end$

%
% orderings_prop_list_all() returns a list of all orderings in the
% format which we use for the property list of each equation, i.e.
%
% ( \omega_1 \omega_2 ... \omega_n )
%
% where each \omega_i is the index in the orderings_ vector
%

symbolic procedure orderings_prop_list_all()$
begin scalar i, l$
   if print_ and tr_orderings then
   <<
      terpri()$ write "orderings_prop_list_all()"$
   >>$
   for i:=0:sub1 upbv(orderings_) do <<
      l := append(l,list(i))$
   >>$
   if tr_orderings then
   <<
      terpri()$ write "list: ", l$
   >>$
   return l$
end$

%
% orderings_add_function(f) adds the given function to all the
% orderings, it is assumed that this is a new function appearing from
% an integration.
% In theory we should add the function to all orderings somewhere and
% then add n! orderings with all the possible combinations (n is the
% length of the orderings vector). This is rather unfortunate.
%
% !FIXME! What we do at the moment is add it at the end of each
% function list.
%

symbolic procedure orderings_add_function(f)$
begin scalar i, vl, fl, ofn$
   if print_ and tr_orderings then
   <<
      terpri()$ write "orderings_add_function(",f,")"$
   >>$
   for i:=0:sub1 upbv(orderings_) do <<
      vl := car(getv(orderings_,i))$
      fl := cadr(getv(orderings_,i))$
      ofn := cddr(getv(orderings_,i))$
      putv(orderings_,i,append(list(vl,append(fl,list(f))),ofn))$
   >>$
   if tr_orderings then
   <<
      terpri()$ write "new orderings vector:"$
      terpri()$ write orderings_$
   >>$
end$

%
% orderings_delete_function(f) is symmetric to add_function() but of
% course the problem here would be to find out which orderings have
% become a duplicate (if any).
% !FIXME! Example of a problem: assume we have the following fn
% situation within a couple of orderings
%
% (f g h)
% (g f h)
%
% Now, orderings_delete_function(f) will give us
%
% (g h)
% (g h)
%
% Oops!
%

symbolic procedure orderings_delete_function(f)$
begin scalar i, vl, fl, ofn$
   if print_ and tr_orderings then
   <<
      terpri()$ write "orderings_delete_function(",f,")"$
   >>$
   for i:=0:sub1 upbv(orderings_) do <<
      vl := car(getv(orderings_,i))$
      fl := cadr(getv(orderings_,i))$
      ofn := cddr(getv(orderings_,i))$
      putv(orderings_,i,append(list(vl,delete(f,fl)),ofn))$
   >>$
   if tr_orderings then
   <<
      terpri()$ write "new orderings vector:"$
      terpri()$ write orderings_$
   >>$
end$

%
% End of module
%

endmodule$

end$

