package hello.core.web;

import hello.core.common.MyLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//MyLogger가 잘 작동하는지 확인하는 테스트용 컨트롤러

@Controller
@RequiredArgsConstructor
public class LogDemoController {
    private final LogDemoService logDemoService;
    private final MyLogger myLogger;
    // private final ObjectProvider<MyLogger> myLoggerProvider; //프록시 방식을 쓰면 Provider를 사용하지 않아도 됨

    //requestURL 값 = http://localhost:8080/log-demo
    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) { //HttpServletRequest를 통해 요청 URL을 받음
        String requestURL = request.getRequestURL().toString();

        System.out.println("myLogger = " + myLogger.getClass()); //주입된 MyLogger 확인

        //MyLogger myLogger = myLoggerProvider.getObject(); //프록시 방식을 쓰면 Provider를 사용하지 않아도 됨
        //받은 requestURL을 myLogger에 저장해둠 -> myLogger는 HTTP 요청 당 각각 구분되므로 다른 HTTP 요청 때문에 값이 섞일 걱정 X
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test"); //컨트롤러에서 controller test라는 로그를 남김
        logDemoService.logic("testId");
        return "OK";
    }
}

/*
<기대 실행 결과>
[d06b992f...] request scope bean created
[d06b992f...][http://localhost:8080/log-demo] controller test
[d06b992f...][http://localhost:8080/log-demo] service id = testId
[d06b992f...] request scope bean closed

<실제 실행 결과> : 어플리케이션 실행 시점에서 오류 발생
Error creating bean with name 'myLogger': Scope 'request' is not active for the current thread;
consider defining a scoped proxy for this bean if you intend to refer to it from a singleton

<이런 결과가 뜨는 이유>
스프링 어플리케이션을 실행하는 시점에서 싱글톤 빈은 생성해서 주입이 가능하지만, request 스코프 빈은 아직 생성되지 않음
왜냐면 request 스코프 빈은 실제 요청이 와야 생성할 수 있기 때문!!

<ObjectProvider 사용 뒤 실행 결과>
[0eb2a012-255f-428f-bfbb-864a679dca3e] request scope bean created : hello.core.common.MyLogger@32e2abb8
[0eb2a012-255f-428f-bfbb-864a679dca3e][http://localhost:8080/log-demo]controller test
[0eb2a012-255f-428f-bfbb-864a679dca3e][http://localhost:8080/log-demo]service id = testId
[0eb2a012-255f-428f-bfbb-864a679dca3e] request scope bean closed : hello.core.common.MyLogger@32e2abb8

ObjectProvider 덕분에 ObjectProvider.getObject()를 호출하는 시점까지 request scope 빈의 생성을 지연할 수 있음
ObjectProvider.getObject()를 호출하는 시점에는 HTTP 요청이 진행 중이므로 request scope 빈의 생성이 정상 처리됨

단, ObjectProvider.getObject()를 컨트롤러와 서비스에서 각각 한번씩 따로 호출해도 같은 HTTP 요청이면 같은 스프링 빈이 반환
-> 구분하기 힘들어진다!
-> 다른 방식은 없을까?

<프록시 방식 사용 뒤 실행 결과>
myLogger = class hello.core.common.MyLogger$$SpringCGLIB$$0
[d6a3da40-c27b-4118-a579-4ccb18c1295a] request scope bean created : hello.core.common.MyLogger@75cf8f7c
[d6a3da40-c27b-4118-a579-4ccb18c1295a][http://localhost:8080/log-demo]controller test
[d6a3da40-c27b-4118-a579-4ccb18c1295a][http://localhost:8080/log-demo]service id = testId
[d6a3da40-c27b-4118-a579-4ccb18c1295a] request scope bean closed : hello.core.common.MyLogger@75cf8f7c

@Scope에서 proxyMode = ScopedProxyMode.TARGET_CLASS를 설정하면 스프링 컨테이너가 CGLIB라는 바이트코드 조작 라이브러리를 통해
MyLogger를 상속받은 가짜 프록시 객체를 생성

-> 순수한 MyLogger 클래스가 아니라 MyLogger$$SpringCGLIB$$0이라는 클래스로 만들어진 객체가 등록된 것을 확인 가능
-> 스프링 컨테이너에 "myLogger"라는 이름으로 이 가짜 프록시 객체가 대신 등록되는 것!!

-> ac.getBean("myLogger", MyLogger.class)로 조회해도 프록시 객체가 조회되며, 의존관계 주입도 이 가짜 프록시 객체가 주입됨

가짜 프록시 객체에는 요청이 오면 그때 내부에서 진짜 빈을 요청하는 위임 로직이 들어있음
클라이언트가 myLogger.log()을 호출하면 사실은 가짜 프록시 객체의 메서드를 호출한 것
이때 가짜 프록시 객체는 request scope의 진짜 myLogger.log()를 호출

가짜 프록시 객체는 원본 클래스를 상속 받아 만들어졌기 때문에 이 객체를 사용하는 클라이언트 입장에서는
원본인지 아닌지도 모르게 동일하게 사용 가능(다형성!)

<프록시 방식의 동작 정리>
1. CGLIB이라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어 주입
2. 이 가짜 프록시 객체는 실제 요청이 오면 그때 내부에서 진짜 빈을 요청
3. 가짜 프록시 객체는 실제 request scope와는 무관함 -> 그냥 가짜이고, 내부에 단순 위임 로직만 가지며, 싱글톤'처럼' 동작

<프록시 방식의 특징 정리>
1. 프록시 객체 덕분에 클라이언트는 싱글톤 빈을 사용하듯 편리하게 request scope를 사용할 수 있음
2. 핵심 아이디어는 진짜 객체 조회를 꼭 필요한 시점까지 지연 처리한다는 것!! (해당 특징은 Provider를 사용할 때도 동일)
3. 프록시 방식의 경우 애너테이션 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있다는 점!
    -> 클라이언트 코드에 변화가 없음!
    -> 다형성과 DI 컨테이너가 가진 큰 장점
4. 꼭 웹 스코프가 아니어도 프록시 방식을 사용할 수 있음

<주의할 점>
1. 마치 싱글톤을 사용하는 것 같지만 다르게 동작하므로 주의해서 사용해야 함
2. 이런 특별한 scope는 꼭 필요한 곳에만 최소화해서 사용할 것! -> 무분별하게 사용하면 유지보수가 어려워짐
*/