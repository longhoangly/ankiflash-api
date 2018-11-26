package theflash.security.authentication;

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
public class JwtConfig extends WebSecurityConfigurerAdapter {

  @Autowired private JwtAuthProvider authProvider;
  @Autowired private JwtAuthEntryPoint entryPoint;

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
  public JwtAuthFilter jwtAuthFilter() {
    List<String> pathsToSkip = Arrays.asList(tokenroute, anonymousroute);
    SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, basedroute);
    JwtAuthFilter filter = new JwtAuthFilter(matcher);
    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationFailureHandler(new JwtAuthFailureHandler());
    filter.setAuthenticationSuccessHandler(new JwtAuthSuccessHandler());
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
