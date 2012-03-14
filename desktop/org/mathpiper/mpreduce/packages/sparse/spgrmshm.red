%**********************************************************************%
%                                                                      %
% Computation of the Gram Schmidt Orthonormalisation process. The      %
% input vectors are represented by lists.                              %
%                                                                      %
% Authors: Karin Gatermann (used symbolically in her symmetry package).%
%          Matt Rebbeck (first few lines of code that make it          %
%                        available from the user level).   May 1994.   %
%                                                                      %
% Extended by Stephen Scowcroft (June 1995) so that Sparse Vectors can %
% can be used.                                                         %
%                                                                      %
%**********************************************************************%

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


module spgrmshm;

symbolic procedure spgram_schmidt(vec_list);
  %
  % Can take a list of lists(which are representing vectors) or any
  % number of arguments each being a list(again which represent the
  % vectors).
  %
  % Karin used lists of standard quotient elements as vectors so a bit
  % of fiddling is required to get the input/output right.
  %
  begin
    scalar gs_list;
    vec_list:=cdr vec_list;
    % Deal with the possibility of the user entering a list of lists.
    if pairp vec_list and pairp car vec_list and caar vec_list = 'list
     and pairp cdar vec_list and pairp cadar vec_list
      and caadar vec_list = 'list
       then vec_list := cdar vec_list;
    vec_list := spconvert_to_sq(vec_list);
    % This bit does all the real work.
    gs_list := gram!+schmid(vec_list);
    return spconvert_from_sq(gs_list);
  end;

flag('(spgram_schmidt),'opfn);

symbolic procedure spconvert_to_sq(vec_list);
  %
  % Takes algebraic list and converts to sq form for input into
  % GramSchmidt.
  %
  begin
    scalar sq_list,val,res;
   for each list in vec_list do
   <<for i:=1:sprow_dim(cadr list) do
       << for j:=1:spcol_dim(cadr list) do
          << val:= simp!* findelem2(cadr list,i,j);
             res:=(val . res);
          >>;
       >>;
      sq_list:=append(sq_list,list(reverse res));
      res:=nil;
    >>;

    return sq_list;
  end;

symbolic procedure spconvert_from_sq(sq_list);
  %
  % Converts sq_list to a readable (from algebraic mode) form.
  %
  begin
    scalar gs_list,cnt,res,val,len;
    for each elt1 in sq_list do
    << cnt:=0;
       len:=length elt1;
       res:=mkempspmat(len,list('spm,len,1));
      for each elt in elt1 do
       << val:=prepsq elt;
          if not (val = 0) then
           letmtr3(list(res,cnt:=cnt+1),list(nil) . list((1 . val)),res,nil)
           else cnt:=cnt+1;
       >>;
       gs_list:=append(gs_list,{res});
       res:=nil;
     >>;
    return 'list . gs_list;
  end;

endmodule;

end;

%***********************************************************************
%=======================================================================
%
% End of Code.
%
%=======================================================================
%***********************************************************************

