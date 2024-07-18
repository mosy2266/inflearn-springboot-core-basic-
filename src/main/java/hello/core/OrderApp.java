package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//주문과 할인 정책 실행
public class OrderApp {

    public static void main(String[] args) {
        //MemberService memberService = new MemberServiceImpl();
        //OrderService orderService = new OrderServiceImpl();

        /*
        AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();
        OrderService orderService = appConfig.orderService();
        */

        //스프링 컨테이너 적용
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        long memberId = 1L;
        Member member = new Member(memberId, "member A", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "item A", 20000);

        System.out.println("order = " + order);
    }
}

//JUnit 테스트를 사용해서 다시 테스트해보자
