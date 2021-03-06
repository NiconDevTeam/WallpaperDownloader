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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import es.estoes.wallpaperDownloader.util.PreferencesManager;
import es.estoes.wallpaperDownloader.util.WDUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;

public class PathChangerWindow extends JFrame {

	/**
	 * Constants.
	 */
	private static ResourceBundle i18nBundle;
	private static final long serialVersionUID = 1L;

	/**
	 * Attributes.
	 */
	private JTextField newPath;
	private JCheckBox oldFolderCheckbox;

	/**
	 * Create the frame.
	 * @param mainWindow 
	 */
	public PathChangerWindow(final WallpaperDownloader mainWindow, String whatToChange) {
		// Resource bundle for i18n
		i18nBundle = WDUtilities.getBundle();
		
		// DISPOSE_ON_CLOSE for closing only this frame instead of the entire application
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBackground(new Color(255, 255, 255));
		switch (whatToChange) {
		case WDUtilities.DOWNLOADS_DIRECTORY:
			setTitle(i18nBundle.getString("path.changer.downloads.directory.title"));
			break;
		case WDUtilities.CHANGER_DIRECTORY:
			setTitle(i18nBundle.getString("path.changer.changer.directory.title"));
			break;
		case WDUtilities.MOVE_DIRECTORY:
			setTitle(i18nBundle.getString("path.changer.move.favorite.directory.title"));
			break;
		default:
			break;
		}
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		
		// Centering window
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(x, y);
		
		newPath = new JTextField();
		newPath.setBounds(12, 33, 298, 31);
		getContentPane().add(newPath);
		newPath.setColumns(10);
		switch (whatToChange) {
		case WDUtilities.DOWNLOADS_DIRECTORY:
			newPath.setText(WDUtilities.getDownloadsPath());
			break;
		case WDUtilities.CHANGER_DIRECTORY:
			newPath.setText(WDUtilities.getChangerPath());
			break;
		case WDUtilities.MOVE_DIRECTORY:
			newPath.setText(WDUtilities.getMoveFavoritePath());
			break;
		default:
			break;
		}
		
		JLabel lblSelectDownloadsPath = null;
		switch (whatToChange) {
		case WDUtilities.DOWNLOADS_DIRECTORY:
			lblSelectDownloadsPath = new JLabel(i18nBundle.getString("path.changer.downloads.directory.select"));
			break;
		case WDUtilities.CHANGER_DIRECTORY:
			lblSelectDownloadsPath = new JLabel(i18nBundle.getString("path.changer.changer.directory.select"));
			break;
		case WDUtilities.MOVE_DIRECTORY:
			lblSelectDownloadsPath = new JLabel(i18nBundle.getString("path.changer.move.favorite.directory.select"));
			break;
		default:
			break;
		}
		lblSelectDownloadsPath.setBounds(12, 12, 312, 15);
		getContentPane().add(lblSelectDownloadsPath);
				
		JButton btnSelectPath = new JButton(i18nBundle.getString("path.changer.select"));
		btnSelectPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set Locale to English
				JComponent.setDefaultLocale(Locale.ENGLISH);
				final JFileChooser fileChooser = new JFileChooser();
			    fileChooser.setDialogTitle(i18nBundle.getString("path.changer.choose"));
			    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    // Disable the "All files" option.
			    fileChooser.setAcceptAllFileFilterUsed(false);
			    // Show hiding files
			    fileChooser.setFileHidingEnabled(false);

			    if (fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) { 
			    	newPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		btnSelectPath.setBounds(317, 35, 117, 25);
		getContentPane().add(btnSelectPath);
		
		JButton btnApplyPathChange = new JButton(i18nBundle.getString("path.changer.apply"));
		btnApplyPathChange.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				PreferencesManager prefm = PreferencesManager.getInstance();
				switch (whatToChange) {
				case WDUtilities.DOWNLOADS_DIRECTORY:
					// All the downloaded wallpapers are moved to the new location
					// Stop harvesting process
					mainWindow.getHarvester().stop();
					WDUtilities.moveDownloadedWallpapers(newPath.getText(), oldFolderCheckbox.isSelected());
					// Change Downloads path within application properties and WDUtilities
					prefm.setPreference("application-downloads-folder", newPath.getText());
					WDUtilities.setDownloadsPath(newPath.getText());
					// Start harvesting process
					mainWindow.getHarvester().start();
					// Refresh new path in main window
					mainWindow.getDownloadsDirectory().setText(newPath.getText());
					break;
				case WDUtilities.CHANGER_DIRECTORY:
					// Change Changer directory path within application properties
					String changerFoldersProperty = prefm.getPreference("application-changer-folder");
					changerFoldersProperty = changerFoldersProperty + ";" + newPath.getText();
					prefm.setPreference("application-changer-folder", changerFoldersProperty);
					// Refresh new path in main window
					mainWindow.getListDirectoriesModel().addElement(newPath.getText());
					if (mainWindow.getListDirectoriesModel().size() > 1) {
						mainWindow.getAppSettingsPanel().add(mainWindow.getBtnRemoveDirectory());
						mainWindow.getAppSettingsPanel().repaint();
					}

					// Information
					if (WDUtilities.getLevelOfNotifications() > 0) {
						DialogManager info = new DialogManager(i18nBundle.getString("path.changer.changer.directory.message") + newPath.getText(), 2000);
						info.openDialog();
					}
					break;
				case WDUtilities.MOVE_DIRECTORY:
					// Change Move favorite directory path within application properties
					prefm.setPreference("move-favorite-folder", newPath.getText());
					// Refresh new path in main window
					mainWindow.getMoveDirectory().setText(newPath.getText());
					// Information
					if (WDUtilities.getLevelOfNotifications() > 0) {
						DialogManager info1 = new DialogManager(i18nBundle.getString("path.changer.move.directory.message") + " " + newPath.getText(), 2000);
						info1.openDialog();
					}
					break;
				default:
					break;
				}					
			
				// Close frame
				dispose();
			}
		});
		btnApplyPathChange.setBounds(361, 236, 73, 25);
		getContentPane().add(btnApplyPathChange);
		
		JButton btnCancel = new JButton(i18nBundle.getString("path.changer.cancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnCancel.setBounds(12, 236, 91, 25);
		getContentPane().add(btnCancel);
		
		if (whatToChange.equals(WDUtilities.DOWNLOADS_DIRECTORY)) {
			oldFolderCheckbox = new JCheckBox(i18nBundle.getString("path.changer.delete.directory"));
			oldFolderCheckbox.setBounds(12, 76, 422, 23);
			getContentPane().add(oldFolderCheckbox);
		}
	}
}
