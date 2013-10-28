package bus.ticketer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import bus.ticketer.objects.Ticket;

import android.os.Environment;
import android.util.SparseArray;

public class FileHandler {

	private String filename;
	private String toWrite;

	public FileHandler(String filename, String toWrite) {
		this.filename = filename;
		this.toWrite = toWrite;
	}

	public FileHandler() {
	}

	public File getAlbumStorageDir(String filename) {
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS),
				filename);
		if (!file.mkdirs()) {
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
	
	public static boolean checkFileExistance(String name) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS), "BusTicketer");
		File[] files = file.listFiles();
		
		for(File f : files) {
			if(f.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public static SparseArray<ArrayList<Ticket>> getTicketCount() {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS), "BusTicketer");
		File[] files = file.listFiles();
		SparseArray<ArrayList<Ticket>> ret = new SparseArray<ArrayList<Ticket>>();
		ArrayList<Ticket> t1Tickets = new ArrayList<Ticket>();
		ArrayList<Ticket> t2Tickets = new ArrayList<Ticket>();
		ArrayList<Ticket> t3Tickets = new ArrayList<Ticket>();
		
		for(File f : files) {
			if(f.getName().contains("t1Ticket"))
				t1Tickets.add(new Ticket(Integer.parseInt(f.getName().split("-")[1].split("\\.")[0])));
			else if(f.getName().contains("t2Ticket"))
				t2Tickets.add(new Ticket(Integer.parseInt(f.getName().split("-")[1].split("\\.")[0])));
			else if(f.getName().contains("t3Ticket"))
				t3Tickets.add(new Ticket(Integer.parseInt(f.getName().split("-")[1].split("\\.")[0])));
		}
		
		ret.append(1, t1Tickets);
		ret.append(2, t2Tickets);
		ret.append(3, t3Tickets);
		
		return ret;
	}
	
	public void deleteFiles() {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS), "BusTicketer");
		File[] files = file.listFiles();
		
		for(File f : files)
			f.delete();
	}
	
	public void setToWrite(String toWrite) {
		this.toWrite = toWrite;
	}

	public String getUsername() {
		filename = "client";
		return readFromFile().get(0);
	}
	
}
