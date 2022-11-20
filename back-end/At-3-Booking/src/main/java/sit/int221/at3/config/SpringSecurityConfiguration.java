package sit.int221.at3.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sit.int221.at3.filter.CustomJwtAuthenticationFilter;
import sit.int221.at3.services.UserService;
import sit.int221.at3.utils.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter{

    @Autowired
    private UserService userService;

    @Autowired
    private CustomJwtAuthenticationFilter customJwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public Argon2PasswordEncoder passwordEncoder(){
        return new Argon2PasswordEncoder(16,27,2,4096,10);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET,"/api/events/**").hasAnyRole("admin","student","lecturer")
        .antMatchers(HttpMethod.POST,"/api/events/**").hasAnyRole("admin","student")
        .antMatchers(HttpMethod.PUT,"/api/events/**").hasAnyRole("admin","student")
        .antMatchers(HttpMethod.DELETE,"/api/events/**").hasAnyRole("admin","student")
        .antMatchers("/api/userList/**").hasRole("admin")
        .antMatchers("/api/mappings/**").hasAnyRole("lecturer","admin")
        .antMatchers("/api/guests/**").permitAll()
        .antMatchers("/api/files/**").permitAll()
        .antMatchers("/api/users/signin","/api/users/signup","/api/users/refresh","/api/users/confirm","/api/users/get_token","/api/users/reset_password","/api/users/verify", "/api/users/confirm_reset_password").permitAll()
        .anyRequest().authenticated()
        .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).
        and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
        and().addFilterBefore(customJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

