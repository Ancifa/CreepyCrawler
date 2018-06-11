package CreepyCrawler.reports;

import CreepyCrawler.crawler.model.Result;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by i on 13.02.2018.
 */
public class ExcelWriter {

    private List<String> headerColumns;

    private void setHeaderColumns() {
        headerColumns = Arrays.asList("Listing ID", "Business Name", "Email", "City", "State", "Category");
    }

    public Workbook write(ArrayList<Result> results, String location, String category) {
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

        return workbook;
    }
}
