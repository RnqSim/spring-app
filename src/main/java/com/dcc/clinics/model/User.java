package com.dcc.clinics.model;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

	@Id
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
    
    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

	public User() {
		
	}
	
	public User(UnverifiedUser unvUser) {
		this.userId = unvUser.getUserId();
		this.username = unvUser.getUsername();
		this.firstName = unvUser.getFirstName();
		this.middleName = unvUser.getMiddleName();
		this.lastName =  unvUser.getLastName();
		this.age =  unvUser.getAge();
		this.sex =  unvUser.getSex();
		this.birthday =  unvUser.getBirthday();
		this.address =  unvUser.getAddress();
		this.contactNumber =  unvUser.getContactNumber();
		this.email =  unvUser.getEmail();
		this.password =  unvUser.getPassword();
		this.userType =  unvUser.getUserType();
	}

	public User(Long userId, String username, String firstName, String middleName, String lastName, int age, String sex,
			Date birthday, String address, String contactNumber, String email, String password, String userType, String avatar) {
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
		this.userType = userType;
		this.avatar = avatar;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	
	
}
