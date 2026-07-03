package com.example.mbminicustomer.Services;


import com.example.mbminicustomer.*;
import com.example.mbminicustomer.ConfigsRepo.CustomerRepo;
import com.example.mbminicustomer.ConfigsRepo.NetCreditRepo;
import com.example.mbminicustomer.ConfigsRepo.SessionRepo;
import com.example.mbminicustomer.Entities.CustomerNetCredit;
import com.example.mbminiframework.Entity.AuthSession;
import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminiframework.PolyCheck.Polygon;
import com.example.mbminiframework.PolyCheck.PolygonChecker;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private AuthKeyGenerator authKeyGenerator;
    @Autowired
    private RefreshTokenGenerator refreshTokenGenerator;

    @Autowired
    private PolygonChecker polygonChecker;

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

    @Value("${opc.api.key}")
    private  String apiKey;

    @Value("${opc.api.url}")
    private String opcUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pin.api.url}")
    private String pinUrl;

    @Autowired
    private Polygon fence;

    private static final String fenceKey="serviceableGeoFence";




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

        if (customer.getLatitude()!=null && customer.getLongitude()!=null){
            if (!fence.insidePolygon(List.of(customer.getLatitude(),customer.getLongitude()))){
                throw new RuntimeException("Location Outside Delivery Fence.");
            }
        }

        Long pincode=customer.getPincode();
        String fullPinUrl = pinUrl + "/" + pincode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> pinResponse = restTemplate.exchange(
                fullPinUrl, HttpMethod.GET, entity, List.class);

        Map firstResult = (Map) pinResponse.getBody().get(0);

        if ("Error".equals(firstResult.get("Status"))) {
            throw new RuntimeException("Invalid Pincode provided: " + pincode);
        }

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

    public String addressUpdate(Long customerId,Long HouseNo, String Locality, String City, Long Pincode){
        Customer customer=customerRepo.getCustomerById(customerId);

        String fullPinUrl = pinUrl + "/" + Pincode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> pinResponse = restTemplate.exchange(
                fullPinUrl, HttpMethod.GET, entity, List.class);

        Map firstResult = (Map) pinResponse.getBody().get(0);

        if ("Error".equals(firstResult.get("Status"))) {
            throw new RuntimeException("Invalid Pincode provided: " + Pincode);
        }

        String address = HouseNo + " " + Locality + ", " + City + " " + Pincode + ", India";
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

        String url = opcUrl+"?q=" + address.replace(" ", "+")
                + "&key=" + apiKey
                + "&limit=1"
                + "&countrycode=in";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();

        List results = (List) body.get("results");
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Could not geocode address: " + address);
        }

        Map result = (Map) results.get(0);
        Map geometry = (Map) result.get("geometry");

        double latitude = ((Number) geometry.get("lat")).doubleValue();
        double longitude = ((Number) geometry.get("lng")).doubleValue();

        if (!fence.insidePolygon(List.of(latitude,longitude))){
            throw new RuntimeException("New Adress Outside Our Delivery Fence.");
        }

        customer.setLatitude(latitude);
        customer.setLongitude(longitude);
        customerRepo.save(customer);

        return "Address Updated";


    }

    public String validPolygon(List<List<Double>> points){

        if (!polygonChecker.isValidFence(points)){
            throw new RuntimeException("Invalid Points provided.");
        }

        redisMethods.addInRedis(points,fenceKey);
        return "Fence set successfully with " + points.size() + " points";
    }

    @PostConstruct
    public void loadFenceFromRedis(){
        List<List<Double>> points=redisMethods.getFence();
        if (points != null) {
            fence = polygonChecker.buildPolygon(points);
            log.info("Fence loaded from Redis with {} points", points.size());
        } else {
            log.info("No fence in Redis — using default fence from framework");
        }
    }








}
