package fet.datn.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;


@Service
public class TokenService {

    public String generateOtpReferenceId() {
        return UUID.randomUUID().toString();
    }

    public String generateCode(String validCharacters, int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(validCharacters.length());
            builder.append(validCharacters.charAt(index));
        }
        return builder.toString();
    }

}
