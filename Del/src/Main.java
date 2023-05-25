import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        String[] inputPaths = {"C:\\Users\\USER\\IdeaProjects\\Del\\excelData\\allGHP.xlsx", "C:\\Users\\USER\\IdeaProjects\\Del\\excelData\\schools.xlsx"};

        FileInputStream fileInputStream = new FileInputStream(inputPaths[0]);


    }
}