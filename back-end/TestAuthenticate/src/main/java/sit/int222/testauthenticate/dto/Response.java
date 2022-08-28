package sit.int222.testauthenticate.dto;

public class Response {
    private final String jwt;

    public Response(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
