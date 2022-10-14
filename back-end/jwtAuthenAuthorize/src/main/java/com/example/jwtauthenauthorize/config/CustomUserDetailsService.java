package com.example.jwtauthenauthorize.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<SimpleGrantedAuthority> roles = null;
		if(username.equals("admin"))
		{
			roles=Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
			return new User("admin","$argon2d$v=19$m=4096,t=10,p=2$bnM2ZWVwd3FmdGgwMDAwMA$QG9Uz6lkTOFZkL3IXXwOOlqdlviXWs6c4cln",roles);
		}
		if(username.equals("user"))
		{
			roles=Arrays.asList(new SimpleGrantedAuthority("ROLE_STUDENT"));
			return new User("user","$argon2d$v=19$m=4096,t=10,p=2$azRwOXBjZG05MGYwMDAwMA$xsw094EhvPrFQl5hNefdU9/uZ9La3r6zDL3b",roles);
		}
		throw new UsernameNotFoundException("User not found with the name "+ username);
	}

}
