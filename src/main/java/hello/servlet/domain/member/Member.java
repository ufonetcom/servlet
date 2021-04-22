package hello.servlet.domain.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {

    private Long id;
    private String user;
    private int age;

    public Member() {
    }

    public Member(String user, int age) {
        this.user = user;
        this.age = age;
    }
}
