/*
 * Author      : Sunil Deshpande
 * File        : Zip.java
 * Package     : org.bt.zipunzip
 * Class       : Zip
 * Description : This is Java class to Zip file names specified on command line.
 *
 */

package org.bt.zipunzip;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipException;
import java.util.Enumeration;
import java.nio.file.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;
//import org.apache.commons.io.filefilter.WildcardFilter;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.FilenameUtils;
//import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Zip {
	String zipFile = "";
	String[] srcFiles = null;
	boolean bMove = false, bPath = false;
	String strZipDir = "";
	String fileSeparator = System.getProperty("file.separator");
	private final byte[] BUFFER = new byte[4096 * 1024];

	public Zip(String zipFileName, String[] srcFiles, boolean bMove, boolean bPath)
	{
		zipFile = zipFileName;
		this.srcFiles = srcFiles;
		this.bMove = bMove;
		this.bPath = bPath;
		int slashPos = zipFileName.lastIndexOf(fileSeparator);
		if (slashPos > 0) {
			strZipDir = zipFileName.substring(0, slashPos + 1);
		} else {
			zipFile = "." + fileSeparator + zipFile;
		}
	}
	
	public void zipFilesRecursive()
	{
		
	}
	//
	public void zipFiles(String[] lin)
	{
		int slashPos;
		File tempZip = null;
		File[] files = null;

		FileOutputStream fout = null;
		ZipOutputStream zout = null;
		String strNameSpec = null, strPath = null;
		
		try {
			tempZip = File.createTempFile("tmp", ".zip", new File(System.getProperty("java.io.tmpdir")));
			tempZip.deleteOnExit();
			File f = new File(zipFile);
			fout = new FileOutputStream(tempZip, true);
			zout = new ZipOutputStream(fout);
			if (f.exists()) {
				//Zip file previously existed. Transfer files from existing zip to new zip.
				copyFromOldZip(zout, zipFile);
			}
			for (String srcFil: srcFiles) {
				slashPos = srcFil.lastIndexOf(fileSeparator);
				if (slashPos > 0) {
					strNameSpec = srcFil.substring(slashPos + 1);
					strPath = srcFil.substring(0, slashPos + 1);
				} else {
					strNameSpec = srcFil;
					strPath = "." + fileSeparator;
				}
				//Check if dest dir exists
				File chkDir = new File(strZipDir);
				if (! chkDir.exists()) {
					//Create the missing zip file path
					chkDir.mkdirs();
					chkDir = null;
				}
				FilenameFilter fileFilter = new WildcardFileFilter(strNameSpec, IOCase.INSENSITIVE);
				//FilenameFilter fileFilter = new WildcardFilter(strNameSpec);
				File dir = new File(strPath);
				files = dir.listFiles(fileFilter);
				if (files != null) {
					for(int i = 0; i < files.length; i++) {
						if (!files[i].isDirectory()) {
							writeToZip(zout, files[i]);
						} else {
							System.out.println("\nEncountered directory..not processed yet");
						}
					}
				} else {
					System.out.println("Source directory does not exist");
					System.exit(-1);
				}
				
			}
			zout.close();
			fout.close();
			Files.move(Paths.get(tempZip.getAbsolutePath()), Paths.get(zipFile), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			if (bMove) {
				for(File fil: files) {
					Files.delete(fil.toPath());
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("FNFE: " + fnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("IOE: " + ioe.getMessage());
		} finally {
			try {
				if (zout != null){
					zout.close();
				}
				if (fout != null) {
					fout.close();
				}
				if (tempZip != null) {
					tempZip.delete();
				}
			} catch (IOException e) {
				// Ignoring IOException in finally block
			}
		}
	}

	private void writeToZip(ZipOutputStream zo, File f) throws ZipException
	{
		//int n;
		String strPath, strName;
		ZipEntry ze;
		strPath = f.getPath();
		//TO DO Remove drive letter from path name
		strName = FilenameUtils.getPath(strPath) + f.getName();

		if (bPath) {
			ze = new ZipEntry(strName);
		} else {
			ze = new ZipEntry(f.getName());
		}
		if (bMove) {
			System.out.println("Moving entry :" + f.getPath());
		} else {
			System.out.println("Adding entry :" + f.getPath());
		}
		try {
			FileInputStream fi = new FileInputStream(f);
			ze.setMethod(ZipEntry.DEFLATED);
			zo.putNextEntry(ze);
			copy(fi, zo);
			zo.closeEntry();
			fi.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	public void copy(InputStream input, OutputStream output) throws IOException
	{
		int bytesRead;
	    while ((bytesRead = input.read(BUFFER))!= -1) {
			output.write(BUFFER, 0, bytesRead);
		}
    }

	private void copyFromOldZip(ZipOutputStream zo, String strZipFile) throws IOException, ZipException
	{
		ZipFile zf = new ZipFile(strZipFile);
		Enumeration<? extends ZipEntry> entries = zf.entries();
		while(entries.hasMoreElements()) {
			ZipEntry ze = entries.nextElement();
			System.out.println("Copying : " + ze.getName());
			zo.putNextEntry(ze);
			copy(zf.getInputStream(ze), zo);
			zo.closeEntry();
		}
		zf.close();
	}

}
