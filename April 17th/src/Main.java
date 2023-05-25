import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        // 학교 목록 읽기
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\USER\\IdeaProjects\\April 17th\\io\\학교 목록.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        ArrayList<AddressAndSchoolName> addressAndSchoolNames = new ArrayList<>();

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            AddressAndSchoolName addressAndSchoolName = new AddressAndSchoolName();
            addressAndSchoolName.setAddress(sheet.getRow(i).getCell(6).getStringCellValue().replaceAll("\\s", ""));
            addressAndSchoolName.setSchoolName(sheet.getRow(i).getCell(4).getStringCellValue().replaceAll("\\s", ""));
            addressAndSchoolNames.add(addressAndSchoolName);
        }

        fileInputStream = new FileInputStream("C:\\Users\\USER\\IdeaProjects\\April 17th\\io\\학교 분류.xlsx");
        workbook = new XSSFWorkbook(fileInputStream);
        XSSFWorkbook outputWorkbook = new XSSFWorkbook();
        ArrayList<AddressAndSchoolName> outputAddressAndSchoolNames = new ArrayList<>();

        for (int sheetNumber = 1; sheetNumber <= 12; sheetNumber++) {

            sheet = workbook.getSheetAt(sheetNumber);
            XSSFSheet outputSheet = outputWorkbook.createSheet("" + sheetNumber);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                AddressAndSchoolName outputAddressAndSchoolName = new AddressAndSchoolName();
                outputAddressAndSchoolName.setAddress(sheet.getRow(i).getCell(2).getStringCellValue().replaceAll("\\s", ""));
                StringBuilder outputName = new StringBuilder();



                for (AddressAndSchoolName addressAndSchoolNameFromList : addressAndSchoolNames) {
                    if (addressAndSchoolNameFromList.getAddress().equals(outputAddressAndSchoolName.getAddress())) {
                        outputName.append(addressAndSchoolNameFromList.getSchoolName());
                        outputName.append(", ");
                    }
                }

                outputAddressAndSchoolName.setSchoolName(outputName.toString());
                outputAddressAndSchoolNames.add(outputAddressAndSchoolName);
                outputSheet.createRow(i).createCell(3).setCellValue(outputAddressAndSchoolName.getSchoolName());
            }

        }
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\USER\\IdeaProjects\\April 17th\\io\\output.xlsx");
        outputWorkbook.write(fileOutputStream);

        System.out.println(new Gson().toJson(outputAddressAndSchoolNames));
    }

    static class AddressAndSchoolName {
        private String schoolName;
        private String address;

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

}