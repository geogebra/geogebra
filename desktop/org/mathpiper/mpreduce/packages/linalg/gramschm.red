module gramchmd;

%**********************************************************************%
%                                                                      %
% Computation of the Gram Schmidt Orthonormalisation process. The      %
% input vectors are represented by lists.                              %
%                                                                      %
% Authors: Karin Gatermann (used symbolically in her symmetry package).%
%          Matt Rebbeck (first few lines of code that make it          %
%                        available from the user level).   May 1994.   %
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




put('gram_schmidt,'psopfn,'gram_schmidt1); % To allow variable input.
symbolic procedure gram_schmidt1(vec_list);
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
    % Deal with the possibility of the user entering a list of lists.
    if pairp vec_list and pairp car vec_list and caar vec_list = 'list
     and pairp cdar vec_list and pairp cadar vec_list
      and caadar vec_list = 'list
       then vec_list := cdar vec_list;
    vec_list := convert_to_sq(vec_list);
    % This bit does all the real work.
    gs_list := gram!+schmid(vec_list);
    return convert_from_sq(gs_list);
  end;



symbolic procedure convert_to_sq(vec_list);
  %
  % Takes algebraic list and converts to sq form for input into
  % GramSchmidt.
  %
  begin
    scalar sq_list;
    sq_list := for each list in vec_list collect
      for each elt in cdr list collect simp!* elt;
    return sq_list;
  end;



symbolic procedure convert_from_sq(sq_list);
  %
  % Converts sq_list to a readable (from algebraic mode) form.
  %
  begin
    scalar gs_list;
    gs_list := 'list.for each elt1 in sq_list collect
                'list.for each elt in elt1 collect prepsq elt;
    return gs_list;
  end;


%
% All the rest is Karin's.
%

symbolic procedure vector!+p(vector1);
  %
  % returns the length of a vector
  % vector -- list of sqs
  %
  begin
    if length(vector1)>0 then return t;
  end;



symbolic procedure get!+vec!+dim(vector1);
  %
  % returns the length of a vector
  % vector -- list of sqs
  %
  begin
    return length(vector1);
  end;



symbolic procedure get!+vec!+entry(vector1,elem);
  %
  % returns the length of a vector
  % vector -- list of sqs
  %
  begin
    return nth(vector1,elem);
  end;



symbolic procedure mk!+vec!+add!+vec(vector1,vector2);
  %
  % returns a vector= vector1+vector2 (internal structure)
  %
  begin
    scalar ent,res,h;
    res:=for ent:=1:get!+vec!+dim(vector1) collect
    <<
      h:= addsq(get!+vec!+entry(vector1,ent),
             get!+vec!+entry(vector2,ent));
      h:=subs2 h where !*sub2=t;
      h
    >>;
    return res;
  end;



symbolic procedure mk!+squared!+norm(vector1);
  %
  % returns a scalar= sum vector_i^2 (internal structure)
  %
  begin
    return mk!+inner!+product(vector1,vector1);
  end;



symbolic procedure my!+nullsq!+p(scal);
  %
  % returns true, if ths sq is zero
  %
  begin
    if null(numr( scal)) then return t;
  end;



symbolic procedure mk!+null!+vec(dimen);
  %
  % returns a vector of zeros
  %
  begin
    scalar nullsq,i,res;
    nullsq:=(nil ./ 1);
    res:=for i:=1:dimen collect nullsq;
    return res;
  end;



symbolic procedure null!+vec!+p(vector1);
  %
  % returns a true, if vector is the zero vector
  begin
    if my!+nullsq!+p(mk!+squared!+norm(vector1)) then return t;
  end;



symbolic procedure mk!+normalize!+vector(vector1);
  %
  % returns a normalized vector (internal structure)
  %
  begin
    scalar scalo,vecres;
    scalo:=simp!* {'sqrt, mk!*sq(mk!+squared!+norm(vector1))};
    if my!+nullsq!+p(scalo) then
     vecres:= mk!+null!+vec(get!+vec!+dim(vector1))
     else
     <<
       scalo:=simp prepsq scalo;
       scalo:=quotsq((1 ./ 1),scalo);
       vecres:= mk!+scal!+mult!+vec(scalo,vector1);
     >>;
    return vecres;
  end;



symbolic procedure mk!+Gram!+Schmid(vectorlist,vector1);
  %
  % returns a vectorlist of orthonormal vectors
  % assumptions: vectorlist is orthonormal basis, internal structure
  %
  begin
    scalar i,orthovec,scalo,vectors;
    orthovec:=vector1;
    for i:=1:(length(vectorlist)) do
    <<
      scalo:= negsq(mk!+inner!+product(orthovec,nth(vectorlist,i)));
      orthovec:=mk!+vec!+add!+vec(orthovec,
         mk!+scal!+mult!+vec(scalo,nth(vectorlist,i)));
    >>;
    orthovec:=mk!+normalize!+vector(orthovec);
    if null!+vec!+p(orthovec) then vectors:=vectorlist
     else vectors:=add!+vector!+to!+list(orthovec,vectorlist);
    return vectors;
  end;



symbolic procedure Gram!+Schmid(vectorlist);
  %
  % returns a vectorlist of orthonormal vectors
  %
  begin
    scalar ortholist,i;
    if length(vectorlist)<1
      then rederr "Error in Gram Schmidt: no input.";
    if vector!+p(car vectorlist) then ortholist:=nil
     else rederr "Error in Gram_schmidt: empty input.";
    for i:=1:length(vectorlist) do
     ortholist:=mk!+Gram!+Schmid(ortholist,nth(vectorlist,i));
    return ortholist;
  end;



symbolic procedure add!+vector!+to!+list(vector1,vectorlist);
  %
  % returns a list of vectors consisting of vectorlist
  % and the vector1 at the end
  % internal structure
  begin
    return append(vectorlist,list(vector1));
  end;



symbolic procedure mk!+inner!+product(vector1,vector2);
  %
  % returns the inner product of vector1 and vector2
  % (internal structure)
  %
  begin
    scalar z,sum,vec2;
    if not(get!+vec!+dim(vector1) = get!+vec!+dim(vector2)) then rederr
   "Error in Gram_schmidt: each list in input must be the same length.";
    sum:=(nil ./ 1);
    if !*complex then vec2:=mk!+conjugate!+vec(vector2)
     else vec2:=vector2;
    for z:=1:get!+vec!+dim(vector1) do
     sum:=addsq(sum,multsq(
            get!+vec!+entry(vector1,z),
            get!+vec!+entry(vec2,z)
                           )
                );
    sum:=subs2 sum where !*sub2=t;
    return sum;
  end;



symbolic procedure mk!+scal!+mult!+vec(scal1,vector1);
  %
  % returns a vector= scalar*vector (internal structure)
  %
  begin
    scalar entry,res,h;
    res:=for each entry in vector1 collect
    <<
      h:=multsq(scal1,entry);
      h:=subs2 h where !*sub2=t;
      h
    >>;
    return res;
  end;


endmodule;  % gram_schmidt.

end;
