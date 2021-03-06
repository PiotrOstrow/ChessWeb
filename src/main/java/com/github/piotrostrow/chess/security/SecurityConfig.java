package com.github.piotrostrow.chess.security;

import com.github.piotrostrow.chess.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

import static com.github.piotrostrow.chess.security.Role.ADMIN;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final JwtFilter jwtFilter;
	private final PasswordEncoder passwordEncoder;

	public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtFilter = jwtFilter;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/", "/resources/**", "/js/**", "/css/**", "/favicon.ico");
	}

	@Bean
	public DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler() {
		DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
		defaultWebSecurityExpressionHandler.setDefaultRolePrefix("");
		return defaultWebSecurityExpressionHandler;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors().and().csrf().disable()

				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()

				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

				.exceptionHandling()
				.authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()))
				.and()

				.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/users/").permitAll()
				.antMatchers(HttpMethod.GET, "/users/{name}/").authenticated()
				.antMatchers(HttpMethod.GET, "/users/").hasAuthority(ADMIN.toString())
				.antMatchers(HttpMethod.DELETE, "/users/{name}/").hasAuthority(ADMIN.toString())

				.antMatchers(HttpMethod.POST, "/auth/login/").permitAll()
				.antMatchers(HttpMethod.POST, "/auth/refresh/").permitAll()
				.antMatchers(HttpMethod.POST, "/auth/logout/").permitAll()

				.antMatchers("/websocket/**").permitAll() // auth at sub-protocol due to JS WebSocket API limitations

				.antMatchers("/puzzles/random/").authenticated()
				.antMatchers("/puzzles/solve/").authenticated()
				.antMatchers("/puzzles/{\\d}/").hasAuthority(ADMIN.toString())
				.antMatchers("/puzzles/").hasAuthority(ADMIN.toString())

				.anyRequest().authenticated();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return this.passwordEncoder;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
