package CreepyCrawler.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by i on 20.01.2018.
 */
public class RecordManager {
    private static final String REPORT_DIRECTPRY = "//CreepyCrowler//reports";
    private static final String USER_HOME_DIR = System.getProperty("user.home");
    private static File dir = new File(USER_HOME_DIR + REPORT_DIRECTPRY);

    private static String reportFilePath;

    public static void makeReportsDir() {
        if (dir.mkdirs()) {
            System.out.println(dir.getPath());
        } else {
//            System.out.println("Directory is not created.");
        }
    }

    public static void makeReportFile(String fileName) {
        makeReportsDir();
        reportFilePath = dir + "//" + fileName;
        File file = new File(reportFilePath);
        try {
            StringBuilder message = new StringBuilder(Calendar.getInstance().getTime().toString());
            if (file.createNewFile()) {
                message.append("\r\nFile ").append(reportFilePath).append(" is created successfully.");
            } else {
//                message.append("\nFile ").append(reportFilePath).append(" was not created!");
            }
//            writeReportRecord(fileName, message.toString());
        } catch (IOException e) {
            e.printStackTrace();
            writeReportRecord(fileName, e.getMessage());
        }
    }

    public static void writeReportRecord(String fileName, String record) {
        reportFilePath = dir + "//" + fileName;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(reportFilePath, true));
            writer.write("\r\n" + record);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String getReportsDirPath() throws Exception{
        if (dir.exists()) {
            return dir.getPath();
        } else {
            makeReportsDir();
            return dir.getPath();
        }
    }
}