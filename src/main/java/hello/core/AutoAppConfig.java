package hello.core;


import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
//@Configuration이 붙은 설정 정보를 컴포넌트 스캔 대상에서 제외(기존 예제 코드 때문에)
@ComponentScan(
        basePackages = "hello.core", //탐색 시작 위치(패키지) 설정, 요즘은 설정 정보 클래스의 위치를 프로젝트 최상단에 두는 편이 좋다
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
public class AutoAppConfig {

    /*

    이렇게 수동 빈 등록과 자동 빈 등록에서 빈 이름이 충돌(동일한 빈 이름)하면 수동 빈 등록이 우선권을 가진다.
    (수동 빈이 자동 빈을 오버라이딩 한다)
    @Bean(name = "memoryMemberRepository")
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    단, 최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌하면 오류가 발생하도록 기본 설정이 바뀌었다.

    스프링 부트 에러 메시지
    Consider renaming one of the beans or enabling overriding by setting
    spring.main.allow-bean-definition-overriding=true
    저게 기본값이 false로 되어 있기 때문에 application.properties에 저걸 등록해서 true로 설정해주면 오류가 발생하지 않는다!
    */
}

//컴포넌트 스캔 : @Component 애너테이션이 붙은 클래스를 스캔해서 스프링 빈으로 등록