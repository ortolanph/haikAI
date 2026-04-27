package pt.pauloortolan.haikai.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final String userId = UUID.randomUUID().toString();

    public String getCustomerId() {
        return userId;
    }

}
