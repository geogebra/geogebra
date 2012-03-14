module degsets;   % Degree set processing.

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


fluid '(!*trallfac
        !*trfac
        bad!-case
        best!-set!-pointer
        dpoly
        factor!-level
        factor!-trace!-list
        factored!-lc
        irreducible
        modular!-info
        one!-complete!-deg!-analysis!-done
        previous!-degree!-map
        split!-list
        valid!-image!-sets);

symbolic procedure check!-degree!-sets(n,multivariate!-case);
% MODULAR!-INFO (vector of size N) contains the modular factors now.
  begin scalar degree!-sets,w,x!-is!-factor,degs;
    w:=split!-list;
    for i:=1:n do <<
      if multivariate!-case then
        x!-is!-factor:=not numberp get!-image!-content
          getv(valid!-image!-sets,cdar w);
      degs:=for each v in getv(modular!-info,cdar w) collect ldeg v;
      degree!-sets:=
        (if x!-is!-factor then 1 . degs else degs)
              . degree!-sets;
      w:=cdr w >>;
    check!-degree!-sets!-1 degree!-sets;
    best!-set!-pointer:=cdar split!-list;
    if multivariate!-case and factored!-lc then <<
      while null(w:=get!-f!-numvec
           getv(valid!-image!-sets,best!-set!-pointer))
       and (split!-list:=cdr split!-list) do
        best!-set!-pointer:=cdar split!-list;
      if null w then bad!-case:=t >>;
            % make sure the set is ok for distributing the
            % leading coefft where necessary;
  end;

symbolic procedure check!-degree!-sets!-1 l;
  % L is a list of degree sets. Try to discover if the entries
  % in it are consistent, or if they imply that some of the
  % modular splittings were 'false'.
  begin scalar i,degree!-map,degree!-map1,dpoly,
               plausible!-split!-found,target!-count;
    factor!-trace <<
       prin2t "Degree sets are:";
       for each s in l do <<
          prin2 "     ";
          for each n in s do <<
             prin2 " "; prin2 n >>;
          terpri() >> >>;
    dpoly:=sum!-list car l;
    target!-count:=length car l;
    for each s in cdr l do
        target!-count:=min(target!-count,length s);
    % This used to be IMIN, but since it was the only use, it was
    % eliminated.
    if null previous!-degree!-map then <<
      degree!-map:=mkvect dpoly;
    % To begin with all degrees of factors may be possible;
      for i:=0:dpoly do putv(degree!-map,i,t) >>
    else <<
      factor!-trace "Refine an existing degree map";
      degree!-map:=previous!-degree!-map >>;
    degree!-map1:=mkvect dpoly;
    for each s in l do <<
    % For each degree set S I will collect in DEGREE-MAP1 a
    % bitmap showing what degree factors would be consistent
    % with that set. By ANDing together all these maps
    % (into DEGREE-MAP) I find what degrees for factors are
    % consistent with the whole of the information I have.
      for i:=0:dpoly do putv(degree!-map1,i,nil);
      putv(degree!-map1,0,t);
      putv(degree!-map1,dpoly,t);
      for each d in s do for i:=dpoly#-d#-1 step -1 until 0 do
        if getv(degree!-map1,i) then
           putv(degree!-map1,i#+d,t);
      for i:=0:dpoly do
        putv(degree!-map,i,getv(degree!-map,i) and
             getv(degree!-map1,i)) >>;
    factor!-trace <<
        prin2t "Possible degrees for factors are: ";
        for i:=1:dpoly#-1 do
          if getv(degree!-map,i) then << prin2 i; prin2 " " >>;
        terpri() >>;
    i:=dpoly#-1;
    while i#>0 do if getv(degree!-map,i) then i:=-1
                 else i:=i#-1;
    if i=0 then <<
       factor!-trace
          prin2t "Degree analysis proves polynomial irreducible";
       return irreducible:=t >>;
    for each s in l do if length s=target!-count then begin
      % Sets with too many factors are not plausible anyway.
      i:=s;
      while i and getv(degree!-map,car i) do i:=cdr i;
      % If I drop through with I null it was because the set was
      % consistent, otherwise it represented a false split;
      if null i then plausible!-split!-found:=t end;
    previous!-degree!-map:=degree!-map;
    if plausible!-split!-found or one!-complete!-deg!-analysis!-done
      then return nil;
%    PRINTC "Going to try getting some more images";
    return bad!-case:=t
  end;

symbolic procedure sum!-list l;
   if null cdr l then car l else car l #+ sum!-list cdr l;

endmodule;

end;
