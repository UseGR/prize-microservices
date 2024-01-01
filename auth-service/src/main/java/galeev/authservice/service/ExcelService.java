package galeev.authservice.service;

import galeev.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final List<String> tableColumnNames = List.of("id", "Имя", "Ник", "ФИО", "Номер телефона",
            "Дата рождения", "Пол", "Узнал(а) о мероприятии из", "Выиграл(а) в розыгрыше", "Является админом");

    @SneakyThrows
    private Workbook prepareHeader() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Зарегистрированные");
        sheet.setColumnWidth(0, 3224);
        sheet.setColumnWidth(1, 3532);
        sheet.setColumnWidth(2, 3708);
        sheet.setColumnWidth(3, 7212);
        sheet.setColumnWidth(4, 4644);
        sheet.setColumnWidth(5, 4176);
        sheet.setColumnWidth(6, 2874);
        sheet.setColumnWidth(7, 7301);
        sheet.setColumnWidth(8, 6862);
        sheet.setColumnWidth(9, 5198);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setFamily(FontFamily.ROMAN);
        headerStyle.setFont(font);

        for (int i = 0; i < tableColumnNames.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(tableColumnNames.get(i));
            headerCell.setCellStyle(headerStyle);
        }

        return workbook;
    }

    @SneakyThrows
    public Mono<Workbook> getRegisteredUsersFile(Flux<User> users) {
        Workbook workbook = prepareHeader();
        Sheet sheet = workbook.getSheet("Зарегистрированные");

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        AtomicInteger rowCounter = new AtomicInteger(0);
        AtomicInteger cellCounter = new AtomicInteger(0);

        return users
                .collectList()
                .flatMap(allUsers -> {
                    for (User user : allUsers) {
                        Row row = sheet.createRow(rowCounter.incrementAndGet());
                        Cell cell = row.createCell(cellCounter.get());
                        cell.setCellValue(user.getId().toString());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getFirstname());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getUsername());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getFullname());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getPhoneNumber());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getDateOfBirth());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getSex() == User.Sex.MALE ? "Мужской" : "Женский");
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getKnowFrom().toString());
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getIsParticipated() ? "Да" : "Нет");
                        cell.setCellStyle(style);

                        cell = row.createCell(cellCounter.incrementAndGet());
                        cell.setCellValue(user.getIsAdmin() ? "Да" : "Нет");
                        cell.setCellStyle(style);

                        cellCounter.set(0);
                    }
                    rowCounter.set(0);

                    return Mono.just(workbook);
                });
    }
}
