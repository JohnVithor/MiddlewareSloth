import sloth.basic.Sloth;
import sloth.basic.annotations.Get;
import sloth.basic.annotations.RequestMapping;
import sloth.basic.annotations.Param;

public class Main {

    @RequestMapping(router = "test")
    static class test {

        @Get(router = "123")
        public String testando(@Param(name = "test", required = false) String test, int test2) {
            return "123";
        }
    }


    public static void main(String[] args) {
        Sloth sloth = new Sloth();
        sloth.register(new test());
//        sloth.init(8080);
    }
}