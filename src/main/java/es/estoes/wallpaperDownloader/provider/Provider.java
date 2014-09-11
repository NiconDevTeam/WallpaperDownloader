package es.estoes.wallpaperDownloader.provider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import es.estoes.wallpaperDownloader.exception.ProviderException;
import es.estoes.wallpaperDownloader.util.PreferencesManager;
import es.estoes.wallpaperDownloader.util.WDUtilities;

public abstract class Provider {
	
	// Constants
	private static final Logger LOG = Logger.getLogger(Provider.class);
	
	// Attributes
	private List<String> keywords;
	private boolean areKeywordsDone;
	protected String activeKeyword;
	private int activeIndex;
	protected String baseURL;
	protected String keywordsProperty;
	protected String resolution;
	
	// Getters & Setters
	public boolean getAreKeywordsDone() {
		return areKeywordsDone;
	}

	// Methods
	/**
	 * Constructor
	 */
	public Provider () {
		PreferencesManager prefm = PreferencesManager.getInstance();
		activeIndex = 0;
		
		// Obtaining resolution
		resolution = prefm.getPreference("wallpaper-resolution");		
	}
	
	/**
	 * This method gets the keywords defined by the user for a specific provider
	 * and split them using delimiter 'zero or more whitespace, ; , zero or more whitespace'
	 */
	public void obtainKeywords() {
		PreferencesManager prefm = PreferencesManager.getInstance();
		String keywordsFromPreferences = prefm.getPreference(keywordsProperty);
		keywords = Arrays.asList(keywordsFromPreferences.split("\\s*" + WDUtilities.PROVIDER_SEPARATOR + "\\s*"));
		if (keywords.isEmpty()) {
			areKeywordsDone = true;
		} else {
			areKeywordsDone = false;
		}
	}

	/**
	 * This method gets the active keyword which will be used for the search
	 */
	protected void obtainActiveKeyword() {
		activeKeyword = keywords.get(activeIndex);
		if (keywords.size() == activeIndex + 1) {
			// The end of the keywords list has been reached. Starts again
			activeIndex = 0;
			areKeywordsDone = true;
		} else {
			activeIndex++;
		}
	}
	
	public void getWallpaper() {
	}
	
	/**
	 * This method checks the disk space within download directory. If it is full, it will remove one or several wallpapers
	 * in order to prepare it to a new download
	 */
	protected void checkAndPrepareDownloadDirectory () {
		LOG.info("Checking download directory. Removing some wallpapers if it is necessary...");
		PreferencesManager prefm = PreferencesManager.getInstance();
		Long maxSize = Long.parseLong(prefm.getPreference("application-max-download-folder-size"));
		File downloadFolder = new File(WDUtilities.getDownloadsPath());
		Long downloadFolderSize = FileUtils.sizeOfDirectory(downloadFolder);
		downloadFolderSize = ((downloadFolderSize / 1024) / 1024); // MBytes
		while (downloadFolderSize > maxSize) {
			File fileToRemove = pickRandomFile();
			try {
				FileUtils.forceDelete(fileToRemove);
				LOG.info(fileToRemove.getPath() + " deleted");
			} catch (IOException e) {
				throw new ProviderException("Error deleting file " + fileToRemove.getPath() + ". Error: " + e.getMessage());
			}
			downloadFolderSize = FileUtils.sizeOfDirectory(downloadFolder);
			downloadFolderSize = ((downloadFolderSize / 1024) / 1024); // MBytes
		}
	}

	@SuppressWarnings("unchecked")
	private File pickRandomFile() {
		File downloadDirectory = new File(WDUtilities.getDownloadsPath());
		List<File> files = (List<File>) FileUtils.listFiles(downloadDirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		Random generator = new Random();
		int index = generator.nextInt(files.size());
		return files.get(index);
	}
}