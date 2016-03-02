package fr.example.springboot;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.inject.Inject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Predicate;

import fr.example.springboot.domain.User;
import fr.example.springboot.repository.UserRepository;
import fr.example.springboot.security.SecurityUtil;
import fr.example.springboot.security.UserDetailsServiceImpl;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Nour Labihi
 *
 */

@SpringBootApplication
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EntityScan(basePackageClasses = {User.class, Jsr310JpaConverters.class})
@EnableSpringDataWebSupport()
@EnableJpaAuditing(auditorAwareRef = "auditor")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    /**
     * renvoie l'utilisateur courant
     * @return
     */
    @Bean
    public AuditorAware<User> auditor() {
        return () -> SecurityUtil.currentUser();
    }
    
    /**
     * configure le mapper builder de Jackson
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {

        Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
        mapperBuilder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapperBuilder.featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapperBuilder.featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        return mapperBuilder;
    }

    /**
     * Congifuration de documentation d'API
     * 
     * @author Nour LABIHI
     */
    @Configuration
    @EnableSwagger2
    @Profile(value = {"dev", "test", "staging"})
    public static class SwaggerConfig {

        @Bean
        public Docket userApi() {
            AuthorizationScope[] authScopes = new AuthorizationScope[1];
            authScopes[0] = new AuthorizationScopeBuilder()
                    .scope("read")
                    .description("read access")
                    .build();
            SecurityReference securityReference = SecurityReference.builder()
                    .reference("test")
                    .scopes(authScopes)
                    .build();

            ArrayList<SecurityContext> securityContexts = newArrayList(
                    SecurityContext
                    .builder()
                    .securityReferences(newArrayList(securityReference))
                    .build()
            );
            return new Docket(DocumentationType.SWAGGER_2)
                    .directModelSubstitute(LocalDateTime.class, String.class)
                    .ignoredParameterTypes(User.class)
                    .securitySchemes(newArrayList(new BasicAuth("test")))
                    .securityContexts(securityContexts)
                    .apiInfo(apiInfo())
                    .select()
                    .paths(apiPaths())
                    .build();
        }

        @SuppressWarnings("unchecked")
		private Predicate<String> apiPaths() {
            return or(
                    regex("/api/.*")
            );
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("Exemple de template en utilisant AngularJs et Spring")
                    .description("documentation d'api ")
                    .termsOfServiceUrl("www.github.com/noure")
                    .contact("Nour LABIHI")
                    .license("Apache License Version 2.0")
                    .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                    .version("2.0")
                    .build();
        }

    }
    
    
    /**
     * Configuration de Spring Security
     * 
     * @author Nour LABIHI
     */

    @Configuration
    @Order(-10)
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Inject
        private UserRepository userRepository;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/**/*.html", //
                            "/css/**", //
                            "/js/**", //
                            "/i18n/**",// 
                            "/libs/**",//
                            "/img/**", //
                            "/webjars/**",//
                            "/ico/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    .antMatchers("/api/signup", "/api/users/username-check")
                    .permitAll()
                    .and()
                        .authorizeRequests()
                        .regexMatchers(HttpMethod.GET, "^/api/users/[\\d]*(\\/)?$").authenticated()
                        .regexMatchers(HttpMethod.GET, "^/api/users(\\/)?(\\?.+)?$").hasRole("ADMIN")
                        .regexMatchers(HttpMethod.DELETE, "^/api/users/[\\d]*(\\/)?$").hasRole("ADMIN")
                        .regexMatchers(HttpMethod.POST, "^/api/users(\\/)?$").hasRole("ADMIN")
                    .and()
                        .authorizeRequests()
                        .antMatchers("/api/**").authenticated()
                    .and()
                        .authorizeRequests()
                        .anyRequest().permitAll()
                    .and()
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                        .httpBasic()
                    .and()
                        .csrf().disable();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
                authenticationManagerBuilder.userDetailsService(new UserDetailsServiceImpl(userRepository))
                    .passwordEncoder(passwordEncoder());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        @Override
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder;
        }

    }
}

