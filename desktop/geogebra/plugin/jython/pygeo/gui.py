from __future__ import division, with_statement

# Standard Library imports
import re, sys, traceback, codecs

# FLAT
#from geogebra.plugin.jython import PythonFlatAPI as API
#api = API.getInstance()
from apiproxy import API, start_new_thread

from pyggb import interface

# Local imports
import lexing
from pylexing import PyLexer

# Java imports
from java.lang import Runnable

from javax.swing import (
    JFrame, JPanel, JScrollPane, JButton,
    BorderFactory, JTextPane,
    JMenuBar, JMenu, JMenuItem, JFileChooser,
    KeyStroke,
    JTabbedPane, JComboBox,
    SwingUtilities,
)
from javax.swing.text import (
    StyleContext, StyleConstants, SimpleAttributeSet, TabStop, TabSet
)
from javax.swing.event import (
    DocumentListener, ChangeListener, UndoableEditListener
)

from javax.swing.undo import (
    UndoManager, CannotUndoException, CannotRedoException
)

from java.awt import (
    Toolkit, BorderLayout, Color as awtColor, GridLayout, Font
)
from java.awt.event import (
    KeyListener, ActionListener, KeyEvent, ActionEvent, FocusListener,
    WindowAdapter,
)

try:
    from javax.swing.filechooser import FileNameExtensionFilter
except ImportError:
    # This is Java < 6: reimplement this class in Python
    from javax.swing.filechooser import FileFilter
    
    class FileNameExtensionFilter(FileFilter):
        def __init__(self, description, extensions):
            self._description = description
            self.extensions = ["." + ext for ext in extensions]
        def getDescription(self):
            return self._description
        def accept(self, file):
            if file.isDirectory():
                return True
            name = file.name
            return any(name.endswith(ext) for ext in self.extensions)


class StdStreams(object):

    """Context manager that replace standard streams with custom ones."""
    
    def __init__(self, stdout, stderr):
        self.streams = stdout, stderr
    def __enter__(self):
        self.sys_streams = sys.stdout, sys.stderr
        sys.stdout, sys.stderr = self.streams
    def __exit__(self, type, value, traceback):
        sys.stdout, sys.stderr = self.sys_streams


class OutputPane(object):
    
    """Pane for outpout of interactive session"""
    
    def __init__(self):
        self.textpane = JTextPane()
        self.doc = self.textpane.getStyledDocument()
        self.textpane.editable = False
        style_context = StyleContext.getDefaultStyleContext()
        default_style = style_context.getStyle(StyleContext.DEFAULT_STYLE)
        parent_style = self.doc.addStyle("parent", default_style)
        StyleConstants.setFontFamily(parent_style, "Monospaced")
        input_style = self.doc.addStyle("input", parent_style)
        output_style = self.doc.addStyle("output", parent_style)
        StyleConstants.setForeground(output_style, awtColor.BLUE)
        error_style = self.doc.addStyle("error", parent_style)
        StyleConstants.setForeground(error_style, awtColor.RED)
        
        # Do a dance to set tab size
        font = Font("Monospaced", Font.PLAIN, 12)
        self.textpane.setFont(font)
        fm = self.textpane.getFontMetrics(font)
        tabw = float(fm.stringWidth(" "*4))
        tabs = [
            TabStop(tabw*i, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE)
            for i in xrange(1, 51)
        ]
        attr_set = style_context.addAttribute(
            SimpleAttributeSet.EMPTY,
            StyleConstants.TabSet,
            TabSet(tabs)
        )
        self.textpane.setParagraphAttributes(attr_set, False)
        #Dance done!
        
    def addtext(self, text, style="input", ensure_newline=False):
        doclen = self.doc.length
        if ensure_newline and doclen:
            if self.doc.getText(doclen - 1, 1) != '\n':
                text = '\n' + text
        self.doc.insertString(self.doc.length, text, self.doc.getStyle(style))
        # Scroll down
        self.textpane.setCaretPosition(self.doc.length)

    def clear(self):
        """Remove all text"""
        self.doc.remove(0, self.doc.length)


class FileManager(object):
    
    def __init__(self, pywin):
        self.fc = JFileChooser()
        self.fc.fileFilter = FileNameExtensionFilter("Python Files", ["py"])
        self.pywin = pywin
        self.load_path = None
        self.script_path = None

    @property
    def script_area(self):
        return self.pywin.script_pane.script_area
    
    def show_open_dialog(self, title):
        self.fc.dialogTitle = title
        res = self.fc.showOpenDialog(self.pywin.frame)
        if res == JFileChooser.APPROVE_OPTION:
            return self.fc.selectedFile
    
    def show_save_dialog(self, title):
        self.fc.dialogTitle = title
        res = self.fc.showSaveDialog(self.pywin.frame)
        if res == JFileChooser.APPROVE_OPTION:
            return self.fc.selectedFile
    
    def save_script(self):
        if self.script_path is None:
            return
        with codecs.open(self.script_path, "wb", "utf-8") as stream:
            stream.write(self.script_area.input)
    
    def open_script(self):
        f = self.show_open_dialog("Select script file to open")
        if f is None:
            return
        self.script_path = f.absolutePath
        with codecs.open(self.script_path, "rb", "utf-8") as stream:
            self.script_area.input = stream.read()
    
    def save_script_as(self):
        f = self.show_save_dialog("Select file to write script to")
        if f is None:
            return
        self.script_path = f.absolutePath
        self.save_script()
    
    def load_script(self):
        f = self.show_open_dialog("Select script file to load")
        if f is None:
            return
        self.load_path = f.absolutePath
        self.reload_script()
    
    def reload_script(self):
        print "*** Loading", self.load_path, "***"
        try:
            self.pywin.execfile(self.load_path)
        except Exception, e:
            self.pywin.outputpane.addtext(str(e) + '\n', 'error')


class InputHistory(object):
    
    class OutOfBounds(Exception):
        pass
    
    def __init__(self):
        self.clear()

    def clear(self):
        self.history = []
        self.history_pos = 0
    
    def append(self, input):
        self.history.append(input)
        self.reset_position()
    
    def reset_position(self):
        self.history_pos = 0
    
    def back(self):
        if self.history_pos < len(self.history):
            self.history_pos += 1
            return self.history[-self.history_pos]
        else:
            raise self.OutOfBounds
    
    def forward(self):
        if self.history_pos > 1:
            self.history_pos -= 1
            return self.history[-self.history_pos]
        else:
            raise self.OutOfBounds
        

class Later(Runnable):
    def __init__(self, f):
        self.f = f
    def run(self):
        self.f()

def later(f):
    SwingUtilities.invokeLater(Later(f))
    return f


tok_style_map = {
    PyLexer.keyword: "kw",
    PyLexer.integer: "number",
    PyLexer.float: "number",
    PyLexer.shortstring: "string",
    PyLexer.comment: "comment",
    PyLexer.builtin: "builtin",
    PyLexer.geo: "geo",
    PyLexer.decorator: "decorator",
}


class PyState(object):
    def __init__(self):
        self.reset()
    def reset(self):
        self.state = 'default'
    def trans(self, token):
        if token.toktype == PyLexer.space:
            return 'default'
        res = getattr(self, 'trans_' + self.state)(token)
        if res is None:
            self.state = 'default'
            return self.trans_default(token)
        else:
            return res
    def trans_default(self, token):
        if token.strval == "def":
            self.state = "def"
        elif token.strval == "class":
            self.state = "class"
        return tok_style_map.get(token.toktype, 'default')
    def trans_def(self, token):
        if token.toktype == PyLexer.identifier:
            self.state = 'default'
            return "defname"
    def trans_class(self, token):
        if token.toktype == PyLexer.identifier:
            self.state = 'default'
            return 'classname'


class NoWrapJTextPane(JTextPane):
    
    """JTextPane which doesn't wrap lines"""
    
    def getScrollableTracksViewportWidth(self):
        parent = self.parent
        if parent:
            return self.getUI().getPreferredSize(self).width <= parent.size.width
        else:
            return True


def new_style(doc, name, parent=None, **style_args):
    if parent is None:
        style_context = StyleContext.getDefaultStyleContext()
        parent = style_context.getStyle(StyleContext.DEFAULT_STYLE)
    style = doc.addStyle(name, parent)
    for name, value in style_args.iteritems():
        method = "set" + name.capitalize()
        getattr(StyleConstants, method)(style, value)
    return style


def document_linecount(doc):
    """ Return the number of lines in a document"""
    if not doc.length:
        return 1
    linecount = doc.defaultRootElement.getElementIndex(doc.length - 1) + 1
    last_char = doc.getText(doc.length - 1, 1)
    if last_char == "\n":
        linecount += 1
    return linecount


def make_line_numbers(start, end):
        ndigits = len(str(end))
        pattern = "%%%ii" % ndigits
        return "\n".join(map(pattern.__mod__, range(start, end + 1)))


class LineNumbering(DocumentListener):
    """Pane containing line numbers.
    
    Listens to changes in a textpane to update itself automatically.
    """
    
    def __init__(self, textpane):
        self.component = JTextPane(
            font=textpane.font,
            border=BorderFactory.createEmptyBorder(5, 5, 5, 5),
            editable=False,
            background=awtColor(220, 220, 220),
        )
        self.doc = self.component.document
        self.component.setParagraphAttributes(
            textpane.paragraphAttributes, True
        )
        self.linecount = 0
        self.style = new_style(self.doc, "default",
            foreground=awtColor(100, 100, 100),
        )
        self.reset(1)
        textpane.document.addDocumentListener(self)
    
    def reset(self, lc):
        self.linecount = lc
        self.doc.remove(0, self.doc.length)
        self.doc.insertString(0, make_line_numbers(1, lc), self.style)
    
    def resize(self, lc):
        if not lc:
            self.reset(1)
        elif len(str(lc)) != len(str(self.linecount)):
            self.reset(lc)
        elif lc > self.linecount:
            self.doc.insertString(self.doc.length, "\n", self.style)
            self.doc.insertString(self.doc.length,
                make_line_numbers(self.linecount + 1, lc),
                self.style
            )
            self.linecount = lc
        elif lc < self.linecount:
            root = self.doc.defaultRootElement
            offset = root.getElement(lc).startOffset - 1
            self.doc.remove(offset, self.doc.length - offset)
            self.linecount = lc
    
    # Implementation of DocumentListener
    def changedUpdate(self, evt):
        pass
    
    def insertUpdate(self, evt):
        self.resize(document_linecount(evt.document))
    
    def removeUpdate(self, evt):
        self.resize(document_linecount(evt.document))


class InputPane(KeyListener, DocumentListener, FocusListener, UndoableEditListener):
    def __init__(self, window=None):
        self.window = window
        self.component = NoWrapJTextPane(
            border=BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        
        def new_style(name, color=None, bold=None, italic=None, underline=None):
            style = self.doc.addStyle(name, self.parent_style)
            if color is not None:
                if isinstance(color, str):
                    color = awtColor(
                        int(color[0:2], 16),
                        int(color[2:4], 16),
                        int(color[4:6], 16)
                    )
                StyleConstants.setForeground(style, color)
            if bold is not None:
                StyleConstants.setBold(style, bold)
            if italic is not None:
                StyleConstants.setItalic(style, italic)
            if underline is not None:
                StyleConstants.setUnderline(style, underline)
            return style
        
        self.doc = self.component.getStyledDocument()

        attrs = SimpleAttributeSet()
        StyleConstants.setLineSpacing(attrs, 0.2)
        self.component.setParagraphAttributes(attrs, True)
        
        style_context = StyleContext.getDefaultStyleContext()
        default_style = style_context.getStyle(StyleContext.DEFAULT_STYLE)
        self.parent_style = self.doc.addStyle("parent", default_style)
        StyleConstants.setFontFamily(self.parent_style, "Monospaced")

        # Set styles for syntax highlighting
        new_style("kw", "990066", bold=True)
        new_style("number", "0033AA")
        new_style("string", "993300")
        new_style("comment", "FF0000")
        new_style("defname", "0033FF", bold=True)
        new_style("classname", "009900", bold=True)
        new_style("builtin", italic=True)
        new_style("geo", underline=True)
        new_style("decorator", "0033FF")
        
        # Do a dance to set tab size
        font = Font("Monospaced", Font.PLAIN, 12)
        self.component.setFont(font)
        fm = self.component.getFontMetrics(font)
        tabw = float(fm.stringWidth(" "*4))
        tabs = [
            TabStop(tabw*i, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE)
            for i in xrange(1, 51)
        ]
        attr_set = style_context.addAttribute(
            SimpleAttributeSet.EMPTY,
            StyleConstants.TabSet,
            TabSet(tabs)
        )
        self.component.setParagraphAttributes(attr_set, False)
        #Dance done!
        
        self.component.addKeyListener(self)
        # Remove?
        # self.nocheck = LockManager()
        self.doc.addDocumentListener(self)

        # This hack to prevent styling to be recorded
        self.changing_styles = False
        self._undo = None
        if self.window:
            self.component.addFocusListener(self)
            self.doc.addUndoableEditListener(self)
    
    # Moving the caret
    def moveCaretToStart(self):
        self.component.setCaretPosition(0)
    def move_caret_to_end(self):
        self.component.setCaretPosition(self.doc.length)
    
    def _getinput(self):
        return self.doc.getText(0, self.doc.length)
    def _setinput(self, input):
        self.doc.remove(0, self.doc.length)
        self.doc.insertString(0, input, self.parent_style)
        self.component.setCaretPosition(0)
    input = property(_getinput, _setinput)

    def reset_undo(self):
        if self._undo is not None:
            self._undo.discardAllEdits()
            self.window.update_undo_state(self._undo)

    def _getundo(self):
        return self._undo
    def _setundo(self, undo):
        if self.window.undo is self._undo:
            self.window.update_undo_state(undo)
        self._undo = undo
    undo = property(_getundo, _setundo)

    def getline(self, offset):
        """Return the start and end offsets of the line at offset"""
        root = self.doc.defaultRootElement
        line = root.getElementIndex(offset)
        element = root.getElement(line)
        return element.startOffset, element.endOffset

    # Indent / dedent selection
    def indent_selection(self):
        component = self.component
        lines = component.selectedText.split("\n")
        for i, line in enumerate(lines):
            if line:
                lines[i] = "\t" + line
        component.replaceSelection("\n".join(lines))
    def dedent_selection(self):
        component = self.component
        lines = component.selectedText.split("\n")
        for i, line in enumerate(lines):
            if line.startswith("\t"):
                lines[i] = line[1:]
            elif line.startswith("    "):
                lines[i] = line[4:]
        component.replaceSelection("\n".join(lines))

    # Implementation of KeyListener
    def keyPressed(self, evt):
        pass
    def keyReleased(self, evt):
        pass
    def keyTyped(self, evt):
        if evt.keyChar == '\n':
            text = self.input
            offset = self.component.caretPosition - 1
            indent = None
            if offset:
                prev_line = self.doc.getText(0, offset).rsplit('\n', 1)[-1]
                indent = re.match('\\s*', prev_line).group(0)
                if text[offset - 1] == ':':
                    indent += '\t'
            if indent:
                self.doc.insertString(offset + 1, indent, self.parent_style)

    # Implementation of DocumentListener
    def change_styles(self, start, end):
        text = self.doc.getText(start, end - start)
        @later
        def do_change_styles(start=start, state=PyState()):
            pos = 0
            self.changing_styles = True
            while True:
                try:
                    for tok in PyLexer.scan(text, pos):
                        toklen = len(tok.strval)
                        style_name = state.trans(tok)
                        style = self.doc.getStyle(style_name)
                        self.doc.setCharacterAttributes(
                            start, toklen, style, True
                        )
                        start += toklen
                        pos += toklen
                    else:
                        break
                except lexing.Error:
                    pos += 1
                    start += 1
            self.changing_styles = False
    def set_line_style(self, offset):
        start, end = self.getline(offset)
        self.change_styles(start, end)  
    def set_region_style(self, offset, length):
        start, _ = self.getline(offset)
        _, end = self.getline(offset + length)
        self.change_styles(start, end)
    def changedUpdate(self, evt):
        pass
    def insertUpdate(self, evt):
        if evt.length == 1:
            self.set_line_style(evt.offset)
        else:
            self.set_region_style(evt.offset, evt.length)
    def removeUpdate(self, evt):
        self.set_line_style(evt.offset - 1)

    # Implementation of FocusListener
    def focusGained(self, evt):
        self.window.update_undo_state(self._undo)
    def focusLost(self, evt):
        self.window.update_undo_state(None)

    # Implementation of UndoableEditListener
    def undoableEditHappened(self, evt):
        if self.changing_styles:
            return
        if self._undo is not None:
            self._undo.addEdit(evt.edit)
            self.window.update_undo_state(self._undo)


class InteractiveInput(InputPane):
    def __init__(self, window, checks_disabled, runcode):
        InputPane.__init__(self, window)
        outside = BorderFactory.createLoweredBevelBorder()
        self.component.border = BorderFactory.createCompoundBorder(
            outside,
            self.component.border
        )
        self.checks_disabled = checks_disabled
        self.runcode = runcode
    def keyTyped(self, evt):
        with self.checks_disabled:
            InputPane.keyTyped(self, evt)
            if evt.keyChar != '\n':
                return
            text = self.input
            lines = self.input.split("\n")
            if (
                len(lines) == 2 and not lines[1] or
                len(lines) > 2 and not lines[-1].strip() and not lines[-2].strip()
            ):
                res = self.runcode(text)
                if res:
                    self.input = ""


class LockManager(object):
    def __init__(self):
        self.lock = 0
    def __enter__(self):
        self.lock += 1
        return self
    def __exit__(self, type, value, traceback):
        self.lock -= 1
    def __nonzero__(self):
        return bool(self.lock)


class MyStream(object):
    def __init__(self, write):
        self.write = write


class WindowPane(object):

    def activate(self):
        pass

    def deactivate(self):
        pass


class InteractivePane(WindowPane, ActionListener, DocumentListener):

    def __init__(self, window, api):
        self.api = api
        self.component = JPanel(BorderLayout())
        
        scrollpane = JScrollPane()
        inputPanel = JPanel()
        inputPanel.layout = GridLayout(1, 1)
        self.check_disabled = LockManager()
        self.input = InteractiveInput(
            window,
            self.check_disabled,
            self.runcode
        )
        self.input.undo = UndoManager()
        self.input.component.document.addDocumentListener(self)
        inputPanel.add(self.input.component)
        self.outputpane = OutputPane()
        scrollpane.viewport.view = self.outputpane.textpane
        self.component.add(scrollpane, BorderLayout.CENTER)
        self.component.add(inputPanel, BorderLayout.PAGE_END)
        self.history = InputHistory()

        self.stdout = MyStream(self.write)
        self.stderr = MyStream(self.error)
        
        self.running_thread = None
        
    # Methods for writing to output area
    def add(self, text):
        self.outputpane.addtext(text, "input", ensure_newline=True)
    def error(self, text):
        self.outputpane.addtext(text, "error", ensure_newline=True)
    def write(self, text):
        self.outputpane.addtext(text, "output")
    def show_traceback(self):
        tb_lines = traceback.format_exception(*sys.exc_info())
        self.error("".join(tb_lines[3:]))
    
    # Code execution
    def runcode(self, text):
        # with StdStreams(self.stdout, self.stderr):
            if self.running_thread:
                self.error("Code running... Ctrl-C to interrupt\n")
                return False
            try:
                code = interface.compile_im(text)
            except Exception:
                self.show_traceback()
                return False
            text = text.strip()
            self.history.append(text)
            self.current_text = ""
            self.add(text + "\n")
            def runner():
                try:
                    with StdStreams(self.stdout, self.stderr):
                        interface.run(code)
                except Exception:   
                    self.show_traceback()
                self.running_thread = None
            self.running_thread = start_new_thread(runner)
            return True
    
    def stopcode(self):
        if self.running_thread:
            self.running_thread._thread.stop()
            self.error("Interrupted\n")
            self.running_thread = None
    
    def indent_selection(self):
        return self.input_area.indent_selection()
    def dedent_selection(self):
        return self.input_area.dedent_selection()
    
    # Implementation of DocumentListener
    def update_current_text(self):
        if not self.check_disabled:
            self.current_text = self.input.input
            self.history.reset_position()
    def changedUpdate(self, evt):
        pass
    def insertUpdate(self, evt):
        self.update_current_text()
    def removeUpdate(self, evt):
        self.update_current_text()

    def activate(self):
        # Put the cursor in the input box
        self.input.component.requestFocusInWindow()
    
    def clear(self):
        """Clear the history"""
        self.outputpane.clear()
        self.history.clear()
    
    # Navigating history
    def history_back(self):
        """Move back in history"""
        try:
            with self.check_disabled:
                self.input.input = self.history.back()
                self.input.reset_undo()
                self.input.move_caret_to_end()
        except InputHistory.OutOfBounds:
            pass
    def history_forward(self):
        """Move forward in history"""
        try:
            with self.check_disabled:
                self.input.input = self.history.forward()
                self.input.reset_undo()
                self.input.move_caret_to_end()
        except InputHistory.OutOfBounds:
            self.input.input = self.current_text
            self.input.reset_undo()
            self.input.move_caret_to_end()
            self.history.reset_position()


class ScriptPane(WindowPane):

    def __init__(self, window, api):
        self.api = api
        self.component = JPanel(BorderLayout())

        # Create editor pane
        scrollpane = JScrollPane()
        self.script_area = InputPane(window)
        line_numbers = LineNumbering(self.script_area.component)
        scrollpane.viewport.view = self.script_area.component
        scrollpane.rowHeaderView = line_numbers.component
        self.component.add(scrollpane, BorderLayout.CENTER)
        
        self.script_area.undo = UndoManager()
        self.reset()
        
    def indent_selection(self):
        self.script_area.indent_selection()
    def dedent_selection(self):
        self.script_area.dedent_selection()

    def reset(self):
        self.script_area.input = self.api.getInitScript()
        self.script_area.reset_undo()
        
    def save_script(self):
        self.api.setInitScript(self.script_area.input)


class EventsPane(WindowPane, ActionListener):
    
    def __init__(self, window, api):
        self.api = api
        self.component = JPanel(BorderLayout())

        # Create editor pane
        scrollpane = JScrollPane()
        self.script_area = InputPane(window)
        self.script_area.undo = UndoManager()
        line_numbers = LineNumbering(self.script_area.component)
        scrollpane.viewport.view = self.script_area.component
        scrollpane.rowHeaderView = line_numbers.component
        self.component.add(scrollpane, BorderLayout.CENTER)

        # Create Selection pane
        select_pane = JPanel()
        self.objects_box = JComboBox([])
        select_pane.add(self.objects_box)
        self.events_box = JComboBox(["update", "click"])
        select_pane.add(self.events_box)
        save_btn = JButton("Save")
        select_pane.add(save_btn)
        self.component.add(select_pane, BorderLayout.PAGE_START)

        self.events_box.addActionListener(self)
        self.objects_box.addActionListener(self)
        save_btn.addActionListener(self)
        
        self.current = None
        self.update_geos()
        interface.addEventListener("add", self.event_listener)
        interface.addEventListener("remove", self.event_listener)
        interface.addEventListener("rename", self.event_listener)
        
        # Hack to be able to change the objects_box
        self.building_objects_box = False

        self.active = False

    def activate(self):
        self.active = True
        if self.must_update_geos:
            self.update_geos()
    def deactivate(self):
        self.active = False
    
    def indent_selection(self):
        return self.script_area.indent_selection()
    def dedent_selection(self):
        return self.script_area.dedent_selection()

    def update_geos(self):
        self.must_update_geos = False
        try:
            self.building_objects_box = True
            self.objects_box.removeAllItems()
            self.geos = self.api.getAllGeos()
            for geo in self.geos:
                tp = API.Geo.getTypeString(geo)
                label = API.Geo.getLabel(geo)
                self.objects_box.addItem(tp + " " + label)
        finally:
            self.building_objects_box = False
        
        if not self.geos:
            self.current = None
            self.objects_box.enabled = False
            self.events_box.enabled = False
            self.script_area.input = ""
            self.script_area.component.enabled = False
        else:
            changed = False
            if self.current is None:
                index, event = 0, 'click'
                changed = True
            else:
                geo, event = self.current
                try:
                    index = self.geos.index(geo)
                except ValueError:
                    index, event = 0, 'click'
                    changed = True
            self.events_box.selectedItem = event
            self.objects_box.selectedIndex = index
            self.events_box.enabled = True
            self.objects_box.enabled = True
            self.script_area.component.enabled = True
            if changed:
                self.update_script_area()
        self.objects_box.repaint()
        self.events_box.repaint()
    
    def event_listener(self, evt, target):
        if self.active:
            self.update_geos()
        else:
            self.must_update_geos = True
    
    def save_current_script(self):
        if self.current is not None:
            geo, evt = self.current
            script = self.script_area.input
            setter = "set" + evt.capitalize() + "Script"
            getattr(API.Geo, setter)(geo, script)
            
    def update_script_area(self):
        self.save_current_script()
        geo_index = self.objects_box.selectedIndex
        if geo_index == -1:
            self.current = None
        else:
            geo = self.geos[geo_index]
            evt = self.events_box.selectedItem
            self.current = geo, evt
            getter = "get" + evt.capitalize() + "Script"
            self.script_area.input = getattr(API.Geo, getter)(geo)
            self.script_area.reset_undo()
        
    def reset(self):
        self.current=None
        self.update_geos()
    
    # Implementation of ActionEvent
    def actionPerformed(self, evt):
        if not self.building_objects_box:
            self.update_script_area()



class PythonWindowAdapter(WindowAdapter):

    """What to do on window events"""
    
    def __init__(self, pywin):
        self.pywin = pywin
        pywin.frame.addWindowListener(self)
    
    def windowClosing(self, evt):
        # Make sure that the Python Window menu item is unchecked.
        # The window is still visible at this point so we need to make
        # is invisible for the benefit of updateMenubar()...
        self.pywin.frame.visible = False
        self.pywin.api.updateMenubar()


class PythonWindow(ActionListener, ChangeListener):
    
    def __init__(self, api):
        self.api = api
        self.frame = JFrame("Python Window")

        tabs = JTabbedPane()

        self.make_menubar()
        self.update_undo_state(None)
        
        self.interactive_pane = InteractivePane(self, api)
        self.script_pane = ScriptPane(self, api)
        self.events_pane = EventsPane(self, api)
        
        tabs.addTab("Interactive", self.interactive_pane.component)
        tabs.addTab("Script", self.script_pane.component)
        tabs.addTab("Events", self.events_pane.component)
        self.panes = [
            self.interactive_pane,
            self.script_pane,
            self.events_pane
        ]
        self.active_pane = self.interactive_pane
        tabs.addChangeListener(self)
        
        self.frame.add(tabs)
        self.frame.visible = False
        self.component = None

        self.window_adapter = PythonWindowAdapter(self)
        # Set up the first active pane as no change event is fired up
        # to start with - but it doesn't work!
        self.frame.pack()
        self.frame.size = 500, 600
        self.active_pane.activate()

    def update_geos(self, geos):
        self.events_pane.update_geos(geos)
    
    def make_menubar(self):
        shortcut = Toolkit.getDefaultToolkit().menuShortcutKeyMask
        menubar = JMenuBar()

        def new_item(title, cmd, key=None, mod=shortcut):
            item = JMenuItem(title, actionCommand=cmd)
            if key is not None:
                item.accelerator = KeyStroke.getKeyStroke(key, mod)
            item.addActionListener(self)
            return item
        filemenu = JMenu("File")
        menubar.add(filemenu)

        self.file_manager = FileManager(self)
        
        item = new_item("Run Python File...", "load", KeyEvent.VK_L)
        filemenu.add(item)

        item = new_item("Run Python File", "reload", KeyEvent.VK_R)
        item.enabled = False
        self.reload_menuitem = item
        filemenu.add(item)

        filemenu.addSeparator()
        
        item = new_item("Open Python Script...", "open", KeyEvent.VK_O)
        filemenu.add(item)

        item = new_item("Save Python Script", "save", KeyEvent.VK_S)
        item.enabled = False
        self.save_menuitem = item
        filemenu.add(item)

        item = new_item("Save Python Script As...", "save_as", KeyEvent.VK_S,
                        mod = shortcut + ActionEvent.SHIFT_MASK)
        filemenu.add(item)

        editmenu = JMenu("Edit")
        menubar.add(editmenu)

        item = new_item("Undo", "undo", KeyEvent.VK_Z)
        editmenu.add(item)
        self.undo_menuitem = item
        item = new_item("Redo", "redo", KeyEvent.VK_Z,
                        mod=shortcut + ActionEvent.SHIFT_MASK)
        editmenu.add(item)
        self.redo_menuitem = item
        
        editmenu.addSeparator()
        
        editmenu.add(new_item("Cut", "cut", KeyEvent.VK_X))
        editmenu.add(new_item("Copy", "copy", KeyEvent.VK_C))
        editmenu.add(new_item("Paste", "paste", KeyEvent.VK_V))

        editmenu.addSeparator()

        item = new_item("Run Script", "runscript", KeyEvent.VK_E)
        editmenu.add(item)
        
        item = new_item("Run Selection", "runselection", KeyEvent.VK_E,
                        mod=shortcut | ActionEvent.SHIFT_MASK)
        editmenu.add(item)

        editmenu.addSeparator()
        
        item = new_item("Indent Selection", "indentselection",
                        KeyEvent.VK_CLOSE_BRACKET)
        editmenu.add(item)
        
        item = new_item("Dedent Selection", "dedentselection",
                        KeyEvent.VK_OPEN_BRACKET)
        editmenu.add(item)
        
        shellmenu = JMenu("Interactive")
        menubar.add(shellmenu)
        
        item = new_item("Interrupt Running code", "interrupt", KeyEvent.VK_C,
            mod=ActionEvent.CTRL_MASK)
        shellmenu.add(item)
        
        item = new_item("Previous Input", "up", KeyEvent.VK_UP,
            mod=ActionEvent.ALT_MASK)
        shellmenu.add(item)
        
        item = new_item("Next Input", "down", KeyEvent.VK_DOWN,
                         mod=ActionEvent.ALT_MASK)
        shellmenu.add(item)
        item = new_item("Clear History", "clearhistory")
        shellmenu.add(item)
        
        self.frame.setJMenuBar(menubar)

    def reset(self):
        """This is called when a new file is loaded"""
        self.script_pane.reset()
        self.events_pane.reset()
    
    def toggle_visibility(self):
        self.frame.visible = not self.frame.visible
    def get_current_script(self):
        return self.script_pane.script_area.input
    def add_component(self, c):
        self.remove_component()
        self.frame.add(c, BorderLayout.PAGE_START)
        self.component = c
    def remove_component(self):
        if self.component is not None:
            self.frame.remove(self.component)
            self.component = None

    def update_undo_state(self, undo):
        self.undo = undo
        if undo is not None and undo.canUndo():
            self.undo_menuitem.enabled = True
            self.undo_menuitem.text = undo.undoPresentationName
        else:
            self.undo_menuitem.enabled = False
            self.undo_menuitem.text = "Undo"
        if undo is not None and undo.canRedo():
            self.redo_menuitem.enabled = True
            self.redo_menuitem.text = undo.redoPresentationName
        else:
            self.redo_menuitem.enabled = False
            self.redo_menuitem.text = "Redo"
    
    # Code execution methods
    def runcode(self, source):
        self.interactive_pane.runcode(source)
    def execfile(self, path):
        with open(path, "rb") as f:
            self.runcode(f.read())
    def error(self, text):
        self.interactive_pane.error(text)
    
    # Implementation of ChangeListener
    def stateChanged(self, evt):
        i = evt.source.selectedIndex
        if self.active_pane is not None:
            self.active_pane.deactivate()
        if 0 <= i < len(self.panes):
            self.active_pane = self.panes[i]
            self.active_pane.activate()
        else:
            self.active_pane = None
    
    # Implementation of ActionListener
    def actionPerformed(self, evt):
        try:
            getattr(self, "action_" + evt.actionCommand)(evt)
        except AttributeError:
            pass
    
    def action_interrupt(self, evt):
        self.interactive_pane.stopcode()
    
    # History actions
    def action_up(self, evt):
        self.interactive_pane.history_back()
    def action_down(self, evt):
        self.interactive_pane.history_forward()
    def action_clearhistory(self, evt):
        self.interactive_pane.clear()
    
    # Script actions
    def action_runscript(self, evt):
        """Run script"""
        self.runcode(self.script_pane.script_area.input)
    def action_runselection(self, evt):
        """Run selected text in script"""
        code = self.script_pane.script_area.component.selectedText.strip()
        self.runcode(code)
    def action_indentselection(self, evt):
        if self.active_pane:
            self.active_pane.indent_selection()
    def action_dedentselection(self, evt):
        if self.active_pane:
            self.active_pane.dedent_selection()
    
    # Saving / loading scripts
    def action_open(self, evt):
        self.file_manager.open_script()
        if self.file_manager.script_path:
            self.save_menuitem.enabled = True
            self.save_menuitem.text = "Save " + self.file_manager.script_path
    def action_save(self, evt):
        self.file_manager.save_script()
    def action_save_as(self, evt):
        self.file_manager.save_script_as()
        if self.file_manager.script_path:
            self.save_menuitem.enabled = True
            self.save_menuitem.text = "Save " + self.file_manager.script_path
    def action_load(self, evt):
        self.file_manager.load_script()
        if self.file_manager.load_path:
            self.reload_menuitem.enabled = True
            self.reload_menuitem.text = "Run " + self.file_manager.load_path
    def action_reload(self, evt):
        self.file_manager.reload_script()

    # Edit actions
    def action_undo(self, evt):
        try:
            self.undo.undo()
        except CannotUndoException:
            print "Can't undo"
        else:
            self.update_undo_state(self.undo)
    def action_redo(self, evt):
        try:
            self.undo.redo()
        except CannotRedoException:
            print "Can't redo"
        else:
            self.update_undo_state(self.undo)
