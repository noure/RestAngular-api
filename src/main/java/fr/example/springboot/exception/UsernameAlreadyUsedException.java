
package fr.example.springboot.exception;

/**
 *
 * @author Nour LABIHI
 */
public class UsernameAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String username;

    public UsernameAlreadyUsedException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    
}
