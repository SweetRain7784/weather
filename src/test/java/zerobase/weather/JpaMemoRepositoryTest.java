package zerobase.weather;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional //test 코드에서 트랜잭션을 사용하게 되면 다 rollback 처리를 함(db 원상복구)
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository; //JpaMemoRepository 객체를 주입받아 테스트에 사용

    @Test
    void insertMemoTest(){
        //given
        Memo newMemo = new Memo(10,"this is jpa memo");

        //when
        jpaMemoRepository.save(newMemo);

        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size()>0);
    }

    @Test
    void findByIdTest(){
        //given
        Memo newMemo = new Memo(11,"jpa");

        //when
        Memo memo = jpaMemoRepository.save(newMemo);
        System.out.println(memo.getId());

        //then
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId());
        assertEquals(result.get().getText(),"jpa");
    }
}
