package hello.core.member;

//assertThat은 jupiter가 아닌 core에 있다...
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//멤버 서비스 테스트 코드
class MemberServiceTest {

    MemberService memberService = new MemberServiceImpl();

    @Test
    void join() {
        //given
        Member member = new Member(1L, "member A", Grade.VIP);

        //when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        //then
        Assertions.assertThat(member).isEqualTo(findMember);
    }
}

/*이 코드의 설계상 문제점이 무엇인가?
* 다른 저장소로 변경할 때 OCP 원칙을 잘 준수하는가?
* DIP를 잘 지키고 있는가?
* -> 의존관계가 인터페이스뿐만 아니라 구현까지 모두 의존한다!!*/