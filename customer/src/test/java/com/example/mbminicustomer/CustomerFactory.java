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
        double lat = 28.380 + (Math.random() * 0.080);
        // lon between 77.04 and 77.10
        double lon = 77.040 + (Math.random() * 0.060);

        randomUser.put("Name", faker.name().fullName());
        randomUser.put("PhoneNo", phoneNo);
        randomUser.put("Password", fakeValuesService.bothify("?????#####"));  // ← was missing
        randomUser.put("Email", fakeValuesService.bothify("??????????#####@gmail.com"));
        randomUser.put("HouseNo", fakeValuesService.numerify("###"));
        randomUser.put("Locality", faker.address().streetName());
        randomUser.put("City", "Gurugram");  // ← fixed to valid city
        randomUser.put("Pincode", fakeValuesService.numerify("11000#"));  // ← Gurugram pincode
        randomUser.put("latitude", String.valueOf(lat));   // ← inside fence
        randomUser.put("longitude", String.valueOf(lon));  // ← inside fence

        return randomUser;
    }
}


