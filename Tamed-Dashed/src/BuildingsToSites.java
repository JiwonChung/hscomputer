import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Building;
import domain.Region;
import domain.Site;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BuildingsToSites {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            ArrayList<Building> inUseBuildings = getBuildings(gson, "inUseBuildings");
            ArrayList<Building> notInUseBuildings = getBuildings(gson, "notInUseBuildings");

            // 출력 결과
            ArrayList<Site> sites = new ArrayList<>();


            // 마중물
            Site primingWater = new Site();
            primingWater.setRegion(inUseBuildings.get(0).getRegion());
            primingWater.addBuildings(inUseBuildings.get(0));
            sites.add(primingWater);


            // sites 에 추가
            addBuildingsToSites(inUseBuildings, sites);
            addBuildingsToSites(notInUseBuildings, sites);

            System.out.println(sites.size());

            for (Site site : sites) {
                site.setBuildingNumber(site.getBuildings().size());
                double grossFloorArea = 0;
                for (int j = 0; j < site.getBuildings().size(); j++) {
                    grossFloorArea += site.getBuildings().get(j).getGrossFloorArea();
                }
                site.setGrossFloorArea(grossFloorArea);
            }

            saveObjectAsJsonFile("sites", sites);


        } catch (Exception ignored) {
        }
    }



    private static void addBuildingsToSites(ArrayList<Building> buildings, ArrayList<Site> sites) {
        for (int i = 1; i < buildings.size(); i++) {
            System.out.println(i);
            Region region = buildings.get(i).getRegion();

            boolean breakFlag = true;
            for (Site site : sites) {
                boolean isCode1Same = site.getRegion().getCode1().equals(region.getCode1());
                boolean isCode2Same = site.getRegion().getCode2().equals(region.getCode2());
                boolean isBunSame = site.getRegion().getBun().equals(region.getBun());
                boolean isJiSame = site.getRegion().getJi().equals(region.getJi());

                if (isCode1Same && isCode2Same && isBunSame && isJiSame) {
                    site.addBuildings(buildings.get(i));
                    breakFlag = false;
                    break;
                }
            }
            if (breakFlag) {
                Site newSite = new Site();
                newSite.setRegion(region);
                newSite.addBuildings(buildings.get(i));
                sites.add(newSite);
                System.out.println("새로운 땅을 추가합니다. ");
            }


        }
    }


    private static ArrayList<Building> getBuildings(Gson gson, String fileName) throws IOException {
        ArrayList<Building> buildings;
        FileReader reader = new FileReader("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\jsonFiles\\" + fileName + ".json");
        buildings = gson.fromJson(reader, new TypeToken<ArrayList<Building>>() {
        }.getType());
        reader.close();
        return buildings;
    }

    private static void saveObjectAsJsonFile(String fileName, ArrayList<Site> objects) {
        try(FileWriter writer = new FileWriter("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\jsonFiles\\" + fileName + ".json")) {
            new Gson().toJson(objects, writer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
