package com.example.mbminicustomer;

import com.example.mbminicustomer.ConfigsRepo.SessionRepo;
import com.example.mbminicustomer.Entities.Customer;
import com.example.mbminicustomer.Services.CustomerService;
import com.example.mbminiframework.RedisPackage.RedisMethods;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
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
    private RedisMethods redisMethods;

    @Autowired
    private CustomerService customerService;

    @LocalServerPort
    private int port;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;


    @BeforeMethod
    public void setup(){
        System.out.println("DEBUG: Connecting to port " + port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

    @org.testng.annotations.BeforeSuite
    @org.testng.annotations.Parameters({"test.db.url", "test.redis.db"})
    public void initTestEnvironment(@org.testng.annotations.Optional("jdbc:mysql://localhost:3306/mb_mini_test?createDatabaseIfNotExist=true") String dbUrl,
                                    @org.testng.annotations.Optional("6") String redisDb) {
        System.setProperty("spring.datasource.url", dbUrl);
        System.setProperty("spring.data.redis.database", redisDb);
        System.out.println(">>> ROUTING INTEGRATION TESTS TO DATABASE: " + dbUrl);
        System.out.println(">>> ROUTING INTEGRATION TESTS TO REDIS DB: " + redisDb);
    }

    @Test(priority = 1)
    public void registerTest(){
        System.out.println("--- TESTING PORT: " + port + " ---");

        Map<String, String> user = CustomerFactory.getRandomUser();

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

    @org.testng.annotations.Test(priority = 2)
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

    @org.testng.annotations.Test(priority = 3)
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

    @org.testng.annotations.Test(priority = 4,dependsOnMethods = "loginTest")
    public void logoutTest() {
        Map<String, String> user = CustomerFactory.getRandomUser();

        String registerResponse = RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/customers/register")
                .then()
                .statusCode(200)
                .extract().asString();

        System.out.println("DEBUG REGISTER RESPONSE: [" + registerResponse + "]");

        Pattern pattern = Pattern.compile("AuthKey:\\s*([^\\s|]+)");
        Matcher matcher = pattern.matcher(registerResponse);
        String authKey = null;
        if (matcher.find()) {
            authKey = matcher.group(1);
        }

        System.out.println("DEBUG AUTH KEY SENDING TO LOGOUT: [" + authKey + "]");

        // check Redis directly
        String redisKey = "com.example.mbminiframework.Entity.AuthSession." + authKey;
        System.out.println("DEBUG REDIS KEY: [" + redisKey + "]");

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }  // small delay

        RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .contentType(ContentType.JSON)
                .header("AuthKey", authKey)
                .when()
                .post("/consumer/customers/logout")
                .then()
                .statusCode(200);
    }

    @org.testng.annotations.Test(priority = 6)
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

    @org.testng.annotations.Test(priority = 5)
    public void failRegisterLogin(){
        System.out.println("Running to get a 400 Error");
        System.out.println("--- TESTING PORT: " + port + " ---");

        Map<String, String> user = CustomerFactory.getRandomUser();
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

    @AfterClass
    public void testCleanup() {
        System.out.println("--- STARTING TEST ENVIRONMENT CLEANUP ---");

        // 1. Wipe Redis DB 6 completely
        try {
            redisMethods.flushAllData(); // Executing jedis.flushDB() as added previously
        } catch (Exception e) {
            System.err.println("CLEANUP ERROR: Failed to clear Redis Database 6: " + e.getMessage());
        }

        // 2. Clear out test session structures
        try {
            sessionRepo.deleteAll();
        } catch (Exception e) {
            System.err.println("CLEANUP ERROR: Failed to sweep session repository: " + e.getMessage());
        }

        // 3. Purge MySQL tables inside mb_mini_test database
        try {
            System.out.println("DEBUG CLEANUP: Truncating customer data tables inside mb_mini_test...");
            // Disable foreign keys temporarily to prevent deletion lock failures
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("TRUNCATE TABLE customer;"); // Replace 'customer' with your actual table name if different
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            System.out.println("DEBUG CLEANUP: Successfully purged all test database records.");
        } catch (Exception e) {
            System.err.println("CLEANUP ERROR: Database truncate statement failed: " + e.getMessage());
        }

        System.out.println("--- TEST ENVIRONMENT CLEANUP COMPLETE ---");
    }

}
