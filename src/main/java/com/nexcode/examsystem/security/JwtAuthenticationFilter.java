package com.nexcode.examsystem.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    
    public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

    private String getJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.replace("Bearer ", "");
		}
		return null;
	}
    
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
		String jwt=getJwt(request);
		if(jwt!=null && jwtService.validate(jwt)) 
		{
			
			Claims claims = jwtService.getClaims(jwt);
			Long cid = Long.parseLong(claims.getId());
            String email = claims.getSubject();
            String username = claims.get("username", String.class);
            String roles = claims.get("roles", String.class);

            List<String> stringArr = Arrays.asList(roles.split(","));
            List<GrantedAuthority> authorities = stringArr.stream().map(r -> new SimpleGrantedAuthority(r))
                    .collect(Collectors.toList());
            UserPrincipal userPrincipal = new UserPrincipal(cid, username, email, null, authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
    }
}
