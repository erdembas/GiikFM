package Class;

public class FileSizeFormatter {
		  private static final String BYTES = " Bytes";
		  private static final String MEGABYTES = " MB";
		  private static final String KILOBYTES = " kB";
		  private static final String GIGABYTES = " GB";
		  private static final long KILO = 1024;
		  private static final long MEGA = KILO * 1024;
		  private static final long GIGA = MEGA * 1024;

		  public static String formatFileSize(final long pBytes){
		    if(pBytes < KILO){
		      return pBytes + BYTES;
		    }else if(pBytes < MEGA){
		      return (int)(0.5 + (pBytes / (double)KILO)) + KILOBYTES;
		    }else if(pBytes < GIGA){
		      return (int)(0.5 + (pBytes / (double)MEGA)) + MEGABYTES;
		    }else {
		      return (int)(0.5 + (pBytes / (double)GIGA)) + GIGABYTES;
		    }
		  }
}
