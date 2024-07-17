package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;

//정액 할인 정책 구현체
//VIP면 1000원 할인받고 아니라면 할인 X
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
