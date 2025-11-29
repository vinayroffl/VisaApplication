package com.example.Visa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.Visa.Auth.AuthenticationRequest;
import com.example.Visa.Dto.UpdateDto;
import com.example.Visa.Entities.Visa;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VisaAppIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate rest;

  private String getBaseUrl() {
    return "http://localhost:" + port;
  }

  private String loginAndGetToken(String email, String password) {
    AuthenticationRequest req = new AuthenticationRequest(email, password);

    ResponseEntity<Map> response = rest.postForEntity(getBaseUrl() + "/user/login", req, Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsKey("jwt");

    return (String) response.getBody().get("jwt");
  }

  @Test
  @DisplayName("1. LOGIN: Should login successfully and return username + token")
  void testLoginSuccess() {
    AuthenticationRequest req = new AuthenticationRequest("eli18@gmail.com", "pass2");

    ResponseEntity<Map> response = rest.postForEntity(getBaseUrl() + "/user/login", req, Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().get("username")).isEqualTo("Elizabeth");
    assertThat(response.getBody()).containsKey("jwt");
  }

  @Test
  @DisplayName("2. LOGIN FAIL: Wrong password returns 400")
  void testLoginFail() {
    AuthenticationRequest req = new AuthenticationRequest("eli18@gmail.com", "wrong");

    ResponseEntity<Map> response = rest.postForEntity(getBaseUrl() + "/user/login", req, Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DisplayName("3. CREATE VISA: USER should create successfully")
  void testCreateVisaAsUser() {
    String token = loginAndGetToken("eli18@gmail.com", "pass2");

    Visa visa = new Visa("APP1000", "USA", "Visiting", 2, "INDIA", "123", "9999445959");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Visa> entity = new HttpEntity<>(visa, headers);

    ResponseEntity<Map> response = rest.postForEntity(getBaseUrl() + "/visa/add", entity, Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).containsEntry("applicationId", "APP1000");
  }

  @Test
  @DisplayName("4. CREATE VISA FAIL: OFFICER cannot create applications")
  void testOfficerCannotCreate() {
    String token = loginAndGetToken("drake60@gmail.com", "pass1");

    Visa visa = new Visa("APP1000", "USA", "Visiting", 2, "INDIA", "123", "9999445959");

    visa.setApplicationId("APP2000");
    visa.setCountry("Canada");
    visa.setStatus("PENDING");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Visa> entity = new HttpEntity<>(visa, headers);

    ResponseEntity<Map> response = rest.postForEntity(getBaseUrl() + "/visa/add", entity, Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DisplayName("5. GET VISA: Should return visa by applicationId")
  void testGetVisa() {
    // first create one
    String token = loginAndGetToken("eli18@gmail.com", "pass2");

    Visa visa = new Visa();

    visa.setApplicationId("APP3000");
    visa.setCountry("Japan");
    visa.setStatus("PENDING");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Visa> entity = new HttpEntity<>(visa, headers);
    rest.postForEntity(getBaseUrl() + "/visa/add", entity, Map.class);

    // now get it
    headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> getEntity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = rest.exchange(
        getBaseUrl() + "/visa/list?applicationID=APP3000",
        HttpMethod.GET,
        getEntity,
        Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsEntry("applicationId", "APP3000");
  }

  @Test
  @DisplayName("6. UPDATE VISA: OFFICER should update status")
  void testUpdateVisaOfficer() {
    // create by user
    String userToken = loginAndGetToken("eli18@gmail.com", "pass2");

    Visa visa = new Visa();
    visa.setApplicationId("APP4000");
    visa.setCountry("UAE");
    visa.setStatus("PENDING");

    HttpHeaders userHeaders = new HttpHeaders();
    userHeaders.setBearerAuth(userToken);
    ResponseEntity<Map> response1 = rest.postForEntity(
        getBaseUrl() + "/visa/add", new HttpEntity<>(visa, userHeaders), Map.class);
    int id = (int) response1.getBody().get("id");
    // login as officer
    String officerToken = loginAndGetToken("drake60@gmail.com", "pass1");

    // PATCH update
    UpdateDto dto = new UpdateDto("APPROVED");
    dto.setStatus("APPROVED");

    HttpHeaders offHeaders = new HttpHeaders();
    offHeaders.setBearerAuth(officerToken);
    HttpEntity<UpdateDto> entity = new HttpEntity<>(dto, offHeaders);

    ResponseEntity<Map> response = rest.exchange(getBaseUrl() + "/visa/update/" + id, HttpMethod.PATCH, entity,
        Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsEntry("status", "APPROVED");
  }

  @Test
  @DisplayName("7. UPDATE FAIL: USER cannot update status (should return 401 per README)")
  void testUserCannotUpdate() {
    String token = loginAndGetToken("eli18@gmail.com", "pass2");
    Visa visa = new Visa();
    visa.setApplicationId("APP4038");
    visa.setCountry("UAE");
    visa.setStatus("PENDING");

    HttpHeaders userHeaders = new HttpHeaders();
    userHeaders.setBearerAuth(token);
    var response1 = rest.postForEntity(
        getBaseUrl() + "/visa/add", new HttpEntity<>(visa, userHeaders), Map.class);
    int id = (int) response1.getBody().get("id");
    UpdateDto dto = new UpdateDto("APPROVED");
    dto.setStatus("APPROVED");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<UpdateDto> entity = new HttpEntity<>(dto, headers);

    ResponseEntity<Map> response = rest.exchange(getBaseUrl() + "/visa/update/" + id, HttpMethod.PATCH, entity,
        Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("8. DELETE VISA: Only creator should delete")
  void testDeleteByCreator() {
    // create app by Elizabeth
    String token = loginAndGetToken("eli18@gmail.com", "pass2");

    Visa visa = new Visa();
    visa.setApplicationId("APP5000");
    visa.setCountry("Australia");
    visa.setStatus("PENDING");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    var response1 = rest.postForEntity(getBaseUrl() + "/visa/add", new HttpEntity<>(visa, headers), Map.class);
    int id = (int) response1.getBody().get("id");
    // delete
    HttpEntity<Void> delEntity = new HttpEntity<>(headers);

    ResponseEntity<Void> response = rest.exchange(
        getBaseUrl() + "/visa/delete/" + id, HttpMethod.DELETE, delEntity, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("9. DELETE FAIL: Officer or other users cannot delete someone else's application")
  void testDeleteNotCreator() {
    // create by Elizabeth
    String tokenUser = loginAndGetToken("eli18@gmail.com", "pass2");

    Visa visa = new Visa();
    visa.setApplicationId("APP6000");
    visa.setCountry("Malaysia");
    visa.setStatus("PENDING");

    HttpHeaders userHeaders = new HttpHeaders();
    userHeaders.setBearerAuth(tokenUser);
    var response1 = rest.postForEntity(
        getBaseUrl() + "/visa/add", new HttpEntity<>(visa, userHeaders), Map.class);
    int id = (int) response1.getBody().get("id");
    // Officer attempts to delete
    String tokenOfficer = loginAndGetToken("drake60@gmail.com", "pass1");
    HttpHeaders offHeaders = new HttpHeaders();
    offHeaders.setBearerAuth(tokenOfficer);

    ResponseEntity<Map> response = rest.exchange(
        getBaseUrl() + "/visa/delete/" + id,
        HttpMethod.DELETE,
        new HttpEntity<>(offHeaders),
        Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
