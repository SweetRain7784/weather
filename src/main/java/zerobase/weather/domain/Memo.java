package zerobase.weather.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo") //JPA 엔티티로 지정
public class Memo {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) //id 필드를 자동으로 생성
    private int id;
    private String text;
}
