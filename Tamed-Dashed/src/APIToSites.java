import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Error;
import domain.Region;
import domain.Site;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class APIToSites {
    public static void main(String[] args) throws IOException {

        // 유틸
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        // fields
        ArrayList<Site> receivedSites = new ArrayList<>();
        List<Site> storedSites = getSitesFromJson(gson, "sites");

        for (int i = 0; i < storedSites.size(); i++) {
            StringBuilder outputOfGet_stringBuilder = getStringBuilder(i, storedSites.get(i).getRegion(), 1);

            Map<String, Object> receivedMap = gson.fromJson(outputOfGet_stringBuilder.toString(), mapType);
            Map<String, Object> innerResponse = (Map<String, Object>) receivedMap.get("response");
            Map<String, Object> innerBody = (Map<String, Object>) innerResponse.get("body");
            Map<String, Object> innerItems;

            double grossFloorArea = 0;
            int buildingNumber = 0;
            Error error = new Error();

            if (innerBody.get("items").getClass().equals(String.class)) {
                // 총괄 없음
                outputOfGet_stringBuilder = getStringBuilder(i, storedSites.get(i).getRegion(), 0);
                receivedMap = gson.fromJson(outputOfGet_stringBuilder.toString(), mapType);
                innerResponse = (Map<String, Object>) receivedMap.get("response");
                innerBody = (Map<String, Object>) innerResponse.get("body");
                if (innerBody.get("items").getClass().equals(String.class)) {
                    // 표제부가 없는 경우 - code 1
                    error.setE1(true);

                } else {
                    // 총괄이 없고 표제부 있는 경우 - B
                    outputOfGet_stringBuilder = getStringBuilder(i, storedSites.get(i).getRegion(), 0);
                    receivedMap = gson.fromJson(outputOfGet_stringBuilder.toString(), mapType);
                    innerResponse = (Map<String, Object>) receivedMap.get("response");
                    innerBody = (Map<String, Object>) innerResponse.get("body");
                    innerItems = (Map<String, Object>) innerBody.get("items");


                    if (innerItems.get("item").getClass().equals(ArrayList.class)) {
                        // 대장오류 건축물이 2개 이상이나 표제부가 없음 - code 6
                        error.setE6(true);
                        /**
                         * 33개
                         * 일단보류하고 나중에 시간 있으면
                         */

                    } else {
                        Map<String, Object> headline = (Map<String, Object>) innerItems.get("item");
                        buildingNumber = innerItems.size() + (int) (double) headline.get("atchBldCnt");
                        grossFloorArea = (double) headline.get("totArea");
                    }

                }

            } else {
                // 총괄 있는 경우 - A
                innerItems = (Map<String, Object>) innerBody.get("items");

                if (innerItems.get("item").getClass().equals(ArrayList.class)) {
                    // code 3 : 총괄이 여러개인 주소지 --> 끝
                    error.setE1(true);

                } else {
                    // 총괄이 멀쩡히 하나만 있는 경우
                    Map<String, Object> innerItem = (Map<String, Object>) innerItems.get("item");

                    // 연면적
                    grossFloorArea = (double) innerItem.get("totArea");

                    // 모든 건물 수 총 합
                    buildingNumber = (int) ((double) innerItem.get("atchBldCnt") + (double) innerItem.get("mainBldCnt"));



                    if (buildingNumber == 0) {
                        // code 2-1: 총괄은 있는데 건물수가 0
                        // 표제부 조회해서 표제부 데이터 삽입
                        // 표제부 건물 수 더하면 됨

                        outputOfGet_stringBuilder = getStringBuilder(i, storedSites.get(i).getRegion(), 0);
                        receivedMap = gson.fromJson(outputOfGet_stringBuilder.toString(), mapType);
                        innerResponse = (Map<String, Object>) receivedMap.get("response");
                        innerBody = (Map<String, Object>) innerResponse.get("body");
                        innerItems = (Map<String, Object>) innerBody.get("items");
                        ArrayList<Map<String, Object>> headlines = (ArrayList<Map<String, Object>>) innerItems.get("item");;

                        buildingNumber = innerItems.size();
                        error.setE2_1(true);
                        for (Map<String, Object> headline : headlines) {
                            buildingNumber += (int) (double) headline.get("atchBldCnt");
                        }

                    }

                    if (grossFloorArea == 0) {
                        // code 2-2: 총괄은 있는데 연면적이 0
                        // 표제부 조회해서 표제부 데이터 삽입

                        outputOfGet_stringBuilder = getStringBuilder(i, storedSites.get(i).getRegion(), 0);
                        receivedMap = gson.fromJson(outputOfGet_stringBuilder.toString(), mapType);
                        innerResponse = (Map<String, Object>) receivedMap.get("response");
                        innerBody = (Map<String, Object>) innerResponse.get("body");
                        innerItems = (Map<String, Object>) innerBody.get("items");
                        ArrayList<Map<String, Object>> headlines = (ArrayList<Map<String, Object>>) innerItems.get("item");

                        for (Map<String, Object> headline : headlines) {
                            grossFloorArea += (double) headline.get("totArea");
                        }
                        error.setE2_2(true);

                    }
                }
            }


            Site site = new Site();
            site.setRegion(storedSites.get(i).getRegion());
            site.setGrossFloorArea(grossFloorArea);
            site.setBuildingNumber(buildingNumber);
            site.setError(error);
            receivedSites.add(site);
        }

        try {
            saveObjectAsJsonFile("receivedSites", receivedSites);
        } catch (Exception ignored) {
            System.out.println("저장실패");
        }
    }

    private static void saveObjectAsJsonFile(String fileName, ArrayList<Site> objects) {
        try (FileWriter writer = new FileWriter("C:\\Users\\USER\\IdeaProjects\\Tamed-Dashed\\jsonFiles\\" + fileName + ".json")) {
            new Gson().toJson(objects, writer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    private static StringBuilder getStringBuilder(int i, Region region, int whatData) throws IOException {
        // 0: 표제부, 1: 총괄표제부, 2: 층별개요
        String basic = "";
        switch (whatData) {
            case 0:
                // 표제부
                basic = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrTitleInfo";
                break;
            case 1:
                // 총괄
                basic = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrRecapTitleInfo";
                break;
            case 2:
                // 층별개요.
                basic = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrFlrOulnInfo";
        }


        StringBuilder urlBuilder = new StringBuilder(basic);
        urlBuilder.append("?").append(urlEncoder("serviceKey")).append("=").append("Pmk9ibaELBa8feE8r2XEBzMVlJKlqhqYiyK6ECJdfgTP8IAvWEU%2ByWSrQZhTLalK%2B6zqHOP4BVsXewngE%2Bve8A%3D%3D");
        urlBuilder.append("&").append(urlEncoder("sigunguCd")).append("=").append(urlEncoder(region.getCode1()));
        urlBuilder.append("&").append(urlEncoder("bjdongCd")).append("=").append(urlEncoder(region.getCode2()));
        if (region.isSan()) {
            urlBuilder.append("&").append(urlEncoder("playGbCd")).append("=").append(urlEncoder("1"));
        } // 산
        urlBuilder.append("&").append(urlEncoder("bun")).append("=").append(urlEncoder(region.getBun())); // 번
        urlBuilder.append("&").append(urlEncoder("ji")).append("=").append(urlEncoder(region.getJi())); // 지
        urlBuilder.append("&").append(urlEncoder("startDate")).append("=").append(urlEncoder("")); //startDay which is blank
        urlBuilder.append("&").append(urlEncoder("endData")).append("=").append(urlEncoder("")); // endDay too
        urlBuilder.append("&").append(urlEncoder("numOfRows")).append("=").append(urlEncoder("100")); // number per pages which is 100
        urlBuilder.append("&").append(urlEncoder("pageNo")).append("=").append(urlEncoder("1")); // page number which always be 1
        urlBuilder.append("&").append(urlEncoder("_type")).append("=").append(urlEncoder("json"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "application/json");

        BufferedReader bufferedReader;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            System.out.println("Error in " + i);
        }

        StringBuilder outputOfGET_stringBuilder = new StringBuilder(); // outputOfGet_stringBuilder --> Json data 갖고 있는 스트링
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            outputOfGET_stringBuilder.append(line);
        }
        bufferedReader.close();
        connection.disconnect();
        return outputOfGET_stringBuilder;
    }

    private static String urlEncoder(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }
}
