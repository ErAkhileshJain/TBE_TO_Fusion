package logfilename;


import java.text.SimpleDateFormat;

import java.util.Date;

import org.apache.log4j.FileAppender;


public class NewLogForEachRunFileAppender extends FileAppender {

    public static String logFileName;

    @Override
    public void setFile(String fileName) {
        if (fileName.indexOf("%timestamp") >= 0) {
            Date d = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmssSS");
            fileName = fileName.replaceAll("%timestamp", "error - "+format.format(d));
            logFileName=fileName;
        }
        super.setFile(fileName);
    }
}
