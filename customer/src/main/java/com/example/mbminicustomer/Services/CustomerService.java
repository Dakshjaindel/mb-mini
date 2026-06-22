package com.example.mbminicustomer.Services;


import com.example.mbminicustomer.*;
import com.example.mbminicustomer.ConfigsRepo.CustomerRepo;
import com.example.mbminicustomer.ConfigsRepo.NetCreditRepo;
import com.example.mbminicustomer.ConfigsRepo.SessionRepo;
import com.example.mbminicustomer.Entities.CustomerNetCredit;
import com.example.mbminiframework.Entity.AuthSession;
import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CustomerService {

    @Autowired
    private AuthKeyGenerator authKeyGenerator;
    @Autowired
    private RefreshTokenGenerator refreshTokenGenerator;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private CustomerRedisService customerRedisService;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private RedisMethods redisMethods;

    @Autowired
    private NetCreditRepo netCreditRepo;




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
            redisMethods.addInRedis(authSession,authSession.getAuthKey(),259200);
            AuditorAwareImpl.clear();
            return "Logging In ------- Started Auth Session " + "AuthKey: " + authKey + " | RefreshToken: " + refreshToken;
        }
        else {
            throw new RuntimeException("Password didnt match");
        }

    }

    @Transactional
    public String Register(Customer customer){
        Customer saved= customerRepo.saveAndFlush(customer);
        System.out.println("ID: " + saved.getId());


        saved.setCreatedBy(String.valueOf(saved.getId()));

        AuditorAwareImpl.setCurrentUser(String.valueOf(saved.getId()));

        Customer audited = customerRepo.save(saved);

        redisMethods.addInRedis(audited,audited.getId().toString());

        String key= String.valueOf(audited.getId());

        String authKey=authKeyGenerator.generate();
        String refreshToken = refreshTokenGenerator.generate();

        AuthSession authSession= new AuthSession(audited.getId(),authKey,refreshToken);

        CustomerNetCredit customerNetCredit=new CustomerNetCredit(audited.getId(), BigDecimal.ZERO,1);
        netCreditRepo.save(customerNetCredit);


        sessionRepo.save(authSession);

        redisMethods.addInRedis(authSession, authSession.getAuthKey(),2952000);

        return "Saved with the Id"+ key +" and Auth Session Started AuthKey: " + authKey + " | RefreshToken: " + refreshToken;
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

        redisMethods.deleteInRedis(session, session.getId());
        sessionRepo.delete(session);

        return "Logged out and session ended";

    }


    public String refresh(String refreshToken){
        AuthSession oldsession= sessionRepo.findByRefreshToken(refreshToken).orElseThrow(() ->new RuntimeException("Session not found"));
        if (oldsession.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh token expired, please log in again");
        }

        redisMethods.deleteInRedis(oldsession, oldsession.getId());
        sessionRepo.delete(oldsession);

        String newAuthKey= authKeyGenerator.generate();
        String newRefreshToken=refreshTokenGenerator.generate();
        AuthSession newSession=new AuthSession(oldsession.getUserId(), newAuthKey,newRefreshToken);
        sessionRepo.save(newSession);
        redisMethods.addInRedis(newSession,newSession.getAuthKey(),259200);


        return "session refreshed with "+ "authKey "+ newAuthKey + " refreshToken "+ newRefreshToken;


    }

    public Customer getCustomerByAuthKey(String authKey){
        AuthSession session = sessionRepo.findByAuthKey(authKey)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return customerRepo.getCustomerById(session.getUserId());
    }







}
