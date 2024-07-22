package hello.core.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    static class TestConfig {
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        //Thread A : A 사용자가 10000원을 주문
        //statefulService1.order("user A", 10000);
        int userAPrice = statefulService1.order("user A", 10000); //지역변수를 선언해주자
        //Thread B : B 사용자가 20000원을 주문
        //statefulService2.order("user B", 20000);
        int userBPrice = statefulService2.order("user B", 20000);

        //Thread A : 사용자 A의 주문 금액 조회
        //int price = statefulService1.getPrice();
        //Thread A : 사용자 A는 10000원을 기대했지만, 실제로 20000원이 출력됨
        //System.out.println("price = " + price);

        //사용자 A의 금액이 제대로 조회된다
        System.out.println("price = " + userAPrice);

        //assertThat(statefulService1.getPrice()).isEqualTo(20000);
    }
}