package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    //다이어리 생성 - 다이어리에 필요한 날짜와 텍스트를 받아 다이어리를 생성하는 API
    @ApiOperation(value="createDiary 한줄 소개", notes="note")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text){
        diaryService.createDiary(date, text);
    }

    //다이어리 조회 - 다이어리에 필요한 날짜를 받아 해당 날짜의 다이어리를 조회하는 API
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value="날짜 형식 : YYYY-MM-DD",example="2020-02-02") LocalDate date){
        return diaryService.readDiary(date);
    }

    //다이어리 조회 - 범위 날짜에 대한 다이어리를 조회하는 API
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        return diaryService.readDiaries(startDate, endDate);
    }

    //다이어리 수정 - 다이어리에 필요한 날짜와 텍스트를 받아 해당 날짜의 다이어리를 수정하는 API
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text){
        diaryService.updateDiary(date,text);
    }

    // 다이어리 삭제 - 다이어리에 필요한 날짜를 받아 해당 날짜의 다이어리를 삭제하는 API
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
