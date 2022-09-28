import sloth.basic.Sloth;
import sloth.basic.annotations.Body;
import sloth.basic.annotations.MethodMapping;
import sloth.basic.annotations.RequestMapping;
import sloth.basic.annotations.Param;
import sloth.basic.http.MethodHTTP;
import sloth.basic.util.ContentType;

public class Main {

    @RequestMapping(path = "teste")
    public static class test {

        public class testBody {
            public String field;

            public testBody(String field) {
                this.field = field;
            }

            public void setField(String field) {
                this.field = field;
            }

            public String getField() {
                return field;
            }
        }

        @MethodMapping(method = MethodHTTP.GET, content_type = ContentType.HTML)
        public Integer testando(@Param(name = "p1") Integer test) {
            return test+test;
        }

        @MethodMapping(method = MethodHTTP.POST, content_type = ContentType.JSON)
        public testBody testando(@Body String asd) {
            return new testBody(asd);
        }
    }


    public static void main(String[] args) {
        Sloth sloth = new Sloth();
        sloth.register(new test());
        sloth.init(8080);
    }
}