package org.mathpiper.mpreduce.ui.gui;

//
// InputPane.java                           Copyright A C Norman, 2000
//
//
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.Keymap;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import org.mathpiper.mpreduce.functions.builtin.Fns;





class InputPane extends JTextPane
{
// This extends a JTextPane since that lets me hook onto all the
// actions and general facilities that that provides me with. But what
// I first do here is to get myself hooks onto all those existing actions
// and then install a new keymap that directs most things to my own set of
// actions. During development these will just invoke the one that I had
// inherited, often printing a message so I can verify what is going on.
// But eventually they implement the behaviour that I really want, much of
// which is described in the file CWin.java

StyledDocument doc;

// A InputPane can be in one of four states.

int state = OUTPUTMODE;

static final int INPUTMODE   = 0;
static final int OUTPUTMODE  = 1;
static final int PAGEWAIT    = 2;
static final int INTERRUPTED = 3;

static final int redColor    = 0x00ff0040;   // slight purple tinge
static final int yellowColor = 0x00ffffc0;   // rather pale yellow
static final int blueColor   = 0x000000c0;   // darkish blue
static final int greenColor  = 0x00008040;   // darkish green
static final int blackColor  = 0x00000040;   // very dark blue

SimpleAttributeSet redText;
SimpleAttributeSet yellowText;
SimpleAttributeSet blueText;
SimpleAttributeSet greenText;
SimpleAttributeSet blackText;

String monospacedFontName = "MonoSpaced";
String fontName = "Serif";
int fontSize = 16;
int fontWeight = Font.PLAIN;
Font fixedFont, font, smallFont, tinyFont;
Font italicFont, smallItalicFont, tinyItalicFont;
FontMetrics metrics;

DefaultCaret caret;

public boolean getScrollableTracksViewportWidth()
{
//  System.out.println("check width option");
    return true; // (doc.getLength() == 0);
}

class ToScreen implements Runnable
{
    String b;

    ToScreen(String s)
    {   b = s;
    }
    
    public void run()
    {
        try
	{   doc.insertString(doc.getLength(), b, null);
	    caret.setDot(doc.getLength());
	}
	catch (BadLocationException e)
	{}
    }
}

// toScreen is called from other threads - I must protect Swing against 
// that!

void toScreen(String b)
{
    SwingUtilities.invokeLater(new ToScreen(b));
}

int afterPrompt = 0;


void showPrompt()
{
    try
    {   doc.insertString(doc.getLength(), 
            (Fns.prompt==null ? "> " : Fns.prompt),
            redText);
        afterPrompt = doc.getLength();
        caret.setDot(afterPrompt);
	scrollRectToVisible(caret);
    }
    catch (BadLocationException e)
    {}
}

Action [] pendingActions = new Action[10];
ActionEvent [] pendingEvents = new ActionEvent[10];
int pendingIn = 0, pendingOut = 0;

void deferAction(Action a, ActionEvent e)
{
    int l = pendingActions.length;
    pendingActions[pendingIn] = a;
    pendingEvents[pendingIn++] = e;
    if (pendingIn == l) pendingIn = 0;
    if (pendingIn == pendingOut) // buffer now full
    {   Action [] w = new Action[2*l];
        ActionEvent [] w1 = new ActionEvent[2*l];
        pendingIn = 0;
        for (int i=pendingOut; i<l; i++) 
        {   w[pendingIn] = pendingActions[pendingOut+i];
            w1[pendingIn++] = pendingEvents[pendingOut+i];
        }
        for (int i=0; i<pendingOut; i++)
        {   w[pendingIn] = pendingActions[i];
            w1[pendingIn++] = pendingEvents[i];
        }
        pendingOut = 0;
        pendingActions = w;
        pendingEvents = w1;
    }
}

void performPendedActions()
{
    int l = pendingActions.length;
    while (state == INPUTMODE && pendingIn != pendingOut)
    {   Action a = pendingActions[pendingOut];
        ActionEvent e = pendingEvents[pendingOut++];
        if (pendingOut == l) pendingOut = 0;
        a.actionPerformed(e);
    }
}

String inputData = null;

// getInputLine will be performed on a task thread, not on the
// event dispatch thread. Therefor I have to do funny things to
// make it valid for working with Swing.


class RequestInput implements Runnable
{
    public void run()
    {
         showPrompt();
	 state = INPUTMODE;
	 performPendedActions();
    }
}

Runnable request = new RequestInput();

synchronized String getInputLine()
{
    SwingUtilities.invokeLater(request); // wake up the event dispatch thread!
// I  use a shared variable "inputData" to transfer data back. But if I
// make all methods that touch it synchonized I should be OK even there.
    while (inputData == null)
    {
// Suppose I am unlucky and the pended actions do not include a newline
// but one is typed while I am just about HERE. Then a notify() is
// done but I miss it! Well in fact by giving a time-out here I am pretty
// much OK. In around 0.2 of a second I will come round and detect that
// inputData has become non-null and so I will exit the loop. Whew!
// The 0.2 sec choice is to keep the overhead of busy-waiting under
// some degree of control while trying to appear responsive in even this
// odd case. My expectation is that USUALLY the newline will not arrive
// until I am safely within the wait() so there will not be any undue
// delay.
        try
        {   wait(200);
        }
        catch (InterruptedException e)
        {}
    }
    String d = inputData; // NB the newline is implicit
    inputData = null;
    return d;
}

synchronized void setInputData(String s)
{
    inputData = s;
    state = OUTPUTMODE;
    notify();
}

HashMap actionHash = new HashMap();

Action getAction(String name)
{
    return (Action)actionHash.get(name);
}

Action oldcopy,               copy;               
Action oldcut,                cut;                
Action olddefaultKeyTyped,    defaultKeyTyped;    
Action olddeleteNextChar,     deleteNextChar;     
Action olddeletePrevChar,     deletePrevChar;     
Action oldinsertBreak,        insertBreak;        
Action oldinsertContent,      insertContent;      
Action oldinsertTab,          insertTab;          
Action oldpaste,              paste;              
Action                        historyUp;
Action                        historyDown;
Action                        insertPasted;

Vector history = new Vector(10, 10);
int historyIndex;

InputPane myself;

InputPane()
{
    myself = this;
    doc = getStyledDocument();

    setPreferredSize(new Dimension(800, 200));
    setEditable(true);

    guessFont();
    setupFonts(fontSize, fontWeight);

    caret = (DefaultCaret)getCaret();
    requestFocus();

    setBackground(new Color(yellowColor)); // a pale yellow
    setForeground(new Color(blackColor));  // very dark blue!

// While updating the contents of the document I will often want to
// set attributes, specifically ones that are to do with the colour of
// the text inserted. Here are some attribute values ready to help me.
    redText   = new SimpleAttributeSet();
    yellowText= new SimpleAttributeSet();
    blueText  = new SimpleAttributeSet();
    greenText = new SimpleAttributeSet();
    blackText = new SimpleAttributeSet();
    StyleConstants.setForeground(redText, new Color(redColor));
    StyleConstants.setForeground(yellowText, new Color(yellowColor));
    StyleConstants.setForeground(blueText, new Color(blueColor));
    StyleConstants.setForeground(greenText, new Color(greenColor));
    StyleConstants.setForeground(blackText, new Color(blackColor));


    Action [] a = getActions();
    for (int i=0; i<a.length; i++)
        actionHash.put(a[i].getValue(Action.NAME), a[i]);

    oldcopy            = getAction(StyledEditorKit.copyAction);
    oldcut             = getAction(StyledEditorKit.cutAction);
    olddefaultKeyTyped = getAction(StyledEditorKit.defaultKeyTypedAction);
    olddeleteNextChar  = getAction(StyledEditorKit.deleteNextCharAction);
    olddeletePrevChar  = getAction(StyledEditorKit.deletePrevCharAction);
    oldinsertBreak     = getAction(StyledEditorKit.insertBreakAction);
    oldinsertContent   = getAction(StyledEditorKit.insertContentAction);
    oldinsertTab       = getAction(StyledEditorKit.insertTabAction);
    oldpaste           = getAction(StyledEditorKit.pasteAction);

    copy            = new CopyAction();
    cut             = new CutAction();
    defaultKeyTyped = new DefaultKeyTypedAction();
    deleteNextChar  = new DeleteNextCharAction();
    deletePrevChar  = new DeletePrevCharAction();
    insertBreak     = new InsertBreakAction();
    insertContent   = new InsertContentAction();
    insertTab       = new InsertTabAction();
    paste           = new PasteAction();
    historyUp       = new HistoryUpAction();
    historyDown     = new HistoryDownAction();
    insertPasted    = new InsertPastedAction();

    Keymap map = getKeymap();

    map.setDefaultAction(new DefaultKeyTypedAction());
    map.addActionForKeyStroke(        // ^X => CUT
        KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK),
        cut);
    map.addActionForKeyStroke(        // ^V => PASTE
        KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK),
        paste);
    map.addActionForKeyStroke(        // ^C => COPY
        KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK),
        copy);
    map.addActionForKeyStroke(        // backspace => deletePrev
        KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
        deletePrevChar);
    map.addActionForKeyStroke(        // delete => deleteNext
        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
        deleteNextChar);
    map.addActionForKeyStroke(        // enter => accept the line
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
        insertBreak);
    map.addActionForKeyStroke(        // tab => tab
        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
        insertTab);
// The default actions for the up and down keys are
//     up/down          move up and down one line
//     shift+up/down    ditto but extending a selection
//     ctrl+up/down     up & down one paragraph
//     ctrl+shift       select up and sown by the paragraph
// I modify this so that ctrl+up/down gives a behaviour rather like
// that of "doskey" or the command history scheme present in some
// Unix shells. It replaces the whole of the current input line
// with one of the previous input lines (which I therefore need to
// store away)
    map.addActionForKeyStroke(        // ctrl-UP
        KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK),
        historyUp);
    map.addActionForKeyStroke(        // ctrl-DOWN
        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK),
        historyDown);
    setKeymap(map);

    requestFocus();
    replaceSelection("\n" +
//                   getPreferredScrollableViewportSize() + "\n" +
                     getBounds() + "\n" +
                     getWidth() + "\n"
                     );


}

void guessFont()
{
    fontSize = 16;
    fontWeight = Font.PLAIN;
}

void setupFonts(int size, int weight)
{
    fontSize = size;
    fontWeight = weight;
// I will prepare seven fonts. The first is a fixed pitch one used for
// simple text output. The remaining seven (and I may decide later on that
// I need yet more) are Roman & Italic styles in three sizes: the intent is
// that they are for body, subscript and sub-subscripts.
    fixedFont = new Font(monospacedFontName, fontWeight, fontSize);
    setFont(fixedFont);
    metrics = getFontMetrics(fixedFont);
// Now the others... I am somewhat uncertain about the scaling
// rules that I should apply here, but I am confident that if the
// main font is really tiny already I should be cautious about
// reducing too abruptly. Tune this for the best visual effect!
    font      = new Font(fontName, fontWeight, fontSize);
    italicFont = new Font(fontName, fontWeight+Font.ITALIC, fontSize);
    int smallSize = (fontSize <= 10) ? fontSize-1 :
                    (fontSize <= 14) ? fontSize-2 :
                    ((7*fontSize+9)/10);
    smallFont = new Font(fontName, fontWeight, smallSize);
    smallItalicFont = new Font(fontName, fontWeight+Font.ITALIC, smallSize);
    int tinySize = (smallSize <= 10) ? smallSize-1 :
                    (smallSize <= 14) ? smallSize-2 :
                    ((7*smallSize+9)/10);
    tinyFont  = new Font(fontName, fontWeight, tinySize);
    tinyItalicFont  = new Font(fontName, fontWeight+Font.ITALIC, tinySize);
}



// I want to arrange that input up to and including the most
// recent prompt string is read-only. To achieve this I arrange
// to reset dot and mark to be beyond there whenever an interesting
// event is triggered. I allow them to roam so I can COPY from earlier
// material.

int adjustSelection()
{
    int dot = caret.getDot();
    int mark = caret.getMark();
    int x = afterPrompt;
    if (dot < afterPrompt)
    {   if (mark < afterPrompt)
        {   caret.setDot(afterPrompt);
            dot = mark = afterPrompt;
        }
        else
        {   caret.setDot(mark);
            caret.moveDot(afterPrompt);
            dot = afterPrompt;
        }
    }
    else if (mark < afterPrompt)
    {   caret.setDot(afterPrompt);
        caret.moveDot(dot);
        mark = afterPrompt;
    }
    if (mark < dot) return mark;
    else return dot;
}

String clipboardSubstitute = null;

class CopyAction extends AbstractAction
{
    CopyAction()
    {   super(StyledEditorKit.copyAction);
    }

    public void actionPerformed(ActionEvent e)
    {
// I possibly want to customise this to transfer some style information
// to the clipboard as well as plain text. Eg I probably want to do special
// things with prompt strings.  But also note that I want COPY (and so also
// CUT) to be executed instantly and not deferred waiting for input. Actually
// that means that CUT will not do much cutting in such circumstances.
//
        int mark = caret.getMark();
        int dot = caret.getDot();
        if (mark == dot) return;
        else if (mark > dot)
        {   int w = mark;
            mark = dot;
            dot = w;
        }
// now (mark < dot) defines the region of my selection
            StringBuffer s = new StringBuffer();
// This really feels pretty gross. I look at each character and discard it
// if it is the colour that I use for prompts! Doing it one character at a
// time makes COPY a slow process, but I expect it only gets used on smallish
// selections!  The reasoning behind this is that I may want to re-input
// previous segments of stuff, and if the prompts get picked up by "COPY"
// and put back by "PASTE" that will mess me up.
//
// Maybe a yet better variation will be to map things so that the
// text on the clipboard reads
//       on screen         on clipboard
//          \                 \\ 
//          {                 \{
//          }                 \}
//          prompt            {\p prompt}
// where this leaves capability for other bits of clipboard syntax
// involving {} to denote other effects I may later introduce.
//
// Anyway that is not yet done and needs more thought!
        for (int i=mark; i<dot; i++)
        {   try
            {   Element ch = doc.getCharacterElement(i);
                AttributeSet a = ch.getAttributes();
                Color col = doc.getForeground(a);
                if (new Color(redColor).equals(col)) continue;
                String chs = doc.getText(i, 1);
                s.append(chs);
            }
            catch (BadLocationException e1)
            {}
        }
        try
        {   Clipboard cb = getToolkit().getSystemClipboard();
            StringSelection ss = new StringSelection(s.toString());
            cb.setContents(ss, ss);
        }
        catch (SecurityException e1)
        {   clipboardSubstitute = s.toString();
        }
    }
}

class CutAction extends AbstractAction
{
    CutAction()
    {   super(StyledEditorKit.cutAction);
    }

    public void actionPerformed(ActionEvent e)
    {
// I want to copy it ALL to the clipboard (and I use the current
// version of that action so I can deal with style-specific stuff
// sometime when I feel keen). But then I delete just that bit
// which is within the current input line.
        copy.actionPerformed(e);
        if (state != INPUTMODE) return;
        adjustSelection();
        replaceSelection("");
    }
}

class DefaultKeyTypedAction extends AbstractAction
{
    DefaultKeyTypedAction()
    {
        super(StyledEditorKit.defaultKeyTypedAction);
    }

    public void actionPerformed(ActionEvent e)
    {
        int c = (int)e.getActionCommand().charAt(0);
// I will be rather cautious about what characters I insert into my text.
// In particular and perhaps unreasonably I will NOT allow characters with
// code 0x100 or upwards. I also forbid control characters and deletes.
        if ((c & 0x7f) < 0x20 || (c & 0x7f) == 0x7f || c >= 0x100 ||
             (e.getModifiers() & 
              (Event.CTRL_MASK|Event.ALT_MASK|Event.META_MASK)) != 0) return;
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
// NB the "dot" returned here is the lower of dot/mark and so it is the
// position to insert after I have done a replaceSelection("");
        int dot = adjustSelection();
// I replace the selection with an empty string and THEN insert my
// new character because that lets me control the attributes (in my case
// colour) of inserted text.
        replaceSelection("");
        try
        {   doc.insertString(dot, e.getActionCommand(), blueText);
            caret.setDot(dot+1);
        }
        catch (BadLocationException e1)
        {}
    }
}

int adjustForDelete(boolean left)
{
// When I hit either delete-forwards or delete-backwards I will behave
// as follows:
//     If either dot or mark line within the current input line
//     I shrink the selection to be the intersection of the current one
//     and the input line. If both dot and mark are outside the
//     current line then delete-forwards moves them to its start and
//     delete backwards moves them both to its end.
//     If there is a non-empty selection left I delete that material and
//     that is all I do (whichever direction the delete was in)
//     Finally I have a caret location within the input line and if
//     there is a character to the relevant size of it I delete that.
    int dot = caret.getDot();
    int mark = caret.getMark();
    int x = afterPrompt;
    if (dot < afterPrompt)
    {   if (mark < afterPrompt)
        {   if (left)
            {   dot = mark = doc.getLength();
                caret.setDot(dot);
            }
            else
            {   dot = mark = afterPrompt;
                caret.setDot(afterPrompt);
            }
        }
        else
        {   caret.setDot(mark);
            caret.moveDot(afterPrompt);
            dot = afterPrompt;
        }
    }
    else if (mark < afterPrompt)
    {   caret.setDot(afterPrompt);
        caret.moveDot(dot);
        mark = afterPrompt;
    }
// now the selection is fully after the prompt location
    if (mark == dot) return dot;
    replaceSelection("");
    return -1;
}


class DeleteNextCharAction extends AbstractAction
{
    DeleteNextCharAction()
    {   super(StyledEditorKit.deleteNextCharAction);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
        int dot = adjustForDelete(false);
        if (dot < 0) return; // done
        if (dot == doc.getLength()) return;
        caret.setDot(dot);
        caret.moveDot(dot+1);
        replaceSelection("");
    }
}

class DeletePrevCharAction extends AbstractAction
{
    DeletePrevCharAction()
    {   super(StyledEditorKit.deletePrevCharAction);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
        int dot = adjustForDelete(true);
        if (dot <= afterPrompt) return;
        caret.setDot(dot);
        caret.moveDot(dot-1);
        replaceSelection("");
    }
}

class InsertBreakAction extends AbstractAction
{
    InsertBreakAction()
    {   super(StyledEditorKit.insertBreakAction);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
        int l = doc.getLength();
        try
        {   String s = getText(afterPrompt, l - afterPrompt);
            history.add(s);  // remember it!
            historyIndex = history.size();
// now I will put a newline on the screen.
            caret.setDot(l); // so it tends to remain at the end...
            doc.insertString(l, "\n", blueText);
// I should do the setInputData LAST here since it flips my global
// state from INPUTMODE to OUTPUTMODE and also lets the client thread
// have my data and potentially start generating new output based on it.
// Actually I might worry a bit about what happens if another mouse or
// keyboard event is triggered while I am busy around here...
            setInputData(s);
	}
        catch (BadLocationException e1)
        {}
    }
}

// The next action is ONLY used as a pending action, so it can only
// ever be triggered when I am in input mode.

class InsertPastedAction extends AbstractAction
{
    InsertPastedAction()
    {
        super("insert-pasted-text");
    }

    public void actionPerformed(ActionEvent e)
    {
        int dot = adjustSelection();
        replaceSelection("");
        try
        {   String s = e.getActionCommand();
            doc.insertString(dot, s, blueText);
            caret.setDot(dot+s.length());
        }
        catch (BadLocationException e1)
        {}
        if (e.getModifiers() != 0) return;
        insertBreak.actionPerformed(e);
    }
}

class InsertContentAction extends AbstractAction
{
    InsertContentAction()
    {   super(StyledEditorKit.insertContentAction);
    }

    public void actionPerformed(ActionEvent e)
    {
        System.out.println("insertContent " +
                           safe(e.getActionCommand()) + " " + 
                           safe(e.paramString()));
//      oldinsertContent.actionPerformed(e);
    }
}

class InsertTabAction extends AbstractAction
{
    InsertTabAction()
    {   super(StyledEditorKit.insertTabAction);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
        int dot = adjustSelection();
        replaceSelection("");
        try
        {   do
            {   doc.insertString(dot++, " ", blueText);
            }
            while ((dot-afterPrompt)%8 != 0);
        }
        catch (BadLocationException e1)
        {}
    }
}

class PasteAction extends AbstractAction
{
    PasteAction()
    {   super(StyledEditorKit.pasteAction);
    }

    public void actionPerformed(ActionEvent e)
    {
// Actually if I go PASTE when not in input mode I think I still need to
// grab the stuff off the clipboard instantly. It is inserting it into
// the buffer that gets delayed.
        Clipboard cb;
        Transferable tr;
        String s = null;
        try
        {
            cb = getToolkit().getSystemClipboard();
            tr = cb.getContents(this);
            s = (String)tr.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception e1)
        {   s = clipboardSubstitute;
        }
        if (s == null || s.length() == 0) return; // No more to do!
        int p = 0, q;
        ActionEvent e2;
        String s1;
        while ((q = s.indexOf('\n', p)) >= 0)
        {   s1 = s.substring(p, q); // up to but not including newline
            e2 = new ActionEvent(this, 0, s1);
            deferAction(insertPasted, e2);
            p = q+1;
        }
        s1 = s.substring(p, s.length()); 
        if (s1.length() != 0)
        {   e2 = new ActionEvent(this, 0, s1, 1); // 1 marks "no newline"!
            deferAction(insertPasted, e2);
        }
        if (state != INPUTMODE) return;
        performPendedActions();
    }
}

class HistoryUpAction extends AbstractAction
{
    HistoryUpAction()
    {   super("history-up");
    }

    public void actionPerformed(ActionEvent e)
    {
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
        if (historyIndex > 0)
        {   historyIndex--;
            String s = "";
            try
            {   s = (String)history.get(historyIndex);
            }
            catch (ArrayIndexOutOfBoundsException e1)
            {};
            caret.setDot(afterPrompt);
            caret.moveDot(doc.getLength());
            replaceSelection("");
            try
            {   doc.insertString(afterPrompt, s, blueText);
                caret.setDot(doc.getLength());
            }
            catch (BadLocationException e1)
            {}
        }
    }
}

class HistoryDownAction extends AbstractAction
{
    HistoryDownAction()
    {   super("history-down");
    }

    public void actionPerformed(ActionEvent e)
    {
        if (state != INPUTMODE)
        {   deferAction(this, e);
            return;
        }
        int n = history.size();
        if (historyIndex < n)
        {   historyIndex++;
            String s = "";
            try
            {   if (historyIndex < n)
                    s = (String)history.get(historyIndex);
            }
            catch (ArrayIndexOutOfBoundsException e1)
            {};
            caret.setDot(afterPrompt);
            caret.moveDot(doc.getLength());
            replaceSelection("");
            try
            {   doc.insertString(afterPrompt, s, blueText);
                caret.setDot(doc.getLength());
            }
            catch (BadLocationException e1)
            {}
        }
    }
}


String safe(String a)
{
    if (a == null) return "<null>";
    String r = "";
    for (int i=0; i<a.length(); i++)
    {   char c = a.charAt(i);
        int ic = (int)c;
        if (32 <= ic && ic < 127) r = r + c;
        else r = r + "\\x" + Integer.toHexString(ic);
    }
    return r;
}

}

// end of InputPane.java
