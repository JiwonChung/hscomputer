import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Region;
import domain.Site;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class APIDataToJson {
    public static void main(String[] args) {

        Gson gson = new Gson();
        List<Site> outputSites = new ArrayList<>();

        try {
            List<Site> sites = getSitesFromJson(gson, "edubuilSites");
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

            /*
             * 688회
             */
            for (int i = 0; i < sites.size(); i++) {
                Site site = sites.get(i);

                Region region = site.getRegion();
                site.setBuildingNumber();
                int buildingNumber = site.getBuildingNumber();
                final String APIKEY = "Pmk9ibaELBa8feE8r2XEBzMVlJKlqhqYiyK6ECJdfgTP8IAvWEU%2ByWSrQZhTLalK%2B6zqHOP4BVsXewngE%2Bve8A%3D%3D";

                // API 에서 데이터 받기
                /**
                 * 여기서부터 API 부르는 코드입니다.
                 */
                // 표제부
                StringBuilder outputOfGET_stringBuilder = getStringBuilder(i, region, APIKEY, 1);

                Map<String, Object> receivedMap = gson.fromJson(outputOfGET_stringBuilder.toString(), mapType);
                Map<String, Object> innerResponse = (Map<String, Object>) receivedMap.get("response");
                Map<String, Object> innerBody = (Map<String, Object>) innerResponse.get("body");
                Map<String, Object> innerItems;
                if (innerBody.get("items").getClass().equals(String.class)) {
                    // 총괄표제부가 없습니다.
                    System.out.println("총괄표제부가 없습니다. " + i);
//                    System.out.println(gson.toJson(innerBody));

                    outputOfGET_stringBuilder = getStringBuilder(i, region, APIKEY, 0);
                    receivedMap = gson.fromJson(outputOfGET_stringBuilder.toString(), mapType);
                    innerResponse = (Map<String, Object>) receivedMap.get("response");
                    innerBody = (Map<String, Object>) innerResponse.get("body");

                    if (innerBody.get("items").getClass().equals(String.class)) {
                        // 표제부가 없는 경우
                        System.out.println(gson.toJson(region));
                        System.out.println(innerBody);
                    } else {
                        innerItems = (Map<String, Object>) innerBody.get("items");
                    }
                }









                // save as APIReceivedSites.json

                // API received recap
                // API received floor by floor
                // API received headline <-- 표제부


            }

        } catch (IOException ignored) {}

    }

    private static StringBuilder getStringBuilder(int i, Region region, String APIKEY, int whatData) throws IOException {
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
        urlBuilder.append("?").append(urlEncoder("serviceKey")).append("=").append(APIKEY);
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

    private static ArrayList<Site> getSitesFromJson(Gson gson, String fileName) throws IOException {
        ArrayList<Site> sites;
        FileReader reader = new FileReader("C:\\Users\\USER\\IdeaProjects\\sour grape\\json directory\\" + fileName + ".json");
        sites = gson.fromJson(reader, new TypeToken<ArrayList<Site>>(){}.getType());
        reader.close();
        return sites;
    }
}
