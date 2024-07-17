package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;

//회원 도메인 - 회원 가입 기능(main)
public class MemberApp {

    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find Member = " + findMember.getName());
    }
}

//단, 어플리케이션 로직으로 이렇게 테스트하는 것은 좋지 않으므로 JUnit 테스트를 사용한다.
//MemberService의 테스트 코드를 작성해보자!
