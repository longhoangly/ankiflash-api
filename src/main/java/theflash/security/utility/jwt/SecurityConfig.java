package theflash.security.utility.jwt;

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

  @Autowired
  private AuthProvider authProvider;

  @Autowired
  private AuthEntryPoint entryPoint;

  @Value("${jwt.token.endpoint}")
  private String tokenEndpoint;

  @Value("${spring.anonymous.endpoint}")
  private String anonymousEndpoint;

  @Value("${spring.based.endpoint}")
  private String basedEndpoint;

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(authProvider));
  }

  @Bean
  public AuthFilter jwtAuthFilter() {
    List<String> pathsToSkip = Arrays.asList(tokenEndpoint, anonymousEndpoint);
    SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, basedEndpoint);
    AuthFilter filter = new AuthFilter(matcher);
    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationFailureHandler(new AuthFailureHandler());
    filter.setAuthenticationSuccessHandler(new AuthSuccessHandler());
    return filter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors();
    http.csrf().disable()
        .authorizeRequests().antMatchers(tokenEndpoint).permitAll()
        .and()
        .authorizeRequests().antMatchers(anonymousEndpoint).permitAll()
        .and()
        .authorizeRequests().antMatchers(basedEndpoint).authenticated()
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
