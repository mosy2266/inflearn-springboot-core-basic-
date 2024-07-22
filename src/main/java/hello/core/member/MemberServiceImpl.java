package hello.core.member;

//회원 서비스 구현체
public class MemberServiceImpl implements MemberService {

    //private final MemberRepository memberRepository = new MemoryMemberRepository();

    //AppConfig를 통한 생성자 주입을 위해 설계 및 코드를 변경
    //MemberServiceImpl은 더이상 MemoryMemberRepository를 의존하지 않음
    private final MemberRepository memberRepository; //오직 MemberRepository 인터페이스에만 의존!

    //MemberServiceImpl 입장에서 생성자를 통해 어떤 구현 객체가 들어올지(주입될지)는 알 수 없음
    //어떤 구현 객체를 주입할지는 오직 외부(AppConfig)에서 결정한다!
    //의존관계에 대한 고민은 외부(AppConfig)에 온전히 떠넘겨지고, MemberServiceImpl은 실행에만 집중할 수 있게 되었음
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void join(Member member) {
        memberRepository.save(member);
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    //테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
