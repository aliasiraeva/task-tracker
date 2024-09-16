import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Main {

    public static final String EXCEL_DATA_FILE = "tracker.xlsx";
    private static Workbook workbook;

    public static void main(String[] args) {
        List<Employee> employees = readEmployeesFromFile();
        Office office = new Office(employees);
        office.workProcess();
        writeToFile(office.getEmployees());
    }

    public static List<Employee> readEmployeesFromFile() {
        List<Employee> employees = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(EXCEL_DATA_FILE)) {
            workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet("Лист1");
            int i = 1;
            while (sheet.getRow(i) != null
                    && sheet.getRow(i).getCell(0) != null
                    && sheet.getRow(i).getCell(0).getStringCellValue() != null) {
                Employee employee = new Employee(sheet.getRow(i).getCell(0).getStringCellValue());
                employees.add(employee);
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    public static void writeToFile(List<Employee> employees) {
        try (FileOutputStream outputStream = new FileOutputStream(EXCEL_DATA_FILE)) {
            Sheet sheet = workbook.getSheet("Лист1");
            for (int i = 1; i <= employees.size(); i++) {
                Employee employee = employees.get(i - 1);
                if (sheet.getRow(i).getCell(1) == null) {
                    sheet.getRow(i).createCell(1);
                }
                sheet.getRow(i).getCell(1).setCellValue(employee.getTimeInHours());
                if (sheet.getRow(i).getCell(2) == null) {
                    sheet.getRow(i).createCell(2);
                }
                sheet.getRow(i).getCell(2).setCellValue(8 - employee.getTimeInHours());
                if (sheet.getRow(i).getCell(3) == null) {
                    sheet.getRow(i).createCell(3);
                }
                sheet.getRow(i).getCell(3).setCellValue(8);
                if (sheet.getRow(i).getCell(4) == null) {
                    sheet.getRow(i).createCell(4);
                }
                sheet.getRow(i).getCell(4).setCellValue((double) employee.getTimeInHours() / 8);
            }
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}