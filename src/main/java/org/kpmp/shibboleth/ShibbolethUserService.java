package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.packages.User;
import org.kpmp.packages.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ShibbolethUserService {

    private UserRepository userRepository;

    public ShibbolethUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(HttpServletRequest request, UTF8Encoder encoder) throws UnsupportedEncodingException {
        String value = handleNull(request.getHeader("mail"));
        String emailAddress = encoder.convertFromLatin1(value);
        value = handleNull(request.getHeader("displayname"));
        String displayName = encoder.convertFromLatin1(value);
        value = handleNull(request.getHeader("givenname"));
        String firstName = encoder.convertFromLatin1(value);
        value = handleNull(request.getHeader("sn"));
        String lastName = encoder.convertFromLatin1(value);

        User user = userRepository.findByEmailAddress(emailAddress);
        if (user == null) {
            user = new User();

        }
        return user;

    }

    private String handleNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
}
