/**
* File: Unzip.java
* Description: Unzip files and list files in .zip file
* TODO: Unzip files recursively.
* Author: Sunil Deshpande
*/
package org.bt.zipunzip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.zip.*;

public class Unzip {
	String fileSeparator = System.getProperty("file.separator");
	String zipFile = null;
	String destPath = null;
	private final byte[] BUFFER = new byte[4096 * 1024];
	public Unzip(String zipFile, String destPath)
	{
		this.zipFile = zipFile;
		if (destPath != null) {
			this.destPath = destPath;
		} else {
			this.destPath = "";
		}

	}
	public void unZipFile() throws ZipException, IOException
	{
		ZipEntry zEntry;
		FileOutputStream fo;
		int bytesRead;

		FileInputStream fi = new FileInputStream(zipFile);
		ZipInputStream zi = new ZipInputStream(fi);
		while((zEntry = zi.getNextEntry()) != null) {
			if (zEntry.getMethod() == ZipEntry.DEFLATED) {
				System.out.println("Deflating file: " + destPath + zEntry.getName());
				fo = new FileOutputStream(destPath + zEntry.getName());

				while ((bytesRead = zi.read(BUFFER)) != -1) {
					fo.write(BUFFER, 0, bytesRead);
				}
				fo.close();
				zi.closeEntry();
			}
		}
		zi.close();
	}
	public void listFiles() throws ZipException, IOException
	{
		ZipEntry zEntry;
		int bytesRead;
		String method = "";
		int count = 0;
		long totalSize = 0;

		FileInputStream fi = new FileInputStream(zipFile);
		ZipInputStream zi = new ZipInputStream(fi);
		System.out.printf("%15s %12s %10s %s\n", "Size","Date", "Method", "File Name");
		System.out.printf("%15s %12s %10s %s\n", "-------------","-----------", "---------", "-------");
		while((zEntry = zi.getNextEntry()) != null) {
			count++;
			totalSize += zEntry.getSize();
			if (zEntry.getMethod() == ZipEntry.DEFLATED) method = "Deflated";
			if (zEntry.getMethod() == ZipEntry.STORED) method = "Stored";
			if (!zEntry.isDirectory()) {
				System.out.printf("%15d %12s %-10s %s\n", zEntry.getSize(), new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date(zEntry.getTime())), method, destPath + zEntry.getName());
			} else {
				System.out.printf("%15s %12s %-10s %s\n", "<DIR>", new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date(zEntry.getTime())), method, destPath + zEntry.getName());
			}
			zi.closeEntry();
		}
		System.out.printf("%15s %12s %10s %s\n", "--------------","", "", "-------");
		System.out.printf("%15d %12s %10s %d files\n", totalSize,"", "", count);
		zi.close();
	}
}
