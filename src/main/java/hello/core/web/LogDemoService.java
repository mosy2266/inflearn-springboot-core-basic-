package hello.core.web;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogDemoService {
    private final MyLogger myLogger;
    //private final ObjectProvider<MyLogger> myLoggerProvider; //프록시 방식을 쓰면 Provider를 사용하지 않아도 됨

    public void logic(String id) {
        //MyLogger myLogger = myLoggerProvider.getObject(); //프록시 방식을 쓰면 Provider를 사용하지 않아도 됨
        myLogger.log("service id = " + id);
    }
}

/*
request scope를 사용하지 않고 파라미터로 이 모든 정보를 서비스 계층에 넘기면 너무 많은 파라미터로 인해 코드가 지저분해짐
더 큰 문제는 requestURL 같은 웹과 관련된 정보가 웹과 관련없는 서비스 계층까지 넘어가게 된다는 것!
웹과 관련된 부분은 컨트롤러까지만 사용해야 함
-> 서비스 계층은 웹 기술에 종속되지 않고 가급적 순수하게 유지되는 것이 유지보수 관점에서 좋음

-> request scope의 MyLogger 덕분에 이런 부분을 파라미터로 넘기지 않고, MyLogger의 멤버 변수에 저장해서 코드와 계층을 깔끔하게 유지 가능
*/