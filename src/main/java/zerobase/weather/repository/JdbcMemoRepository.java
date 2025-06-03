package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

//repository 생성
@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    //application.properties 파일에서 지정한 MySQL 데이터베이스 연결 정보를 사용하여 JdbcTemplate 객체를 생성
    @Autowired
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //Memo 객체를 데이터베이스에 저장하는 메서드
    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    //Memo 객체를 통해 데이터베이스에서 전체 조회하는 메서드
    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    //Memo 객체를 통해 데이터베이스에서 id로 조회하는 메서드
    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst(); //Optional 객체를 통해 만약 id가 없을 경우 null을 반환하지 않도록 처리
    }

    private RowMapper<Memo> memoRowMapper(){
        //ResultSet
        //데이터를 가져올 때 {id=1, text='this is a memo'} 형식으로 가져오게 되는데, 이를 Memo 객체로 변환하는 역할 중괄호나 대괄호 없이 가져오게 된다.
        return (rs, rowNum)-> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
