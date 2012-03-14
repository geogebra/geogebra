%
% fastgets.lsp                     Copyright (C) 1995-2004 Codemist Ltd
%

(prog (how!-many w n)
    (setq how!-many 32)

    (symbol!-make!-fastget how!-many)

% It is important that NONCOM is given the fastget tag zero, since that
% number is hard coded into the Lisp kernel in "cslread.c"
    (symbol!-make!-fastget 'noncom           0)
    (symbol!-make!-fastget 'lose             1)

% The following list gives property-list indicators in roughly the
% order of priority across the whole range of REDUCE test files provided
% with REDUCE 3.6.  For each test the number of successful and unsuccessful
% property-list accesses was counted. (S and F).  The score S+2*F was used
% to allow for failing accesses needing to scan all the list, while
% successful ones only go on average half way. For each test scores for each
% particular indicator were expressed as percentages. Tables of all these
% percentages were put together and sorted - the list that follows is what
% emerged when only the highest-placed mention of a tag was kept. The effect
% should be that the most heavily used tags in each test come in this list.
% This list can be longer than the number of fast-get tags I support and only
% the relevant number of items on it will be used.
%
% At present I am assuming that the priority order will be roughly the
% same for REDUCE 3.8!
%
    (setq w '(
            convert       rules         field            opmtch
            optional      noncom        rtype            dname
            indexvar      phystype      oldnam           opfn
            psopfn        avalue        share            zerop
            prepfn2       newnam        onep             intequivfn
            trace         polyfn        symmetric        binding
            nary          ifdegree      alt              switch!*
            remember      minusp        modefn           rtypefn 
            infix         tokprop       full             delchar
            class         delim         times            pprifn
            spaced        simpfn        number!-of!-args stat
            plus          i2d           prifn            idvalfn
            tag           package!-name fkernfn          !*decs!*
            difference    rvalue        tracing          struct
            prtch         kvalue        mksqsubfn        dfform
            noform        subscripted   switch           mgen
             ))            
    (setq n 2)
top (cons ((equal n how!-many) (return nil)))
    (symbol!-make!-fastget (car w) n)
    (setq w (cdr w))
    (setq n (add1 n))
    (go top))


% end of fastgets.lsp



