package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//어플리케이션 전체의 동작 방식을 구성(config)하기 위해 구현 객체를 생성하고 연결하는 별도의 설정 클래스
//AppConfig를 스프링 기반으로 변경 -> configuration, bean 등의 애너테이션 사용
@Configuration //스프링 컨테이너는 해당 애너테이션이 붙은 AppConfig를 설정(구성) 정보로 사용
public class AppConfig {

    /*
    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }
    */

    //위 코드에는 중복이 존재하고, 역할에 따른 구현이 잘 보이지 않음 -> 리팩터링을 해보자

    @Bean //해당 메서드가 스프링 컨테이너에 스프링 빈으로 등록됨, 따로 설정해주지 않으면 메서드 명이 스프링 빈의 이름으로 사용됨
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService"); //호출 로그 남김. 1번
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService"); //1번
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    //다른 구현체로 변경할 땐 이 부분만 고치면 된다
    @Bean
    public MemoryMemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository"); //2번? 3번?
        return new MemoryMemberRepository();
    }

    //할인 정책의 변경이 필요할 땐 이 부분만 고치면 된다
    @Bean
    public DiscountPolicy discountPolicy() {
        //return new FixDiscountPolicy();
        return new RateDiscountPolicy(); //할인 정책을 바꾸어도 AppConfig(구성 영역)만 변경하면 된다!
        //클라이언트 코드인 OrderServiceImpl를 포함해서 사용 영역의 어떤 코드도 변경할 필요 X
    }
}

/*
AppConfig는 어플리케이션의 실제 동작에 필요한 구현 객체를 생성해준다
-> MemberServiceImpl, MemoryMemberRepository, OrderServiceImpl, FixDiscountPolicy

또한 생성한 객체 인스턴스의 참조를 생성자를 통해서 주입(연결)해준다
-> MemberServiceImpl에는 MemoryMemberRepository
   OrderServiceImpl에는 MemoryMemberRepository, FixDiscountPolicy

AppConfig를 도입하면서 어플리케이션이 크게 사용 영역과 구성 영역으로 분리되었다!
구성 영역 : 객체를 생성하고 구성(Configuration) -> AppConfig
*/