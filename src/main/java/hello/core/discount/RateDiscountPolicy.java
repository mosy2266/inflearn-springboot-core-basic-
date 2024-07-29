package hello.core.discount;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

//정률 할인 정책
//@Qualifier("mainDiscountPolicy") //의존관계 주입 시 추가적인 방법을 제공하는 것 -> 주의 : 빈 이름을 변경하는 것이 아님
/*
@Qualifier는 NoUniqueBeanDefinitionException 오류를 해결하는 방법 중 하나
주입 시 @Qualifier를 붙여주고 등록한 이름을 적어주면 됨
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
                        @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) << 이런 식으로!
*/
//@Primary //@Autowired할 때 여러 번 매칭되면 @Primary가 붙은 빈이 우선권을 가진다!
@Component  //컴포넌트 스캔의 대상이 되도록
@MainDiscountPolicy //직접 만든 애너테이션!! @Qualifier("mainDiscountPolicy") << 이렇게 적으면 오타가 나도 컴파일 오류가 안 생김
public class RateDiscountPolicy implements DiscountPolicy {

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
}

/*
코드에서 자주 사용하는 메인 데이터베이스의 커넥션을 획득하는 스프링 빈이 있고, 가끔 사용하는 서브 데이터베이스의 커넥션을 획득하는 스프링 빈이
있다고 생각해보자

-> 메인 데이터베이스의 커넥션을 획득하는 스프링 빈에는 @Primary를 적용해서 @Qualifier 없이 편리하게 조회
-> 서브 데이터베이스의 커넥션 빈을 획득할 때에는 @Qualifier를 지정해서 명시적으로 획득
물론 이때 메인 데이터베이스의 스프링 빈을 등록할 떄 @Qualifier를 지정해주는 것은 상관 X

@Primary는 기본값처럼 동작, @Qualifier는 매우 상세하게 동작
-> 스프링은 자동보다는 수동이, 넓은 범위의 선택권보다는 좁은 범위의 선택권이 우선 순위가 더 높음
-> @Qualifier가 우선 순위가 더 높다! 그래서 위 예시처럼 서브 스프링 빈의 명시적 획득이 가능한 것
*/