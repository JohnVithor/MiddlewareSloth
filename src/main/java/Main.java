import sloth.basic.Sloth;
import sloth.basic.annotations.Get;
import sloth.basic.annotations.RequestMapping;
import sloth.basic.annotations.Param;

public class Main {

    @RequestMapping(router = "test")
    public static class test {

        @Get(router = "123")
        public String testando(@Param(name = "test") Integer test, @Param(name = "asd") String asd) {
            return asd + test;
        }
    }


    public static void main(String[] args) {
        Sloth sloth = new Sloth();
        sloth.register(new test());
        sloth.init(8080);
    }
}