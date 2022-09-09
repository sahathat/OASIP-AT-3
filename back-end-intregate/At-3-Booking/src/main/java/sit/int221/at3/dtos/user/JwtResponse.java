package sit.int221.at3.dtos.user;

public class JwtResponse {
    private String token;

    public JwtResponse()
    {

    }

    public JwtResponse(String token) {
        super();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
