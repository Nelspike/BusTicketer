package bus.ticketer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.os.Environment;

public class FileWriter {

	private String filename, username;
	private FileOutputStream fileOutStream;
	
	public FileWriter(String name) {
		this.filename = name;
	}
	
	public FileWriter(String name, String username) {
		this.filename = name;
		this.username = username;
		getFileStream();
	}
	
	public void createFile() {
		String toWrite = "Ticket purchased in ";
		toWrite += new Date() + " by " + username + '\n';
		try {
			fileOutStream.write(toWrite.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToFile() {
		String toWrite = "Ticket validated in ";
		toWrite += new Date() + " by " + username + '\n';
		try {
			fileOutStream.write(toWrite.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void getFileStream() {
		File directory = getAlbumStorageDir("BusTicketer");
		File file = new File(directory, filename);

		fileOutStream = null;
		try {
			fileOutStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private File getAlbumStorageDir(String filename) {
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS),
				filename);
		if (!file.mkdirs()) {
		}
		return file;
	}
}
