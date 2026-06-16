package com.example.mbminicustomer.Services;


import com.example.mbminicustomer.*;
import com.example.mbminicustomer.ConfigsRepo.CustomerRepo;
import com.example.mbminicustomer.ConfigsRepo.SessionRepo;
import com.example.mbminicustomer.Entities.AuthSession;
import com.example.mbminicustomer.Entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerService {

    private AuthKeyGenerator authKeyGenerator;

    private RefreshTokenGenerator refreshTokenGenerator;

    private final CustomerRepo customerRepo;

    @Autowired
    private CustomerRedisService customerRedisService;

    private final SessionRepo sessionRepo;

    public CustomerService(AuthKeyGenerator authKeyGenerator, CustomerRepo customerRepo, SessionRepo sessionRepo,RefreshTokenGenerator refreshTokenGenerator) {
        this.authKeyGenerator = authKeyGenerator;
        this.customerRepo = customerRepo;
        this.sessionRepo = sessionRepo;
        this.refreshTokenGenerator=refreshTokenGenerator;
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
            AuditorAwareImpl.setCurrentUser(String.valueOf(customer.getId()));

            String authKey=authKeyGenerator.generate();
            String refreshToken= refreshTokenGenerator.generate();

            AuthSession authSession= new AuthSession(customer.getId(),authKey,refreshToken);
            sessionRepo.save(authSession);
            customerRedisService.sessionInRedis(authSession);
            AuditorAwareImpl.clear();
            return "Logging In ------- Started Auth Session " + "AuthKey: " + authKey + " | RefreshToken: " + refreshToken;
        }
        else {
            throw new RuntimeException("Password didnt match");
        }

    }

    public String Register(Customer customer){
        Customer saved= customerRepo.save(customer);

        saved.setCreatedBy(String.valueOf(saved.getId()));

        AuditorAwareImpl.setCurrentUser(String.valueOf(saved.getId()));

        Customer audited = customerRepo.save(saved);

        customerRedisService.addInRedis(audited,audited.getId().toString());

        String key= String.valueOf(audited.getId());

        String authKey=authKeyGenerator.generate();
        String refreshToken = refreshTokenGenerator.generate();

        AuthSession authSession= new AuthSession(audited.getId(),authKey,refreshToken);

        sessionRepo.save(authSession);

        customerRedisService.addInRedis(authSession,authSession.getId().toString());

        return "Saved with the Id"+ key +" and Auth Session Started";
    }

    public String update(Long CustomerId, String newName,String newEmail, Long newHouseNo, String newLocality, String newCity, Long newPincode ){
        Customer customer=customerRepo.getCustomerById(CustomerId);
        customerRedisService.updateInRedis(CustomerId,newName,newEmail,newHouseNo,newLocality,newCity,newPincode);
        if (newName !=null){
            customer.setName(newName);
        }
        if (newEmail != null){
            customer.setEmail(newEmail);
        }
        if (newHouseNo != null){
            customer.setHouseNo(newHouseNo);
        }
        if (newLocality != null){
            customer.setLocality(newLocality);
        }
        if (newCity != null){
            customer.setCity(newCity);
        }
        if (newPincode != null){
            customer.setPincode(newPincode);
        }
        customerRepo.save(customer);

        return "Updated Successfully";
    }


    public String passwordUpdate(Long CustomerId,String newPass){
        Customer customer=customerRepo.getCustomerById(CustomerId);
        customerRedisService.updatePassInRedis(CustomerId,newPass);
        customer.setPassword(newPass);
        customerRepo.save(customer);
        return "Password Updated";
    }

    public String logout(String authKey){

        AuthSession session=sessionRepo.findByAuthKey(authKey).orElseThrow(() ->new RuntimeException("Session not found"));

        customerRedisService.deleteSessionInRedis(session);
        sessionRepo.delete(session);

        return "Logged out and session ended";

    }


    public String refresh(String refreshToken){
        AuthSession oldsession= sessionRepo.findByRefreshToken(refreshToken).orElseThrow(() ->new RuntimeException("Session not found"));
        if (oldsession.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh token expired, please log in again");
        }

        customerRedisService.deleteSessionInRedis(oldsession);
        sessionRepo.delete(oldsession);

        String newAuthKey= authKeyGenerator.generate();
        String newRefreshToken=refreshTokenGenerator.generate();
        AuthSession newSession=new AuthSession(oldsession.getUserId(), newAuthKey,newRefreshToken);
        sessionRepo.save(newSession);
        customerRedisService.sessionInRedis(newSession);

        return "session refreshed with "+ "authKey "+ newAuthKey + " refreshToken "+ newRefreshToken;


    }







}
