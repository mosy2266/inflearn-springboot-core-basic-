package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//정액 할인 정책 구현체
//VIP면 1000원 할인받고 아니라면 할인 X
@Component //DiscountPolicy의 하위 타입인 FixDiscountPolicy와 RateDiscountPolicy 둘 다 스프링 빈으로 선언하면?
//@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy {

    private int discountFixAmount = 1000;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
