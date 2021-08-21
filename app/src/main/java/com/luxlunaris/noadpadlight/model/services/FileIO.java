package com.luxlunaris.noadpadlight.model.services;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.luxlunaris.noadpadlight.R;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;


import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

		ZipFile zipped = new ZipFile(destPath+".zip");

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


	/**
	 * Given a sourcefile's filepath clone it somewhere else.
	 * @param sourcePath
	 * @param destPath
	 */
	public static void copyFile(String sourcePath, String destPath){

		try {
			FileUtils.copyFile(new File(sourcePath), new File(destPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete a directory recursively.
	 * @param path
	 */
	public static void deleteDirectory(String path){
		try {
			FileUtils.deleteDirectory(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copy a directory.
	 * @param sourcePath
	 * @param destPath
	 */
	public static void copyDirectory(String sourcePath, String destPath){

		try {
			FileUtils.copyDirectory(new File(sourcePath), new File(destPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Copy an array of files to a destination directory.
	 * (Does not remove files already in the directory).
	 * @param files
	 * @param destDirPath
	 */
	public static void copyFilesToDirectory(File[] files, String destDirPath){
		File destDir = new File(destDirPath);

		if(!destDir.exists()){
			destDir.mkdirs();
		}

		for(File file : files){
			try {

				if(file.isFile()){
					FileUtils.copyFileToDirectory(file, destDir);
				}

				if(file.isDirectory()){
					FileUtils.copyDirectoryToDirectory(file, destDir);
				}

			} catch (IOException e) {
				e.printStackTrace();
				Log.d("exporting", "exception " +e.getMessage());
			}
		}
	}

	/**
	 * Given the uri of an external file, make a copy of it
	 * in app-internal storage and return it.
	 * @param contentUri
	 * @return
	 */
	public static File getFileFromUri(Context context, Uri contentUri) {
		//Use content Resolver to get the input stream that it holds the data and copy that in a temp file of your app file directory for your references
		File selectedFile = new File(context.getCacheDir(), "import"); //your app file dir or cache dir you can use


		Log.d("EXTERNAL_INTENT", selectedFile.exists()+" "+selectedFile.isFile());

		selectedFile.delete();
		deleteDirectory(selectedFile.getPath());

		Log.d("EXTERNAL_INTENT", "after deletion: "+selectedFile.exists()+" "+selectedFile.isFile());

		try {

			InputStream in = context.getContentResolver().openInputStream(contentUri);
			OutputStream out = new FileOutputStream(selectedFile);

			byte[] buf = new byte[1024];
			int len;

			if (in != null) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return selectedFile;
	}


	public static void exportFile(Context context, File file, String mimeType){
		Uri uri = FileProvider.getUriForFile(context, "com.luxlunaris.fileprovider", file);

		//create an intent to share the backup file with another app
		Intent intent = new Intent(Intent.ACTION_SEND);
		//intent.setType("application/zip");
		intent.setType(mimeType);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		context.startActivity(Intent.createChooser(intent, context.getString(R.string.export)));

	}

	/**
	 * Clear the contents of a directory w/out
	 * deleting the directory itself.
	 * @param path
	 */
	/*
	public static void clearDirectory(String path){
		for(File file : new File(path).listFiles()){
			if(file.isDirectory()){
				deleteDirectory(file.getPath());
			}else{
				file.delete();
			}
		}
	}

	 */












}
