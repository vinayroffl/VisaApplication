# Visa Management System API

Visa Management System is an API used where the user can able to request
for visa to other countries where they want to go and the visa officers review that
application and approve the status of the application.

## Note

- Use Classes that are provided to you.
- All inputs and outputs are case sensitive.
- All authentication and authorization process should needs to be implement
using JWT Token.
- JWT token should be sent as a Bearer token in Authorization request
header.

  For example: Authorization value would be ```"Bearer <SPACE>
<JWT TOKEN>"```.

### Models

#### UserInfo Model

| Field Name | DataType | PrimaryKey | ForeignKey | Comments |
| ---------- |:--------:|:----------:|------------|:--------:|
| id     | ""eger  |  Yes     |      |      |
| name     | String   |      |      |      |
| email    | String   |      |      |Unique  |
| password   | String   |      |      |      |
| roles    | String   |      |      |      |

#### Visa Model

|   Field Name  |  DataType  |   PrimaryKey | ForeignKey|      Comments    |
| ------------- | :--------  | :----------: | :--------: | :------------------------  |
| id      | ""eger  |   Yes    |       | Auto-generated       |
| applicationID | String   |        |       | Unique[Eg:AID1234]     |
| country     | String   |        |       |              |
| visaType    | String   |        |       |              |
| duration    | Float    |        |       | In Months[Eg:2.5]      |
| nationality   | String   |        |       |              |
| passportNumber| String   |        |       |              |
| status    | String   |        |       | Default=”approval pending” |
| phoneNumber   | ""eger  |        |       |              |
| applicant   | UserInfo   |        |   Yes   |Many to one with user model |

#### User Model has the following data

|Id | Username  |  Email         | Password  |  Roles   |
| ---| --- |---|---|---|
|1 |  Daniel  |  <daniel123@gmail.com> | pass1   |  OFFICER |
|2 |  Alice   |  <alice456@gmail.com>  | pass2   |  USER  |
|3 |  Jack    |  <jack789@gmail.com>   | pass3   |  USER  |

### EndPoints

- ``` user/login ``` **Post Method** (accessed by anyone without authentication)

  Get the email, password from the user and check whether it is found in user model, if found then return username and token as response with status code 200.

  **Request :**

  ```json
  {
  "email": "daniel123@gmail.com",
  "password": "pass1"
  }
  ```

  **Response:**

  ```json
  {
  "username": "Daniel",
  "token": "token"
  }
  ```

  If given email id not found, then return with status code 400.
  If given password does not match with actual password, with status code
  400.

- ```visa/list``` **Get Method** (accessed by any users who are authenticated)

  Get the applicationID as a parameter from the URL and return all the
  application details which are related with given application ID as a response with status code 200.

  **Request:**
  (accessed by any users who are authenticated) *localhost:8080/visa/list?applicationID=AID12345*

  **Response:**

  ```json
  {
    "applicationId": "1",
    "country": "USA",
    "visatype": "Visiting",
    "duration": "2",
    "nationality": "India",
    "passportNumber": "123456789",
    "phoneNumber": "9999669599"
  }
  ```

  If no value given to search in the URL, then return with status code 400.

- ```visa/add``` **Post Method** (users who are authenticated, and he/she needs to have role **“USER”**)

  Get the details applicationID, country, visaType, duration, nationality,passportNumber, status, phoneNumber, applicant_id (current logged in user id) from the user and store it in the visa model, then return it as a response with status code 201.

  **Request:**

  ```json
  {
  
  "applicationId":"",
  "country":"",
  "visatype":"",
  "duration":"",
  "nationality":"",
  "passportNumber":"",
  "phoneNumber":""

  }
  ```

  **Response:**

  ```json
  {
  "id":"",
  "applicationId":"",
  "country":"",
  "visatype":"",
  "duration":"",
  "nationality":"",
  "passportNumber":"",
  "phoneNumber":"",
  "status":"",
  "applicantId":""
  }
  ```

  If the given applicationID already found, then return with status code 400.
  If authenticated user did not have role USER, then return status code 403.
  If tried to access the endpo"" without token, return 401 status code.

- ```visa/update/{"":id}``` Patch Method** (accessed by users who are authenticated and has role **“OFFICER”**)

  Get the detail ( status ) to be patched from the user and update it in the visa detail with given id, then return it as a response with status code 200. Use UpdateDto class to pass the details.

  **Request:**

  ```json
  REQUEST:
  {
  "status":""
  }
  ```

  **Response:**

  ```json
  {
  "id":"",
  "applicationId":"",
  "country":"",
  "visatype":"",
  "duration":"",
  "nationality":"",
  "passportNumber":"",
  "phoneNumber":"",
  "status":"",
  "applicantId": ""
  }
  ```

  If the given id not found, then return with status code 404.
  If the authenticated user didn’t have role OFFICER, then return status code 401.

- ```visa/delete/{"":id}``` **Delete Method** (users who are authenticated, and he/she needs to have role **“USER”**)

  **Note: This method requires authentication.**

  User who is authenticated and need to be the creator of the Visa object with given ID, then only they can be able to perform delete operation. Get the visa object with the id from visa model, delete it and return 204 as a status code.

  If the given id not found, then return with status code 404.
  If the authenticated user is not the creator of the given id object, then return with status code 403.

## codes in which you need to code

```java
package com.example.Visa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class VisaApplication {

  public static void main(String[] args) {
    SpringApplication.run(VisaApplication.class, args);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
```

```java
package com.example.Visa.Auth;

public class AuthenticationRequest {
  
  private String email;
  private String password;
  
  public AuthenticationRequest() {
    
  }
  public AuthenticationRequest(String email, String password) {
    super();
    this.email = email;
    this.password = password;
  }
  
  
  
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  
  
}
```

```java
package com.example.Visa.Auth;

public class AuthenticationResponse {
  
  private final String jwt;
  
  public AuthenticationResponse(String jwt) {
    super();
    this.jwt = jwt;
  }
  public String getJwt() {
    return jwt;
  }
}
```

```java
package com.example.Visa.Config;

import com.example.Visa.Entities.UserInfo;
import com.example.Visa.Repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

  @Autowired
  UserInfoRepository userInfoRepository;
  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    userInfoRepository.save(new UserInfo("Daniel","daniel123@gmail.com",passwordEncoder.encode("pass1"),"OFFICER"));
    userInfoRepository.save(new UserInfo("Alice","alice456@gmail.com",passwordEncoder.encode("pass2"),"USER"));
    userInfoRepository.save(new UserInfo("Jack","jack789@gmail.com",passwordEncoder.encode("pass3"),"USER"));
  }
}
```

```java
package com.example.Visa.Config;


import com.example.Visa.Filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPo"";
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


public class SecurityConfig {

  private JwtAuthFilter authFilter;

  UserInfoUserDetailsService userInfoUserDetailsService;

  @Bean
  UserDetailsService userDetailsService() {return null;}

  @Bean
  WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {return null;}

  @Bean
  AuthenticationProvider authenticationProvider(){return null;}

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {return null;}

}
```

```java
package com.example.Visa.Config;


import com.example.Visa.Entities.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoUserDetails implements UserDetails {

  private String name;
  private String password;
  private List<GrantedAuthority> authorities;

  public UserInfoUserDetails(UserInfo userInfo) {}

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
```

```java
package com.example.Visa.Config;


import com.example.Visa.Entities.UserInfo;
import com.example.Visa.Repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class UserInfoUserDetailsService implements UserDetailsService {

  private UserInfoRepository repository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return null;
  }

}
```

```java
package com.example.Visa.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Visa.Auth.AuthenticationRequest;
import com.example.Visa.Dto.DeleteDto;
import com.example.Visa.Dto.UpdateDto;
import com.example.Visa.Entities.Visa;
import com.example.Visa.Services.ApplicationService;
import com.example.Visa.Services.AuthService;

public class Controller {

  ApplicationService as;

  AuthService auth;
  
  @PostMapping("/user/login")
  public ResponseEntity<Object> login(AuthenticationRequest ar){return null;}
  
  @PostMapping("/visa/add")
  public ResponseEntity<Object> createApplication(Visa application){return null;}
  
  @DeleteMapping("/visa/delete/{id}")
  public ResponseEntity<Object> deleteApplication("" id){return null;}
  
  @GetMapping("/visa/list")
  public ResponseEntity<Object> getAllApplication(String applicationID){return null;}
  
  @PatchMapping("/visa/update/{id}")
  public ResponseEntity<Object> updateApplication("" id, UpdateDto dto){return null;}
  

}
```

```java
package com.example.Visa.Dto;

public class DeleteDto {
  
  private "" applicationId;

  public "" getApplicationId() {
    return applicationId;
  }

  public void setApplicationId("" applicationId) {
    this.applicationId = applicationId;
  }
  
  

}
```

```java
package com.example.Visa.Dto;

public class UpdateDto {
  
  private String status;

  public UpdateDto(String status) {
    super();
    this.status = status;
  }
  
  UpdateDto(){}

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
  

}
```

```java
package com.example.Visa.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private "" id;
  private String name;
  private String email;
   // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  private String roles;

  public UserInfo(String name, String email, String password, String roles) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.roles = roles;
  }

  public "" getId() {
    return id;
  }

  public void setId("" id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }
}
```

```java
package com.example.Visa.Entities;





import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GeneratorType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;



@Entity
public class Visa {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private "" id;
  @Column(unique=true)
  private String applicationId;
  private String country;
  private String visaType;
  private float duration;
  private String nationality;
  private String passportNumber;
  private String phoneNumber;
  private String status="approval pending";
  @ManyToOne
  @JsonProperty("applicantId")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private UserInfo applicant;
  
  
  

  Visa(){}


  public Visa(String applicationId, String country, String visaType, float duration, String nationality,
      String passportNumber, String phoneNumber) {
    super();
    this.applicationId = applicationId;
    this.country = country;
    this.visaType = visaType;
    this.duration = duration;
    this.nationality = nationality;
    this.passportNumber = passportNumber;
    this.phoneNumber = phoneNumber;
  }
  
  public "" getId() {
    return id;
  }
  public void setId("" id) {
    this.id = id;
  }
  public String getApplicationId() {
    return applicationId;
  }
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }
  public float getDuration() {
    return duration;
  }
  public void setDuration(float duration) {
    this.duration = duration;
  }
  public String getNationality() {
    return nationality;
  }
  public void setNationality(String nationality) {
    this.nationality = nationality;
  }
  public String getPassportNumber() {
    return passportNumber;
  }
  public void setPassportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
  }
  public String getVisaType() {
    return visaType;
  }


  public void setVisaType(String visaType) {
    this.visaType = visaType;
  }


  public String getPhoneNumber() {
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public UserInfo getApplicant() {
    return applicant;
  }
  public void setApplicant(UserInfo applicant) {
    this.applicant = applicant;
  }
  
  
  

}
```

```java
package com.example.Visa.Filter;



import com.example.Visa.Config.UserInfoUserDetailsService;
import com.example.Visa.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {


  private JwtService jwtService;

  private UserInfoUserDetailsService userDetailsService;

  @Override
  protected void doFilter""ernal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    
  }
}
```

```java
package com.example.Visa.Repositories;



import com.example.Visa.Entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public ""erface UserInfoRepository extends JpaRepository<UserInfo, ""eger> {

  //Add the required annotations to make the UserInfoRepository

}
```

```java
package com.example.Visa.Repositories;

import java.util.List;
import java.util.Optional;

import com.example.Visa.Entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Visa.Entities.Visa;

public ""erface VisaRepository extends JpaRepository<Visa, ""eger> {

}
```

```java
package com.example.Visa.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.Visa.Entities.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.Visa.Dto.UpdateDto;
import com.example.Visa.Entities.Visa;
import com.example.Visa.Repositories.UserInfoRepository;
import com.example.Visa.Repositories.VisaRepository;
import com.example.Visa.Config.UserInfoUserDetails;

public class ApplicationService {

  VisaRepository ar;

  AuthenticationManager am;

  UserInfoRepository ur;
  
  public ResponseEntity<Object> createApplication(Visa application){
    return null;
  }


  public ResponseEntity<Object> deleteApplication(""eger Id){
    return null;
  }
  
  public ResponseEntity<Object> getApplication(String applicationId){
    return null;
  }
  
  
  public ResponseEntity<Object> updateApplication("" id, UpdateDto dto){
    return null;
  }

  
}
```

```java
package com.example.Visa.Services;

import java.util.HashMap;
import java.util.Map;

import com.example.Visa.Config.UserInfoUserDetailsService;
import com.example.Visa.Entities.UserInfo;
import com.example.Visa.Repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.Visa.Auth.AuthenticationRequest;
//import com.example.Visa.Entities.Users;
//import com.example.Visa.Repositories.UsersRepository;
//import com.example.Visa.Security.JwtUtil;
import com.example.Visa.Filter.JwtAuthFilter;

public class AuthService {

  private UserInfoRepository urepo;

  private AuthenticationManager am;

  private UserInfoUserDetailsService mud;

  private JwtService jut;
  
  
  public ResponseEntity<Object> createAuthenticationtoken(AuthenticationRequest ar){
    return null;
  }

}
```

```java
package com.example.Visa.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtService {

  /*
    Implement the business logic for the JwtService  operations in this class
    Make sure to add required annotations
  */

  public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

  public static final long JWT_TOKEN_VALIDITY = 500 * 60 * 60;

  public String extractUsername(String token) {
    return null;
  }

  public Date extractExpiration(String token) {
    return null;
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    return null;
  }

  private Claims extractAllClaims(String token) {
    return null;
  }

  private Boolean isTokenExpired(String token) {
    return null;
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    return null;
  }




  public String generateToken(String userName){
     return null;
  }

  private String createToken(Map<String, Object> claims, String userName) {
    return null;
  }

  private Key getSignKey() {
    return null;
  }
}
```

```java
package com.example.Visa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class VisaApplication {

  public static void main(String[] args) {
    SpringApplication.run(VisaApplication.class, args);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
```
