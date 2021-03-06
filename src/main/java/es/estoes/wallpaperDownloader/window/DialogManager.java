/**
 * Copyright 2016-2018 Eloy García Almadén <eloy.garcia.pca@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.estoes.wallpaperDownloader.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import es.estoes.wallpaperDownloader.util.WDUtilities;

public class DialogManager {

	// Constants
	private static ResourceBundle i18nBundle;
	
	// Attributes
	private final JOptionPane optionPane;
	private final JDialog dialog;
	private final String message;
	private final int time;
	
	
	// Methods
	/**
	 * Constructor
	 * @param message message to display
	 * @param time time in msec to close the message automatically
	 */
	public DialogManager(String message, int time) {
		super();
		// Resource bundle for i18n
		i18nBundle = WDUtilities.getBundle();

		this.message = message;
		this.time = time;
		optionPane = new JOptionPane(this.message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		dialog = new JDialog();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		dialog.setTitle(i18nBundle.getString("dialog.manager.title"));
		dialog.setBackground(new Color(255, 255, 255));
		dialog.setModal(true);	
		dialog.setContentPane(optionPane);	
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
		// Centering dialog after pack method
		int x = (screenSize.width - dialog.getWidth()) / 2;
		int y = (screenSize.height - dialog.getHeight()) / 2;
		dialog.setLocation(x, y);
	}

	/**
	 * Constructor
	 * @param message message to display
	 */
	public DialogManager(String message) {
		super();
		// Resource bundle for i18n
		i18nBundle = WDUtilities.getBundle();

		this.message = message;
		this.time = 0;
		optionPane = new JOptionPane(this.message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		dialog = new JDialog();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		dialog.setTitle(i18nBundle.getString("dialog.manager.title"));
		dialog.setModal(true);	
		dialog.setContentPane(optionPane);	
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
		// Centering dialog after pack method
		int x = (screenSize.width - dialog.getWidth()) / 2;
		int y = (screenSize.height - dialog.getHeight()) / 2;
		dialog.setLocation(x, y);
	}

	/**
	 * Opens dialog.
	 */
	public void openDialog() {
		// Create timer to dispose of dialog after n seconds defined in time attribute
		if (time != 0) {
			Timer timer = new Timer(this.time, new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent ae) {
			        dialog.dispose();
			    }
			});
			
			// The timer should only go off once
			timer.setRepeats(false);

			// Start timer to close JDialog as dialog modal we must start the timer before its visible
			timer.start();
		}
		dialog.setVisible(true);

	}

	/**
	 * Closes dialog.s
	 */
	public void closeDialog() {
		dialog.dispose();
	}
}
