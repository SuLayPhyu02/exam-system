package com.nexcode.examsystem.service.impl;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.nexcode.examsystem.model.entities.User;
import com.nexcode.examsystem.model.exception.NotFoundException;
import com.nexcode.examsystem.model.exception.UnauthorizedException;
import com.nexcode.examsystem.model.requests.LoginRequest;
import com.nexcode.examsystem.model.responses.JwtResponse;
import com.nexcode.examsystem.repository.UserRepository;
import com.nexcode.examsystem.security.JwtService;
import com.nexcode.examsystem.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	
	public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService,
			UserRepository userRepository) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}
	@Override
	public JwtResponse authenticate(LoginRequest loginRequest) {
		
		Date expiredAt = new Date((new Date()).getTime() + 86400 * 1000);
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		User foundedUser = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(()->new NotFoundException("User not found"));
		boolean isFirstTime=foundedUser.isPasswordChanged();
		String username=foundedUser.getUsername();
		userRepository.save(foundedUser);
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
			
			String jwt = jwtService.createJwtToken(authentication);
			return new JwtResponse(username,jwt, expiredAt.toInstant().toString(),isFirstTime);
		}
		throw new UnauthorizedException("Email or Password is wrong!");	
	}
}
