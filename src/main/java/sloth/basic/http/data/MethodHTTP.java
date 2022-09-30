package sloth.basic.http.data;

public enum MethodHTTP {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    final String value;

    MethodHTTP(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
