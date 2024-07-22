package hello.core.singleton;

public class StatefulService {

    private int price; //상태를 유지하는(stateful) 필드

    /*
    public void order(String name, int price) {
       System.out.println("name = " + name + " price = " + price);
       this.price = price; //여기가 문제가 된다!!
    }

    public int getPrice() {
        return price;
    }
    */

    //아래 코드로 변경해서 무상태를 실현해주자
    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        //this.price = price; //여기가 문제가 된다!!
        return price;
    }

}
