package CreepyCrawler.ui;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExportToExcel extends HttpServlet {

    public StreamResource downloadExcelFile(Workbook workbook, String fileName) {
        byte[] wb = getBytesFromWorkbook(workbook);
        StreamResource.StreamSource source =(StreamResource.StreamSource) () ->
                new ByteArrayInputStream(wb);

        StreamResource resource = new StreamResource(source, fileName) {
            DownloadStream downloadStream;

            @Override
            public DownloadStream getStream() {
                if (downloadStream == null) {
                    downloadStream = super.getStream();
                }
                return downloadStream;
            }
        };
        resource.getStream().setParameter("Content-Disposition", "attachment;filename=" + fileName + ".xls");
        resource.getStream().setParameter("Content-Type", "application/vnd.ms-excel");

        return resource;
    }

    private byte[] getBytesFromWorkbook(Workbook workbook) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
