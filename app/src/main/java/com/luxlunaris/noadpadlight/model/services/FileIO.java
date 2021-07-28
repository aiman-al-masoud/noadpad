package com.luxlunaris.noadpadlight.model.services;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileIO {

	/**
	 * Reads text from a file.
	 * @param filePath
	 * @return
	 */
	public static synchronized  String read(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			String buf = null;
			String result="";
			while((buf=reader.readLine())!=null) {
				result+=buf+"\n";
			}
			reader.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Writes text to a file.
	 * @param filePath
	 * @param text
	 */
	public static synchronized void write(String filePath, String text) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
			writer.write(text);
			writer.flush();
			writer.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Appends text to a file.
	 * @param filePath
	 * @param text
	 */
	public static synchronized void append(String filePath, String text) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true));
			writer.write(text);
			writer.flush();
			writer.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Reads a single line from a file.
	 * @param filepath
	 * @return
	 */
	public static synchronized String readLine(String filepath){
		try {
			RandomAccessFile raf = new RandomAccessFile(filepath, "r");
			String buf = raf.readLine();
			raf.close();
			return buf;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * Turn a directory at sourcePath to a zipped file at destPath
	 * @param sourcePath
	 * @param destPath
	 * @return
	 */
	public static File zipDir(String sourcePath, String destPath) {

		ZipFile zipped = new ZipFile(destPath);

		try {
			zipped.addFolder(new File(sourcePath));



		} catch (Exception e) {
			e.printStackTrace();
		}

		return zipped.getFile();
	}


	/**
	 * Turn a zipped file at zippedPath to a folder at destPath.
	 * @param zippedPath
	 * @param destPath
	 * @return
	 */
	public static File unzipDir(String zippedPath, String destPath) {

		ZipFile zipped = new ZipFile(zippedPath);

		try {
			zipped.extractAll(destPath);
		} catch (ZipException e) {
			e.printStackTrace();
		}

		return new File(destPath);
	}



		
	
}
