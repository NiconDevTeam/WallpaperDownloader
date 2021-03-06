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

package es.estoes.wallpaperDownloader.util;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;
import es.estoes.wallpaperDownloader.changer.LinuxWallpaperChanger;
import es.estoes.wallpaperDownloader.changer.WallpaperChanger;
import es.estoes.wallpaperDownloader.window.ChooseWallpaperWindow;
import es.estoes.wallpaperDownloader.window.DialogManager;
import es.estoes.wallpaperDownloader.window.WallpaperDownloader;
import es.estoes.wallpaperDownloader.window.WallpaperManagerWindow;
import org.apache.commons.io.comparator.*;

/**
 * This class gathers all the utilities needed for the correct behavior of the application. 
 * It implements a kind of Singleton pattern not pure and only based on static methods. 
 * Actually, there won't be an object of this class ever 
 * @author egarcia
 *
 */
public class WDUtilities {

	// Constants
	protected static final Logger LOG = Logger.getLogger(WDUtilities.class);
	private static PropertiesManager pm = PropertiesManager.getInstance();
	
	public static final String APP_YES = "yes";
	public static final String APP_NO = "no";
	public static final String PROVIDER_SEPARATOR = ";";
	public static final String QM = "?";
	public static final String EQUAL = "=";
	public static final String AND = "&";
	public static final String URL_SLASH = "/";
	public static final String UNDERSCORE = "_";
	public static final String PERIOD = ".";
	public static final String DASH = "-";
	public static final String COLON = ":";
	public static final String WD_PREFIX = "wd-";
	public static final String DEFAULT_DOWNLOADS_DIRECTORY = "downloads";
	public static final String UNIT_MB = "MB";
	public static final String UNIT_KB = "KB";
	public static final String WD_FAVORITE_PREFIX = "fwd-";
	public static final String SORTING_BY_DATE = "sort_by_date";
	public static final String SORTING_NO_SORTING = "no_sorting";
	public static final String SORTING_MULTIPLE_DIR = "multiple_directories";
	public static final String WD_ALL = "all";
	public static final String OS_LINUX = "Linux";
	public static final String OS_WINDOWS = "Windows";
	public static final String OS_WINDOWS_7 = "Windows 7";
	public static final String OS_WINDOWS_10 = "Windows 10";
	public static final String OS_UNKNOWN = "UNKNOWN";
	public static final String DE_UNITY = "Unity";
	public static final String DE_GNOME = "GNOME";
	public static final String DE_KDE = "KDE";
	public static final String DE_MATE = "MATE";
	public static final String DE_XFCE = "XFCE";
	public static final String DE_UNKNOWN = "UNKNOWN";
	public static final String DE_GNOME3 = "GNOME3";
	public static final String DE_CINNAMON = "Cinnamon";
	public static final String DE_PANTHEON = "Pantheon";
	public static final String DOWNLOADS_DIRECTORY = "downloads_directory";
	public static final String CHANGER_DIRECTORY = "changer_directory";
	public static final String MOVE_DIRECTORY = "move_directory";
	public static final String PLASMA_SCRIPT = "plasma-changer.sh";
	public static final String SCRIPT_LOCATION = "/scripts/";
	public static final String IMG_JPG_SUFFIX = "jpg";
	public static final String IMG_JEPG_SUFFIX = "jpeg";
	public static final String IMG_PNG_SUFFIX = "png";
	public static final String WD_DESKTOP_FILE = "wallpaperdownloader.desktop";
	public static final String WD_SNAP_DESKTOP_FILE = "snap.wallpaperdownloader.desktop";
	public static final String DESKTOP_LOCATION = "/desktop/";
	public static final String WD_ICON_FILE = "wallpaperdownloader.svg";
	public static final String ICON_LOCATION = "/images/desktop/";
	
	public static final int STATUS_CODE_200 = 200;
	public static final CharSequence SNAP_KEY = "snap";
	

	// Attributes
	private static String appPath;
	private static String downloadsPath;
	private static String userConfigurationFilePath;
	private static String operatingSystem;
	private static WallpaperChanger wallpaperChanger;
	
	private static Dimension screenSize;
	public static Double width, height;
	
	private static File directory;
	private static long space;

	// Getters & Setters
	public static String getAppPath() {
		return appPath;
	}

	public static void setAppPath(String appPath) {
		WDUtilities.appPath = appPath;
	}

	public static String getDownloadsPath() {
		return downloadsPath;
	}

	public static void setDownloadsPath(String downloadsPath) {
		WDUtilities.downloadsPath = downloadsPath;
	}

	public static String getUserConfigurationFilePath() {
		return userConfigurationFilePath;
	}

	public static void setUserConfigurationFilePath(
			String userConfigurationFilePath) {
		WDUtilities.userConfigurationFilePath = userConfigurationFilePath;
	}
	
	public static String getBlacklistDirectoryPath() {
		Path blacklistPath = Paths.get(appPath.toString());
		blacklistPath = blacklistPath.resolve(pm.getProperty("app.blacklist.path"));
		return blacklistPath.toString();
	}

	public static String getOperatingSystem() {
		return operatingSystem;
	}

	public static void setOperatingSystem(String operatingSystem) {
		WDUtilities.operatingSystem = operatingSystem;
	}

	public static WallpaperChanger getWallpaperChanger() {
		return wallpaperChanger;
	}

	public static void setWallpaperChanger(WallpaperChanger wallpaperChanger) {
		WDUtilities.wallpaperChanger = wallpaperChanger;
	}
	
	// Methods (All the methods are static)

	/**
	 * Constructor
	 */
	private WDUtilities() {

	}

	/**
	 * Get screen resolution.
	 * @return
	 */
	public static String getResolution() {
		return getWidthResolution() + "x" + getHeightResolution();
	}

	/**
	 * Get screen width resolution.
	 * @return
	 */
	public static int getWidthResolution() {
		return width.intValue();
	}
	
	/**
	 * Get screen height resolution.
	 * @return
	 */
	public static int getHeightResolution() {
		return height.intValue();
	}
	
	/**
	 * Autor: Frederick Salazar
	 * Fecha: 26 nov 2018
	 * descr: obtiene las propiedades de la pantalla
	 */
	
	public static void getDimensionScreen(){
		try{
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			width = screenSize.getWidth();
			height = screenSize.getHeight();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Get all the wallpapers from a directory.
	 * @param wallpapersType Type of the wallpapers wanted to retrieve (favorite, non favorite, all)
	 * @param directory Directory to gell all the wallpapers
	 * @return a list of File
	 */
	public static List<File> getAllWallpapers(String wallpapersType, String directory) {
		LOG.info("Getting all the wallpapers in " + directory + "...");
		File downloadDirectory;
		if (directory.equals(WDUtilities.DOWNLOADS_DIRECTORY)) {
			downloadDirectory = new File(WDUtilities.getDownloadsPath());
		} else {
			downloadDirectory = new File(directory);
		}
		List<File> wallpapers = new ArrayList<File>();
		if (wallpapersType.equals(WDUtilities.WD_ALL)) {
			wallpapers = (List<File>) FileUtils.listFiles(downloadDirectory, FileFilterUtils.suffixFileFilter(WDUtilities.IMG_JEPG_SUFFIX), null);
			wallpapers.addAll((List<File>) FileUtils.listFiles(downloadDirectory, FileFilterUtils.suffixFileFilter(WDUtilities.IMG_JPG_SUFFIX), null));
			wallpapers.addAll((List<File>) FileUtils.listFiles(downloadDirectory, FileFilterUtils.suffixFileFilter(WDUtilities.IMG_PNG_SUFFIX), null));
		} else {
			wallpapers = (List<File>) FileUtils.listFiles(downloadDirectory, FileFilterUtils.prefixFileFilter(wallpapersType), null);
		}
				
		return wallpapers;
	}

	/**
	 * Move all the wallpapers to a new location.
	 * @param newPath
	 */
	public static void moveDownloadedWallpapers(String newPath, boolean deleteOldFolder) {
		if (!WDUtilities.getDownloadsPath().equals(newPath)) {
			File destDir = new File(newPath);
			File srcDir = new File(WDUtilities.getDownloadsPath());
			// Get all the wallpapers from the current location
			List<File> wallpapers = getAllWallpapers(WDUtilities.WD_ALL, WDUtilities.DOWNLOADS_DIRECTORY);

			// Move every file to the new location
			Iterator<File> wallpaperIterator = wallpapers.iterator();
			while (wallpaperIterator.hasNext()) {
				File wallpaper = wallpaperIterator.next();
				try {
					FileUtils.moveFileToDirectory(wallpaper , destDir, true);
					if (LOG.isInfoEnabled()) {
						LOG.info("Wallpaper " + wallpaper.getAbsolutePath() + " has been moved to " + destDir.getAbsolutePath());
					}
				} catch (IOException e) {
					// Something went wrong
					LOG.error("Error moving file " + wallpaper.getAbsolutePath());
					// Information
					DialogManager info = new DialogManager("Something went wrong. Downloads directory couldn't be changed. Check log for more information.", 2000);
					info.openDialog();
				}
			}

			// Remove old directory if it is necessary
			if (deleteOldFolder) {
				try {
					FileUtils.deleteDirectory(srcDir);
					if (LOG.isInfoEnabled()) {
						LOG.info("Old downlad folder " + srcDir.getAbsolutePath() + " has been removed");
					}
				} catch (IOException e) {
					// Something went wrong
					LOG.error("The original downloads directory " + WDUtilities.getDownloadsPath() + " couldn't be removed. Please, check also directory " + destDir.getAbsolutePath() + " because all the wallpapers were copyied there");
					// Information
					DialogManager info = new DialogManager("Something went wrong. Downloads directory couldn't be changed. Check log for more information.", 2000);
					info.openDialog();
				}
			}

			// Information
			if (WDUtilities.getLevelOfNotifications() > 0) {
				DialogManager info = new DialogManager("Dowloads directory has been succesfully changed to " + destDir.getAbsolutePath(), 2000);
				info.openDialog();
			}
			if (LOG.isInfoEnabled()) {
				LOG.info("Downloads directory has been successfully changed to " + destDir.getAbsolutePath());
			}

		}
	}

	/**
	 * Calculates the percentage of the space occupied within the directory.
	 * @param directoryPath
	 * @return int
	 */
	public static int getPercentageSpaceOccupied(String directoryPath) {
		
		PreferencesManager prefm = PreferencesManager.getInstance();
		int downloadsDirectorySize = new Integer(prefm.getPreference("application-max-download-folder-size"));
		long spaceLong = WDUtilities.getDirectorySpaceOccupied(directoryPath, WDUtilities.UNIT_KB);
		// Obtaining percentage
		int percentage = (int) ((spaceLong * 100) / (downloadsDirectorySize * 1024));
		if (percentage > 100) {
			percentage = 100;
		}
		LOG.info("Downloads directory space occupied: " + percentage + " %");
		return percentage;
	}

	/**
	 * Calculate the space occupied within the directory.
	 * @param directoryPath
	 * @param unit
	 * @return
	 */
	public static long getDirectorySpaceOccupied(String directoryPath, String unit) {
		space = 0;
		try {
			directory = new File(directoryPath);
			// Calculates the space in bytes
			space = FileUtils.sizeOfDirectory(directory);		
			
			if (unit.equals(WDUtilities.UNIT_MB)) {
				// Turning bytes into Megabytes
				space = (space / 1024) / 1024;
			} else if (unit.equals(WDUtilities.UNIT_KB)) {
				// Turning	 bytes into Kilobytes
				space = Math.round(Math.ceil(space/1024.0));
			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			directory = null;
		}
		return space;
	}

	/**
	 * Set wallpapers as favorite.
	 * @param originalAbsolutePath
	 */
	public static void setFavorite(List<String> originalAbsolutePaths) {
		Iterator<String> wallpaperIterator = originalAbsolutePaths.iterator();
		while (wallpaperIterator.hasNext()) {
			String currentWallpaperPath = wallpaperIterator.next();
			File originalWallpaper = new File(currentWallpaperPath);
			String wallpaperOriginalName = originalWallpaper.getName();
			int index = wallpaperOriginalName.indexOf("-");
			String wallpaperFavoriteName = WD_FAVORITE_PREFIX + wallpaperOriginalName.substring(index + 1);
			File favoriteWallpaper = new File(WDUtilities.getDownloadsPath() + File.separatorChar + wallpaperFavoriteName);
			originalWallpaper.renameTo(favoriteWallpaper);

			// Information
			if (WDUtilities.getLevelOfNotifications() > 1) {
				if (!wallpaperIterator.hasNext()) {
					DialogManager info = new DialogManager("Wallpaper/s set as favorite.", 2000);
					info.openDialog();
				}
			}
			WallpaperDownloader.refreshJScrollPane();
			LOG.info(currentWallpaperPath + " wallpaper has been set as favorite");
		}		
	}

	/**
	 * Set wallpapers as no favorite.
	 * @param originalAbsolutePath
	 */
	public static void setNoFavorite(List<String> originalAbsolutePaths) {
		Iterator<String> wallpaperIterator = originalAbsolutePaths.iterator();
		while (wallpaperIterator.hasNext()) {
			String currentWallpaperPath = wallpaperIterator.next();
			File originalWallpaper = new File(currentWallpaperPath);
			String wallpaperOriginalName = originalWallpaper.getName();
			int index = wallpaperOriginalName.indexOf("-");
			String wallpaperNoFavoriteName = WD_PREFIX + wallpaperOriginalName.substring(index + 1);
			File favoriteWallpaper = new File(WDUtilities.getDownloadsPath() + File.separatorChar + wallpaperNoFavoriteName);
			originalWallpaper.renameTo(favoriteWallpaper);
			
			// Information
			if (WDUtilities.getLevelOfNotifications() > 1) {
				if (!wallpaperIterator.hasNext()) {
					DialogManager info = new DialogManager("Wallpaper/s set as no favorite.", 2000);
					info.openDialog();
				}
			}
			WallpaperDownloader.refreshJScrollPane();
			LOG.info(currentWallpaperPath + " wallpaper has been set as no favorite");
		}	
	}

	/**
	 * Remove wallpapers.
	 * @param originalAbsolutePaths
	 */
	public static void removeWallpaper(List<String> originalAbsolutePaths, Boolean notification) {
		Iterator<String> wallpaperIterator = originalAbsolutePaths.iterator();
		while (wallpaperIterator.hasNext()) {
			String currentWallpaperPath = wallpaperIterator.next();
			File originalWallpaper = new File(currentWallpaperPath);
			try {
				// 1.- File is deleted
				FileUtils.forceDelete(originalWallpaper);
				// 2.- File is added to the blacklist
				WDUtilities.addToBlacklist(originalWallpaper.getName());
				// 3.- Information about deleting is displayed if it is required
				if (WDUtilities.getLevelOfNotifications() > 1) {
					if (notification && !wallpaperIterator.hasNext()) {
						DialogManager info = new DialogManager("Wallpaper/s removed.", 2000);
						info.openDialog();
					}
				}
				if (LOG.isInfoEnabled()) {
					LOG.info(currentWallpaperPath + " wallpaper has been removed");
					LOG.info("Refreshing space occupied progress bar...");
				}
				WallpaperDownloader.refreshProgressBar();
				WallpaperDownloader.refreshJScrollPane();
			} catch (IOException e) {
				LOG.error("The wallpaper " + currentWallpaperPath + " couldn't be removed. Error: " + e.getMessage());
			}
		}		
	}

	/**
	 * Adds a file to the blacklist directory.
	 * @param fileName the name of the file
	 */
	public static void addToBlacklist(String fileName) {
		File blacklistedFile = new File(WDUtilities.getBlacklistDirectoryPath() + File.separator + fileName);
		try {
			FileUtils.touch(blacklistedFile);
			if (LOG.isInfoEnabled()) {
				LOG.info(fileName + " added to the blacklist");	
			}
		} catch (IOException exception) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Error adding " + fileName + " to the blacklist. Error: " + exception.getMessage());	
			}
		}
	}

	/**
	 * Get all wallpapers downloaded sorted by date.
	 * @return
	 */
	public static File[] getAllWallpapersSortedByDate(String wallpapersType) {
		List<File> wallpaperList = getAllWallpapers(wallpapersType, WDUtilities.DOWNLOADS_DIRECTORY);
		File[] wallpapers = new File[wallpaperList.size()];
		wallpapers = wallpaperList.toArray(wallpapers);
		Arrays.sort(wallpapers, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
		return wallpapers;
	}
	
/**
 * Get the image icon for the wallpapers.
 * @param numWallpapers number of wallpapers
 * @param from first wallpaper to get
 * @param sort sorting
 * @param wallpapersType type of the wallpapers to get (favorite, no favorite)
 * @return
 */
	public static ImageIcon[] getImageIconWallpapers(int numWallpapers, int from, String sort, String wallpapersType) {
		File[] wallpapers = {};
		List<File> wallpapersList = new ArrayList<File>();
		int to = from + numWallpapers;
		int j = 0;
		
		switch (sort) {
		case SORTING_BY_DATE:
			wallpapers = getAllWallpapersSortedByDate(wallpapersType);
			break;
		case SORTING_NO_SORTING:
			wallpapersList = getAllWallpapers(wallpapersType, WDUtilities.DOWNLOADS_DIRECTORY);
			wallpapers = wallpapersList.toArray(wallpapers);
			break;
		case SORTING_MULTIPLE_DIR:
			// Retrieves all the wallpapers stored on different directories set by the user in the changer
			// property
			PreferencesManager prefm = PreferencesManager.getInstance();
			String changerFoldersProperty = prefm.getPreference("application-changer-folder");
			String[] changerFolders = changerFoldersProperty.split(";");
			for (int index = 0; index < changerFolders.length; index ++) {
				String directory = changerFolders[index];
				wallpapersList.addAll(getAllWallpapers(wallpapersType, directory));
			}
			wallpapers = wallpapersList.toArray(wallpapers);
			break;
		default:
			break;
		}
		
		switch (wallpapersType) {
		case WDUtilities.WD_ALL:
			// Adding total number of favorite wallpapers to ChooseWallpaperWindow if it exists
			ChooseWallpaperWindow.refreshWallpapersTotalNumber(wallpapers.length);				
			break;
		case WDUtilities.WD_FAVORITE_PREFIX:
			// Adding total number of favorite wallpapers to WallpaperManagerWindow if it exists
			WallpaperManagerWindow.refreshFavoriteWallpapersTotalNumber(wallpapers.length);				
			break;
		case WDUtilities.WD_PREFIX:
			if (WallpaperManagerWindow.lblTotalNoFavoriteWallpapers != null) {
				// Adding total number of no favorite wallpapers to WallpaperManagerWindow if it exists only if WallpaperManagerWindow has been
				// instantiated
				WallpaperManagerWindow.refreshNoFavoriteWallpapersTotalNumber(wallpapers.length);				
			}
			// Adding total number of favorite wallpapers to ChooseWallpaperWindow if it exists
			ChooseWallpaperWindow.refreshWallpapersTotalNumber(wallpapers.length);				
			break;
		default:
			break;
		}
		
		int wallpapersLength = wallpapers.length;
		if (to > wallpapersLength) {
			to = wallpapersLength;
		}
		ImageIcon[] wallpaperIcons = new ImageIcon[0];
		if (wallpapers.length > 0) {
			wallpaperIcons = new ImageIcon[numWallpapers];
			for (int i = from; i < to; i++) {
				ImageIcon originalIcon = new ImageIcon(wallpapers[wallpapersLength - (i + 1)].getAbsolutePath());
				Image scaledImg = WDUtilities.getScaledImage(originalIcon.getImage(), 127, 100);
				ImageIcon resizedIcon = new ImageIcon(scaledImg);
				// Setting a description (absolute path). Doing this. it will be possible to know this path later
				resizedIcon.setDescription(wallpapers[wallpapersLength - (i + 1)].getAbsolutePath());
				wallpaperIcons[j] = resizedIcon;
				j ++;
			}
		}
		return wallpaperIcons;
	}
	
	/**
	 * It picks a random file from a directory.
	 * @param includeFavoriteWallpapers if it is set to TRUE, this method will pick favorite and not favorite wallpapers
	 * @return
	 */
	public static File pickRandomFile(Boolean includeFavoriteWallpapers, String directory) {
		String wallpapersType = WDUtilities.WD_ALL;
		if (!includeFavoriteWallpapers) {
			wallpapersType = WDUtilities.WD_PREFIX;
		}
		List<File> files = WDUtilities.getAllWallpapers(wallpapersType, directory);
		if (!files.isEmpty()) {
			Random generator = new Random();
			int index = generator.nextInt(files.size());
			return files.get(index);			
		} else {
			return null;
		}
		
	}

	/**
	 * It picks a random image from a directory.
	 * @param directoryPath directory path to get all the images
	 * @return File if there is a wallpaper, null otherwise
	 */
	public static File pickRandomImage(String directoryPath) {

		LOG.info("Getting all the images in " + directoryPath + "...");
		File directory = new File(directoryPath);
		FilenameFilter imageFilenameFilter = new ImageFilenameFilter();
		File[] images = directory.listFiles(imageFilenameFilter);
		if (images.length > 0) {
			Random generator = new Random();
			int index = generator.nextInt(images.length);
			return images[index];			
		} else {
			return null;
		}
		
	}

	/**
	 * Checks if a wallpaper is blacklisted.
	 * @param wallpaperName wallpaper name
	 * @return boolean
	 */
	public static boolean isWallpaperBlacklisted(String wallpaperName) {
		File blacklistedFile = new File(WDUtilities.getBlacklistDirectoryPath() + File.separator + wallpaperName);
		boolean result = blacklistedFile.exists();
		if (LOG.isInfoEnabled()) {
			LOG.info("Is " + wallpaperName + " blacklisted?: " + result + ".PS: Only wallpapers not blacklisted will be stored");
		}
		return result;
	}
	
	/**
	 * Checks if a wallpaper is favorite.
	 * @param wallpaperAbsolutePath wallpaper absolute path
	 * @return boolean
	 */
	public static boolean isWallpaperFavorite(String wallpaperAbsolutePath) {
		if (wallpaperAbsolutePath.contains(WD_FAVORITE_PREFIX)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if wallpaperdownloader application has been installed via snap package.
	 * @return boolean
	 */
	public static boolean isSnapPackage() {
		boolean result = Boolean.FALSE;
		PreferencesManager prefm = PreferencesManager.getInstance();
		String downloadsDirectoryString = prefm.getPreference("application-first-time-downloads-folder");
		if (downloadsDirectoryString.contains(WDUtilities.SNAP_KEY)) {
			result = Boolean.TRUE;
		}
		return result;

	}

	/**
	 * Checks if the application can be minimized to system tray.
	 * Plasma 5 and GNOME 3 don't support traditional system tray icon and behavior
	 * @return boolean
	 */
	public static boolean isMinimizable() {
		boolean result = Boolean.FALSE;
		if (WDUtilities.getOperatingSystem().equals(WDUtilities.OS_WINDOWS) || 
			WDUtilities.getOperatingSystem().equals(WDUtilities.OS_WINDOWS_7) || 
			WDUtilities.getOperatingSystem().equals(WDUtilities.OS_WINDOWS_10)) {
			result = Boolean.TRUE;
		} else {
			LinuxWallpaperChanger wallpaperChanger = (LinuxWallpaperChanger)WDUtilities.getWallpaperChanger();
			switch (wallpaperChanger.getDesktopEnvironment()) {
			case WDUtilities.DE_UNITY:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_MATE:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_XFCE:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_KDE:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_GNOME:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_GNOME3:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_CINNAMON:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_PANTHEON:
				result = Boolean.TRUE;
				break;
			default:
				break;
			}
		}
		return result;
	}

	/**
	 * Gets changer directory.
	 * @return String
	 */
	public static String getChangerPath() {
		PreferencesManager prefm = PreferencesManager.getInstance();
		String changerDirectoryString = prefm.getPreference("application-changer-folder");
		return changerDirectoryString;
	}

	/**
	 * Gets move favorite directory.
	 * @return String
	 */
	public static String getMoveFavoritePath() {
		PreferencesManager prefm = PreferencesManager.getInstance();
		String moveFavoriteDirectoryString = prefm.getPreference("move-favorite-folder");
		if (moveFavoriteDirectoryString.equals(PreferencesManager.DEFAULT_VALUE)) {
			moveFavoriteDirectoryString = WDUtilities.getDownloadsPath();
		}
		return moveFavoriteDirectoryString;
	}

	/**
	 * Moves all favorite wallpapers to another directory.
	 * @param destDir directory where all the favorite wallpapers are going to be moved.
	 */
	public static void moveFavoriteWallpapers(String destDir) {
		if (!destDir.equals(PreferencesManager.DEFAULT_VALUE)) {
			// Information

			// Get all favorite wallpapers from the current location
			List<File> wallpapers = getAllWallpapers(WDUtilities.WD_FAVORITE_PREFIX, WDUtilities.getDownloadsPath());
			Iterator<File> wallpapersIterator = wallpapers.iterator();
			// Wallpapers will be copied and blacklisted in order to prevent them to be downloaded again
			while (wallpapersIterator.hasNext()) {
				File wallpaper = wallpapersIterator.next();
				File destFile = new File(destDir + File.separator + wallpaper.getName());
				try {
					FileUtils.copyFile(wallpaper, destFile);
					List<String> wallpaperPathList = new ArrayList<String>();
					wallpaperPathList.add(wallpaper.getAbsolutePath());
					WDUtilities.removeWallpaper(wallpaperPathList, Boolean.FALSE);
					if (LOG.isInfoEnabled()) {
						LOG.info(wallpaper.getAbsolutePath() + " favorite wallpaper moved to " + destFile.getAbsolutePath());
					}
				} catch (IOException exception) {
					if (LOG.isInfoEnabled()) {
						LOG.error("Error moving " + wallpaper.getAbsolutePath() + ". Error: " + exception.getMessage());
					}
				}
			}

		}
	}
	
	/**
	 * Gets the level of notifications defined by the user.
	 * @return Integer
	 */
	public static Integer getLevelOfNotifications() {
		PreferencesManager prefm = PreferencesManager.getInstance();
		return new Integer(prefm.getPreference("application-notifications"));
	}

	/**
	 * Scales an image.
	 * @param srcImg original image
	 * @param width width to scale the image
	 * @param height height to scale the image
	 * @return Image scaled image
	 */
	public static Image getScaledImage(Image srcImg, int width, int height){
	    BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, width, height, null);
	    g2.dispose();

	    return resizedImg;
	}

	/**
	 * Checks Internet connectivity.
	 * @return boolean
	 */
	public static boolean checkConnectivity() {
		boolean result = false;
		try {
			String pingCommand = "ping -c 3 www.google.com";
			if (WDUtilities.getOperatingSystem().equals(OS_WINDOWS) ||
				WDUtilities.getOperatingSystem().equals(OS_WINDOWS_7) ||
				WDUtilities.getOperatingSystem().equals(OS_WINDOWS_10)) {
				pingCommand = "ping -n 3 www.google.com";
			} else if (WDUtilities.isSnapPackage()) {
				pingCommand = "curl -I www.google.com";				
			}
			Process process = java.lang.Runtime.getRuntime().exec(pingCommand);
			if (process.waitFor() == 0) {
				result = true;
			} else {
	            if (LOG.isInfoEnabled()) {
            	LOG.error("Internet connection is down.");
            }
			}
		} catch (Exception exception) {
			if (LOG.isInfoEnabled()) {
				LOG.error("Error executing ping command. Message: " + exception.getMessage());
			}
		}
		return result;
	}

	/**
	 * Gets the i18n bundle for displaying the messages
	 * @return
	 */
	public static ResourceBundle getBundle() {
		PreferencesManager prefm = PreferencesManager.getInstance();
		String language = "en";
		switch (prefm.getPreference("application-i18n")) {
		case "0":
			language = "en";
			break;
		case "1":
			language = "es";
			break;
		default:
			language = "en";
			break;
		}		
		Locale.setDefault(Locale.forLanguageTag(language));
		Locale locale = new Locale(language);
		return ResourceBundle.getBundle("I18n", locale);
	}

	/**
	 * Opens a link in a browser depending on the OS.
	 * @param link link to be opened
	 */
	public static void openLinkOnBrowser(String link) {
		switch (WDUtilities.getOperatingSystem()) {
		case WDUtilities.OS_LINUX:
			Process process;
		      try {
		    	  if (WDUtilities.isSnapPackage()) {
			          process = Runtime.getRuntime().exec("/usr/local/bin/xdg-open " + link);
		    	  } else {
			          process = Runtime.getRuntime().exec("xdg-open " + link);
		    	  }
		          process.waitFor();
		          process.destroy();
		      } catch (Exception exception) {
		    	  if (LOG.isInfoEnabled()) {
		    		LOG.error("Browser couldn't be opened. Error: " + exception.getMessage());  
		    	  }
		      }						
		      break;
		default:
		    if (Desktop.isDesktopSupported()) {
	        try {
	          Desktop.getDesktop().browse(new URI(link));
	        } catch (Exception exception) { 
	        	LOG.error(exception.getMessage()); 
	        }
	     }
			break;
		}		
	}

	/**
	 * Checks if the desktop environment is GNOME or derivative.
	 * @return boolean
	 */
	public static boolean isGnomeish() {
		boolean result = Boolean.FALSE;
		if (WDUtilities.getOperatingSystem().equals(WDUtilities.OS_LINUX)) {
			LinuxWallpaperChanger wallpaperChanger = (LinuxWallpaperChanger)WDUtilities.getWallpaperChanger();
			switch (wallpaperChanger.getDesktopEnvironment()) {
			case WDUtilities.DE_UNITY:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_MATE:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_GNOME:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_GNOME3:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_CINNAMON:
				result = Boolean.TRUE;
				break;
			case WDUtilities.DE_PANTHEON:
				result = Boolean.TRUE;
				break;
			default:
				break;
			}
		}
		return result;
	}

	/**
	 * Changes multi monitor mode for desktop environments such as GNOME or derivatives.
	 * @param mode
	 */
	public static void changeMultiMonitorModeGnomish(String mode) {
      Process process;
      try {
			LinuxWallpaperChanger wallpaperChanger = (LinuxWallpaperChanger)WDUtilities.getWallpaperChanger();
			String command = "gsettings set org.gnome.desktop.background picture-options \"" + mode + "\"";
			switch (wallpaperChanger.getDesktopEnvironment()) {
			case WDUtilities.DE_MATE:
				command = "gsettings set org.mate.background picture-options \"" + mode + "\"";
				break;
			case WDUtilities.DE_CINNAMON:
				command = "gsettings set org.cinnamon.desktop.background picture-options \"" + mode + "\"";
				break;
			default:
				break;
			}
    	  
          process = Runtime.getRuntime().exec(command);

    	  BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    	  BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    	  // Read the output from the command
    	  String processOutput = null;
    	  while ((processOutput = stdInput.readLine()) != null) {
        	  if (LOG.isInfoEnabled()) {
        		  LOG.info(processOutput);
        	  }
    	  }
			
    	  // Read any errors from the attempted command
    	  while ((processOutput = stdError.readLine()) != null) {
        	  if (LOG.isInfoEnabled()) {
        		  LOG.error(processOutput);
        	  }
    	  }

    	  if (LOG.isInfoEnabled()) {
    		LOG.info("Multi monitor mode changed");  
    	  }

          process.waitFor();
          process.destroy();
      } catch (Exception exception) {
    	  if (LOG.isInfoEnabled()) {
    		LOG.error("Multi monitor mode couldn't be changed. Error: " + exception.getMessage());  
    	  }
      }	
	}

	/**
	 * Gets the path where the autostart file should be located within Linux systems.
	 * @return autostart file path
	 */
	public static String getAutostartFilePath() {
		String path = Paths.get(System.getProperty("user.home")).toString();
		if (WDUtilities.isSnapPackage()) {
			path = WDUtilities.sanitazeSnapDirectory(path);
		}
		path = path + File.separator + ".config" + File.separator + "autostart" + File.separator;
		return path;
	}

	/**
	 * Sanitazes a directory if it is within snap structure.
	 * Snap version will be changed to current directory
	 * @param directory
	 * @return sanitazed directory
	 */
	public static String sanitazeSnapDirectory(String directory) {
		 if (directory.contains(WDUtilities.SNAP_KEY)) {
			 // The directory is inside snap structure
			 if (!directory.contains("current")) {
				 // The directory is changed to current directory
				 String[] directoryParts = directory.split(File.separator + WDUtilities.SNAP_KEY + File.separator + "wallpaperdownloader" + File.separator);
				 directory = directoryParts[0] + 
						 							File.separator + 
						 							WDUtilities.SNAP_KEY + 
						 							File.separator + 
						 							"wallpaperdownloader" + 
						 							File.separator +
						 							"current";
				 if (directoryParts[1].contains(File.separator)) {
					 directory = directory + directoryParts[1].substring(directoryParts[1].indexOf(File.separator), directoryParts[1].length());
				 }
			 }
		 }		
		 return directory;
	}

}
