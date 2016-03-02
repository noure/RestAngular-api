package fr.example.springboot.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.example.springboot.domain.User;
import fr.example.springboot.repository.UserRepository;
/**
 * 
 * @author Nour LABIHI
 *
 */
@Named
public class InitDB implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(InitDB.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (log.isInfoEnabled()) {
            log.info("importing data into database...");
        }

        Long usersCount = userRepository.count();
        if (usersCount == 0) {
            if (log.isDebugEnabled()) {
                log.debug("import users data into database...");
            }
            
            userRepository.save(
                new User().builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .name("Administrator")
                .role("ADMIN")
                .build()
            );
            
            userRepository.save(
                 new User().builder()
                .username("testuser")
                .password(passwordEncoder.encode("testuser"))
                .name("Test User")
                .role("USER")
                .build()
            );
            
            userRepository.save(
                    new User().builder()
                   .username("nour")
                   .password(passwordEncoder.encode("test++"))
                   .name("the developer")
                   .role("ADMIN")
                   .build()
               );

        }

    }

}
