package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

//주문과 할인 정책 실행
public class OrderApp {

    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        OrderService orderService = new OrderServiceImpl();

        long memberId = 1L;
        Member member = new Member(memberId, "member A", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "item A", 10000);

        System.out.println("order = " + order);
    }
}

//JUnit 테스트를 사용해서 다시 테스트해보자
