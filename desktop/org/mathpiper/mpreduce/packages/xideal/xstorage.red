module xstorage;

% Storage and retrieval of critical pairs and polynomials.

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


Comment. Critical pairs and polynomials are stored in a search tree,
         called an xset here:

        xset            ::= empty_xset | item . xset
        empty_xset      ::= any . nil
        item            ::= any

All changes to xset are made destructively as side-effects.

endcomment;


symbolic smacro procedure xset_ptrs c;
   cdr c;


symbolic smacro procedure left_xset c;
   cadr c;


symbolic smacro procedure right_xset c;
   cddr c;


symbolic procedure find_item(pr,c);
   % pr:item, c:xset -> find_item:xset|nil
   % if pr in c, returns pointer to pr, otherwise nil
   if empty_xsetp c then nil
   else find_item(pr,left_xset c) or
        (if xset_item c = pr then c) or
        find_item(pr,right_xset c);


symbolic procedure add_item(pr,c);
   % pr:item, c:xset -> add_item:nil
   % add new item pr to structure c as side-effect
   % goes left iff xkey pr < xkey xset_item c
   if empty_xsetp c then
    <<xset_item c := pr;
      xset_ptrs c := empty_xset() . empty_xset();>>
   else if monordp(xkey xset_item c,xkey pr) then
      add_item(pr,left_xset c)
   else
      add_item(pr,right_xset c);


symbolic procedure remove_item(pr,c);
   % pr:item, c:xset -> remove_item:item or nil
   % deletes pr, if present, from c as side-effect
   if c := find_item(pr,c) then remove_root_item c;


symbolic procedure remove_least_item c;
   % c:xset -> remove_least_item:item
   % returns "least" item in structure and deletes it as side-effect
   if empty_xsetp c then rederr "How did we get here?"
   else if empty_xsetp left_xset c then remove_root_item c
   else remove_least_item left_xset c;


symbolic procedure remove_root_item c;
   % c:xset -> remove_root_item:item
   % deletes first item in c, which is not empty
   begin scalar x,y;
   x := left_xset c; y := xset_item c;
   xset_item c := xset_item right_xset c;
   xset_ptrs c := xset_ptrs right_xset c;
   if not empty_xsetp x then % graft x onto the left-most part of c
    <<while not empty_xsetp c do c := left_xset c;
      xset_item c := xset_item x;
      xset_ptrs c := xset_ptrs x>>;
   return y;
   end;


symbolic procedure remove_items(c,u);
   % c:xset of lists, u:list -> remove_items:nil
   % removes all items containing elements of u from c
   begin
   if empty_xsetp c then return;
   remove_items(left_xset c,u);
   remove_items(right_xset c,u);
   if xnp(u,xset_item c) then remove_root_item c;
   end;

endmodule;

end;
