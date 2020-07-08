/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.jlo.talend.tweak.log;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Level;

/**
 *
 * @author jan
 */
public class Log4JPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();
    private Log4JModel tableModel = new Log4JModel();

    public Log4JPanel() {
        initialize();
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane();
        {
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.weightx = 1;
	        gbc.weighty = 1;
	        gbc.insets = new Insets(2, 2, 2, 2);
	        gbc.fill = GridBagConstraints.BOTH;
	        this.add(scrollPane, gbc);
        }
        scrollPane.setViewportView(table);
        table.setModel(tableModel);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setCellEditor(new LevelCellEditor());
        tableModel.init();
        JButton jButtonRefresh = new JButton("Refresh");
        jButtonRefresh.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
                tableModel.init();
            }
            
        });
        {
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        gbc.insets = new Insets(2, 2, 2, 2);
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.weightx = 1;
	        this.add(jButtonRefresh, gbc);
        }
        
    }
    
    public void refreshLogger() {
    	tableModel.init();
    }
    
    private static class LevelCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;
		private JComboBox<String> cb = null;

        @SuppressWarnings("unchecked")
		LevelCellEditor() {
            super(new JComboBox<String>());
            cb = (JComboBox<String>) super.getComponent();
            cb.addItem(Level.ALL.toString());
            cb.addItem(Level.TRACE.toString());
            cb.addItem(Level.DEBUG.toString());
            cb.addItem(Level.ERROR.toString());
            cb.addItem(Level.FATAL.toString());
            cb.addItem(Level.WARN.toString());
            cb.addItem(Level.INFO.toString());
            cb.addItem(Level.OFF.toString());
        }
            
    }
    

}
