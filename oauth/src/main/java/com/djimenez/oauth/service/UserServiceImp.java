package com.djimenez.oauth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.djimenez.commons.users.models.entity.User;
import com.djimenez.oauth.clients.UserFeignClient;

@Service
public class UserServiceImp implements UserDetailsService{

	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);

	
	@Autowired
	private UserFeignClient client;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = client.findByUsername(username);
		if(user==null) {
			log.info("Error in the login, user '" + username + "' not found in the system");
			throw new UsernameNotFoundException("Error in the login, user '" + username + "' not found in the system");
		}
		List<GrantedAuthority> authohrities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.peek(authority -> log.info("Role: " + authority.getAuthority()))
				.collect(Collectors.toList());
		log.info("User authenticated: " + username);
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, authohrities);
	}

}
