package com.github.piotrostrow.chess.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.thymeleaf.util.StringUtils.isEmpty;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		Optional<String> token = getToken(request);

		token.flatMap(jwtUtil::getAuthentication)
				.ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));

		chain.doFilter(request, response);
	}

	private Optional<String> getToken(HttpServletRequest request) {
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(isEmpty(header) || !header.startsWith("Bearer "))
			return Optional.empty();

		return Optional.of(header.substring(7).trim());
	}
}
