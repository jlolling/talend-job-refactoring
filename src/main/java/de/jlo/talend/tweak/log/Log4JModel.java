package de.jlo.talend.tweak.log;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

/**
 *
 * @author jan
 */
public class Log4JModel extends AbstractTableModel {

	private static final Logger logger = Logger.getLogger(Log4JModel.class);
	private static final long serialVersionUID = 1L;
	private Vector<Logger> loggerList = new Vector<Logger>();
    
    @SuppressWarnings("unchecked")
	public void init() {
    	logger.debug("init");
    	loggerList.removeAllElements();
        Logger rootLogger = Logger.getRootLogger();
        LoggerRepository rep = rootLogger.getLoggerRepository();
        for (Enumeration<Logger> el = rep.getCurrentLoggers(); el.hasMoreElements(); ) {
            loggerList.add(el.nextElement());
        }
        Collections.sort(loggerList, new Comparator<Logger>() {

			public int compare(Logger o1, Logger o2) {
				return o1.getName().compareTo(o2.getName());
			}
        	
		});
        loggerList.add(0, rootLogger);
        fireTableDataChanged();
    }

    public int getRowCount() {
        return loggerList.size();
    }

    public int getColumnCount() {
        return 2;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }
    
    @Override
    public Class<String> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "LoggerName";
            case 1: return "Level";
            default: return "unknown";
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Logger logger = loggerList.get(rowIndex);
        if (columnIndex == 0) {
            return logger.getName();
        } else if (columnIndex == 1) {
            if (logger.getLevel() != null) {
                return logger.getLevel().toString();
            } else {
                return null;
            }
        } else {
            return "unknown columnIndex=" + columnIndex;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Logger logger = loggerList.get(rowIndex);
        if (Level.ALL.toString().equals(value)) {
            logger.setLevel(Level.ALL);
        } else if (Level.ALL.toString().equals(value)) {
            logger.setLevel(Level.ALL);
        } else if (Level.TRACE.toString().equals(value)) {
            logger.setLevel(Level.TRACE);
        } else if (Level.DEBUG.toString().equals(value)) {
            logger.setLevel(Level.DEBUG);
        } else if (Level.ERROR.toString().equals(value)) {
            logger.setLevel(Level.ERROR);
        } else if (Level.FATAL.toString().equals(value)) {
            logger.setLevel(Level.FATAL);
        } else if (Level.INFO.toString().equals(value)) {
            logger.setLevel(Level.INFO);
        } else if (Level.OFF.toString().equals(value)) {
            logger.setLevel(Level.OFF);
        } else if (Level.WARN.toString().equals(value)) {
            logger.setLevel(Level.WARN);
        } 
    }
    
    
  
}
