package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {
    //application.properties에 있는 openweathermap.key 값을 가져오기
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    private final DateWeatherRepository dateWeatherRepository;
    
    //새벽 1시마다 다이어리를 저장해주는 함수
    @Transactional
    @Scheduled(cron="0 0 1 * * *")   //초분시일월 -> 매일 새벽 1시마다 동작
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi(){
        //open weather map에서 날씨 정보를 가져오기
        String weatherData= getWeatherString();

        //받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        //파싱된 날씨를 dateweather 객체로 변환
        DateWeather dateWeather = new DateWeather();

        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((double)parsedWeather.get("temp"));
        return dateWeather;
    }

    // 다이어리 생성 - 다이어리에 필요한 날짜와 텍스트를 받아 다이어리를 생성하는 함수
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text){
        logger.info("stared to create diary");
        //날씨 데이터 가져오기(DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        //우리 db에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);

        logger.info("end to create diary");
    }

    // 다이어리 조회 - 다이어리에 필요한 날짜를 받아 해당 날짜의 다이어리를 조회하는 함수
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date){
        logger.debug("read diary");
        if(date.isAfter(LocalDate.ofYearDay(3050, 1))){
            throw new InvalidDate();
        }
        return diaryRepository.findAllByDate(date);
    }

    // 다이어리 조회 - 범위 날짜에 대한 다이어리를 조회하는 함수
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate){
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    // 다이어리 수정 - 다이어리에 필요한 날짜와 텍스트를 받아 해당 날짜의 다이어리를 수정하는 함수
    public void updateDiary(LocalDate date, String text){
        //날짜에 맞는 데이터 중에 첫번째 다이어리 하나를 받아서 nowDiary에 저장
        Diary nowDiary = diaryRepository.getFirstByDate(date);

        //가져온 다이어리의 데이터 중 텍스트만 수정
        nowDiary.setText(text);

        //수정된 다이어리 저장
        diaryRepository.save(nowDiary);
    }

    // 다이어리 삭제 - 다이어리에 필요한 날짜를 받아 해당 날짜의 다이어리를 삭제하는 함수
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size()==0){
            //api를 통해 현재 데이터를 가져와서 저장하는 방식
            return getWeatherFromApi();
        }else{
            return dateWeatherListFromDB.get(0);
        }
    }

    private String getWeatherString(){
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="+apiKey;

        try{
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode=connection.getResponseCode();

            BufferedReader br;
            if(responseCode ==200){
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();

            return response.toString();

        }catch(Exception e){
            return "falied to get response";
        }
    }

    //json 파싱 함수
    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject)jsonParser.parse(jsonString);
        }catch(ParseException e){
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);

        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }
}
