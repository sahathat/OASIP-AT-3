package sit.int221.at3.dtos.user;

public class JwtResponse {
    private String token;
    private String email;
    private String role;

    public JwtResponse()
    {

    }

    public JwtResponse(String token, String email, String role) {
        super();
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
