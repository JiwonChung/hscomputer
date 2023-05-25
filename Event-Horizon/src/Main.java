import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.*;
import domain.Error;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main extends JFrame {

    Container contentPane;
    JButton button_download = new JButton("저장하기");
    // 동 --> 코드
    private static Map<String, String> dongToCode = new LinkedHashMap<>();
    final String API_KEY = "Pmk9ibaELBa8feE8r2XEBzMVlJKlqhqYiyK6ECJdfgTP8IAvWEU%2ByWSrQZhTLalK%2B6zqHOP4BVsXewngE%2Bve8A%3D%3D";

    public Main() throws IOException {
        // 로딩 ---------------------------------------------------------------------------------------------------------
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\USER\\IdeaProjects\\Event-Horizon\\src\\asset\\restartKit dong to code.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i).getCell(5) == null) {

            } else  {
                sheet.getRow(i).getCell(7).setCellType(CellType.STRING);
                dongToCode.put(sheet.getRow(i).getCell(5).getStringCellValue(), sheet.getRow(i).getCell(7).getStringCellValue());
            }

        }

        // -------------------------------------------------------------------------------------------------------------


        setTitle("건축물대장 조회&대조 프로그램");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        contentPane = getContentPane();
        contentPane.add(button_download);

        setLayout(null);
        button_download.setBounds(200, 66, 100, 66);
        add(button_download);
        createMenu();
        setSize(500, 200);
        setVisible(true);

    }

    void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open..");

        openItem.addActionListener(new OpenActionListener());
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
    }

    class OpenActionListener implements ActionListener {

        JFileChooser chooser;

        OpenActionListener() {
            chooser = new JFileChooser();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xlsx", "xlsx");
            chooser.setFileFilter(filter);

            int ret = chooser.showOpenDialog(null);
            if (ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null,
                        "파일을 선택하지 않았습니다. ",
                        "경고",
                        JOptionPane.WARNING_MESSAGE);
            }


            String filePath = chooser.getSelectedFile().getPath();

            button_download.addActionListener(e1 -> {

                JFileChooser saveFileChooser = new JFileChooser();
                saveFileChooser.setFileFilter(filter);
                if (saveFileChooser.showSaveDialog(null) != 0) {
                    System.out.println(saveFileChooser.showSaveDialog(null));
                    JOptionPane.showMessageDialog(null,
                            "저장 실패",
                            "경고",
                            JOptionPane.WARNING_MESSAGE);
                }

                String output_filePath = saveFileChooser.getSelectedFile().getAbsolutePath() + ".xlsx";

                try {
                    new LandRegisterCheck(filePath, output_filePath, API_KEY);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.out.println("저장에 실패했습니다. ");
                }
            });

        }
    }

    public static class LandRegisterCheck {

        // 건물 저장
        private ArrayList<Building> buildings = new ArrayList<>();
        // 부지 저장
        private ArrayList<Site> edubuilSites = new ArrayList<>();
        private ArrayList<Site> receivedSites;



        public LandRegisterCheck(String inputPath, String outputPath, String APIKEY) throws Exception {
            /*
            1. 도로명에서 동 추출 - 3 0
            2. 동으로 코드 추출 0
            3. 지번에서 번, 지 추출 - 4 0
            4. in use buildings & not in use buildings --> buildings 0
            5. buildings to edubuilSites
            6. get "receivedSites" e@, e@
            7. make report
             */

            // get buildings from excel --------------------------------------------------------------------------------
            FileInputStream fileInputStream = new FileInputStream(inputPath);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow row;


            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);

                Building building = new Building();

                Region region = new Region();
                String jiBun = row.getCell(4).getStringCellValue();
                String name = row.getCell(2).getStringCellValue();
                region.setName(name);
                region.setAddress(jiBun);
                String[] bunJi = getBunJi(jiBun);
                if (bunJi[2].equals("3")) {
                    region.setSan(true);
                }
                region.setBun(bunJi[0]);
                region.setJi(bunJi[1]);
                String codeFromDong = getCodeFromDong(getDong(row.getCell(3).getStringCellValue()));
                System.out.println("정지원입니다. " + i);
                region.setCode1(codeFromDong.substring(0, 5));
                region.setCode2(codeFromDong.substring(5));
                building.setRegion(region);
                building.setGrossFloorArea(row.getCell(9).getNumericCellValue());
                buildings.add(building);
            }
            //----------------------------------------------------------------------------------------------------------


            // make edubuilSites from excel ----------------------------------------------------------------------------
            Site primingWater = new Site();
            primingWater.setRegion(buildings.get(0).getRegion());
            primingWater.addBuilding(buildings.get(0));
            primingWater.setId(0);
            edubuilSites.add(primingWater);

            addBuildingsToSites(buildings, edubuilSites);

            // 연면적합, 건물 수 기입
            for (Site site : edubuilSites) {
                site.setBuildingNumber(site.getBuildings().size());
                double grossFloorArea = 0;
                for (int j = 0; j < site.getBuildings().size(); j++) {
                    grossFloorArea += site.getBuildings().get(j).getGrossFloorArea();
                }
                site.setGrossFloorArea(grossFloorArea);
            }

            // ---------------------------------------------------------------------------------------------------------


            // make "receivedSites" from API ---------------------------------------------------------------------------
            receivedSites = getSitesFromAPI(APIKEY);
            for (Site s : receivedSites) {
                System.out.println(s.getId());
            }

            // ---------------------------------------------------------------------------------------------------------


            // make output and serve e4, e5 ----------------------------------------------------------------------------
            makeOutput(outputPath);
            // ---------------------------------------------------------------------------------------------------------

        }

        // output 만들기
        private void makeOutput(String outputPath) {
            List<ForReport> outputLists = new ArrayList<>();
            // output 생성 및 에러코드4,5 발급
            for (int i = 0; i < edubuilSites.size(); i++) {
                ForReport forReport = new ForReport();
                Error error;

                // region
                forReport.setRegion(edubuilSites.get(i).getRegion());

                // eduBuil
                forReport.setEdubuilBuildingNumber(edubuilSites.get(i).getBuildingNumber());
                forReport.setEdubuilGrossFloorArea(edubuilSites.get(i).getGrossFloorArea());

                // API received
                forReport.setLandRegisteredBuildingNumber(receivedSites.get(i).getBuildingNumber());
                forReport.setLandRegisteredGrossFloorArea(receivedSites.get(i).getGrossFloorArea());

                // error
                error = receivedSites.get(i).getErrorMessages();
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



            try(FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
                workbook.write(fileOutputStream);
            } catch (IOException ignored){}
        }


        // sites 에 빌딩 넣기
        private void addBuildingsToSites(ArrayList<Building> buildings, ArrayList<Site> sites) {
            for (int i = 1; i < buildings.size(); i++) {
                Region region = buildings.get(i).getRegion();

                boolean breakFlag = true;
                for (Site site : sites) {
                    boolean isCode1Same = site.getRegion().getCode1().equals(region.getCode1());
                    boolean isCode2Same = site.getRegion().getCode2().equals(region.getCode2());
                    boolean isBunSame = site.getRegion().getBun().equals(region.getBun());
                    boolean isJiSame = site.getRegion().getJi().equals(region.getJi());

                    if (isCode1Same && isCode2Same && isBunSame && isJiSame) {
                        site.addBuilding(buildings.get(i));
                        breakFlag = false;
                        break;
                    }
                }
                if (breakFlag) {
                    Site newSite = new Site();
                    newSite.setRegion(region);
                    newSite.addBuilding(buildings.get(i));
                    newSite.setId(i);
                    sites.add(newSite);
                }
            }
        }



        // 도로명 넣으면 동 리턴함
        String getDong(String doromyeong) {
            String[] splitedString = doromyeong.split("\\(");

            String returnValue = splitedString[splitedString.length - 1];
            returnValue = returnValue.split("\\)")[0];
            return returnValue;
        }


        // 동으로 코드 리턴함
        String getCodeFromDong(String dong) {
            for (Map.Entry<String, String>  a : dongToCode.entrySet()) {
                if (a.getKey().equals(dong)) {
                    return a.getValue();
                }
            }
            return "10자리 코드를 찾지 못 했습니다. ";
        }

        // 지번주소 넣으면 번, 지 리턴함
        String[] getBunJi(String jiBun) {
            String splitedString[] = jiBun.split("-");
            int bun = 0, ji = 0;
            int[] bunJiSanInteger = {bun, ji, 0};

            if (splitedString.length == 1) {
                String[] bun_aa = jiBun.split("\\s+");
                String bun_a = bun_aa[3];

                if (bun_a.contains("산")) {
                    bunJiSanInteger[2] = 3;
                    bun_a = bun_a.replaceAll("산", "");
                }
                bun = Integer.parseInt(bun_a);
            } else {
                String[] bun_a = splitedString[0].split("\\s+");

                if (bun_a[bun_a.length - 1].contains("산")) {
                    bunJiSanInteger[2] = 3;
                    bun_a[bun_a.length - 1] = bun_a[bun_a.length - 1].replaceAll("산", "");
                }
                bun = Integer.parseInt(bun_a[bun_a.length - 1]);
                String[] ji_a = splitedString[1].split("\\s+");
                ji = Integer.parseInt(ji_a[0]);
            }

            String[] bunJiSan = {"", "", ""};
            if (bun > 999) {
                bunJiSan[0] = String.valueOf(bun);
            } else if (bun > 99) {
                bunJiSan[0] = "0" + bun;
            } else if (bun > 9) {
                bunJiSan[0] = "00" + bun;
            } else if (bun > 0) {
                bunJiSan[0] = "000" + bun;
            } else {
                bunJiSan[0] = "0000";
                System.out.println("개버그 0000은 있을 수 없다. ");
            }
            if (ji > 999) {
                bunJiSan[1] = String.valueOf(ji);
            } else if (ji > 99) {
                bunJiSan[1] = "0" + ji;
            } else if (ji > 9) {
                bunJiSan[1] = "00" + ji;
            } else if (ji > 0) {
                bunJiSan[1] = "000" + ji;
            } else {
                bunJiSan[1] = "0000";
            }
            bunJiSan[2] = String.valueOf(bunJiSanInteger[2]);
            return bunJiSan;
        }

        ArrayList<Site> getSitesFromAPI(String APIKEY) throws IOException {
            Gson gson = new Gson();
            java.lang.reflect.Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            ArrayList<Site> returnSiteArray = new ArrayList<>();

            // 내일 만들거
//            getStringBuilderFromAPI(APIKEY, )
            for (int i = 0; i < edubuilSites.size(); i++){
                StringBuilder apiReceivedString = getStringBuilderFromAPI(APIKEY, i, edubuilSites.get(i).getRegion(), 1);

                Map<String, Object> receivedMap = gson.fromJson(apiReceivedString.toString(), mapType);
                Map<String, Object> innerBody = (Map<String, Object>) ((Map<String, Object>) receivedMap.get("response")).get("body");

                Map<String, Object> innerItems;

                double grossFloorArea = 0;
                int buildingNumber = 0;
                Error error = new Error();

                if (innerBody.get("items").getClass().equals(String.class)) {
                    // 총괄 없음
                    apiReceivedString = getStringBuilderFromAPI(APIKEY, i, edubuilSites.get(i).getRegion(), 0);
                    receivedMap = gson.fromJson(apiReceivedString.toString(), mapType);
                    innerBody = (Map<String, Object>) ((Map<String, Object>) receivedMap.get("response")).get("body");
                    if (innerBody.get("items").getClass().equals(String.class)) {
                        // 표제부가 없는 경우 - code 1
                        error.setE1(true);

                    } else {
                        // 총괄이 없고 표제부 있는 경우 - B
                        apiReceivedString = getStringBuilderFromAPI(APIKEY, i, edubuilSites.get(i).getRegion(), 0);
                        receivedMap = gson.fromJson(apiReceivedString.toString(), mapType);
                        innerBody = (Map<String, Object>) ((Map<String, Object>) receivedMap.get("response")).get("body");
                        innerItems = (Map<String, Object>) innerBody.get("items");


                        if (innerItems.get("item").getClass().equals(ArrayList.class)) {
                            // 대장오류 건축물이 2개 이상이나 총괄 표제부가 없음 - code 6
                            error.setE6(true);

                            System.out.println(((ArrayList<Object>)innerItems.get("item")).toString());

                            ArrayList<Object> innerItem = (ArrayList<Object>) innerItems.get("item");

                            for (int j = 0; j < innerItem.size(); j++) {
                                buildingNumber += (int) (double) (((Map<String, Object>) innerItem.get(j)).get("atchBldCnt"));
                                grossFloorArea += (double) (((Map<String, Object>) innerItem.get(j)).get("totArea"));
                            }
                            buildingNumber += innerItems.size();


                        } else {
                            // 멀쩡히 표제부 1개
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
                        error.setE3(true);

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

                            apiReceivedString = getStringBuilderFromAPI(APIKEY, i, edubuilSites.get(i).getRegion(), 0);
                            receivedMap = gson.fromJson(apiReceivedString.toString(), mapType);
                            innerBody = (Map<String, Object>) ((Map<String, Object>) receivedMap.get("response")).get("body");
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

                            apiReceivedString = getStringBuilderFromAPI(APIKEY, i, edubuilSites.get(i).getRegion(), 0);
                            receivedMap = gson.fromJson(apiReceivedString.toString(), mapType);
                            innerBody = (Map<String, Object>) ((Map<String, Object>) receivedMap.get("response")).get("body");
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
                site.setRegion(edubuilSites.get(i).getRegion());
                site.setGrossFloorArea(grossFloorArea);
                site.setBuildingNumber(buildingNumber);
                site.setError(error);
                site.setId(i);
                returnSiteArray.add(site);

                System.out.println(Math.round((double) i / edubuilSites.size() * 100) / 100.0);

            }


            return returnSiteArray;
        }


        StringBuilder getStringBuilderFromAPI(String APIKEY, int i, Region region, int whatData) throws IOException {
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


        private String urlEncoder(String text) throws UnsupportedEncodingException {
            return URLEncoder.encode(text, "UTF-8");
        }





        private static void makeFirstSheet(java.util.List<Site> edubuilSites, XSSFSheet sheet) {
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

            for (int i = 0; i < edubuilSites.size(); i++) {
                row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(i + 1);

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


    }



    public static void main(String[] args) throws IOException {
        new Main();
    }
}
