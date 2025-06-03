package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JdbcMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //해당 어노테이션이 살아있는 경우에 아무리 테스트를 진행해도 DB에서 확인 불가능
public class JdbcMemoRepositoryTest {
    @Autowired
    JdbcMemoRepository jdbcMemoRepository; //JdbcMemoRepository 객체를 주입받아 테스트에 사용

    //insert 구문 테스트
    @Test
    void insertMemoTest(){
        //given 주어진 것
        Memo newMemo = new Memo(2,"insertMemoTest");

        //when ~했을 때
        jdbcMemoRepository.save(newMemo);

        //then ~결과값이 나온다
        Optional<Memo> result=jdbcMemoRepository.findById(2);
        assertEquals(result.get().getText(),"insertMemoTest");
    }

    @Test
    void findAllMemoTest(){
        List<Memo> memoList = jdbcMemoRepository.findAll();
        System.out.println(memoList); //[zerobase.weather.domain.Memo@1aa59698]로 출력됨
        assertNotNull(memoList);
    }
}
