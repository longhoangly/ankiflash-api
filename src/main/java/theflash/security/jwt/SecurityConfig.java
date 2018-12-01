package theflash.security.jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private AuthProvider authProvider;
  @Autowired private AuthEntryPoint entryPoint;

  @Value("${jwt.token.route}")
  private String tokenroute;

  @Value("${spring.anonymous.endpoint}")
  private String anonymousroute;

  @Value("${spring.based.endpoint}")
  private String basedroute;

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(authProvider));
  }

  @Bean
  public AuthFilter jwtAuthFilter() {
    List<String> pathsToSkip = Arrays.asList(tokenroute, anonymousroute);
    SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, basedroute);
    AuthFilter filter = new AuthFilter(matcher);
    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationFailureHandler(new AuthFailureHandler());
    filter.setAuthenticationSuccessHandler(new AuthSuccessHandler());
    return filter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests().antMatchers(tokenroute).permitAll()
        .and()
        .authorizeRequests().antMatchers(anonymousroute).permitAll()
        .and()
        .authorizeRequests().antMatchers(basedroute).authenticated()
        .and()
        .authorizeRequests().anyRequest().authenticated()
        .and()
        .exceptionHandling().authenticationEntryPoint(entryPoint)
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    http.headers().cacheControl();
  }
}
