module xColor;

%----------------------------------------------------------------------
% File:      xcolor.red
% Purpose:   Evaluation of colour factor for SU(n) gauge group
% Author:    A.Kryukov
% E-address: kryukov@npi.msu.su
% Vertion:   4.2.1
% Release:   Aug. 17, 1994
%----------------------------------------------------------------------
% Revision: 10/03/91  Start
%           17/08/94  RemoveG2
%           11/03/91  Split3GV
%           11/03/91  Exist3GV, ExistQGV
%           12/03/91  Put's and so on
%           14/03/91  CError
%           15/03/91  ChkCG
%           19/03/91  Color1
%           19/03/91  ZCoefP
%           17/08/94  RemoveG1
%----------------------------------------------------------------------

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


%----------------------------------------------------------------------
%                         xColor package.
%----------------------------------------------------------------------

  imports AddSQ,MultSQ,NegSQ,QuotSQ,ExptSQ$
  exports Color0$

create!-package('(xcolor cface),'(contrib physics));

%----------------------------------------------------------------------
%
% Structure definitions.
% ----------------------
%
% c-grpah ::= (v1 v2 ...), where vK - vertex.
% c0-graph::= (sq . c-graph), where sq - standard quotient.
% vertex  ::= (vtype e1 e2 e3), where eI is name of corresponding edge.
% vtype   ::= G3|QG|QX, G3 - three gluon vertex type,
%                       QG - quark-gluon vertex type,
%                       GX - quark-gluon vertex type with free gluon
%                            (not yet implemented).
% If vtype = G3 then e1,e2,e3 are gluons.Its order is clock.
% If vtype = QG then e1 is in-quark, e2 is out-quark and e3 is a gluon.
%----------------------------------------------------------------------
%
% Example:
% --------
%             e1
%        ----->------
%       /            \
%       |     e2     |
%    v1 *............* v2  <=> c0=((1 . 1) (QG e3 e1 e2) (QG e1 e3 e2))
%       |            |
%       \     e3     /
%        ----->------
%
% Here: ----->----- quark,
%       ........... gluon.
%----------------------------------------------------------------------
%
%                   Transformation rules.
%                   ---------------------
%        (see: A.Kryukov & A.Rodionov
%              Program "COLOR" for computing the group-theoretic
%              weight of Feynman diagrams in non-abelian gauge theories.
%              Comp. Phys. Comm., 48(1988),327-334)
%
%        :                  (       :                    :        )
%        :            1     (       :                    :        )
%        :         = ---    (       *         -          *        )  (9)
%        :            A     (      / \                  / \       )
%   .....*.....             ( ....*-<-*....        ....*->-*....  )
%
%   --<--*--<--             ( -<--     --<-            --<--      )
%        :                  (     \   /          1                )
%        :         =  A     (      | |        - ---               ) (10)
%        :                  (     /   \          n                )
%   --<--*--<--             ( -<--     --<-            --<--      )
%
% Here: n - order of SU(n) group,
%       A - normalization factor. Sp(TiTj) = A*Delta(i,j).           (3)
%
%----------------------------------------------------------------------

%----------------------- Selector/Constructor -------------------------

symbolic smacro procedure GetCoef g0$ car g0$
symbolic smacro procedure GetVL g0$ cdr g0$

symbolic smacro procedure PutCoef(g0,c)$ rplacA(g0,c)$
symbolic smacro procedure PutVL(g0,vl)$ rplacD(g0,vl)$

symbolic smacro procedure GetTV v$ car v$
symbolic smacro procedure GetE1 v$ cadr v$
symbolic smacro procedure GetE2 v$ caddr v$
symbolic smacro procedure GetE3 v$ cadddr v$
symbolic smacro procedure GetInQ v$ GetE1 v$
symbolic smacro procedure GetOutQ v$ GetE2 v$

symbolic smacro procedure PutTV(v,tv)$ rplacA(v,tv)$
symbolic smacro procedure PutE1(v,e)$ rplacA(cdr v,e)$
symbolic smacro procedure PutE2(v,e)$ rplacA(cddr v,e)$
symbolic smacro procedure PutE3(v,e)$ rplacA(cdddr v,e)$
symbolic smacro procedure PutInQ(v,e)$ PutE1(v,e)$
symbolic smacro procedure PutOutQ(v,e)$ PutE2(v,e)$

symbolic smacro procedure MkG0(c,g0)$ c . g0$

symbolic smacro procedure ChkTV(v,tv)$ GetTV v eq tv$
symbolic smacro procedure QGVp v$ ChkTV(v,'QG)$
symbolic smacro procedure G3Vp v$ ChkTV(v,'G3)$

symbolic smacro procedure ZCoefP g0$ null numr GetCoef g0$

symbolic smacro procedure MkCopyG0 g0$
  %--------------------------------------------------------------------
  % Make a copy of structure g0 without copying coeffitient.
  %--------------------------------------------------------------------
  GetCoef g0 . MkCopy GetVL g0$

symbolic smacro procedure ChkHP v$
  %--------------------------------------------------------------------
  % Check headpole.
  %--------------------------------------------------------------------
  %            -->--                     ........
  %           /     \                   :        :
  %          |       |                  :        :
  %   .......*v      | = 0,     ........*v       : = 0
  %          |       |                  :        :
  %           \     /                   :        :
  %            --<--                     ........
  %--------------------------------------------------------------------
  GetE1 v eq GetE2 v or GetE1 v eq GetE3 v or GetE2 v eq GetE3 v$

%----------------------------- Debug ----------------------------------

%symbolic smacro procedure DMessage x$
%  << prin2 "====>"$ print x >>$

%----------------------------- Others ---------------------------------

symbolic procedure CError u$
  %--------------------------------------------------------------------
  % Output error message and interupt evaluation.
  %--------------------------------------------------------------------
  << terpri!* t$
     for each x in "***** xCOLOR:" . u do <<
       prin2!* " "$
       varpri(x,x,nil)
    >>$
    terpri!* t$
    Error1()
  >>$

symbolic procedure RemoveV(g0,v)$
  %--------------------------------------------------------------------
  % Remove vertex v from g0.
  % g0 is modified.
  %--------------------------------------------------------------------
  if null g0 then CError list("Vertex",v,"is absent.")
  else if cadr g0 eq v then rplacD(g0,cddr g0)
  else RemoveV(cdr g0,v)$

symbolic smacro procedure ExistQGV g0$
  %--------------------------------------------------------------------
  % Find quark-gluon vertex in g0.
  % Return quark-gluon vertex or nil.
  %--------------------------------------------------------------------
  assoc('QG,GetVL g0)$

symbolic smacro procedure Exist3GV g0$
  %--------------------------------------------------------------------
  % Find three-gluon vertex in g0.
  % Return three-gluon vertex or nil.
  %--------------------------------------------------------------------
  assoc('G3,GetVL g0)$

symbolic procedure MkCopy u$
  %--------------------------------------------------------------------
  % Make a copy of any structures.
  %--------------------------------------------------------------------
  if atom u then u else MkCopy car u . MkCopy cdr u$

symbolic smacro procedure RevV(v,e)$
  %--------------------------------------------------------------------
  % Revolve v such that e become the first edge.
  % v is modified.
  %--------------------------------------------------------------------
  if null G3Vp v or null memq(e,cdr v)
    then CError list("Edge",e,"is absent in vertex",v)
  else RevV0(v,e)$

symbolic procedure RevV0(v,e)$
  %--------------------------------------------------------------------
  % Revolve v such that e become the first edge.
  % v is modified.
  %--------------------------------------------------------------------
  if GetE1 v eq e then v
  else begin scalar w$
         w := GetE1 v$
         PutE1(v,GetE2 v)$
         PutE2(v,GetE3 v)$
         PutE3(v,w)$
         return RevV0(v,e)$
       end$ % RevV0

%------------------------ Global/Fluid --------------------------------

global '(SU_order Spur_TT n!*!*2!-1)$

SU_order := '(3 . 1)$        % default value
Spur_TT  := '(1 . 2)$        % default value
n!*!*2!-1:= '(8 . 1)$        % default value

%----------------------------------------------------------------------

symbolic procedure Color0 g0$
  %--------------------------------------------------------------------
  % g0 - c-graph.
  % Return colour factor (s.q.).
  %--------------------------------------------------------------------
  if ChkCG g0 then
    MultSQ(AFactor g0,Color1(MkG0(1 ./ 1,MkCopy g0),nil,nil ./ 1))
  else CError list "This is impossible!"$

symbolic procedure ChkCG g0$
  %--------------------------------------------------------------------
  % Check structure g0.
  % Return t if g0 is ok else output message and interupt program.
  %--------------------------------------------------------------------
  begin scalar x,u,vl,z$
    vl := g0$
    while vl do <<
      x := car vl$
      if GetTV x eq 'QG then <<
          if (z:=assoc(GetInQ x,u)) then
            if cdr z eq 'OutQ then rplacD(z,'ok)
            else CError
               list(car z,"can not use as in-quark in vertex",x)
          else u:=(GetInQ x . 'InQ) . u$
          if (z:=assoc(GetOutQ x,u)) then
            if cdr z eq 'InQ then rplacD(z,'ok)
            else CError
               list(car z,"can not use as out-quark in vertex",x)
          else u:=(GetOutQ x . 'OutQ) . u$
          if (z:=assoc(GetE3 x,u)) then
            if cdr z eq 'Gluon then rplacD(z,'ok)
            else CError list(car z,"can not use as gluon in vertex",x)
          else u:=(GetE3 x . 'Gluon) . u$
        >>
      else if GetTV x eq 'G3 then <<
          if (z:=assoc(GetE1 x,u)) then
            if cdr z eq 'Gluon then rplacD(z,'ok)
            else CError list(car z,"can not use as gluon in vertex",x)
          else u:=(GetE1 x . 'Gluon) . u$
          if (z:=assoc(GetE2 x,u)) then
            if cdr z eq 'Gluon then rplacD(z,'ok)
            else CError list(car z,"can not use as gluon in vertex",x)
          else u:=(GetE2 x . 'Gluon) . u$
          if (z:=assoc(GetE3 x,u)) then
            if cdr z eq 'Gluon then rplacD(z,'ok)
            else CError list(car z,"can not use as gluon in vertex",x)
          else u:=(GetE3 x . 'Gluon) . u$
        >>
      else CError list("Invalid type of vertex",x)$
      vl := cdr vl$
    >>$
    while u do <<
      X := car u$
      if null(cdr x eq 'ok) then
        CError list(car x,"is a free particle. Not yet implemented.")
      else if null idp car x then
        CError list(car x,"invalid as a name of particle.")
      else u:=cdr u$
    >>$
    return t$  % o.k.
  end$ % ChkCG

symbolic procedure AFactor g0$
  %--------------------------------------------------------------------
  % Calculate A-factor of g0:
  % A**(<num. of QG-vert.>+<num. of 3G-vert.>-<num. of free gluons>)/2
  % Return A-factor (s.q.).
  %--------------------------------------------------------------------
  begin scalar n$
    n := 0$
    for each x in g0 do if QGVp x or G3Vp x then n := n + 1$
    if remainder(n,2) neq 0 then
      CError list("Invalid structure of c0-graph.",
        if null g0 then nil
        else if null cdr g0 then car g0
        else 'times . g0)$
    return ExptSQ(Spur_TT,n/2)$
  end$ % AFactor

%symbolic procedure Color1(g0,st,result)$ Color2(g0,st,result)$

symbolic procedure Color1(g0,st,result)$
  %--------------------------------------------------------------------
  % g0 - c0-graph,
  % st - stack for still uncalculated graphs,
  % Return results - colour factor (s.q.).
  %--------------------------------------------------------------------
  if ZCoefP g0 or null GetVL g0 then
    if null st then AddSQ(GetCoef g0,result)
    else Color1(car st,cdr st,AddSQ(GetCoef g0,result))
  else begin scalar v$
%
%  Patch from 15/08/93
%
%   if (v:=Exist3GV g0) then <<
%        if ChkHP v then return Color1((nil ./ 1) . nil,st,result)$
%        g0 := Split3GV(g0,v)$
%        return Color1(car g0,cdr g0 . st,result)
%     >>
    if (v:=ExistQGV g0) then <<
        if ChkHP v then return Color1((nil ./ 1) . nil,st,result)$
        g0 := RemoveG(g0,v)$
        return
          Color1(car g0
                ,if cdr g0 then (cdr g0 . st) else st
                ,result
                )
      >>
    else if (v:=Exist3GV g0) then <<
         if ChkHP v then return Color1((nil ./ 1) . nil,st,result)$
         g0 := Split3GV(g0,v)$
         return Color1(car g0,cdr g0 . st,result)
      >>
    else CError list("Invalid structure of c0-graph."
           ,if null g0 then nil
            else if null cdr g0 then car g0
            else 'times . g0
                    )$
  end$ % Color1

symbolic procedure RemoveG(g0,v1)$
  %--------------------------------------------------------------------
  % Remove gluon which containe in quark-gluon vertex(v1).
  % Return pair (g1.g2), where g1 and g2 are graphs.
  %--------------------------------------------------------------------
  begin scalar v2$
    v2 := FindE(GetVL g0,GetE3 v1)$
    if car v2 eq v1 then v2 := FindE(cdr v2,GetE3 v1)$
    if null v2 then CError list("Free edge",GetE3 v1,"in vertex",v1)$
    v2 := car v2$
    if ChkHP v2 then return (((nil ./ 1) . nil) . nil)$
    if QGVp v2 then return RemoveG1(g0,v1,v2)
    else if G3Vp v2 then return RemoveG2(g0,v1,v2)
    else CError list("Invalid type of vertex",v1)$
  end$ % RemoveG

symbolic procedure FindE(vl,e)$
  %--------------------------------------------------------------------
  % Find vertex included edge e in vertex list vl.
  % Return vertex list started by vertex included e or nil.
  %--------------------------------------------------------------------
  if null vl then nil
  else if memq(e,cdar vl) then vl
  else FindE(cdr vl,e)$

symbolic procedure RemoveG1(g0,v1,v2)$
  %--------------------------------------------------------------------
  % Remove gluon between two quark-gluon verticies v1 and v2.
  % Return pair (g1.g2), where g1 and g2 are graphs.
  %--------------------------------------------------------------------
  begin scalar v3,v6,g1,w$
    RemoveV(g0,v1)$
    RemoveV(g0,v2)$
    %------------------------------------------------------------------
    %      --<--
    %     /     \
    %    |       |
    %  v1*.......*v2  =  n**2-1
    %    |       |
    %     \     /
    %      -->--
    %------------------------------------------------------------------
    %DMessage "2. 3j-symbol?"$
    if GetInQ v1 eq GetOutQ v2 and GetOutQ v1 eq GetInQ v2 then
      return (MkG0(MultSQ(n!*!*2!-1,GetCoef g0),GetVL g0) . nil)$
    %------------------------------------------------------------------
    %           v1
    %  v3--<----*--<--                    v3--<----
    %           :     \                            \
    %           :      |                            |
    %           :      |  =  (n**2-1)/n             |
    %           :      |                            |
    %           :     /                            /
    %  v5-->----*-->--                    v5-->----
    %           v2
    %------------------------------------------------------------------
    %DMessage "3. Arc.?"$
    v3 := FindE(GetVL g0,GetOutQ v1)$
    if GetInQ v1 eq GetOutQ v2 then <<
        if v3 then PutInQ(car v3,GetInQ v2)
        else CError list("Free edge",GetOutQ v1,"in vertex",v1)$
        return
          (MkG0(MultSQ(QuotSQ(n!*!*2!-1,SU_order),GetCoef g0),GetVL g0)
          . nil
          )$
      >>$
    v6 := FindE(GetVL g0,GetOutQ v2)$
    if GetOutQ v1 eq GetInQ v2 then <<
        if v6 then PutInQ(car v6,GetInQ v1)
        else CError list("Free edge",GetOutQ v2,"in vertex",v2)$
      return
        (MkG0(MultSQ(QuotSQ(n!*!*2!-1,SU_order),GetCoef g0),GetVL g0)
        . nil
        )$
      >>$
    %------------------------------------------------------------------
    %           v1
    %  v3--<--*--<--     v3--<--     --<--v4         v3--<--v4
    %         :                 \   /
    %         :                  | |             1
    %         :       =          | |          - ---            (10')
    %         :                  | |             n
    %         :                 /   \
    %  v5-->--*-->--     v5-->--     -->--v6         v5-->--v6
    %         v2
    %                            (a)                    (b)
    %------------------------------------------------------------------
    %DMessage "4. Common case."$
    if null v3 or null v6 then
      CError list("Invalid structure of c-graph"
                 ,if null g0 then nil
                  else if null cdr g0 then car g0
                  else 'times . g0
                 )$
    v3 := car v3$
    v6 := car v6$
    PutInQ(v3,GetInQ v2)$
    PutInQ(v6,GetInQ v1)$
    %------------------------------------------------------------------
    % Diagram (b)
    %------------------------------------------------------------------
    g1 := MkCopyG0 g0$
    w := GetVL g1$
    v3 := car member(v3,w)$
    v6 := car member(v6,w)$
    PutInQ(v3,GetInQ v1)$
    PutInQ(v6,GetInQ v2)$
    %------------------------------------------------------------------
    return
      (g0 . MkG0(MultSQ(QuotSQ(('-1 ./ 1),SU_order),GetCoef g1),w))$
  end$

symbolic procedure RemoveG2(g0,v1,v2)$
  %--------------------------------------------------------------------
  % Remove gluon between quark-gluon(v1) and three-gluon(v2) verticies.
  % Return pair (g1.g2), where g1 and g2 are graphs.
  %--------------------------------------------------------------------
  begin scalar g1,z,u1,u2$
    v2 := RevV(v2,GetE3 v1)$
    PutTV(v2,'QG)$
    g1 := MkCopyG0 g0$
    u1 := car member(v1,g1)$
    u2 := car member(v2,g1)$
    %------------------------------------------------------------------
    %      2  v2 3              3   v2 3               3   v2 3
    %  v6.....*.....v5       v6..   *......v5      v6...   *.....v5
    %         :                  .  |\                  . /|
    %         :1         =        . | \2       -         . |1
    %         :                    .|  \                / .|
    %  v4-->--*-->--v3       v4-->--*   ->-v3      v4->-   *--->-v3
    %         v1                 1  v1                     v1  2
    %
    %                              (a)                    (b)
    %------------------------------------------------------------------
    %DMessage "2. Common case."$
    z := GetE2 v1$
    PutE2(v1,GetE3 v1)$
    PutE3(v1,GetE2 v2)$
    PutE2(v2,z)$
    %------------------------------------------------------------------
    % Diagram (b)
    %------------------------------------------------------------------
    z := GetE1 u1$
    PutE1(u1,GetE3 u1)$
    PutE3(u1,GetE2 u2)$
    PutE2(u2,GetE1 u2)$
    PutE1(u2,z)$
    %------------------------------------------------------------------
    return (g0 . MkG0(NegSQ GetCoef g1,GetVL g1))$
  end$ % RemoveG2

symbolic procedure Split3GV(g0,v1)$
  %--------------------------------------------------------------------
  % Split three-gluon verticies v1 onto three quark-gluon verticies.
  % g0 is modified.
  % Return (g1 . g2), where g1 and g2 are graphs.
  %--------------------------------------------------------------------
  begin scalar v5,v6,g1,z$
    %------------------------------------------------------------------
    %         v2                   v2                  v2
    %         :                    :                   :
    %         :                    :                   :
    %         :          =         *v6       -         *v6        (9')
    %         :                   / \                 / \
    %  v4.....*.....v3       ....*-<-*....       ....*->-*....
    %         v1                 v1  v5              v1  v5
    %
    %                             (a)                 (b)
    %------------------------------------------------------------------
    v5 := list('QG,GenSym(),GenSym(),GetE2 v1)$
    v6 := list('QG,GenSym(),GetInQ v5,GetE1 v1)$
    PutTV(v1,'QG)$
    PutE1(v1,GetOutQ v5)$
    PutE2(v1,GetInQ v6)$
    PutVL(g0,v5 . v6 . GetVL g0)$
    %------------------------------------------------------------------
    % Diagram (b)
    %------------------------------------------------------------------
    g1 := MkCopyG0 g0$
    v1 := car member(v1,GetVL g1)$
    v5 := car member(v5,GetVL g1)$
    v6 := car member(v6,GetVL g1)$
    z := GetInQ v1$
    PutE1(v1,GetOutQ v1)$
    PutE2(v1,z)$
    z := GetInQ v5$
    PutE1(v5,GetOutQ v5)$
    PutE2(v5,z)$
    z := GetInQ v6$
    PutE1(v6,GetOutQ v6)$
    PutE2(v6,z)$
    %------------------------------------------------------------------
    return (g0 . MkG0(NegSQ GetCoef g1,GetVL g1))$
  end$ % Split3GV

%----------------------------------------------------------------------
endmodule;

end;
