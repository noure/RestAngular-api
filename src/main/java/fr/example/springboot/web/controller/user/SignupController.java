package fr.example.springboot.web.controller.user;

import fr.example.springboot.Constants;
import fr.example.springboot.exception.InvalidRequestException;
import fr.example.springboot.web.dto.SignupForm;
import fr.example.springboot.web.dto.UserDetails;
import fr.example.springboot.service.UserService;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Nour LABIHI
 */
@RequestMapping(value = Constants.URI_API_PREFIX)
@RestController
public class SignupController {

    private static final Logger log = LoggerFactory.getLogger(SignupController.class);

    @Inject
    private UserService userService;

    @RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> signup(
            @RequestBody @Valid SignupForm form,
            BindingResult errors,
            HttpServletRequest req) {
        log.debug("signup data@" + form);

        if (errors.hasErrors()) {
            throw new InvalidRequestException(errors);
        }

        UserDetails saved = userService.registerUser(form);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                ServletUriComponentsBuilder
                .fromContextPath(req)
                .path(Constants.URI_API_PREFIX + Constants.URI_USERS + "/{id}")
                .buildAndExpand(saved.getId()).toUri()
        );

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
