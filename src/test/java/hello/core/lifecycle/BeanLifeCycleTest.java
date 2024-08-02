package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close(); //스프링 컨테이너를 종료(ConfigurableApplicationContext 필요)
    }

    /*
    !! 스프링 빈의 이벤트 라이프 사이클
    스프링 컨테이너 생성 - 스프링 빈 생성 - 의존관계 주입 - 초기화 콜백 - 사용 - 소멸전 콜백 - 스프링 종료

    InitializingBean, DisposableBean 인터페이스 적용 전 출력 결과

    생성자 호출, url = null
    connect: null
    call:null message = 초기화 연결 메시지

    -> 객체를 생성하는 단계에서는 url이 없기 때문에 null(당연함)
    ----------------------------------------------------------
    적용 후 출력 결과(public class NetworkClient implements InitializingBean, DisposableBean)

    생성자 호출, url = null
    connect: null
    call:null message = 초기화 연결 메시지
    NetworkClient.afterPropertiesSet
    connect: http://hello-spring.dev
    call:http://hello-spring.dev message = 초기화 연결 메시지
    NetworkClient.destroy
    close: http://hello-spring.dev

    -> 초기화 메서드가 의존관계 주입 완료 이후 적절하게 호출됨
    -> 스프링 컨테이너의 종료가 호출되자 소멸 메서드가 호출됨

    해당 방법의 단점
    1. 스프링 전용 인터페이스에 의존
    2. 초기화 및 소멸 메서드의 이름 변경 불가
    3. 코드를 고칠 수 없는 외부 라이브러리에 적용 불가
    */

    @Configuration
    static class LifeCycleConfig {

        //@Bean(initMethod = "init", destroyMethod = "close") //설정 정보에 초기화 및 소멸 메서드를 지정
        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }

    /*
    설정 정보에 초기화 및 소멸 메서드를 지정한 결과

    생성자 호출, url = null
    connect: null
    call:null message = 초기화 연결 메시지
    NetworkClient.init
    connect: http://hello-spring.dev
    call:http://hello-spring.dev message = 초기화 연결 메시지
    NetworkClient.close
    close: http://hello-spring.dev

    해당 방법의 특징
    1. 메서드 이름을 자유롭게 지정 가능
    2. 스프링 빈이 스프링 코드에 의존하지 않음
    3. 코드가 아니라 설정 정보(@Bean)에 의존하기 때문에 코드를 고칠 수 없는 외부 라이브러리에도 적용 가능

    @Bean의 destroyMethod 속성이 가진 종료 메서드 추론 기능
    - 라이브러리는 대부분 close, shutdown이라는 이름의 종료 메서드를 사용
    - 해당 속성의 기본값은 (inferred)(추론)로 등록되어 있음
    - 이 추론 기능은 close, shutdown이라는 이름의 메서드를 자동으로 호출해줌
    - 따라서 직접 스프링 빈으로 등록하면 종료 메서드는 따로 적어주지 않아도 잘 동작함
    - 추론 기능을 사용하기 싫으면 destroyMethod=""처럼 빈 공백을 지정해주면 됨
    */

    /*
    @PostConstruct, @PreDestroy 애너테이션을 적용한 결과

    생성자 호출, url = null
    connect: null
    call:null message = 초기화 연결 메시지
    NetworkClient.init
    connect: http://hello-spring.dev
    call:http://hello-spring.dev message = 초기화 연결 메시지
    NetworkClient.close
    close: http://hello-spring.dev

    해당 방법의 특징
    1. 최신 스프링에서 가장 권장하는 방법
    2. 애너테이션만 붙이면 되므로 매우 편리
    3. 스프링에 종속적인 기술이 아니라 자바 표준이므로 스프링이 아닌 다른 컨테이너에서도 동작
    4. 컴포넌트 스캔과 잘 어울림

    해당 방법의 유일한 단점
    - 외부 라이브러리에는 적용할 수 없음(애너테이션을 붙여 코드를 고쳐야 하므로)
    */
}

/*
결론!

초기화, 소멸 메서드를 사용할 때에는 @PostConstructor, @PreDestroy 애너테이션을 사용하자!
코드를 고칠 수 없는 외부 라이브러리를 초기화, 종료해야 할 땐 @Bean의 initMethod, destroyMethod 속성을 사용하자!
*/