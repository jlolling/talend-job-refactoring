package de.jlo.talend.tweak.log;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * contains a JTextArea for displaying loglines from Appender
 * @author  lolling.jan
 */
public final class LogPanel extends JPanel {

    private static final Logger      logger                  = Logger.getLogger(LogPanel.class);

    private static final long        serialVersionUID        = 1L;
    private int                      maxLines                = 0;
    private JScrollPane              scrollPane              = null;
    private JTextArea                jTextArea               = null;
    private transient LogWriter                writer;
    private transient PatternLayout            layout                  = null;
    private transient WriterAppender           appender;
    private boolean                  stopped                 = false;
    private boolean                  loggingEnabled          = true;
    private JPanel                   jPanelTools             = null;
    private JButton                  jButtonSaveAs           = null;
    private JButton                  jButtonClear            = null;
    private File                     file;
    private JCheckBox                jCheckBoxAutoScrolling  = null;
    private JCheckBox                jCheckBoxLoggingEnabled = null;
    private JCheckBox                jCheckBoxDebugEnabled = null;
    private JLabel                   jLabel                  = null;
    private String                   suggestedFileName;
    private boolean                  changed                 = false;
    private static LogPanel logPanel;

    public LogPanel() {
        writer = new LogWriter(this);
        layout = new PatternLayout("%d{HH:mm} [%-5p]:  %m%n");
        appender = new WriterAppender(layout, writer);
        Logger root = Logger.getRootLogger();
        root.addAppender(appender);
        initialize();
    }

    public static LogPanel getInstance() {
        if (logPanel == null) {
            logPanel = new LogPanel();
        }
        return logPanel;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(321, 109));
        scrollPane = new JScrollPane();
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        this.add(getJPanelTools(), java.awt.BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(jTextArea);
        changed = false;
    }

    /**
     * @return  the internal appender for use in any logger
     * @uml.property  name="appender"
     */
    public Appender getAppender() {
        return appender;
    }

    public void setAppenderLayout(Layout layout) {
        appender.setLayout(layout);
    }

    public Layout getAppenderLayout() {
        return appender.getLayout();
    }

    /**
     * set the PatternLayout format
     * @param format
     */
    public void setConversionPattern(String format) {
        layout.setConversionPattern(format);
    }

    public String getConversionPattern() {
        return layout.getConversionPattern();
    }

    public void setMaxLogLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public int getMaxLogLines() {
        return maxLines;
    }

    public void setLineWrap(boolean lineWrap) {
        jTextArea.setLineWrap(lineWrap);
    }

    public boolean getLineWrap() {
        return jTextArea.getLineWrap();
    }

    public void clear() {
        stopped = true;
        jTextArea.setText(null);
        stopped = false;
        changed = false;
    }

    public void setAutoScrolling(boolean auto) {
        this.jCheckBoxAutoScrolling.setSelected(auto);
    }

    public boolean isAutoScrolling() {
        return jCheckBoxAutoScrolling.isSelected();
    }

    /**
     * @param loggingEnabled  the loggingEnabled to set
     * @uml.property  name="loggingEnabled"
     */
    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
    }

    /**
     * @return  the loggingEnabled
     * @uml.property  name="loggingEnabled"
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Appends a string to the textarea and scrolls it down.
     * If the number of lines in the area exceeds MAXLINES then the topmost
     * 5 lines will be truncated.
     */
    public void append(String s) {
        if (stopped == false && loggingEnabled) {
            changed = true;
            // Append string
            jTextArea.append(s);
            if (maxLines > 0) {
                // check Linecount
                int len = jTextArea.getLineCount();
                int lines4Delete = (maxLines > 5) ? 5 : maxLines;
                if (len > maxLines) {
                    try {
                        jTextArea.getDocument().remove(0, lines4Delete);
                    } catch (javax.swing.text.BadLocationException exception) {
                        exception.printStackTrace();
                    }
                }
            }
            if (jCheckBoxAutoScrolling.isSelected()) {
                // Scroll down the textarea to the bottom
                Dimension size = jTextArea.getSize();
                JViewport port = scrollPane.getViewport();
                Point point = new Point(0, size.height);
                port.setViewPosition(point);
            }
        }
    }

    public void newLine() {
        append("\n");
    }

    /**
     * LogWriter has the ability to forward the log4j output to a LogFrame class.
     */
    private static class LogWriter extends Writer {
        // Ref to the LogFrame instance that should receive the output
        private LogPanel logpanel;

        /**
         * Constructs a new LogWriter and registers the LogFrame.
         */
        public LogWriter(LogPanel logframe) {
            this.logpanel = logframe;
        }

        /**
         * Method declaration
         *
         *
         * @throws java.io.IOException
         */
        @Override
		public void close() throws java.io.IOException {
        // TODO: implement this java.io.Writer abstract method
        }

        /**
         * Method declaration
         *
         * @throws java.io.IOException
         */
        @Override
		public void flush() throws java.io.IOException {
        // TODO: implement this java.io.Writer abstract method
        }

        /**
         * Append 'text' to the LogFrame textarea.
         */
        @Override
        public void write(String text) {
            logpanel.append(text);
        }

        @Override
		public void write(char[] parm1, int parm2, int parm3) throws java.io.IOException {
            write(String.valueOf(parm1, parm2, parm3));
        }
    }

    /**
     * This method initializes jPanelTools	
     * @return  javax.swing.JPanel
     * @uml.property  name="jPanelTools"
     */
    private JPanel getJPanelTools() {
        if (jPanelTools == null) {
            jLabel = new JLabel();
            jLabel.setText("Logging");
            jPanelTools = new JPanel();
            jPanelTools.add(jLabel, null);
            jPanelTools.add(getJButtonSaveAs(), null);
            jPanelTools.add(getJButtonClear(), null);
            jPanelTools.add(getJCheckBoxAutoScrolling(), null);
            jPanelTools.add(getJCheckBoxLoggingEnabled(), null);
            jPanelTools.add(getJCheckBoxDebugEnabled(), null);
        }
        return jPanelTools;
    }

    /**
     * This method initializes jButtonSaveAs	
     * @return  javax.swing.JButton
     * @uml.property  name="jButtonSaveAs"
     */
    private JButton getJButtonSaveAs() {
        if (jButtonSaveAs == null) {
            jButtonSaveAs = new JButton();
            jButtonSaveAs.setText("Save");
            jButtonSaveAs.setToolTipText("save log entries in file");
            jButtonSaveAs.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    showSaveFileDialog();
                }
            });
        }
        return jButtonSaveAs;
    }

    /**
     * This method initializes jButtonClear	
     * @return  javax.swing.JButton
     * @uml.property  name="jButtonClear"
     */
    private JButton getJButtonClear() {
        if (jButtonClear == null) {
            jButtonClear = new JButton();
            jButtonClear.setText("Clear");
            jButtonClear.setToolTipText("clear log entries");
            jButtonClear.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    clear();
                }
            });
        }
        return jButtonClear;
    }

    public void showSaveFileDialog() {
        final JFileChooser chooser = new JFileChooser();
        if (file != null) {
            chooser.setCurrentDirectory(file.getParentFile());
        } else {
            if (suggestedFileName != null) {
                chooser.setSelectedFile(new File(suggestedFileName));
            }
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        if (file != null) {
            chooser.setSelectedFile(file);
        }
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("save file");
        final int returnVal = chooser.showSaveDialog(this);
        // hier weiter wenn der modale FileDialog geschlossen wurde
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (f.getAbsolutePath().endsWith(".log") == false) {
                f = new File(f.getAbsolutePath() + ".log");
            }
            saveDocument(f);
        }
    }

    public void saveDocument(File file) {
        if (file != null) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("saveDocument(file=" + file + ")");
                }
                BufferedWriter bwFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                String text = jTextArea.getText();
                bwFile.write(text);
                bwFile.close();
                this.file = file;
                changed = false;
            } catch (FileNotFoundException e) {
                logger.error("loadDocument file=" + file + " failed:" + e.getMessage(), e);
            } catch (IOException e) {
                logger.error("loadDocument file=" + file + " failed:" + e.getMessage(), e);
            }
        } else {
            this.file = null;
        }
    }

    /**
     * This method initializes jCheckBoxFreeze	
     * @return  javax.swing.JCheckBox
     * @uml.property  name="jCheckBoxAutoScrolling"
     */
    private JCheckBox getJCheckBoxAutoScrolling() {
        if (jCheckBoxAutoScrolling == null) {
            jCheckBoxAutoScrolling = new JCheckBox();
            jCheckBoxAutoScrolling.setText("auto scrolling");
            jCheckBoxAutoScrolling.setSelected(true);
            jCheckBoxAutoScrolling.setToolTipText("shows always the end of text");
        }
        return jCheckBoxAutoScrolling;
    }

    /**
     * This method initializes jCheckBoxLoggingEnabled	
     * @return  javax.swing.JCheckBox
     * @uml.property  name="jCheckBoxLoggingEnabled"
     */
    private JCheckBox getJCheckBoxLoggingEnabled() {
        if (jCheckBoxLoggingEnabled == null) {
            jCheckBoxLoggingEnabled = new JCheckBox();
            jCheckBoxLoggingEnabled.setText("logging on");
            jCheckBoxLoggingEnabled.setSelected(loggingEnabled);
            jCheckBoxLoggingEnabled.addItemListener(new java.awt.event.ItemListener() {
                @Override
				public void itemStateChanged(java.awt.event.ItemEvent e) {
                    loggingEnabled = jCheckBoxLoggingEnabled.isSelected();
                }
            });
        }
        return jCheckBoxLoggingEnabled;
    }

    /**
     * This method initializes jCheckBoxLoggingEnabled	
     * @return  javax.swing.JCheckBox
     * @uml.property  name="jCheckBoxLoggingEnabled"
     */
    private JCheckBox getJCheckBoxDebugEnabled() {
        if (jCheckBoxDebugEnabled == null) {
        	jCheckBoxDebugEnabled = new JCheckBox();
        	jCheckBoxDebugEnabled.setText("debug");
        	jCheckBoxDebugEnabled.setSelected(Logger.getRootLogger().isDebugEnabled());
        	jCheckBoxDebugEnabled.addItemListener(new java.awt.event.ItemListener() {
                @Override
				public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (jCheckBoxDebugEnabled.isSelected()) {
                    	Logger.getRootLogger().setLevel(Level.DEBUG);
                    } else {
                    	Logger.getRootLogger().setLevel(Level.INFO);
                    }
                }
            });
        }
        return jCheckBoxDebugEnabled;
    }

    /**
     * @return  the suggestedFileName
     * @uml.property  name="suggestedFileName"
     */
    public final String getSuggestedFileName() {
        return suggestedFileName;
    }

    /**
     * @param suggestedFileName  the suggestedFileName to set
     * @uml.property  name="suggestedFileName"
     */
    public final void setSuggestedFileName(String suggestedFileName) {
        this.suggestedFileName = suggestedFileName;
        this.file = null;
    }

    /**
     * @return  the changed
     * @uml.property  name="changed"
     */
    public final boolean isChanged() {
        return changed;
    }

} //  @jve:decl-index=0:visual-constraint="10,10"