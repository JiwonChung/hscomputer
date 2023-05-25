import com.google.gson.Gson;
import domain.Building;
import domain.Region;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class ExcelToBuildings {
    public static void main(String[] args) {
        Gson gson = new Gson();

        ArrayList<Building> inUseBuildings = new ArrayList<>();
        ArrayList<Building> demolitionBuildings = new ArrayList<>();
        ArrayList<Building> notInUseBuildings = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\excelFiles\\restartKit.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow row;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                Building building = new Building();
                Region region = new Region();
                String runningStatus = row.getCell(7).getStringCellValue();
                if (runningStatus.equals("사용") || runningStatus.equals("예정")) {
                    setBuilding(inUseBuildings, row, building, region);
                } else if (runningStatus.equals("철거")) {
                    setBuilding(demolitionBuildings, row, building, region);
                } else if (runningStatus.equals("불용")) {
                    setBuilding(notInUseBuildings, row, building, region);
                }
            }
        } catch (Exception ignored) {
        }
        System.out.println("inUseBuildings size: " + inUseBuildings.size());
        System.out.println("demolitionBuildings size: " + demolitionBuildings.size());
        System.out.println("notInUseBuildings size: " + notInUseBuildings.size());


        saveObjectAsJsonFile("demolitionBuildings", demolitionBuildings);
        saveObjectAsJsonFile("inUseBuildings", inUseBuildings);
        saveObjectAsJsonFile("notInUseBuildings", notInUseBuildings);
    }

    private static void saveObjectAsJsonFile(String fileName, ArrayList<Building> buildings) {
        try(FileWriter writer = new FileWriter("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\jsonFiles\\" + fileName + ".json")) {
            new Gson().toJson(buildings, writer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setBuilding(ArrayList<Building> inUseBuildings, XSSFRow row, Building building, Region region) {
        // 산인지 판별 후 지역 삽입
        if (row.getCell(23).getCellType() == CellType.BLANK) {
            setRegion(row, region);
            setParameters(row, building);
        } else {
            setRegion(row, region);
            setParameters(row, building);
            region.setSan(true);
        }
        building.setRegion(region);
        inUseBuildings.add(building);
    }

    private static void setParameters(XSSFRow row, Building building) {
        building.setUndergroundFloor((int) row.getCell(12).getNumericCellValue());
        building.setGroundFloor((int) row.getCell(13).getNumericCellValue());
        building.setGrossFloorArea(row.getCell(14).getNumericCellValue());
    }

    private static void setRegion(XSSFRow row, Region region) {
        // set school name
        region.setName(String.valueOf(row.getCell(3).getStringCellValue()));

        // set 지번 주소
        region.setAddress(String.valueOf(row.getCell(18).getStringCellValue()));

        // set region
        region.setCode1(String.valueOf((int) row.getCell(19).getNumericCellValue()));
        region.setCode2(String.valueOf((int) row.getCell(20).getNumericCellValue()));

        // set bun
        int bun = (int) row.getCell(21).getNumericCellValue();
        if (bun > 999) {
            region.setBun(String.valueOf(bun));
        } else if (bun > 99) {
            region.setBun("0" + bun);
        } else if (bun > 9) {
            region.setBun("00" + bun);
        } else if (bun > 0) {
            region.setBun("000" + bun);
        } else {
            region.setBun("0000");
            System.out.println("개버그 0000은 있을 수 없다. ");
        }

        // set ji
        bun = (int) row.getCell(22).getNumericCellValue();
        if (bun > 999) {
            region.setJi(String.valueOf(bun));
        } else if (bun > 99) {
            region.setJi("0" + bun);
        } else if (bun > 9) {
            region.setJi("00" + bun);
        } else if (bun > 0) {
            region.setJi("000" + bun);
        } else {
            region.setJi("0000");
        }

    }
}