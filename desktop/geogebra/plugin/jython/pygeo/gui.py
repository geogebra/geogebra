from __future__ import division, with_statement

# Standard Library imports
import re

# Local imports
import lexing

# Java imports
from java.lang import Runnable

from javax.swing import (
    JFrame, JPanel, JTextArea, JScrollPane, BoxLayout, JButton, JList,
    DefaultListModel, ListCellRenderer, BorderFactory, JTextPane,
    JMenuBar, JMenu, JMenuItem, JFileChooser,
    KeyStroke,
    JTabbedPane, JComboBox,
    SwingUtilities,
)
from javax.swing.text import (
    StyleContext, StyleConstants, SimpleAttributeSet, TabStop, TabSet
)
from javax.swing.event import DocumentListener

from java.awt import (
    Toolkit, Component, BorderLayout, Color as awtColor, GridLayout, Font
)
from java.awt.event import KeyListener, ActionListener, KeyEvent, ActionEvent

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
        if ensure_newline and self.doc.getText(doclen - 1, 1) != '\n':
            text = '\n' + text
        self.doc.insertString(self.doc.length, text, self.doc.getStyle(style))
        # Scroll down
        self.textpane.setCaretPosition(self.doc.length)


class FileManager(object):
    
    def __init__(self, pywin):
        self.fc = JFileChooser()
        self.fc.fileFilter = FileNameExtensionFilter("Python Files", ["py"])
        self.pywin = pywin
        self.load_path = None
        self.script_path = None
    
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
        with open(self.script_path, "wb") as stream:
            stream.write(self.pywin.script_area.input)
    
    def open_script(self):
        f = self.show_open_dialog("Select script file to open")
        if f is None:
            return
        self.script_path = f.absolutePath
        with open(self.script_path, "rb") as stream:
            self.pywin.script_area.input = stream.read()
    
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


import keyword, __builtin__

class PyLexer(lexing.Lexer):
    separators = [
        'space', 'punctuation', 'assignment', 'grouper', 'operator',
        'shortstring', 'geo'
    ]
    order = [
        'float', 'integer', 'shortstring',
        'operator', 'assignment',
        'grouper',
        'keyword', 'builtin', 'identifier', 
        'space', 'punctuation'
        'comment',
    ]

    space = r'\s'
    decorator = r'^\s*@'
    punctuation = r"[.,;:]"
    assignment = r"=|\+=|-=|\*\*=|\*=|/=|//=|<<=|>>=|\^=|\|=|&="
    grouper = r"[(){}\[\]]"
    operator = r"\+|-|\*\*|\*|//|/|\^|&|\||~|<=|>=|==|!="
    keyword = r"(%s)(?!\w)" % "|".join(keyword.kwlist)
    builtin = r"(%s)(?!\w)" % "|".join(dir(__builtin__))
    integer = r"[0-9]+"
    identifier = r"[a-zA-Z_]\w*"
    float = r"[0-9]+\.[0-9]*([eE][+-]?[0-9]+)?"
    shortstring = r"[uU]?[rR]?'([^'\n\\]|\\[^\n])*'" \
                  r'|[uU]?[rR]?"([^"\n\\]|\\[^\n])*"'
    comment = r'#.*'
    geo = r"\$" + identifier
    decorator = "@" + identifier

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


class InputPane(KeyListener, DocumentListener):
    def __init__(self):
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

    def _getinput(self):
        return self.doc.getText(0, self.doc.length)
    def _setinput(self, input):
        self.doc.remove(0, self.doc.length)
        self.doc.insertString(0, input, self.parent_style)
        self.component.setCaretPosition(0)
    input = property(_getinput, _setinput)

    def getline(self, offset):
        """Return the start and end offsets of the line at offset"""
        root = self.doc.defaultRootElement
        line = root.getElementIndex(offset)
        element = root.getElement(line)
        return element.startOffset, element.endOffset
    
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


class InteractiveInput(InputPane):
    def __init__(self, checks_disabled, runcode):
        InputPane.__init__(self)
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

class ScriptPane(ActionListener):
    def __init__(self):
        self.component = JPanel(BorderLayout())

        # Create editor pane
        scrollpane = JScrollPane()
        self.script_area = InputPane()
        line_numbers = LineNumbering(self.script_area.component)
        scrollpane.viewport.view = self.script_area.component
        scrollpane.rowHeaderView = line_numbers.component
        self.component.add(scrollpane, BorderLayout.CENTER)

        self.script_area.input = api.initScript
        
        # Create Selection pane
        select_pane = JPanel()
        save_btn = JButton("Save")
        select_pane.add(save_btn)
        self.component.add(select_pane, BorderLayout.PAGE_START)

        save_btn.addActionListener(self)

    def actionPerformed(self, evt):
        api.initScript = self.script_area.input

        
class EventsPane(ActionListener):
    
    def __init__(self):
        self.component = JPanel(BorderLayout())

        # Create editor pane
        scrollpane = JScrollPane()
        self.script_area = InputPane()
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
    
    def update_geos(self, geos):
        self.objects_box.removeAllItems()
        self.geos = geos
        for geo in self.geos:
            self.objects_box.addItem(geo.typeString + " " + geo.label)
        self.objects_box.repaint()

    def save_current_script(self):
        if self.current is None:
            return
        geo, evt = self.current
        script = self.script_area.input
        setattr(geo, evt + "Script", script)
            
    def update_script_area(self):
        self.save_current_script()
        geo = self.geos[self.objects_box.selectedIndex]
        evt = self.events_box.selectedItem
        self.current = geo, evt
        self.script_area.input = getattr(geo, evt + "Script")

    # Implementation of ActionEvent
    def actionPerformed(self, evt):
        self.update_script_area()


class PythonWindow(KeyListener, DocumentListener, ActionListener):
    
    def __init__(self, interface):
        self.interface = interface
        
        self.frame = JFrame("Python Window")

        tabs = JTabbedPane()

        # Create Interactive Pane
        interactive_pane = JPanel(BorderLayout())        
        scrollpane = JScrollPane()
        inputPanel = JPanel()
        inputPanel.layout = GridLayout(1, 1)
        self.check_disabled = LockManager()
        self.input = InteractiveInput(self.check_disabled, self.runcode)
        self.input.component.document.addDocumentListener(self)
        inputPanel.add(self.input.component)
        self.outputpane = OutputPane()
        scrollpane.viewport.view = self.outputpane.textpane
        interactive_pane.add(scrollpane, BorderLayout.CENTER)
        interactive_pane.add(inputPanel, BorderLayout.PAGE_END)

        # Create Script Pane
        self.script_pane = ScriptPane()

        # Create Events Pane
        self.events_pane = EventsPane()
        
        tabs.addTab("Interactive", interactive_pane)
        tabs.addTab("Script", self.script_pane.component)
        tabs.addTab("Events", self.events_pane.component)
        
        self.frame.add(tabs)
        self.frame.size = 500, 600
        self.frame.visible = False
        self.component = None
        self.make_menubar()
        self.history = InputHistory()

    def update_geos(self, geos):
        self.events_pane.update_geos(geos)
    
    def make_menubar(self):
        shortcut = Toolkit.getDefaultToolkit().menuShortcutKeyMask
        menubar = JMenuBar()

        def new_item(title, cmd, key, mod=shortcut):
            item = JMenuItem(title, actionCommand=cmd)
            item.accelerator = KeyStroke.getKeyStroke(key, mod)
            item.addActionListener(self)
            return item
        filemenu = JMenu("File")
        menubar.add(filemenu)

        fm = self.file_manager = FileManager(self)
        
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
        
        item = new_item("Previous Input", "up", KeyEvent.VK_UP,
            mod=ActionEvent.ALT_MASK)
        shellmenu.add(item)
        
        item = new_item("Next Input", "down", KeyEvent.VK_DOWN,
                         mod=ActionEvent.ALT_MASK)
        shellmenu.add(item)


        self.frame.setJMenuBar(menubar)
    
    def toggle_visibility(self):
        self.frame.visible = not self.frame.visible
    def add_component(self, c):
        self.remove_component()
        self.frame.add(c, BorderLayout.PAGE_START)
        self.component = c
    def remove_component(self):
        if self.component is not None:
            self.frame.remove(self.component)
            self.component = None
    def add(self, text, type="input"):
        self.outputpane.addtext(text, type)
        self.frame.validate()
    def error(self, text):
        self.outputpane.addtext(text, "error", ensure_newline=True)
    def write(self, text):
        self.add(text, "output")

    # Code execution methods
    def runcode(self, source, interactive=True):
        if not source.strip():
            return True
        processed_source = source.replace("$", "geo.")
        code = self.interface.compileinteractive(processed_source)
        if code in ("continue", "error"):
            code = self.interface.compilemodule(processed_source)
            if code == "error":
                return
        if code == "error":
            return False
        source = source.strip()
        if interactive:
            self.history.append(source)
            self.current_text = ""
            self.outputpane.addtext(source +'\n', "input", ensure_newline=True)
        self.interface.run(code)
        return True
    def run(self):
        source = self.input.text
        if not source.strip():
            self.input.text = ""
            return
        processed_source = source.replace("$", "geo.")
        code = self.interface.compileinteractive(processed_source)
        if code in ("continue", "error"):
            code = self.interface.compilemodule(processed_source)
            if code == "error":
                return
        source = source.strip()
        self.history.append(source)
        self.outputpane.addtext(source +'\n', "input", ensure_newline=True)
        result = self.interface.run(code)
        if result == "OK":
            self.input.text = ""
    def execfile(self, path):
        return execfile(path, self.interface.namespace)

    # Implementation of KeyListener
    def keyPressed(self, evt):
        pass
    def keyReleased(self, evt):
        pass
    def keyTyped(self, evt):
        if evt.keyChar == '\n':
            text = self.input.text
            if text.endswith('\n\n'):
                self.run()
                return
            t = text.rstrip()
            if '\n' not in t and not t.endswith(':'):
                self.run()
                return
            offset = self.input.caretPosition - 1
            indent = None
            if offset:
                lines = text[:offset].rsplit('\n', 1)
                if len(lines) == 1:
                    line = text[:offset]
                else:
                    line = lines[1]
                indent = re.match('\\s*', line).group(0)
                if len(indent) == len(line):
                    # No non-whitespace on this line
                    if len(text) == offset + 1:
                        self.run()
                        return
                elif text[offset - 1] == ':':
                    indent += '\t'
            if indent:
                with self.check_disabled:
                    self.input.text = text[:offset + 1] + indent + text[offset + 1:]
                self.input.caretPosition = offset + len(indent) + 1

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

    # Implementation of ActionListener
    def actionPerformed(self, evt):
        try:
            getattr(self, "action_" + evt.actionCommand)(evt)
        except AttributeError:
            pass

    # Navigating history
    def action_up(self, evt):
        """Move back in history"""
        try:
            with self.check_disabled:
                self.input.input = self.history.back()
        except InputHistory.OutOfBounds:
            pass
    def action_down(self, evt):
        """Move forward in history"""
        try:
            with self.check_disabled:
                self.input.input = self.history.forward()
        except InputHistory.OutOfBounds:
            self.input.input = self.current_text
            self.history.reset_position()

    # Script actions
    def action_runscript(self, evt):
        """Run script"""
        self.runcode(self.script_area.input, interactive=False)
    def action_runselection(self, evt):
        """Run selected text in script"""
        code = self.script_area.component.selectedText.strip()
        self.runcode(code, interactive=False)
    def action_indentselection(self, evt):
        component = self.script_area.component
        lines = component.selectedText.split("\n")
        for i, line in enumerate(lines):
            if line:
                lines[i] = "\t" + line
        component.replaceSelection("\n".join(lines))
    def action_dedentselection(self, evt):
        component = self.script_area.component
        lines = component.selectedText.split("\n")
        for i, line in enumerate(lines):
            if line.startswith("\t"):
                lines[i] = line[1:]
            elif line.startswith("    "):
                lines[i] = line[4:]
        component.replaceSelection("\n".join(lines))
    
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

