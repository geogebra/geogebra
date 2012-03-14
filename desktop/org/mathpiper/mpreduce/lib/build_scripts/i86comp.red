% "i86comp.red"                 Copyright 1991-2007,  Codemist Ltd
%
% Compiler that turns Lisp code into Intel 80x86 32-bit assembler in a way
% that fits in with the conventions used with CSL/CCL
%
% It is hoped that parts of this compoiler will form a framework upon
% which native compilers for other architectures can be built. Even with
% just the Intel one there are three different sets of register and calling
% conventions I would like to support (!), viz
%       Watcom C 11.0 register based calling
%       Microsoft Visual C++ 5.0 fast calling
%       Linux/GCC for Intel architectures
% This incoherence is amazing and horrid!
%
% The rules for these configurations appear to be as follows, but
% astonishing though it may seem I have found it amazingly difficult to
% find these rules documented. Certainly Microsoft explicitly indicate
% that the register-usage for their __fastcall linkage may vary between
% releases of their C compiler. Explanations of where to place arguments
% are tolerably well explained, but the statement of what registers may be
% corrupted and which must be preserved is buried somewhere...
%
%
%   register           (a)             (b)               (c)
%
%   EAX              result        arg1/result         result
%   EBX              preserved     arg3 or preserved   preserved
%   ECX              scratch       arg4 or preserved   arg1 or scratch
%   EDX              scratch       arg2 or preserved   arg2 or scratch
%   EBP              preserved     preserved           preserved
%   ESI              preserved     preserved           preserved
%   EDI              preserved     preserved           preserved
%   ESP              stack         stack               stack
%
% (a) Linux/GCC all functions, Watcom and MSVC __cdecl and va_args cases
% (b) Watcom "/r5" register-based calling
% (c) MSVC __fastcall
%
%
%                                                        M A Dmitriev
%                                                        A C Norman

global '(i_machine);

i_machine := cdr assoc('native, lispsystem!*);

% i_machine = 2           Watcom 11.0
%           = 3           MS VC++ 5.0
%           = 4           Linux
%           otherwise     something not supported here.

if not (i_machine=2 or i_machine=3 or i_machine=4) then
    error(0, "Unsupported architecture for this compiler");

%
% Assembler for use when generating native code within CSL/CCL. The
% overall structure of this code is intende to be fairly independent of
% the actual machine architecture supported, and there will be call-backs
% into particular code-generators when system sensitive operations have
% to be performed.
%

%
% This low-level assembler is activated using a procedural interface.
% To create some native code the correct sequence to use is:
%     i_startproc();           set things going
%     for each basic block do
%        i_putlabel lab;
%        for each instruction in the block do
%           i_putcomment '(disassembly of the instrn);
%           mixture of
%              i_putbyte 8-bits
%              i_put32 32-bits             Intel byte-order
%              i_extern <data>             32-bit ref to external symbol
%              i_putjump(data, lab)        variable length jump instruction
%     i_resolve();                         resolve labels
%
% There is a put32r to insert bytes in Sun rather than Intel byte order,
% and put16, put16r calls for 16-bit values.
%
% To go with this assembler there must be machine-specific procedures
% to decode the jump stuff:
%     i_jumpsize(pc, target, data)
%     i_jumpbytes(pc, target, data)
% where i_jumpsize MUST return a list whose length is the same as
% the value of i_jumpsize. The data handed down is whatever was passed to
% i_putjump, and it can be as complicated a structure as the architecture
% needs.
%
% put_extern takes an argument that one of the following, the meaning of
% which are explained later:
%      (absolute xxx)
%      (relative xxx)
%      (rel_plus_4 xxx)
%      (rel_minus_2 xxx)
%      (rel_minus_4 xxx)
%      (rel_offset xxx n)
%
% where xxx can be one of the following possibilities:
%     a negative integer -(n+1)     n is used to look up in a useful_functions
%                                   table (in file fns3.c of the CSL sources)
%     a positive integer n          address_of_variable (from fns3.c) will be
%                                   called with n as an argument
%     (n 0)                         entry n from zero_arg_functions (eval4.c)
%     (n 1)                         entry n from one_arg_functions
%     (n 2)                         entry n from two_arg_functions
%     (n 3)                         entry n from three_arg_functions
% and the code in restart.c will need to agree with the layout created
% here for relocation modes that link to these entities.
%
% All the addressing modes (at present) generate a 32 bit reference. The
% simplest one is ABSOLUTE which just puts the address of the target
% in the 32 bit location. The other modes all insert an adddress of the
% target relative to the current location. The complication is that some
% computers want this to be relative to the start of the 32-bit address,
% some relative to the start of the instruction containing that address and
% some use the start of the NEXT instruction as the base. I use plain
% RELATIVE for relocation from the start address of the value being
% stored. REL_PLUS_4 is relative to the word after this (ie +4). REL_MINUS_2
% and REL_MINUS_4 are expected to be useful if you need to be relative to the
% start of an instruction which has 2 or 4 bytes before the 32-bit offset.
% Finally REL_OFFSET is a catch-all that puts an extra signed byte in the
% relocation table to show the offset from the effect of just RELATIVE.
% In general I expect any particular computer to use just one of these,
% for instance Intel use REL_PLUS_4, but the others are there to make it
% easy to implement many different compiler back-ends. I have room in the
% encoding to add several more modes if and when necessary!
%
%
% Of course for any particular computer architecture I will have a
% higher level assembler that accepts input in a fairly symbolic form
% and converts it into the bit-patterns required here.
%
% A procedure is accumulated as a sequence of blocks. Each of these
% has an associated label, which will be a gensym if no user label was
% provided. Jump instructions only occur at the end of one of these
% blocks. When a block is complete it sits in the list of blocks in
% the form
%    (label location size b<n> b<n-1> ... b<0>)
% where size is the size in bytes represented by the sequence of bytes
% b<i>, except that the size of any final JUMP is not included. The
% items in the list may be
%       an integer                        just that byte
%       (JUMP shortform longform label)   short/long are lists of bytes
%       (EXTERN something)                4 bytes external reference
%       (COMMENT c1 c2 ...)               to display in listing
%

fluid '(i_procedure i_block i_blocksize i_label i_pc i_externs);

global '(!*genlisting);

!*genlisting := nil;

switch genlisting;  % For the benefit of RLISP/Reduce users


symbolic procedure i_startproc();
 << i_label := list nil;
    i_procedure := nil;
    i_externs := nil;
    i_block := nil;
    i_blocksize := 0;
    i_pc := 0;
    nil
 >>;

symbolic procedure i_putlabel l;
  begin
% car i_label can be nil at the start of a procedure or just after a jump
% has been issued. If a label is set in such a case and any instructions
% have been set in the dummy block then I invent a gensym-label for it,
% but if a real label gets set soon enough I can avoid introducing any
% sort of dummy mess.
    if car i_label = nil then <<
       if i_block = nil then <<
          rplaca(i_label, l);
          return >>
       else rplaca(i_label, gensym()) >>;
% 
    rplacd(i_label, i_pc . i_blocksize . i_block);
    i_procedure := i_label . i_procedure;
    put(car i_label, 'i_label, i_label);
% When I first create a procedure I suppose (optimistically) that all
% jumps can be rendered in short form.
    i_pc := i_pc + i_blocksize;
    if i_block and eqcar(car i_block, 'jump) then
       i_pc := i_pc + length cadar i_block + 1;
    i_label := list l;
    i_block := nil;
    i_blocksize := 0;
    nil
  end;

% The user MUST put a comment just before each instruction if
% disassembly is to behave properly. However if the assembly code
% is not going to be displayed I can avoid storing the extra rubbish.

symbolic procedure i_putcomment n;
 << if !*genlisting then i_block := ('comment . n) . i_block;
    nil
 >>;

symbolic procedure i_putbyte n;
 << i_block := n . i_block;
    i_blocksize := i_blocksize + 1;
    nil
 >>;

symbolic procedure i_put32 n;
 << i_putbyte logand(n, 0xff);
    n := logand(n, 0xffffffff) / 0x100;
    i_putbyte logand(n, 0xff);
    n := irightshift(n, 8);
    i_putbyte logand(n, 0xff);
    n := irightshift(n, 8);
    i_putbyte logand(n, 0xff);
    nil
 >>;

% Codegenerators will need to use whether i_put32 or i_put32r
% depending on the byte ordering used by the architecture that they support.

symbolic procedure i_put32r n;
 << n := logand(n, 0xffffffff);
    i_putbyte logand(n / 0x01000000, 0xff);
    i_putbyte logand(n / 0x00010000, 0xff);
    i_putbyte logand(n / 0x00000100, 0xff);
    i_putbyte logand(n, 0xff);
    nil
 >>;

%
% i_put16 and i_put16r dump 16 bit values.
%

symbolic procedure i_put16 n;
 << i_putbyte logand(n, 0xff);
    n := irightshift(ilogand(n, 0xffff), 8);
    i_putbyte logand(n, 0xff);
    nil
 >>;

symbolic procedure i_put16r n;
 << n := logand(n, 0xffff);
    i_putbyte irightshift(n, 8);
    i_putbyte logand(n, 0xff);
    nil
 >>;

% In order to be able to optimise short jumps I will arrange to start a
% fresh basic block after every jump instruction. I also store two
% possible byte sequences for use in the final code, one for when the
% target address is close by and the other for when it is further away.
% 

symbolic procedure i_putjump(data, lab);
 << i_block := list('jump, data, lab) . i_block;
    if car i_label = nil then rplaca(i_label, gensym());
    rplacd(i_label, i_pc . i_blocksize . i_block);
    i_procedure := i_label . i_procedure;
    put(car i_label, 'i_label, i_label);
% When a jump is first issued I will assemble it as a jump-to-self
% which I expect to use the shortest form of jump available. Later on
% and only if necessary I will expand it to a longer variant of the
% instruction.
    i_pc := i_pc + i_blocksize + i_jumpsize(i_pc, i_pc, data);
    i_label := list nil;                  % leave in pending state
    i_block := nil;
    i_blocksize := 0;
    flag(list lab, 'i_used);              % To get it displayed in listing
    nil
 >>;

% References to "external" symbols will be used to call functions in the
% Lisp kernel and to reference key variables there. At present I assume that
% all such references will require a 32-bit field. This will get filled in by
% load-time relocation code.

symbolic procedure i_putextern a;
 << i_block := list('extern, a) . i_block;
    i_externs := list(i_label, i_blocksize, a) . i_externs;
    i_blocksize := i_blocksize + 4;
    nil
 >>;

% prinhexb displays a hex number and then a blank, but only
% if !*genlisting is true.

symbolic procedure prinhexb(n, w);
  if !*genlisting then <<
     prinhex(n, w);
     princ " " >>;

% i_resolve() iterates over the code re-calculating the length of
% each basic block and hence deducing how long each jump instruction
% has to be. When it has done that it scans the code to make a map
% showing what external symbols will need relocating, and it builds
% the relevant tables. Finally it allocates space for the assembled
% code and puts the bytes where they need to be, optionally printing
% a nice neat version for the user to admire.

symbolic procedure i_resolve();
  begin
    scalar changed, pc, hardcode_handle, c, c1, c2, c3, gap, oll;
    oll := linelength 80;
    i_putlabel nil;      % Flushes last block into data structures
% The blocks had been collected in reverse order since that is how Lisp
% finds it easiest to build up lists.
    i_procedure := reversip i_procedure;
% Iterate until position of all blocks stabilises. In the very worst case
% this could take a number of passes proportional to the length of the
% code being assembled, but I do not expect that to happen often enough
% to worry about it.
    repeat <<
       changed := nil;
       pc := 0;
       for each b in i_procedure do begin
          scalar loc, len, j;
          loc := cadr b;         % estimated location
          len := caddr b;        % length of block (excluding jump)
          j := cdddr b;
          if j then j := car j;
          if eqcar(j, 'jump) then j := cdr j else j := nil;
          if loc neq pc then <<
             changed := t;       % will need to go around again.
             rplaca(cdr b, pc) >>;
          pc := pc + len;
% The next bit evaluates the size of a jump instruction.
          if j then begin
             scalar target, offset;
             target := cadr get(cadr j, 'i_label);
             pc := pc + i_jumpsize(pc, target, car j) end
          end
    >> until not changed;
% When I get to here pc shows the total size of the compiled code, and
% all labels have been resolved with jumps able to be in their shortest
% valid forms. The next thing to do is to sort out external references.
    i_pc := pc;

    i_externs := reversip i_externs;
    for each r in i_externs do rplaca(r, cadar r); 
    c := i_externs;
    pc := 0;
    i_externs := nil;
    while c do begin
       scalar data, address, offset, addressmode, target, op;
       c1 := car c;
       data := caddr c1;            % The "data" passed to i_putextern
       address := car c1 + cadr c1; % word to relocate
       offset := address - pc;      % distance from previous relocation
       pc := address;               % store loc to calculate next offset
       addressmode := car data;     % data = {addressmode,target}
       target := cadr data;
% The variable op will accumulate the first byte of the relocation information
% which packs an address mode and a target catagory into 169 possibilities
% as 13*13.
       op := 13*get(addressmode, 'i_addressmode);
% The target is coded in a slighly (!) ugly way here. I decode it and
% merge part of the information into the opcode byte, leaving the variable
% "target" holding an 8-bit specification of just what to address.
       if numberp target then <<
          if target < 0 then <<
              op := op + 4;       % RELOC_DIRECT_ENTRY
              target := -(target+1) >>
          else op := op + 5 >>    % RELOC_VAR
       else <<
          op := op + cadr target; % RELOC_0_ARGS to RELOC_3_ARGS
          target := car target >>;
% Now things are a bit messy. If the next relocation is close to the
% current one (which it almost always will be) I use a single byte offset
% to indicate where it is.
       if offset < 256 then       % can use one-byte offset
          i_externs := offset . (op+1) . i_externs
% If the next relocation is 256 or more bytes away I have to use an extended
% form of relocation record. This spreads the opcode across two bytes and
% that give space for 15 bits of genuine offset. If the gap was over
% 0x7fff then even this is not enough, and in that case I use multiple
% instances of the biggest offset I do support and do null relocations
% at the intermediate places.
       else <<
          while offset > 0x7fff do <<
% The sequence 0xff 0xff 0xff will be treated as NOP with offset 0x7fff
% and thus provides for arbitrary expansion of the range of offsets.
             i_externs := 0xff . 0xff . 0xff . i_externs;
             offset := offset - 0x7fff >>;
% NB (obviously?) the coding use here must agree with the corresponding
% stuff in source file "restart.c" that unpicks stuff.
          i_externs := logand(offset, 0xff) . (171 + op/2) . i_externs;
          i_externs := (128*remainder(op, 2) + (offset/256)) . i_externs >>;
       i_externs := target . i_externs;
% Here when I support RELOC_SELF_2 I will need to insert a target extension
% byte into the code-stream here.
%
% Add an extra byte if the relocation needed patching with a further offset,
% if we had address mode REL_OFFSET.
       if eqcar(gap, 'rel_offset) then
          i_externs := logand(caddr data, 0xff) . i_externs;
% I put a "comment" into the list so that I can display a nice
% or at least fairly symbolic indication of the relocation information
% when the user has !*genlisting switched on.
       i_externs := list(pc, data) . i_externs;
       c := cdr c end;          
    i_externs := '(termination) . 0 . i_externs;  % Terminate the list
% The first 4 bytes of some BPS give its length, and then the
% next 4 bytes give the offset of the start of the actual code in it.
% thuse there are 8 bytes of stuff to allow for.
    gap := 8;
    for each r in i_externs do if numberp r then gap := gap+1;
% I will ensure that the compiled code itself starts at a word boundary. I
% could make it start at a doubleword boundary easily enough if that made
% a real difference to performance.
    c := logand(gap, 3);
    if c neq 0 then <<
       while c neq 4 do <<
          i_externs := 0 . i_externs;
          c := c + 1;
          gap := gap + 1 >>;  % Word align
       i_externs := '(alignment) . i_externs >>;
    i_externs := reversip i_externs; % Back in the tidy order;
% Insert the data that gives the offset to the start of real compiled code
    i_externs := list('start, compress 
                        ('!! . '!0 . '!x . explodehex gap)) . i_externs;
    i_externs := logand(gap / 0x01000000, 0xff) . i_externs;
    i_externs := logand(gap / 0x00010000, 0xff) . i_externs;
    i_externs := logand(gap / 0x00000100, 0xff) . i_externs;
    i_externs := logand(gap, 0xff) . i_externs;
% Create space for the assembled code.
    i_pc := i_pc + gap;
    hardcode_handle := make!-native(i_pc);
    pc := 4;
    while i_externs do <<
       prinhexb(pc, 4);
       if !*genlisting then princ ": ";
       while i_externs and numberp car i_externs do <<
          prinhexb(car i_externs, 2);
          native!-putv(hardcode_handle, pc, car i_externs);
          pc := pc + 1;
          i_externs := cdr i_externs >>;
       if not atom i_externs then <<
          if !*genlisting then <<
             ttab 35;
             if numberp caar i_externs then <<
                princ "@";
                prinhex(gap+caar i_externs, 4);
                princ ": " >>
             else  <<
                princ caar i_externs;
                princ " " >>;
             if cdar i_externs then printc cadar i_externs
             else terpri() >>;
          i_externs := cdr i_externs >> >>;
    if !*genlisting then terpri();  % between relocation table & code
    pc := gap;
    for each b in i_procedure do <<
% I display labels unless they are never referenced.
       if !*genlisting and flagp(car b, 'i_used) then <<
          ttab 30; prin car b; printc ":" >>;
% The instructions within a basic block had been accumulated in a list
% that is reversed, so put it right here.
       c := reverse cdddr b;    % Code list
% I expect the first item in the list to be a comment, but if it is not
% I will annotate things with a "?" rather than crashing.
       if c and eqcar(car c, 'comment) then <<
          c1 := cdar c; c := cdr c >>
       else c1 := '(!?);
       while c do <<
          prinhexb(pc, 4); princ ": ";    % Address to put things at.
% Since I really wanted comments before each instruction I will scan
% forwrad until I either find the next comment or I hit the end of the list.
          while c and not eqcar(c2 := car c, 'comment) do <<
             if numberp c2 then <<
                prinhexb(c2, 2);
                native!-putv(hardcode_handle, pc, c2);
                pc := pc + 1 >>
             else if eqcar(c2, 'extern) then <<
                if !*genlisting then princ "xx xx xx xx ";
                native!-putv(hardcode_handle, pc, 0); pc := pc + 1;
                native!-putv(hardcode_handle, pc, 0); pc := pc + 1;
                native!-putv(hardcode_handle, pc, 0); pc := pc + 1;
                native!-putv(hardcode_handle, pc, 0); pc := pc + 1 >>
             else if eqcar(c2, 'jump) then <<
                for each j in i_jumpbytes(pc-gap,
                                          cadr get(caddr c2, 'i_label),
                                          cadr c2) do <<
                   prinhexb(j, 2);
                   native!-putv(hardcode_handle, pc, j); pc := pc + 1 >> >>;
             c := cdr c >>;
          if !*genlisting then <<     % Now display the comment
             ttab 34;
             for each w in c1 do <<
                if w = '!; then ttab 55 else princ " ";
                princ w >>;
             terpri() >>;
          if c and eqcar(c2, 'comment) then << 
             c1 := cdr c2; c := cdr c >> >> >>;
% At the end of dealing with a procedure I will clean up the property lists
% of all the symbols that were used as labels in it.
    for each b in i_procedure do <<
       remflag(list car b, 'i_used);
       remprop(car b, 'i_label) >>;
    linelength oll;
    return (hardcode_handle . gap)
  end;

put('absolute, 'i_addressmode, 0);   % Absolute address of target
put('relative, 'i_addressmode, 1);   % relative to start of reference
put('rel_plus_4, 'i_addressmode, 2); % relative to end of reference
put('rel_minus_2, 'i_addressmode, 3);% relative to 2 before item
put('rel_minus_4, 'i_addressmode, 4);% relative to 4 before item
put('rel_offset, 'i_addressmode, 5); % generic offset relative address




%============================================================================
% Now some Intel versions of jump support. This supposes that the "jump data"
% passed down to i_putjump was just the one-byte opcode for the short
% form of a relative jump.

symbolic procedure i_jumpsize(pc, target, data);
  begin
    scalar offset;
    offset := target - (pc + 2);  % Suppose short here
    if offset >= -128 and offset <= 127 then return 2  % short jump
    else if data = 0xeb then return 5                  % unconditional
    else return 6                                      % conditional
  end;

symbolic procedure i_jumpbytes(pc, target, data);
  begin
    scalar r, offset;
    offset := target - (pc + 2);  % Suppose short for the moment
    if offset >= -128 and offset <= 127 then
        return list(data, logand(offset, 0xff));
% An unconditional jump grows by 3 bytes while a conditional one
% needs an extra 4. And on this architecture the offset is taken from the
% end of the jump instruction, and so I need to adjust it a bit here.
    if data = 0xeb then <<          % 0xeb = short unconditional jump
       offset := offset - 3;
       r := list 0xe9 >>            % 0xe9 = long unconditional jump
    else <<
       offset := offset - 4;
       r := list(data+0x10, 0x0f) >>;  % +0x10 turns short to long jump
    offset := logand(offset, 0xffffffff);
    r := logand(offset, 0xff) . r;
    offset := offset / 0x100;
    r := ilogand(offset, 0xff) . r;
    offset := irightshift(offset, 8);
    r := ilogand(offset, 0xff) . r;
    offset := irightshift(offset, 8);
    r := ilogand(offset, 0xff) . r;
    return reversip r
  end;





%
% Next the code that transforms symbolically represented i80x86 instructions
% into native machine code.
%


% The main macro of the code generator. Generates opcodes for a sequence of
% i80x86 instructions represented in symbolic form. A macro is used just to
% make the calling form perhaps more natural. The sequence supplied to this
% macro looks as a list of parameters of arbitary length, not as a Lisp list
% (into which the macro transforms this sequence). Things that are names
% of Intel opcodes or registers do not need to be quoted... I detect them
% and insert a quote during macro expansion.

symbolic macro procedure i!:gopcode u;
   list('i!:genopcode, 'list .
       for each v in cdr u collect
          if atom v then
             (if get(v, 'i!:regcode) or get(v, 'i!:nargs) then mkquote v
              else v)
          else if eqcar(v, 'list) then for each v1 in v collect
             (if atom v1 and get(v1, 'i!:regcode) then mkquote v1
              else v1)
          else v);

% Now the procedure which actually gets called. It looks for items that
% are flagged as being opcodes, and for each such it knows how many
% operands to expect. It can then call lower level routines to collect and
% process those operands. Some amount of peephole optimisation is done on
% the way, which is probably not where I want it to be done, but it can
% remain here until I have re-worked the higher level compiler.

symbolic procedure i!:genopcode u;
  begin
    scalar c, nargs;
    while u do <<
      c := car u;
      nargs := get(c, 'i!:nargs);
      if nargs then <<   % It is an opcode...
         u := cdr u;
         if nargs = 2 then <<
            i!:2arginstr(c, car u, cadr u);
            u := cddr u >>
         else if nargs = 1 then <<
            i!:1arginstr(c, car u);
            u := cdr u >>
         else i!:noarginstr c >>
      else if c = '!: then <<  % label
         i!:proc_label cadr u;
         u := cddr u >>
      else u := cdr u >>  % Ignore anything that is not understood!
 end;


<<
   % Codes of the processor registers
   put('eax,  'i!:regcode, 0);
   put('ecx,  'i!:regcode, 1);
   put('edx,  'i!:regcode, 2);
   put('ebx,  'i!:regcode, 3);
   put('esp,  'i!:regcode, 4);
   put('ebp,  'i!:regcode, 5);
   put('esi,  'i!:regcode, 6);
   put('edi,  'i!:regcode, 7);
   % ds and ebp have the same code, but instructions which contain memory
   % references of the form {ds,...} have a special prefix. However, this
   % code generator will produce wrong output for "mov ds,const" instruction.
   % But I can't imagine what it can be needed for and I am not sure it is
   % legal in the user mode.
   put('ds,   'i!:regcode, 5);

% Irregular table of instructions opcodes. Values associated with the
% properties are either main or secondary opcodes for different formats
% of the instructions.

   put('add, 'i!:nargs, 2);        put('add, 'i!:rm!-reg, 0x01);
   put('add, 'i!:immed!-rm, 0x81); put('add, 'i!:immed!-rm!-secopcode, 0);
   put('add, 'i!:immed!-eax, 0x05);

   put('and, 'i!:nargs, 2);         put('and, 'i!:rm!-reg, 0x21);
   put('and, 'i!:immed!-rm, 0x81);  put('and, 'i!:immed!-rm!-secopcode, 4);
   put('and, 'i!:immed!-eax, 0x25);

   put('call, 'i!:nargs, 1);
   put('call, 'i!:reg, 0xff);       put('call, 'i!:reg!-secopcode, 0xd0);
   put('call, 'i!:jump, 0xe8);

   put('cmp, 'i!:nargs, 2);         put('cmp, 'i!:rm!-reg, 0x39);
   put('cmp, 'i!:immed!-rm, 0x81);  put('cmp, 'i!:immed!-rm!-secopcode, 7);
   put('cmp, 'i!:immed!-eax, 0x3d);

   put('dec, 'i!:nargs, 1);
   put('dec, 'i!:reg, 0x48);

   put('mul, 'i!:nargs, 2);
   put('mul, 'i!:rm!-reg!-prefix, 0x0f);

   put('mul, 'i!:rm!-reg, 0xaf);  put('mul, 'i!:rm!-reg!-dbit_preset, 1);
   put('mul, 'i!:immed!-rm, 0x69);

   put('inc, 'i!:nargs, 1);
   put('inc, 'i!:reg, 0x40);

   put('je,  'i!:nargs, 1);         put('je,  'i!:jump, 0x74);
   put('jne, 'i!:nargs, 1);         put('jne, 'i!:jump, 0x75);
   put('jg,  'i!:nargs, 1);         put('jg,  'i!:jump, 0x7f);
   put('jge, 'i!:nargs, 1);         put('jge, 'i!:jump, 0x7d);
   put('jl,  'i!:nargs, 1);         put('jl,  'i!:jump, 0x7c);
   put('jle, 'i!:nargs, 1);         put('jle, 'i!:jump, 0x7e);
   put('ja,  'i!:nargs, 1);         put('ja,  'i!:jump, 0x77);
   put('jae, 'i!:nargs, 1);         put('jae, 'i!:jump, 0x73);
   put('jb,  'i!:nargs, 1);         put('jb,  'i!:jump, 0x72);
   put('jbe, 'i!:nargs, 1);         put('jbe, 'i!:jump, 0x76);

   put('jmp, 'i!:nargs, 1);         put('jmp, 'i!:jump, 0xeb);

   put('mov, 'i!:nargs, 2);         put('mov, 'i!:rm!-reg, 0x89);
   put('mov, 'i!:immed!-rm, 0xc7);  put('mov, 'i!:immed!-rm!-secopcode, 0);
   flag('(mov), 'i!:immed!-rm!-noshortform);
   put('mov, 'i!:immed!-reg, 0xb8);

   put('neg, 'i!:nargs, 1);
   put('neg, 'i!:rm, 0xf5);         put('neg, 'i!:rm!-secopcode, 3);

   put('or, 'i!:nargs, 2);          put('or, 'i!:rm!-reg, 0x09);
   put('or, 'i!:immed!-rm, 0x81);   put('or, 'i!:immed!-rm!-secopcode, 1);
   put('or, 'i!:immed!-eax, 0x0d);

   put('pop, 'i!:nargs, 1);
   put('pop, 'i!:reg, 0x58);
   put('pop, 'i!:mem, 0x8f);        put('pop, 'i!:mem!-secopcode, 0x00);

   put('push, 'i!:nargs, 1);
   put('push, 'i!:reg, 0x50);
   put('push, 'i!:mem, 0xff);       put('push, 'i!:mem!-secopcode, 0x06);
   put('push, 'i!:immed8, 0x6a);    put('push, 'i!:immed32, 0x68);

   put('ret, 'i!:nargs, 0);         put('ret, 'i!:code, 0xc3);

   put('shl, 'i!:nargs, 2);
   put('shl, 'i!:immed!-rm, 0xc1);  put('shl, 'i!:immed!-rm!-secopcode, 4);
   flag('(shl), 'i!:immed!-rm!-shortformonly);

   put('shr, 'i!:nargs, 2);
   put('shr, 'i!:immed!-rm, 0xc1);  put('shr, 'i!:immed!-rm!-secopcode, 5);
   flag('(shr), 'i!:immed!-rm!-shortformonly);

   put('sub, 'i!:nargs, 2);         put('sub, 'i!:rm!-reg, 0x29);
   put('sub, 'i!:immed!-rm, 0x81);  put('sub, 'i!:immed!-rm!-secopcode, 5);
   put('sub,  'i!:immed!-eax, 0x2d);

   put('test, 'i!:nargs, 2);
   put('test, 'i!:rm!-reg, 0x85);   put('test, 'i!:rm!-reg!-dbit_preset, 0);
   put('test, 'i!:immed!-rm, 0xf7); put('test, 'i!:immed!-rm!-secopcode, 0);
   flag('(test), 'i!:immed!-rm!-noshortform);
   put('test, 'i!:immed!-eax, 0xa9);

   put('xor, 'i!:nargs, 2);         put('xor, 'i!:rm!-reg, 0x31);
   put('xor, 'i!:immed!-rm, 0x81);  put('xor, 'i!:immed!-rm!-secopcode, 6);
   put('xor, 'i!:immed!-eax, 0x35);

% These instructions necessarily change registers when they are executed.
% Hence we should keep track of them to get peephole optimisation right.

   flag('(add and dec mul inc neg or shl shr sub xor), 'i!:changes_reg)

>>;


fluid '(i!:reg_vec);

% Addresses of some internal CSL variables and functions.
% This table is needed by code compiled from Lisp which necessarily uses
% Lisp run-time library and internal variables

% Of course a worry here is that these addresses potentially change each
% time Lisp is re-loaded into memory, and so I need to be a little
% careful about their treatment.

global '(OFS_NIL OFS_STACK OFS_LISP_TRUE OFS_CURRENT_MODULUS OFS_STACKLIMIT);

<<
  OFS_NIL             := 0;   % Arg to give to native!-address
  OFS_STACK           := 1;
  OFS_LISP_TRUE       := 98;
  OFS_CURRENT_MODULUS := 29;
!#if common!-lisp!-mode
  OFS_STACKLIMIT      := 16;
!#else
  OFS_STACKLIMIT      := 15;
!#endif

% What follows will allow me to patch up direct calls to Lisp kernel
% functions. The (negative) integers are codes to pass to native!-address
% at the Lisp level and are then slightly adjusted to go in the relocation
% tables that are generated here.

  put('cons,           'c!:direct_call_func, -1);
  put('ncons,          'c!:direct_call_func, -2);
  put('list2,          'c!:direct_call_func, -3);
  put('list2!*,        'c!:direct_call_func, -4);
  put('acons,          'c!:direct_call_func, -5);
  put('list3,          'c!:direct_call_func, -6);
  put('plus2,          'c!:direct_call_func, -7);
  put('difference,     'c!:direct_call_func, -8);
  put('add1,           'c!:direct_call_func, -9);
  put('sub1,           'c!:direct_call_func, -10);
  put('get,            'c!:direct_call_func, -11);
  put('lognot,         'c!:direct_call_func, -12);
  put('ash,            'c!:direct_call_func, -13);
  put('quotient,       'c!:direct_call_func, -14);
  put('remainder,      'c!:direct_call_func, -15);
  put('times2,         'c!:direct_call_func, -16);
  put('minus,          'c!:direct_call_func, -17);
  put('rational,       'c!:direct_call_func, -18);
  put('lessp,          'c!:direct_call_func, -19);
  put('leq,            'c!:direct_call_func, -20);
  put('greaterp,       'c!:direct_call_func, -21);
  put('geq,            'c!:direct_call_func, -22);
  put('zerop,          'c!:direct_call_func, -23);
  put('reclaim,        'c!:direct_call_func, -24);
  put('error,          'c!:direct_call_func, -25);
  put('equal_fn,       'c!:direct_call_func, -26);
  put('cl_equal_fn,    'c!:direct_call_func, -27);
  put('aerror,         'c!:direct_call_func, -28);
  put('integerp,       'c!:direct_call_func, -29);
  put('apply,          'c!:direct_call_func, -30);
>>;

fluid '(off_env off_nargs);

off_nargs := 12;  % off_env is set dynamically in cg_fndef

symbolic procedure i!:translate_memref(a);
% Check if an atomic symbol is a variable of the program being compiled, and
% if so, return its assembler representation (memory address in a suitable
% form). The first line implements the general mechanism of translating
% references for local variables kept in stack. For such a symbolic variable
% the 'i!:locoffs property should contain its offset in stack. The rest deals
% with the translation of symbolic representations of CSL internal variables.
%
% ACN dislikes the use of the STRING "nil" here. Also resolution of the
% addresses of C_nil, stack etc should be deferred to load time. But leave
% it as it is for now since it works!
%
  if (get(a, 'i!:locoffs)) then {'ebp, get(a, 'i!:locoffs)}
  else if a = "nil" then {'ebp,-4}
  else if a = 'env or a = '!.env then {'ebp,off_env}
  else if a = 'C_nil then {'ds,OFS_NIL}
  else if a = 'stack then {'ds,OFS_STACK}
  else if a = 'lisp_true then {'ds,OFS_LISP_TRUE}
  else if a = 'current_modulus then {'ds,OFS_CURRENT_MODULUS}
  else if a = 'stacklimit then {'ds,OFS_STACKLIMIT}
  else if flagp(a, 'c!:live_across_call) then {'ebx,-get(a, 'c!:location)*4}
  else a;  % Otherwise we hope that this is a symbolic label - a call
           % or jump operand.


symbolic procedure i!:outmemfield(reg, mem);
% Generate the second and further bytes of the instruction whose operand is
% memory. For 2-arg instructions reg means code of the register operand,
% for 1-arg instructions it is a secondary opcode
% Examples of the forms of memory references accepted are given below:
% {ds,1234}, {ebx,-16}, {eax,2,ebx}, {ecx,4,edx,32}
 begin
   scalar secbyte, thirdbyte, constofs, constofslong, reg1name,
          reg1, reg2, mul;

   reg1name := car mem;
   reg1 := get(reg1name, 'i!:regcode);

   if length mem = 1 or
      ((length mem = 2) and numberp cadr mem) then <<
     % [reg1] or [reg1 + ofs]
     secbyte := reg*8 + reg1;
     mem := cdr mem;

     % Curious peculiarities of constant offset length field behaviour
     % when ebp (or ds) is an operand force me to do this weird thing.
     if (not mem) and (reg1name = 'ebp) then mem := cons(0, nil);

     if mem then <<
       constofs := car mem;
       if (constofs > 127) or (constofs < -128) or (reg1name = 'ds) then <<
         if reg1name neq 'ds then secbyte := secbyte + 0x80;
         constofslong := t >>
       else <<
         secbyte := secbyte + 0x40;
         constofslong := nil >>
       >>;
     i_putbyte secbyte
     >>
   else <<  % [reg + reg] or [reg + const*reg] or [reg + const*reg + ofs]
     secbyte := 0x04 + reg*8; % 0x04 is a magic number, imho
     thirdbyte := reg1;
     mem := cdr mem;
     if numberp car mem then <<
       mul := car mem;
       if mul = 8 then thirdbyte := thirdbyte + 0xc0
       else if mul = 4 then thirdbyte := thirdbyte + 0x80
       else if mul = 2 then thirdbyte := thirdbyte + 0x40;
       mem := cdr mem >>;
     reg2 := get(car mem, 'i!:regcode);
     thirdbyte := thirdbyte + reg2*8;
     mem := cdr mem;

     if (not mem) and (reg1name = 'ebp) then mem := 0 . nil;

     if mem then <<
       constofs := car mem;
       if (constofs > 127) or (constofs < -128) then <<
         % Weird thing with ebp again - only for it in this case we should
         % put 00 in two bits representing the offset length
         if reg1name neq 'ebp then secbyte := secbyte + 0x80;
         constofslong := t >>
       else <<
         secbyte := secbyte + 0x40;
         constofslong := nil >>
       >>
     else constofs := nil;
     i_putbyte secbyte;
     i_putbyte thirdbyte
     >>;

   if constofs then
     if constofslong then <<
         if reg1name='ds then i_putextern list('absolute, constofs)
         else i_put32 constofs >>
     else i_putbyte ilogand(constofs, 0xff)
 end;


symbolic procedure i!:remove_reg_memrefs(reg);
% A part of peephole optimisation. We maintain the table which has an entry
% per register. An entry for register reg contains registers and memory
% references whose contents are equal to reg. When reg is changed, we
% must flush its entry. This is already done when this procedure called.
% But what we should also do (here) is to check if the buffer for any
% register other than reg contains reg or a memory reference which includes
% reg, such as {reg,1000}, and remove all such references.
begin
  scalar regi, regi1, memref;

  for i := 0:2 do <<
    regi := getv(i!:reg_vec, i);
    regi1 := nil;
    while regi neq nil do <<
      memref := car regi;
      regi := cdr regi;
      if (atom memref) and (memref neq reg) then regi1 := memref . regi1
      else if not member(reg, memref) then regi1 := memref . regi1;
      >>;
    putv(i!:reg_vec, i, regi1)
    >>
end;


symbolic procedure i!:eq_to_reg(mem);
% Check if a memory variable is equal to some register at the current moment
begin
  scalar i,res;

  res := nil;
  for i := 0:2 do
    if member(mem, getv(i!:reg_vec, i)) then res := i;

  return res;
end;


symbolic procedure i!:regname(code);
% Return register symbolic name for its code
  if code = 0 then 'eax
  else if code = 1 then 'ecx
  else if code = 2 then 'edx
  else error1 "bad regname";


symbolic procedure encomment(reg1, a1);
   if reg1 then list a1
   else begin
     scalar x;
     x := i!:translate_memref a1;
     if a1 = x then return list a1
     else return list(x, '!;, list a1) end;

symbolic procedure i!:2arginstr(instr, a1, a2);
% Process an instruction with two arguments
 begin
   scalar reg1, reg2, isnuma2, longnuma2, code, secopcode,
          tmp, dbit, pref, c1, c2;

   reg1 := get(a1, 'i!:regcode);
   reg2 := get(a2, 'i!:regcode);
   isnuma2 := numberp a2;
   if isnuma2 then longnuma2 := not zerop irightshift(a2,8);

   % Peephole optimisation - replace "instr d,mem" with
   %                                 "instr d,reg" if reg = mem
   if (not reg2) and (not isnuma2) then <<
     reg2 := i!:eq_to_reg(a2);
     if reg2 and not ((instr = 'mov) and (reg1 = reg2)) then
        a2 := i!:regname(reg2)
     else reg2 := nil;
     >>;

   % Peephole optimisation - redundant memory-register transfers suppression
   if (reg1) and (reg1 <= 2) then <<
     if flagp(instr, 'i!:changes_reg) then <<
       putv(i!:reg_vec, reg1, nil);
       i!:remove_reg_memrefs(a1);
       >>
     else if (instr = 'mov) then << % mov reg1, a2(which is mem or reg)
       if member(a2, getv(i!:reg_vec, reg1)) then % Suppress MOV
          return nil
       else <<
         i!:remove_reg_memrefs(a1);
         if not reg2 then <<  % a2 is a memory location
           if (not atom a2) and (member(a1,a2)) then
              putv(i!:reg_vec, reg1, nil)
           else putv(i!:reg_vec, reg1, a2 . nil) >>
         else <<              % a2 is a register
           putv(i!:reg_vec, reg1, a2 . getv(i!:reg_vec, reg2));
           putv(i!:reg_vec, reg2, a1 . getv(i!:reg_vec, reg2));
           >>
         >>
       >>
     >>
   else if (instr = 'mov) and reg2 and (reg2 <= 2) then <<
     if member(a1, getv(i!:reg_vec, reg2)) then  % Suppress MOV
        return nil
     else <<
       for i := 0:2 do
         putv(i!:reg_vec, i, delete(a1, getv(i!:reg_vec,i)));
       putv(i!:reg_vec, reg2, a1 . getv(i!:reg_vec, reg2))
       >>
     >>;

   c1 := encomment(reg1, a1); c2 := encomment(reg2, a2);
   if null cdr c1 then c1 := append(c1, c2)
   else c1 := car c1 . append(c2, cdr c1);

   i_putcomment (instr . c1);

   if reg1 then           % Immediate/register/memory to register variant
     if isnuma2 then <<   % Immediate to register variants
       if longnuma2 and (a1 = 'eax) then code := get(instr, 'i!:immed!-eax)
       else code := nil;
       if code then <<    % "Immediate to eax" version of instruction
         i_putbyte code;
         i_put32 a2;
         >>
       else <<            % "Immediate to register" version of
                          % instruction (MOV,?..)
         code := get(instr, 'i!:immed!-reg);
         if code then <<
           i_putbyte(code + reg1);
           i_put32 a2;
           >>
         else <<          % General "immediate to register/memory" version
           code := get(instr, 'i!:immed!-rm);
           if code then <<
             secopcode := get(instr, 'i!:immed!-rm!-secopcode);
             if not secopcode then secopcode := reg1;

             if longnuma2 then <<  % Long immediate constant
               if flagp(instr, 'i!:immed!-rm!-shortformonly) then <<
                 error1 "Long constant is invalid here" >>;
               i_putbyte code; i_putbyte(0xc0 + secopcode*8 + reg1);
               i_put32 a2
               >>
             else <<               % Short immediate constant
               if flagp(instr, 'i!:immed!-rm!-noshortform) then <<
                 i_putbyte code; i_putbyte(0xc0 + secopcode*8 + reg1);
                 i_put32 a2 >>
               else if flagp(instr, 'i!:immed!-rm!-shortformonly) then <<
                 i_putbyte code; i_putbyte(0xc0 + secopcode*8 + reg1);
                 i_putbyte a2 >>
               else <<
                 i_putbyte(code+2);
                 i_putbyte(0xc0 + secopcode*8 + reg1);
                 i_putbyte a2 >>
               >>
             >>
           else error1 "Invalid combination of opcode and operands 1"
           >>
         >>
       >>
     else <<              % Register/memory to register
       code := get(instr, 'i!:rm!-reg);
       if not code then
         error1 "Invalid combination of opcode and operands 2";
       if reg2 then <<    % Register to register
         if (pref := get(instr, 'i!:rm!-reg!-prefix)) then i_putbyte pref;
         if (dbit := get(instr, 'i!:rm!-reg!-dbit_preset)) then <<
           % Special case when changing d bit changes the whole instruction
           i_putbyte code;
           if dbit = 0 then <<
             tmp := reg1; reg1 := reg2; reg2 := tmp >>
           >>
         else i_putbyte(code + 2);
         i_putbyte(0xc0 + reg1*8 + reg2)
         >>
       else <<            % Memory to register
         if atom a2 then a2 := i!:translate_memref(a2);
         if car a2 = 'ds then <<
           i_putbyte 0x3E;
           if (instr = 'mov) and (reg1 = 0) then << % mov eax,ds:[...]
             i_putbyte 0xa1;
             i_putextern list('absolute, cadr a2); 
             % More complicated ds addressing is not implemented yet!
             return nil
             >>
           >>;
         i_putbyte(code + 2);
         i!:outmemfield(reg1, a2)
         >>
       >>

   else if reg2 then <<   % Register to memory
     code := get(instr, 'i!:rm!-reg);
     if not code then
       error1 "Invalid combination of opcode and operands 3";
     if atom a1 then a1 := i!:translate_memref(a1);
     if car a1 = 'ds then <<
       i_putbyte 0x3E;
       if (instr = 'mov) and (reg2 = 0) then << % mov ds:[...],eax
         i_putbyte 0xa3;
         i_putextern list('absolute, cadr a1);
         % More complicated ds addressing is not implemented yet!
         return nil
         >>
       >>;
     i_putbyte code;
     i!:outmemfield(reg2, a1)
     >>

   else error1 "Invalid combination of opcode and operands 4"

 end;


symbolic procedure i!:1arginstr(instr, a1);
% Process an instruction with one argument
 begin
   scalar reg1, code, secopcode, labrec, curpos, dist;

   reg1 := get(a1, 'i!:regcode);
   % Peephole optimisation - replace push mem with push reg if mem = reg
   if (not reg1) and (instr = 'push) then <<
     reg1 := i!:eq_to_reg(a1);
     if reg1 then a1 := i!:regname(reg1)
     >>;

   if not reg1 and atom a1 then a1 := i!:translate_memref(a1);

   % Part of peephole optimisation - control of changing register contents
   if flagp(instr, 'i!:changes_reg) and reg1 and (reg1 <= 2) then <<
     putv(i!:reg_vec, reg1, nil);
     i!:remove_reg_memrefs(a1)
     >>;

   i_putcomment (instr . encomment(reg1, a1));

   if atom a1 then <<       % Register or label operand
     if reg1 then <<        % Register operand
       code := get(instr, 'i!:reg);
       if code then <<      % "Register" version of instruction
         secopcode := get(instr, 'i!:reg!-secopcode);
         if not secopcode then i_putbyte(code + reg1)
         else <<
           i_putbyte code;
           i_putbyte(secopcode + reg1) >>
         >>
       else <<              % "Register/memory" version of instruction
         code := get(instr, 'i!:rm);
         secopcode := get(instr, 'i!:rm!-secopcode);
         i_putbyte(code+2);
         i_putbyte(0xc0 + secopcode*8 + reg1)
         >>
       >>
     else if numberp a1 then <<  % Immediate operand
       if (a1 > 127) or (a1 < -128) then <<
         code := get(instr, 'i!:immed32);
         i_putbyte code;
         i_put32 a1 >>
       else <<
         code := get(instr, 'i!:immed8);
         i_putbyte code;
         i_putbyte a1 >>
       >>
     else <<                % Jumps and call remain, thus label operand
       code := get(instr, 'i!:jump);
       if not code then
         error1 "Invalid combination of opcode and operands 1";

       if instr = 'call then <<
printc("##### CALL ", a1);
         i_putbyte code;
         i_putextern list('rel_plus_4, 99);      % What am I calling????
         % Part of peephole optimisation
         for i := 0:2 do putv(i!:reg_vec, i, nil)
         >>
       else i_putjump(code, a1);
       >>
     >>
   else <<                  % Memory operand
     code := get(instr, 'i!:mem);
     secopcode := get(instr, 'i!:mem!-secopcode);
     if not secopcode then secopcode := 0;
     if car a1 = 'ds then i_putbyte 0x3E;
     i_putbyte code;
     i!:outmemfield(secopcode, a1);
     >>

 end;


symbolic procedure i!:noarginstr instr;
% Process an instruction with no arguments
 << i_putcomment list instr;
    i_putbyte get(instr,'i!:code) >>;


symbolic procedure i!:proc_label lab;
% Process a label
 begin
  i_putlabel lab;
  % Part of peephole optimisation
  for i := 0:2 do putv(i!:reg_vec, i, nil)
 end;




%
% Now the higher level parts of the compiler.
%


global '(!*fastvector !*unsafecar);
flag('(fastvector unsafecar), 'switch);

% Some internal CSL constants
global '(TAG_BITS TAG_CONS TAG_FIXNUM TAG_ODDS TAG_SYMBOL TAG_NUMBERS
         TAG_VECTOR GC_STACK SPID_NOPROP);
TAG_BITS    := 7;
TAG_CONS    := 0;
TAG_FIXNUM  := 1;
TAG_ODDS    := 2;
TAG_SYMBOL  := 4;
TAG_NUMBERS := 5;
TAG_VECTOR  := 6;
GC_STACK    := 2;
SPID_NOPROP := 0xc2 + 0x0b00;



%
% I start with some utility functions that provide something
% related to a FORMAT or PRINTF facility
%


% This establishes a default handler for each special form so that
% any that I forget to treat more directly will cause a tidy error
% if found in compiled code.

symbolic procedure c!:cspecform(x, env);
   error(0, list("special form", x));

<< put('and,                    'c!:code, function c!:cspecform);
!#if common!-lisp!-mode
   put('block,                  'c!:code, function c!:cspecform);
!#endif
   put('catch,                  'c!:code, function c!:cspecform);
   put('compiler!-let,          'c!:code, function c!:cspecform);
   put('cond,                   'c!:code, function c!:cspecform);
   put('declare,                'c!:code, function c!:cspecform);
   put('de,                     'c!:code, function c!:cspecform);
!#if common!-lisp!-mode
   put('defun,                  'c!:code, function c!:cspecform);
!#endif
   put('eval!-when,             'c!:code, function c!:cspecform);
   put('flet,                   'c!:code, function c!:cspecform);
   put('function,               'c!:code, function c!:cspecform);
   put('go,                     'c!:code, function c!:cspecform);
   put('if,                     'c!:code, function c!:cspecform);
   put('labels,                 'c!:code, function c!:cspecform);
!#if common!-lisp!-mode
   put('let,                    'c!:code, function c!:cspecform);
!#else
   put('!~let,                  'c!:code, function c!:cspecform);
!#endif
   put('let!*,                  'c!:code, function c!:cspecform);
   put('list,                   'c!:code, function c!:cspecform);
   put('list!*,                 'c!:code, function c!:cspecform);
   put('macrolet,               'c!:code, function c!:cspecform);
   put('multiple!-value!-call,  'c!:code, function c!:cspecform);
   put('multiple!-value!-prog1, 'c!:code, function c!:cspecform);
   put('or,                     'c!:code, function c!:cspecform);
   put('prog,                   'c!:code, function c!:cspecform);
   put('prog!*,                 'c!:code, function c!:cspecform);
   put('prog1,                  'c!:code, function c!:cspecform);
   put('prog2,                  'c!:code, function c!:cspecform);
   put('progn,                  'c!:code, function c!:cspecform);
   put('progv,                  'c!:code, function c!:cspecform);
   put('quote,                  'c!:code, function c!:cspecform);
   put('return,                 'c!:code, function c!:cspecform);
   put('return!-from,           'c!:code, function c!:cspecform);
   put('setq,                   'c!:code, function c!:cspecform);
   put('tagbody,                'c!:code, function c!:cspecform);
   put('the,                    'c!:code, function c!:cspecform);
   put('throw,                  'c!:code, function c!:cspecform);
   put('unless,                 'c!:code, function c!:cspecform);
   put('unwind!-protect,        'c!:code, function c!:cspecform);
   put('when,                   'c!:code, function c!:cspecform) >>;

fluid '(current_procedure current_args current_block current_contents
        all_blocks registers stacklocs);

fluid '(available used);

available := used := nil;

fluid '(lab_end_proc);

symbolic procedure c!:reset_gensyms();
 << remflag(used, 'c!:live_across_call);
    remflag(used, 'c!:visited);
    while used do <<
      remprop(car used, 'c!:contents);
      remprop(car used, 'c!:why);
      remprop(car used, 'c!:where_to);
      remprop(car used, 'c!:count);
      remprop(car used, 'c!:live);
      remprop(car used, 'c!:clash);
      remprop(car used, 'c!:chosen);
      remprop(car used, 'c!:location);
      remprop(car used, 'i!:locoffs);
      if plist car used then begin
         scalar o; o := wrs nil;
         princ "+++++ "; prin car used; princ " ";
         prin plist car used; terpri();
         wrs o end;
      available := car used . available;
      used := cdr used >> >>;

!#if common!-lisp!-mode

fluid '(my_gensym_counter);
my_gensym_counter := 0;

!#endif

symbolic procedure c!:my_gensym();
  begin
    scalar w;
    if available then << w := car available; available := cdr available >>
!#if common!-lisp!-mode
    else w := compress1
       ('!v . explodec (my_gensym_counter := my_gensym_counter + 1));
!#else
    else w := gensym1 "v";
!#endif
    used := w . used;
    if plist w then << princ "????? "; prin w; princ " => "; prin plist w; terpri() >>;
    return w
  end;

symbolic procedure c!:newreg();
  begin
    scalar r;
    r := c!:my_gensym();
    registers := r . registers;
    return r
  end;

symbolic procedure c!:startblock s;
 << current_block := s;
    current_contents := nil
 >>;

symbolic procedure c!:outop(a,b,c,d);
  if current_block then
     current_contents := list(a,b,c,d) . current_contents;

symbolic procedure c!:endblock(why, where_to);
  if current_block then <<
% Note that the operations within a block are in reversed order.
    put(current_block, 'c!:contents, current_contents);
    put(current_block, 'c!:why, why);
    put(current_block, 'c!:where_to, where_to);
    all_blocks := current_block . all_blocks;
    current_contents := nil;
    current_block := nil >>;

%
% Now for a general driver for compilation
%

symbolic procedure c!:cval_inner(x, env);
  begin
    scalar helper;
% NB use the "improve" function from the regular compiler here...
    x := s!:improve x;
% atoms and embedded lambda expressions need their own treatment.
    if atom x then return c!:catom(x, env)
    else if eqcar(car x, 'lambda) then
       return c!:clambda(cadar x, 'progn . cddar x, cdr x, env)
% a c!:code property gives direct control over compilation
    else if helper := get(car x, 'c!:code) then
       return funcall(helper, x, env)
% compiler-macros take precedence over regular macros, so that I can
% make special expansions in the context of compilation. Only used if the
% expansion is non-nil
    else if (helper := get(car x, 'c!:compile_macro)) and
            (helper := funcall(helper, x)) then
       return c!:cval(helper, env)
% regular Lisp macros get expanded
    else if idp car x and (helper := macro!-function car x) then
       return c!:cval(funcall(helper, x), env)
% anything not recognised as special will be turned into a
% function call, but there will still be special cases, such as
% calls to the current function, calls into the C-coded kernel, etc.
    else return c!:ccall(car x, cdr x, env)
  end;

symbolic procedure c!:cval(x, env);
  begin
     scalar r;
     r := c!:cval_inner(x, env);
     if r and not member!*!*(r, registers) then
        error(0, list(r, "not a register", x));
     return r
  end;

symbolic procedure c!:clambda(bvl, body, args, env);
  begin
    scalar w, fluids, env1;
    env1 := car env;
    w := for each a in args collect c!:cval(a, env);
    for each v in bvl do <<
       if globalp v then begin scalar oo;
           oo := wrs nil;
           princ "+++++ "; prin v;
           princ " converted from GLOBAL to FLUID"; terpri();
           wrs oo;
           unglobal list v;
           fluid list v end;
       if fluidp v then <<
          fluids := (v . c!:newreg()) . fluids;
          flag(list cdar fluids, 'c!:live_across_call); % silly if not
          env1 := ('c!:dummy!:name . cdar fluids) . env1;
          c!:outop('ldrglob, cdar fluids, v, c!:find_literal v);
          c!:outop('strglob, car w, v, c!:find_literal v) >>
       else <<
          env1 := (v . c!:newreg()) . env1;
          c!:outop('movr, cdar env1, nil, car w) >>;
       w := cdr w >>;
    if fluids then c!:outop('fluidbind, nil, nil, fluids);
    env := env1 . append(fluids, cdr env);
    w := c!:cval(body, env);
    for each v in fluids do
       c!:outop('strglob, cdr v, car v, c!:find_literal car v);
    return w
  end;

symbolic procedure c!:locally_bound(x, env);
   atsoc(x, car env);

flag('(nil t), 'c!:constant);

fluid '(literal_vector);

symbolic procedure c!:find_literal x;
  begin
    scalar n, w;
    w := literal_vector;
    n := 0;
    while w and not (car w = x) do <<
      n := n + 1;
      w := cdr w >>;
    if null w then literal_vector := append(literal_vector, list x);
    return n
  end;

symbolic procedure c!:catom(x, env);
  begin
    scalar v, w;
    v := c!:newreg();
    if idp x and (w := c!:locally_bound(x, env)) then
       c!:outop('movr, v, nil, cdr w)
    else if null x or x = 't or c!:small_number x then
       c!:outop('movk1, v, nil, x)
    else if not idp x or flagp(x, 'c!:constant) then
       c!:outop('movk, v, x, c!:find_literal x)
    else c!:outop('ldrglob, v, x, c!:find_literal x);
    return v
  end;

symbolic procedure c!:cjumpif(x, env, d1, d2);
  begin
    scalar helper, r;
    x := s!:improve x;
    if atom x and (not idp x or
         (flagp(x, 'c!:constant) and not c!:locally_bound(x, env))) then
       c!:endblock('goto, list (if x then d1 else d2))
    else if not atom x and (helper := get(car x, 'c!:ctest)) then
       return funcall(helper, x, env, d1, d2)
    else <<
       r := c!:cval(x, env);
       c!:endblock(list('ifnull, r), list(d2, d1)) >>
  end;

fluid '(current);

symbolic procedure c!:ccall(fn, args, env);
  c!:ccall1(fn, args, env);

fluid '(visited);

symbolic procedure c!:has_calls(a, b);
  begin
    scalar visited;
    return c!:has_calls_1(a, b)
  end;

symbolic procedure c!:has_calls_1(a, b);
% true if there is a path from node a to node b that has a call instruction
% on the way.
  if a = b or not atom a or memq(a, visited) then nil
  else begin
    scalar has_call;
    visited := a . visited;
    for each z in get(a, 'c!:contents) do
       if eqcar(z, 'call) then has_call := t;
    if has_call then return
       begin scalar visited;
       return c!:can_reach(a, b) end;
    for each d in get(a, 'c!:where_to) do
       if c!:has_calls_1(d, b) then has_call := t;
    return has_call
  end;

symbolic procedure c!:can_reach(a, b);
  if a = b then t
  else if not atom a or memq(a, visited) then nil
  else <<
    visited := a . visited;
    c!:any_can_reach(get(a, 'c!:where_to), b) >>;

symbolic procedure c!:any_can_reach(l, b);
  if null l then nil
  else if c!:can_reach(car l, b) then t
  else c!:any_can_reach(cdr l, b);

symbolic procedure c!:pareval(args, env);
  begin
    scalar tasks, tasks1, merge, split, r;
    tasks := for each a in args collect (c!:my_gensym() . c!:my_gensym());
    split := c!:my_gensym();
    c!:endblock('goto, list split);
    for each a in args do begin
      scalar s;
% I evaluate each arg as what is (at this stage) a separate task
      s := car tasks;
      tasks := cdr tasks;
      c!:startblock car s;
      r := c!:cval(a, env) . r;
      c!:endblock('goto, list cdr s);
% If the task did no procedure calls (or only tail calls) then it can be
% executed sequentially with the other args without need for stacking
% anything.  Otherwise it more care will be needed.  Put the hard
% cases onto tasks1.
!#if common!-lisp!-mode
      tasks1 := s . tasks1
!#else
      if c!:has_calls(car s, cdr s) then tasks1 := s . tasks1
      else merge := s . merge
!#endif
    end;
%-- % if there are zero or one items in tasks1 then again it is easy -
%-- % otherwise I flag the problem with a notionally parallel construction.
%--     if tasks1 then <<
%--        if null cdr tasks1 then merge := car tasks1 . merge
%--        else <<
%--           c!:startblock split;
%--           printc "***** ParEval needed parallel block here...";
%--           c!:endblock('par, for each v in tasks1 collect car v);
%--           split := c!:my_gensym();
%--           for each v in tasks1 do <<
%--              c!:startblock cdr v;
%--              c!:endblock('goto, list split) >> >> >>;
    for each z in tasks1 do merge := z . merge; % do sequentially
%--
%--
% Finally string end-to-end all the bits of sequential code I have left over.
    for each v in merge do <<
      c!:startblock split;
      c!:endblock('goto, list car v);
      split := cdr v >>;
    c!:startblock split;
    return reversip r
  end;

symbolic procedure c!:ccall1(fn, args, env);
  begin
    scalar tasks, merge, r, val;
    fn := list(fn, cdr env);
    val := c!:newreg();
    if null args then c!:outop('call, val, nil, fn)
    else if null cdr args then
      c!:outop('call, val, list c!:cval(car args, env), fn)
    else <<
      r := c!:pareval(args, env);
      c!:outop('call, val, r, fn) >>;
    c!:outop('reloadenv, 'env, nil, nil);
    return val
  end;

fluid '(restart_label reloadenv does_call current_c_name);

%
% The "proper" recipe here arranges that functions that expect over 2 args use
% the "va_arg" mechanism to pick up ALL their args.  This would be pretty
% heavy-handed, and at least on a lot of machines it does not seem to
% be necessary.  I will duck it for a while more at least.
%

fluid '(proglabs blockstack retloc);

symbolic procedure c!:cfndef(current_procedure, current_c_name, args, body);
  begin
    scalar env, n, w, current_args, current_block, restart_label,
           current_contents, all_blocks, entrypoint, exitpoint, args1,
           registers, stacklocs, literal_vector, reloadenv, does_call,
           blockstack, proglabs, stackoffs, env_vec, i, retloc;

    c!:reset_gensyms();
    i_startproc();
    i!:reg_vec := mkvect 2;
    c!:find_literal current_procedure; % For benefit of backtraces
%
% cope with fluid vars in an argument list by mapping the definition
%    (de f (a B C d) body)     B and C fluid
% onto
%    (de f (a x y c) (prog (B C) (setq B x) (setq C y) (return body)))
% so that the fluids get bound by PROG.
%
    current_args := args;
    for each v in args do
       if v = '!&optional or v = '!&rest then
          error(0, "&optional and &rest not supported by this compiler (yet)")
       else if globalp v then begin scalar oo;
          oo := wrs nil;
          princ "+++++ "; prin v;
          princ " converted from GLOBAL to FLUID"; terpri();
          wrs oo;
          unglobal list v;
          fluid list v;
          n := (v . c!:my_gensym()) . n end
       else if fluidp v then n := (v . c!:my_gensym()) . n;

    restart_label := c!:my_gensym();
    body := list('c!:private_tagbody, restart_label, body);
    if n then <<
       body := list list('return, body);
       args := subla(n, args);
       for each v in n do
         body := list('setq, car v, cdr v) . body;
       body := 'prog . (for each v in reverse n collect car v) . body >>;

    n := length args;
    if n = 0 or n >= 3 then w := t else w := nil;

    if w or i_machine = 4 then off_env := 8 else off_env := 4;

% Here I FUDDGE the issue of args passed in registers by flushing them
% back to the stack. I guess I will need to repair the stack to
% compensate somewhere too...
    retloc := 0;
    if i_machine = 2 then <<
       if n = 1 then << i!:gopcode(push,edx, push,eax); retloc := 2 >>
       else if n = 2 then << i!:gopcode(push,ebx, push,edx, push,eax); retloc := 3 >> >>
    else if i_machine = 3 then <<
       if n = 1 or n = 2 then i!:gopcode(push, edx, push, ecx);
       retloc := 2 >>;

    if i_machine = 4 then <<
       if w then stackoffs := 16 else stackoffs := 12 >>
    else if i_machine = 3 then <<
       if w then stackoffs := 16 else stackoffs := 8 >>
    else if i_machine = 2 then <<
       if w then stackoffs := 12 else stackoffs := 8 >>
    else error(0, "unknown machine");

    n := 0;
    env := nil;
    for each x in args do begin
       scalar aa;
       n := n+1;
       if n = retloc then stackoffs := stackoffs+4;
       aa := c!:my_gensym();
       env := (x . aa) . env;
       registers := aa . registers;
       args1 := aa . args1;
       put(aa, 'i!:locoffs, stackoffs);
       stackoffs := stackoffs + 4
       end;
    c!:startblock (entrypoint := c!:my_gensym());
    exitpoint := current_block;
    c!:endblock('goto, list list c!:cval(body, env . nil));

    c!:optimise_flowgraph(entrypoint, all_blocks, env,
                        length args . current_procedure, args1);


    env_vec := mkvect(length literal_vector - 1);
    i := 0;
    for each v in literal_vector do <<
       putv(env_vec, i, v);
       i := i + 1 >>;

    if !*genlisting then <<
       terpri();
       ttab 28;
       princ "+++ Native code for ";
       prin current_procedure;
       printc " +++" >>;

    i := i_resolve();
    symbol!-set!-native(current_procedure, length args,
                        car i, cdr i,
                        env_vec);
    return nil
  end;

% c!:ccompile1 directs the compilation of a single function, and bind all the
% major fluids used by the compilation process

flag('(rds deflist flag fluid global
       remprop remflag unfluid
       unglobal dm carcheck i86!-end), 'eval);

flag('(rds), 'ignore);

fluid '(!*backtrace);

symbolic procedure c!:ccompilesupervisor;
  begin
    scalar u, w;
top:u := errorset('(read), t, !*backtrace);
    if atom u then return;      % failed, or maybe EOF
    u := car u;
    if u = !$eof!$ then return; % end of file
    if atom u then go to top
% the apply('i86!-end, nil) is here because i86!-end has a "stat"
% property and so it will mis-parse if I just write "i86!-end()".  Yuk.
    else if eqcar(u, 'i86!-end) then return apply('i86!-end, nil)
    else if eqcar(u, 'rdf) then <<
!#if common!-lisp!-mode
       w := open(u := eval cadr u, !:direction, !:input,
                 !:if!-does!-not!-exist, nil);
!#else
       w := open(u := eval cadr u, 'input);
!#endif
       if w then <<
          terpri();
          princ "Reading file "; print u;
          w := rds w;
          c!:ccompilesupervisor();
          princ "End of file "; print u;
          close rds w >>
       else << princ "Failed to open file "; print u >> >>
    else c!:ccmpout1 u;
    go to top
  end;


global '(c!:char_mappings);

c!:char_mappings := '(
  (!  . !A)  (!! . !B)  (!# . !C)  (!$ . !D)
  (!% . !E)  (!^ . !F)  (!& . !G)  (!* . !H)
  (!( . !I)  (!) . !J)  (!- . !K)  (!+ . !L)
  (!= . !M)  (!\ . !N)  (!| . !O)  (!, . !P)
  (!. . !Q)  (!< . !R)  (!> . !S)  (!: . !T)
  (!; . !U)  (!/ . !V)  (!? . !W)  (!~ . !X)
  (!` . !Y));

symbolic procedure c!:inv_name n;
  begin
    scalar r, w;
    r := '(_ !C !C !");
!#if common!-lisp!-mode
    for each c in explode2 package!-name symbol!-package n do <<
      if c = '_ then r := '_ . r
      else if alpha!-char!-p c or digit c then r := c . r
      else if w := atsoc(c, c!:char_mappings) then r := cdr w . r
      else r := '!Z . r >>;
    r := '!_ . '!_ . r;
!#endif
    for each c in explode2 n do <<
      if c = '_ then r := '_ . r
!#if common!-lisp!-mode
      else if alpha!-char!-p c or digit c then r := c . r
!#else
      else if liter c or digit c then r := c . r
!#endif
      else if w := atsoc(c, c!:char_mappings) then r := cdr w . r
      else r := '!Z . r >>;
    r := '!" . r;
!#if common!-lisp!-mode
    return compress1 reverse r
!#else
    return compress reverse r
!#endif
  end;


fluid '(defnames);

symbolic procedure c!:ccmpout1 u;
  begin
    scalar w;

    if atom u then return nil
    else if eqcar(u, 'progn) then <<
       for each v in cdr u do codesize := codesize + c!:ccmpout1 v;
       return nil >>
    else if eqcar(u, 'i86!-end) then nil
    else if flagp(car u, 'eval) or
          (car u = 'setq and not atom caddr u and flagp(caaddr u, 'eval)) then
       errorset(u, t, !*backtrace);
    if eqcar(u, 'rdf) then begin
!#if common!-lisp!-mode
       w := open(u := eval cadr u, !:direction, !:input,
                 !:if!-does!_not!-exist, nil);
!#else
       w := open(u := eval cadr u, 'input);
!#endif
       if w then <<
          princ "Reading file "; print u;
          w := rds w;
          c!:ccompilesupervisor();
          princ "End of file "; print u;
          close rds w >>
       else << princ "Failed to open file "; print u >> end
!#if common!-lisp!-mode
    else if eqcar(u, 'defun) then return c!:ccmpout1 macroexpand u
!#endif
    else if eqcar(u, 'de) then <<
        u := cdr u;
!#if common!-lisp!-mode
        w := compress1 ('!" . append(explodec package!-name
                                       symbol!-package car u,
                        '!@ . '!@ . append(explodec symbol!-name car u,
                        append(explodec "@@Builtin", '(!")))));
        w := intern w;
        defnames := list(car u, c!:inv_name car u, length cadr u, w) . defnames;
!#else
        defnames := list(car u, c!:inv_name car u, length cadr u) . defnames;
!#endif
        if posn() neq 0 then terpri();
        princ "Compiling "; prin caar defnames; princ " ... ";
        c!:cfndef(caar defnames, cadar defnames, cadr u, 'progn . cddr u);
        terpri() >>;

    return nil;
  end;


fluid '(!*defn dfprint!* dfprintsave);

!#if common!-lisp!-mode
symbolic procedure c!:concat(a, b);
   compress1('!" . append(explode2 a, append(explode2 b, '(!"))));
!#else
symbolic procedure c!:concat(a, b);
   compress('!" . append(explode2 a, append(explode2 b, '(!"))));
!#endif

symbolic procedure c!:ccompilestart name;
    defnames := nil;


symbolic procedure i86!-end;
<<
    !*defn := nil;
    dfprint!* := dfprintsave
>>;

put('i86!-end, 'stat, 'endstat);

symbolic procedure i86!-begin u;
 begin
    terpri();
    princ "IN files;  or type in expressions"; terpri();
    princ "When all done, execute i86!-END;"; terpri();
    verbos nil;
    defnames := nil;
    dfprintsave := dfprint!*;
    dfprint!* := 'c!:ccmpout1;
    !*defn := t;
    if getd 'begin then return nil;
    return c!:ccompilesupervisor()
    % There is a problem with compilesupervisor at the moment, so this way the
    % function does not return code size.
  end;


put('i86!-begin, 'stat, 'rlis);


symbolic procedure i86!-compile u;
  begin
    defnames := nil;   % but subsequently ignored!
    c!:ccmpout1 u;
  end;


%
% Global treatment of a flow-graph...
%

symbolic procedure c!:print_opcode(s, depth);
  begin
    scalar op, r1, r2, r3, helper;
    op := car s; r1 := cadr s; r2 := caddr s; r3 := cadddr s;
    helper := get(op, 'c!:opcode_printer);
    if helper then funcall(helper, op, r1, r2, r3, depth)
    else << prin s; terpri() >>
  end;

symbolic procedure c!:print_exit_condition(why, where_to, depth);
  begin
    scalar helper, lab1, drop1, lab2, drop2, negate, jmptype, args,
           nargs, iflab1, iflab2, lab_end, pops;
% An exit condition is one of
%     goto          (lab)
%     goto          ((return-register))
%     (ifnull v)    (lab1 lab2)    ) etc, where v is a register and
%     (ifatom v)    (lab1 lab2)    ) lab1, lab2 are labels for true & false
%     (ifeq v1 v2)  (lab1 lab2)    ) and various predicates are supported
%     ((call fn) a1 a2) ()         tail-call to given function
%
    if why = 'goto then <<
       where_to := car where_to;
       if atom where_to then <<
          i!:gopcode(jmp, where_to);
          c!:display_flowgraph(where_to, depth, t) >>
       else <<
          c!:pgoto(nil, where_to, depth) >>;
       return nil >>
    else if eqcar(car why, 'call) then return begin
       scalar locs, g, w;
       nargs := length cdr why;

       <<
          for each a in cdr why do
            if flagp(a, 'c!:live_across_call) then <<
               g := c!:my_gensym();
               args := g . args >>
            else args := a . args;

          i!:gopcode(push, esi);

% The next line is a HORRID fudge to keep ebx safe when it was going to be
% used by the calling standard. Ugh
          if i_machine = 2 and length cdr why = 2 then i!:gopcode(push,ebx);

          for each a in reverse(cdr why) do
            if flagp(a, 'c!:live_across_call) then
               i!:gopcode(push,{ebx,-get(a, 'c!:location)*4})
            else i!:gopcode(push, a);

          c!:pld_eltenv(c!:find_literal cadar why);

          % Compute qenv(fn) and put into edx
           i!:gopcode(mov,edx,{eax,4});
          % See further comments for the similar construction in c!:pcall
          if nargs = 1 then i!:gopcode(mov,esi,{eax,8})
          else if nargs = 2 then i!:gopcode(mov,esi,{eax,12})
          else <<
            i!:gopcode(mov,esi,{eax,16});
            i!:gopcode(push, nargs);
            nargs := nargs + 1
            >>;
          i!:gopcode(push,edx);
% Here I adapt (CRUDELY) for possibly different calling machanisms
          pops := 4*(nargs+1);
print list(i_machine, nargs, pops, 'tailcall);
          if i_machine = 2 and (pops = 8 or pops = 12) then <<
             i!:gopcode(pop,eax, pop,edx); pops := pops-8;
             if pops = 4 then << i!:gopcode(pop,ebx); pops := pops-4 >> >>
          else if i_machine = 3 and (pops = 8 or pops = 12) then <<
             i!:gopcode(pop,ecx, pop,edx); pops := pops-8 >>;
          i!:gopcode(call,esi);
          if pops neq 0 then i!:gopcode(add,esp,pops);

% The next line is a HORRID fudge to keep ebx safe when it was going to be
% used by the calling standard. Ugh
          if i_machine = 2 and length cdr why = 2 then i!:gopcode(pop,ebx);

          i!:gopcode(pop, esi);
          if depth neq 0 then c!:ppopv(depth);
          i!:gopcode(jmp,lab_end_proc)
          >>;
       return nil end;

    lab1 := car where_to;
    drop1 := atom lab1 and not flagp(lab1, 'c!:visited);
    lab2 := cadr where_to;
    drop2 := atom lab2 and not flagp(drop2, 'c!:visited);
    if drop2 and get(lab2, 'c!:count) = 1 then <<
       where_to := list(lab2, lab1);
       drop1 := t >>
    else if drop1 then negate := t;
    helper := get(car why, 'c!:exit_helper);
    if null helper then error(0, list("Bad exit condition", why));


    %! Left for testing purposes and should be removed later ------

    if not atom(car where_to) then
      % In this case it is implied that we should generate not just a jump, but
      % a piece of code which is executed if the condition is satisfied.
      iflab1 := c!:my_gensym();
    if not atom(cadr where_to) then iflab2 := c!:my_gensym();

    jmptype := funcall(helper, cdr why, negate);

    if not drop1 then <<
      if not iflab1 then c!:pgoto(jmptype, car where_to, depth)
      else i!:gopcode(jmptype, iflab1);
      if not iflab2 then c!:pgoto('jmp, cadr where_to, depth)
      else i!:gopcode(jmp, iflab2)
      >>
    else
      if not iflab2 then c!:pgoto(jmptype, cadr where_to, depth)
      else <<
        i!:gopcode(jmptype,iflab2);
        lab_end := c!:my_gensym();
        i!:gopcode(jmp,lab_end) >>;

    if iflab1 then <<
      i!:gopcode('!:,iflab1);
      c!:pgoto(jmptype, car where_to, depth) >>;
    if iflab2 then <<
      i!:gopcode('!:,iflab2);
      c!:pgoto(jmptype, cadr where_to, depth) >>;
    if lab_end then i!:gopcode('!:,lab_end);

    if atom car where_to then c!:display_flowgraph(car where_to, depth, drop1);
    if atom cadr where_to then c!:display_flowgraph(cadr where_to, depth, nil)
  end;

%-----------------------------------------------------------------------------

%    There are certain conventions about locations of some variables:
% 1. I assume the address of current stack top is residing in ebx permanently;
%    *OOGGGUMPHHH*. On Linux ebx is perserved across procedure calls and so
%    this use of it as a "register variable" is OK, but on Watcom it gets
%    used in some procedure calls and potentially clobbered on any. Oh dear!
% 2. nil is always the first local variable of any function, thus it is referred
%    everywhere as [ebp-4]
% 3. env is always the first formal parameter of any function, thus it is
%    referred everywhere as [ebp+off_env]
% 4. nargs (if exists at all) is always the second formal parameter of any
%    function, thus it is referred everywhere as [ebp+off_nargs]

symbolic procedure c!:pmovr(op, r1, r2, r3, depth);
 <<

   if flagp(r3, 'c!:live_across_call) then
     i!:gopcode(mov, eax, {ebx,-4*get(r3, 'c!:location)})
   else i!:gopcode(mov, eax, r3);
   if flagp(r1, 'c!:live_across_call) then
     i!:gopcode(mov, {ebx,-4*get(r1, 'c!:location)},eax)
   else i!:gopcode(mov, r1, eax)
 >>;

put('movr, ' c!:opcode_printer, function c!:pmovr);

symbolic procedure c!:pld_eltenv(elno);
 <<
   % #define elt(v, n)  (*(Lisp_Object *)((char *)(v)-2+(((int32_t)(n))<<2)))

   i!:gopcode(mov, edx,{ebp,off_env});
   i!:gopcode(mov, eax,{edx,4*elno-2})
 >>;

symbolic procedure c!:pst_eltenv(elno);
 <<
   i!:gopcode(mov, edx,{ebp,off_env});
   i!:gopcode(mov, {edx,4*elno-2},eax)
 >>;

symbolic procedure c!:pld_qvaleltenv(elno);
 <<
   % #define qvalue(p)      (*(Lisp_Object *)(p))

   c!:pld_eltenv(elno);
   i!:gopcode(mov, eax, {eax});
 >>;

symbolic procedure c!:pst_qvaleltenv(elno);
 <<
   i!:gopcode(mov, edx,{ebp,off_env});
   i!:gopcode(mov, ecx,{edx,4*elno-2});
   i!:gopcode(mov, {ecx},eax);
 >>;

symbolic procedure c!:pmovk(op, r1, r2, r3, depth);
 <<

   c!:pld_eltenv(r3);
   i!:gopcode(mov, r1,eax)
 >>;

put('movk, 'c!:opcode_printer, function c!:pmovk);

symbolic procedure c!:pmovk1(op, r1, r2, r3, depth);
   if null r3 then <<
     i!:gopcode(mov, eax, {ebp,-4});
     i!:gopcode(mov, r1, eax)
     >>
   else if r3 = 't then <<
     i!:gopcode(mov, eax, 'lisp_true);
     i!:gopcode(mov, r1, eax)
     >>
   else <<
     i!:gopcode(mov, eax, 16*r3+1);
     i!:gopcode(mov, r1, eax)
     >>;

put('movk1, 'c!:opcode_printer, function c!:pmovk1);

procedure c!:preloadenv(op, r1, r2, r3, depth);
% will not be encountered unless reloadenv variable has been set up.
 <<
   i!:gopcode(mov, ecx,{ebx,-reloadenv*4});
   i!:gopcode(mov, {ebp,off_env},ecx)
 >>;

put('reloadenv, 'c!:opcode_printer, function c!:preloadenv);

symbolic procedure c!:pldrglob(op, r1, r2, r3, depth);
 <<
   c!:pld_qvaleltenv(r3);
   i!:gopcode(mov, r1,eax)
 >>;

put('ldrglob, 'c!:opcode_printer, function c!:pldrglob);

symbolic procedure c!:pstrglob(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov, eax,r1);
   c!:pst_qvaleltenv(r3)
 >>;

put('strglob, 'c!:opcode_printer, function c!:pstrglob);

symbolic procedure c!:pnilglob(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov, eax, {ebp,-4});
   c!:pst_qvaleltenv(r3)
 >>;

put('nilglob, 'c!:opcode_printer, function c!:pnilglob);

symbolic procedure c!:pgentornil(condtype, dest);
 begin
   scalar condjmp, lab1, lab2;

   if condtype = 'eq then condjmp := 'jne
   else if condtype = 'neq then condjmp := 'je
   else if condtype = '< then condjmp := 'jge
   else if condtype = '> then condjmp := 'jle;
   lab1 := c!:my_gensym();
   lab2 := c!:my_gensym();
   i!:gopcode(condjmp, lab1);
   i!:gopcode(mov,eax,'lisp_true, jmp,lab2);
   i!:gopcode('!:,lab1, mov,eax,{ebp,-4});
   i!:gopcode('!:,lab2, mov,dest,eax)
 end;


symbolic procedure c!:pnull(op, r1, r2, r3, depth);
 <<

   i!:gopcode(mov,eax,r3);
   i!:gopcode(cmp,eax,{ebp,-4});
   c!:pgentornil('eq, r1)
 >>;


put('null, 'c!:opcode_printer, function c!:pnull);
put('not,  'c!:opcode_printer, function c!:pnull);

symbolic procedure c!:pfastget(op, r1, r2, r3, depth);
 begin
   scalar lab1,lab_end;

   lab1 := c!:my_gensym(); lab_end := c!:my_gensym();

   i!:gopcode(mov,eax,r2);
   i!:gopcode(and,eax,TAG_BITS, cmp,eax,TAG_SYMBOL, je,lab1);
   i!:gopcode(mov,eax,{ebp,-4}, jmp,lab_end);
   i!:gopcode('!:,lab1);
   i!:gopcode(mov,eax,r2, mov,eax,{eax,28}, cmp,eax,{ebp,-4}, je,lab_end);
   i!:gopcode(mov,eax,{eax,4*(car r3)-2});

   i!:gopcode(cmp,eax,SPID_NOPROP, jne,lab_end, mov,eax,{ebp,-4});
   i!:gopcode('!:,lab_end, mov,r1,eax)
  end;

put('fastget, 'c!:opcode_printer, function c!:pfastget);
flag('(fastget), 'c!:uses_nil);

symbolic procedure c!:pfastflag(op, r1, r2, r3, depth);
 begin
   scalar lab1, lab2, lab_end;


   lab1 := c!:my_gensym(); lab2 := c!:my_gensym(); lab_end := c!:my_gensym();

   i!:gopcode(mov,eax,r2);
   i!:gopcode(and,eax,TAG_BITS, cmp,eax,TAG_SYMBOL, je,lab1);
   i!:gopcode(mov,eax,{ebp,-4}, jmp,lab_end);
   i!:gopcode('!:,lab1);
   i!:gopcode(mov,eax,r2, mov,eax,{eax,28}, cmp,eax,{ebp,-4}, je,lab_end);
   i!:gopcode(mov,eax,{eax,4*(car r3)-2});

   i!:gopcode(cmp,eax,SPID_NOPROP, je,lab2, mov,eax,'lisp_true, jmp,lab_end);
   i!:gopcode('!:,lab2, mov,eax,{ebp,-4});
   i!:gopcode('!:,lab_end, mov,r1,eax)
 end;

put('fastflag, 'c!:opcode_printer, function c!:pfastflag);
flag('(fastflag), 'c!:uses_nil);

symbolic procedure c!:pcar(op, r1, r2, r3, depth);
 begin
   if not !*unsafecar then <<
     c!:pgoto(nil, c!:find_error_label(list('car, r3), r2, depth), depth);

     % #define car_legal(p) is_cons(p)
     % #define is_cons(p)   ((((int)(p)) & TAG_BITS) == TAG_CONS)
     % TAG_CONS = 0
     i!:gopcode(mov,eax,r3, test,eax,TAG_BITS);
     c!:pgoto('jne, c!:find_error_label(list('car, r3), r2, depth), depth)
     >>;

   c!:pqcar(op, r1, r2, r3, depth)
 end;

put('car, 'c!:opcode_printer, function c!:pcar);

symbolic procedure c!:pcdr(op, r1, r2, r3, depth);
 begin
   if not !*unsafecar then <<
     c!:pgoto(nil, c!:find_error_label(list('cdr, r3), r2, depth), depth);

     i!:gopcode(mov,eax,r3, test,eax,TAG_BITS);
     c!:pgoto('jne, c!:find_error_label(list('cdr, r3), r2, depth), depth)
     >>;

   c!:pqcdr(op, r1, r2, r3, depth)
 end;

put('cdr, 'c!:opcode_printer, function c!:pcdr);

symbolic procedure c!:pqcar(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r3);
   i!:gopcode(mov,eax,{eax}, mov,r1,eax)
 >>;

put('qcar, 'c!:opcode_printer, function c!:pqcar);

symbolic procedure c!:pqcdr(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r3);
   i!:gopcode(mov,eax,{eax,4}, mov,r1,eax)
 >>;

put('qcdr, 'c!:opcode_printer, function c!:pqcdr);

symbolic procedure c!:patom(op, r1, r2, r3, depth);
 <<

   i!:gopcode(mov,eax,r3, test,eax,TAG_BITS);
   c!:pgentornil('neq, r1);
 >>;

put('atom, 'c!:opcode_printer, function c!:patom);

symbolic procedure c!:pnumberp(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r3, test,eax,1);
   c!:pgentornil('neq, r1)
 >>;

put('numberp, 'c!:opcode_printer, function c!:pnumberp);

symbolic procedure c!:pfixp(op, r1, r2, r3, depth);
 <<
   c!:pgencall('integerp, {"nil",r3}, r1)
 >>;

put('fixp, 'c!:opcode_printer, function c!:pfixp);

symbolic procedure c!:piminusp(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r3, test,eax,eax);
   c!:pgentornil('<, r1)
 >>;

put('iminusp, 'c!:opcode_printer, function c!:piminusp);

symbolic procedure c!:pilessp(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, cmp,eax,r3);
   c!:pgentornil('<, r1)
 >>;

put('ilessp, 'c!:opcode_printer, function c!:pilessp);

symbolic procedure c!:pigreaterp(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, cmp,eax,r3);
   c!:pgentornil('>, r1)
 >>;

put('igreaterp, 'c!:opcode_printer, function c!:pigreaterp);

symbolic procedure c!:piminus(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,2, sub,eax,r3);
   i!:gopcode(mov, r1, eax)
 >>;

put('iminus, 'c!:opcode_printer, function c!:piminus);

symbolic procedure c!:piadd1(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov, eax, r3);
   i!:gopcode(add,eax,0x10, mov,r1,eax)
 >>;

put('iadd1, 'c!:opcode_printer, function c!:piadd1);

symbolic procedure c!:pisub1(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov, eax, r3);
   i!:gopcode(sub,eax,0x10, mov,r1,eax)
 >>;

put('isub1, 'c!:opcode_printer, function c!:pisub1);

symbolic procedure c!:piplus2(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, add,eax,r3);
   i!:gopcode(sub,eax,TAG_FIXNUM, mov,r1,eax)
 >>;

put('iplus2, 'c!:opcode_printer, function c!:piplus2);

symbolic procedure c!:pidifference(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, sub,eax,r3);
   i!:gopcode(add,eax,TAG_FIXNUM, mov,r1,eax)
 >>;

put('idifference, 'c!:opcode_printer, function c!:pidifference);

symbolic procedure c!:pitimes2(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, shr,eax,4);
   i!:gopcode(mov,edx,r3, shr,edx,4);
   i!:gopcode(mul,eax,edx, shl,eax,4, add,eax,TAG_FIXNUM);
   i!:gopcode(mov, r1, eax);
 >>;

put('itimes2, 'c!:opcode_printer, function c!:pitimes2);

symbolic procedure c!:pmodular_plus(op, r1, r2, r3, depth);
 begin
   scalar lab1;

   lab1 := c!:my_gensym();
   i!:gopcode(mov,eax,r2, shr,eax,4);
   i!:gopcode(mov,edx,r3, shr,edx,4);
   i!:gopcode(add,eax,edx, cmp,eax,'current_modulus, jl,lab1);
   i!:gopcode(sub, eax, 'current_modulus);
   i!:gopcode('!:,lab1, shl,eax,4, add,eax,TAG_FIXNUM, mov,r1,eax)
 end;

put('modular!-plus, 'c!:opcode_printer, function c!:pmodular_plus);

symbolic procedure c!:pmodular_difference(op, r1, r2, r3, depth);
 begin
   scalar lab1;

   lab1 := c!:my_gensym();
   i!:gopcode(mov,eax,r2, shr,eax,4);
   i!:gopcode(mov,edx,r3, shr,edx,4);
   i!:gopcode(sub,eax,edx, test,eax,eax, jge,lab1);
   i!:gopcode(add,eax,'current_modulus);
   i!:gopcode('!:,lab1, shl,eax,4, add,eax,TAG_FIXNUM, mov,r1,eax)
 end;

put('modular!-difference, 'c!:opcode_printer, function c!:pmodular_difference);

symbolic procedure c!:pmodular_minus(op, r1, r2, r3, depth);
 begin
   scalar lab1;

   lab1 := c!:my_gensym();
   i!:gopcode(mov,eax,r3, shr,eax,4);
   i!:gopcode(test,eax,eax, je,lab1);
   i!:gopcode(sub,eax,'current_modulus, neg,eax);
   i!:gopcode('!:,lab1, shl,eax,4, add,eax,TAG_FIXNUM, mov,r1,eax)
 end;

put('modular!-minus, 'c!:opcode_printer, function c!:pmodular_minus);

!#if (not common!-lisp!-mode)

symbolic procedure c!:passoc(op, r1, r2, r3, depth);
 <<
   c!:pgencall('assoc, list("nil", r2, r3), r1)
 >>;

put('assoc, 'c!:opcode_printer, function c!:passoc);
flag('(assoc), 'c!:uses_nil);

!#endif

symbolic procedure c!:patsoc(op, r1, r2, r3, depth);
 <<
   c!:pgencall('atsoc, list("nil", r2, r3), r1)
 >>;

put('atsoc, 'c!:opcode_printer, function c!:patsoc);
flag('(atsoc), 'c!:uses_nil);

!#if (not common!-lisp!-mode)

symbolic procedure c!:pmember(op, r1, r2, r3, depth);
 <<
   c!:pgencall('member, {"nil", r2, r3}, r1)
 >>;

put('member, 'c!:opcode_printer, function c!:pmember);
flag('(member), 'c!:uses_nil);

!#endif

symbolic procedure c!:pmemq(op, r1, r2, r3, depth);
 <<
   c!:pgencall('memq, {"nil", r2, r3}, r1)
 >>;

put('memq, 'c!:opcode_printer, function c!:pmemq);
flag('(memq), 'c!:uses_nil);

!#if common!-lisp!-mode

symbolic procedure c!:pget(op, r1, r2, r3, depth);
 <<
   c!:pgencall('get, {r2, r3, "nil"}, r1);
 >>;

flag('(get), 'c!:uses_nil);
!#else

symbolic procedure c!:pget(op, r1, r2, r3, depth);
 <<
   c!:pgencall('get, list(r2, r3), r1);
 >>;

!#endif

put('get, 'c!:opcode_printer, function c!:pget);

symbolic procedure c!:pgetv(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, sub,eax,2);
   i!:gopcode(mov,edx,r3, shr,edx,2, add,eax,edx);
   i!:gopcode(mov,eax,{eax}, mov,r1,eax)
 >>;

put('getv, 'c!:opcode_printer, function c!:pgetv);

symbolic procedure c!:pqputv(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, sub,eax,2);
   i!:gopcode(mov,edx,r3, shr,edx,2, add,edx,eax);
   i!:gopcode(mov,eax,r1, mov,{edx},eax)
 >>;

put('qputv, 'c!:opcode_printer, function c!:pqputv);

symbolic procedure c!:peq(op, r1, r2, r3, depth);
 <<
   i!:gopcode(mov,eax,r2, cmp,eax,r3);
   c!:pgentornil('eq, r1)
 >>;

put('eq, 'c!:opcode_printer, function c!:peq);
flag('(eq), 'c!:uses_nil);


symbolic procedure c!:pgenpequal(fname, args, res);
 begin
   scalar jmpinstr, lab1, lab2;
   jmpinstr := c!:pgenequal(fname, args, nil);
   % Jump instruction is issued for the case the condition is true
   lab1 := c!:my_gensym();
   lab2 := c!:my_gensym();
   i!:gopcode(jmpinstr, lab1);
   i!:gopcode(mov,eax,{ebp,-4}, jmp,lab2);
   i!:gopcode('!:,lab1, mov,eax,'lisp_true);
   i!:gopcode('!:,lab2, mov,res,eax)
 end;

!#if common!-lisp!-mode
symbolic procedure c!:pequal(op, r1, r2, r3, depth);
 <<
   c!:pgenpequal('cl_equal_fn, list(r2, r3), r1);
 >>;
!#else
symbolic procedure c!:pequal(op, r1, r2, r3, depth);
 begin
   c!:pgenpequal('equal_fn, list(r2, r3), r1)
 end;
!#endif

put('equal, 'c!:opcode_printer, function c!:pequal);
flag('(equal), 'c!:uses_nil);

symbolic procedure c!:pfluidbind(op, r1, r2, r3, depth);
   nil;

put('fluidbind, 'c!:opcode_printer, function c!:pfluidbind);


symbolic procedure c!:pgencall(addr, arglist, dest);
% Generate a call sequence.
 begin
   scalar reg, nargs, c_dir, pops;

   if not (reg := get(addr,'i!:regcode)) then <<
     nargs := length arglist;
     if not atom car arglist then <<
       % We encode (nil, actual no of args) or (env, actual no of args) this way
       nargs := cadar arglist;
       car arglist := caar arglist;
       >>
     else if (car arglist = 'env) or (car arglist = "nil") then
       nargs := nargs - 1
     else <<
       % This is a direct C entrypoint or direct C predicate or one of special
       % functions: reclaim, error, equal_fn, aerror which behave the same
       % and for which we don't need to pass the number of args.
       if (c_dir := get(addr, 'c!:direct_call_func)) then nargs := nil >>
     >>;

% The next line is a HORRID fudge to keep ebx safe when it was going to be
% used by the calling standard. Ugh
   if i_machine = 2 and length arglist = 3 then i!:gopcode(push,ebx);

% I have to reverse the order of parameters, since we use C call model
   for each a in reverse arglist do i!:gopcode(push, a);
   pops := 4*length arglist;
% Here I adapt (CRUDELY) for possibly different calling mechanisms
print list(i_machine, pops, 'call);
   if i_machine = 2 and (pops = 8 or nargs = 12) then <<
      i!:gopcode(pop,eax, pop,edx); pops := pops-8;
      if pops = 4 then << i!:gopcode(pop,ebx); pops := pops-4 >> >>
   else if i_machine = 3 and (pops = 8 or pops = 12) then <<
      i!:gopcode(pop,ecx, pop,edx); pops := pops-8 >>;
   if reg then i!:gopcode(call, addr)
   else <<
      i_putcomment list('call, addr, list nargs, c_dir);
      i_putbyte 0xe8;
      if c_dir then i_putextern list('rel_plus_4, c_dir)
      else i_putextern list('rel_plus_4, list(addr, nargs)) >>;
   if pops neq 0 then i!:gopcode(add, esp, pops);

% The next line is a HORRID fudge to keep ebx safe when it was going to be
% used by the calling standard. Ugh
   if i_machine = 2 and length arglist = 3 then i!:gopcode(pop,ebx);
   if dest neq nil then i!:gopcode(mov,dest,eax);
 end;

symbolic procedure c!:pcall(op, r1, r2, r3, depth);
 begin
 % r3 is (name <fluids to unbind on error>)
   scalar w, boolfn, nargs, lab1;

%--     if car r3 = current_procedure then <<
%--        nargs := length r2;
%--        if null r2 or nargs >= 3 then <<
%--          r2 := cons(nargs, r2);
%--          r2 := cons({'env, nargs}, r2) >>
%--        else r2 := cons('env, r2);
%--        c!:pgencall(car r3, r2, r1)
%--        >>

    begin
       nargs := length r2;
       c!:pld_eltenv(c!:find_literal car r3);

       % Compute qenv(fn) and put into edx
       i!:gopcode(mov,edx,{eax,4});

       r2 := cons('edx, r2);
       if nargs = 1 then i!:gopcode(mov,ecx,{eax,8})
       else if nargs = 2 then i!:gopcode(mov,ecx,{eax,12})
       else <<
         i!:gopcode(mov,ecx,{eax,16});
         r2 := car r2 . nargs . cdr r2
         >>;
       c!:pgencall('ecx, r2, r1)
       end;

    if not flagp(car r3, 'c!:no_errors) then <<
       if null cadr r3 and depth = 0 then <<

         lab1 := c!:my_gensym();
         i!:gopcode(mov,eax,'C_nil, mov,{ebp,-4},eax);
         i!:gopcode(and,eax,1, je,lab1);
         i!:gopcode(mov,eax,{ebp,-4}, jmp,lab_end_proc);
         i!:gopcode('!:,lab1)
         >>
       else <<
         i!:gopcode(mov,eax,'C_nil, mov,{ebp,-4},eax);

         c!:pgoto(nil, c!:find_error_label(nil, cadr r3, depth), depth);

         i!:gopcode(and,eax,1);
         c!:pgoto('jne, c!:find_error_label(nil, cadr r3, depth), depth)
         >>
       >>;

    if boolfn then <<

      i!:gopcode(mov,eax,r1, test,eax,eax);
      c!:pgentornil('neq, r1)
      >>
  end;

put('call, 'c!:opcode_printer, function c!:pcall);


symbolic procedure c!:ppopv(depth);
 <<
  i!:gopcode(sub,ebx,depth*4, mov,'stack,ebx)
 >>;

symbolic procedure c!:pgoto(jmptype, lab, depth);
 begin
  if atom lab then <<
    if jmptype neq nil then   %! when test sup removed nil test not required
      return i!:gopcode(jmptype, lab)
    else return nil
    >>;
  lab := get(car lab, 'c!:chosen);
  if zerop depth then <<
    i!:gopcode(mov,eax,lab, jmp,lab_end_proc)
    >>
  else if flagp(lab, 'c!:live_across_call) then <<
    i!:gopcode(mov, eax, {ebx, -get(lab, 'c!:location)*4});
    c!:ppopv(depth);
    i!:gopcode(jmp,lab_end_proc)
    >>
  else <<
    c!:ppopv(depth);
    i!:gopcode(mov,eax,lab, jmp,lab_end_proc)
    >>
end;

symbolic procedure c!:pifnull(s, negate);
  <<
    i!:gopcode(mov, eax, car s);
    i!:gopcode(cmp, eax, {ebp,-4});
    if negate then 'jne
    else 'je
    >>;

put('ifnull, 'c!:exit_helper, function c!:pifnull);

symbolic procedure c!:pifatom(s, negate);
  <<
    i!:gopcode(mov,eax,car s, test,eax,TAG_BITS);
    if negate then 'je
    else 'jne
    >>;

put('ifatom, 'c!:exit_helper, function c!:pifatom);

symbolic procedure c!:pifsymbol(s, negate);
  <<
    i!:gopcode(mov, eax, car s);
    i!:gopcode(and,eax,TAG_BITS, cmp,eax,TAG_SYMBOL);
    if negate then 'jne
    else 'je
    >>;

put('ifsymbol, 'c!:exit_helper, function c!:pifsymbol);

symbolic procedure c!:pifnumber(s, negate);
  <<
    i!:gopcode(mov,eax,car s, test,eax,1);
    if negate then 'je
    else 'jne
    >>;

put('ifnumber, 'c!:exit_helper, function c!:pifnumber);

symbolic procedure c!:pifizerop(s, negate);
 <<
    i!:gopcode(mov,eax,car s, cmp,eax,1);
    if negate then 'jne
    else 'je
    >>;

put('ifizerop, 'c!:exit_helper, function c!:pifizerop);

symbolic procedure c!:pifeq(s, negate);
 <<
    i!:gopcode(mov,eax,car s, cmp,eax,cadr s);
    if negate then 'jne
    else 'je
    >>;

put('ifeq, 'c!:exit_helper, function c!:pifeq);

symbolic procedure c!:pgenequal(fname, args, negate);
% Perform the evaluation of the macro below, and issue a cond jump command so
% that jump is performed if the condition is satisfied. fname should be
% either equal_fn or cl_equal_fn, and this parameter is required only
% because of my desire to support both SL and CL at least here
 begin
  scalar lab_ok, lab_fail, lab_end;
  % #define equal(a, b)                                \
  %     ((a) == (b) ||                                 \
  %      (((((a) ^ (b)) & TAG_BITS) == 0) &&           \
  %       ((unsigned)(((a) & TAG_BITS) - 1) > 3) &&    \
  %       equal_fn(a, b)))

  lab_ok := c!:my_gensym(); lab_fail := c!:my_gensym(); lab_end := c!:my_gensym();
  i!:gopcode(mov, ecx,car args);
  i!:gopcode(mov, edx,cadr args);
  i!:gopcode(cmp,ecx,edx, je,lab_ok);
  i!:gopcode(mov,eax,ecx, xor,eax,edx, test,eax,7, jne,lab_fail);
  i!:gopcode(mov,eax,ecx, and,eax,7, dec,eax);
  i!:gopcode(cmp,eax,3, jbe,lab_fail);
  c!:pgencall(fname,{'ecx,'edx},nil);
  i!:gopcode(test,eax,eax, jne,lab_ok);
  i!:gopcode('!:,lab_fail, xor,eax,eax, jmp,lab_end);
  i!:gopcode('!:,lab_ok, mov,eax,1);
  i!:gopcode('!:,lab_end, test,eax,eax);
  if negate then return 'je
  else return 'jne
 end;

!#if common!-lisp!-mode
symbolic procedure c!:pifequal(s, negate);
  c!:pgenequal('cl_equal_fn, s, negate);
!#else
symbolic procedure c!:pifequal(s, negate);
  c!:pgenequal('equal_fn, s, negate);
!#endif

put('ifequal, 'c!:exit_helper, function c!:pifequal);

symbolic procedure c!:pifilessp(s, negate);
  <<
    i!:gopcode(mov,eax,car s, cmp,eax,cadr s);
    if negate then 'jge
    else 'jl >>;

put('ifilessp, 'c!:exit_helper, function c!:pifilessp);

symbolic procedure c!:pifigreaterp(s, negate);
  <<
    i!:gopcode(mov,eax,car s, cmp,eax,cadr s);
    if negate then 'jle
    else 'jg >>;

put('ifigreaterp, 'c!:exit_helper, function c!:pifigreaterp);

%------------------------------------------------------------------------------

symbolic procedure c!:display_flowgraph(s, depth, dropping_through);
  if not atom s then <<
    c!:pgoto(nil, s, depth) >>
  else if not flagp(s, 'c!:visited) then begin
    scalar why, where_to;
    flag(list s, 'c!:visited);
    if not dropping_through or not (get(s, 'c!:count) = 1) then
        i!:gopcode('!:, s);
    for each k in reverse get(s, 'c!:contents) do c!:print_opcode(k, depth);
    why := get(s, 'c!:why);
    where_to := get(s, 'c!:where_to);
    if why = 'goto and (not atom car where_to or
                        (not flagp(car where_to, 'c!:visited) and
                         get(car where_to, 'c!:count) = 1)) then
       c!:display_flowgraph(car where_to, depth, t)
    else c!:print_exit_condition(why, where_to, depth)
  end;

fluid '(startpoint);

symbolic procedure c!:branch_chain(s, count);
  begin
    scalar contents, why, where_to, n;
% do nothing to blocks already visted or return blocks.
    if not atom s then return s
    else if flagp(s, 'c!:visited) then <<
       n := get(s, 'c!:count);
       if null n then n := 1 else n := n + 1;
       put(s, 'c!:count, n);
       return s >>;
    flag(list s, 'c!:visited);
    contents := get(s, 'c!:contents);
    why := get(s, 'c!:why);
    where_to := for each z in get(s, 'c!:where_to) collect
                    c!:branch_chain(z, count);
% Turn movr a,b; return a; into return b;
    while contents and eqcar(car contents, 'movr) and
        why = 'goto and not atom car where_to and
        caar where_to = cadr car contents do <<
      where_to := list list cadddr car contents;
      contents := cdr contents >>;
    put(s, 'c!:contents, contents);
    put(s, 'c!:where_to, where_to);
% discard empty blocks
    if null contents and why = 'goto then <<
       remflag(list s, 'c!:visited);
       return car where_to >>;
    if count then <<
      n := get(s, 'c!:count);
      if null n then n := 1
      else n := n + 1;
      put(s, 'c!:count, n) >>;
    return s
  end;

symbolic procedure c!:one_operand op;
 << flag(list op, 'c!:set_r1);
    flag(list op, 'c!:read_r3);
    put(op, 'c!:code, function c!:builtin_one) >>;

symbolic procedure c!:two_operands op;
 << flag(list op, 'c!:set_r1);
    flag(list op, 'c!:read_r2);
    flag(list op, 'c!:read_r3);
    put(op, 'c!:code, function c!:builtin_two) >>;

for each n in '(car cdr qcar qcdr null not atom numberp fixp iminusp
                iminus iadd1 isub1 modular!-minus) do c!:one_operand n;
!#if common!-lisp!-mode
for each n in '(eq equal atsoc memq iplus2 idifference
                itimes2 ilessp igreaterp getv get
                modular!-plus modular!-difference
                ) do c!:two_operands n;
!#else
for each n in '(eq equal atsoc memq iplus2 idifference
                assoc member
                itimes2 ilessp igreaterp getv get
                modular!-plus modular!-difference
                ) do c!:two_operands n;
!#endif


flag('(movr movk movk1 ldrglob call reloadenv fastget fastflag), 'c!:set_r1);
flag('(strglob qputv), 'c!:read_r1);
flag('(qputv fastget fastflag), 'c!:read_r2);
flag('(movr qputv), 'c!:read_r3);
flag('(ldrglob strglob nilglob movk call), 'c!:read_env);
% special opcodes:
%   call fluidbind

fluid '(fn_used nil_used nilbase_used);

symbolic procedure c!:live_variable_analysis all_blocks;
  begin
    scalar changed, z;
    repeat <<
      changed := nil;
      for each b in all_blocks do
        begin
          scalar w, live;
          for each x in get(b, 'c!:where_to) do
             if atom x then live := union(live, get(x, 'c!:live))
             else live := union(live, x);
          w := get(b, 'c!:why);
          if not atom w then <<
             if eqcar(w, 'ifnull) or eqcar(w, 'ifequal) then nil_used := t;
             live := union(live, cdr w);
             if eqcar(car w, 'call) and
                not (cadar w = current_procedure) then <<
                    fn_used := t; live := union('(env), live) >> >>;
          for each s in get(b, 'c!:contents) do
            begin % backwards over contents
              scalar op, r1, r2, r3;
              op := car s; r1 := cadr s; r2 := caddr s; r3 := cadddr s;
              if op = 'movk1 then <<
                  if r3 = nil then nil_used := t
                  else if r3 = 't then nilbase_used := t >>
              else if atom op and flagp(op, 'c!:uses_nil) then nil_used := t;
              if flagp(op, 'c!:set_r1) then
!#if common!-lisp!-mode
                 if memq(r1, live) then live := remove(r1, live)
!#else
                 if memq(r1, live) then live := delete(r1, live)
!#endif
                 else if op = 'call then nil % Always needed
                 else op := 'nop;
              if flagp(op, 'c!:read_r1) then live := union(live, list r1);
              if flagp(op, 'c!:read_r2) then live := union(live, list r2);
              if flagp(op, 'c!:read_r3) then live := union(live, list r3);
              if op = 'call then <<
                 if not flagp(car r3, 'c!:no_errors) then nil_used := t;
                 does_call := t;
                 fn_used := t;
                 if not flagp(car r3, 'c!:no_errors) then
                     flag(live, 'c!:live_across_call);
                 live := union(live, r2) >>;
              if flagp(op, 'c!:read_env) then live := union(live, '(env))
            end;
!#if common!-lisp!-mode
          live := append(live, nil); % because CL sort is destructive!
!#endif
          live := sort(live, function orderp);
          if not (live = get(b, 'c!:live)) then <<
            put(b, 'c!:live, live);
            changed := t >>
        end
    >> until not changed;
    z := registers;
    registers := stacklocs := nil;
    for each r in z do
       if flagp(r, 'c!:live_across_call) then stacklocs := r . stacklocs
       else registers := r . registers;
  end;

symbolic procedure c!:insert1(a, b);
  if memq(a, b) then b
  else a . b;

symbolic procedure c!:clash(a, b);
  if flagp(a, 'c!:live_across_call) = flagp(b, 'c!:live_across_call) then <<
    put(a, 'c!:clash, c!:insert1(b, get(a, 'c!:clash)));
    put(b, 'c!:clash, c!:insert1(a, get(b, 'c!:clash))) >>;

symbolic procedure c!:build_clash_matrix all_blocks;
  begin
    for each b in all_blocks do
      begin
        scalar live, w;
        for each x in get(b, 'c!:where_to) do
           if atom x then live := union(live, get(x, 'c!:live))
           else live := union(live, x);
        w := get(b, 'c!:why);
        if not atom w then <<
           live := union(live, cdr w);
           if eqcar(car w, 'call) then
              live := union('(env), live) >>;
        for each s in get(b, 'c!:contents) do
          begin
            scalar op, r1, r2, r3;
            op := car s; r1 := cadr s; r2 := caddr s; r3 := cadddr s;
            if flagp(op, 'c!:set_r1) then
               if memq(r1, live) then <<
!#if common!-lisp!-mode
                  live := remove(r1, live);
!#else
                  live := delete(r1, live);
!#endif
                  if op = 'reloadenv then reloadenv := t;
                  for each v in live do c!:clash(r1, v) >>
               else if op = 'call then nil
               else <<
                  op := 'nop;
                  rplacd(s, car s . cdr s); % Leaves original instrn visible
                  rplaca(s, op) >>;
            if flagp(op, 'c!:read_r1) then live := union(live, list r1);
            if flagp(op, 'c!:read_r2) then live := union(live, list r2);
            if flagp(op, 'c!:read_r3) then live := union(live, list r3);
% Maybe CALL should be a little more selective about need for "env"?
            if op = 'call then live := union(live, r2);
            if flagp(op, 'c!:read_env) then live := union(live, '(env))
          end
      end;
    return nil
  end;

symbolic procedure c!:allocate_registers rl;
  begin
    scalar schedule, neighbours, allocation;
    neighbours := 0;
    while rl do begin
      scalar w, x;
      w := rl;
      while w and length (x := get(car w, 'c!:clash)) > neighbours do
        w := cdr w;
      if w then <<
        schedule := car w . schedule;
        rl := deleq(car w, rl);
        for each r in x do put(r, 'c!:clash, deleq(car w, get(r, 'c!:clash))) >>
      else neighbours := neighbours + 1
    end;
    for each r in schedule do begin
      scalar poss;
      poss := allocation;
      for each x in get(r, 'c!:clash) do
        poss := deleq(get(x, 'c!:chosen), poss);
      if null poss then <<
         poss := c!:my_gensym();
         allocation := append(allocation, list poss) >>
      else poss := car poss;
      put(r, 'c!:chosen, poss)
    end;
    return allocation
  end;

symbolic procedure c!:remove_nops all_blocks;
% Remove no-operation instructions, and map registers to reflect allocation
  for each b in all_blocks do
    begin
      scalar r;
      for each s in get(b, 'c!:contents) do
        if not eqcar(s, 'nop) then
          begin
            scalar op, r1, r2, r3;
            op := car s; r1 := cadr s; r2 := caddr s; r3 := cadddr s;
            if flagp(op, 'c!:set_r1) or flagp(op, 'c!:read_r1) then
               r1 := get(r1, 'c!:chosen);
            if flagp(op, 'c!:read_r2) then r2 := get(r2, 'c!:chosen);
            if flagp(op, 'c!:read_r3) then r3 := get(r3, 'c!:chosen);
            if op = 'call then
               r2 := for each v in r2 collect get(v, 'c!:chosen);
            if not (op = 'movr and r1 = r3) then
               r := list(op, r1, r2, r3) . r
          end;
      put(b, 'c!:contents, reversip r);
      r := get(b, 'c!:why);
      if not atom r then
         put(b, 'c!:why,
                car r . for each v in cdr r collect get(v, 'c!:chosen))
    end;

fluid '(error_labels);

symbolic procedure c!:find_error_label(why, env, depth);
  begin
    scalar w, z;
    z := list(why, env, depth);
    w := assoc!*!*(z, error_labels);
    if null w then <<
       w := z . c!:my_gensym();
       error_labels := w . error_labels >>;
    return cdr w
  end;

symbolic procedure c!:assign(u, v, c);
  if flagp(u, 'fluid) then list('strglob, v, u, c!:find_literal u) . c
  else list('movr, u, nil, v) . c;

symbolic procedure c!:insert_tailcall b;
  begin
    scalar why, dest, contents, fcall, res, w;
    why := get(b, 'c!:why);
    dest := get(b, 'c!:where_to);
    contents := get(b, 'c!:contents);
    while contents and not eqcar(car contents, 'call) do <<
      w := car contents . w;
      contents := cdr contents >>;
    if null contents then return nil;
    fcall := car contents;
    contents := cdr contents;
    res := cadr fcall;
    while w do <<
      if eqcar(car w, 'reloadenv) then w := cdr w
      else if eqcar(car w, 'movr) and cadddr car w = res then <<
        res := cadr car w;
        w := cdr w >>
      else res := w := nil >>;
    if null res then return nil;
    if c!:does_return(res, why, dest) then
       if car cadddr fcall = current_procedure then <<
          for each p in pair(current_args, caddr fcall) do
             contents := c!:assign(car p, cdr p, contents);
          put(b, 'c!:contents, contents);
          put(b, 'c!:why, 'goto);
          put(b, 'c!:where_to, list restart_label) >>
       else <<
          nil_used := t;
          put(b, 'c!:contents, contents);
          put(b, 'c!:why, list('call, car cadddr fcall) . caddr fcall);
          put(b, 'c!:where_to, nil) >>
  end;

symbolic procedure c!:does_return(res, why, where_to);
  if not (why = 'goto) then nil
  else if not atom car where_to then res = caar where_to
  else begin
    scalar contents;
    where_to := car where_to;
    contents := reverse get(where_to, 'c!:contents);
    why := get(where_to, 'c!:why);
    where_to := get(where_to, 'c!:where_to);
    while contents do
      if eqcar(car contents, 'reloadenv) then contents := cdr contents
      else if eqcar(car contents, 'movr) and cadddr car contents = res then <<
        res := cadr car contents;
        contents := cdr contents >>
      else res := contents := nil;
    if null res then return nil
    else return c!:does_return(res, why, where_to)
  end;

symbolic procedure c!:pushpop(op, v);
  begin
    scalar n, w, instr, src, dest, addr,    v1,n1;

    if null v then return nil;
    n := length v;

    if op = 'push then <<
      instr := 'add;
      src := 'eax >>
    else <<
      instr := 'sub;
      dest := 'eax >>;

    addr := 0;
    for each x in v do <<
      if op = 'push then <<
        addr := addr + 4;
        dest := {'ebx, addr};
        i!:gopcode(mov, eax, x) >>
      else src := {'ebx, addr};
      i!:gopcode(mov, dest, src);
      if op = 'pop then <<
        i!:gopcode(mov, x,eax);
        addr := addr - 4 >>
      >>;

    i!:gopcode(add,ebx,addr, mov,'stack,ebx)
  end;

symbolic procedure c!:optimise_flowgraph(startpoint, all_blocks,
                                          env, argch, args);
  begin
    scalar w, n, locs, stacks, error_labels, fn_used, nil_used,
           nilbase_used, locsno, lab1, addr, lab_ok, stackoffs;

!#if common!-lisp!-mode
    nilbase_used := t;  % For onevalue(xxx) at least
!#endif
    for each b in all_blocks do c!:insert_tailcall b;
    startpoint := c!:branch_chain(startpoint, nil);
    remflag(all_blocks, 'c!:visited);
    c!:live_variable_analysis all_blocks;
    c!:build_clash_matrix all_blocks;
    if error_labels and env then reloadenv := t;
    for each u in env do
      for each v in env do c!:clash(cdr u, cdr v); % keep all args distinct
    locs := c!:allocate_registers registers;
    stacks := c!:allocate_registers stacklocs;
    flag(stacks, 'c!:live_across_call);
    c!:remove_nops all_blocks;
    startpoint := c!:branch_chain(startpoint, nil); % after tailcall insertion
    remflag(all_blocks, 'c!:visited);
    startpoint := c!:branch_chain(startpoint, t); % ... AGAIN to tidy up
    remflag(all_blocks, 'c!:visited);
    if does_call then nil_used := t;

    lab_end_proc := c!:my_gensym();
    locsno := 0;

    if nil_used then <<
      locsno := locsno + 1 >>;
    if locs then <<
      locsno := locsno + length(locs)
       >>;

    % In ASM code I don't use fn since it is well replaced by hardware register

    i!:gopcode(push,ebp, mov,ebp,esp);

    if locsno > 0 then <<
      i!:gopcode(sub,esp,locsno*4);
      stackoffs := 0;
      if nil_used then stackoffs := stackoffs - 4;
      for each v in locs do <<
        stackoffs := stackoffs - 4;
        put(v, 'i!:locoffs, stackoffs) >>
      >>;

    if nil_used then
      i!:gopcode(mov,eax,'C_nil, mov,{ebp,-4},eax);
    i!:gopcode(push,ebx, mov,ebx,'stack);

    %!! Has not been perfectly processed yet due to the string parameter
    % # define argcheck(var, n, msg) if ((var)!=(n)) return aerror(msg);
    if car argch = 0 or car argch >= 3 then <<
      lab_ok := c!:my_gensym();
      i!:gopcode(mov,eax,{ebp,off_nargs}, cmp,eax,car argch, je,lab_ok);
      c!:pgencall('aerror, {999}, nil);
      i!:gopcode(jmp,lab_end_proc);
      i!:gopcode('!:,lab_ok) >>;

% I will not do a stack check if I have a leaf procedure, and I hope
% that this policy will speed up code a bit.
    if does_call then <<

       lab1 := c!:my_gensym();
       i!:gopcode(cmp,ebx,'stacklimit, jl,lab1);
% This is slightly clumsy code to save all args on the stack across the
% call to reclaim(), but it is not executed often...
       c!:pushpop('push, args);


       %!! Has not been perfectly processed yet due to the string parameter
       c!:pgencall('reclaim, {'!.env,0,GC_STACK,0}, {'ebp,off_env});

       c!:pushpop('pop, reverse args);
       i!:gopcode(mov,eax,'C_nil, mov,{ebp,-4},eax);

       i!:gopcode(and,eax,1, je,lab1);
       i!:gopcode(mov,eax,{ebp,-4}, jmp,lab_end_proc);

       i!:gopcode('!:,lab1) >>;

    if reloadenv then <<
      i!:gopcode(mov,eax,{ebp,off_env}, add,ebx,4,
                 mov,{ebx},eax, mov,'stack,ebx) >>;
    n := 0;
    if stacks then <<

       for each v in stacks do <<
          put(v, 'c!:location, n);
          n := n+1 >>;

       stackoffs := 0;
       i!:gopcode(mov, eax,{ebp,-4});
       for each v in stacks do <<
         stackoffs := stackoffs + 4;
         i!:gopcode(mov, {ebx,stackoffs},eax) >>;
       i!:gopcode(add,ebx,stackoffs, mov,'stack,ebx) >>;
    if reloadenv then <<
       reloadenv := n;
       n := n + 1 >>;
    for each v in env do
      if flagp(cdr v, 'c!:live_across_call) then <<
        i!:gopcode(mov, eax,cdr v);
        i!:gopcode(mov, {ebx,-get(get(cdr v, 'c!:chosen), 'c!:location)*4},eax) >>
      else <<
        i!:gopcode(mov, eax,cdr v);
        i!:gopcode(mov, get(cdr v, 'c!:chosen),eax) >>;

    c!:display_flowgraph(startpoint, n, t);

    if error_labels then <<
       for each x in error_labels do <<
          i!:gopcode('!:, cdr x);
          c!:print_error_return(caar x, cadar x, caddar x) >> >>;
    remflag(all_blocks, 'c!:visited);

    i!:gopcode('!:,lab_end_proc);
    i!:gopcode(pop,ebx, mov,esp,ebp, pop,ebp);
    if retloc neq 0 then i!:gopcode(add,esp,4*retloc);
    i!:gopcode(ret);
  end;

symbolic procedure c!:print_error_return(why, env, depth);
  begin
    scalar args;

    if reloadenv and env then <<
       i!:gopcode(mov,eax,{ebx,-reloadenv*4}, mov,{ebp,off_env},eax)
       >>;
    if null why then <<
% One could imagine generating backtrace entries here...
       for each v in env do <<
         i!:gopcode(mov, eax,get(cdr v, 'c!:chosen));
         c!:pst_qvaleltenv(c!:find_literal car v) >>;

       if depth neq 0 then c!:ppopv(depth);

       i!:gopcode(mov,eax,{ebp,-4}, jmp,lab_end_proc)
       >>
    else if flagp(cadr why, 'c!:live_across_call) then <<
       i!:gopcode(push, {ebx,-get(cadr why, 'c!:location)*4});
       for each v in env do <<
          i!:gopcode(mov, eax,get(cdr v, 'c!:chosen));
          c!:pst_qvaleltenv(c!:find_literal car v)
          >>;
       if depth neq 0 then c!:ppopv(depth);
          if eqcar(why, 'car) then "err_bad_car"
          else if eqcar(why, 'cdr) then "err_bad_cdr"
          else error(0, list(why, "unknown_error"));

       %!! Has not been properly processed yet because of the string parameter
       args := list(1,
         if eqcar(why, 'car) then 0         % "err_bad_car"
         else if eqcar(why, 'cdr) then 0    % "err_bad_cdr"
         else 0,                            % error(0, list(why, "unknown_error"));
         cadr why);
       c!:pgencall('error, args, nil);
       i!:gopcode(jmp,lab_end_proc)
       >>
    else <<
       for each v in env do <<
          i!:gopcode(mov, eax, get(cdr v, 'c!:chosen));
          c!:pst_qvaleltenv(c!:find_literal car v)
          >>;
       if depth neq 0 then c!:ppopv(depth);

       %!! Has not been properly processed yet due to the string parameter
       args := list(1,
         if eqcar(why, 'car) then 0       % "err_bad_car"
         else if eqcar(why, 'cdr) then 0  % "err_bad_cdr"
         else 0,                          % error(0, list(why, "unknown_error"));
         cadr why);
       c!:pgencall('error, args, nil);
       i!:gopcode(jmp,lab_end_proc)
       >>
  end;


%
% Now I have a series of separable sections each of which gives a special
% recipe that implements or optimises compilation of some specific Lisp
% form.
%

symbolic procedure c!:cand(u, env);
  begin
    scalar w, r;
    w := reverse cdr u;
    if null w then return c!:cval(nil, env);
    r := list(list('t, car w));
    w := cdr w;
    for each z in w do
       r := list(list('null, z), nil) . r;
    r := 'cond . r;
    return c!:cval(r, env)
  end;
%--    scalar next, done, v, r;
%--    v := c!:newreg();
%--    done := c!:my_gensym();
%--    u := cdr u;
%--    while cdr u do <<
%--      next := c!:my_gensym();
%--      c!:outop('movr, v, nil, c!:cval(car u, env));
%--      u := cdr u;
%--      c!:endblock(list('ifnull, v), list(done, next));
%--      c!:startblock next >>;
%--    c!:outop('movr, v, nil, c!:cval(car u, env));
%--    c!:endblock('goto, list done);
%--    c!:startblock done;
%--    return v
%--  end;

put('and, 'c!:code, function c!:cand);

!#if common!-lisp!-mode

symbolic procedure c!:cblock(u, env);
  begin
    scalar progret, progexit, r;
    progret := c!:newreg();
    progexit := c!:my_gensym();
    blockstack := (cadr u . progret . progexit) . blockstack;
    u := cddr u;
    for each a in u do r := c!:cval(a, env);
    c!:outop('movr, progret, nil, r);
    c!:endblock('goto, list progexit);
    c!:startblock progexit;
    blockstack := cdr blockstack;
    return progret
  end;


put('block, 'c!:code, function c!:cblock);

!#endif

symbolic procedure c!:ccatch(u, env);
   error(0, "catch");

put('catch, 'c!:code, function c!:ccatch);

symbolic procedure c!:ccompile_let(u, env);
   error(0, "compiler-let");

put('compiler!-let, 'c!:code, function c!:ccompiler_let);

symbolic procedure c!:ccond(u, env);
  begin
    scalar v, join;
    v := c!:newreg();
    join := c!:my_gensym();
    for each c in cdr u do begin
      scalar l1, l2;
      l1 := c!:my_gensym(); l2 := c!:my_gensym();
      if atom cdr c then <<
         c!:outop('movr, v, nil, c!:cval(car c, env));
         c!:endblock(list('ifnull, v), list(l2, join)) >>
      else <<
         c!:cjumpif(car c, env, l1, l2);
         c!:startblock l1;    % if the condition is true
         c!:outop('movr, v, nil, c!:cval('progn . cdr c, env));
         c!:endblock('goto, list join) >>;
      c!:startblock l2 end;
    c!:outop('movk1, v, nil, nil);
    c!:endblock('goto, list join);
    c!:startblock join;
    return v
  end;

put('cond, 'c!:code, function c!:ccond);

symbolic procedure c!:cdeclare(u, env);
   error(0, "declare");

put('declare, 'c!:code, function c!:cdeclare);

symbolic procedure c!:cde(u, env);
   error(0, "de");

put('de, 'c!:code, function c!:cde);

symbolic procedure c!:cdefun(u, env);
   error(0, "defun");

put('!~defun, 'c!:code, function c!:cdefun);

symbolic procedure c!:ceval_when(u, env);
   error(0, "eval-when");

put('eval!-when, 'c!:code, function c!:ceval_when);

symbolic procedure c!:cflet(u, env);
   error(0, "flet");

put('flet, 'c!:code, function c!:cflet);


symbolic procedure c!:cfunction(u, env);
  begin
    scalar v;
    u := cadr u;
    if not atom u then error(0, "function/funarg needed");
    v := c!:newreg();
    c!:outop('movk, v, u, c!:find_literal u);
    return v
  end;

put('function, 'c!:code, function c!:cfunction);

symbolic procedure c!:cgo(u, env);
  begin
    scalar w, w1;
    w1 := proglabs;
    while null w and w1 do <<
       w := assoc!*!*(cadr u, car w1);
       w1 := cdr w1 >>;
    if null w then error(0, list(u, "label not set"));
    c!:endblock('goto, list cadr w);
    return nil      % value should not be used
  end;

put('go, 'c!:code, function c!:cgo);

symbolic procedure c!:cif(u, env);
  begin
    scalar v, join, l1, l2;
    v := c!:newreg();
    join := c!:my_gensym();
    l1 := c!:my_gensym();
    l2 := c!:my_gensym();
    c!:cjumpif(cadr u, env, l1, l2);
    c!:startblock l1;
    c!:outop('movr, v, nil, c!:cval(car (u := cddr u), env));
    c!:endblock('goto, list join);
    c!:startblock l2;
    c!:outop('movr, v, nil, c!:cval(cadr u, env));
    c!:endblock('goto, list join);
    c!:startblock join;
    return v
  end;

put('if, 'c!:code, function c!:cif);

symbolic procedure c!:clabels(u, env);
   error(0, "labels");

put('labels, 'c!:code, function c!:clabels);

symbolic procedure c!:expand!-let(vl, b);
  if null vl then 'progn . b
  else if null cdr vl then c!:expand!-let!*(vl, b)
  else begin scalar vars, vals;
    for each v in vl do
      if atom v then << vars := v . vars; vals := nil . vals >>
      else if atom cdr v then << vars := car v . vars; vals := nil . vals >>
      else << vars := car v . vars; vals := cadr v . vals >>;
    return ('lambda . vars . b) . vals
  end;

symbolic procedure c!:clet(x, env);
   c!:cval(c!:expand!-let(cadr x, cddr x), env);

!#if common!-lisp!-mode
put('let, 'c!:code, function c!:clet);
!#else
put('!~let, 'c!:code, function c!:clet);
!#endif

symbolic procedure c!:expand!-let!*(vl, b);
  if null vl then 'progn . b
  else begin scalar var, val;
    var := car vl;
    if not atom var then <<
       val := cdr var;
       var := car var;
       if not atom val then val := car val >>;
    b := list list('return, c!:expand!-let!*(cdr vl, b));
    if val then b := list('setq, var, val) . b;
    return 'prog . list var . b
  end;

symbolic procedure c!:clet!*(x, env);
   c!:cval(c!:expand!-let!*(cadr x, cddr x), env);

put('let!*, 'c!:code, function c!:clet!*);

symbolic procedure c!:clist(u, env);
  if null cdr u then c!:cval(nil, env)
  else if null cddr u then c!:cval('ncons . cdr u, env)
  else if eqcar(cadr u, 'cons) then
    c!:cval(list('acons, cadr cadr u, caddr cadr u, 'list . cddr u), env)
  else if null cdddr u then c!:cval('list2 . cdr u, env)
  else c!:cval(list('list2!*, cadr u, caddr u, 'list . cdddr u), env);

put('list, 'c!:code, function c!:clist);

symbolic procedure c!:clist!*(u, env);
  begin
    scalar v;
    u := reverse cdr u;
    v := car u;
    for each a in cdr u do
      v := list('cons, a, v);
    return c!:cval(v, env)
  end;

put('list!*, 'c!:code, function c!:clist!*);

symbolic procedure c!:ccons(u, env);
  begin
    scalar a1, a2;
    a1 := s!:improve cadr u;
    a2 := s!:improve caddr u;
    if a2 = nil or a2 = '(quote nil) or a2 = '(list) then
       return c!:cval(list('ncons, a1), env);
    if eqcar(a1, 'cons) then
       return c!:cval(list('acons, cadr a1, caddr a1, a2), env);
    if eqcar(a2, 'cons) then
       return c!:cval(list('list2!*, a1, cadr a2, caddr a2), env);
    if eqcar(a2, 'list) then
       return c!:cval(list('cons, a1,
                     list('cons, cadr a2, 'list . cddr a2)), env);
    return c!:ccall(car u, cdr u, env)
  end;

put('cons, 'c!:code, function c!:ccons);

symbolic procedure c!:cget(u, env);
  begin
    scalar a1, a2, w, r, r1;
    a1 := s!:improve cadr u;
    a2 := s!:improve caddr u;
    if eqcar(a2, 'quote) and idp(w := cadr a2) and
       (w := symbol!-make!-fastget(w, nil)) then <<
        r := c!:newreg();
        c!:outop('fastget, r, c!:cval(a1, env), w . cadr a2);
        return r >>
    else return c!:ccall(car u, cdr u, env)
  end;

put('get, 'c!:code, function c!:cget);

symbolic procedure c!:cflag(u, env);
  begin
    scalar a1, a2, w, r, r1;
    a1 := s!:improve cadr u;
    a2 := s!:improve caddr u;
    if eqcar(a2, 'quote) and idp(w := cadr a2) and
       (w := symbol!-make!-fastget(w, nil)) then <<
        r := c!:newreg();
        c!:outop('fastflag, r, c!:cval(a1, env), w . cadr a2);
        return r >>
    else return c!:ccall(car u, cdr u, env)
  end;

put('flagp, 'c!:code, function c!:cflag);

symbolic procedure c!:cgetv(u, env);
  if not !*fastvector then c!:ccall(car u, cdr u, env)
  else c!:cval('qgetv . cdr u, env);

put('getv, 'c!:code, function c!:cgetv);
!#if common!-lisp!-mode
put('svref, 'c!:code, function c!:cgetv);
!#endif

symbolic procedure c!:cputv(u, env);
  if not !*fastvector then c!:ccall(car u, cdr u, env)
  else c!:cval('qputv . cdr u, env);

put('putv, 'c!:code, function c!:cputv);

symbolic procedure c!:cqputv(x, env);
  begin
    scalar rr;
    rr := c!:pareval(cdr x, env);
    c!:outop('qputv, caddr rr, car rr, cadr rr);
    return caddr rr
  end;

put('qputv, 'c!:code, function c!:cqputv);

symbolic procedure c!:cmacrolet(u, env);
   error(0, "macrolet");

put('macrolet, 'c!:code, function c!:cmacrolet);

symbolic procedure c!:cmultiple_value_call(u, env);
   error(0, "multiple_value_call");

put('multiple!-value!-call, 'c!:code, function c!:cmultiple_value_call);

symbolic procedure c!:cmultiple_value_prog1(u, env);
   error(0, "multiple_value_prog1");

put('multiple!-value!-prog1, 'c!:code, function c!:cmultiple_value_prog1);

symbolic procedure c!:cor(u, env);
  begin
    scalar next, done, v, r;
    v := c!:newreg();
    done := c!:my_gensym();
    u := cdr u;
    while cdr u do <<
      next := c!:my_gensym();
      c!:outop('movr, v, nil, c!:cval(car u, env));
      u := cdr u;
      c!:endblock(list('ifnull, v), list(next, done));
      c!:startblock next >>;
    c!:outop('movr, v, nil, c!:cval(car u, env));
    c!:endblock('goto, list done);
    c!:startblock done;
    return v
  end;

put('or, 'c!:code, function c!:cor);

symbolic procedure c!:cprog(u, env);
  begin
    scalar w, w1, bvl, local_proglabs, progret, progexit, fluids, env1;
    env1 := car env;
    bvl := cadr u;
    for each v in bvl do
       if globalp v then error(0, list(v, "attempt to bind a global"))
       else if fluidp v then <<
          fluids := (v . c!:newreg()) . fluids;
          flag(list cdar fluids, 'c!:live_across_call); % silly if not
          env1 := ('c!:dummy!:name . cdar fluids) . env1;
          c!:outop('ldrglob, cdar fluids, v, c!:find_literal v);
          c!:outop('nilglob, nil, v, c!:find_literal v) >>
       else <<
          env1 := (v . c!:newreg()) . env1;
          c!:outop('movk1, cdar env1, nil, nil) >>;
    if fluids then c!:outop('fluidbind, nil, nil, fluids);
    env := env1 . append(fluids, cdr env);
    u := cddr u;
    progret := c!:newreg();
    progexit := c!:my_gensym();
    blockstack := (nil . progret . progexit) . blockstack;
    for each a in u do if atom a then
       if atsoc(a, local_proglabs) then <<
          if not null a then <<
             w := wrs nil;
             princ "+++++ multiply defined label: "; prin a;
             terpri(); wrs w >> >>
       else local_proglabs := list(a, c!:my_gensym()) . local_proglabs;
    proglabs := local_proglabs . proglabs;
    for each a in u do
      if atom a then <<
        w := cdr(assoc!*!*(a, local_proglabs));
        if null cdr w then <<
           rplacd(w, t);
           c!:endblock('goto, list car w);
           c!:startblock car w >> >>
      else c!:cval(a, env);
    c!:outop('movk1, progret, nil, nil);
    c!:endblock('goto, list progexit);
    c!:startblock progexit;
    for each v in fluids do
      c!:outop('strglob, cdr v, car v, c!:find_literal car v);
    blockstack := cdr blockstack;
    proglabs := cdr proglabs;
    return progret
  end;

put('prog, 'c!:code, function c!:cprog);

symbolic procedure c!:cprog!*(u, env);
   error(0, "prog*");

put('prog!*, 'c!:code, function c!:cprog!*);

symbolic procedure c!:cprog1(u, env);
  begin
    scalar g;
    g := c!:my_gensym();
    g := list('prog, list g,
              list('setq, g, cadr u),
              'progn . cddr u,
              list('return, g));
    return c!:cval(g, env)
  end;

put('prog1, 'c!:code, function c!:cprog1);

symbolic procedure c!:cprog2(u, env);
  begin
    scalar g;
    u := cdr u;
    g := c!:my_gensym();
    g := list('prog, list g,
              list('setq, g, cadr u),
              'progn . cddr u,
              list('return, g));
    g := list('progn, car u, g);
    return c!:cval(g, env)
  end;

put('prog2, 'c!:code, function c!:cprog2);

symbolic procedure c!:cprogn(u, env);
  begin
    scalar r;
    u := cdr u;
    if u = nil then u := '(nil);
    for each s in u do r := c!:cval(s, env);
    return r
  end;

put('progn, 'c!:code, function c!:cprogn);

symbolic procedure c!:cprogv(u, env);
   error(0, "progv");

put('progv, 'c!:code, function c!:cprogv);

symbolic procedure c!:cquote(u, env);
  begin
    scalar v;
    u := cadr u;
    v := c!:newreg();
    if null u or u = 't or c!:small_number u then
         c!:outop('movk1, v, nil, u)
    else c!:outop('movk, v, u, c!:find_literal u);
    return v;
  end;

put('quote, 'c!:code, function c!:cquote);

symbolic procedure c!:creturn(u, env);
  begin
    scalar w;
    w := assoc!*!*(nil, blockstack);
    if null w then error(0, "RETURN out of context");
    c!:outop('movr, cadr w, nil, c!:cval(cadr u, env));
    c!:endblock('goto, list cddr w);
    return nil      % value should not be used
  end;

put('return, 'c!:code, function c!:creturn);

!#if common!-lisp!-mode

symbolic procedure c!:creturn_from(u, env);
  begin
    scalar w;
    w := assoc!*!*(cadr u, blockstack);
    if null w then error(0, "RETURN-FROM out of context");
    c!:outop('movr, cadr w, nil, c!:cval(caddr u, env));
    c!:endblock('goto, list cddr w);
    return nil      % value should not be used
  end;

!#endif

put('return!-from, 'c!:code, function c!:creturn_from);

symbolic procedure c!:csetq(u, env);
  begin
    scalar v, w;
    v := c!:cval(caddr u, env);
    u := cadr u;
    if not idp u then error(0, list(u, "bad variable in setq"))
    else if (w := c!:locally_bound(u, env)) then
       c!:outop('movr, cdr w, nil, v)
    else if flagp(u, 'c!:constant) then
       error(0, list(u, "attempt to use setq on a constant"))
    else c!:outop('strglob, v, u, c!:find_literal u);
    return v
  end;

put('setq, 'c!:code, function c!:csetq);
put('noisy!-setq, 'c!:code, function c!:csetq);

!#if common!-lisp!-mode

symbolic procedure c!:ctagbody(u, env);
  begin
    scalar w, bvl, local_proglabs, res;
    u := cdr u;
    for each a in u do if atom a then
       if atsoc(a, local_proglabs) then <<
          if not null a then <<
             w := wrs nil;
             princ "+++++ multiply defined label: "; prin a;
             terpri(); wrs w >> >>
       else local_proglabs := list(a, c!:my_gensym()) . local_proglabs;
    proglabs := local_proglabs . proglabs;
    for each a in u do
      if atom a then <<
        w := cdr(assoc!*!*(a, local_proglabs));
        if null cdr w then <<
           rplacd(w, t);
           c!:endblock('goto, list car w);
           c!:startblock car w >> >>
      else res := c!:cval(a, env);
    if null res then res := c!:cval(nil, env);
    proglabs := cdr proglabs;
    return res
  end;

put('tagbody, 'c!:code, function c!:ctagbody);

!#endif

symbolic procedure c!:cprivate_tagbody(u, env);
% This sets a label for use for tail-call to self.
  begin
    u := cdr u;
    c!:endblock('goto, list car u);
    c!:startblock car u;
% This seems to be the proper place to capture the internal names associated
% with argument-vars that must be reset if a tail-call is mapped into a loop.
    current_args := for each v in current_args collect begin
       scalar z;
       z := assoc!*!*(v, car env);
       return if z then cdr z else v end;
    return c!:cval(cadr u, env)
  end;

put('c!:private_tagbody, 'c!:code, function c!:cprivate_tagbody);

symbolic procedure c!:cthe(u, env);
   c!:cval(caddr u, env);

put('the, 'c!:code, function c!:cthe);

symbolic procedure c!:cthrow(u, env);
   error(0, "throw");

put('throw, 'c!:code, function c!:cthrow);

symbolic procedure c!:cunless(u, env);
  begin
    scalar v, join, l1, l2;
    v := c!:newreg();
    join := c!:my_gensym();
    l1 := c!:my_gensym();
    l2 := c!:my_gensym();
    c!:cjumpif(cadr u, env, l2, l1);
    c!:startblock l1;
    c!:outop('movr, v, nil, c!:cval('progn . cddr u, env));
    c!:endblock('goto, list join);
    c!:startblock l2;
    c!:outop('movk1, v, nil, nil);
    c!:endblock('goto, list join);
    c!:startblock join;
    return v
  end;

put('unless, 'c!:code, function c!:cunless);

symbolic procedure c!:cunwind_protect(u, env);
   error(0, "unwind_protect");

put('unwind!-protect, 'c!:code, function c!:cunwind_protect);

symbolic procedure c!:cwhen(u, env);
  begin
    scalar v, join, l1, l2;
    v := c!:newreg();
    join := c!:my_gensym();
    l1 := c!:my_gensym();
    l2 := c!:my_gensym();
    c!:cjumpif(cadr u, env, l1, l2);
    c!:startblock l1;
    c!:outop('movr, v, nil, c!:cval('progn . cddr u, env));
    c!:endblock('goto, list join);
    c!:startblock l2;
    c!:outop('movk1, v, nil, nil);
    c!:endblock('goto, list join);
    c!:startblock join;
    return v
  end;

put('when, 'c!:code, function c!:cwhen);

%
% End of code to handle special forms - what comes from here on is
% more concerned with performance than with speed.
%

!#if (not common!-lisp!-mode)

% mapcar etc are compiled specially as a fudge to achieve an effect as
% if proper environment-capture was implemented for the functional
% argument (which I do not support at present).

symbolic procedure c!:expand_map(fnargs);
  begin
    scalar carp, fn, fn1, args, var, avar, moveon, l1, r, s, closed;
    fn := car fnargs;
% if the value of a mapping function is not needed I demote from mapcar to
% mapc or from maplist to map.
%   if context > 1 then <<
%      if fn = 'mapcar then fn := 'mapc
%      else if fn = 'maplist then fn := 'map >>;
    if fn = 'mapc or fn = 'mapcar or fn = 'mapcan then carp := t;
    fnargs := cdr fnargs;
    if atom fnargs then error(0,"bad arguments to map function");
    fn1 := cadr fnargs;
    while eqcar(fn1, 'function) or
          (eqcar(fn1, 'quote) and eqcar(cadr fn1, 'lambda)) do <<
       fn1 := cadr fn1;
       closed := t >>;
% if closed is false I will insert FUNCALL since I am invoking a function
% stored in a variable - NB this means that the word FUNCTION becomes
% essential when using mapping operators - this is because I have built
% a 2-Lisp rather than a 1-Lisp.
    args := car fnargs;
    l1 := c!:my_gensym();
    r := c!:my_gensym();
    s := c!:my_gensym();
    var := c!:my_gensym();
    avar := var;
    if carp then avar := list('car, avar);
    if closed then fn1 := list(fn1, avar)
    else fn1 := list('apply1, fn1, avar);
    moveon := list('setq, var, list('cdr, var));
    if fn = 'map or fn = 'mapc then fn := sublis(
       list('l1 . l1, 'var . var,
            'fn . fn1, 'args . args, 'moveon . moveon),
       '(prog (var)
             (setq var args)
       l1    (cond
                ((not var) (return nil)))
             fn
             moveon
             (go l1)))
    else if fn = 'maplist or fn = 'mapcar then fn := sublis(
       list('l1 . l1, 'var . var,
            'fn . fn1, 'args . args, 'moveon . moveon, 'r . r),
       '(prog (var r)
             (setq var args)
       l1    (cond
                ((not var) (return (reversip r))))
             (setq r (cons fn r))
             moveon
             (go l1)))
    else fn := sublis(
       list('l1 . l1, 'l2 . c!:my_gensym(), 'var . var,
            'fn . fn1, 'args . args, 'moveon . moveon,
            'r . c!:my_gensym(), 's . c!:my_gensym()),
       '(prog (var r s)
             (setq var args)
             (setq r (setq s (list nil)))
       l1    (cond
                ((not var) (return (cdr r))))
             (rplacd s fn)
       l2    (cond
                ((not (atom (cdr s))) (setq s (cdr s)) (go l2)))
             moveon
             (go l1)));
    return fn
  end;


put('map,     'c!:compile_macro, function c!:expand_map);
put('maplist, 'c!:compile_macro, function c!:expand_map);
put('mapc,    'c!:compile_macro, function c!:expand_map);
put('mapcar,  'c!:compile_macro, function c!:expand_map);
put('mapcon,  'c!:compile_macro, function c!:expand_map);
put('mapcan,  'c!:compile_macro, function c!:expand_map);

!#endif

% caaar to cddddr get expanded into compositions of
% car, cdr which are compiled in-line

symbolic procedure c!:expand_carcdr(x);
  begin
    scalar name;
    name := cdr reverse cdr explode2 car x;
    x := cadr x;
    for each v in name do
        x := list(if v = 'a then 'car else 'cdr, x);
    return x
  end;

<< put('caar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cddr, 'c!:compile_macro, function c!:expand_carcdr);
   put('caaar, 'c!:compile_macro, function c!:expand_carcdr);
   put('caadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cadar, 'c!:compile_macro, function c!:expand_carcdr);
   put('caddr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdaar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cddar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdddr, 'c!:compile_macro, function c!:expand_carcdr);
   put('caaaar, 'c!:compile_macro, function c!:expand_carcdr);
   put('caaadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('caadar, 'c!:compile_macro, function c!:expand_carcdr);
   put('caaddr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cadaar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cadadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('caddar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cadddr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdaaar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdaadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdadar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdaddr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cddaar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cddadr, 'c!:compile_macro, function c!:expand_carcdr);
   put('cdddar, 'c!:compile_macro, function c!:expand_carcdr);
   put('cddddr, 'c!:compile_macro, function c!:expand_carcdr) >>;

symbolic procedure c!:builtin_one(x, env);
  begin
    scalar r1, r2;
    r1 := c!:cval(cadr x, env);
    c!:outop(car x, r2:=c!:newreg(), cdr env, r1);
    return r2
  end;

symbolic procedure c!:builtin_two(x, env);
  begin
    scalar a1, a2, r, rr;
    a1 := cadr x;
    a2 := caddr x;
    rr := c!:pareval(list(a1, a2), env);
    c!:outop(car x, r:=c!:newreg(), car rr, cadr rr);
    return r
  end;

symbolic procedure c!:narg(x, env);
  c!:cval(expand(cdr x, get(car x, 'c!:binary_version)), env);

for each n in
   '((plus plus2)
     (times times2)
     (iplus iplus2)
     (itimes itimes2)) do <<
        put(car n, 'c!:binary_version, cadr n);
        put(car n, 'c!:code, function c!:narg) >>;

!#if common!-lisp!-mode
for each n in
   '((!+ plus2)
     (!* times2)) do <<
        put(car n, 'c!:binary_version, cadr n);
        put(car n, 'c!:code, function c!:narg) >>;
!#endif

symbolic procedure c!:cplus2(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if numberp a and numberp b then c!:cval(a+b, env)
       else if a = 0 then c!:cval(b, env)
       else if a = 1 then c!:cval(list('add1, b), env)
       else if b = 0 then c!:cval(a, env)
       else if b = 1 then c!:cval(list('add1, a), env)
       else if b = -1 then c!:cval(list('sub1, a), env)
       else c!:ccall(car u, cdr u, env)
  end;

put('plus2, 'c!:code, function c!:cplus2);

symbolic procedure c!:ciplus2(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if numberp a and numberp b then c!:cval(a+b, env)
       else if a = 0 then c!:cval(b, env)
       else if a = 1 then c!:cval(list('iadd1, b), env)
       else if b = 0 then c!:cval(a, env)
       else if b = 1 then c!:cval(list('iadd1, a), env)
       else if b = -1 then c!:cval(list('isub1, a), env)
       else c!:builtin_two(u, env)
  end;

put('iplus2, 'c!:code, function c!:ciplus2);

symbolic procedure c!:cdifference(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if numberp a and numberp b then c!:cval(a-b, env)
       else if a = 0 then c!:cval(list('minus, b), env)
       else if b = 0 then c!:cval(a, env)
       else if b = 1 then c!:cval(list('sub1, a), env)
       else if b = -1 then c!:cval(list('add1, a), env)
       else c!:ccall(car u, cdr u, env)
  end;

put('difference, 'c!:code, function c!:cdifference);

symbolic procedure c!:cidifference(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if numberp a and numberp b then c!:cval(a-b, env)
       else if a = 0 then c!:cval(list('iminus, b), env)
       else if b = 0 then c!:cval(a, env)
       else if b = 1 then c!:cval(list('isub1, a), env)
       else if b = -1 then c!:cval(list('iadd1, a), env)
       else c!:builtin_two(u, env)
  end;

put('idifference, 'c!:code, function c!:cidifference);

symbolic procedure c!:ctimes2(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if numberp a and numberp b then c!:cval(a*b, env)
       else if a = 0 or b = 0 then c!:cval(0, env)
       else if a = 1 then c!:cval(b, env)
       else if b = 1 then c!:cval(a, env)
       else if a = -1 then c!:cval(list('minus, b), env)
       else if b = -1 then c!:cval(list('minus, a), env)
       else c!:ccall(car u, cdr u, env)
  end;

put('times2, 'c!:code, function c!:ctimes2);

symbolic procedure c!:citimes2(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if numberp a and numberp b then c!:cval(a*b, env)
       else if a = 0 or b = 0 then c!:cval(0, env)
       else if a = 1 then c!:cval(b, env)
       else if b = 1 then c!:cval(a, env)
       else if a = -1 then c!:cval(list('iminus, b), env)
       else if b = -1 then c!:cval(list('iminus, a), env)
       else c!:builtin_two(u, env)
  end;

put('itimes2, 'c!:code, function c!:citimes2);

symbolic procedure c!:cminus(u, env);
  begin
    scalar a, b;
    a := s!:improve cadr u;
    return if numberp a then c!:cval(-a, env)
       else if eqcar(a, 'minus) then c!:cval(cadr a, env)
       else c!:ccall(car u, cdr u, env)
  end;

put('minus, 'c!:code, function c!:cminus);

symbolic procedure c!:ceq(x, env);
  begin
    scalar a1, a2, r, rr;
    a1 := s!:improve cadr x;
    a2 := s!:improve caddr x;
    if a1 = nil then return c!:cval(list('null, a2), env)
    else if a2 = nil then return c!:cval(list('null, a1), env);
    rr := c!:pareval(list(a1, a2), env);
    c!:outop('eq, r:=c!:newreg(), car rr, cadr rr);
    return r
  end;

put('eq, 'c!:code, function c!:ceq);

symbolic procedure c!:cequal(x, env);
  begin
    scalar a1, a2, r, rr;
    a1 := s!:improve cadr x;
    a2 := s!:improve caddr x;
    if a1 = nil then return c!:cval(list('null, a2), env)
    else if a2 = nil then return c!:cval(list('null, a1), env);
    rr := c!:pareval(list(a1, a2), env);
    c!:outop((if c!:eqvalid a1 or c!:eqvalid a2 then 'eq else 'equal),
          r:=c!:newreg(), car rr, cadr rr);
    return r
  end;

put('equal, 'c!:code, function c!:cequal);


%
% The next few cases are concerned with demoting functions that use
% equal tests into ones that use eq instead

symbolic procedure c!:is_fixnum x;
   fixp x and x >= -134217728 and x <= 134217727;

symbolic procedure c!:certainlyatom x;
   null x or x=t or c!:is_fixnum x or
   (eqcar(x, 'quote) and (symbolp cadr x or c!:is_fixnum cadr x));

symbolic procedure c!:atomlist1 u;
  atom u or
  ((symbolp car u or c!:is_fixnum car u) and c!:atomlist1 cdr u);

symbolic procedure c!:atomlist x;
  null x or
  (eqcar(x, 'quote) and c!:atomlist1 cadr x) or
  (eqcar(x, 'list) and
   (null cdr x or
    (c!:certainlyatom cadr x and
     c!:atomlist ('list . cddr x)))) or
  (eqcar(x, 'cons) and
   c!:certainlyatom cadr x and
   c!:atomlist caddr x);

symbolic procedure c!:atomcar x;
  (eqcar(x, 'cons) or eqcar(x, 'list)) and
  not null cdr x and
  c!:certainlyatom cadr x;

symbolic procedure c!:atomkeys1 u;
  atom u or
  (not atom car u and
   (symbolp caar u or c!:is_fixnum caar u) and
   c!:atomlist1 cdr u);

symbolic procedure c!:atomkeys x;
  null x or
  (eqcar(x, 'quote) and c!:atomkeys1 cadr x) or
  (eqcar(x, 'list) and
   (null cdr x or
    (c!:atomcar cadr x and
     c!:atomkeys ('list . cddr x)))) or
  (eqcar(x, 'cons) and
   c!:atomcar cadr x and
   c!:atomkeys caddr x);

!#if (not common!-lisp!-mode)

symbolic procedure c!:comsublis x;
   if c!:atomkeys cadr x then 'subla . cdr x
   else nil;

put('sublis, 'c!:compile_macro, function c!:comsublis);

symbolic procedure c!:comassoc x;
   if c!:certainlyatom cadr x or c!:atomkeys caddr x then 'atsoc . cdr x
   else nil;

put('assoc, 'c!:compile_macro, function c!:comassoc);
put('assoc!*!*, 'c!:compile_macro, function c!:comassoc);

symbolic procedure c!:commember x;
   if c!:certainlyatom cadr x or c!:atomlist caddr x then 'memq . cdr x
   else nil;

put('member, 'c!:compile_macro, function c!:commember);

symbolic procedure c!:comdelete x;
   if c!:certainlyatom cadr x or c!:atomlist caddr x then 'deleq . cdr x
   else nil;

put('delete, 'c!:compile_macro, function c!:comdelete);

!#endif

symbolic procedure c!:ctestif(x, env, d1, d2);
  begin
    scalar l1, l2;
    l1 := c!:my_gensym();
    l2 := c!:my_gensym();
    c!:jumpif(cadr x, l1, l2);
    x := cddr x;
    c!:startblock l1;
    c!:jumpif(car x, d1, d2);
    c!:startblock l2;
    c!:jumpif(cadr x, d1, d2)
  end;

put('if, 'c!:ctest, function c!:ctestif);

symbolic procedure c!:ctestnull(x, env, d1, d2);
  c!:cjumpif(cadr x, env, d2, d1);

put('null, 'c!:ctest, function c!:ctestnull);
put('not, 'c!:ctest, function c!:ctestnull);

symbolic procedure c!:ctestatom(x, env, d1, d2);
  begin
    x := c!:cval(cadr x, env);
    c!:endblock(list('ifatom, x), list(d1, d2))
  end;

put('atom, 'c!:ctest, function c!:ctestatom);

symbolic procedure c!:ctestconsp(x, env, d1, d2);
  begin
    x := c!:cval(cadr x, env);
    c!:endblock(list('ifatom, x), list(d2, d1))
  end;

put('consp, 'c!:ctest, function c!:ctestconsp);

symbolic procedure c!:ctestsymbol(x, env, d1, d2);
  begin
    x := c!:cval(cadr x, env);
    c!:endblock(list('ifsymbol, x), list(d1, d2))
  end;

put('idp, 'c!:ctest, function c!:ctestsymbol);

symbolic procedure c!:ctestnumberp(x, env, d1, d2);
  begin
    x := c!:cval(cadr x, env);
    c!:endblock(list('ifnumber, x), list(d1, d2))
  end;

put('numberp, 'c!:ctest, function c!:ctestnumberp);

symbolic procedure c!:ctestizerop(x, env, d1, d2);
  begin
    x := c!:cval(cadr x, env);
    c!:endblock(list('ifizerop, x), list(d1, d2))
  end;

put('izerop, 'c!:ctest, function c!:ctestizerop);

symbolic procedure c!:ctesteq(x, env, d1, d2);
  begin
    scalar a1, a2, r;
    a1 := cadr x;
    a2 := caddr x;
    if a1 = nil then return c!:cjumpif(a2, env, d2, d1)
    else if a2 = nil then return c!:cjumpif(a1, env, d2, d1);
    r := c!:pareval(list(a1, a2), env);
    c!:endblock('ifeq . r, list(d1, d2))
  end;

put('eq, 'c!:ctest, function c!:ctesteq);

symbolic procedure c!:ctesteqcar(x, env, d1, d2);
  begin
    scalar a1, a2, r, d3;
    a1 := cadr x;
    a2 := caddr x;
    d3 := c!:my_gensym();
    r := c!:pareval(list(a1, a2), env);
    c!:endblock(list('ifatom, car r), list(d2, d3));
    c!:startblock d3;
    c!:outop('qcar, car r, nil, car r);
    c!:endblock('ifeq . r, list(d1, d2))
  end;

put('eqcar, 'c!:ctest, function c!:ctesteqcar);

global '(least_fixnum greatest_fixnum);

least_fixnum := -expt(2, 27);
greatest_fixnum := expt(2, 27) - 1;

symbolic procedure c!:small_number x;
  fixp x and x >= least_fixnum and x <= greatest_fixnum;

symbolic procedure c!:eqvalid x;
  if atom x then c!:small_number x
  else if flagp(car x, 'c!:fixnum_fn) then t
  else car x = 'quote and (idp cadr x or c!:small_number cadr x);

flag('(iplus iplus2 idifference iminus itimes itimes2), 'c!:fixnum_fn);

symbolic procedure c!:ctestequal(x, env, d1, d2);
  begin
    scalar a1, a2, r;
    a1 := s!:improve cadr x;
    a2 := s!:improve caddr x;
    if a1 = nil then return c!:cjumpif(a2, env, d2, d1)
    else if a2 = nil then return c!:cjumpif(a1, env, d2, d1);
    r := c!:pareval(list(a1, a2), env);
    c!:endblock((if c!:eqvalid a1 or c!:eqvalid a2 then 'ifeq else 'ifequal) .
                  r, list(d1, d2))
  end;

put('equal, 'c!:ctest, function c!:ctestequal);

symbolic procedure c!:ctestilessp(x, env, d1, d2);
  begin
    scalar r;
    r := c!:pareval(list(cadr x, caddr x), env);
    c!:endblock('ifilessp . r, list(d1, d2))
  end;

put('ilessp, 'c!:ctest, function c!:ctestilessp);

symbolic procedure c!:ctestigreaterp(x, env, d1, d2);
  begin
    scalar r;
    r := c!:pareval(list(cadr x, caddr x), env);
    c!:endblock('ifigreaterp . r, list(d1, d2))
  end;

put('igreaterp, 'c!:ctest, function c!:ctestigreaterp);

symbolic procedure c!:ctestand(x, env, d1, d2);
  begin
    scalar next;
    for each a in cdr x do <<
      next := c!:my_gensym();
      c!:cjumpif(a, env, next, d2);
      c!:startblock next >>;
    c!:endblock('goto, list d1)
  end;

put('and, 'c!:ctest, function c!:ctestand);

symbolic procedure c!:ctestor(x, env, d1, d2);
  begin
    scalar next;
    for each a in cdr x do <<
      next := c!:my_gensym();
      c!:cjumpif(a, env, d1, next);
      c!:startblock next >>;
    c!:endblock('goto, list d2)
  end;

put('or, 'c!:ctest, function c!:ctestor);

% Here are some of the things that are built into the Lisp kernel
% and that I am happy to allow the compiler to generate direct calls to.

<<

%
% In these tables there are some functions that would need adjusting
% for a Common Lisp compiler, since they take different numbers of
% args in Common and Standard Lisp.
% This means, to be specific:
%
%  Lgensym     Lread       Latan       Ltruncate   Lfloat
%  Lintern     Lmacroexpand            Lmacroexpand_1
%  Lrandom     Lunintern   Lappend     Leqn        Lgcd
%  Lgeq        Lgreaterp   Llcm        Lleq        Llessp
%  Lquotient
%
% In these cases (at least!) the Common Lisp version of the compiler will
% need to avoid generating the call that uses this table.
%
% Some functions are missing from the list here because they seemed
% critical enough to be awarded single-byte opcodes or because the
% compiler always expands them away - car through cddddr are the main
% cases, together with eq and equal.
%

   put('batchp,                'zero_arg_fn, 0);
   put('date,                  'zero_arg_fn, 1);
   put('eject,                 'zero_arg_fn, 2);
   put('error0,                'zero_arg_fn, 3);
   put('gctime,                'zero_arg_fn, 4);
   put('gensym,                'zero_arg_fn, 5);
   put('lposn,                 'zero_arg_fn, 6);
   put('next!-random,          'zero_arg_fn, 7);
   put('posn,                  'zero_arg_fn, 8);
   put('read,                  'zero_arg_fn, 9);
   put('readch,                'zero_arg_fn, 10);
   put('terpri,                'zero_arg_fn, 11);
   put('time,                  'zero_arg_fn, 12);
   put('tyi,                   'zero_arg_fn, 13);
   put('load!-spid,            'zero_arg_fn, 14);  % ONLY used in compiled code

   put('absval,                'one_arg_fn, 0);
   put('add1,                  'one_arg_fn, 1);
   put('atan,                  'one_arg_fn, 2);
   put('apply0,                'one_arg_fn, 3);
   put('atom,                  'one_arg_fn, 4);
   put('boundp,                'one_arg_fn, 5);
   put('char!-code,            'one_arg_fn, 6);
   put('close,                 'one_arg_fn, 7);
   put('codep,                 'one_arg_fn, 8);
   put('compress,              'one_arg_fn, 9);
   put('constantp,             'one_arg_fn, 10);
   put('digitp,                'one_arg_fn, 11);
   put('endp,                  'one_arg_fn, 12);
   put('eval,                  'one_arg_fn, 13);
   put('evenp,                 'one_arg_fn, 14);
   put('evlis,                 'one_arg_fn, 15);
   put('explode,               'one_arg_fn, 16);
   put('explode2lc,            'one_arg_fn, 17);
   put('explodec,              'one_arg_fn, 18);
   put('fixp,                  'one_arg_fn, 19);
   put('float,                 'one_arg_fn, 20);
   put('floatp,                'one_arg_fn, 21);
   put('symbol!-specialp,      'one_arg_fn, 22);
   put('gc,                    'one_arg_fn, 23);
   put('gensym1,               'one_arg_fn, 24);
   put('getenv,                'one_arg_fn, 25);
   put('symbol!-globalp,       'one_arg_fn, 26);
   put('iadd1,                 'one_arg_fn, 27);
   put('symbolp,               'one_arg_fn, 28);
   put('iminus,                'one_arg_fn, 29);
   put('iminusp,               'one_arg_fn, 30);
   put('indirect,              'one_arg_fn, 31);
   put('integerp,              'one_arg_fn, 32);
   put('intern,                'one_arg_fn, 33);
   put('isub1,                 'one_arg_fn, 34);
   put('length,                'one_arg_fn, 35);
   put('lengthc,               'one_arg_fn, 36);
   put('linelength,            'one_arg_fn, 37);
   put('alpha!-char!-p,        'one_arg_fn, 38);
   put('load!-module,          'one_arg_fn, 39);
   put('lognot,                'one_arg_fn, 40);
   put('macroexpand,           'one_arg_fn, 41);
   put('macroexpand!-1,        'one_arg_fn, 42);
   put('macro!-function,       'one_arg_fn, 43);
   put('get!-bps,              'one_arg_fn, 44);
   put('make!-global,          'one_arg_fn, 45);
   put('smkvect,               'one_arg_fn, 46);
   put('make!-special,         'one_arg_fn, 47);
   put('minus,                 'one_arg_fn, 48);
   put('minusp,                'one_arg_fn, 49);
   put('mkvect,                'one_arg_fn, 50);
   put('modular!-minus,        'one_arg_fn, 51);
   put('modular!-number,       'one_arg_fn, 52);
   put('modular!-reciprocal,   'one_arg_fn, 53);
   put('null,                  'one_arg_fn, 54);
   put('oddp,                  'one_arg_fn, 55);
   put('onep,                  'one_arg_fn, 56);
   put('pagelength,            'one_arg_fn, 57);
   put('consp,                 'one_arg_fn, 58);
   put('plist,                 'one_arg_fn, 59);
   put('plusp,                 'one_arg_fn, 60);
   put('prin,                  'one_arg_fn, 61);
   put('princ,                 'one_arg_fn, 62);
   put('print,                 'one_arg_fn, 63);
   put('printc,                'one_arg_fn, 64);
   put('random,                'one_arg_fn, 65);
   put('rational,              'one_arg_fn, 66);
   put('rdf1,                  'one_arg_fn, 67);
   put('rds,                   'one_arg_fn, 68);
   put('remd,                  'one_arg_fn, 69);
   put('reverse,               'one_arg_fn, 70);
   put('nreverse,              'one_arg_fn, 71);
   put('whitespace!-char!-p,   'one_arg_fn, 72);
   put('set!-small!-modulus,   'one_arg_fn, 73);
   put('xtab,                  'one_arg_fn, 74);
   put('special!-char,         'one_arg_fn, 75);
   put('special!-form!-p,      'one_arg_fn, 76);
   put('spool,                 'one_arg_fn, 77);
   put('stop,                  'one_arg_fn, 78);
   put('stringp,               'one_arg_fn, 79);
   put('sub1,                  'one_arg_fn, 80);
   put('symbol!-env,           'one_arg_fn, 81);
   put('symbol!-function,      'one_arg_fn, 82);
   put('symbol!-name,          'one_arg_fn, 83);
   put('symbol!-value,         'one_arg_fn, 84);
   put('system,                'one_arg_fn, 85);
   put('truncate,              'one_arg_fn, 86);
   put('ttab,                  'one_arg_fn, 87);
   put('tyo,                   'one_arg_fn, 88);
   put('unintern,              'one_arg_fn, 89);
   put('unmake!-global,        'one_arg_fn, 90);
   put('unmake!-special,       'one_arg_fn, 91);
   put('upbv,                  'one_arg_fn, 92);
   put('simple!-vectorp,       'one_arg_fn, 93);
   put('verbos,                'one_arg_fn, 94);
   put('wrs,                   'one_arg_fn, 95);
   put('zerop,                 'one_arg_fn, 96);
   put('car,                   'one_arg_fn, 97);
   put('cdr,                   'one_arg_fn, 98);
   put('caar,                  'one_arg_fn, 99);
   put('cadr,                  'one_arg_fn, 100);
   put('cdar,                  'one_arg_fn, 101);
   put('cddr,                  'one_arg_fn, 102);
   put('car,                   'one_arg_fn, 103);   % Really QCAR (unchecked)
   put('cdr,                   'one_arg_fn, 104);
   put('caar,                  'one_arg_fn, 105);
   put('cadr,                  'one_arg_fn, 106);
   put('cdar,                  'one_arg_fn, 107);
   put('cddr,                  'one_arg_fn, 108);
   put('ncons,                 'one_arg_fn, 109);
   put('numberp,               'one_arg_fn, 110);
   put('is!-spid,              'one_arg_fn, 111);  % ONLY used in compiled code
   put('spid!-to!-nil,         'one_arg_fn, 112);  % ONLY used in compiled code
   put('mv!-list,              'one_arg_fn, 113);  % ONLY used in compiled code

   put('append,                'two_arg_fn, 0);
   put('ash,                   'two_arg_fn, 1);
   put('assoc,                 'two_arg_fn, 2);
   put('atsoc,                 'two_arg_fn, 3);
   put('deleq,                 'two_arg_fn, 4);
   put('delete,                'two_arg_fn, 5);
   put('divide,                'two_arg_fn, 6);
   put('eqcar,                 'two_arg_fn, 7);
   put('eql,                   'two_arg_fn, 8);
   put('eqn,                   'two_arg_fn, 9);
   put('expt,                  'two_arg_fn, 10);
   put('flag,                  'two_arg_fn, 11);
   put('flagpcar,              'two_arg_fn, 12);
   put('gcd,                   'two_arg_fn, 13);
   put('geq,                   'two_arg_fn, 14);
   put('getv,                  'two_arg_fn, 15);
   put('greaterp,              'two_arg_fn, 16);
   put('idifference,           'two_arg_fn, 17);
   put('igreaterp,             'two_arg_fn, 18);
   put('ilessp,                'two_arg_fn, 19);
   put('imax,                  'two_arg_fn, 20);
   put('imin,                  'two_arg_fn, 21);
   put('iplus2,                'two_arg_fn, 22);
   put('iquotient,             'two_arg_fn, 23);
   put('iremainder,            'two_arg_fn, 24);
   put('irightshift,           'two_arg_fn, 25);
   put('itimes2,               'two_arg_fn, 26);
   put('lcm,                   'two_arg_fn, 27);
   put('leq,                   'two_arg_fn, 28);
   put('lessp,                 'two_arg_fn, 29);
   put('make!-random!-state,   'two_arg_fn, 30);
   put('max2,                  'two_arg_fn, 31);
   put('member,                'two_arg_fn, 32);
   put('memq,                  'two_arg_fn, 33);
   put('min2,                  'two_arg_fn, 34);
   put('mod,                   'two_arg_fn, 35);
   put('modular!-difference,   'two_arg_fn, 36);
   put('modular!-expt,         'two_arg_fn, 37);
   put('modular!-plus,         'two_arg_fn, 38);
   put('modular!-quotient,     'two_arg_fn, 39);
   put('modular!-times,        'two_arg_fn, 40);
   put('nconc,                 'two_arg_fn, 41);
   put('neq,                   'two_arg_fn, 42);
   put('orderp,                'two_arg_fn, 43);
   put('quotient,              'two_arg_fn, 44);
   put('rem,                   'two_arg_fn, 45);
   put('remflag,               'two_arg_fn, 46);
   put('remprop,               'two_arg_fn, 47);
   put('rplaca,                'two_arg_fn, 48);
   put('rplacd,                'two_arg_fn, 49);
   put('sgetv,                 'two_arg_fn, 50);
   put('set,                   'two_arg_fn, 51);
   put('smemq,                 'two_arg_fn, 52);
   put('subla,                 'two_arg_fn, 53);
   put('sublis,                'two_arg_fn, 54);
   put('symbol!-set!-definition, 'two_arg_fn, 55);
   put('symbol!-set!-env,      'two_arg_fn, 56);
   put('times2,                'two_arg_fn, 57);
   put('xcons,                 'two_arg_fn, 58);
   put('equal,                 'two_arg_fn, 59);
   put('eq,                    'two_arg_fn, 60);
   put('cons,                  'two_arg_fn, 61);
   put('list2,                 'two_arg_fn, 62);
   put('get,                   'two_arg_fn, 63);
   put('getv,                  'two_arg_fn, 64);   % QGETV
   put('flagp,                 'two_arg_fn, 65);
   put('apply1,                'two_arg_fn, 66);
   put('difference2,           'two_arg_fn, 67);
   put('plus2,                 'two_arg_fn, 68);
   put('times2,                'two_arg_fn, 69);

   put('bpsputv,               'three_arg_fn, 0);
   put('errorsetn,             'three_arg_fn, 1);
   put('list2star,             'three_arg_fn, 2);
   put('list3,                 'three_arg_fn, 3);
   put('putprop,               'three_arg_fn, 4);
   put('putv,                  'three_arg_fn, 5);
   put('sputv,                 'three_arg_fn, 6);
   put('subst,                 'three_arg_fn, 7);
   put('apply2,                'three_arg_fn, 8);
   put('acons,                 'three_arg_fn, 9);

   "native entrypoints established" >>;

flag(
 '(atom atsoc codep constantp deleq digit endp eq eqcar evenp
   eql fixp flagp flagpcar floatp get globalp iadd1 idifference idp
   igreaterp ilessp iminus iminusp indirect integerp iplus2 irightshift
   isub1 itimes2 liter memq minusp modular!-difference modular!-expt
   modular!-minus modular!-number modular!-plus modular!-times not
   null numberp onep pairp plusp qcaar qcadr qcar qcdar qcddr
   qcdr remflag remprop reversip seprp special!-form!-p stringp
   symbol!-env symbol!-name symbol!-value threevectorp vectorp zerop),
 'c!:no_errors);

end;

% End of i86comp.red

