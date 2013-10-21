package bus.ticketer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class FileHandler {

	private String filename;
	private String toWrite;

	public FileHandler(String filename, String toWrite) {
		this.filename = filename;
		this.toWrite = toWrite;
	}

	public File getAlbumStorageDir(String filename) {
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS),
				filename);
		if (!file.mkdirs()) {
			Log.e("No file", "Directory not created");
		}
		return file;
	}

	public void writeToFile() {
		File directory = getAlbumStorageDir("BusTicketer");
		File file = new File(directory, filename);

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			outputStream.write(toWrite.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> readFromFile() {
		String scan;
		ArrayList<String> ret = new ArrayList<String>();
		File directory = getAlbumStorageDir("BusTicketer");
		File file = new File(directory, filename);

		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fileReader);

		try {
			while ((scan = br.readLine()) != null)
				ret.add(scan);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void deleteFile() {
		File directory = getAlbumStorageDir("BusTicketer");
		File file = new File(directory, filename);
		
		file.delete();
	}
	
	public void setToWrite(String toWrite) {
		this.toWrite = toWrite;
	}

}
