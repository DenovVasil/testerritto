package com.netcracker.testerritto.services;

import static org.junit.Assert.assertTrue;

import com.netcracker.testerritto.ApplicationConfiguration;
import com.netcracker.testerritto.dao.UserDAO;
import com.netcracker.testerritto.exceptions.ServiceException;
import com.netcracker.testerritto.models.GradeCategory;
import com.netcracker.testerritto.models.Group;
import com.netcracker.testerritto.models.Question;
import com.netcracker.testerritto.models.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ApplicationConfiguration.class)
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TestService testService;

    private User user1;
    private User user2;        //with out group
    private BigInteger user1Id;
    private BigInteger user2Id;
    private BigInteger test1Id;

    private Group group1;
    private BigInteger group1Id;

    @Before
    public void init() {
        user1 = new User("Allina", "Verde",
            "verde.@gmail.com", "111111", "677990674");

        user1Id = userService.createUser(user1);

        group1 = new Group();
        group1.setLink("link");
        group1.setName("GroupName");
        group1.setCreatorUserId(user1Id);

        group1Id = groupService.createGroup(group1);

        user2 = new User("Gena", "Bukin",
            "gukinn.@gmail.com", "111111", "567832221");
        user2Id = userService.createUser(user2);

        test1Id = testService.createTest(getNewTest());


    }

    @After
    public void tearDown() {
        userService.deleteUser(user1Id);
        groupService.deleteGroup(group1Id);
        userService.deleteUser(user2Id);
        testService.deleteTest(test1Id);
    }


    @Test
    public void getUserByEmail() {
        User newUser = userService.getUserByEmail(user1.getEmail());
        assertTrue(newUser.getPhone().equals(user1.getPhone()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByEmailWrongEmail() {
        User newUser = userService.getUserByEmail("user1.getEmail()");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByEmailNullEmail() {
        User newUser = userService.getUserByEmail(null);
    }

    @Test
    public void updateUser() {
        User newUser = new User("Badoeva", "Zhana",
            "banana@gmail.com", "111111", "123456089");
        newUser.setId(user1Id);
        user1 = userService.updateUser(newUser);
        assertTrue(user1.equals(newUser));
    }


    @Test(expected = IllegalArgumentException.class)
    public void updateUserWithExistingPhone() {
        user1.setPhone(user2.getPhone());
        user1.setId(user1Id);
        userService.updateUser(user1);
    }

    @Test(expected = ServiceException.class)
    public void updateUserWithBadId() {
        user1.setId(BigInteger.valueOf(-12345777));
        userService.updateUser(user1);
    }


    @Test(expected = IllegalArgumentException.class)
    public void createUserWithEmailNullParamTest() {
        user1.setEmail(null);
        userService.createUser(user1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithExistEmailTest() {

        userService.createUser(user1);
    }

    @Test
    public void createUserWithUniqueEmailTest() {
        User user = userService.getUser(user1Id);
        assertTrue(user1.getEmail().equals(user.getEmail()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithPhoneNullParamTest() {
        user1.setEmail("verdeShMerde.@gmailll");
        user1.setPhone(null);
        userService.createUser(user1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithExistPhoneTest() {
        user1.setEmail("verdeShMerde.@gmailll");
        BigInteger id2 = userService.createUser(user1);
    }

    @Test
    public void createUserWithUniquePhoneTest() {
        User user = userService.getUser(user1Id);
        assertTrue(user1.getPhone().equals(user.getPhone()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithNullFirstName() {
        user1.setEmail("verdeShMerde.@gmail.com");
        user1.setPhone("123451488");
        user1.setFirstName(null);
        userService.createUser(user1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithEmptyLastName() {
        user1.setEmail("verdeShMerde.@gmailll");
        user1.setPhone("123451488");
        user1.setLastName("");
        userService.createUser(user1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithNullPassword() {
        user1.setEmail("verdeShMerde.@gmailll");
        user1.setPhone("123451488");
        user1.setPassword(null);
        userService.createUser(user1);
    }

    @Test(expected = ServiceException.class)
    public void deleteUserTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.getUser(id);

    }


    @Test(expected = ServiceException.class)
    public void getUserWithBadId() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.getUser(id);

    }

    @Test
    public void getUserWithGoodId() {
        User user = new User("Allina", "Verde",
            "verdun@gmail.com", "111111", "123456787");
        BigInteger id = userService.createUser(user);
        User user1 = userService.getUser(id);
        assertTrue(user.getFirstName().equals(user1.getFirstName()));
        userService.deleteUser(id);
    }

    @Test(expected = ServiceException.class)
    public void updateLastNameBadIdTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);    //service.delete
        userService.updateLastName(id, "Covington");
    }

    @Test
    public void updateLastNameTest() {
        userService.updateLastName(user1Id, "Covington");
        User user = userService.getUser(user1Id);
        assertTrue(user.getLastName().equals("Covington"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateFirstNameWithNullParamTest() {
        userService.updateLastName(user1Id, null);
    }

    @Test
    public void updateFirstNameTest() {
        userService.updateFirstName(user1Id, "Gena");
        User user = userService.getUser(user1Id);
        assertTrue(user.getFirstName().equals("Gena"));
    }


    @Test(expected = ServiceException.class)
    public void updateFirstNameBadIdTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.updateFirstName(id, "Covington");
    }

    @Test(expected = IllegalArgumentException.class)
    public void updatePasswordWithNullParamTest() {
        userService.updatePassword(user1Id, null);
    }

    @Test(expected = ServiceException.class)
    public void updatePasswordWithBadIdTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.updatePassword(id, "Covin");
    }

    @Test
    public void updatePasswordTest() {
        userService.updatePassword(user1Id, "123456");
        User user = userService.getUser(user1Id);
        assertTrue(user.getPassword().equals("123456"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateEmailWithExistingEmailTest() {
        userService.updateEmail(user1Id, user1.getEmail());

    }

    @Test
    public void updateEmailWithNoExistingEmailTest() {
        userService.updateEmail(user1Id, "verdeShMerde.@gmaillllll");
        User user = userService.getUser(user1Id);
        assertTrue(user.getEmail().equals("verdeShMerde.@gmaillllll"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateEmailWithEmptyEmailTest() {
        userService.updateEmail(user1Id, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void updatePhoneWithExistingPhoneTest() {
        userService.updatePhone(user1Id, user1.getPhone());

    }

    @Test
    public void updatePhoneWithNoExistingPhoneTest() {
        userService.updatePhone(user1Id, user1.getPhone() + "423");
        User user = userService.getUser(user1Id);
        assertTrue(user.getPhone().equals(user1.getPhone() + "423"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void updatePhoneWithNullPhoneTest() {
        userService.updatePhone(user1Id, null);
    }

    @Test(expected = ServiceException.class)
    public void updatePhoneBadIdTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "134567876");
        BigInteger id = userService.createUser(user);
        userDAO.deleteUser(id);
        userService.updatePhone(id, user1.getPhone() + "ere");
    }

    @Test
    public void getGroupsWithEmptyResultTest() {
        List<Group> groupsList = userService.getGroups(user2Id);
        assertTrue(groupsList.size() == 0);
    }

    @Test
    public void getGroupsTest() {
        List<Group> groupList = userService.getGroups(user1Id);
        assertTrue(groupList.size() == 1);

        List<com.netcracker.testerritto.models.Test> testsList = groupList.get(0).getTests();
        assertTrue(testsList.size() == 1);

        List<User> userList = groupList.get(0).getUsers();
        assertTrue(userList.size() == 1);

    }

    @Test
    public void getCreatedGroups() {
        List<Group> groupList = userService.getCreatedGroups(user1Id);
        assertTrue(groupList.size() == 1);

        List<com.netcracker.testerritto.models.Test> testsList = groupList.get(0).getTests();
        assertTrue(testsList.size() == 1);

        List<User> userList = groupList.get(0).getUsers();
        assertTrue(userList.size() == 1);
    }

    @Test(expected = ServiceException.class)
    public void getGroupsBadIdTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.getGroups(id);
    }

    @Test
    public void getCreatedGroupsTest() {
        List<Group> groupsList = userService.getCreatedGroups(user1Id);
        assertTrue(groupsList.size() == 1);
    }

    @Test(expected = ServiceException.class)
    public void getCreatedGroupsBadIdTest() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456786");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.getCreatedGroups(id);

    }

    @Test
    public void deleteCreatedGroupTest() {
        Group newGroup = new Group();
        newGroup.setLink("Newlink");
        newGroup.setName("NewGroupName");
        newGroup.setCreatorUserId(user1Id);

        BigInteger newGroupId = groupService.createGroup(newGroup);

        userService.deleteCreatedGroup(user1Id, newGroupId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCreatedGroupBadUserId() {
        userService.deleteCreatedGroup(user2Id, group1Id);
    }

    @Test(expected = ServiceException.class)
    public void deleteCreatedGroupDeletedUserId() {
        User user = new User("Allina", "Verde",
            "verdun@gamil.com", "111111", "123456787");
        BigInteger id = userService.createUser(user);
        userService.deleteUser(id);
        userService.deleteCreatedGroup(id, group1Id);
    }


    @Test
    public void enterInGroupTest() {
        userService.enterInGroup(user2Id, group1Id);
    }

    @Test(expected = ServiceException.class)
    public void enterInGroupInWhichYouConsistTest() {
        userService.enterInGroup(user1Id, group1Id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void enterInGroupWithNullGroupIdParamTest() {
        userService.enterInGroup(user1Id, null);
    }

    @Test
    public void exitFromGroupTest() {
        userService.exitFromGroup(user1Id, group1Id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exitFromGroupInWhichYouDontConsistTest() {
        userService.exitFromGroup(user2Id, group1Id);
    }

    @Test(expected = ServiceException.class)
    public void exitFromGroupWithBadGroupIdTest() {
        Group newGroup = new Group();
        newGroup.setLink("Newlink");
        newGroup.setName("NewGroupName");
        newGroup.setCreatorUserId(user1Id);

        BigInteger newGroupId = groupService.createGroup(newGroup);
        groupService.deleteGroup(newGroupId);
        userService.exitFromGroup(user1Id, newGroupId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNameValidNameStartWithSpaceTest() {
        String name = user1.getFirstName();
        name = " " + name;
        userService.isNameValid(name, "firstName ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNameValidNameFinishWithSpaceTest() {
        String name = user1.getFirstName();
        name = name + "  ";
        userService.isNameValid(name, "firstName ");
    }

    @Test
    public void isNameValidTest() {
        assertTrue(userService.isNameValid(user1.getFirstName()+ " de", "first Name "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNameValidNameWithNoAlphabetCharsTest() {
        String name = user1.getFirstName() +" 43";
        userService.isNameValid(name, "firstName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPhoneValidWrongValueTest(){
        userService.isPhoneValid("f23456789");
    }

    @Test
    public void isPhoneValidTest(){
        userService.isPhoneValid("987654321");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isEmailValidWrongValueTest(){
        userService.isEmailValid("vas.09gmail.com");
    }

    @Test
    public void isEmailValid(){
        userService.isEmailValid("vas.0910@gmail.com");
    }


    private com.netcracker.testerritto.models.Test getNewTest() {
        List<GradeCategory> grades = new ArrayList<>();
        List<User> experts = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        return new com.netcracker.testerritto.models.Test(null, group1Id, "JustTest", user1Id,
            grades, experts, questions);
    }


}
