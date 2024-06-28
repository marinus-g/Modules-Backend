package academy.mischok.modules;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "";
    static String[] HEADERS = {};
    static String SHEET = "Quiz";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<QuizExcelEntity> excelToQuiz(InputStream inputStream) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rowIterator = sheet.iterator();
            List<QuizExcelEntity> quizExcelEntities = new ArrayList<QuizExcelEntity>();

            int rowNum = 0;
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();

                if (rowNum == 0) {
                    rowNum++;
                    continue;
                }
                Iterator<Cell> cellIterator = currentRow.iterator();
                QuizExcelEntity quizExcelEntity = new QuizExcelEntity();
                int cellNum = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellNum) {
                        case 0:
                            quizExcelEntity.setFullName(cell.getStringCellValue());
                            break;
                        case 1:
                            quizExcelEntity.setFirstName(cell.getStringCellValue());
                            break;
                        case 2:
                            quizExcelEntity.setLastName(cell.getStringCellValue());
                            break;
                        case 3:
                            quizExcelEntity.setEmail(cell.getStringCellValue());
                            break;
                        case 4:
                            quizExcelEntity.setModule(cell.getStringCellValue());
                            break;
                        case 5:
                            quizExcelEntity.setDueToDate(cell.getLocalDateTimeCellValue().toLocalDate());
                            break;
                        case 6:
                            quizExcelEntity.setEvaluationDate(cell.getLocalDateTimeCellValue().toLocalDate());
                            break;
                        case 7:
                            quizExcelEntity.setTurnedIn(cell.getStringCellValue());
                            break;
                        case 8:
                            quizExcelEntity.setFeedback(cell.getStringCellValue());
                            break;
                        case 9:
                            quizExcelEntity.setAchievedPoints((int) cell.getNumericCellValue());
                            break;
                        case 10:
                            quizExcelEntity.setMaxPoints((int) cell.getNumericCellValue());
                            break;
                        case 11:
                            quizExcelEntity.setPercent(cell.getNumericCellValue());
                            break;
                        default:
                            break;
                    }
                    cellNum++;
                }
                quizExcelEntities.add(quizExcelEntity);
            }
            workbook.close();
            return quizExcelEntities;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
