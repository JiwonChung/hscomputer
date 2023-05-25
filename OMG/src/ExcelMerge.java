import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

public class ExcelMerge {

    public static void main(String[] args) throws Exception {

        // Input folder path
        String folderPath = "C:\\Users\\USER\\IdeaProjects\\OMG\\input_folder";

        // Output file path
        String outputFile = "C:\\Users\\USER\\IdeaProjects\\OMG\\output\\output.xlsx";

        // output
        XSSFWorkbook outputWorkbook = new XSSFWorkbook();
        XSSFSheet outputSheet = outputWorkbook.createSheet("엄마가");
        List<XSSFRow> rows = new ArrayList<>();

        // Get all the .xlsx files in the folder
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx"));

        assert files != null;
        System.out.println(files.length);

        // Load all the workbooks and add their sheets to the list
        for (int i = 0; i < files.length; i++) {
            XSSFWorkbook workbook = new XSSFWorkbook(Files.newInputStream(files[i].toPath()));
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow outputRow = outputSheet.createRow(i);
            sheet.getRow(2).getCell(2).setCellType(CellType.STRING);

            boolean a = sheet.getRow(2).getCell(2).getStringCellValue().equals("SH00000009"),
                    b = sheet.getRow(2).getCell(3).getStringCellValue().equals("홍길동"),
                    c = sheet.getRow(2).getCell(2).getStringCellValue().equals("");
            if (a || b || c) {
                if (setRow(sheet, 3, outputRow) == 1) {
                    System.out.println(files[i].getName());
                }
            } else {
                if (setRow(sheet, 2, outputRow) == 1) {
                    System.out.println(files[i].getName());
                }
            }
            rows.add(outputRow);
        }



        // Write the output workbook to the output file
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        outputWorkbook.write(fileOutputStream);
        fileOutputStream.close();

    }

    private static int setRow(XSSFSheet sheet, int rownum, XSSFRow outputRow) {
        XSSFRow row = sheet.getRow(rownum);

        // 지역
        XSSFCell outputCell = outputRow.createCell(0);
        row.getCell(0).setCellType(CellType.STRING);
        outputCell.setCellValue(row.getCell(0).getStringCellValue());

        // 학교명
        outputCell = outputRow.createCell(1);
        row.getCell(1).setCellType(CellType.STRING);
        outputCell.setCellValue(row.getCell(1).getStringCellValue());

        if (row.getCell(1).getStringCellValue().equals("")) {
            return 1;
        }

        // 학교코드
        outputCell = outputRow.createCell(2);
        row.getCell(2).setCellType(CellType.STRING);
        outputCell.setCellValue(row.getCell(2).getStringCellValue());

        // 신청자
        outputCell = outputRow.createCell(3);
        row.getCell(3).setCellType(CellType.STRING);
        outputCell.setCellValue(row.getCell(3).getStringCellValue());

        // 연락처
        outputCell = outputRow.createCell(4);
        row.getCell(4).setCellType(CellType.STRING);
        outputCell.setCellValue(row.getCell(4).getStringCellValue());

        // 비고
        outputCell = outputRow.createCell(5);
        row.getCell(5).setCellType(CellType.STRING);
        outputCell.setCellValue(row.getCell(5).getStringCellValue());

        return 0;
    }
}
