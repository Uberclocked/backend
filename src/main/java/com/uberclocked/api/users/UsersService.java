package com.uberclocked.api.users;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    public User create(Jwt jwt) {
        String auth0Id = jwt.getSubject();
        User user = usersRepository.findByAuth0Id(auth0Id).orElse(null);
        if(user != null){
            return user;
        }
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        User newUser = new User(auth0Id, email, name);
        newUser.setLastLogin(LocalDateTime.now());
        return usersRepository.save(newUser);
    }
}
