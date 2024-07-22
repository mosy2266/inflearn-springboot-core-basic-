package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationSingletonTest {

    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        //모두 같은 인스턴스를 참고하고 있다
        System.out.println("memberService -> memberRepository = " + memberRepository1);
        System.out.println("orderService -> memberRepository = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);

        /*
        memberService와 orderService는 그렇다 쳐도 memberRepository는 총 3번 호출되어야 하지 않나?
        1. 스프링 컨테이너가 스프링 빈을 등록하는 과정에서 @Bean이 붙어 있는 memberRepository() 호출
        2. memberService() 로직에서 memberRepository() 호출
        3. orderService() 로직에서 memberRepository() 호출
        근데 AppConfig.java에서 설정해둔 호출 로그가 모두 1번씩만 출력되었다
        */
    }


    @Test
    void configurationDeep() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        //AnnotationConfigApplicationContext에 파라미터로 넘긴 값 또한 스프링 빈으로 등록됨 -> AppConfig도 스프링 빈으로 등록된다

        AppConfig bean = ac.getBean(AppConfig.class);
        System.out.println("bean = " + bean.getClass());

        /*
        출력 결과 :
            bean = class hello.core.AppConfig$$SpringCGLIB$$0

        스프링이 CGLIB이라는 바이트코드 조작 라이브러리를 통해 AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고, 그
        클래스를 스프링 빈으로 등록한 것!
        -> 새로 만들어진 이 임의의 클래스가 싱글톤이 보장되도록 해준다

        @Bean이 붙은 메서드마다
        "스프링 빈이 이미 존재하면 해당 빈을 반환하고, 스프링 빈이 없으면 생성해서 등록한 뒤 반환하는 코드"가 동적으로 만들어짐

        @Configuration 애너테이션을 붙이지 않았을 때 출력 결과 :
            bean = class hello.core.AppConfig

        CGLIB 라이브러리를 사용하지 않음을 볼 수 있다! -> 바이트코드 조작 X, 순수한 AppConfig로 스프링 빈에 등록됨
        */
    }
}

/*
@Configuration을 사용하지 않았을 때 configurationTest()의 출력 결과 : memberRepository()를 3번 호출하고, 인스턴스도 서로 다르다

    call AppConfig.memberService
    call AppConfig.memberRepository
    call AppConfig.orderService
    call AppConfig.memberRepository
    call AppConfig.memberRepository

    memberService -> memberRepository = hello.core.member.MemoryMemberRepository@61526469
    orderService -> memberRepository = hello.core.member.MemoryMemberRepository@274872f8
    memberRepository = hello.core.member.MemoryMemberRepository@76ba13c

-> 싱글톤이 보장되지 않는다

스프링 설정 정보에는 항상 @Configuration을 사용하자!
*/



