module codopt;   % Generalization of Breuer's Growth Factor Algorithm.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Authors :   J.A. van Hulzen, B.J.A. Hulshof, W.N. Borst.            ;
% ------------------------------------------------------------------- ;

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


symbolic$

%-------------------------------------------------------------------- ;
% The module CODOPT contains:                                         ;
%                                                                     ;
% THE GENERALIZED VERSION OF BREUER'S GROWTH FACTOR ALGORITHM         ;
%                                                                     ;
% A description can be found in :                                     ;
% M.A. Breuer : "Generation of Optimal Code for Expressions via       ;
%      Factorization", Comm.ACM 12, 333-340 (1969).                   ;
% J.A. van Hulzen : "Breuer's Grow Factor Algorithm in Computer       ;
%      Algebra",Proceedings SYMSAC '81 (P.S. Wang, ed.), 100-104, New ;
%      York: ACM(1981).                                               ;
% J.A. van Hulzen : "Code Optimization of Multivariate Polynomial     ;
%      Schemes : A Pragmatic Approach", Proceedings EUROCAL '83 (J.A. ;
%      van Hulzen, ed.),Springer LNCS-series nr 162, 286-300 (1983).  ;
% ------------------------------------------------------------------- ;
%                                                                     ;
% ------ DATA STRUCTURES AND WEIGHTS ------                           ;
% Via FFVAR!! and in combination with SSETVARS(also the CODMAT module);
% a set of input-expressions is decomposed and stored in the "matrix" ;
% CODMAT.                                                             ;
% The Breuer-like searches, for finding common subexpressions (cse's  ;
% for short), concentrate on Zstrt's, defining the primitive parts    ;
% (pp's for short) of input-expressions. These pp's are either linear ;
% expressions (Opval='PLUS) or monomials (Opval='TIMES). The pp's be- ;
% long to larger expressions if CHROW is not NIL at the same level or ;
% if the FarVar-field of the row contains a rowindex (of a father ex- ;
% pression).                                                          ;
% The Zstrt is a list of pairs Z.Such a Z consists of a (column)index,;
% denoted by XIND(Z) or YIND(Z) and an integer value IVAL(Z), being   ;
% the exponent (or coefficient) of the variable corresponding with the;
% column-index, occurring in this pair. In a similar way columns are  ;
% used to define the occurrences of variables in the description of   ;
% the input-expressions( see the CODMAT module).                      ;
% Each row or column has a weight WGHT=((AWght.MWght).HWght), where   ;
% HWght=AWght + 3*MWght. The A(dditive)W(ei)ght is the length of the  ;
% Zstrt. The M(ultiplicative)W(ei)ght is its number of (|IVAL|>1)-ele-;
% ments. The factor 3 reflects the assumption that multiplication is 3;
% times as expensive as addition. The HWghts play an essential role in;
% the heuristics (on which the Breuer searches are based) and are com-;
% puted and stored via application of the procedure INITWGHT (see the ;
% CODMAT module).                                                     ;
% NOTE : It is of course possible to make the factor 3 a parameter.   ;
% This requires some resettings in the weight-routines (see the module;
% CODMAT).                                                            ;
% HWghts can be associated with both rows and columns.                ;
% This allows to produce weightfactors (see the references),  to be   ;
% associated with rows (or columns) to refine heuristic decisions, if ;
% required. The weightfactor of a row(column) is the sum of the HWghts;
% of those columns(rows) which share a non-zero entry with it.Although;
% the use  of weightfactors might improve decision making, its over-  ;
% head in computational cost can be considerable, certainly when the  ;
% CODMAT-matrix is large. The visual intuitive selection-mechanisms   ;
% for cse-building (extend a set of column-indices against the price  ;
% of reducing the number of parents (rows)) can be impractical, becau-;
% se - certainly initially - the number of variables is a fraction of ;
% the number of rows, corresponding with (sub)pp's.                   ;
% So we drop the weightfactors and we select rows instead of columns. ;
% To speed up the row-selection all rows with an equal HWght are col- ;
% lected in a double linked list, using the HiR-fields. These sets are;
% accessible via the elements of the CODHISTO-vector (details are gi- ;
% ven in the CODMAT module, procedure INSHISTO). We recall only that  ;
% CODHISTO(i) = k means that HWght(k) = i and that HiR(k) allows to   ;
% access the FILO-list of rows j with HWght(j) = i.                   ;
% NOTE : These FILO-lists, a kind of buckets, can contain both PLUS-  ;
% and TIMES-rows if both are SETFREE (see the COSYMP module and again ;
% INSHISTO). The operator-type is irrelevant during the Breuer-search.;
% In fact, it is only explicitly required in the procedure ADDCSE.    ;
%                                                                     ;
% ------ THE SEARCHES : THE ESSENTIALS ------                         ;
% Initially the cse's are either linear expressions or monomials. To  ;
% discover them the integer-matrices (CODMAT-parts with PLUS and TIMES;
% Opval-fields,respectively), are heuristically searched for submatri-;
% ces of rank 1 of maximal size. The size is determined, using a      ;
% profit-criterium. A basic scan is used, which can be qualified as   ;
% "test whether the determinant of a (2,2)-matrix of non-zero entries ;
% is zero". Its use is based on information about the row-weights,    ;
% which allow to locate completely dense submatrices. The row-weight  ;
% is a reflection of the arithmetic complexity of the pp,corresponding;
% with the row. Since we want to reduce the arithmetic complecity AC =;
% (n+,n*) of the set of input-expressions, a cse-selection ought to   ;
% contribute to a reduction of the number of additions (n+) and/or the;
% number of multiplications (n*). This is only possible if the cse oc-;
% curs at least twice and if the additive weight AWght is at least 2. ;
% The profit-criterium WSI is based on this assumption. Its actual va-;
% lue is (|Psi|-1) * (|Jsi|-1). Here Psi is the set of Parent- row in-;
% dices and Jsi is the set of indices of columns, which are associated;
% with variables occurring in the cse under construction.             ;
% Once a cse is found its description is removed from the rows,defined;
% by Psi, and from the columns, with indices in Jsi. The cse itself is;
% added to CODMAT as a new row. It has a system-selected name (given  ;
% in the FarVar-field and produced with FNEWSYM (see CODCTL module)), ;
% which is also used as recognizer of the new column added to CODMAT, ;
% to define the occurrences of the new cse (via the Psi-set). In addi-;
% tion the HWghts of the Psi rows, used in the previous resettings are;
% recomputed and reinserted via CODHISTO and the cse-row is entered in;
% CODHISTO, to allow it to play its own role in the optimization. We  ;
% also insert the new cse in the output hierarchy via the ORDR-field  ;
% of the Psi-parents, associated with the cse. We finally remark that ;
% it also might be possible that the cse is identical to one or more  ;
% of its parent-pp's. In this case it might be necessary to migrate   ;
% information from the PLUS(TIMES)-matrix to the TIMES(PLUS)-matrix.  ;
% Further details are given in the source, contained in this module.  ;
%                                                                     ;
% Essentially all searches are done in Zstrt's. A Zstrt is a list of  ;
% pairs (index . value). The ordering in the Zstrt is based on the    ;
% indices. A column-Zstrt contains (positive) row-indices, given in   ;
% descending order. A row-Zstrt contains (negative) column-indices,   ;
% given in ascending order. The indices define relative positions. In ;
% all operations on CODMAT information-pieces (except for MKZEL-calls);
% these relative positions, produced via Rowmax and Rowmin value chan-;
% ges, are needed for information retrieval and information storage.  ;
% These relative CODMAT-positions are used during the searches, i.e.  ;
% the sets (lists) Psi and Jsi are built with them.During the searches;
% ordering is only relevant if the procedure PnthXZZ is used. The ap- ;
% plication PnthXZZ(A,B) delivers the Zstrt B, but after removal of   ;
% the elements preceding the Z-element with the A-index. This Z-elem. ;
% can thus be obtained as CAR(PnthXZZ(A,B)). Since the searches are   ;
% based on row-selection followed by Jsi-resettings, only ordering in ;
% Jsi is relevant. When a cse is found, Psi is ordered, before making ;
% and adding to CODMAT the corresponding Zstrt.                       ;
%                                                                     ;
% ------ DOMAIN CONSIDERATIONS ------                                 ;
% As stipulated above operator considerations are hardly relevant du- ;
% ring cse-searches. Identical tests can be applied for cse's occur-  ;
% ring in linear expressions as well as in monomials, albeit that via ;
% the Expand- and ShrinkProd mechanism additional searches are perfor-;
% med for monomial-cse's, simply because the mathematical context is  ;
% somewhat richer. When allowing various coefficient domains, a dis-  ;
% tinction between coefficient- and exponent searches is needed :     ;
% Assuming MkZel, SetIVal and IVal become generic functions, the fol- ;
% lowing changes in CODOPT are required :                             ;
% - ExtBrsea - A double CODHISTO-mechanism ( to allow to analyse PLUS ;
%              and TIMES rows separately) is required and doubles in  ;
%              fact initialization, as well as appl. of ExtBrsea1.    ;
% - TestPr   - The zero-minor test has to be made generic.            ;
% - RZstrtCse- The GC-computations uses ABS-value computations, which ;
%               ought to be generic, as well as the gcd comp.'s with  ;
% - Gcd2     - This routine must be generic.                          ;
% - CZstrtCse- The ZZcse-construction requires multiplication factor  ;
%              computations, i.e. divisions of domain-elements, which ;
%              ought to be generic.                                   ;
% ------------------------------------------------------------------- ;

% ------------------------------------------------------------------- ;
% The global identifiers needed in this module are :                  ;
% ------------------------------------------------------------------- ;

global '(psi jsi npsi njsi wsi rcoccup roccup1 roccup2 newjsi newnjsi
         codhisto headhisto rowmin rowmax )$

% ------------------------------------------------------------------- ;
% Description of the global variables used in this module (see also   ;
% the CODMAT module):                                                 ;
% ------------------------------------------------------------------- ;
%   Roccup1 : Indices of rows, which become (temporarily) irrelevant  ;
%             during a cse search (see procedure FindOptRow).         ;
%   Roccup2 : Indices of rows, which were (temporarily) selected as   ;
%             candidate-parent row (see procedure FindOptRow).        ;
%   RCoccup : Indices of rows and columns, either used for building   ;
%             the cse or leading to a failure, i.e. to Wsi=0.         ;
%       Psi : Indices of the parents of the cse.                      ;
%      NPsi : Number of elments in Psi.                               ;
%       Jsi : A list of column indices representing the current cse.  ;
%      NJsi : Number of elements in Jsi.                              ;
%    NewJsi : Contains the new Jsi if a certain rowindex is added to  ;
%             Psi (see FINDOPTROW).                                   ;
%   NewNJsi : Number of elements in NewJsi.                           ;
%       Wsi : Profitfunction = (|Psi|-1)*(|Jsi|-1). See proc. TestRed.;
%  CodHisto : Vector representing the Histogram.                      ;
% Headhisto : CodHisto(i) = 0 if i > Headhisto, i.e. the list of rows ;
%             with HWght = HeadHisto is accessible via CodHisto(Head- ;
%             Histo).                                                 ;
%-------------------------------------------------------------------- ;


rcoccup:=roccup1:=roccup2:=nil;


symbolic procedure extbrsea;
% ------------------------------------------------------------------- ;
% The main procedure governing the Breuer-searches. Both,monomials and;
% linear expressions, can be found as cse.                            ;
% ------------------------------------------------------------------- ;
begin scalar further;
   % ---------------------------------------------------------------- ;
   % We start excluding those rows and columns, which are irrelevant  ;
   % for our searches : Either the FarVar-field = -1 (This setting is ;
   % performed by application of the procedure ClearRow, defined in   ;
   % the module CODMAT, and expresses that a row or column is not in  ;
   % use anymore) or = -2 (Columns reservedto store temporarily mono- ;
   % mial information created in ExpandProd and removed in ShrinkProd);
   % ---------------------------------------------------------------- ;
   for x:=rowmin:rowmax do
    if farvar(x)=-1 or farvar(x)=-2
    then setoccup(x)
    else setfree(x);
   % ---------------------------------------------------------------- ;
   % After initialization the searches are performed.                 ;
   % ---------------------------------------------------------------- ;
   initbrsea();
   extbrsea1();
   % ---------------------------------------------------------------- ;
   % The remaining monomials can further be analysed for cse-occurren-;
   % ces when they are temporarily expanded, using a specific addition;
   % chain mechanism (see procedure EXPANDPROD).                      ;
   % ---------------------------------------------------------------- ;
   repeat
    <<expandprod();
      for x:=rowmin:rowmax do
       if not(farvar(x)=-1) and opval(x) eq 'times
        then setfree(x)
        else setoccup(x);
      initbrsea();
      extbrsea1();
      % ------------------------------------------------------------- ;
      % Once the continued searches, based on expanded monomial infor-;
      % mation, are completed, the original monomial-variable informa-;
      % tion structure is restored by shrinking the sets of columns,  ;
      % associated with the various monomial-variables, together into ;
      % the originally used columns (details are given in the procedu-;
      % re SHRINKPROD).                                               ;
      % ------------------------------------------------------------- ;
      further:=shrinkprod();
    >>
   until not(further);
   % ---------------------------------------------------------------- ;
   % Once the Breuer-searches are completed control is passed over to ;
   % IMPROVELAYOUT, before TCHScheme and finally CODFAC are used.     ;
   % TCHScheme allows information migration and CODFAC application of ;
   % the distributive law. Application of IMPROVELAYOUT might lead to ;
   % the conclusion that the Expand-Shrink activities resulted in re- ;
   % dundant cse-names, such as a double name for x^2 or the like.    ;
   % Details are given in OPTIMIZELOOP (see the module CODCTL).       ;
   % ---------------------------------------------------------------- ;
end;

symbolic procedure initbrsea;
% ------------------------------------------------------------------- ;
% The CODMAT-submatrices are prepared for the Breuer-searches.        ;
% The weights are set, the vector CODHISTO gets its initial values    ;
% and redundant information is temporarily removed. It is of course   ;
% needed again for output and eventually during later stages of the   ;
% optimization process, due to information migration. Information is  ;
% redundant when a row or column, i.e a Zstrt, only contains one Z-   ;
% element. This demands for a recursive search through CODMAT, since  ;
% a redundant row can lead to a redundant column if the element they  ;
% share ought to be disregarded.                                      ;
% ------------------------------------------------------------------- ;
begin scalar hlen;
  hlen:=histolen;
  for x:=rowmin:rowmax do
  if free(x) then initwght(x);
  % ----------------------------------------------------------------- ;
  % Only the weights for relevant rows and columns are computed. Once ;
  % the weights are known, the redundancy can be removed using :      ;
  % ----------------------------------------------------------------- ;
  redcodmat();
  % ----------------------------------------------------------------- ;
  % If the vector CODHISTO is already known, it might have been crea- ;
  % ted during a previous use of the Optimizer. In this case its en-  ;
  % tries are set to NIL. Otherwise it is created, before the HWght-  ;
  % information is stored in the HiR-fields and in CODHISTO.          ;
  % ----------------------------------------------------------------- ;
  if codhisto
   then for x:=0:histolen do sethisto(x,nil)
   else codhisto:=mkvect(hlen);
  headhisto:=0;
  for x:=0:rowmax do
   inshisto(x);
end;

symbolic procedure redcodmat;
% ------------------------------------------------------------------- ;
% Recursive removal of redundant information using the procedure      ;
% TestRed.                                                            ;
% ------------------------------------------------------------------- ;
for x:=rowmin:rowmax do testred(x);

symbolic procedure testred(x);
% ------------------------------------------------------------------- ;
% If the row or column X is still relevant but has an additive weight ;
% of 1 or 0 its information is irrelevant for the searches.           ;
% Remark : It is possible to consider the LOWER BOUND of 2 as a PARA- ;
% METER. If we are only interested in cse's of a LENGTH of AT LEAST M ;
% we have to replace the 2 by M and to MAKE this M GLOBAL. It demands ;
% a revision of the procedure DOWNWGHT1 and similar routines, given in;
% the CODMAT module, and a modification of the profit criterium WSI   ;
% (see the procedure EXTBRSEA1).                                      ;
% So when a row is redundant we declare it to be occupied and reduce  ;
% the weights of the column its shares its element with, before we    ;
% test if this column is now redundant as well. The role of rows and  ;
% columns are thus interchangeable.                                   ;
% ------------------------------------------------------------------- ;
if free(x) and awght(x)<2
then
 <<setoccup(x);
   foreach z in zstrt(x) do
    <<downwght1(yind z,ival z);
      testred(yind z)>>
 >>;

symbolic procedure extbrsea1;
% ------------------------------------------------------------------- ;
% This procedure defines the kernel of the generalized Breuer-search. ;
% It is based on the basic scan for zero-determinants. An explanation ;
% is given, using a (6,4)-matrix B of integers, which can also be     ;
% found in Van Hulzen '83, p.295 :                                    ;
%                                                                     ;
% column -4 -3 -2 -1                                                  ;
%                                                                     ;
% row 6 | 0  0  1  1 | AWght = 2 MWght = 0 HWght = 2 CodHisto( 2) = 6 ;
%     5 | 0  1  2  2 |         3         2         9         ( 9)   5 ;
%     4 | 0  2  2  3 |         3         3        12         (12)   4 ;
%     3 | 2  3  4  5 |         4         4        16         (16)   3 ;
%     2 | 4  6  0  0 |         2         2         8         ( 8)   2 ;
%     1 | 1  6  8 10 |         4         3        13         (13)   1 ;
%                                                                     ;
% AWght = 3  5  5  5                                                  ;
%                                                                     ;
% Hence Zstrt(-4) = ((3.2) (2.4) (1.1))                               ;
%   and Zstrt( 6) = ((-2.1)(-1.1)).                                   ;
% ------------------------------------------------------------------- ;
begin scalar hr,hc,x;
  while hr:=findhr() do
  % ----------------------------------------------------------------- ;
  % ExtBrsea1 consists of a WHILE-loop,which is executed as long as   ;
  % a first parent-row can be found using CODHISTO, via FindHR. So    ;
  % initially Psi = (HR).                                             ;
  % ----------------------------------------------------------------- ;
   if hc:=findhc(hr)
   % ---------------------------------------------------------------- ;
   % As long as a row HC can be found, which can be used in combinati-;
   % on with HR, the cse-search continues. Since redundancy is removed;
   % the AWght of HC is at least 2. Via FINDHC the column with maximal;
   % AWght, which shares a non-zero element with Row(HR) is selected. ;
   % ---------------------------------------------------------------- ;
    then
     <<wsi:=0;
       while not null(x:=findoptrow(hr,hc,(wsi/npsi)+1)) do
        brupdate(x);
       % ------------------------------------------------------------ ;
       % The Breuer-search continues as long as profit is gained. The ;
       % minimal rowlength for continuation is Floor(Wsi/NPsi) + 2.   ;
       % The number of rows is iteratively extended :                 ;
       % NPs(i+1) = NPs(i) + 1 or NPsi = NPs(i+1) - 1.                ;
       % Since Ws(i+1) > Ws(i) or NPsi * (NJs(i+1) - 1) > Ws(i), the  ;
       % number of columns, which are required for a further cse-exten;
       % sion is at least NJs(i+1),i.e. is larger than Floor(Wsi/NPsi);
       % + 1.                                                         ;
       % ------------------------------------------------------------ ;
       foreach x in roccup1 do
        setfree(x);
       % ------------------------------------------------------------ ;
       % Not usable during construction of the present cse. Given free;
       % again for a next attempt, with of course another HR.         ;
       % ------------------------------------------------------------ ;
       foreach x in roccup2 do
        setfree(x);
       % ------------------------------------------------------------ ;
       % Used for cse-construction, but now possibly reusable.        ;
       % ------------------------------------------------------------ ;
       roccup1:=roccup2:=nil;
       if wsi>0
        then
         <<foreach x in rcoccup do
            setfree(x);
           rcoccup:=nil;
           % -------------------------------------------------------- ;
           % Rows and Columns used for building the cse can eventually;
           % be usable again. Hence also given free again.            ;
           % Finally all necessary resettings in CODMAT and CODHISTO  ;
           % are performed with AddCse, before the search for further ;
           % cse's is continued.                                      ;
           % -------------------------------------------------------- ;
           addcse()>>
         else
          if npsi=1
           then
            << % ---------------------------------------------------- ;
               % If Wsi = 0 and NPsi = 1 the (HR,HC)-selection was un-;
               % lucky.No cse is found, i.e. HC has to be disregarded.;
               % ---------------------------------------------------- ;
               setoccup(hc);
               rcoccup:=hc.rcoccup
            >>
      >>
     else
      << % ---------------------------------------------------------- ;
         % No columns available for cse-construction using the row HR.;
         % Hence HR is an unlucky choise. The elements of RCoccup are ;
         % freed to be reused. HR is disregarded via RowDel(HR), with ;
         % as a consequence a possible, intermediate introduction of  ;
         % redundancy, which can be removed by applying TestredZZ.    ;
         % ---------------------------------------------------------- ;
         foreach x in rcoccup do
          setfree(x);
         rcoccup:=nil;
         rowdel(hr);
         testredzz(hr)
      >>
end;
symbolic procedure findhr;
% ------------------------------------------------------------------- ;
% CODHISTO is subjected to a top-down search to find the non-zero en- ;
% try with maximal index, i.e. to find the index of the most interes- ;
% ting row. This is row 3 in the example in the comment in ExtBrsea1. ;
% This value is returned. In addition Psi, NPsi and RCoccur are initia;
% lized (Psi = (3), NPsi = 1 and RCoccur = (3),for example). Finally  ;
% row X (= 3), selected as most attractive row, is removed from the   ;
% candidate rows, by assigning NIL to the FREE-field.                 ;
% Note that X = Nil is possible, implying that the search, defined in ;
% ExtBrsea1,is finished during this stage of the optimization process.;
% ------------------------------------------------------------------- ;
begin scalar x;
  while headhisto>0 and null(x:=histo headhisto) do
  headhisto:=headhisto-1;
  if x
  then
  <<psi:=list x;
    npsi:=1;
    setoccup(x);
    rcoccup:=x.rcoccup>>;
  return x
end;

symbolic procedure findhc(hr);
% ------------------------------------------------------------------- ;
% HR is the index of a row, for instance selected with FindHR.        ;
% The Zstrt of HR is used to select the column, which can best be used;
% in combination with the row HR to start constructing a cse, i.e. the;
% "leftmost" column with locally maximal AWght. When looking at the   ;
% example in ExtBrsea1 this will be column -3.                        ;
% In addition Jsi and NJsi are initialized. Only the columns, which   ;
% are FREE are used( Jsi = (-1 -2 -3 -4), NJsi = 4).The return value  ;
% is Y = -3.                                                          ;
% NOTE :ExtBrsea1 is applied as long as it is possible.This might lead;
% to the need of disregarding columns during some stage in the itera- ;
% tive process. Therefore the test FREE(Y1:=Yind Z) is required.      ;
% ------------------------------------------------------------------- ;
begin scalar y,y1,aw,awmax;
  awmax:=njsi:=0;
  jsi:=nil;
  foreach z in zstrt(hr) do
  if free(y1:=yind z)
  then
  <<jsi:=y1.jsi;
    njsi:=njsi+1;
    if (aw:=awght y1)>awmax
    then
    <<awmax:=aw;
      y:=y1>>
  >>;
  jsi:=reverse(jsi);
  return y
end;

symbolic procedure findoptrow(hr,hc,lmax);
% ------------------------------------------------------------------- ;
% The row-index HR and the column-index HC are used to  find a Row(X),;
% applying the test defined in the procedure TestPr, such that Row(HR);
% and Row(X) have a cse of at least a length Lmax + 1.                ;
% If HR =3 and HC = -3  FindOptRow will produce X = 1.                ;
% In TestPr a zero-minor-test is performed, always using B(HR,HC), and;
% here for shortness called Bil. Bil is used in all the TestPr-tests. ;
% These tests are done for all rows, which share a non-zero element   ;
% with the column HC, and which are not yet disregarded for further   ;
% searches.The new version of Jsi is assigned to the local variable S,;
% i.e. the return-value of TestPr. If S is a list of one element, HC, ;
% its Cdr is Nil, i.e Row(X1) does not contribute to a possible cse,  ;
% contained in a pp, defined by Row(HR). Then X1 is added to the list ;
% Roccup1. If the profit is satisfactory, i.e. if the list S is longer;
% than Lmax a new set of column-indices, called NewJsi, is created and;
% the index X1 is also renamed and returned. Hence when no X1 is found;
% X is not initialized, implying that Nil is returned.                ;
% Regardless of X1's role, it is added to the list Roccup2 if S con-  ;
% tains at least 2 elements. Before returning to the calling procedure;
% ExtBrsea1, the FREE-field of Row(X1) is set to Nil, implying that it;
% is disregarded until further notice.                                ;
% TestPr produces S = (-1 -2 -3).                                     ;
% ------------------------------------------------------------------- ;
begin scalar l,s,x,x1,bil;
  bil:=ival(car pnthxzz(hc,zstrt hr));
  foreach z in zstrt(hc) do
   if free(x1:=xind z)
    then
     <<if null(cdr(s:=testpr(x1,hr,ival z,bil)))
        then roccup1:=x1.roccup1
        else
         <<if (l:=length s)>lmax
            then
             <<newnjsi:=lmax:=l;
               x:=x1;
               newjsi:=s
             >>;
           roccup2:=x1.roccup2
         >>;
        setoccup(x1)
     >>;
  return x
end;

symbolic procedure testpr(x,hr,bkl,bil);
% ------------------------------------------------------------------- ;
% TestPr is a procedure to perform zero-minor tests.                  ;
% X and HR are row-indices. Bkl = B(X,HC) and Bil = B(HR,HC).         ;
% The test is : Is Bil*Bkj - Bij*Bkl = 0?                             ;
% Assumptions : Bkj = B(X,j) and Bij = B(HR,j), where j is running    ;
% through Jsi, the set of indices of columns, which share a non-zero  ;
% element with Row(HR).HC is an element of Jsi.                       ;
% The new JSI-set is returned. It contains at least HC.               ;
% ------------------------------------------------------------------- ;
begin scalar zz,zzhr,x1,y,p,ljsi,cljsi;
  ljsi:=jsi;
  zz:=zstrt(x);
  zzhr:=zstrt(hr);
  while ljsi and zz do
  if (cljsi:=car ljsi)=(x1:=xind car zz)
  then
  << % -------------------------------------------------------------- ;
     % The list LJsi is initially equal to the already existing Jsi,a ;
     % list consisting of column-indices. The lists ZZ and ZZHR are,  ;
     % initially the Zstrt's of Row(X) and Row(HR), respectively. The ;
     % Zstrt's consist of pairs (column-index . coefficient/exponent).;
     % The WHILE-loop is performed as long as the lists LJsi and ZZ   ;
     % are not yet empty. The test defining alternative actions is ba-;
     % sed on a comparison of the car-elements of the remaining parts ;
     % of these lists, which are given in ascending index-order.      ;
     % -------------------------------------------------------------- ;
     zzhr:=pnthxzz(cljsi,zzhr);
     % -------------------------------------------------------------- ;
     % The Zstrt ZZHR is also in ascending order. If the Car of LJsi, ;
     % CLJsi, is equal to X1, the column-index of the Car of Zstrt(X),;
     % the elements of Zstrt(HR), preceding the element, containing   ;
     % CLJSI as column-index,are removed from ZZHR.                   ;
     % This can imply that ZZHR =(),i.e. that Car(ZZHR) = Nil and that;
     % IVal(Car(ZZHR)) = 0.                                           ;
     % -------------------------------------------------------------- ;
    if zeropp(dm!-difference(dm!-times(ival(car zz),bil),
                              dm!-times(ival(car zzhr),bkl)))
     then p:=cljsi.p;
comment
  if zeropp(dm!-difference(dm!-quotient(bil,bkl),
                            dm!-quotient(ival(car zzhr),ival(car zz))))
      then p:=cljsi.p;
     % -------------------------------------------------------------- ;
     % CLJsi can be added to the new Jsi-list, which is under construc;
     % tion, using P, if the test succeeds.Here Ival(Car ZZ) = Bkj and;
     % IVal(Car ZZHR) = Bij.                                          ;
     % -------------------------------------------------------------- ;
     ljsi:=cdr(ljsi);
     zz:=cdr(zz)
  >>
  else
    if cljsi>x1
    % --------------------------------------------------------------- ;
    % The lists are in ascending order. Hence if the Car's do not     ;
    % match one of the two has to be skipped.                         ;
    % --------------------------------------------------------------- ;
     then zz:=cdr(zz)
     else ljsi:=cdr(ljsi);
  return p
end;

symbolic procedure brupdate(x);
% ------------------------------------------------------------------- ;
% Assume Row(X) was found with procedure FindOptRow. It is the most   ;
% recently found cse-parent. Therefore the administration needs some  ;
% updating : The set Psi of parents must be extended with X, the set  ;
% Jsi of column-indices ought to be replaced by NewJsi and (de)activa-;
% tion of relevant rows(columns) ought to take place.                 ;
% ------------------------------------------------------------------- ;
<<psi:=x.psi;
  npsi:=npsi+1;
  jsi:=reverse(newjsi);
  njsi:=newnjsi;
  wsi:=(njsi-1)*(npsi-1);
  % ----------------------------------------------------------------- ;
  % Roccup2 is the set of indices of rows, which can possibly contri- ;
  % bute to a cse. During the previous FindOptRow-step Row(X) received;
  % apparently a higher priority. Row(X) is not longer a candidate pa-;
  % rent for the cse, presently being built.                          ;
  % ----------------------------------------------------------------- ;
  foreach x in roccup2 do
  setfree(x);
  roccup2:=nil;
  setoccup(x);
  rcoccup:=x.rcoccup
>>;

symbolic procedure addcse;
% ------------------------------------------------------------------- ;
% The cse defined by the index-sets Psi and Jsi is added to CODMAT.   ;
% So its occurrences in the rows,which have an index in Psi, are remo-;
% ved, the description of the cse is added as a new row to CODMAT and ;
% the system-selected cse-name is used to head a new column,defining  ;
% occurrences in the parent-rows. In combination with these measures  ;
% some weights have to be reset and thus also some information in     ;
% CODHISTO. The cse-ordering has - finally - to be taken care of via  ;
% the procedure SETPREV (see the CODMAT module for comment).          ;
% ------------------------------------------------------------------- ;
begin scalar zz,zzr,zzc,lzzr,lzzc,opv,var,gc,flt,min;
  zzr:=lzzr:=rzstrtcse() ;
  lzzc:=czstrtcse(ival car zzr);
  gc:=dm!-abs(ival car lzzc);
  min:=gc;
  flt:=floatprop(gc);
  foreach zz in lzzc do    % We have to test all the zz elements
  <<                       % because one could be a float
     flt:=flt or floatprop(ival zz);
     min:=dm!-min(min,dm!-abs(ival zz));
     if not(flt) then gc:=gcd2(gc,abs(ival zz))
  >>;
  if flt then gc:=min;     % When a float was encountered we take the
                           % smallest IVal, otherwise the gcd.
  if not(!:onep gc) then   % Correct when flt.
           << zz:=nil;     % When not(flt) gc<1 is not possible
              foreach z in zzr do
                 zz:=mkzel(xind z,dm!-times(ival(z),gc)).zz;
              zzr:=lzzr:=reverse zz;
              zz:=nil;
              foreach z in lzzc do
                 zz:=mkzel(xind z,dm!-quotient(ival(z),gc)).zz;
              lzzc:=reverse zz
            >>;
   zz:=nil;

  % ----------------------------------------------------------------- ;
  % ZZr and LZZr are assigned a row-Zstrt, in ascending order, defi-  ;
  % ning the cse, which must be added to CODMAT, in row Rowmax.       ;
  % LZZc is the column-Zstrt of the cse in ascending, thus "wrong" or-;
  % der. But LZZc is reversed, when updating the parent-rows in the   ;
  % Psi-loop. Similarly LZZr is used in the Jsi-loop for updating co- ;
  % lumns.                                                            ;
  % ----------------------------------------------------------------- ;
  var:=fnewsym();
  rowmax:=rowmax+1;
  setrow(rowmax,opv:=opval car jsi,var,list nil,zzr);
  % ----------------------------------------------------------------- ;
  % List Nil, parameter 4, defines the empty list of children and ex- ;
  % presses that also the EXPCOF-field of row(Rowmax) remains unused. ;
  % ----------------------------------------------------------------- ;
  rowmin:=rowmin-1;
  setrow(rowmin,opv,var,nil,nil);
  % ----------------------------------------------------------------- ;
  % The column(Rowmin) is reserved for the cse-description reverse(   ;
  % LZZc). Only the name Var is stored in the FarVar-field, like the  ;
  % operator-value in the OPVAL-field.                                ;
  % ----------------------------------------------------------------- ;
  if opv eq 'plus
   then put(var,'varlst!+,rowmin)
   else put(var,'varlst!*,rowmin);
  put(var,'rowindex,rowmax);
  % ----------------------------------------------------------------- ;
  % The new cse-name is stored either in the list of add.variables or ;
  % in the list of multiplicative variables. Its row-index is stored  ;
  % to allow retrieval of relevant information later on.              ;
  % ----------------------------------------------------------------- ;
  foreach x in psi do
  <<zz:=remzzzz(zzr,zstrt x);
    zzc:=car(lzzc).zzc;
    setzstrt(x,mkzel(rowmin,val car lzzc).zz);
    delhisto(x);
    initwght(x);
    inshisto(x);
    setprev(x,rowmax);
    lzzc:=cdr(lzzc)
    % --------------------------------------------------------------- ;
    % The cse Zstrt-description is removed from all the parent-Zstrt's;
    % before the thus shortened Zstrt's are extended with the required;
    % information about occurence and multiplicity of the new cse,re- ;
    % presented by column(Rowmin). Since column-indices are negative  ;
    % and row-Zstrt's are in ascending order a dotted pair constructi-;
    % on the SetZstrt-application is used. The Psi-loop allows to step;
    % wise reverse the column-Zstrt LZZc to produce the required form ;
    % ZZc, a Zstrt in descending order.                               ;
    % Once a row is modified it is removed from the CODHISTO-hierarchy;
    % and its HWght is recomputed before it is reinserted via CODHISTO;
    % Finally the ORDR-fields in  the parents are reset, by adding the;
    % location of the new cse to the already stored information about ;
    % the output ordering.(see for SetPrev the module CODMAT).        ;
    % --------------------------------------------------------------- ;
  >>;
  foreach y in jsi do
  <<setzstrt(y,mkzel(rowmax,val car lzzr).remzzzz(zzc,zstrt y));
    lzzr:=cdr lzzr;
    initwght(y)>>;
  setzstrt(rowmin,zzc);
  % ----------------------------------------------------------------- ;
  % The column-Zstrt ZZc is removed from all the Jsi columns it is oc-;
  % curring in and ZZc itself is stored in column(Rowmin), already re-;
  % served for this purpose. All relevant column-HWghts are recomputed;
  % like done for row(Rowmax) :                                       ;
  % ----------------------------------------------------------------- ;
  initwght(rowmax);
  inshisto(rowmax);
  initwght(rowmin);
  % ----------------------------------------------------------------- ;
  % Finally we test the modified columns and rows for redundancy.     ;
  % ----------------------------------------------------------------- ;
  foreach x in jsi do
   testredh(x);
  foreach x in psi do
   testredh(x)
end;

symbolic procedure rzstrtcse;
% ------------------------------------------------------------------- ;
% The Zstrt defining the cse,associated with Psi and Jsi, is made.    ;
% Psi is a list of row-indices, defining the parents.                 ;
% Jsi is a list of column -indices, defining the variables, occurring ;
% in the cse.                                                         ;
% Jsi is in ascending order. Psi is - in fact - not ordered.          ;
% This is due to the construction process.                            ;
% The cse-Zstrt is made out of the Zstrt of Row(Car Psi). The IVal's  ;
% in this Zstrt (coefficients or exponents) can be either integers or ;
% floats. When all of these IVals are integer (e.g. when dealing with ;
% exponents) the parents contain an integer-multiple (or integral     ;
% power) of the cse. In this case, when constructing the cse-Zstrt    ;
% such that the IVal's are relative prime all further required        ;
% resettings lead to integer IVal's in CODMAT.                        ;
% When one of the IVal's is a float, the smalest one is divided out.  ;
% Generally, this leads to float IVal's in CODMAT.                    ;
% ------------------------------------------------------------------- ;
begin scalar ljsi,zz,zzcse,gc,flt,min;
  zz:=pnthxzz(car jsi,zstrt car psi);
  zzcse:=list(car zz);
  gc:=dm!-abs(ival(car zz));
  min:=gc;
  flt:=floatprop(gc);
  % ----------------------------------------------------------------- ;
  % All initializations for the WHILE-loop are made :                 ;
  % ZZ is that part of the Zstrt(Car Psi) that starts with the element;
  % containing the leftmost element of Jsi in its index-field.        ;
  % So its first element is also the first element of the cse-Zstrt.  ;
  % The IVal-value of this head-element is assumed to contain the gcd ;
  % of all the IVal's of the cse. During the WHILE-loop other elements;
  % of Jsi,collected in LJsi are consumed,thus producing the cse-Zstrt;
  % ----------------------------------------------------------------- ;
  foreach ljsi in cdr(jsi) do
  <<zz:=pnthxzz(ljsi,zz);
    flt:=flt or floatprop(ival car zz);
    min:=dm!-min(min,dm!-abs(ival car zz));
    if not(flt) then gc:=gcd2(gc,abs(ival car zz));
    zzcse:=car(zz).zzcse
  >>;
  if flt then gc:=min; % When a float has been encountered, the ;
  return               % minimum of the ival's is divided out   ;
    if !:onep(gc) or expshrtest()
     then reverse(zzcse)
     % -------------------------------------------------------------- ;
     % If GC = 1 the IVal's are relative prime or/so there is no need ;
     % to divide out an IVal. The ZZcse ought to be                   ;
     % reversed, because the cons-construction reverses the original  ;
     % information.                                                   ;
     % The alternative expresses that the GC(d) of the exponents, de- ;
     % fining a monomial-cse, obtained after temporarily expanding the;
     % TIMES-columns, has not to be divided out, since it is in con-  ;
     % flict with the information storage and retrieval of the tempo- ;
     % rarily used TIMES-columns, as realized by using the NPCD- and  ;
     % PCDvar indicators in ExpandProd and ShrinkProd.                ;
     % -------------------------------------------------------------- ;
     else
     <<zz:=nil;
       foreach z in zzcse do
         zz:=mkzel(xind z,dm!-quotient(ival(z),gc)).zz;
         % ---------------------------------------------------------- ;
         % Due to the cons-construction, reversion is now superfluous.;
         % The GC is divided out to get relative prime IVal's.        ;
         % ---------------------------------------------------------- ;
       zz
     >>
end;

symbolic procedure gcd2(a1,a2);
% ------------------------------------------------------------------- ;
% The Gcd of A1 and A2 is computed. The value returned is positive, if;
% A1 and A2 are positive.                                             ;
% ------------------------------------------------------------------- ;
begin scalar a3;
  a3:=remainder(a1,a2);
  return
    if a3=0
      then a2
      else gcd2(a2,a3)
end;

symbolic procedure expshrtest;
% ------------------------------------------------------------------- ;
% ExpShrTest returns T is Jsi contains atleast one index of a column, ;
% which is temporarily used to store (part of) the expanded represen- ;
% tation of a column, defining a TIMES-variable. Such a column has a  ;
% -2 Farvar-value. Details : Expandprod and ShrinkProd.               ;
% ------------------------------------------------------------------- ;
begin scalar ljsi,further;
 if not (opval(car jsi) eq 'plus)
  then << ljsi:=jsi;
          while (ljsi and not further) do
           << further:=(farvar(car ljsi)=-2);
              ljsi:=cdr ljsi>>
       >>;
 return(further)
end;

symbolic procedure czstrtcse(iv);
% ------------------------------------------------------------------- ;
% The row-Zstrt of the actual cse is made by applying RZstrtCse. The  ;
% parameter IV is the IVal of the head-element of this Zstrt. It will ;
% be used to compute the multiplicity of the cse in the different pa- ;
% rents. These multiplicities are stored as IVal's in the column-Zstrt;
% associated with the new life of the cse as new variable.            ;
% ------------------------------------------------------------------- ;
begin scalar lpsi,zz,zzcse;
  zz:=zstrt(car jsi);
  lpsi:=ordn(psi); % Standard Reduce function ;
  psi:=nil;
  % ----------------------------------------------------------------- ;
  % The set LPsi defines Psi in descending order, i.e. the ordering   ;
  % needed for the construction of the column-Zstrt. ZZ is the Zstrt  ;
  % of the column,which contains the parameter IV as one of its IVal's;
  % ZZ is used to produce the Psi elements, which form the cse-Zstrt, ;
  % called ZZcse.ZZ is in descending order. During the WHILE-loop exe-;
  % cution Psi is reconstructed in ascending order.                   ;
  % ----------------------------------------------------------------- ;
  while lpsi do
   <<zz:=pnthxzz(car lpsi,zz);
     zzcse:=mkzel(car lpsi,dm!-quotient(ival(car zz),iv)).zzcse;
     psi:=car(lpsi).psi;
     lpsi:=cdr(lpsi)
     % -------------------------------------------------------------- ;
     % ZZ is used to built ZZcse. Using Car(LPsi) the non-relevant e- ;
     % lements of ZZ are removed, allowing to access the next column- ;
     % element, which can be used to produce the cse-column. The mul- ;
     % tiplicity has to be stored as IVal of the actual Z-element, and;
     % is found by dividing the IVal of the present Car of ZZ by IV.  ;
     % The IVal's of the row-Zstrt of the cse are relative prime, im- ;
     % plying that the IVal's of the relevant elements of ZZ are all  ;
     % integral multiples of IV.                                      ;
     % ZZcse is made in ascending order.                              ;
     % -------------------------------------------------------------- ;
   >>;
  return zzcse
end;

symbolic procedure testredzz(x);
% ------------------------------------------------------------------- ;
% TestredZZ is mutually recursive with TestredH and use in combination;
% with this routine to remove redundancy from CODMAT. Always of course;
% on a temporary basis.                                               ;
% ------------------------------------------------------------------- ;
foreach z in zstrt(x) do testredh(yind z);

symbolic procedure testredh(x);
% ------------------------------------------------------------------- ;
% Row (column) X is disregarded during further searches and its infor-;
% mation is deleted from CODHISTO, if the length of Zstrt(X) is redu- ;
% ced to 1. This redundancy test has to be done recursively.          ;
% ------------------------------------------------------------------- ;
if free(x) and awght(x)<2
 then
  <<rowdel(x);
    testredzz(x)>>;

symbolic procedure expandprod;
% ------------------------------------------------------------------- ;
% Only linear-expression like monomial cse's are found when applying  ;
% ExtBrsea1. The zero-minor condition is too strong. Monomial cse be- ;
% haviour is additive. Therefore addition chain mechanisms are employ-;
% ed to extend the relevant TIMES-columns in a number of temporarily  ;
% used columns, of which all the non-zero elements have the same expo-;
% nent value. Then ExtBrsea1 can be applied again, after relevant re- ;
% settings in CODHISTO. Procedure Shrinkprod is applied to undo this  ;
% expansion after the additional searches.                            ;
% Expandprod's functioning is illustrated by an example :             ;
% Assume : Y = -15, Var (= FarVar Y) = X and                          ;
%          Zstrt(Y) = ((6.1)(5.5)(4.5)(3.3)(2.5)(1.2)).               ;
% Zstrt(Y) is transformed into a matrix, using algorithm 2.1, given in;
% van Hulzen '83, page 296-297. The overall functioning can be vizua- ;
% lized in the following way :                                        ;
%                                                                     ;
%     Before      Expandprod Application         After                ;
%                                                                     ;
%  column|-15|                       column|-15 -23 -24 -40 |         ;
%        +---+                             +----------------+         ;
%  row 1 | 2 |                       row 1 | 1   1          |         ;
%      2 | 5 |                           2 | 1   1   1   2  |         ;
%      3 | 3 |                           3 | 1   1   1      |         ;
%      4 | 5 |                           4 | 1   1   1   2  |         ;
%      5 | 5 |                           5 | 1   1   1   2  |         ;
%      6 | 1 |                           6 | 1              |         ;
%        -----                             ------------------         ;
%                                                                     ;
% ------------------------------------------------------------------- ;
begin scalar var,pcvary,pcdvar,zzr,ivalz,n,m,npcdvar,npcdv,col!*,
                                                               relcols;
  for y:=rowmin:(-1) do
   if opval(y) eq 'times and not numberp(farvar y) and testrel(y)
    then relcols:=y . relcols;
  foreach y in relcols do
  << var := farvar y;
     % -------------------------------------------------------------- ;
     % TIMES-columns are only elaborated, when their Farvar-field is  ;
     % not a number, i.e. is the name of a variable or a cse, and if  ;
     % their Zstrt consists of at least 2 elements, which are not all ;
     % equal 1.                                                       ;
     % The Zstrt of such a column contains IVal's being powers of Var,;
     % the name associated with the column.                           ;
     % -------------------------------------------------------------- ;
     pcvary:=pcdvar:=zzr:=nil;
     foreach zel in zstrt(y) do
      if not((ivalz:=ival zel)=1)
       then
        <<setival(zel,1);
          % --------------------------------------------------------- ;
          % Zstrt(Y) is modified. All exponents are reduced to 1, i.e.;
          % Zstrt(Y) := ((6.1)(5.1)(4.1)(3.1)(2.1)(1.1)).             ;
          % The remaining exponent parts are saved in PCvarY, using   ;
          % InsPCvv, as pairs of the form ((exponent-1).(list of indi-;
          % ces of associated rows). So                               ;
          % PCvarY := ((1.(1))(2.(3))(4.(2 4 5))).                    ;
          % --------------------------------------------------------- ;
          pcvary:=inspcvv(xind zel,ivalz-1,pcvary)
        >>;
     pcdvar:=inspcvv(y,1,pcdvar);
     % -------------------------------------------------------------- ;
     % PCDvar is a list of pairs consisting of an exponent EXPO and a ;
     % list of indices of columns, which were (temporarily) used to   ;
     % store occurrences of Var^EXPO. Initially holds :               ;
     % PCDvar := ((1.(-15))).                                         ;
     % -------------------------------------------------------------- ;
     n:=0;
     npcdv:=npcdvar:=get(var,'npcdvar);
     % -------------------------------------------------------------- ;
     % NPCDvar is a list of column-indices, which were used during a  ;
     % previous ExpandProd activity, to store temporarily the additio-;
     % nal columns, to be produced with PCvarY. NPCDvar was stored on ;
     % the property-list of Var, during a previous application of     ;
     % Expandprod, and using the actual value of NPCDv. Assume now,   ;
     % for the example, that NPCDvar = (-23 -24).                     ;
     % NPCDv is initially the previous version of NPCDvar, but eventu-;
     % ally extended, during an ExpandProd-application. This new value;
     % is stored on the property-list of Var before leaving ExpandProd;
     % Hence the columns, associated with NPCDvar are reused when ever;
     % necessary. Their Farvar-fields will always contain the value -2;
     % to avoid a wrong use.                                          ;
     % -------------------------------------------------------------- ;
     foreach pc in pcvary do
     % -------------------------------------------------------------- ;
     % Each item of the PCvarY list is now used to make a new column, ;
     % starting with the smallest exponent value.                     ;
     % -------------------------------------------------------------- ;
      <<if npcdvar
         then
          <<col!*:=car(npcdvar);
            npcdvar:=cdr(npcdvar);
            % ------------------------------------------------------- ;
            % The first 2 columns, which are selected are -23 and -24.;
            % ------------------------------------------------------- ;
          >>
         else
          <<col!*:=rowmin:=rowmin-1;
            npcdv:=col!*.npcdv;
            % ------------------------------------------------------- ;
            % All additional columns, which are needed are newly gene-;
            % rated. Assume their indices to be -40, -41, ...         ;
            % ------------------------------------------------------- ;
          >>;
        %------------------------------------------------------------ ;
        % Hence, whenever necessary a new column-index is made and ad-;
        % ded to the set (list) NPCDv.                                ;
        % ----------------------------------------------------------- ;
        zzr:=mkzel(col!*,car(pc)-n).zzr;
        % ----------------------------------------------------------- ;
        % ZZr is a Zstrt, used to produce relevant additional row in- ;
        % formation, needed on a temporary basis, when expanding mono-;
        % mial row descriptions. ZZr is growing during the execution  ;
        % of the current ForEach-loop in the following way :          ;
        % ZZr := ((-23 . 1)),                                         ;
        % ZZr := ((-24 . 1) (-23 . 1)),                               ;
        % ZZr := ((-40 . 2) (-24 . 1) (-23 . 1)).                     ;
        % ----------------------------------------------------------- ;
        setrow(col!*,'times,-2,nil,nil);
        % ----------------------------------------------------------- ;
        % FarVar := -2 setting of column COL!*.                       ;
        % ----------------------------------------------------------- ;
        foreach x in cdr(pc) do
         % ---------------------------------------------------------- ;
         % PC is a pair (reduced exponent . list of indices of rows,of;
         % which the Zstrt ought to be temporarily modified).         ;
         % ---------------------------------------------------------- ;
         foreach z in zzr do
          <<setzstrt(x,inszzzr(z,zstrt x));
            % ------------------------------------------------------- ;
            % Every element of ZZr is inserted in Zstrt(X), where X is;
            % running through the row-index list, defined by PC.      ;
            % ------------------------------------------------------- ;
            setzstrt(yind z,inszzz(mkzel(x,val z),zstrt yind z))
            % ------------------------------------------------------- ;
            % The Zstrts of the corresponding col.s are also modified.;
            % ------------------------------------------------------- ;
          >>;
        % ----------------------------------------------------------- ;
        % This double FOREACH-loop is executed inside the PC-FOREACH- ;
        % loop. For the example holds :                               ;
        % PC=(1.(1)) & ZZr=((-23 . 1)) gives insertion of (-23 . 1) in;
        % Zstrt(row(1)) and of (1 . 1) in Zstrt(col(-23)).            ;
        % PC=(2.(3)) & ZZr=((-24 .1 )(-23 . 1)) gives insertion of    ;
        % (-24 . 1) and (-23 . 1) in Zstrt(row(3)) and of (3 . 1) in  ;
        % Zstrt(col(-23)) and Zstrt(col(-24)).                        ;
        % Finally PC=(4.(2 4 5)) & ZZr=((-40 . 2)(-24 . 1)(-23 . 1))  ;
        % gives insertion of (-40 . 2),(-24 . 1) and (-23 . 1) in     ;
        % in Zstrt(row(2)), Zstrt(row(4)) and Zstrt(row(5)),of (2 . 2);
        % (4 . 2) and (5 . 2) in Zstrt(col(-40)), and of (2 . 1),(4 . ;
        % 1) and (5 . 1), finally, in both Zstrt(col(-23)) and Zstrt( ;
        % col(-24)).                                                  ;
        % See also the matrix shown above.                            ;
        % ----------------------------------------------------------- ;
        pcdvar:=inspcvv(col!*,car(pc)-n,pcdvar);
        % ----------------------------------------------------------- ;
        % The PCDvar-list is also iteratively built up. This list is  ;
        % needed in Shrinkprod. Its final form for the example is :   ;
        % ((1.(-15 -23 -24)) (2.(-40)))                               ;
        % ----------------------------------------------------------- ;
        n:=car(pc);
        % ----------------------------------------------------------- ;
        % N is used to compute the reduced exponents iteratively.     ;
        % ----------------------------------------------------------- ;
      >>;
     put(var,'pcdvar,pcdvar);
     put(var,'npcdvar,npcdv);
   >>
end;

symbolic procedure testrel colindex;
% ------------------------------------------------------------------- ;
% TestRel(evance) is used to determine if the TIMES-column with index ;
% Y possesses a Zstrt n which at least 2 elements obey the condition  ;
% that their IVal-value is at least 2. This test is either performed  ;
% in EXPANDPROD or in SHRINKPROD. In the latter case the test is need-;
% ed to be able to decide if a next application of EXPANDPROD is re-  ;
% quired. If so this is indicated by setting the flag EXPSHR. Hence   ;
% its existence is tested in the former case. When the flag proves to ;
% have been set it is removed to allow a possible next test. If it was;
% not yet set the TIMES-column with the index Y has not been used be- ;
% fore in an application of EXPANDPROD.                               ;
% ------------------------------------------------------------------- ;
begin scalar btst,mn,rcol,relcols,relrow,onerows,orows;
  if(btst:=flagp(list(farvar(colindex)),'expshr))
    then remflag(list(farvar(colindex)),'expshr)
    else
      << mn:=0;
         foreach z in zstrt(colindex) do
          if ival(z)>1 then << mn:=mn+1;
                               if mn=1 then relrow:=xind z
                            >>
                       else onerows:=xind(z).onerows;
         if not (btst:=(mn>1)) and mn=1 and
            onerows and length(zstrt(relrow))>1
            then
             << mn:=0;
                foreach z in zstrt(relrow) do
                 if (yind(z) neq colindex)
                  then << mn:=mn+1; relcols:=yind(z).relcols >>;
                if mn>0
                 then
                  while relcols and not(btst) do
                   << rcol:=car relcols; relcols:=cdr relcols;
                      orows:=onerows;
                      while orows and not(btst) do
                       << btst:=pnthxzz(car orows,zstrt rcol);
                          orows:=cdr orows
                       >>
                   >>
             >>
      >>;
  return(btst)
end;

symbolic procedure inspcvv(x,iv,s);
% ------------------------------------------------------------------- ;
% S is a list of pairs, given in ascending Car-ordering. The Cars are ;
% integers IV and the Cdrs are lists of objects X. Application of     ;
% InsPCvv leads to inclusion of the object X in the list associated   ;
% with IV. This Integer Value might be an exponent and the objects can;
% be row-indices, for instance.                                       ;
% ------------------------------------------------------------------- ;
if null(s)
 then list(iv.list(x))
 else
  if dm!-eq(iv,caar(s))
   then (iv.(x.cdar(s))).cdr(s)
   else
    if dm!-lt(iv,caar(s))
     then (iv.list(x)).s
     else car(s).inspcvv(x,iv,cdr s);


symbolic procedure shrinkprod;
% ------------------------------------------------------------------- ;
% After expansion of certain Times-columns additional Breuer-searches ;
% are performed. Shrinkprod is used to restore the remaining informa- ;
% tion in the standard form. So the distributed exponent portions are ;
% added together and stored in the original column. For the example,  ;
% introduced in Expandprod all remaining information is to be collect-;
% ed in column -15.                                                   ;
% Assume the Breuer-searches to have produced the following result :  ;
%                                                                     ;
% column|-15 -23 -24 -40|-60 -61 -62|     Row(7) and column(-60)      ;
%       +---------------+-----------+     define cse X5=X^2*X3.       ;
% row 1 |               |         1 |                                 ;
%     2 |               | 1         |     Row(8) and column(-61)      ;
%     3 |               |     1     |     define cse X3=X*X2.         ;
%     4 |               | 1         |                                 ;
%     5 |               | 1         |     Row(9) and column(-62)      ;
%     6 | 1             |           |     define cse X2=X*X.          ;
%       +---------------+-----------+                                 ;
%     7 |             2 |     1     |     The columns -15,-23 and -24 ;
%     8 |         1     |         1 |     define X-occurrences and    ;
%     9 | 1   1         |           |     the column -40 defines an   ;
%       -----------------------------     X^2-occurrence.             ;
%                                                                     ;
% ShrinkProd is used to recombine the information of column -15 and   ;
% those given in the PCDvar-list. The result is :                     ;
%                                                                     ;
% column|-15 -23 -24 -40|-60 -61 -62|                                 ;
%       +---------------+-----------+                                 ;
% row 1 |               |         1 |    The columns -23, -24 and -40 ;
%     2 |               | 1         |    remain unused until the next ;
%     3 |               |     1     |    application of ExpandProd.   ;
%     4 |               | 1         |    The indices remain stored in ;
%     5 |               | 1         |    the list NPCDvar (see the    ;
%     6 | 1             |           |    procedure ExpandProd).       ;
%       +---------------+-----------+    X^2 can again be found as a  ;
%     7 | 2             |     1     |    cse (see column -15). Hence  ;
%     8 | 1             |         1 |    ImproveLayout(see the module ;
%     9 | 2             |           |    CODAD1) is needed.           ;
%       -----------------------------                                 ;
%                                                                     ;
% ------------------------------------------------------------------- ;
begin scalar var,pcdvar,zz,zstreet,el,exp,collst,indx,further;
  for y:=rowmin:(-1) do
  if not numberp(var:=farvar y) and (pcdvar:=get(var,'pcdvar))
                                and opval(y) eq 'times
  then
  << % -------------------------------------------------------------- ;
     % Only Times-columns are elaborated, which are associated with   ;
     % those Var's of which the PCDvar-indicator has a nonNil value.  ;
     % The Opval test is needed because Var's are in general associa- ;
     % ted with both PLUS and TIMES-columns.                          ;
     % For the example holds : Var = X and PCDvar = ((1.(-15 -23 -24) ;
     % (2.(-40))).                                                    ;
     % -------------------------------------------------------------- ;
     zstreet:=zstrt(y);
     % -------------------------------------------------------------- ;
     % Initially holds : Zstrt(Y) = Zstreet = ((9.1)(6.1)).           ;
     % Application of ShrinkProd leads to : Zstreet = ((9.2)(8.1)(7.2);
     % (6.1)). This also affects the Zstrt's of the rows 7,8 and 9 and;
     % of the columns -23,-24 and -40.                                ;
     % -------------------------------------------------------------- ;
     foreach pcd in pcdvar do
      <<% ----------------------------------------------------------- ;
        % Pcd gets 2 different values for the example :               ;
        % (1.(-15=Y -23 -24)) and (2.(-40)).                          ;
        % ----------------------------------------------------------- ;
        exp:=car(pcd);
        collst:=delete(y,cdr pcd);
        % ----------------------------------------------------------- ;
        % The original Var!* column is left out during the now follow-;
        % ing reconstruction process, because it is Zstreet = Zstrt(Y);
        % which is restored.                                          ;
        % ----------------------------------------------------------- ;
        foreach col in collst do
        % ----------------------------------------------------------- ;
        % These Col's are all FarVar = -2 columns.                    ;
        % ----------------------------------------------------------- ;
         <<foreach z in zstrt(col) do
            <<% ----------------------------------------------------- ;
              % These Z's are pairs (row-index . exponent-value).     ;
              % ----------------------------------------------------- ;
              indx:=xind(z);
              if el:=assoc(indx,zstreet)
               then setival(el,ival(el)+exp)
                    % ----------------------------------------------- ;
                    % If the row-index Indx is already used in the des;
                    % cription of Zstreet (i.e. in the column -15 of  ;
                    % the example) only the value in the exponent-    ;
                    % field of the Z-element has to be reset. This is ;
                    % done with SetIval, implying that through a      ;
                    % Replaca command  Zstreet is also modified!      ;
                    % ----------------------------------------------- ;
               else
                <<% ------------------------------------------------- ;
                  % If the row-index Indx is not yet used in the des- ;
                  % cription of Zstreet a new element has to be added ;
                  % to both Zstreet and the Zstrt of the row Indx.    ;
                  % ------------------------------------------------- ;
                  zstreet:=inszzz(el:=mkzel(indx,exp),zstreet);
                  setzstrt(indx,inszzzr(mkzel(y,val el),zstrt indx))
                >>;
              setzstrt(indx,delyzz(col,zstrt indx))
              % ----------------------------------------------------- ;
              % Now the element Z is removed from the Zstrt of row    ;
              % Indx. The complete column Col is emptied and can thus ;
              % freely be reused during a next application of Expandp.;
              % To avoid any confusion ClearRow is used, implying that;
              % the FarVar-field of the column Col gets the value -1. ;
              % ----------------------------------------------------- ;
            >>;
           clearrow(col)
         >>
       >>;
      setzstrt(y,zstreet);
      remprop(var,'pcdvar);
      % ------------------------------------------------------------- ;
      % The final Zstreet-value is stored in column Y ( in the example;
      % column -15) and the PCDvar information is removed from the    ;
      % property list of Var.                                         ;
      % ------------------------------------------------------------- ;
      if testrel(y) then <<further:=t;flag(list(var),'expshr)>>
      % ------------------------------------------------------------- ;
      % After regrouping TIMES-column information it is tested if a   ;
      % next application of EXPANDPROD is needed. If so T is returned.;
      % This value is used in EXTBRSEA to decide if the EXPAND-SHRINK ;
      % repeat-loop has to be continued or not.                       ;
      % ------------------------------------------------------------- ;
    >>;
    return(further)
 end;

endmodule;

end;


