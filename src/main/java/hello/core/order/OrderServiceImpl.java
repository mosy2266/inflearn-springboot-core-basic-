package hello.core.order;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component  //컴포넌트 스캔의 대상이 되도록
//@RequiredArgsConstructor //Lombok 라이브러리 -> @RequiredArgsConstructor : final이 붙은 필드를 모아서 자동으로 생성자를 만들어줌
public class OrderServiceImpl implements OrderService {

    /*
    정액 할인 정책 -> 정률 할인 정책으로 변경할 소요가 발생

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    //private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); //할인 정책을 변경했음

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    위 코드는 DIP를 위반
    -> 추상(인터페이스)인 DiscountPolicy에도 의존하고 있으며 구체(구현) 클래스인 FixDiscountPolicy,
    RateDiscountPolicy에도 의존하고 있음

    그래서 FixDiscountPolicy를 RateDiscountPolicy로 변경하는 순간, 이 OrderServiceImpl의 코드도 변경해야 한다!!
    -> OCP 또한 위반한다고 볼 수 있다

    그러면 어떻게 해야 하나?
    -> 인터페이스에만 의존하도록 의존관계를 변경해주면 된다
    */

    //인터페이스에만 의존하도록 설계와 코드를 변경
    private final MemberRepository memberRepository;
    //@Autowired //DiscountPolicy의 하위 타입 둘 다 스프링 빈으로 선언된 상태에서 의존관계 자동 주입을 설정하면?
    //NoUniqueBeanDefinitionException 오류가 발생!! -> 이때 해결 방법은?
    /*
    @Autowired는 타입 매칭을 시도하고, 이때 빈이 여러 개면 필드 명, 파라미터 명으로 추가 매칭한다 -> 필드 명을 빈 이름으로 변경해준다
    private final DiscountPolicy rateDiscountPolicy;
    */
    private final DiscountPolicy discountPolicy; //구현체가 없다!! -> 이 상태에서는 NPE(Null Pointer Exception)이 발생
    //이 문제를 해결하려면 누군가가 클라이언트인 OrderServiceImpl에 DiscountPolicy의 구현 객체를 대신 생성하고 주입해줘야 함!

    //@Autowired //의존관계를 자동으로 주입해줌 -> 단, 생성자가 딱 하나만 있으면 @Autowired를 생략해도 자동 주입됨(스프링 빈에만 해당)
    /* @RequiredArgsConstructor를 사용한 뒤 실제 class를 열어보면(ctrl+F12) 해당 생성자가 추가되어 있는 것을 확인할 수 있다
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    */
    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    //테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}

//위 코드로 이제 OrderServiceImpl은 더이상 FixDiscountPolicy를 의존하지 않고 단지 DiscountPolicy에만 의존함
//OrderServiceImpl에는 MemoryMemberRepository, FixDiscountPolicy 객체의 의존관계가 주입됨(AppConfig 코드 참고)