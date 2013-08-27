package scraper;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Copy files, using two techniques, FileChannels and streams. Using
 * FileChannels is usually faster than using streams.
 */
public class CopyFiles {

	/**
	 * This may fail for VERY large files.
	 */
	static public void copyWithChannels(String source, String target, boolean aAppend) throws FileNotFoundException, IOException {
		File aSourceFile = new File(source);
		File aTargetFile = new File(target);
		log("Copying files with channels.");
		ensureTargetDirectoryExists(aTargetFile.getParentFile());
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;

		inStream = new FileInputStream(aSourceFile);
		inChannel = inStream.getChannel();
		outStream = new FileOutputStream(aTargetFile, aAppend);
		outChannel = outStream.getChannel();
		long bytesTransferred = 0;
		//defensive loop - there's usually only a single iteration :
		while (bytesTransferred < inChannel.size()) {
			bytesTransferred += inChannel.transferTo(0, inChannel.size(), outChannel);
		}

		//being defensive about closing all channels and streams
		if (inChannel != null) {
			inChannel.close();
		}
		if (outChannel != null) {
			outChannel.close();
		}
		if (inStream != null) {
			inStream.close();
		}
		if (outStream != null) {
			outStream.close();
		}


	}

	static public void copyWithStreams(String source, String target, boolean aAppend) {
		File aSourceFile = new File(source);
		File aTargetFile = new File(target);
		log("Copying files with streams.");
		ensureTargetDirectoryExists(aTargetFile.getParentFile());
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			try {
				byte[] bucket = new byte[32 * 1024];
				inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
				outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile, aAppend));
				int bytesRead = 0;
				while (bytesRead != -1) {
					bytesRead = inStream.read(bucket); //-1, 0, or more
					if (bytesRead > 0) {
						outStream.write(bucket, 0, bytesRead);
					}
				}
			} finally {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			}
		} catch (FileNotFoundException ex) {
			log("File not found: " + ex);
		} catch (IOException ex) {
			log(ex);
		}
	}

	static public void ensureTargetDirectoryExists(File aTargetDir) {
		if (!aTargetDir.exists()) {
			aTargetDir.mkdirs();
		}
	}

	public static void log(Object aThing) {
		System.out.println(String.valueOf(aThing));
	}
}
