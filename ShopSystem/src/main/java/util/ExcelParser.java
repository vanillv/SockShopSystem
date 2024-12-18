package util;
import model.SockDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser {
    public static List<SockDto> parseExcelFile(MultipartFile file) {
        try {
            List<SockDto> socks = new ArrayList<>();
            try (InputStream is = file.getInputStream()) {
                Workbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    SockDto sock = new SockDto();
                    sock.setColor(row.getCell(0).getStringCellValue());
                    sock.setCottonPercentage((int) row.getCell(1).getNumericCellValue());
                    sock.setQuantity((int) row.getCell(2).getNumericCellValue());
                    socks.add(sock);
                }
            }
            return socks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }
}
