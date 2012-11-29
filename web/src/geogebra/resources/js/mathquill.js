/**
 * Copyleft 2010-2011 Jay and Han (laughinghan@gmail.com)
 *   under the GNU Lesser General Public License
 *     http://www.gnu.org/licenses/lgpl.html
 * Project Website: http://mathquill.com
 * 
 * This file was modified by the colleagues at GeoGebra Inc.
 * The file became part of the web version of the software GeoGebra.
 * (2012.03.30. - 2012.06.28.) Appropriate license terms apply.
 */

(function() {

var $ = jQuery,
  undefined,
  _, //temp variable of prototypes
  jQueryDataKey = '[[mathquill internal data]]',
  min = Math.min,
  max = Math.max;

/*************************************************
 * Abstract base classes of blocks and commands.
 ************************************************/

/**
 * MathElement is the core Math DOM tree node prototype.
 * Both MathBlock's and MathCommand's descend from it.
 */
function MathElement(){}
_ = MathElement.prototype;
_.prev = 0;
_.next = 0;
_.parent = 0;
_.firstChild = 0;
_.lastChild = 0;
_.eachChild = function(fn) {
  for (var child = this.firstChild; child; child = child.next)
    if (fn.call(this, child) === false) break;

  return this;
};
_.foldChildren = function(fold, fn) {
  this.eachChild(function(child) {
    fold = fn.call(this, fold, child);
  });
  return fold;
};
_.keydown = function(e) {
  return this.parent.keydown(e);
};
_.textInput = function(ch) {
  return this.parent.textInput(ch);
};

/**
 * Commands and operators, like subscripts, exponents, or fractions.
 * Descendant commands are organized into blocks.
 * May be passed a MathFragment that's being replaced.
 */
function MathCommand(){}
_ = MathCommand.prototype = new MathElement;
_.init = function(cmd, html_template, text_template, replacedFragment) {
  var self = this; // minifier optimization

  if (cmd) self.cmd = cmd;
  if (html_template) self.html_template = html_template;
  if (text_template) self.text_template = text_template;

  self.jQ = $(self.html_template[0]).data(jQueryDataKey, {cmd: self});
  self.initBlocks(replacedFragment);
};
_.initBlocks = function(replacedFragment) {
  var self = this;
  //single-block commands
  if (self.html_template.length === 1) {
    self.firstChild =
    self.lastChild =
    self.jQ.data(jQueryDataKey).block =
      (replacedFragment && replacedFragment.blockify()) || new MathBlock;

    self.firstChild.parent = self;
    self.firstChild.jQ = self.jQ.append(self.firstChild.jQ);

    return;
  }
  //otherwise, the succeeding elements of html_template should be child blocks
  var newBlock, prev, num_blocks = self.html_template.length;
  this.firstChild = newBlock = prev =
    (replacedFragment && replacedFragment.blockify()) || new MathBlock;

  newBlock.parent = self;
  newBlock.jQ = $(self.html_template[1])
    .data(jQueryDataKey, {block: newBlock})
    .append(newBlock.jQ)
    .appendTo(self.jQ);

  newBlock.blur();

  for (var i = 2; i < num_blocks; i += 1) {
    newBlock = new MathBlock;
    newBlock.parent = self;
    newBlock.prev = prev;
    prev.next = newBlock;
    prev = newBlock;

    newBlock.jQ = $(self.html_template[i])
      .data(jQueryDataKey, {block: newBlock})
      .appendTo(self.jQ);

    newBlock.blur();
  }
  self.lastChild = newBlock;
};
_.latex = function() {
  return this.foldChildren(this.cmd, function(latex, child) {
    return latex + '{' + (child.latex() || ' ') + '}';
  });
};
_.text_template = [''];
_.text = function() {
  var i = 0;
  return this.foldChildren(this.text_template[i], function(text, child) {
    i += 1;
    var child_text = child.text();
    if (text && this.text_template[i] === '('
        && child_text[0] === '(' && child_text.slice(-1) === ')')
      return text + child_text.slice(1, -1) + this.text_template[i];
    return text + child.text() + (this.text_template[i] || '');
  });
};
_.insertAt = function(cursor) {
  var cmd = this;

  cmd.parent = cursor.parent;
  cmd.next = cursor.next;
  cmd.prev = cursor.prev;

  if (cursor.prev)
    cursor.prev.next = cmd;
  else
    cursor.parent.firstChild = cmd;

  if (cursor.next)
    cursor.next.prev = cmd;
  else
    cursor.parent.lastChild = cmd;

  cursor.prev = cmd;

  cmd.jQ.insertBefore(cursor.jQ);

  //adjust context-sensitive spacing
  cmd.respace();
  if (cmd.next)
    cmd.next.respace();
  if (cmd.prev)
    cmd.prev.respace();

  cmd.placeCursor(cursor);

  cursor.redraw(); //this will soon be cmd.trigger('redraw')
};
_.respace = $.noop; //placeholder for context-sensitive spacing
_.placeCursor = function(cursor) {
  //append the cursor to the first empty child, or if none empty, the last one
  cursor.appendTo(this.foldChildren(this.firstChild, function(prev, child) {
    return prev.isEmpty() ? prev : child;
  }));
};
_.isEmpty = function() {
  return this.foldChildren(true, function(isEmpty, child) {
    return isEmpty && child.isEmpty();
  });
};
_.remove = function() {
  var self = this,
      prev = self.prev,
      next = self.next,
      parent = self.parent;

  if (prev)
    prev.next = next;
  else
    parent.firstChild = next;

  if (next)
    next.prev = prev;
  else
    parent.lastChild = prev;

  self.jQ.remove();

  return self;
};

/**
 * Lightweight command without blocks or children.
 */
function Symbol(cmd, html, text) {
  this.init(cmd, [ html ],
    [ text || (cmd && cmd.length > 1 ? cmd.slice(1) : cmd) ]);
}
_ = Symbol.prototype = new MathCommand;
_.initBlocks = $.noop;
_.latex = function(){ return this.cmd; };
_.text = function(){ return this.text_template; };
_.placeCursor = $.noop;
_.isEmpty = function(){ return true; };

/**
 * Children and parent of MathCommand's. Basically partitions all the
 * symbols and operators that descend (in the Math DOM tree) from
 * ancestor operators.
 */
function MathBlock(){}
_ = MathBlock.prototype = new MathElement;
_.latex = function() {
  return this.foldChildren('', function(latex, child) {
    return latex + child.latex();
  });
};
_.text = function() {
  return this.firstChild === this.lastChild ?
    this.firstChild.text() :
    this.foldChildren('(', function(text, child) {
      return text + child.text();
    }) + ')';
};
_.isEmpty = function() {
  return this.firstChild === 0 && this.lastChild === 0;
};
_.focus = function() {
  this.jQ.addClass('hasCursor');
  if (this.isEmpty())
    this.jQ.removeClass('empty');

  return this;
};
_.blur = function() {
  this.jQ.removeClass('hasCursor');
  if (this.isEmpty())
    this.jQ.addClass('empty');

  return this;
};

/**
 * An entity outside the Math DOM tree with one-way pointers (so it's only
 * a "view" of part of the tree, not an actual node/entity in the tree)
 * that delimit a list of symbols and operators.
 */
function MathFragment(parent, prev, next) {
  if (!arguments.length) return;

  var self = this;

  self.parent = parent;
  self.prev = prev || 0; //so you can do 'new MathFragment(block)' without
  self.next = next || 0; //ending up with this.prev or this.next === undefined

  self.jQinit(self.fold($(), function(jQ, child){ return child.jQ.add(jQ); }));
}
_ = MathFragment.prototype;
_.remove = MathCommand.prototype.remove;
_.jQinit = function(children) {
  this.jQ = children;
};
_.each = function(fn) {
  for (var el = this.prev.next || this.parent.firstChild; el !== this.next; el = el.next)
    if (fn.call(this, el) === false) break;

  return this;
};
_.fold = function(fold, fn) {
  this.each(function(el) {
    fold = fn.call(this, fold, el);
  });
  return fold;
};
_.latex = function() {
  return this.fold('', function(latex, el){ return latex + el.latex(); });
};
_.blockify = function() {
  var self = this,
      prev = self.prev,
      next = self.next,
      parent = self.parent,
      newBlock = new MathBlock,
      newFirstChild = newBlock.firstChild = prev.next || parent.firstChild,
      newLastChild = newBlock.lastChild = next.prev || parent.lastChild;

  if (prev)
    prev.next = next;
  else
    parent.firstChild = next;

  if (next)
    next.prev = prev;
  else
    parent.lastChild = prev;

  newFirstChild.prev = self.prev = 0;
  newLastChild.next = self.next = 0;

  self.parent = newBlock;
  self.each(function(el){ el.parent = newBlock; });

  newBlock.jQ = self.jQ;

  return newBlock;
};

/*********************************************
 * Root math elements with event delegation.
 ********************************************/

function createRoot(jQ, root, textbox, editable) {
  var contents = jQ.contents().detach();

  if (!textbox) {
    jQ.addClass('mathquill-rendered-math');
  }

  root.jQ = jQ.data(jQueryDataKey, {
    block: root,
    revert: function() {
      jQ.empty().unbind('.mathquill')
        .removeClass('mathquill-rendered-math mathquill-editable mathquill-textbox')
        .append(contents);
    }
  });

  var cursor = root.cursor = new Cursor(root);

  root.renderLatex(contents.text());

  //textarea stuff
  var textareaSpan = root.textarea = $.browser.webkit && /Mobile/.test(navigator.userAgent) ?
      $('<span class="textarea"><span tabindex=0></span></span>')
    : $('<span class="textarea"><textarea></textarea></span>'),
    textarea = textareaSpan.children();

  /******
   * TODO [Han]: Document this
   */
  var textareaSelectionTimeout;
  root.selectionChanged = function() {
    if (textareaSelectionTimeout === undefined) {
      textareaSelectionTimeout = setTimeout(setTextareaSelection);
    }
    forceIERedraw(jQ[0]);
  };
  function setTextareaSelection() {
    textareaSelectionTimeout = undefined;
    var latex = cursor.selection ? '$'+cursor.selection.latex()+'$' : '';
    textarea.val(latex);
    if (latex) {
      textarea[0].select();
    }
  }

  //prevent native selection except textarea
  jQ.bind('selectstart.mathquill', function(e) {
    if (e.target !== textarea[0]) e.preventDefault();
    e.stopPropagation();
  });

  //drag-to-select event handling
  var anticursor, blink = cursor.blink;
  jQ.bind('mousedown.mathquill', function(e) {
    function mousemove(e) {
      cursor.seek($(e.target), e.pageX, e.pageY);

      if (cursor.prev !== anticursor.prev
          || cursor.parent !== anticursor.parent) {
        cursor.selectFrom(anticursor);
      }

      return false;
    }

    // docmousemove is attached to the document, so that
    // selection still works when the mouse leaves the window.
    function docmousemove(e) {
      // [Han]: i delete the target because of the way seek works.
      // it will not move the mouse to the target, but will instead
      // just seek those X and Y coordinates.  If there is a target,
      // it will try to move the cursor to document, which will not work.
      // cursor.seek needs to be refactored.
      delete e.target;

      return mousemove(e);
    }

    function mouseup(e) {
      anticursor = undefined;
      cursor.blink = blink;
      if (!cursor.selection) {
        if (editable) {
          cursor.show();
        }
        else {
          textareaSpan.detach();
        }
      }

      // delete the mouse handlers now that we're not dragging anymore
      jQ.unbind('mousemove', mousemove);
      $(document).unbind('mousemove', docmousemove).unbind('mouseup', mouseup);
    }

    cursor.blink = $.noop;
    cursor.seek($(e.target), e.pageX, e.pageY);

    anticursor = new MathFragment(cursor.parent, cursor.prev, cursor.next);

    if (!editable) jQ.prepend(textareaSpan);

    jQ.mousemove(mousemove);
    $(document).mousemove(docmousemove).mouseup(mouseup);

    e.stopPropagation();
  });

  if (!editable) {
    jQ.bind('cut paste', false).bind('copy', setTextareaSelection)
      .prepend('<span class="selectable">$'+root.latex()+'$</span>');
    textarea.blur(function() {
      cursor.clearSelection();
      setTimeout(detach); //detaching during blur explodes in WebKit
    });
    function detach() {
      textareaSpan.detach();
    }
    return;
  }

  jQ.prepend(textareaSpan);

  //root CSS classes
  jQ.addClass('mathquill-editable');
  if (textbox)
    jQ.addClass('mathquill-textbox');

  //focus and blur handling
  textarea.focus(function(e) {
    if (!cursor.parent)
      cursor.appendTo(root);
    cursor.parent.jQ.addClass('hasCursor');
    if (cursor.selection) {
      cursor.selection.jQ.removeClass('blur');
      setTimeout(root.selectionChanged); //select textarea after focus
    }
    else
      cursor.show();
    e.stopPropagation();
  }).blur(function(e) {
    cursor.hide().parent.blur();
    if (cursor.selection)
      cursor.selection.jQ.addClass('blur');
    e.stopPropagation();
  });

  jQ.bind('focus.mathquill blur.mathquill', function(e) {
    textarea.trigger(e);
  }).bind('mousedown.mathquill', function() {
    setTimeout(focus);
  }).bind('click.mathquill', focus) //stupid Mobile Safari
  .blur();
  function focus() {
    textarea.focus();
  }

  //clipboard event handling
  jQ.bind('cut', function(e) {
    setTextareaSelection();

    if (cursor.selection) {
      setTimeout(function() {
        cursor.deleteSelection();
        cursor.redraw();
      });
    }

    e.stopPropagation();
  })
  .bind('copy', function(e) {
    setTextareaSelection();
    e.stopPropagation();
  })
  //added by Eli to allow for custom paste content to be sent in from the outside
  .bind('custom_paste', function(e, str) {
    //removed by Eli -- this looks like a change that Han made to more cleanly fix the problem with copy breaking Safari
    //setting this to true meant that subsequent keystrokes would be ignored
    pasting = true;
    setTimeout(function(){paste(str)}, 0);
    e.stopPropagation();
  })
  .bind('paste', function(e) {
    pasting = true;
    setTimeout(paste);
    e.stopPropagation();
  })
  .bind('select_all', function(e) {
  	var cursor_parent = cursor.parent;
  	while (cursor_parent.parent)
  		cursor_parent = cursor_parent.parent;
	cursor.clearSelection().appendTo(cursor_parent);
	while (cursor.prev)
		cursor.selectLeft();
  });
  function paste(str) {
    //FIXME HACK the parser in RootTextBlock needs to be moved to
    //Cursor::writeLatex or something so this'll work with MathQuill textboxes
    //ADDED BY ELI: for some reason "1" is being passed in as the argument for str in paste
    var latex = (typeof str == "string" ? str : textarea.val());
    if (latex.slice(0,1) === '$' && latex.slice(-1) === '$') {
      latex = latex.slice(1, -1);
    }
 
    cursor.writeLatex(latex).show();
    textarea.val('');
    
    if (cursor.root)
	    cursor.root.triggerSpecialEvent("render");
    pasting = false;
  }

  //keyboard events and text input, see Wiki page "Keyboard Events"
  var lastKeydn, lastKeydnHappened, lastKeypressWhich, pasting=false;
  jQ.bind('keydown.mathquill', function(e) {
    lastKeydn = e;
    lastKeydnHappened = true;
    if (cursor.parent.keydown(e) === false)
      e.preventDefault();
  }).bind('keypress.mathquill', function(e) {
    if (lastKeydnHappened)
      lastKeydnHappened = false;
    else {
      //there's two ways keypress might be triggered without a keydown happening first:
      if (lastKeypressWhich !== e.which)
        //all browsers do that if this textarea is given focus during the keydown of
        //a different focusable element, i.e. by that element's keydown event handler.
        //No way of knowing original keydown, so ignore this keypress
        return;
      else
        //some browsers do that when auto-repeating key events, replay the keydown
        cursor.parent.keydown(lastKeydn);
    }
    lastKeypressWhich = e.which;

    //make sure setTextareaSelection() doesn't happen before textInput(), where we
    //check if any text was typed
    if (textareaSelectionTimeout !== undefined)
      clearTimeout(textareaSelectionTimeout);

    //after keypress event, trigger virtual textInput event if text was
    //input to textarea
    setTimeout(textInput);
  });

  function textInput() {
    if (pasting || (
      'selectionStart' in textarea[0]
      && textarea[0].selectionStart !== textarea[0].selectionEnd
    )) return;
    var text = textarea.val();
    if (text) {
      textarea.val('');
      for (var i = 0; i < text.length; i += 1) {
        cursor.parent.textInput(text.charAt(i));
      }
    }
    else {
      if (cursor.selection || textareaSelectionTimeout !== undefined)
        setTextareaSelection();
    }
  }
}

function RootMathBlock(){}
_ = RootMathBlock.prototype = new MathBlock;
_.latex = function() {
  return MathBlock.prototype.latex.call(this).replace(/(\\[a-z]+) (?![a-z])/ig,'$1');
};
_.text = function() {
  return this.foldChildren('', function(text, child) {
    return text + child.text();
  });
};
_.renderLatex = function(latex) {
  var jQ = this.jQ;

  jQ.children().slice(1).remove();
  this.firstChild = this.lastChild = 0;

  // temporarily take the element out of the displayed DOM
  // while we add stuff to it.  Grab the next element or the parent
  // so we know where to put it back.
  var next = jQ.next(),
      parent = jQ.parent();

  jQ.detach();
  this.cursor.appendTo(this).writeLatex(latex);

  // Put. the element. back.
  // if there's no next element, it's at the end of its parent
  next.length ? next.before(jQ) : parent.append(jQ);

  // XXX HACK ALERT
  this.jQ.mathquill('redraw');
  this.blur();
};
_.keydown = function(e)
{
  e.ctrlKey = e.ctrlKey || e.metaKey;
  switch ((e.originalEvent && e.originalEvent.keyIdentifier) || e.which) {
  case 8: //backspace
  case 'Backspace':
  case 'U+0008':
    if (e.ctrlKey)
      while (this.cursor.prev || this.cursor.selection)
        this.cursor.backspace();
    else {
   	  if( this.isEmpty() )
        this.triggerSpecialEvent( "upwardDelete" );
      else
        this.cursor.backspace();
    }
    this.triggerSpecialEvent('render');
    break;
  case 27: //may as well be the same as tab until we figure out what to do with it
  case 'Esc':
  case 'U+001B':
  case 9: //tab
  case 'Tab':
  case 'U+0009':
    if (e.ctrlKey) break;

    var parent = this.cursor.parent;
    if (e.shiftKey) { //shift+Tab = go one block left if it exists, else escape left.
      if (parent === this.cursor.root) //cursor is in root editable, continue default
        return this.skipTextInput = true;
      else if (parent.prev) //go one block left
        this.cursor.appendTo(parent.prev);
      else //get out of the block
        this.cursor.insertBefore(parent.parent);
    }
    else { //plain Tab = go one block right if it exists, else escape right.
      if (parent === this.cursor.root) //cursor is in root editable, continue default
        return this.skipTextInput = true;
      else if (parent.next) //go one block right
        this.cursor.prependTo(parent.next);
      else //get out of the block
        this.cursor.insertAfter(parent.parent);
    }

    this.cursor.clearSelection();
    break;
  case 13: //enter
  case 'Enter':
  	this.triggerSpecialEvent( "enterPressed" );
    break;
  case 35: //end
  case 'End':
    if (e.shiftKey)
      while (this.cursor.next || (e.ctrlKey && this.cursor.parent !== this))
        this.cursor.selectRight();
    else //move to the end of the root block or the current block.
      this.cursor.clearSelection().appendTo(e.ctrlKey ? this : this.cursor.parent);
    break;
  case 36: //home
  case 'Home':
    if (e.shiftKey)
      while (this.cursor.prev || (e.ctrlKey && this.cursor.parent !== this))
        this.cursor.selectLeft();
    else //move to the start of the root block or the current block.
      this.cursor.clearSelection().prependTo(e.ctrlKey ? this : this.cursor.parent);
    break;
  case 37: //left
  case 'Left':
    if (e.ctrlKey) break;

    if (e.shiftKey)
      this.cursor.selectLeft();
    else
      this.cursor.moveLeft();
    break;
  case 38: //up
  case 'Up':
    if (e.ctrlKey || e.shiftKey) break;

    if (this.cursor.next.cmd === '\\sum ') //TODO: better architecture to not need a special case for these
      this.cursor.clearSelection().prependTo(this.cursor.next.lastChild);
    else if (this.cursor.prev.cmd === '\\sum ')
      this.cursor.clearSelection().appendTo(this.cursor.prev.lastChild);
    else if (this.cursor.next instanceof Fraction)
      this.cursor.clearSelection().prependTo(this.cursor.next.firstChild);
    else if (this.cursor.prev instanceof Fraction)
      this.cursor.clearSelection().appendTo(this.cursor.prev.firstChild);
    else if (this.cursor.next.cmd === '^')
      this.cursor.clearSelection().prependTo(this.cursor.next.firstChild);
    else if (this.cursor.next && this.cursor.next.next.cmd === '^' && this.cursor.next.next.respaced)
      this.cursor.clearSelection().prependTo(this.cursor.next.next.firstChild);
    else {
      var ancestor = this.cursor.parent, ancestor_prev;
      while (ancestor && !ancestor_prev) {
        ancestor_prev = ancestor.prev;
        ancestor = ancestor.parent.parent;
      }
      if (ancestor_prev)
        this.cursor.clearSelection().appendTo(ancestor_prev);
      else
        this.triggerSpecialEvent('upPressed');
    }

    break;
  case 39: //right
  case 'Right':
    if (e.ctrlKey) break;

    if (e.shiftKey)
      this.cursor.selectRight();
    else
      this.cursor.moveRight();
    break;
  case 40: //down
  case 'Down':
    if (e.ctrlKey || e.shiftKey) break;

    if (this.cursor.next.cmd === '\\sum ') //TODO: better architecture to not need a special case for these
      this.cursor.clearSelection().prependTo(this.cursor.next.firstChild);
    else if (this.cursor.prev.cmd === '\\sum ')
      this.cursor.clearSelection().appendTo(this.cursor.prev.firstChild);
    else if (this.cursor.next instanceof Fraction)
      this.cursor.clearSelection().prependTo(this.cursor.next.lastChild);
    else if (this.cursor.prev instanceof Fraction)
      this.cursor.clearSelection().appendTo(this.cursor.prev.lastChild);
    else if (this.cursor.next.cmd === '_')
      this.cursor.clearSelection().prependTo(this.cursor.next.firstChild);
    else if (this.cursor.next && this.cursor.next.next.cmd === '_' && this.cursor.next.next.respaced)
      this.cursor.clearSelection().prependTo(this.cursor.next.next.firstChild);
    else {
      var ancestor = this.cursor.parent, ancestor_next;
      while (ancestor && !ancestor_next) {
        ancestor_next = ancestor.next;
        ancestor = ancestor.parent.parent;
      }
      if (ancestor_next)
        this.cursor.clearSelection().prependTo(ancestor_next);
      else
        this.triggerSpecialEvent('downPressed');
    }

    break;
  case 46: //delete
  case 'Del':
  case 'U+007F':
    if (e.ctrlKey)
      while (this.cursor.next || this.cursor.selection)
        this.cursor.deleteForward();
    else {
          if( this.isEmpty() )
		  this.triggerSpecialEvent( "downwardDelete" );  
	  else
	      this.cursor.deleteForward();
    }
    this.triggerSpecialEvent('render');
    break;
  case 65: //the 'A' key, as in Ctrl+A Select All
  case 'A':
  case 'U+0041':
    if (e.ctrlKey && !e.shiftKey && !e.altKey) {
      if (this !== this.cursor.root) //so not stopPropagation'd at RootMathCommand
        return this.parent.keydown(e);

      this.cursor.clearSelection().appendTo(this);
      while (this.cursor.prev)
        this.cursor.selectLeft();
      break;
    }
  default:
    this.skipTextInput = false;
    return true;
  }
  this.skipTextInput = true;
  return false;
  
};
_.textInput = function(ch) {
  if (!this.skipTextInput) {
    this.cursor.write(ch);
	this.triggerSpecialEvent("render");
  }
};

//triggers a special event occured:
//	1) pressed up and was at 'top' of equation
//  2) pressed down and was at 'bottom' of equation
//  3) pressed backspace and equation was empty
//  4) the equation was rendered
//  5) etc
_.triggerSpecialEvent = function(eventName){
	var jQ = this.jQ;
	setTimeout( function(){ jQ.trigger(eventName)}, 1 );
}

function RootMathCommand(cursor) {
  this.init('$');
  this.firstChild.cursor = cursor;
  this.firstChild.textInput = function(ch) {
    if (this.skipTextInput) return;

    if (ch !== '$' || cursor.parent !== this)
      cursor.write(ch);
    else if (this.isEmpty()) {
      cursor.insertAfter(this.parent).backspace()
        .insertNew(new VanillaSymbol('\\$','$')).show();
    }
    else if (!cursor.next)
      cursor.insertAfter(this.parent);
    else if (!cursor.prev)
      cursor.insertBefore(this.parent);
    else
      cursor.write(ch);
  };
}
_ = RootMathCommand.prototype = new MathCommand;
_.html_template = ['<span class="mathquill-rendered-math"></span>'];
_.initBlocks = function() {
  this.firstChild =
  this.lastChild =
  this.jQ.data(jQueryDataKey).block =
    new RootMathBlock;

  this.firstChild.parent = this;
  this.firstChild.jQ = this.jQ;
};
_.latex = function() {
  return '$' + this.firstChild.latex() + '$';
};

function RootTextBlock(){}
_ = RootTextBlock.prototype = new MathBlock;
_.renderLatex = function(latex) {
  var self = this, cursor = self.cursor;
  self.jQ.children().slice(1).remove();
  self.firstChild = self.lastChild = 0;
  cursor.show().appendTo(self);

  latex = latex.match(/(?:\\\$|[^$])+|\$(?:\\\$|[^$])*\$|\$(?:\\\$|[^$])*$/g) || '';
  for (var i = 0; i < latex.length; i += 1) {
    var chunk = latex[i];
    if (chunk[0] === '$') {
      if (chunk[-1+chunk.length] === '$' && chunk[-2+chunk.length] !== '\\')
        chunk = chunk.slice(1, -1);
      else
        chunk = chunk.slice(1);

      var root = new RootMathCommand(cursor);
      cursor.insertNew(root);
      root.firstChild.renderLatex(chunk);
      cursor.show().insertAfter(root);
    }
    else {
      for (var j = 0; j < chunk.length; j += 1)
        this.cursor.insertNew(new VanillaSymbol(chunk[j]));
    }
  }
};
_.keydown = RootMathBlock.prototype.keydown;
_.textInput = function(ch) {
  if (this.skipTextInput) return;

  this.cursor.deleteSelection();
  if (ch === '$')
    this.cursor.insertNew(new RootMathCommand(this.cursor));
  else
    this.cursor.insertNew(new VanillaSymbol(ch));
};

/***************************
 * Commands and Operators.
 **************************/

var CharCmds = {}, LatexCmds = {}; //single character commands, LaTeX commands

var scale, // = function(jQ, x, y) { ... }
//will use a CSS 2D transform to scale the jQuery-wrapped HTML elements,
//or the filter matrix transform fallback for IE 5.5-8, or gracefully degrade to
//increasing the fontSize to match the vertical Y scaling factor.

//ideas from http://github.com/louisremi/jquery.transform.js
//see also http://msdn.microsoft.com/en-us/library/ms533014(v=vs.85).aspx

  forceIERedraw = $.noop,
  div = document.createElement('div'),
  div_style = div.style,
  transformPropNames = {
    transform:1,
    WebkitTransform:1,
    MozTransform:1,
    OTransform:1,
    msTransform:1
  },
  transformPropName;

for (var prop in transformPropNames) {
  if (prop in div_style) {
    transformPropName = prop;
    break;
  }
}

if (transformPropName) {
  scale = function(jQ, x, y) {
    jQ.css(transformPropName, 'scale('+x+','+y+')');
  };
}
else if ('filter' in div_style) { //IE 6, 7, & 8 fallback, see https://github.com/laughinghan/mathquill/wiki/Transforms
  forceIERedraw = function(el){ el.className = el.className; };
  scale = function(jQ, x, y) { //NOTE: assumes y > x
    x /= (1+(y-1)/2);
    jQ.addClass('matrixed').css({
      fontSize: y + 'em',
      marginTop: '-.1em',
      filter: 'progid:DXImageTransform.Microsoft'
        + '.Matrix(M11=' + x + ",SizingMethod='auto expand')"
    });
    function calculateMarginRight() {
      jQ.css('marginRight', (1+jQ.width())*(x-1)/x + 'px');
    }
    calculateMarginRight();
    var intervalId = setInterval(calculateMarginRight);
    $(window).load(function() {
      clearTimeout(intervalId);
      calculateMarginRight();
    });
  };
}
else {
  scale = function(jQ, x, y) {
    jQ.css('fontSize', y + 'em');
  };
}

function proto(parent, child) { //shorthand for prototyping
  child.prototype = parent.prototype;
  return child;
}

function bind(cons) { //shorthand for binding arguments to constructor
  var args = Array.prototype.slice.call(arguments, 1);

  return proto(cons, function() {
    cons.apply(this, Array.prototype.concat.apply(args, arguments));
  });
}

//because I miss the <font> tag
//(that's a joke, I hate this, it's like actively *fighting*
// separation of presentation and content and everything HTML and CSS
// are about, but it's an intrinsic problem with WYSIWYG)
//TODO: WYSIWYM?
function Style(cmd, html_template, replacedFragment) {
  this.init(cmd, [ html_template ], undefined, replacedFragment);
}
proto(MathCommand, Style);
//fonts
LatexCmds.mathrm = bind(Style, '\\mathrm', '<span class="roman font"></span>');
LatexCmds.mathit = bind(Style, '\\mathit', '<i class="font"></i>');
LatexCmds.mathbf = bind(Style, '\\mathbf', '<b class="font"></b>');
LatexCmds.mathsf = bind(Style, '\\mathsf', '<span class="sans-serif font"></span>');
LatexCmds.mathtt = bind(Style, '\\mathtt', '<span class="monospace font"></span>');
//text-decoration
LatexCmds.underline = bind(Style, '\\underline', '<span class="underline"></span>');
LatexCmds.overline = LatexCmds.bar = bind(Style, '\\overline', '<span class="overline"></span>');

// colors
LatexCmds.lightviolet = bind(Style, '\\lightviolet', '<span style="color:#E0B0FF"></span>');
LatexCmds.lightyellow = bind(Style, '\\lightyellow', '<span style="color:#FFFACD"></span>');
LatexCmds.lightgreen = bind(Style, '\\lightgreen', '<span style="color:#D0F0C0"></span>');
LatexCmds.lightorange = bind(Style, '\\lightorange', '<span style="color:#FFEFD5"></span>');
LatexCmds.yellow = bind(Style, '\\yellow', '<span style="color:#FFFF00"></span>');
LatexCmds.darkblue = bind(Style, '\\darkblue', '<span style="color:#1C39BB"></span>');
LatexCmds.lightpurple = bind(Style, '\\lightpurple', '<span style="color:#CCCCFF"></span>');
LatexCmds.lightblue = bind(Style, '\\lightblue', '<span style="color:#7D7DFF"></span>');
LatexCmds.maroon = bind(Style, '\\maroon', '<span style="color:#800000"></span>');
LatexCmds.lightgray = bind(Style, '\\lightgray', '<span style="color:#A0A0A0"></span>');
LatexCmds.pink = bind(Style, '\\pink', '<span style="color:#FFC0CB"></span>');
LatexCmds.gold = bind(Style, '\\gold', '<span style="color:#FFD700"></span>');
LatexCmds.black = bind(Style, '\\black', '<span style="color:#000000"></span>');
LatexCmds.orange = bind(Style, '\\orange', '<span style="color:#FF7F00"></span>');
LatexCmds.indigo = bind(Style, '\\indigo', '<span style="color:#4B0082"></span>');
LatexCmds.purple = bind(Style, '\\purple', '<span style="color:#800080"></span>');
LatexCmds.darkgray = bind(Style, '\\darkgray', '<span style="color:#202020"></span>');
LatexCmds.green = bind(Style, '\\green', '<span style="color:#00FF00"></span>');
LatexCmds.silver = bind(Style, '\\silver', '<span style="color:#404040"></span>');
LatexCmds.white = bind(Style, '\\white', '<span style="color:#FFFFFF"></span>');
LatexCmds.lime = bind(Style, '\\lime', '<span style="color:#BFFF00"></span>');
LatexCmds.gray = bind(Style, '\\gray', '<span style="color:#808080"></span>');
LatexCmds.darkgreen = bind(Style, '\\darkgreen', '<span style="color:#006400"></span>');
LatexCmds.magenta = bind(Style, '\\magenta', '<span style="color:#FF00FF"></span>');
LatexCmds.cyan = bind(Style, '\\cyan', '<span style="color:#00FFFF"></span>');
LatexCmds.red = bind(Style, '\\red', '<span style="color:#FF0000"></span>');
LatexCmds.crimson = bind(Style, '\\crimson', '<span style="color:#DC143C"></span>');
LatexCmds.turquoise = bind(Style, '\\turquoise', '<span style="color:#AFEEEE"></span>');
LatexCmds.blue = bind(Style, '\\blue', '<span style="color:#0000FF"></span>');
LatexCmds.violet = bind(Style, '\\violet', '<span style="color:#7F00FF"></span>');
LatexCmds.brown = bind(Style, '\\brown', '<span style="color:#993300"></span>');
LatexCmds.aqua = bind(Style, '\\aqua', '<span style="color:#BCD4E6"></span>');

function SupSub(cmd, html, text, replacedFragment) {
  this.init(cmd, [ '<'+html+'/>', '<span class="'+html+'"/>' ], [ text ], replacedFragment);
}
_ = SupSub.prototype = new MathCommand;
_.placeCursor = function(cursor) {
  //TODO: should there be a better place for this?
  if (this.prev instanceof BigSymbol && this.prev.cmd !== '\\int ') {
    var bigSym = this.prev, block = this.firstChild,
      firstChild = {_: 'firstChild', '^': 'lastChild'}[this.cmd],
      lastChild = {_: 'lastChild', '^': 'firstChild'}[this.cmd],
      append = {_: 'append', '^': 'prepend'}[this.cmd],
      prev = {_: 'prev', '^': 'next'}[this.cmd],
      next = {_: 'next', '^': 'prev'}[this.cmd];
    bigSym.jQ[append](
      $('<span class="'+{_: 'from', '^': 'to'}[this.cmd]+'"></span>')[append](
        block.jQ.removeClass('sup sub')
      )
    );
    block.parent = bigSym;
    bigSym[firstChild] = block;
    if (bigSym[lastChild]) {
      bigSym[lastChild][prev] = block;
      block[next] = bigSym[lastChild];
    }
    else
      bigSym[lastChild] = block;
    this.isEmpty = function(){ return true; };//FIXME hack so backspace deletes this
    cursor.backspace().appendTo(block);
    return;
  }
  this.cursor = cursor.appendTo(this.firstChild);
};
_.latex = function() {
  var latex = this.firstChild.latex();
  if (latex.length === 1)
    return this.cmd + latex;
  else
    return this.cmd + '{' + (latex || ' ') + '}';
};
_.redraw = function() {
  if (this.prev)
    this.prev.respace();
  //SupSub::respace recursively calls respace on all the following SupSubs
  //so if prev is a SupSub, no need to call respace on this or following nodes
  if (!(this.prev instanceof SupSub)) {
    this.respace();
    //and if next is a SupSub, then this.respace() will have already called
    //this.next.respace()
    if (this.next && !(this.next instanceof SupSub))
      this.next.respace();
  }
};
_.respace = function() {
  if (
    this.prev.cmd === '\\int ' || (
      this.prev instanceof SupSub && this.prev.cmd != this.cmd
      && this.prev.prev && this.prev.prev.cmd === '\\int '
    )
  ) {
    if (!this['int']) {
      this['int'] = true;
      this.jQ.addClass('int');
    }
  }
  else {
    if (this['int']) {
      this['int'] = false;
      this.jQ.removeClass('int');
    }
  }

  this.respaced = this.prev instanceof SupSub && this.prev.cmd != this.cmd && !this.prev.respaced;
  if (this.respaced) {
    var fontSize = +this.jQ.css('fontSize').slice(0,-2),
      prevWidth = this.prev.jQ.outerWidth(),
      thisWidth = this.jQ.outerWidth();
    this.jQ.css({
      left: (this['int'] && this.cmd === '_' ? -.25 : 0) - prevWidth/fontSize + 'em',
      marginRight: .1 - min(thisWidth, prevWidth)/fontSize + 'em'
        //1px extra so it doesn't wrap in retarded browsers (Firefox 2, I think)
    });
  }
  else if (this['int'] && this.cmd === '_') {
    this.jQ.css({
      left: '-.25em',
      marginRight: ''
    });
  }
  else {
    this.jQ.css({
      left: '',
      marginRight: ''
    });
  }

  if (this.next instanceof SupSub)
    this.next.respace();

  return this;
};
_.keydown = function(e) {
  if (this.cursor.parent.parent !== this || e.ctrlKey || e.metaKey)
    return this.parent.keydown(e);
  //e.which === 9 <=> Tab key
  else if (e.which === 9) {
    e.preventDefault();
    if (e.shiftKey && this.respaced)
      this.cursor.clearSelection().appendTo(this.prev.firstChild);
    else if (!e.shiftKey && this.next.respaced)
      this.cursor.clearSelection().prependTo(this.next.firstChild);
    else
      return this.parent.keydown(e);
  }
  else if (e.shiftKey)
    return this.parent.keydown(e);
  //e.which === 37 <=> Left key
  else if (e.which === 37 && !this.cursor.prev && this.respaced)
    this.cursor.clearSelection().insertBefore(this.prev);
  //e.which === 39 <=> Right key
  else if (e.which === 39 && !this.cursor.next && this.next.respaced)
    this.cursor.clearSelection().insertAfter(this.next);
  //e.which === 38 <=> Up, 40 <=> Down key
  else if ((e.which === 38 && this.cmd === '_' && !(this.cursor.next.cmd === '^'))
        || (e.which === 40 && this.cmd === '^' && !(this.cursor.next.cmd === '_'))
  ) {
    if (!this.cursor.prev || this.cursor.next)
      this.cursor.clearSelection().insertBefore(this.respaced ? this.prev : this);
    else
      this.cursor.clearSelection().insertAfter(this.next.respaced ? this.next : this);
  }
  else
    return this.parent.keydown(e);
};

LatexCmds.subscript = LatexCmds._ = proto(SupSub, function(replacedFragment) {
  SupSub.call(this, '_', 'sub', '_', replacedFragment);
});

LatexCmds.superscript =
LatexCmds.supscript =
LatexCmds['^'] = proto(SupSub, function(replacedFragment) {
  SupSub.call(this, '^', 'sup', '^', replacedFragment);
});

function Fraction(replacedFragment) {
  this.init('\\frac', undefined, undefined, replacedFragment);
  this.jQ.append('<span style="display:inline-block;width:0">&nbsp;</span>');
}
_ = Fraction.prototype = new MathCommand;
_.html_template = [
  '<span class="fraction"></span>',
  '<span class="numerator"></span>',
  '<span class="denominator"></span>'
];
_.text_template = ['(', '/', ')'];

LatexCmds.frac = LatexCmds.dfrac = LatexCmds.cfrac = LatexCmds.fraction = Fraction;

function LiveFraction() {
  Fraction.apply(this, arguments);
}
_ = LiveFraction.prototype = new Fraction;
_.placeCursor = function(cursor) { //TODO: better architecture so this can be done more cleanly, highjacking MathCommand::placeCursor doesn't seem like the right place to do this
  if (this.firstChild.isEmpty()) {
    var prev = this.prev;
    while (prev &&
      !(
        prev instanceof BinaryOperator ||
        prev instanceof TextBlock ||
        prev instanceof BigSymbol ||
        //prev.isLastLetter || //COMMENTED OUT BY ELI so that cos(x)/  would put the entire cos(x) in the numerator
        prev.cmd === ',' ||
        prev.cmd === ':' ||
        prev.cmd === '\\space '
      ) //lookbehind for operator
    )
      prev = prev.prev;

    if (prev instanceof BigSymbol && prev.next instanceof SupSub) {
      prev = prev.next;
      if (prev.next instanceof SupSub && prev.next.cmd != prev.cmd)
        prev = prev.next;
    }

    if (prev !== this.prev) { //FIXME: major Law of Demeter violation, shouldn't know here that MathCommand::initBlocks does some initialization that MathFragment::blockify doesn't
      var newBlock = new MathFragment(this.parent, prev, this).blockify();
      newBlock.jQ = this.firstChild.jQ.empty().removeClass('empty').append(newBlock.jQ).data(jQueryDataKey, { block: newBlock });
      newBlock.next = this.lastChild;
      newBlock.parent = this;
      this.firstChild = this.lastChild.prev = newBlock;
    }
  }
  cursor.appendTo(this.lastChild);
};

LatexCmds.over = CharCmds['/'] = LiveFraction;

function SquareRoot(replacedFragment) {
  this.init('\\sqrt', undefined, undefined, replacedFragment);
}
_ = SquareRoot.prototype = new MathCommand;
_.html_template = [
  '<span class="block"><span class="sqrt-prefix">&radic;</span></span>',
  '<span class="sqrt-stem"></span>'
];
_.text_template = ['sqrt(', ')'];
_.redraw = function() {
  var block = this.lastChild.jQ;
  scale(block.prev(), 1, block.innerHeight()/+block.css('fontSize').slice(0,-2) - .1);
};

LatexCmds.Sqrt = LatexCmds.sqrt = LatexCmds['√'] = SquareRoot;

function NthRoot(replacedFragment) {
  SquareRoot.call(this, replacedFragment);
  this.jQ = this.firstChild.jQ.detach().add(this.jQ);
}
_ = NthRoot.prototype = new SquareRoot;
_.html_template = [
  '<span class="block"><span class="sqrt-prefix">&radic;</span></span>',
  '<sup class="nthroot"></sup>',
  '<span class="sqrt-stem"></span>'
];
_.text_template = ['nroot(', ',', ')'];
_.text = function() {
  return 'nroot('+
  	this.lastChild.text()+
  	','+
  	this.firstChild.text()+
  	')';
};
_.latex = function() {
  return '\\sqrt['+this.firstChild.latex()+']{'+this.lastChild.latex()+'}';
};

LatexCmds.nthroot = LatexCmds.nroot = NthRoot;

// Round/Square/Curly/Angle Brackets (aka Parens/Brackets/Braces)
function Bracket(open, close, cmd, end, replacedFragment) {
  this.init('\\left'+cmd,
    ['<span class="block"><span class="paren">'+open+'</span><span class="block"></span><span class="paren">'+close+'</span></span>'],
    [open, close],
    replacedFragment);
  this.end = '\\right'+end;
}
_ = Bracket.prototype = new MathCommand;
_.initBlocks = function(replacedFragment) { //FIXME: possible Law of Demeter violation, hardcore MathCommand::initBlocks knowledge needed here
  this.firstChild = this.lastChild =
    (replacedFragment && replacedFragment.blockify()) || new MathBlock;
  this.firstChild.parent = this;
  this.firstChild.jQ = this.jQ.children(':eq(1)')
    .data(jQueryDataKey, {block: this.firstChild})
    .append(this.firstChild.jQ);

  var block = this.blockjQ = this.firstChild.jQ;
  this.bracketjQs = block.prev().add(block.next());
};
//Want to auto-expand paren to end of block
_.placeCursor = function(cursor) { //TODO: better architecture so this can be done more cleanly, highjacking MathCommand::placeCursor doesn't seem like the right place to do this
  if (this.firstChild.isEmpty() && this.next) {
    //FIXME: Law of Demeter violation, shouldn't know here that MathCommand::initBlocks does some initialization that MathFragment::blockify doesn't
    var newBlock = new MathFragment(this.parent, this, 0).blockify();
    newBlock.jQ = this.firstChild.jQ.empty().removeClass('empty').append(newBlock.jQ).data(jQueryDataKey, { block: newBlock });
    newBlock.parent = this;
    this.firstChild = this.lastChild = newBlock;
  }
  cursor.prependTo(this.firstChild);
};
_.latex = function() {
  return this.cmd + this.firstChild.latex() + this.end;
};
_.redraw = function() {
  var height = this.blockjQ.outerHeight()/+this.blockjQ.css('fontSize').slice(0,-2);
  scale(this.bracketjQs, min(1 + .2*(height - 1), 1.2), 1.05*height);
};


//proto(Bracket, function(replacedFragment) {
//  Bracket.call(this, '{', '}', '\\{', '\\}', replacedFragment);
//});
LatexCmds.langle = LatexCmds.lang = proto(Bracket, function(replacedFragment) {
  Bracket.call(this,'&lang;','&rang;','\\langle ','\\rangle ',replacedFragment);
});

// Closing bracket matching opening bracket above
function CloseBracket(open, close, cmd, end, replacedFragment) {
  Bracket.apply(this, arguments);
}
_ = CloseBracket.prototype = new Bracket;
_.placeCursor = function(cursor) {
  //if my parent is a matching open-paren, and I was not passed
  //a selection fragment, then close the open-paren here,
  //i.e. move everything after me in the open-paren to
  //after the paren group just closed
  if (this.firstChild.isEmpty()) {
    if (this.parent.parent.end === this.end) {
      //FIXME: Law of Demeter violation, shouldn't know here that MathCommand::initBlocks does some initialization that MathFragment::blockify doesn't
      var newBlock = this.prev ? new MathFragment(this.parent, 0, this).blockify() : new MathBlock;
      newBlock.jQ = this.firstChild.jQ.empty().removeClass('empty').append(newBlock.jQ).data(jQueryDataKey, { block: newBlock });
      newBlock.parent = this;
      this.firstChild = this.lastChild = newBlock;
      cursor.insertBefore(this).backspace().insertAfter(this);
      this.firstChild.blur();
    }
    else
      cursor.appendTo(this.firstChild);
  }
  this.redraw();
};


//proto(CloseBracket, function(replacedFragment) {
//  CloseBracket.call(this, '{','}','\\{','\\}',replacedFragment);
//});
LatexCmds.rangle = LatexCmds.rang = proto(CloseBracket, function(replacedFragment) {
  CloseBracket.call(this,'&lang;','&rang;','\\langle ','\\rangle ',replacedFragment);
});

function Paren(open, close, replacedFragment) {
  Bracket.call(this, open, close, open, close, replacedFragment);
}
Paren.prototype = Bracket.prototype;

LatexCmds.lparen = CharCmds['('] = proto(Paren, function(replacedFragment) {
  Paren.call(this, '(', ')', replacedFragment);
});
LatexCmds.lbrack = LatexCmds.lbracket = CharCmds['['] = proto(Paren, function(replacedFragment) {
  Paren.call(this, '[', ']', replacedFragment);
});

function CloseParen(open, close, replacedFragment) {
  CloseBracket.call(this, open, close, open, close, replacedFragment);
}
CloseParen.prototype = CloseBracket.prototype;

LatexCmds.rparen = CharCmds[')'] = proto(CloseParen, function(replacedFragment) {
  CloseParen.call(this, '(', ')', replacedFragment);
});
LatexCmds.rbrack = LatexCmds.rbracket = CharCmds[']'] = proto(CloseParen, function(replacedFragment) {
  CloseParen.call(this, '[', ']', replacedFragment);
});

function Pipes(replacedFragment) {
  Paren.call(this, '|', '|', replacedFragment);
}
_ = Pipes.prototype = new Paren;
_.placeCursor = function(cursor) {
  if (!this.next && this.parent.parent && this.parent.parent.end === this.end && this.firstChild.isEmpty())
    cursor.backspace().insertAfter(this.parent.parent);
  else
    cursor.appendTo(this.firstChild);
};

LatexCmds.lpipe = LatexCmds.rpipe = CharCmds['|'] = Pipes;

function TextBlock(replacedText) {
  if (replacedText instanceof MathFragment)
    this.replacedText = replacedText.remove().jQ.text();
  else if (typeof replacedText === 'string')
    this.replacedText = replacedText;

  this.init();
}
_ = TextBlock.prototype = new MathCommand;
_.cmd = '\\text';
_.html_template = ['<span class="text"></span>'];
_.text_template = ['"', '"'];
_.initBlocks = function() { //FIXME: another possible Law of Demeter violation, but this seems much cleaner, like it was supposed to be done this way
  this.firstChild =
  this.lastChild =
  this.jQ.data(jQueryDataKey).block = new InnerTextBlock;

  this.firstChild.parent = this;
  this.firstChild.jQ = this.jQ.append(this.firstChild.jQ);
};
_.placeCursor = function(cursor) { //TODO: this should be done in the constructor that's passed replacedFragment, but you need the cursor to create new characters and insert them
  (this.cursor = cursor).appendTo(this.firstChild);

  if (this.replacedText)
    for (var i = 0; i < this.replacedText.length; i += 1)
      this.write(this.replacedText.charAt(i));
};
_.write = function(ch) {
  this.cursor.insertNew(new VanillaSymbol(ch));
};
_.keydown = function(e) {
  //backspace and delete and ends of block don't unwrap
  if (!this.cursor.selection &&
    (
      (e.which === 8 && !this.cursor.prev) ||
      (e.which === 46 && !this.cursor.next)
    )
  ) {
    if (this.isEmpty())
      this.cursor.insertAfter(this);
    return false;
  }
  return this.parent.keydown(e);
};
_.textInput = function(ch) {
  this.cursor.deleteSelection();
  if (ch !== '$')
    this.write(ch);
  else if (this.isEmpty())
    this.cursor.insertAfter(this).backspace().insertNew(new VanillaSymbol('\\$','$'));
  else if (!this.cursor.next)
    this.cursor.insertAfter(this);
  else if (!this.cursor.prev)
    this.cursor.insertBefore(this);
  else { //split apart
    var next = new TextBlock(new MathFragment(this.firstChild, this.cursor.prev));
    next.placeCursor = function(cursor) { //FIXME HACK: pretend no prev so they don't get merged
      this.prev = 0;
      delete this.placeCursor;
      this.placeCursor(cursor);
    };
    next.firstChild.focus = function(){ return this; };
    this.cursor.insertAfter(this).insertNew(next);
    next.prev = this;
    this.cursor.insertBefore(next);
    delete next.firstChild.focus;
  }
};
function InnerTextBlock(){}
_ = InnerTextBlock.prototype = new MathBlock;
_.blur = function() {
  this.jQ.removeClass('hasCursor');
  if (this.isEmpty()) {
    var textblock = this.parent, cursor = textblock.cursor;
    if (cursor.parent === this)
      this.jQ.addClass('empty');
    else {
      cursor.hide();
      textblock.remove();
      if (cursor.next === textblock)
        cursor.next = textblock.next;
      else if (cursor.prev === textblock)
        cursor.prev = textblock.prev;

      cursor.show().redraw();
    }
  }
  return this;
};
_.focus = function() {
  MathBlock.prototype.focus.call(this);

  var textblock = this.parent;
  if (textblock.next.cmd === textblock.cmd) { //TODO: seems like there should be a better way to move MathElements around
    var innerblock = this,
      cursor = textblock.cursor,
      next = textblock.next.firstChild;

    next.eachChild(function(child){
      child.parent = innerblock;
      child.jQ.appendTo(innerblock.jQ);
    });

    if (this.lastChild)
      this.lastChild.next = next.firstChild;
    else
      this.firstChild = next.firstChild;

    next.firstChild.prev = this.lastChild;
    this.lastChild = next.lastChild;

    next.parent.remove();

    if (cursor.prev)
      cursor.insertAfter(cursor.prev);
    else
      cursor.prependTo(this);

    cursor.redraw();
  }
  else if (textblock.prev.cmd === textblock.cmd) {
    var cursor = textblock.cursor;
    if (cursor.prev)
      textblock.prev.firstChild.focus();
    else
      cursor.appendTo(textblock.prev.firstChild);
  }
  return this;
};

CharCmds.$ =
LatexCmds.text =
LatexCmds.textnormal =
LatexCmds.textrm =
LatexCmds.textup =
LatexCmds.textmd =
  TextBlock;

function makeTextBlock(latex, html) {
  function SomeTextBlock() {
    TextBlock.apply(this, arguments);
  }
  _ = SomeTextBlock.prototype = new TextBlock;
  _.cmd = latex;
  _.html_template = [ html ];

  return SomeTextBlock;
}

LatexCmds.em = LatexCmds.italic = LatexCmds.italics =
LatexCmds.emph = LatexCmds.textit = LatexCmds.textsl =
  makeTextBlock('\\textit', '<i class="text"></i>');
LatexCmds.strong = LatexCmds.bold = LatexCmds.textbf =
  makeTextBlock('\\textbf', '<b class="text"></b>');
LatexCmds.sf = LatexCmds.textsf =
  makeTextBlock('\\textsf', '<span class="sans-serif text"></span>');
LatexCmds.tt = LatexCmds.texttt =
  makeTextBlock('\\texttt', '<span class="monospace text"></span>');
LatexCmds.textsc =
  makeTextBlock('\\textsc', '<span style="font-variant:small-caps" class="text"></span>');
LatexCmds.uppercase =
  makeTextBlock('\\uppercase', '<span style="text-transform:uppercase" class="text"></span>');
LatexCmds.lowercase =
  makeTextBlock('\\lowercase', '<span style="text-transform:lowercase" class="text"></span>');

// input box to type a variety of LaTeX commands beginning with a backslash
function LatexCommandInput(replacedFragment) {
  this.init('\\');
  if (replacedFragment) {
    this.replacedFragment = replacedFragment.detach();
    this.isEmpty = function(){ return false; };
  }
}
_ = LatexCommandInput.prototype = new MathCommand;
_.html_template = ['<span class="latex-command-input">\\</span>'];
_.text_template = ['\\'];
_.placeCursor = function(cursor) { //TODO: better architecture, better place for this to be done, and more cleanly
  this.cursor = cursor.appendTo(this.firstChild);
  if (this.replacedFragment)
    this.jQ =
      this.jQ.add(this.replacedFragment.jQ.addClass('blur').bind(
        'mousedown mousemove', //FIXME: is monkey-patching the mousedown and mousemove handlers the right way to do this?
        function(e) {
          $(e.target = this.nextSibling).trigger(e);
          return false;
        }
      ).insertBefore(this.jQ));
};
_.latex = function() {
  return '\\' + this.firstChild.latex() + ' ';
};
_.keydown = function(e) {
  if (e.which === 9 || e.which === 13) { //tab or enter
    this.renderCommand();
    return false;
  }
  return this.parent.keydown(e);
};
_.textInput = function(ch) {
  if (ch.match(/[a-z]/i)) {
    this.cursor.deleteSelection();
    this.cursor.insertNew(new VanillaSymbol(ch));
    return;
  }
  this.renderCommand();
  if (ch === ' ' || (ch === '\\' && this.firstChild.isEmpty()))
    return;

  this.cursor.parent.textInput(ch);
};
_.renderCommand = function() {
  this.jQ = this.jQ.last();
  this.remove();
  if (this.next)
    this.cursor.insertBefore(this.next);
  else
    this.cursor.appendTo(this.parent);

  var latex = this.firstChild.latex();
  if (latex)
    this.cursor.insertCmd(latex, this.replacedFragment);
  else {
    var cmd = new VanillaSymbol('\\backslash ','\\');
    this.cursor.insertNew(cmd);
    if (this.replacedFragment)
      this.replacedFragment.remove();
  }
};

CharCmds['\\'] = LatexCommandInput;
  
function Binomial(replacedFragment) {
  this.init('\\binom', undefined, undefined, replacedFragment);
  this.jQ.wrapInner('<span class="array"></span>');
  this.blockjQ = this.jQ.children();
  this.bracketjQs =
    $('<span class="paren">(</span>').prependTo(this.jQ)
    .add( $('<span class="paren">)</span>').appendTo(this.jQ) );
}
_ = Binomial.prototype = new MathCommand;
_.html_template =
  ['<span class="block"></span>', '<span></span>', '<span></span>'];
_.text_template = ['choose(',',',')'];
_.redraw = Bracket.prototype.redraw;
LatexCmds.binom = LatexCmds.binomial = Binomial;

function Choose() {
  Binomial.apply(this, arguments);
}
_ = Choose.prototype = new Binomial;
_.placeCursor = LiveFraction.prototype.placeCursor;

LatexCmds.choose = Choose;

function Vector(replacedFragment) {
  this.init('\\vector', undefined, undefined, replacedFragment);
}
_ = Vector.prototype = new MathCommand;
_.html_template = ['<span class="array"></span>', '<span></span>'];
_.latex = function() {
  return '\\begin{matrix}' + this.foldChildren([], function(latex, child) {
    latex.push(child.latex());
    return latex;
  }).join('\\\\') + '\\end{matrix}';
};
_.text = function() {
  return '[' + this.foldChildren([], function(text, child) {
    text.push(child.text());
    return text;
  }).join() + ']';
}
_.placeCursor = function(cursor) {
  this.cursor = cursor.appendTo(this.firstChild);
};
_.keydown = function(e) {
  var currentBlock = this.cursor.parent;

  if (currentBlock.parent === this) {
    if (e.which === 13) { //enter
      var newBlock = new MathBlock;
      newBlock.parent = this;
      newBlock.jQ = $('<span></span>')
        .data(jQueryDataKey, {block: newBlock})
        .insertAfter(currentBlock.jQ);
      if (currentBlock.next)
        currentBlock.next.prev = newBlock;
      else
        this.lastChild = newBlock;

      newBlock.next = currentBlock.next;
      currentBlock.next = newBlock;
      newBlock.prev = currentBlock;
      this.cursor.appendTo(newBlock).redraw();
      return false;
    }
    else if (e.which === 9 && !e.shiftKey && !currentBlock.next) { //tab
      if (currentBlock.isEmpty()) {
        if (currentBlock.prev) {
          this.cursor.insertAfter(this);
          delete currentBlock.prev.next;
          this.lastChild = currentBlock.prev;
          currentBlock.jQ.remove();
          this.cursor.redraw();
          return false;
        }
        else
          return this.parent.keydown(e);
      }

      var newBlock = new MathBlock;
      newBlock.parent = this;
      newBlock.jQ = $('<span></span>').data(jQueryDataKey, {block: newBlock}).appendTo(this.jQ);
      this.lastChild = newBlock;
      currentBlock.next = newBlock;
      newBlock.prev = currentBlock;
      this.cursor.appendTo(newBlock).redraw();
      return false;
    }
    else if (e.which === 8) { //backspace
      if (currentBlock.isEmpty()) {
        if (currentBlock.prev) {
          this.cursor.appendTo(currentBlock.prev)
          currentBlock.prev.next = currentBlock.next;
        }
        else {
          this.cursor.insertBefore(this);
          this.firstChild = currentBlock.next;
        }

        if (currentBlock.next)
          currentBlock.next.prev = currentBlock.prev;
        else
          this.lastChild = currentBlock.prev;

        currentBlock.jQ.remove();
        if (this.isEmpty())
          this.cursor.deleteForward();
        else
          this.cursor.redraw();

        return false;
      }
      else if (!this.cursor.prev)
        return false;
    }
  }
  return this.parent.keydown(e);
};

LatexCmds.vector = Vector;

LatexCmds.editable = proto(RootMathCommand, function() {
  this.init('\\editable');
  createRoot(this.jQ, this.firstChild, false, true);
  var cursor;
  this.placeCursor = function(c) { cursor = c.appendTo(this.firstChild); };
  this.firstChild.blur = function() {
    if (cursor.prev !== this.parent) return; //when cursor is inserted after editable, append own cursor FIXME HACK
    delete this.blur;
    this.cursor.appendTo(this);
    MathBlock.prototype.blur.call(this);
  };
  this.latex = function(){ return this.firstChild.latex(); };
  this.text = function(){ return this.firstChild.text(); };
});

/**********************************
 * Symbols and Special Characters
 *********************************/

function Variable(ch, html) {
  if (ch == 'f') {
	  Symbol.call(this, ch, '<var class="florin">f</var>');
  } else {
	  Symbol.call(this, ch, '<var>'+(html || ch)+'</var>');
  }
}
_ = Variable.prototype = new Symbol;
_.insertAt = function(cursor) {
  //want the longest possible autocommand, so assemble longest series of letters (Variables) first
  var cmd = this.cmd;
  for (var i = 0, prev = cursor.prev; i < MAX_AUTOCMD_LEN - 1 && prev && prev instanceof Variable; i += 1, prev = prev.prev)
    cmd = prev.cmd + cmd;
  //then test if there's an autocommand here, starting with the longest possible and slicing
  while (cmd.length) {
    if (AutoCmds.hasOwnProperty(cmd)) {
      for (var i = 1; i < cmd.length; i += 1) cursor.backspace();
      cursor.insertNew(new LatexCmds[cmd](undefined, cmd));
      return;
    }
    cmd = cmd.slice(1);
  }
  MathCommand.prototype.insertAt.apply(this, arguments);
};
_.respace = function() {
  //TODO: in better architecture, should be done in createBefore and backspace
  //respace is called too often, inefficient

  //want the longest possible autocommand, so assemble longest series of letters (Variables)
  var cmd = this.cmd;
  for (var prev = this.prev; prev instanceof Variable; prev = prev.prev)
    cmd = prev.cmd + cmd;
  for (var next = this.next; next instanceof Variable; next = next.next)
    cmd += next.cmd;

  //removeClass from all the things before figuring out what's an autocmd, if any
  (new MathFragment(this.parent, prev, next)).each(function(el) {
    el.jQ.removeClass('un-italicized last');
    delete el.isFirstLetter;
    delete el.isLastLetter;
  });

  //test if there's an autocommand here, going through substrings from longest to shortest
  outer: for (var i = 0, first = prev.next || this.parent.firstChild; i < cmd.length; i += 1, first = first.next) {
    for (var len = min(MAX_UNITALICIZED_LEN, cmd.length - i); len > 0; len -= 1) {
      if (UnItalicizedCmds.hasOwnProperty(cmd.slice(i, i + len))) {
        first.isFirstLetter = true;
        for (var j = 0, letter = first; j < len; j += 1, letter = letter.next) {
          letter.jQ.addClass('un-italicized');
          var last = letter;
        }
        last.isLastLetter = true;
        if (!(last.next instanceof SupSub || last.next instanceof Bracket))
          last.jQ.addClass('last');
        i += len - 1;
        first = last;
        continue outer;
      }
    }
  }
};
_.latex = function() {
  if (this.isFirstLetter)
    return '\\' + this.cmd;
  else if (this.isLastLetter)
    return this.cmd + ' ';
  return this.cmd;
};
_.text = function() {
  var text = this.cmd;
  if (this.prev && !(this.prev instanceof Variable)
	  && !(this.prev.cmd === ' ')
      && !(this.prev instanceof BinaryOperator))
    text = '*' + text;

  // skip spaces
  var nex = this;
  while ((nex.next) && (nex.next.cmd === ' '))
	nex = nex.next;

  if (nex.next && !(nex.next instanceof BinaryOperator)
      && !(nex.jQ.hasClass('un-italicized'))
      && !(nex.next.cmd === '(')
      && !(nex.next.cmd === '\\left(')
      && !(nex.next.cmd === '^')) {
    text += '*';
  }

  return text;
};

function UnItalicized(replacedFragment, fn) {
  this.cmd = fn;
}
_ = UnItalicized.prototype = new Symbol;
_.insertAt = function(cursor) {
  cursor.writeLatex(this.cmd).show();
};
//backslashless commands, words where adjacent letters (Variables)
//that form them automatically are turned into commands
var UnItalicizedCmds = {
  // GeoGebra+MathQuill
  erf: 1,
  Erf: 1,
  psi: 1,
  polygamma: 1,
  ln: 1,
  log: 1,
  Ln: 1,
  ld: 1,
  log2: 1,
  lg: 1,
  log10: 1,
  beta: 1,
  Beta: 1,
  betaRegularized: 1,
  ibeta: 1,
  gamma: 1,
  igamma: 1,
  Gamma: 1,
  gammaRegularized: 1,
  cosIntegral: 1,
  CosIntegral: 1,
  sinIntegral: 1,
  SinIntegral: 1,
  expIntegral: 1,
  ExpIntegral: 1,
  gGbInTeGrAl: 1,
  gGbSuBsTiTuTiOn: 1,
  arbint: 1,
  arbconst: 1,
  arbcomplex: 1,
  cbrt: 1,
  Cbrt: 1,	
  abs: 1,
  Abs: 1,
  sgn: 1,
  sign: 1,
  Sign: 1,
  floor: 1,
  Floor: 1,
  ceil: 1,
  Ceil: 1,
  round: 1,
  Round: 1,
  conjugate: 1,
  Conjugate: 1,
  arg: 1,
  Arg: 1,
  random: 1,
  exp: 1,
  Exp: 1,
  x: 1,
  y: 1

  // MathQuill
  //min: 1,
  //nCr: 1,
  //nPr: 1,
  //gcd: 1,
  //lcm: 1,
  //max: 1,
  //mod: 1,
  //lcm: 1,
  //gcd: 1,
  //gcf: 1
  //line: 1,
  //polygon: 1,
  //dottedline: 1,
  //dottedpolygon:1,
  //stroke:1,
  //fill:1,
}, MAX_UNITALICIZED_LEN = 16, AutoCmds = {
  // GeoGebra+MathQuill
  sqrt: 1,
  Sqrt: 1,
  nthroot: 2,
  nroot: 2,

  // MathQuill
  //sum: 1,
  pi: 1
  //theta: 1,
  //int: 1
}, MAX_AUTOCMD_LEN = 7;

(function() {
  var trigs = {
    sin: 1, cos: 1, tan: 1,
    Sin: 1, Cos: 1, Tan: 1};
  for (var trig in trigs) {
    UnItalicizedCmds[trig] =
    UnItalicizedCmds['arc'+trig] =
    UnItalicizedCmds['Arc'+trig] =
    UnItalicizedCmds['a'+trig] =
    UnItalicizedCmds['A'+trig] =
    UnItalicizedCmds['ar'+trig] =
    UnItalicizedCmds[trig+'h'] =
    UnItalicizedCmds['arc'+trig+'h'] =
    UnItalicizedCmds['Arc'+trig+'h'] =
    UnItalicizedCmds['a'+trig+'h'] =
    UnItalicizedCmds['A'+trig+'h'] =
    UnItalicizedCmds['ar'+trig+'h'] = 1;
  }
 
  trigs = {
	sec: 1, cosec: 1, csc: 1,
	Sec: 1, Cosec: 1, Csc: 1,
	cotan: 1, cot: 1, ctg: 1,
	Cotan: 1, Cot: 1, Ctg: 1};
  for (var trig in trigs) {
    UnItalicizedCmds[trig] =
    UnItalicizedCmds[trig+'h'] = 1;
  }
  trigs = { tan2: 1, Tan2: 1 };
  for (var trig in trigs) {
    UnItalicizedCmds['a'+trig] =
    UnItalicizedCmds['A'+trig] =
    UnItalicizedCmds['ar'+trig] =
    UnItalicizedCmds['Arc'+trig] =
    UnItalicizedCmds['arc'+trig] = 1;
  }

  for (var fn in UnItalicizedCmds)
    LatexCmds[fn] = UnItalicized;
}());


function VanillaSymbol(ch, html) {
  Symbol.call(this, ch, '<span>'+(html || ch)+'</span>');
}
VanillaSymbol.prototype = Symbol.prototype;

LatexCmds.lbrace = CharCmds['{'] =
	bind(VanillaSymbol, '\\lbrace ', '{');
LatexCmds.rbrace = CharCmds['}'] =
	bind(VanillaSymbol, '\\rbrace ', '}');

CharCmds[' '] = bind(VanillaSymbol, ' ', ' ');

LatexCmds.prime = CharCmds["'"] = bind(VanillaSymbol, "'", '&prime;');

function NonSymbolaSymbol(ch, html) { //does not use Symbola font
  Symbol.call(this, ch, '<span class="nonSymbola">'+(html || ch)+'</span>');
}
NonSymbolaSymbol.prototype = Symbol.prototype;

LatexCmds['@'] = NonSymbolaSymbol;
LatexCmds['&'] = bind(NonSymbolaSymbol, '\\&', '&');
LatexCmds['%'] = bind(NonSymbolaSymbol, '\\%', '%');

//the following are all Greek to me, but this helped a lot: http://www.ams.org/STIX/ion/stixsig03.html

//lowercase Greek letter variables
LatexCmds.alpha =
LatexCmds.beta =
LatexCmds.gamma =
LatexCmds.delta =
LatexCmds.zeta =
LatexCmds.eta =
LatexCmds.theta =
LatexCmds.iota =
LatexCmds.kappa =
LatexCmds.mu =
LatexCmds.nu =
LatexCmds.xi =
LatexCmds.rho =
LatexCmds.sigma =
LatexCmds.tau =
LatexCmds.chi =
LatexCmds.psi =
LatexCmds.omega = proto(Symbol, function(replacedFragment, latex) {
  Variable.call(this,'\\'+latex+' ','&'+latex+';');
});

//why can't anybody FUCKING agree on these
LatexCmds.phi = //W3C or Unicode?
  bind(Variable,'\\phi ','&#981;');

LatexCmds.phiv = //Elsevier and 9573-13
LatexCmds.varphi = //AMS and LaTeX
  bind(Variable,'\\varphi ','&phi;');

LatexCmds.epsilon = //W3C or Unicode?
  bind(Variable,'\\epsilon ','&#1013;');

LatexCmds.epsiv = //Elsevier and 9573-13
LatexCmds.varepsilon = //AMS and LaTeX
  bind(Variable,'\\varepsilon ','&epsilon;');

LatexCmds.piv = //W3C/Unicode and Elsevier and 9573-13
LatexCmds.varpi = //AMS and LaTeX
  bind(Variable,'\\varpi ','&piv;');

LatexCmds.sigmaf = //W3C/Unicode
LatexCmds.sigmav = //Elsevier
LatexCmds.varsigma = //LaTeX
  bind(Variable,'\\varsigma ','&sigmaf;');

LatexCmds.thetav = //Elsevier and 9573-13
LatexCmds.vartheta = //AMS and LaTeX
LatexCmds.thetasym = //W3C/Unicode
  bind(Variable,'\\vartheta ','&thetasym;');

LatexCmds.upsilon = //AMS and LaTeX and W3C/Unicode
LatexCmds.upsi = //Elsevier and 9573-13
  bind(Variable,'\\upsilon ','&upsilon;');

//these aren't even mentioned in the HTML character entity references
LatexCmds.gammad = //Elsevier
LatexCmds.Gammad = //9573-13 -- WTF, right? I dunno if this was a typo in the reference (see above)
LatexCmds.digamma = //LaTeX
  bind(Variable,'\\digamma ','&#989;');

LatexCmds.kappav = //Elsevier
LatexCmds.varkappa = //AMS and LaTeX
  bind(Variable,'\\varkappa ','&#1008;');

LatexCmds.rhov = //Elsevier and 9573-13
LatexCmds.varrho = //AMS and LaTeX
  bind(Variable,'\\varrho ','&#1009;');

//Greek constants, look best in un-italicised Times New Roman
LatexCmds.pi = LatexCmds['π'] = bind(NonSymbolaSymbol,'\\pi ','&pi;');
LatexCmds.theta = LatexCmds['θ'] = bind(NonSymbolaSymbol,'\\theta ','&theta;');
LatexCmds.lambda = bind(NonSymbolaSymbol,'\\lambda ','&lambda;');

//uppercase greek letters

LatexCmds.Upsilon = //LaTeX
LatexCmds.Upsi = //Elsevier and 9573-13
LatexCmds.upsih = //W3C/Unicode "upsilon with hook"
LatexCmds.Upsih = //'cos it makes sense to me
  bind(Symbol,'\\Upsilon ','<var style="font-family: serif">&upsih;</var>'); //Symbola's 'upsilon with a hook' is a capital Y without hooks :(

LatexCmds.Gamma =
LatexCmds.Delta =
LatexCmds.Theta =
LatexCmds.Lambda =
LatexCmds.Xi =
LatexCmds.Pi =
LatexCmds.Sigma =
LatexCmds.Phi =
LatexCmds.Psi =
LatexCmds.Omega =

//other symbols with the same LaTeX command and HTML character entity reference
LatexCmds.forall = proto(Symbol, function(replacedFragment, latex) {
  VanillaSymbol.call(this,'\\'+latex+' ','&'+latex+';');
});

function BinaryOperator(cmd, html, text) {
  Symbol.call(this, cmd, '<span class="binary-operator">'+html+'</span>', text);
}
_ = BinaryOperator.prototype = new Symbol; //so instanceof will work
_.insertAt = function(cursor) {
  var cmd = cursor.prev.cmd + this.cmd;
  if (cmd === '<=')
    cursor.backspace().insertNew(new BinaryOperator('\\le ', '&le;'));
  else if (cmd === '>=')
    cursor.backspace().insertNew(new BinaryOperator('\\ge ', '&ge;'));
  else
    MathCommand.prototype.insertAt.apply(this, arguments);
};

function PlusMinus(cmd, html) {
  VanillaSymbol.apply(this, arguments);
}
_ = PlusMinus.prototype = new BinaryOperator; //so instanceof will work
_.respace = function() {
  if (!this.prev) {
    this.jQ[0].className = '';
  }
  else if (
    this.prev instanceof BinaryOperator &&
    this.next && !(this.next instanceof BinaryOperator)
  ) {
    this.jQ[0].className = 'unary-operator';
  }
  else {
    this.jQ[0].className = 'binary-operator';
  }
  return this;
};

LatexCmds['+'] = bind(PlusMinus, '+', '+');
//yes, these are different dashes, I think one is an en dash and the other is a hyphen
LatexCmds['–'] = LatexCmds['-'] = bind(PlusMinus, '-', '&minus;');
LatexCmds['±'] = LatexCmds.pm = LatexCmds.plusmn = LatexCmds.plusminus =
  bind(PlusMinus,'\\pm ','&plusmn;');
LatexCmds.mp = LatexCmds.mnplus = LatexCmds.minusplus =
  bind(PlusMinus,'\\mp ','&#8723;');

CharCmds['*'] = LatexCmds.sdot = LatexCmds.cdot =
  bind(BinaryOperator, '\\cdot ', '&middot;', '*');
//semantically should be &sdot;, but &middot; looks better

LatexCmds['='] = bind(BinaryOperator, '=', '=');
LatexCmds['<'] = bind(BinaryOperator, '<', '&lt;');
LatexCmds['>'] = bind(BinaryOperator, '>', '&gt;');

LatexCmds.notin =
LatexCmds.sim =
LatexCmds.cong =
LatexCmds.equiv =
LatexCmds.oplus =
LatexCmds.otimes = proto(BinaryOperator, function(replacedFragment, latex) {
  BinaryOperator.call(this, '\\'+latex+' ', '&'+latex+';');
});

LatexCmds.times = proto(BinaryOperator, function(replacedFragment, latex) {
  BinaryOperator.call(this, '\\times ', '&times;', '[x]')
});

LatexCmds['÷'] = LatexCmds.div = LatexCmds.divide = LatexCmds.divides =
  bind(BinaryOperator,'\\div ','&divide;', '[/]');

LatexCmds['≠'] = LatexCmds.ne = LatexCmds.neq = bind(BinaryOperator,'\\ne ','&ne;');

LatexCmds.ast = LatexCmds.star = LatexCmds.loast = LatexCmds.lowast =
  bind(BinaryOperator,'\\ast ','&lowast;');
  //case 'there4 = // a special exception for this one, perhaps?
LatexCmds.therefor = LatexCmds.therefore =
  bind(BinaryOperator,'\\therefore ','&there4;');

LatexCmds.cuz = // l33t
LatexCmds.because = bind(BinaryOperator,'\\because ','&#8757;');

LatexCmds.prop = LatexCmds.propto = bind(BinaryOperator,'\\propto ','&prop;');

LatexCmds['≈'] = LatexCmds.asymp = LatexCmds.approx = bind(BinaryOperator,'\\approx ','&asymp;');

LatexCmds.lt = bind(BinaryOperator,'<','&lt;');

LatexCmds.gt = bind(BinaryOperator,'>','&gt;');

LatexCmds['≤'] = LatexCmds.le = LatexCmds.leq = bind(BinaryOperator,'\\le ','&le;');

LatexCmds['≥'] = LatexCmds.ge = LatexCmds.geq = bind(BinaryOperator,'\\ge ','&ge;');

LatexCmds.isin = LatexCmds['in'] = bind(BinaryOperator,'\\in ','&isin;');

LatexCmds.ni = LatexCmds.contains = bind(BinaryOperator,'\\ni ','&ni;');

LatexCmds.notni = LatexCmds.niton = LatexCmds.notcontains = LatexCmds.doesnotcontain =
  bind(BinaryOperator,'\\not\\ni ','&#8716;');

LatexCmds.sub = LatexCmds.subset = bind(BinaryOperator,'\\subset ','&sub;');

LatexCmds.sup = LatexCmds.supset = LatexCmds.superset =
  bind(BinaryOperator,'\\supset ','&sup;');

LatexCmds.nsub = LatexCmds.notsub =
LatexCmds.nsubset = LatexCmds.notsubset =
  bind(BinaryOperator,'\\not\\subset ','&#8836;');

LatexCmds.nsup = LatexCmds.notsup =
LatexCmds.nsupset = LatexCmds.notsupset =
LatexCmds.nsuperset = LatexCmds.notsuperset =
  bind(BinaryOperator,'\\not\\supset ','&#8837;');

LatexCmds.sube = LatexCmds.subeq = LatexCmds.subsete = LatexCmds.subseteq =
  bind(BinaryOperator,'\\subseteq ','&sube;');

LatexCmds.supe = LatexCmds.supeq =
LatexCmds.supsete = LatexCmds.supseteq =
LatexCmds.supersete = LatexCmds.superseteq =
  bind(BinaryOperator,'\\supseteq ','&supe;');

LatexCmds.nsube = LatexCmds.nsubeq =
LatexCmds.notsube = LatexCmds.notsubeq =
LatexCmds.nsubsete = LatexCmds.nsubseteq =
LatexCmds.notsubsete = LatexCmds.notsubseteq =
  bind(BinaryOperator,'\\not\\subseteq ','&#8840;');

LatexCmds.nsupe = LatexCmds.nsupeq =
LatexCmds.notsupe = LatexCmds.notsupeq =
LatexCmds.nsupsete = LatexCmds.nsupseteq =
LatexCmds.notsupsete = LatexCmds.notsupseteq =
LatexCmds.nsupersete = LatexCmds.nsuperseteq =
LatexCmds.notsupersete = LatexCmds.notsuperseteq =
  bind(BinaryOperator,'\\not\\supseteq ','&#8841;');


//sum, product, coproduct, integral
function BigSymbol(ch, html) {
  Symbol.call(this, ch, '<span class="large-operator"><big>'+html+'</big></span>');
}
_ = BigSymbol.prototype = new Symbol; //so instanceof will work
_.isEmpty = MathCommand.prototype.isEmpty;
_.insertAt = function(cursor, isWriteLatex) {
  //FIXME HACK
  if (this.cmd === '\\sum ')
    if (isWriteLatex)
      this.placeCursor = function(cursor) {
        this.cursor = cursor;
      };
    else
      this.placeCursor = function(cursor) {
        this.cursor = cursor.writeLatex('^{}_{n=}').appendTo(this.firstChild).show();
      };
  MathCommand.prototype.insertAt.apply(this, arguments);
};
_.latex = function() {
  var fromLatex = this.firstChild ? '_'+simplify(this.firstChild.latex()) : '',
    toLatex = this.lastChild ? '^'+simplify(this.lastChild.latex()) : '';
  return this.cmd + fromLatex + toLatex;

  function simplify(latex) {
    return latex.length === 1 ? latex : '{' + (latex || ' ') + '}';
  }
};
_.keydown = function(e) {
  if (this.cursor.parent.parent !== this || e.ctrlKey || e.metaKey)
    return this.parent.keydown(e);
  //e.which === 38 <=> Up key
  else if (e.which === 38) {
    if (this.cursor.parent === this.firstChild)
      this.cursor.clearSelection()[this.cursor.prev || !this.cursor.next ? 'insertAfter' : 'insertBefore'](this);
  }
  //e.which === 40 <=> Down key
  else if (e.which === 40) {
    if (this.cursor.parent === this.lastChild)
      this.cursor.clearSelection()[this.cursor.prev || !this.cursor.next ? 'insertAfter' : 'insertBefore'](this);
  }
  else
    return this.parent.keydown(e);
};

LatexCmds['∑'] = LatexCmds.sum = LatexCmds.summation = bind(BigSymbol,'\\sum ','&sum;');
LatexCmds['∏'] = LatexCmds.prod = LatexCmds.product = bind(BigSymbol,'\\prod ','&prod;');
LatexCmds.coprod = LatexCmds.coproduct = bind(BigSymbol,'\\coprod ','&#8720;');
LatexCmds['∫'] = LatexCmds['int'] = LatexCmds.integral = proto(BigSymbol, function() {
  Symbol.call(this, '\\int ', '<big>&int;</big>');
});



//the canonical sets of numbers
LatexCmds.N = LatexCmds.naturals = LatexCmds.Naturals =
  bind(VanillaSymbol,'\\mathbb{N}','&#8469;');

LatexCmds.P =
LatexCmds.primes = LatexCmds.Primes =
LatexCmds.projective = LatexCmds.Projective =
LatexCmds.probability = LatexCmds.Probability =
  bind(VanillaSymbol,'\\mathbb{P}','&#8473;');

LatexCmds.Z = LatexCmds.integers = LatexCmds.Integers =
  bind(VanillaSymbol,'\\mathbb{Z}','&#8484;');

LatexCmds.Q = LatexCmds.rationals = LatexCmds.Rationals =
  bind(VanillaSymbol,'\\mathbb{Q}','&#8474;');

LatexCmds.R = LatexCmds.reals = LatexCmds.Reals =
  bind(VanillaSymbol,'\\mathbb{R}','&#8477;');

LatexCmds.C =
LatexCmds.complex = LatexCmds.Complex =
LatexCmds.complexes = LatexCmds.Complexes =
LatexCmds.complexplane = LatexCmds.Complexplane = LatexCmds.ComplexPlane =
  bind(VanillaSymbol,'\\mathbb{C}','&#8450;');

LatexCmds.H = LatexCmds.Hamiltonian = LatexCmds.quaternions = LatexCmds.Quaternions =
  bind(VanillaSymbol,'\\mathbb{H}','&#8461;');

//spacing
LatexCmds.quad = LatexCmds.emsp = bind(VanillaSymbol,'\\quad ','    ');
LatexCmds.qquad = bind(VanillaSymbol,'\\qquad ','        ');
/* spacing special characters, gonna have to implement this in LatexCommandInput::textInput somehow
case ',':
  return new VanillaSymbol('\\, ',' ');
case ':':
  return new VanillaSymbol('\\: ','  ');
case ';':
  return new VanillaSymbol('\\; ','   ');
case '!':
  return new Symbol('\\! ','<span style="margin-right:-.2em"></span>');
*/

//binary operators
LatexCmds.diamond = bind(VanillaSymbol, '\\diamond ', '&#9671;');
LatexCmds.bigtriangleup = bind(VanillaSymbol, '\\bigtriangleup ', '&#9651;');
LatexCmds.ominus = bind(VanillaSymbol, '\\ominus ', '&#8854;');
LatexCmds.uplus = bind(VanillaSymbol, '\\uplus ', '&#8846;');
LatexCmds.bigtriangledown = bind(VanillaSymbol, '\\bigtriangledown ', '&#9661;');
LatexCmds.sqcap = bind(VanillaSymbol, '\\sqcap ', '&#8851;');
LatexCmds.triangleleft = bind(VanillaSymbol, '\\triangleleft ', '&#8882;');
LatexCmds.sqcup = bind(VanillaSymbol, '\\sqcup ', '&#8852;');
LatexCmds.triangleright = bind(VanillaSymbol, '\\triangleright ', '&#8883;');
LatexCmds.odot = bind(VanillaSymbol, '\\odot ', '&#8857;');
LatexCmds.bigcirc = bind(VanillaSymbol, '\\bigcirc ', '&#9711;');
LatexCmds.dagger = bind(VanillaSymbol, '\\dagger ', '&#0134;');
LatexCmds.ddagger = bind(VanillaSymbol, '\\ddagger ', '&#135;');
LatexCmds.wr = bind(VanillaSymbol, '\\wr ', '&#8768;');
LatexCmds.amalg = bind(VanillaSymbol, '\\amalg ', '&#8720;');

//relationship symbols
LatexCmds.models = bind(VanillaSymbol, '\\models ', '&#8872;');
LatexCmds.prec = bind(VanillaSymbol, '\\prec ', '&#8826;');
LatexCmds.succ = bind(VanillaSymbol, '\\succ ', '&#8827;');
LatexCmds.preceq = bind(VanillaSymbol, '\\preceq ', '&#8828;');
LatexCmds.succeq = bind(VanillaSymbol, '\\succeq ', '&#8829;');
LatexCmds.simeq = bind(VanillaSymbol, '\\simeq ', '&#8771;');
LatexCmds.mid = bind(VanillaSymbol, '\\mid ', '&#8739;');
//LatexCmds.ll = bind(VanillaSymbol, '\\ll ', '&#8810;');//disturbing in GeoGebraWeb
//LatexCmds.gg = bind(VanillaSymbol, '\\gg ', '&#8811;');//disturbing in GeoGebraWeb
LatexCmds.parallel = bind(VanillaSymbol, '\\parallel ', '&#8741;');
LatexCmds.bowtie = bind(VanillaSymbol, '\\bowtie ', '&#8904;');
LatexCmds.sqsubset = bind(VanillaSymbol, '\\sqsubset ', '&#8847;');
LatexCmds.sqsupset = bind(VanillaSymbol, '\\sqsupset ', '&#8848;');
LatexCmds.smile = bind(VanillaSymbol, '\\smile ', '&#8995;');
LatexCmds.sqsubseteq = bind(VanillaSymbol, '\\sqsubseteq ', '&#8849;');
LatexCmds.sqsupseteq = bind(VanillaSymbol, '\\sqsupseteq ', '&#8850;');
LatexCmds.doteq = bind(VanillaSymbol, '\\doteq ', '&#8784;');
LatexCmds.frown = bind(VanillaSymbol, '\\frown ', '&#8994;');
LatexCmds.vdash = bind(VanillaSymbol, '\\vdash ', '&#8870;');
LatexCmds.dashv = bind(VanillaSymbol, '\\dashv ', '&#8867;');

//arrows
LatexCmds.longleftarrow = bind(VanillaSymbol, '\\longleftarrow ', '&#8592;');
LatexCmds.longrightarrow = bind(VanillaSymbol, '\\longrightarrow ', '&#8594;');
LatexCmds.Longleftarrow = bind(VanillaSymbol, '\\Longleftarrow ', '&#8656;');
LatexCmds.Longrightarrow = bind(VanillaSymbol, '\\Longrightarrow ', '&#8658;');
LatexCmds.longleftrightarrow = bind(VanillaSymbol, '\\longleftrightarrow ', '&#8596;');
LatexCmds.updownarrow = bind(VanillaSymbol, '\\updownarrow ', '&#8597;');
LatexCmds.Longleftrightarrow = bind(VanillaSymbol, '\\Longleftrightarrow ', '&#8660;');
LatexCmds.Updownarrow = bind(VanillaSymbol, '\\Updownarrow ', '&#8661;');
LatexCmds.mapsto = bind(VanillaSymbol, '\\mapsto ', '&#8614;');
LatexCmds.nearrow = bind(VanillaSymbol, '\\nearrow ', '&#8599;');
LatexCmds.hookleftarrow = bind(VanillaSymbol, '\\hookleftarrow ', '&#8617;');
LatexCmds.hookrightarrow = bind(VanillaSymbol, '\\hookrightarrow ', '&#8618;');
LatexCmds.searrow = bind(VanillaSymbol, '\\searrow ', '&#8600;');
LatexCmds.leftharpoonup = bind(VanillaSymbol, '\\leftharpoonup ', '&#8636;');
LatexCmds.rightharpoonup = bind(VanillaSymbol, '\\rightharpoonup ', '&#8640;');
LatexCmds.swarrow = bind(VanillaSymbol, '\\swarrow ', '&#8601;');
LatexCmds.leftharpoondown = bind(VanillaSymbol, '\\leftharpoondown ', '&#8637;');
LatexCmds.rightharpoondown = bind(VanillaSymbol, '\\rightharpoondown ', '&#8641;');
LatexCmds.nwarrow = bind(VanillaSymbol, '\\nwarrow ', '&#8598;');

//Misc
LatexCmds.space = bind(VanillaSymbol, '\\space ', '&nbsp;');
LatexCmds.ldots = bind(VanillaSymbol, '\\ldots ', '&#8230;');
LatexCmds.cdots = bind(VanillaSymbol, '\\cdots ', '&#8943;');
LatexCmds.vdots = bind(VanillaSymbol, '\\vdots ', '&#8942;');
LatexCmds.ddots = bind(VanillaSymbol, '\\ddots ', '&#8944;');
LatexCmds.surd = bind(VanillaSymbol, '\\surd ', '&#8730;');
LatexCmds.triangle = bind(VanillaSymbol, '\\triangle ', '&#9653;');
LatexCmds.ell = bind(VanillaSymbol, '\\ell ', '&#8467;');
LatexCmds.top = bind(VanillaSymbol, '\\top ', '&#8868;');
LatexCmds.flat = bind(VanillaSymbol, '\\flat ', '&#9837;');
LatexCmds.natural = bind(VanillaSymbol, '\\natural ', '&#9838;');
LatexCmds.sharp = bind(VanillaSymbol, '\\sharp ', '&#9839;');
LatexCmds.wp = bind(VanillaSymbol, '\\wp ', '&#8472;');
LatexCmds.bot = bind(VanillaSymbol, '\\bot ', '&#8869;');
LatexCmds.clubsuit = bind(VanillaSymbol, '\\clubsuit ', '&#9827;');
LatexCmds.diamondsuit = bind(VanillaSymbol, '\\diamondsuit ', '&#9826;');
LatexCmds.heartsuit = bind(VanillaSymbol, '\\heartsuit ', '&#9825;');
LatexCmds.spadesuit = bind(VanillaSymbol, '\\spadesuit ', '&#9824;');

//variable-sized
LatexCmds.oint = bind(VanillaSymbol, '\\oint ', '&#8750;');
LatexCmds.bigcap = bind(VanillaSymbol, '\\bigcap ', '&#8745;');
LatexCmds.bigcup = bind(VanillaSymbol, '\\bigcup ', '&#8746;');
LatexCmds.bigsqcup = bind(VanillaSymbol, '\\bigsqcup ', '&#8852;');
LatexCmds.bigvee = bind(VanillaSymbol, '\\bigvee ', '&#8744;');
LatexCmds.bigwedge = bind(VanillaSymbol, '\\bigwedge ', '&#8743;');
LatexCmds.bigodot = bind(VanillaSymbol, '\\bigodot ', '&#8857;');
LatexCmds.bigotimes = bind(VanillaSymbol, '\\bigotimes ', '&#8855;');
LatexCmds.bigoplus = bind(VanillaSymbol, '\\bigoplus ', '&#8853;');
LatexCmds.biguplus = bind(VanillaSymbol, '\\biguplus ', '&#8846;');

//delimiters
LatexCmds.lfloor = bind(VanillaSymbol, '\\lfloor ', '&#8970;');
LatexCmds.rfloor = bind(VanillaSymbol, '\\rfloor ', '&#8971;');
LatexCmds.lceil = bind(VanillaSymbol, '\\lceil ', '&#8968;');
LatexCmds.rceil = bind(VanillaSymbol, '\\rceil ', '&#8969;');
LatexCmds.slash = bind(VanillaSymbol, '\\slash ', '&#47;');
LatexCmds.opencurlybrace = bind(VanillaSymbol, '\\opencurlybrace ', '&#123;');
LatexCmds.closecurlybrace = bind(VanillaSymbol, '\\closecurlybrace ', '&#125;');

//various symbols

LatexCmds.caret = bind(VanillaSymbol,'\\caret ','^');
LatexCmds.underscore = bind(VanillaSymbol,'\\underscore ','_');
LatexCmds.backslash = bind(VanillaSymbol,'\\backslash ','\\');
LatexCmds.vert = bind(VanillaSymbol,'|');
LatexCmds.perp = LatexCmds.perpendicular = bind(VanillaSymbol,'\\perp ','&perp;');
LatexCmds.nabla = LatexCmds.del = bind(VanillaSymbol,'\\nabla ','&nabla;');
LatexCmds.hbar = bind(VanillaSymbol,'\\hbar ','&#8463;');

LatexCmds.AA = LatexCmds.Angstrom = LatexCmds.angstrom =
  bind(VanillaSymbol,'\\text\\AA ','&#8491;');

LatexCmds.ring = LatexCmds.circ = LatexCmds.circle =
  bind(VanillaSymbol,'\\circ ','&#8728;');

LatexCmds.bull = LatexCmds.bullet = bind(VanillaSymbol,'\\bullet ','&bull;');

LatexCmds.setminus = LatexCmds.smallsetminus =
  bind(VanillaSymbol,'\\setminus ','&#8726;');

LatexCmds.not = //bind(Symbol,'\\not ','<span class="not">/</span>');
LatexCmds['¬'] = LatexCmds.neg = bind(VanillaSymbol,'\\neg ','&not;');

LatexCmds['…'] = LatexCmds.dots = LatexCmds.ellip = LatexCmds.hellip =
LatexCmds.ellipsis = LatexCmds.hellipsis =
  bind(VanillaSymbol,'\\dots ','&hellip;');

LatexCmds.converges =
LatexCmds.darr = LatexCmds.dnarr = LatexCmds.dnarrow = LatexCmds.downarrow =
  bind(VanillaSymbol,'\\downarrow ','&darr;');

LatexCmds.dArr = LatexCmds.dnArr = LatexCmds.dnArrow = LatexCmds.Downarrow =
  bind(VanillaSymbol,'\\Downarrow ','&dArr;');

LatexCmds.diverges = LatexCmds.uarr = LatexCmds.uparrow =
  bind(VanillaSymbol,'\\uparrow ','&uarr;');

LatexCmds.uArr = LatexCmds.Uparrow = bind(VanillaSymbol,'\\Uparrow ','&uArr;');

LatexCmds.to = bind(BinaryOperator,'\\to ','&rarr;');

LatexCmds.rarr = LatexCmds.rightarrow = bind(VanillaSymbol,'\\rightarrow ','&rarr;');

LatexCmds.implies = bind(BinaryOperator,'\\Rightarrow ','&rArr;');

LatexCmds.rArr = LatexCmds.Rightarrow = bind(VanillaSymbol,'\\Rightarrow ','&rArr;');

LatexCmds.gets = bind(BinaryOperator,'\\gets ','&larr;');

LatexCmds.larr = LatexCmds.leftarrow = bind(VanillaSymbol,'\\leftarrow ','&larr;');

LatexCmds.impliedby = bind(BinaryOperator,'\\Leftarrow ','&lArr;');

LatexCmds.lArr = LatexCmds.Leftarrow = bind(VanillaSymbol,'\\Leftarrow ','&lArr;');

LatexCmds.harr = LatexCmds.lrarr = LatexCmds.leftrightarrow =
  bind(VanillaSymbol,'\\leftrightarrow ','&harr;');

LatexCmds.iff = bind(BinaryOperator,'\\Leftrightarrow ','&hArr;');

LatexCmds.hArr = LatexCmds.lrArr = LatexCmds.Leftrightarrow =
  bind(VanillaSymbol,'\\Leftrightarrow ','&hArr;');

LatexCmds.Re = LatexCmds.Real = LatexCmds.real = bind(VanillaSymbol,'\\Re ','&real;');

LatexCmds.Im = LatexCmds.imag =
LatexCmds.image = LatexCmds.imagin = LatexCmds.imaginary = LatexCmds.Imaginary =
  bind(VanillaSymbol,'\\Im ','&image;');

LatexCmds.part = LatexCmds.partial = bind(VanillaSymbol,'\\partial ','&part;');

LatexCmds.inf = LatexCmds.infin = LatexCmds.infty = LatexCmds.infinity =
  bind(VanillaSymbol,'\\infty ','&infin;');

LatexCmds.alef = LatexCmds.alefsym = LatexCmds.aleph = LatexCmds.alephsym =
  bind(VanillaSymbol,'\\aleph ','&alefsym;');

LatexCmds.xist = //LOL
LatexCmds.xists = LatexCmds.exist = LatexCmds.exists =
  bind(VanillaSymbol,'\\exists ','&exist;');

LatexCmds.and = LatexCmds.land = LatexCmds.wedge =
  bind(VanillaSymbol,'\\wedge ','&and;');

LatexCmds.or = LatexCmds.lor = LatexCmds.vee = bind(VanillaSymbol,'\\vee ','&or;');

LatexCmds.o = LatexCmds.O =
LatexCmds.empty = LatexCmds.emptyset =
LatexCmds.oslash = LatexCmds.Oslash =
LatexCmds.nothing = LatexCmds.varnothing =
  bind(BinaryOperator,'\\varnothing ','&empty;');

LatexCmds.cup = LatexCmds.union = bind(BinaryOperator,'\\cup ','&cup;');

LatexCmds.cap = LatexCmds.intersect = LatexCmds.intersection =
  bind(BinaryOperator,'\\cap ','&cap;');

LatexCmds.deg = LatexCmds.degree = bind(VanillaSymbol,'^\\circ ','&deg;');

LatexCmds.ang = LatexCmds.angle = bind(VanillaSymbol,'\\angle ','&ang;');

/********************************************
 * Cursor and Selection "singleton" classes
 *******************************************/

/* The main thing that manipulates the Math DOM. Makes sure to manipulate the
HTML DOM to match. */

/* Sort of singletons, since there should only be one per editable math
textbox, but any one HTML document can contain many such textboxes, so any one
JS environment could actually contain many instances. */

//A fake cursor in the fake textbox that the math is rendered in.
function Cursor(root) {
  this.parent = this.root = root;
  var jQ = this.jQ = this._jQ = $('<span class="cursor">&zwj;</span>');

  //closured for setInterval
  this.blink = function(){ jQ.toggleClass('blink'); }
}
_ = Cursor.prototype;
_.prev = 0;
_.next = 0;
_.parent = 0;
_.show = function() {
  this.jQ = this._jQ.removeClass('blink');
  if ('intervalId' in this) //already was shown, just restart interval
    clearInterval(this.intervalId);
  else { //was hidden and detached, insert this.jQ back into HTML DOM
    if (this.next) {
      if (this.selection && this.selection.prev === this.prev)
        this.jQ.insertBefore(this.selection.jQ);
      else
        this.jQ.insertBefore(this.next.jQ.first());
    }
    else
      this.jQ.appendTo(this.parent.jQ);
    this.parent.focus();
  }
  this.intervalId = setInterval(this.blink, 500);
  return this;
};
_.hide = function() {
  if ('intervalId' in this)
    clearInterval(this.intervalId);
  delete this.intervalId;
  this.jQ.detach();
  this.jQ = $();
  return this;
};
_.redraw = function() {
  for (var ancestor = this.parent; ancestor; ancestor = ancestor.parent)
    if (ancestor.redraw)
      ancestor.redraw();
};
_.insertAt = function(parent, prev, next) {
  var old_parent = this.parent;

  this.parent = parent;
  this.prev = prev;
  this.next = next;

  old_parent.blur(); //blur may need to know cursor's destination
};
_.insertBefore = function(el) {
  this.insertAt(el.parent, el.prev, el)
  this.parent.jQ.addClass('hasCursor');
  this.jQ.insertBefore(el.jQ.first());
  return this;
};
_.insertAfter = function(el) {
  this.insertAt(el.parent, el, el.next);
  this.parent.jQ.addClass('hasCursor');
  this.jQ.insertAfter(el.jQ.last());
  return this;
};
_.prependTo = function(el) {
  this.insertAt(el, 0, el.firstChild);
  if (el.textarea) //never insert before textarea
    this.jQ.insertAfter(el.textarea);
  else
    this.jQ.prependTo(el.jQ);
  el.focus();
  return this;
};
_.appendTo = function(el) {
  this.insertAt(el, el.lastChild, 0);
  this.jQ.appendTo(el.jQ);
  el.focus();
  return this;
};
_.hopLeft = function() {
  this.jQ.insertBefore(this.prev.jQ.first());
  this.next = this.prev;
  this.prev = this.prev.prev;
  return this;
};
_.hopRight = function() {
  this.jQ.insertAfter(this.next.jQ.last());
  this.prev = this.next;
  this.next = this.next.next;
  return this;
};
_.moveLeft = function() {
  if (this.selection)
    this.insertBefore(this.selection.prev.next || this.parent.firstChild).clearSelection();
  else {
    if (this.prev) {
      if (this.prev.cmd === '_' && this.prev.respaced) //FIXME HACKS
        this.appendTo(this.prev.prev.firstChild);
      else if (this.prev.cmd === '\\sum ')
        this.appendTo(this.prev.lastChild);
      else if (this.prev.firstChild)
        this.appendTo(this.prev.firstChild)
      else
        this.hopLeft();
    }
    else { //we're at the beginning of a block
      if (this.parent !== this.root)
        this.insertBefore(this.parent.parent);
      //else we're at the beginning of the root, so do nothing.
    }
  }
  return this.show();
};
_.moveRight = function() {
  if (this.selection)
    this.insertAfter(this.selection.next.prev || this.parent.lastChild).clearSelection();
  else {
    if (this.next) {
      if (this.next.cmd === '_' && this.next.next.respaced) //FIXME HACKS
        this.prependTo(this.next.next.firstChild);
      else if (this.next.cmd === '\\sum ')
        this.prependTo(this.next.lastChild);
      else if (this.next.firstChild)
        this.prependTo(this.next.firstChild)
      else
        this.hopRight();
    }
    else { //we're at the end of a block
      if (this.parent !== this.root)
        this.insertAfter(this.parent.parent);
      //else we're at the end of the root, so do nothing.
    }
  }
  return this.show();
};
_.seek = function(target, pageX, pageY) {
  var cursor = this.clearSelection();
  if (target.hasClass('empty')) {
    cursor.prependTo(target.data(jQueryDataKey).block);
    return cursor;
  }

  var data = target.data(jQueryDataKey);
  if (data) {
    //if clicked a symbol, insert at whichever side is closer
    if (data.cmd && !data.block) {
      if (target.outerWidth() > 2*(pageX - target.offset().left))
        cursor.insertBefore(data.cmd);
      else
        cursor.insertAfter(data.cmd);

      return cursor;
    }
  }
  //if no MathQuill data, try parent, if still no, forget it
  else {
    target = target.parent();
    data = target.data(jQueryDataKey);
    if (!data)
      data = {block: cursor.root};
  }

  if (data.cmd)
    cursor.insertAfter(data.cmd);
  else
    cursor.appendTo(data.block);

  //move cursor to position closest to click
  var dist = cursor.offset().left - pageX, prevDist;
  do {
    cursor.moveLeft();
    prevDist = dist;
    dist = cursor.offset().left - pageX;
  }
  while (dist > 0 && (cursor.prev || cursor.parent !== cursor.root));

  if (-dist > prevDist)
    cursor.moveRight();

  return cursor;
};
_.offset = function() {
  //in Opera 11.62, .getBoundingClientRect() and hence jQuery::offset()
  //returns all 0's on inline elements with negative margin-right (like
  //the cursor) at the end of their parent, so temporarily remove the
  //negative margin-right when calling jQuery::offset()
  //Opera bug DSK-360043
  //http://bugs.jquery.com/ticket/11523
  //https://github.com/jquery/jquery/pull/717
  var jQ = this.jQ.removeClass('cursor'),
    offset = jQ.offset();
  jQ.addClass('cursor');
  return offset;
};
_.writeLatex = function(latex) {
/* This didn't work...
  if (this.prev && this.next && this.prev.jQ && this.next.jQ &&
	  this.prev.jQ.hasClass('un-italicized') && this.next.jQ.hasClass('un-italicized')) {
	  var previous = this.prev;
	  var nextt = this.next;
	  this.selectLeft();
	  while (previous.prev && previous.prev.jQ && previous.prev.jQ.hasClass('un-italicized')) {
		  this.selectLeft();
		  previous = previous.prev;
	  }
	  this.selectRight();
	  while (nextt.next && nextt.next.jQ && nextt.next.jQ.hasClass('un-italicized')) {
		  this.selectRight();
		  nextt = nextt.next;
	  }
  }
  //if (nextt.prev.jQ.hasClass('un-italicized') && (nextt.cmd === '(')) {
//	  this.selectRight();// add and remove the same thing
//	  latex = latex + '(';// because it is needed for pattern matching
//  }
//  this.clearSelection();
  */
  this.deleteSelection();
  latex = ( latex && latex.match(/\\text\{([^}]|\\\})*\}|\\[a-z]*|[a-z]+\s*\(|[^\s]/ig) ) || 0;

  (function writeLatexBlock(cursor) {
    while (latex.length) {
      var token = latex.shift(); //pop first item
      if (!token || token === '}') return;

      var cmd;
      if (token.slice(0, 6) === '\\text{') {
        cmd = new TextBlock(token.slice(6, -1));
        cursor.insertNew(cmd).insertAfter(cmd);
        continue; //skip recursing through children
      }
      else if (
        token === '\\left'
        || token === '\\right'
        || token === '('
        || token === ')'
        || token === '['
        || token === ']'
      ) { //FIXME HACK: implement real \left and \right LaTeX commands, rather than special casing them here
        if (token.slice(0, 1) === '\\') token = latex.shift();
        if (token === '\\') token = latex.shift();

        cursor.insertCh(token);
        cmd = cursor.prev || cursor.parent.parent;

        if (cursor.prev) //was a close-paren, so break recursion
          return cursor.appendTo(cursor.prev.lastChild);
        else //was an open-paren, hack to put the following latex
          latex.unshift('{'); //in the ParenBlock in the math DOM
      }
      else if (/^\\[a-z]+|[a-z]+\s*\($/i.test(token)) {
    	var realtoken = token.match(/[a-z]+/ig).shift();
        var cmd = LatexCmds[realtoken];
        if (token.charAt(token.length-1) === '(')
         	latex.unshift('(');
        if (cmd)
          cursor.insertNew(cmd = new cmd(undefined, realtoken), true); //FIX ME HACK isWriteLatex
        else {
        	if (token.charAt(token.length-1) === '(')
        	{
        		if (realtoken.length > MAX_UNITALICIZED_LEN)
        			MAX_UNITALICIZED_LEN = realtoken.length;
        		// nasty hack, but needed
        		UnItalicizedCmds[realtoken] = 1;
        		cmd = LatexCmds[realtoken] = UnItalicized;
        		cursor.insertNew(cmd = new cmd(undefined, realtoken), true);
        	} else {
        		cmd = new TextBlock(realtoken);
            	cursor.insertNew(cmd).insertAfter(cmd);
            	continue; //skip recursing through children
        	}
        }
      }
      else {
        if (token.match(/[a-zA-Z]/))
          cmd = new Variable(token);
        else if (cmd = LatexCmds[token])
          cmd = new cmd;
        else
          cmd = new VanillaSymbol(token);

        cursor.insertNew(cmd);
      }
      if (!cursor.prev) {
        while (true) {
          var token = latex.shift();
          if (!token) return false;

          if (token === '{')
            writeLatexBlock(cursor);
          else
            cursor.insertCh(token);

          if (cursor.parent.next)
            cursor.prependTo(cursor.parent.next);
          else
            break;
        }
        cursor.insertAfter(cursor.parent.parent);
      }
    }
  }(this));
  return this.hide();
};
_.write = function(ch) {
  return this.show().insertCh(ch);
};
_.insertCh = function(ch) {
  if (this.selection) {
    //gotta do this before this.selection is mutated by 'new cmd(this.selection)'
    this.prev = this.selection.prev;
    this.next = this.selection.next;
  }

  var cmd;
  if (ch.match(/^[a-zA-Z]$/))
    cmd = new Variable(ch);
  else if (cmd = CharCmds[ch] || LatexCmds[ch])
    cmd = new cmd(this.selection, ch);
  else
    cmd = new VanillaSymbol(ch);

  if (this.selection) {
    if (cmd instanceof Symbol)
      this.selection.remove();
    delete this.selection;
  }

  return this.insertNew(cmd);
};
_.insertNew = function(cmd, isWriteLatex) { //FIXME HACK isWriteLatex
  cmd.insertAt(this, isWriteLatex);
  return this;
};
_.insertCmd = function(latexCmd, replacedFragment) {
  var cmd = LatexCmds[latexCmd];
  if (cmd) {
    cmd = new cmd(replacedFragment, latexCmd);
    this.insertNew(cmd);
    if (cmd instanceof Symbol && replacedFragment)
      replacedFragment.remove();
  }
  else {
    cmd = new TextBlock(latexCmd);
    cmd.firstChild.focus = function(){ delete this.focus; return this; };
    this.insertNew(cmd).insertAfter(cmd);
    if (replacedFragment)
      replacedFragment.remove();
  }
  return this;
};
_.unwrapGramp = function() {
  var gramp = this.parent.parent,
    greatgramp = gramp.parent,
    prev = gramp.prev,
    cursor = this;

  gramp.eachChild(function(uncle) {
    if (uncle.isEmpty()) return;

    uncle.eachChild(function(cousin) {
      cousin.parent = greatgramp;
      cousin.jQ.insertBefore(gramp.jQ.first());
    });
    uncle.firstChild.prev = prev;
    if (prev)
      prev.next = uncle.firstChild;
    else
      greatgramp.firstChild = uncle.firstChild;

    prev = uncle.lastChild;
  });
  prev.next = gramp.next;
  if (gramp.next)
    gramp.next.prev = prev;
  else
    greatgramp.lastChild = prev;

  if (!this.next) { //then find something to be next to insertBefore
    if (this.prev)
      this.next = this.prev.next;
    else {
      while (!this.next) {
        this.parent = this.parent.next;
        if (this.parent)
          this.next = this.parent.firstChild;
        else {
          this.next = gramp.next;
          this.parent = greatgramp;
          break;
        }
      }
    }
  }
  if (this.next)
    this.insertBefore(this.next);
  else
    this.appendTo(greatgramp);

  gramp.jQ.remove();

  if (gramp.prev)
    gramp.prev.respace();
  if (gramp.next)
    gramp.next.respace();
};
_.backspace = function() {
  if (this.deleteSelection());
  else if (this.prev) {
    if (this.prev.isEmpty())
      this.prev = this.prev.remove().prev;
    else if (this.prev instanceof Bracket)
      return this.appendTo(this.prev.firstChild).deleteForward();
    else
      this.selectLeft();
  }
  else if (this.parent !== this.root) {
    if (this.parent.parent.isEmpty())
      return this.insertAfter(this.parent.parent).backspace();
    else
      this.unwrapGramp();
  }

  if (this.prev)
    this.prev.respace();
  if (this.next)
    this.next.respace();
  this.redraw();

  return this;
};
_.deleteForward = function() {
  if (this.deleteSelection());
  else if (this.next) {
    if (this.next.isEmpty())
      this.next = this.next.remove().next;
    else if (this.next instanceof Bracket)
      return this.prependTo(this.next.firstChild).backspace();
    else
      this.selectRight();
  }
  else if (this.parent !== this.root) {
    if (this.parent.parent.isEmpty())
      return this.insertBefore(this.parent.parent).deleteForward();
    else
      this.unwrapGramp();
  }

  if (this.prev)
    this.prev.respace();
  if (this.next)
    this.next.respace();
  this.redraw();

  return this;
};
_.selectFrom = function(anticursor) {
  //find ancestors of each with common parent
  var oneA = this, otherA = anticursor; //one ancestor, the other ancestor
  loopThroughAncestors: while (true) {
    for (var oneI = this; oneI !== oneA.parent.parent; oneI = oneI.parent.parent) //one intermediate, the other intermediate
      if (oneI.parent === otherA.parent) {
        left = oneI;
        right = otherA;
        break loopThroughAncestors;
      }

    for (var otherI = anticursor; otherI !== otherA.parent.parent; otherI = otherI.parent.parent)
      if (oneA.parent === otherI.parent) {
        left = oneA;
        right = otherI;
        break loopThroughAncestors;
      }

    if (oneA.parent.parent)
      oneA = oneA.parent.parent;
    if (otherA.parent.parent)
      otherA = otherA.parent.parent;
  }
  //figure out which is left/prev and which is right/next
  var left, right, leftRight;
  if (left.next !== right) {
    for (var next = left; next; next = next.next) {
      if (next === right.prev) {
        leftRight = true;
        break;
      }
    }
    if (!leftRight) {
      leftRight = right;
      right = left;
      left = leftRight;
    }
  }
  this.hide().selection = new Selection(
    left.parent,
    left.prev,
    right.next
  );
  this.insertAfter(right.next.prev || right.parent.lastChild);
  this.root.selectionChanged();
};
_.selectLeft = function() {
  if (this.selection) {
    if (this.selection.prev === this.prev) { //if cursor is at left edge of selection;
      if (this.prev) { //then extend left if possible
        this.hopLeft().next.jQ.prependTo(this.selection.jQ);
        this.selection.prev = this.prev;
      }
      else if (this.parent !== this.root) //else level up if possible
        this.insertBefore(this.parent.parent).selection.levelUp();
    }
    else { //else cursor is at right edge of selection, retract left
      this.prev.jQ.insertAfter(this.selection.jQ);
      this.hopLeft().selection.next = this.next;
      if (this.selection.prev === this.prev) {
        this.deleteSelection();
        return;
      }
    }
  }
  else {
    if (this.prev)
      this.hopLeft();
    else //end of a block
      if (this.parent !== this.root)
        this.insertBefore(this.parent.parent);
      else
        return;

    this.hide().selection = new Selection(this.parent, this.prev, this.next.next);
  }
  this.root.selectionChanged();
};
_.selectRight = function() {
  if (this.selection) {
    if (this.selection.next === this.next) { //if cursor is at right edge of selection;
      if (this.next) { //then extend right if possible
        this.hopRight().prev.jQ.appendTo(this.selection.jQ);
        this.selection.next = this.next;
      }
      else if (this.parent !== this.root) //else level up if possible
        this.insertAfter(this.parent.parent).selection.levelUp();
    }
    else { //else cursor is at left edge of selection, retract right
      this.next.jQ.insertBefore(this.selection.jQ);
      this.hopRight().selection.prev = this.prev;
      if (this.selection.next === this.next) {
        this.deleteSelection();
        return;
      }
    }
  }
  else {
    if (this.next)
      this.hopRight();
    else //end of a block
      if (this.parent !== this.root)
        this.insertAfter(this.parent.parent);
      else
        return;

    this.hide().selection = new Selection(this.parent, this.prev.prev, this.next);
  }
  this.root.selectionChanged();
};
_.clearSelection = function() {
  if (this.show().selection) {
    this.selection.clear();
    delete this.selection;
    this.root.selectionChanged();
  }
  return this;
};
_.deleteSelection = function() {
  if (!this.show().selection) return false;

  this.prev = this.selection.prev;
  this.next = this.selection.next;
  this.selection.remove();
  delete this.selection;
  this.root.selectionChanged();
  return true;
};

function Selection(parent, prev, next) {
  MathFragment.apply(this, arguments);
}
_ = Selection.prototype = new MathFragment;
_.jQinit = function(children) {
  this.jQ = children.wrapAll('<span class="selection"></span>').parent();
    //can't do wrapAll(this.jQ = $(...)) because wrapAll will clone it
};
_.levelUp = function() {
  this.clear().jQinit(this.parent.parent.jQ);

  this.prev = this.parent.parent.prev;
  this.next = this.parent.parent.next;
  this.parent = this.parent.parent.parent;

  return this;
};
_.clear = function() {
  this.jQ.replaceWith(this.jQ.children());
  return this;
};
_.blockify = function() {
  this.jQ.replaceWith(this.jQ = this.jQ.children());
  return MathFragment.prototype.blockify.call(this);
};
_.detach = function() {
  var block = MathFragment.prototype.blockify.call(this);
  this.blockify = function() {
    this.jQ.replaceWith(block.jQ = this.jQ = this.jQ.children());
    return block;
  };
  return this;
};

/*********************************************************
 * The actual jQuery plugin and document ready handlers.
 ********************************************************/

//The publicy exposed method of jQuery.prototype, available (and meant to be
//called) on jQuery-wrapped HTML DOM elements.
$.fn.mathquill = function(cmd, latex) {
  switch (cmd) {
  case 'redraw':
    this.find(':not(:has(:first))').each(function() {
      var data = $(this).data(jQueryDataKey);
      if (data && (data.cmd || data.block)) Cursor.prototype.redraw.call(data.cmd || data.block);
    });
    return this;
  case 'revert':
    return this.each(function() {
      var data = $(this).data(jQueryDataKey);
      if (data && data.revert)
        data.revert();
    });
  case 'latex':
    if (arguments.length > 1) {
      return this.each(function() {
        var data = $(this).data(jQueryDataKey);
        if (data && data.block && data.block.renderLatex){
          data.block.renderLatex(latex);
          data.block.triggerSpecialEvent( 'render' );
        }
      });
    }

    var data = this.data(jQueryDataKey);
    return data && data.block && data.block.latex();
  case 'text':
    var data = this.data(jQueryDataKey);
    return data && data.block && data.block.text();
  case 'html':
    return this.html().replace(/ ?hasCursor|hasCursor /, '')
      .replace(/ class=(""|(?= |>))/g, '')
      .replace(/<span class="?cursor( blink)?"?><\/span>/i, '')
      .replace(/<span class="?textarea"?><textarea><\/textarea><\/span>/i, '');
  case 'write':
    if (arguments.length > 1)
      return this.each(function() {
        var data = $(this).data(jQueryDataKey),
          block = data && data.block,
          cursor = block && block.cursor;

        if (cursor)
          cursor.writeLatex(latex).parent.blur();
      });
  case 'cmd':
    if (arguments.length > 1)
      return this.each(function() {
        var data = $(this).data(jQueryDataKey),
          block = data && data.block,
          cursor = block && block.cursor;

        if (cursor) {
          cursor.show();
          if (/^\\[a-z]+$/i.test(latex)) {
            if (cursor.selection) {
              //gotta do cursor before cursor.selection is mutated by 'new cmd(cursor.selection)'
              cursor.prev = cursor.selection.prev;
              cursor.next = cursor.selection.next;
            }
            cursor.insertCmd(latex.slice(1), cursor.selection);
            delete cursor.selection;
          }
          else
            cursor.insertCh(latex);
          cursor.hide().parent.blur();
        }
      });
  case 'moveStart':
  	var data = this.data(jQueryDataKey);
  	if( data && data.block )
	   	data.block.cursor.prependTo( data.block );
  	break;
  case 'moveEnd':
    var data = this.data(jQueryDataKey);
  	if( data && data.block )
	   	data.block.cursor.appendTo( data.block );
  	break;
  case 'selection':
    var data = this.data(jQueryDataKey);
  	if( data && data.block )
      return data.block.cursor.selection ? '$'+data.block.cursor.selection.latex()+'$' : '';
    else
      return undefined;
  default:
    var textbox = cmd === 'textbox',
      editable = textbox || cmd === 'editable',
      RootBlock = textbox ? RootTextBlock : RootMathBlock;
    return this.each(function() {
      createRoot($(this), new RootBlock, textbox, editable);
    });
  }
};

//on document ready, mathquill-ify all `<tag class="mathquill-*">latex</tag>`
//elements according to their CSS class.
$(function() {
  $('.mathquill-editable:not(.mathquill-rendered-math)').mathquill('editable');
  $('.mathquill-textbox:not(.mathquill-rendered-math)').mathquill('textbox');
  $('.mathquill-embedded-latex').mathquill();
});


}());
