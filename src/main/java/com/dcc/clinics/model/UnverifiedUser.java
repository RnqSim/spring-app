package com.dcc.clinics.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "unverified_users")
public class UnverifiedUser {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    @Column(name = "first_name", length = 30)
    private String firstName;

    @Column(name = "middle_name", length = 30)
    private String middleName;

    @Column(name = "last_name", length = 30)
    private String lastName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "sex", nullable = false, length = 6)
    private String sex;

    @Column(name = "birthday", nullable = false)
    private Date birthday;

    @Column(name = "address", nullable = false, length = 120)
    private String address;

    @Column(name = "contact_number", nullable = false, length = 12)
    private String contactNumber;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "pword", nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

	
	@Column(name = "otp")
	@JsonProperty("otp")
	Integer otp;
	
	
	public UnverifiedUser() {
	}
	
	public UnverifiedUser(User user, Integer otp) {
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName =  user.getLastName();
		this.age =  user.getAge();
		this.sex =  user.getSex();
		this.birthday =  user.getBirthday();
		this.address =  user.getAddress();
		this.contactNumber =  user.getContactNumber();
		this.email =  user.getEmail();
		this.password =  user.getPassword();
		this.userType =  user.getUserType();
		this.otp = otp;
	}

	public UnverifiedUser(Long userId, String username, String firstName, String middleName, String lastName,
			Integer age, String sex, Date birthday, String address, String contactNumber, String email, String password,
			Integer otp, String userType) {
		super();
		this.userId = userId;
		this.username = username;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.age = age;
		this.sex = sex;
		this.birthday = birthday;
		this.address = address;
		this.contactNumber = contactNumber;
		this.email = email;
		this.password = password;
		this.otp = otp;
		this.userType = userType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
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

	public Integer getOtp() {
		return otp;
	}

	public void setOtp(Integer otp) {
		this.otp = otp;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	
	
}
