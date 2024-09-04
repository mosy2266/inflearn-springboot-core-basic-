package hello.core.scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);

        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUserPrototype() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBean {
        //private final PrototypeBean prototypeBean; //생성시점에 주입 -> 우리는 이런 걸 원하지 않음! 사용할 때마다 새로 생성하고 싶다

        /*
        @Autowired
        public ClientBean(PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }
        */

        /*
        @Autowired
        private ObjectProvider<PrototypeBean> prototypeBeanProvider; //ObjectProvider를 사용! (단 얘는 스프링에 의존)
        */

        @Autowired
        private Provider<PrototypeBean> provider; //Provider를 사용 -> 자바 표준이므로 스프링이 아닌 다른 컨테이너에서도 사용 가능

        public int logic() {
            //ObjectProvider의 getObject()를 호출 -> 내부에서 스프링 컨테이너를 통해 해당 빈을 찾아서 반환해줌(DL 정도의 기능만 제공)
            //prototypeBeanProvider.getObject()를 통해 항상 새로운 프로토타입 빈이 생성됨
            //PrototypeBean prototypeBean = prototypeBeanProvider.getObject();

            //provider.get()을 통해 항상 새로운 프로토타입 빈이 생성됨
            PrototypeBean prototypeBean = provider.get(); //Provider의 get()은 ObjectProvider의 getObject()와 동일한 기능
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init" + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
