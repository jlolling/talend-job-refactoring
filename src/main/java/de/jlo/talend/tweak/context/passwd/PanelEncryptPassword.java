package de.jlo.talend.tweak.context.passwd;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PanelEncryptPassword extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField decryptedTextField;
	private JTextField encryptedTextField;
	private JPasswordField pwfield;
	private JButton buttonEncrypt = null;
	private JButton buttonDecrypt = null;

	public PanelEncryptPassword() {
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new GridBagLayout());
		int y = 1;
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = y;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			pwfield = new JPasswordField();
			pwfield.setToolTipText("Set the master password or leaf it empty to use the default");
			pwfield.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					char[] array = pwfield.getPassword();
					if (array != null && array.length > 0) {
						String pw = String.valueOf(array);
						if (pw != null && pw.trim().isEmpty() == false) {
							PasswordEncryptUtil.setRawKey(pw);
						} else {
							PasswordEncryptUtil.setRawKey(null);
						}
					}
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// do nothing
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					// do nothing
				}
				
			});
			add(pwfield, gbc);
		}
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = y;
			gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel label = new JLabel("Master password");
			add(label, gbc);
		}
		y++;
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = y;
			gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
			add(getDecryptedTextField(), gbc);
		}
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = y;
			add(getButtonEncrypt(), gbc);
		}	
		y++;
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = y;
			gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
			add(getEncryptedTextField(), gbc);
		}
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = y;
			add(getButtonDecrypt(), gbc);
		}
	}
	
	private JButton getButtonEncrypt() {
		if (buttonEncrypt == null) {
			buttonEncrypt = new JButton("Encrypt");
			buttonEncrypt.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = decryptedTextField.getText();
					try {
						if (text != null && text.trim().isEmpty() == false) {
							encryptedTextField.setText(PasswordEncryptUtil.encryptPassword(text));
						} else {
							encryptedTextField.setText("");
						}
					} catch (Exception e1) {
						encryptedTextField.setText(e1.getMessage());
					}
				}
			});
		}
		return buttonEncrypt;
	}
	
	private JButton getButtonDecrypt() {
		if (buttonDecrypt == null) {
			buttonDecrypt = new JButton("Decrypt");
			buttonDecrypt.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = encryptedTextField.getText();
					try {
						if (text != null && text.trim().isEmpty() == false) {
							decryptedTextField.setText(PasswordEncryptUtil.decryptPassword(text));
						} else {
							decryptedTextField.setText("");
						}
					} catch (Exception e1) {
						decryptedTextField.setText(e1.getMessage());
					}
				}
			});
		}
		return buttonDecrypt;
	}
	
	private JTextField getEncryptedTextField() {
		if (encryptedTextField == null) {
			encryptedTextField = new JTextField();
			encryptedTextField.setPreferredSize(new Dimension(400, 20));
			encryptedTextField.setToolTipText("Encrypted Password");
		}
		return encryptedTextField;
	}

	private JTextField getDecryptedTextField() {
		if (decryptedTextField == null) {
			decryptedTextField = new JTextField();
			decryptedTextField.setPreferredSize(new Dimension(400, 20));
			decryptedTextField.setToolTipText("Clear text");
		}
		return decryptedTextField;
	}

}
