import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeGo extends JFrame {

    Container contentPane;
    JButton button_download = new JButton("저장하기");
    public WeGo() throws HeadlessException {
        setTitle("엘셀을 합쳐보아요. ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = getContentPane();
        contentPane.add(button_download);

        setLayout(null);
        button_download.setBounds(100, 66, 100, 66);
        add(button_download);
        createMenu();
        setSize(300, 200);
        setVisible(true);
    }

    void createMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");

        // open 메뉴 아이템에 Action listener 등록
        openItem.addActionListener(new OpenActionListener());
        fileMenu.add(openItem);
        mb.add(fileMenu);
        this.setJMenuBar(mb);
    }

    class OpenActionListener implements ActionListener {
        JFileChooser chooser;
        OpenActionListener() {
            chooser = new JFileChooser();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xlsx", "xlsx"
            );
            chooser.setFileFilter(filter);

            // 파일 다이얼로그 출력
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
                if (saveFileChooser.showSaveDialog(null)
                != 0) {
                    System.out.println(saveFileChooser.showSaveDialog(null));
                    JOptionPane.showMessageDialog(null,
                            "저장을 하지 않았습니다. ",
                            "경고",
                            JOptionPane.WARNING_MESSAGE);
                }

                String output_filePath = saveFileChooser.getSelectedFile().getAbsolutePath() + ".xlsx";

                // 파일 저장하기
                try {
                    new ExcelMerge(filePath, output_filePath);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });

        }
    }
    public static void main(String[] args) {
        new WeGo();
    }

    public static class ExcelMerge {
        private String FILE_NAME = "/Users/jiwon/code repo/EcelFIle_merger/src/main/resources/excelFile/File.xlsx";
        private String OUTPUT_FILE_NAME = "/Users/jiwon/code repo/EcelFIle_merger/src/main/resources/excelFile/wego.xlsx";
        public ExcelMerge(String FILE_NAME, String OUTPUT_FILE_NAME) throws IOException {

            this.FILE_NAME = FILE_NAME;
            this.OUTPUT_FILE_NAME = OUTPUT_FILE_NAME;

            // 파일 여러개도 할 수 있게 해주기 FILE_NAME을 []로 받기

            System.out.println("we go");

            java.util.List<java.util.List<Object>> objectList = new ArrayList<>();
            // read
            FileInputStream excelFile = new FileInputStream(FILE_NAME);
            Workbook workbook = new XSSFWorkbook(excelFile);

            // have to iterate from here
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    // row 바꾸기
                    java.util.List<Object> objectList_for_row = new ArrayList<>();

                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex);
                        objectList_for_row.add(panorama(cell));
                    }
                    objectList.add(objectList_for_row);
                }
            }

            // write
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet sheet = xssfWorkbook.createSheet("output");

            int rowNumb = 0;
            for (List<Object> objectList_for_row : objectList) {
                Row row = sheet.createRow(rowNumb++);
                int colNum = 0;
                for (Object object : objectList_for_row) {
                    Cell cell = row.createCell(colNum++);

                    if (object instanceof String) {
                        cell.setCellValue((String) object);
                    } else if (object instanceof Integer) {
                        cell.setCellValue((Integer) object);
                    } else {
                        cell.setCellValue("");
                    }
                }
            }

            FileOutputStream outputStream = new FileOutputStream(OUTPUT_FILE_NAME);
            xssfWorkbook.write(outputStream);
            xssfWorkbook.close();


        }

        private static Object panorama(Cell cell) {
            Object object;
            if (cell.getCellType() == CellType.STRING) {
                object = cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                object = (long) cell.getNumericCellValue();
            } else {
                object = "";
            }
            return object;

        }


    }
}
