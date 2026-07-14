package com.example.mbminicustomer;

import com.example.mbminicustomer.ConfigsRepo.SessionRepo;
import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminicustomer.Services.CustomerService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MbMiniCustomerApplicationTests extends AbstractTestNGSpringContextTests {

    private Map<String,String> user;

    @Autowired
    private RandomPointGenerator pointGenerator;

    @Autowired
    private CustomerService customerService;

    @LocalServerPort
    private int port;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private CustomerFactory customerFactory;

    @BeforeMethod
    public void setup(){
        System.out.println("DEBUG: Connecting to port " + port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

    @Test
    public void registerTest(){
        System.out.println("--- TESTING PORT: " + port + " ---");

        Map<String, String> user = customerFactory.getRandomUser();

        RestAssured.given()
                .baseUri("http://localhost")
                .port(port) // Explicitly map it here
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/customers/register") // Add /api prefix here if context-path exists
                .then()
                .statusCode(200);

    }

    @org.testng.annotations.Test
    public void generateLoginTest(){
        System.out.println("--- TESTING PORT: " + port + "---");
        Map<String,String> user= CustomerFactory.getRandomUser();
        String PhoneNo= user.get("PhoneNo");
        Map<String,String> input=new HashMap<>();
        input.put("PhoneNo",PhoneNo);
        String registered=customerService.Register(new Customer(user.get("Name"),user.get("PhoneNo"),user.get("Password"),user.get("Email"),Long.valueOf(user.get("HouseNo")),user.get("Locality"),user.get("City"),Long.valueOf(user.get("Pincode")),Double.valueOf(user.get("latitude")),Double.valueOf(user.get("longitude"))));
        RestAssured.given()
                .baseUri("http://localhost")
                .port(port) // Explicitly map it here
                .contentType(ContentType.JSON)
                .body(input)
                .when()
                .post("/customers/generate_login") // Add /api prefix here if context-path exists
                .then()
                .statusCode(200);

    }

    @org.testng.annotations.Test
    public void loginTest(){
        System.out.println("--- TESTING PORT: " + port + "---");
        Map<String,String> user= CustomerFactory.getRandomUser();
        String PhoneNo= user.get("PhoneNo");
        String password=user.get("Password");
        String registered=customerService.Register(new Customer(user.get("Name"),user.get("PhoneNo"),user.get("Password"),user.get("Email"),Long.valueOf(user.get("HouseNo")),user.get("Locality"),user.get("City"),Long.valueOf(user.get("Pincode")),Double.valueOf(user.get("latitude")),Double.valueOf(user.get("longitude"))));
        Map<String,String> input=new HashMap<>();
        input.put("PhoneNo",PhoneNo);
        input.put("Password",password);
        RestAssured.given()
                .baseUri("http://localhost")
                .port(port) // Explicitly map it here
                .contentType(ContentType.JSON)
                .body(input)
                .when()
                .post("/customers/login") // Add /api prefix here if context-path exists
                .then()
                .statusCode(200);
    }

    @org.testng.annotations.Test
    public void logoutTest() {
        System.out.println("--- TESTING PORT: " + port + "---");
        Map<String, String> user = CustomerFactory.getRandomUser();
        String registered = customerService.Register(new Customer(user.get("Name"), user.get("PhoneNo"), user.get("Password"), user.get("Email"), Long.valueOf(user.get("HouseNo")), user.get("Locality"), user.get("City"), Long.valueOf(user.get("Pincode")), Double.valueOf(user.get("latitude")), Double.valueOf(user.get("longitude"))));
        Pattern pattern = Pattern.compile("AuthKey:\\s*([^\\s|]+)");
        Matcher matcher = pattern.matcher(registered);
        String authKey = null;
        if (matcher.find()) {
            authKey = matcher.group(1);
        }
        System.out.println("DEBUG LOGOUT TOKEN: [" + authKey + "]");
        boolean exists = sessionRepo.findByAuthKey(authKey).isPresent();
        System.out.println("--- DOES SESSION EXIST IN DB BEFORE LOGOUT? " + exists + " ---");


        RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .contentType(ContentType.JSON)
                // Add "Bearer " prefix with a space here
                .header("AuthKey", "Bearer " + authKey)
                .when()
                .post("/consumer/customers/logout")
                .then()
                .statusCode(200);
    }

    @org.testng.annotations.Test
    public void fenceTest(){
        List<List<Double>> points= RandomPointGenerator.generateNearbyPoints();
        Map<String,List<List<Double>>> input=new HashMap<>();
        input.put("points",points);
        RestAssured.given()
                .baseUri("http://localhost")
                .port(port) // Explicitly map it here
                .contentType(ContentType.JSON)
                .body(input)
                .when()
                .post("/setFence") // Add /api prefix here if context-path exists
                .then()
                .statusCode(200);


    }

    @org.testng.annotations.Test
    public void failRegisterLogin(){
        System.out.println("Running to get a 400 Error");
        System.out.println("--- TESTING PORT: " + port + " ---");

        Map<String, String> user = customerFactory.getRandomUser();
        user.put("Pincode",  "1230984L");

        RestAssured.given()
                .baseUri("http://localhost")
                .port(port) // Explicitly map it here
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/customers/register") // Add /api prefix here if context-path exists
                .then()
                .statusCode(400);

    }

}
