package com.example.mbminicustomer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private AuthKeyGenerator authKeyGenerator;

    private final CustomerRepo customerRepo;

    @Autowired
    private RedisService redisService;

    private final SessionRepo sessionRepo;

    public CustomerService(AuthKeyGenerator authKeyGenerator, CustomerRepo customerRepo, SessionRepo sessionRepo) {
        this.authKeyGenerator = authKeyGenerator;
        this.customerRepo = customerRepo;
        this.sessionRepo = sessionRepo;
    }


    public String generateLogin(String PhoneNo){
        boolean res= customerRepo.existsByPhoneNo(PhoneNo);
        if (res==true){
            return "Phone no. found";
        }
        else {
            throw new RuntimeException("User not found with "+ PhoneNo.toString());
        }
    }

    public String login(String PhoneNo,String Password){
        Customer customer= customerRepo.findByPhoneNo(PhoneNo);

        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        if (encoder.matches(Password, customer.getPassword())){
            String authKey=authKeyGenerator.generate();
            AuthSession authSession= new AuthSession(customer.getId(),authKey);
            sessionRepo.save(authSession);
            redisService.sessionInRedis(authSession);
            return "Logging In ------- Started Auth Session";
        }
        else {
            throw new RuntimeException("Password didnt match");
        }

    }

    public String Register(Customer customer){
        Customer saved= customerRepo.save(customer);
        redisService.Register(saved);
        String key= String.valueOf(saved.getId());
        String authKey=authKeyGenerator.generate();
        AuthSession authSession= new AuthSession(saved.getId(),authKey);
        sessionRepo.save(authSession);
        redisService.sessionInRedis(authSession);
        return "Saved with the Id"+ key +" and Auth Session Started";
    }







}
