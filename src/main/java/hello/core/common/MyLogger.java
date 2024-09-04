package hello.core.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

//로그를 출력하기 위한 MyLogger 클래스

@Component
//request 스코프로 지정 -> 해당 빈은 HTTP 요청 당 하나씩 생성되고, HTTP 요청이 끝나는 시점에 소멸됨
//proxyMode = ScopedProxyMode.TARGET_CLASS를 추가 -> 적용 대상이 인터페이스가 아닌 클래스
//만약 적용 대상이 인터페이스라면 TARGET_CLASS를 INTERFACES로 바꿔준다
//MyLogger의 가짜 프록시 클래스를 만들어 두고 HTTP request와 상관없이 가짜 프록시 클래스를 다른 빈에 미리 주입해둘 수 있음!!
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS) //프록시 방식 사용
public class MyLogger {
    private String uuid;
    private String requestURL;

    //requestURL은 빈이 생성되는 시점에는 알 수 없으므로 외부에서 setter로 입력 받음
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.println("[" + uuid + "]" + "[" + requestURL + "]" + message);
    }

    //빈이 생성되는 시점에 자동으로 @PostConstruct 초기화 메서드를 통해 uuid를 생성, 저장해둠
    //이 빈은 HTTP 요청 당 하나씩 생성되므로 uuid를 저장해두면 다른 HTTP 요청과 구분 가능
    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString();
        System.out.println("[" + uuid + "] request scope bean created : " + this);
    }

    //이 빈이 소멸되는 시점에 @PreDestroy를 사용, 종료 메시지를 남김
    @PreDestroy
    public void close() {
        System.out.println("[" + uuid + "] request scope bean closed : " + this);
    }
}