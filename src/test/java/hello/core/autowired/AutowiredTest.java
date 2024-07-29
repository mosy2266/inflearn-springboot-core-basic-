package hello.core.autowired;

import hello.core.member.Member;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

public class AutowiredTest {

    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }

    static class TestBean {

        //아예 호출이 안된다
        @Autowired(required = false)
        public void setNoBean1(Member noBean1) {
            System.out.println("noBean1 = " + noBean1);
        }

        //null을 호출
        @Autowired
        public void setNoBean2(@Nullable Member noBean2) {
            System.out.println("noBean2 = " + noBean2);
        }

        //Optional.empty를 호출
        @Autowired
        public void setNoBean3(Optional<Member> noBean3) {
            System.out.println("noBean3 = " + noBean3);
        }
    }
}

/*
@Autowired(required = false) : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출되지 않음
org.springframework.lang.@Nullable : 자동 주입할 대상이 없으면 null이 입력됨
Optional<> : 자동 주입할 대상이 없으면 Optional.empty가 입력됨

Member는 스프링 빈이 아님 -> setNoBean1()은 @Autowired(required = false)이므로 호출 자체가 안 됨

위 Test 실행(출력) 결과
noBean2 = null
noBean3 = Optional.empty
*/