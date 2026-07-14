package com.example.mbminicustomer;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Component
public class CustomerFactory {

    public static Map<String, String> getRandomUser() {
        Map<String, String> randomUser = new HashMap<>();
        Faker faker = new Faker();
        FakeValuesService fakeValuesService = new FakeValuesService(Locale.ENGLISH, new RandomService());

        // guaranteed 10 digit phone starting with 9
        String phoneNo = "9" + fakeValuesService.numerify("#########");

        // lat between 28.38 and 28.46 (inside Gurugram fence)
        // Replace random lat/long generation with fence-safe values
        double lat = 28.420 + (Math.random() * 0.020);  // 28.420 to 28.440
        double lon = 77.038 + (Math.random() * 0.020); // 77.041 to 77.099
        System.out.println("DEBUG LAT: " + lat + " LON: " + lon);

        // Replace pincode generation with Gurugram pincode
        String[] gurugramPincodes = {"110001", "110002", "110003", "110009", "110011", "110015", "110018"};
        randomUser.put("Pincode", gurugramPincodes[(int)(Math.random() * gurugramPincodes.length)]);

        randomUser.put("Name", faker.name().fullName());
        randomUser.put("PhoneNo", phoneNo);
        randomUser.put("Password", fakeValuesService.bothify("?????#####"));  // ← was missing
        randomUser.put("Email", fakeValuesService.bothify("??????????#####@gmail.com"));
        randomUser.put("HouseNo", fakeValuesService.numerify("###"));
        randomUser.put("Locality", faker.address().streetName());
        randomUser.put("City", "Gurugram");  // ← fixed to valid city
        
        randomUser.put("latitude", String.valueOf(lat));
        randomUser.put("longitude", String.valueOf(lon));
        return randomUser;
    }
}


