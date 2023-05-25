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
            // get previous objects.
            Gson gson = new Gson();
            ArrayList<Building> inUseBuildings = new ArrayList<>();
            ArrayList<Building> notInUseBuildings = new ArrayList<>();
            ArrayList<Building> demolitionBuildings = new ArrayList<>();


            inUseBuildings = getBuildings(gson, "inUseBuildings");
            notInUseBuildings = getBuildings(gson, "notInUseBuildings");
            demolitionBuildings = getBuildings(gson, "demolitionBuildings");

            System.out.println(inUseBuildings.size());

            // output file
            ArrayList<Site> sites = new ArrayList<>();

            // 첫번째는 그냥 추가
            Site firstSiteThatShouldBeAdded = new Site();
            firstSiteThatShouldBeAdded.setRegion(inUseBuildings.get(0).getRegion());
            firstSiteThatShouldBeAdded.setBuilding(inUseBuildings.get(0));
            sites.add(firstSiteThatShouldBeAdded);


            addBuildingsToSites(inUseBuildings, sites);
            addBuildingsToSites(notInUseBuildings, sites);



            System.out.println(sites.size());

            System.out.println(gson.toJson(sites));
            saveObjectAsJsonFile("C:\\Users\\USER\\IdeaProjects\\sour grape\\json directory\\edubuilSites.json", sites);






        } catch (IOException e) {}
    }

    private static void addBuildingsToSites(ArrayList<Building> inUseBuildings, ArrayList<Site> sites) {
        for (int i = 1; i < inUseBuildings.size(); i++) {
            System.out.println(i);
            Region region = new Region();
            region.setCode1(inUseBuildings.get(i).getRegion().getCode1());
            region.setCode2(inUseBuildings.get(i).getRegion().getCode2());
            region.setBun(inUseBuildings.get(i).getRegion().getBun());
            region.setJi(inUseBuildings.get(i).getRegion().getJi());

            boolean breakFlag = true;
            for (int j = 0; j < sites.size(); j++) {
                boolean isCode1Same = sites.get(j).getRegion().getCode1().equals(region.getCode1());
                boolean isCode2Same = sites.get(j).getRegion().getCode2().equals(region.getCode2());
                boolean isBunSame = sites.get(j).getRegion().getBun().equals(region.getBun());
                boolean isJiSame = sites.get(j).getRegion().getJi().equals(region.getJi());

                if (isCode1Same && isCode2Same && isBunSame && isJiSame) {
                    sites.get(j).addNewBuilding(inUseBuildings.get(i));
                    breakFlag = false;
                    sites.get(j).setBuildingNumber();
                    break;
                }
            }
            if (breakFlag) {
                Site newSite = new Site();
                newSite.setRegion(region);
                newSite.setBuilding(inUseBuildings.get(i));
                newSite.setBuildingNumber();
                sites.add(newSite);
                System.out.println("새로운 땅을 추가합니다. ");
            }
        }
    }

    private static ArrayList<Building> getBuildings(Gson gson, String fileName) throws IOException {
        ArrayList<Building> buildings;
        FileReader reader = new FileReader("C:\\Users\\USER\\IdeaProjects\\sour grape\\json directory\\" + fileName + ".json");
        buildings = gson.fromJson(reader, new TypeToken<ArrayList<Building>>(){}.getType());
        reader.close();
        return buildings;
    }

    private static void saveObjectAsJsonFile(String fileName, ArrayList<Site> sites) {
        try (FileWriter writer = new FileWriter(fileName)) {
            new Gson().toJson(sites, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
