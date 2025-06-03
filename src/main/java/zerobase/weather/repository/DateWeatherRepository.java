package zerobase.weather.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
    //date에 따라 그날의 weather 값을 가져오는 함수 작성
    List<DateWeather> findAllByDate(LocalDate date);
}
