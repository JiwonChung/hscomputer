import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Error;
import domain.ForReport;
import domain.Site;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this class determine e5, e6 and get output.xlsx
 */
public class GetOutput {
    public static void main(String[] args) throws Exception {

        // 유틸
        Gson gson = new Gson();
        List<Site> edubuilSites = getSitesFromJson(gson, "sites");
        List<Site> apiReceivedSites = getSitesFromJson(gson, "receivedSites");
        List<ForReport> outputLists = new ArrayList<>();

        // output 생성 및 에러코드4,5 발급
        for (int i = 0; i < edubuilSites.size(); i++) {
            System.out.println(edubuilSites.get(i).getBuildingNumber());
            ForReport forReport = new ForReport();
            Error error;

            // region
            forReport.setRegion(edubuilSites.get(i).getRegion());

            // eduBuil
            forReport.setEdubuilBuildingNumber(edubuilSites.get(i).getBuildingNumber());
            forReport.setEdubuilGrossFloorArea(edubuilSites.get(i).getGrossFloorArea());

            // API received
            forReport.setLandRegisteredBuildingNumber(apiReceivedSites.get(i).getBuildingNumber());
            forReport.setLandRegisteredGrossFloorArea(apiReceivedSites.get(i).getGrossFloorArea());

            // error
            error = apiReceivedSites.get(i).getErrorMessages();
            forReport.setGrossFloorAreaDifference(Math.abs(forReport.getEdubuilGrossFloorArea() - forReport.getLandRegisteredGrossFloorArea()));
            forReport.setBuildingNumberDifference(Math.abs(forReport.getEdubuilBuildingNumber() - forReport.getLandRegisteredBuildingNumber()));

            if (forReport.getBuildingNumberDifference() != 0) {
                if (forReport.getEdubuilBuildingNumber() > forReport.getLandRegisteredBuildingNumber()) {
                    error.setE4_2(true);
                } else {
                    error.setE4_1(true);
                }
            }
            if (forReport.getGrossFloorAreaDifference() >= 10) {
                if (forReport.getEdubuilGrossFloorArea() > forReport.getLandRegisteredGrossFloorArea()) {
                    error.setE5_2(true);
                } else {
                    error.setE5_1(true);
                }
            }

            forReport.setError(error);
            outputLists.add(forReport);
        }


        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("region");
        makeFirstSheet(edubuilSites, sheet);

        XSSFSheet sheet2 = workbook.createSheet("report");
        makeSecondSheet(outputLists, sheet2);


        try (FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\excelFiles\\report.xlsx")) {
            workbook.write(fileOutputStream);
        } catch (IOException ignored) {
        }

    }

    private static void makeFirstSheet(List<Site> edubuilSites, XSSFSheet sheet) {
        XSSFCell cell;
        XSSFRow row;
        // 목차 생성
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("index");
        cell = row.createCell(1);
        cell.setCellValue("대표학교이름");
        cell = row.createCell(2);
        cell.setCellValue("지번주소");
        cell = row.createCell(3);
        cell.setCellValue("code1");
        cell = row.createCell(4);
        cell.setCellValue("code2");
        cell = row.createCell(5);
        cell.setCellValue("번");
        cell = row.createCell(6);
        cell.setCellValue("지");
        cell = row.createCell(7);
        cell.setCellValue("산 여부");

        for (int i = 1; i < edubuilSites.size(); i++) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);

            cell = row.createCell(1);
            cell.setCellValue(edubuilSites.get(i).getRegion().getName());
            cell = row.createCell(2);
            cell.setCellValue(edubuilSites.get(i).getRegion().getAddress());
            cell = row.createCell(3);
            cell.setCellValue(edubuilSites.get(i).getRegion().getCode1());
            cell = row.createCell(4);
            cell.setCellValue(edubuilSites.get(i).getRegion().getCode2());
            cell = row.createCell(5);
            cell.setCellValue(edubuilSites.get(i).getRegion().getBun());
            cell = row.createCell(6);
            cell.setCellValue(edubuilSites.get(i).getRegion().getJi());
            cell = row.createCell(7);
            if (edubuilSites.get(i).getRegion().isSan()) {
                cell.setCellValue("O");
            } else {
                cell.setCellValue("X");
            }
        }
    }

    private static void makeSecondSheet(List<ForReport> outputLists, XSSFSheet sheet) {
        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);

        /**
         * title row
         */
        {
            cell = row.createCell(0);
            cell.setCellValue("지역 번호");
            cell = row.createCell(1);
            cell.setCellValue("에듀빌 연면적");
            cell = row.createCell(2);
            cell.setCellValue("대장 연면적");
            cell = row.createCell(3);
            cell.setCellValue("연면적 차");
            cell = row.createCell(4);
            cell.setCellValue("에듀빌 건물 수");
            cell = row.createCell(5);
            cell.setCellValue("대장 건물 수");
            cell = row.createCell(6);
            cell.setCellValue("건물 수 차");
            cell = row.createCell(7);
            cell.setCellValue("E1");
            cell = row.createCell(8);
            cell.setCellValue("E2");
            cell = row.createCell(9);
            cell.setCellValue("E2_1");
            cell = row.createCell(10);
            cell.setCellValue("E2_2");
            cell = row.createCell(11);
            cell.setCellValue("E3");
            cell = row.createCell(12);
            cell.setCellValue("E4_1");
            cell = row.createCell(13);
            cell.setCellValue("E4_2");
            cell = row.createCell(14);
            cell.setCellValue("E5_1");
            cell = row.createCell(15);
            cell.setCellValue("E5_2");
            cell = row.createCell(16);
            cell.setCellValue("E6");
        }

        /**
         * data row
         */
        for (int i = 0; i < outputLists.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell = row.createCell(1);
            cell.setCellValue(outputLists.get(i).getEdubuilGrossFloorArea());
            cell = row.createCell(2);
            cell.setCellValue(outputLists.get(i).getLandRegisteredGrossFloorArea());
            cell = row.createCell(3);
            cell.setCellValue(outputLists.get(i).getGrossFloorAreaDifference());
            cell = row.createCell(4);
            cell.setCellValue(outputLists.get(i).getEdubuilBuildingNumber());
            cell = row.createCell(5);
            cell.setCellValue(outputLists.get(i).getLandRegisteredBuildingNumber());
            cell = row.createCell(6);
            cell.setCellValue(outputLists.get(i).getBuildingNumberDifference());
            cell = row.createCell(7);
            cell.setCellValue(outputLists.get(i).getError().isE1());
            cell = row.createCell(8);
            cell.setCellValue(outputLists.get(i).getError().isE2());
            cell = row.createCell(9);
            cell.setCellValue(outputLists.get(i).getError().isE2_1());
            cell = row.createCell(10);
            cell.setCellValue(outputLists.get(i).getError().isE2_2());
            cell = row.createCell(11);
            cell.setCellValue(outputLists.get(i).getError().isE3());
            cell = row.createCell(12);
            cell.setCellValue(outputLists.get(i).getError().isE4_1());
            cell = row.createCell(13);
            cell.setCellValue(outputLists.get(i).getError().isE4_2());
            cell = row.createCell(14);
            cell.setCellValue(outputLists.get(i).getError().isE5_1());
            cell = row.createCell(15);
            cell.setCellValue(outputLists.get(i).getError().isE5_2());
            cell = row.createCell(16);
            cell.setCellValue(outputLists.get(i).getError().isE6());
        }


    }

    private static ArrayList<Site> getSitesFromJson(Gson gson, String fileName) throws IOException {
        ArrayList<Site> sites;
        FileReader reader = new FileReader("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\jsonFiles\\" + fileName + ".json");
        sites = gson.fromJson(reader, new TypeToken<ArrayList<Site>>() {
        }.getType());
        reader.close();
        return sites;
    }

}
