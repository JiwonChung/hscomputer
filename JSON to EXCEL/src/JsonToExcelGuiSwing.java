import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JsonToExcelGuiSwing extends JFrame {
    private File jsonFile;
    private JLabel fileLabel;

    public JsonToExcelGuiSwing() {
        super("JSON to Excel Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 100));
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JButton chooseFileButton = new JButton("Choose JSON File");
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(JsonToExcelGuiSwing.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                jsonFile = fileChooser.getSelectedFile();
                fileLabel.setText(jsonFile.getName());
            }
        });
        topPanel.add(chooseFileButton);

        fileLabel = new JLabel("No file selected");
        topPanel.add(fileLabel);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        JButton convertButton = new JButton("Convert to Excel");
        convertButton.addActionListener(e -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(jsonFile);

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Data");
                List<String> header = new ArrayList<>();
                header.add("Key");
                header.add("Value");

                int rowNum = 0;
                Row headerRow = sheet.createRow(rowNum++);
                for (int i = 0; i < header.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(header.get(i));
                }

                addData(sheet, root, rowNum);

                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(JsonToExcelGuiSwing.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        workbook.write(outputStream);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        bottomPanel.add(convertButton);

        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void addData(Sheet sheet, JsonNode node, int rowNum) {
        if (node.isObject()) {
            for (JsonNode child : node) {
                addData(sheet, child, rowNum);
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                addData(sheet, child, rowNum);
            }
        } else {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(node.asText());
            row.createCell(1).setCellValue(node.asText());
        }
    }

    public static void main(String[] args) {
        JsonToExcelGuiSwing frame = new JsonToExcelGuiSwing();
        frame.setVisible(true);
    }
}
