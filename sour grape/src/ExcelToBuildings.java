import com.google.gson.Gson;
import domain.Building;
import domain.Region;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExcelToBuildings {
    public static void main(String[] args) {

        ArrayList<Building> inUseBuildings = new ArrayList<>();
        ArrayList<Building> demolitionBuildings = new ArrayList<>();
        ArrayList<Building> notInUseBuildings = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\USER\\IdeaProjects\\sour grape\\src\\edubuil.xlsx");
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("inUseBuilding Size: " + inUseBuildings.size());
        System.out.println("demolitionBuilding size: " + demolitionBuildings.size());
        System.out.println("not in use building size: " + notInUseBuildings.size());


        saveObjectAsJsonFile("C:\\Users\\USER\\IdeaProjects\\sour grape\\json directory\\inUseBuildings.json", inUseBuildings);
        saveObjectAsJsonFile("C:\\Users\\USER\\IdeaProjects\\sour grape\\json directory\\demolitionBuildings.json", demolitionBuildings);
        saveObjectAsJsonFile("C:\\Users\\USER\\IdeaProjects\\sour grape\\json directory\\notInUseBuildings.json", notInUseBuildings);
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

    private static void saveObjectAsJsonFile(String fileName, ArrayList<Building> buildings) {
        try (FileWriter writer = new FileWriter(fileName)) {
            new Gson().toJson(buildings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setParameters(XSSFRow row, Building building) {
        building.setUndergroundFloor((long) row.getCell(12).getNumericCellValue());
        building.setGroundFloor((long) row.getCell(13).getNumericCellValue());
        building.setGrossFloorArea((long) row.getCell(14).getNumericCellValue());
        building.setBuildingArea((long) row.getCell(15).getNumericCellValue());
    }

    private static void setRegion(XSSFRow row, Region region) {
        // set region
        region.setCode1(String.valueOf((int) row.getCell(19).getNumericCellValue()));
        region.setCode2(String.valueOf((int) row.getCell(20).getNumericCellValue()));
        region.setBunIntegerInput((int) row.getCell(21).getNumericCellValue());
        region.setJiIntegerInput((int) row.getCell(22).getNumericCellValue());
    }
}