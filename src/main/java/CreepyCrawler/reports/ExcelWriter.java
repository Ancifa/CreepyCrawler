package CreepyCrawler.reports;

import CreepyCrawler.crawler.model.Result;
import org.apache.commons.exec.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by i on 13.02.2018.
 */
public class ExcelWriter {

    private List<String> headerColumns;

    public List<String> getHeaderColumns() {
        return headerColumns;
    }

    public void setHeaderColumns(ArrayList<String> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public void setHeaderColumns() {
        headerColumns = Arrays.asList("Listing ID", "Business Name", "Email", "City", "State", "Category");
    }

    public void write(String fileName, ArrayList<Result> results, String location, String category) throws Exception {
        String fullFileName = RecordManager.getReportsDirPath() + "//" + fileName + ".xls";
//        Workbook workbook = WorkbookFactory.create(new File(fullFileName));
        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet(location + " " + category);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        setHeaderColumns();

        for (int i = 0; i < headerColumns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerColumns.get(i));
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (Result result : results) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(result.getListingId());
            row.createCell(1).setCellValue(result.getBusinessName());
            if (!result.getEmailsList().isEmpty()) {
                StringBuilder emailString = new StringBuilder();
                for (String email : result.getEmailsList()) {
                    emailString.append(email).append("; ");
                }
                emailString.deleteCharAt(emailString.length() - 1);
                emailString.deleteCharAt(emailString.length() - 1);
                row.createCell(2).setCellValue(emailString.toString());
            } else {
                row.createCell(2).setCellValue("N/A");
            }
            row.createCell(3).setCellValue(result.getCity());
            row.createCell(4).setCellValue(result.getState());
            row.createCell(5).setCellValue(result.getPrimaryCategory());
        }

        for (int i = 0; i < headerColumns.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream outputStream = new FileOutputStream(fullFileName);
        workbook.write(outputStream);
        outputStream.close();

    }
}
