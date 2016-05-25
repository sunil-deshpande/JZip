package org.bt;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.bt.zipunzip.*;

public class Main {
	private static final String USAGE = "JZip.bat [options] files";
	private static final String HEADER = "Java utility for zipping/unzipping files v0.1";
	private static final String FOOTER = "Developed by Sunil Deshpande (C) 2015\n";
	public static void main(String[] args) 
	{
		String zipFileName = null;
		String fileSeparator = System.getProperty("file.separator");
		String destPath = null;
		boolean bMove, bPath;
		Zip zip;

		CommandLineParser clp = new PosixParser();

		Options opts = new Options();
		OptionGroup optionGroup = new OptionGroup();

		opts.addOption("h", "help", true, "Show usage (this screen)");
		opts.addOption("d", "dest", true, "Destination direcotry");
		opts.addOption("m", "move", false, "Move files (will delete source)");
		opts.addOption("f", "file", true, "Get file name");
		opts.addOption("v", "version", false, "Version information");
		opts.addOption("p", "path", false, "Use full paths while zipping/unzipping files");
		opts.addOption("r", "recurse", false, "Recurse through directory tree(--p taken by default)");
		optionGroup.addOption(new Option("a","add", false, "add files to zip file"));
		optionGroup.addOption(new Option("x", "extract", false, "Extact files from archive"));
		optionGroup.addOption(new Option("l", "list", false, "List of file from archive"));
		opts.addOptionGroup(optionGroup);

		try {
			CommandLine line = clp.parse(opts, args);
			if (args.length == 0) {
				printUsage(opts);
				System.exit(1);
			}
			if (line.hasOption('v')) {
				System.out.println("JZip version v0.1\n");
				System.exit(0);
			}
			if (!(line.hasOption('a') || line.hasOption('x')|| line.hasOption('l'))) {
				System.out.println("Must have one of 'a' or 'x' or 'l' option specified");
				printUsage(opts);
				System.exit(2);
			}
			if (line.hasOption('h')) {
				printUsage(opts);
			}
			if (line.hasOption('m')) {
				bMove = true;
			} else {
				bMove = false;
			}
			if (line.hasOption('p')) {
				bPath = true;
			} else {
				bPath = false;
			}
			if (line.hasOption("f")) {
				zipFileName = line.getOptionValue("f");
			}
			if (line.hasOption("d")) {
				destPath = line.getOptionValue("d");
				String x = destPath.substring(destPath.length() - 1);
				if (!x.equalsIgnoreCase(fileSeparator)) {
					destPath = destPath + fileSeparator;
				}
				File dir = new File(destPath);
				if (!dir.exists()) {
					System.out.println("Destination directory " + dir.getAbsolutePath() + " does not exist. Will create");
					dir.mkdirs();
				}
			}
			if(line.hasOption('a')) {
				if (zipFileName == null || zipFileName.equals("")) {
					System.out.println("Zip file name not specified. Specify with -f or --file option");
					System.exit(5);
				}
				String[] lin = line.getArgs();
				zip = new Zip(zipFileName, lin, bMove, bPath);
				zip.zipFiles(lin);
				zip = null;
			}
			if(line.hasOption('x')) {
				Unzip uz = new Unzip(zipFileName, destPath);
				uz.unZipFile();
			}
			if (line.hasOption('l')){
				if (!line.hasOption('f')){
					System.out.println("Must specify zip file name with option -f");
					printUsage(opts);
					System.exit(-1);
				} else {
                    System.out.println("Listing files in archive " + line.getOptionValue("f"));
				    Unzip uz = new Unzip(zipFileName, destPath);
    				uz.listFiles();
                }
			}
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			printUsage(opts);
		} catch (ZipException ze) {
			System.out.println("Exception " + ze.getMessage());
		} catch (IOException ioe) {
			System.out.println("Exception " + ioe.getMessage());
		}
	}
	private static void printUsage(Options options)
	{
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(80);
		helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
	}
}
/*------------------ End of File ----------------------------------------*/
