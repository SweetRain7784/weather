package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e) {
        System.out.println("error from GlobalExceptionHandler");
        e.printStackTrace();  // 에러 상세 로그 출력
        return "서버 내부 오류가 발생했습니다: " + e.getMessage();
    }
}
