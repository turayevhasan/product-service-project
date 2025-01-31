package uz.pdp.utils;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.pdp.model.AuthUser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelWorker {

    public String writeUserDetailsToExcelAndGetName(List<AuthUser> authUsers, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sample Sheet");
        // Create a row and put some cells in it
        Row row = sheet.createRow(0);

        //to make bold text file header row
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Cell cell1 = row.createCell(0);
        cell1.setCellValue("Id");
        cell1.setCellStyle(headerCellStyle);

        Cell cell2 = row.createCell(1);
        cell2.setCellValue("Name");
        cell2.setCellStyle(headerCellStyle);

        Cell cell3 = row.createCell(2);
        cell3.setCellValue("Age");
        cell3.setCellStyle(headerCellStyle);

        Cell cell4 = row.createCell(3);
        cell4.setCellValue("Gender");
        cell4.setCellStyle(headerCellStyle);

        Cell cell5 = row.createCell(4);
        cell5.setCellValue("Email");
        cell5.setCellStyle(headerCellStyle);

        // Write data
        for (int i = 0; i < authUsers.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);

            Cell idCell = dataRow.createCell(0);
            idCell.setCellValue(String.valueOf(authUsers.get(i).getId()));

            Cell nameCell = dataRow.createCell(1);
            nameCell.setCellValue(authUsers.get(i).getName());

            Cell ageCell = dataRow.createCell(2);
            ageCell.setCellValue(authUsers.get(i).getAge());

            Cell genderCell = dataRow.createCell(3);
            genderCell.setCellValue(authUsers.get(i).getGender());

            Cell emailCell = dataRow.createCell(4);
            emailCell.setCellValue(authUsers.get(i).getEmail());
        }
        // Write the output to a file
        try (FileOutputStream fileOutputStream = new FileOutputStream("E:/spring-core/downloads/files/" + fileName + ".xlsx")) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName + ".xlsx";
    }

}