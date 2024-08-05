package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.dto.UserDto;
import com.tamarwell.expense_tracker.entity.Role;
import com.tamarwell.expense_tracker.entity.User;
import com.tamarwell.expense_tracker.exception.EmailAlreadyRegisteredException;
import com.tamarwell.expense_tracker.exception.UserNotFoundException;
import com.tamarwell.expense_tracker.exception.UsernameAlreadyTakenException;
import com.tamarwell.expense_tracker.exception.WeakPasswordException;
import com.tamarwell.expense_tracker.repository.RoleRepository;
import com.tamarwell.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User normalUser;

    private UserDto userDto;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setId(1L);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setId(2L);

        Set<Role> adminRolesList = new HashSet<>();
        adminRolesList.add(adminRole);
        adminRolesList.add(userRole);

        Set<Role> userRolesList = new HashSet<>();
        adminRolesList.add(userRole);


        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("TestUsername");
        adminUser.setEmail("test@test.com");
        adminUser.setRoles(adminRolesList);

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername("TestUsername1");
        normalUser.setEmail("test1@test.com");
        normalUser.setPassword("encrypted password");
        normalUser.setLastName("Last");
        normalUser.setFirstName("First");
        normalUser.setRoles(userRolesList);

        userDto = new UserDto();
        userDto.setEmail("test1@test.com");
        userDto.setUsername("TestUsername1");
        userDto.setPassword("password");
        userDto.setLastName("Last");
        userDto.setFirstName("First");
    }

    @Test
    public void testGetAllUsers(){
        List<User> userList = new ArrayList<>();
        userList.add(adminUser);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserRolesById(){
        List<String> adminRoleNameList = new ArrayList<>();
        adminRoleNameList.add("ROLE_ADMIN");
        adminRoleNameList.add("ROLE_USER");

        List<String> userRoleNameList = new ArrayList<>();
        userRoleNameList.add("ROLE_USER");

        when(userRepository.findRoleNamesById(normalUser.getId())).thenReturn(userRoleNameList);
        when(userRepository.findRoleNamesById(adminUser.getId())).thenReturn(adminRoleNameList);

        List<String> adminRoleResult = userService.getUserRolesById(adminUser.getId());
        List<String> userRoleResult = userService.getUserRolesById(normalUser.getId());

        assertEquals(adminRoleResult, adminRoleNameList);
        assertEquals(userRoleResult, userRoleNameList);
        verify(userRepository, times(1)).findRoleNamesById(1L);
        verify(userRepository, times(1)).findRoleNamesById(2L);
    }

    @Test
    public void testFindByUsername(){
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));

        Optional<User> result = userService.findByUsername(adminUser.getUsername());

        assertTrue(result.isPresent());
        assertEquals(adminUser, result.get());
        verify(userRepository, times(1)).findByUsername(adminUser.getUsername());
    }

    @Test
    public void testFindByEmail(){
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));

        Optional<User> result = userService.findByEmail(adminUser.getEmail());

        assertTrue(result.isPresent());
        assertEquals(adminUser, result.get());
        verify(userRepository, times(1)).findByEmail(adminUser.getEmail());
    }

    @Test
    public void testFindById(){
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        Optional<User> result = userService.findById(adminUser.getId());

        assertTrue(result.isPresent());
        assertEquals(adminUser, result.get());
        verify(userRepository, times(1)).findById(adminUser.getId());
    }

    @Test
    public void testDeleteUserByIdFound(){
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        userService.deleteUserById(adminUser.getId());

        verify(userRepository, times(1)).deleteUserById(adminUser.getId());
    }

    @Test
    public void testDeleteUserByIdNotFound(){
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.empty());

        verify(userRepository, times(0)).deleteUserById(adminUser.getId());
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUserById(adminUser.getId()));
    }

    @Test
    public void testDeleteUserByUsernameFound(){
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));

        userService.deleteUserByUsername(adminUser.getUsername());

        verify(userRepository, times(1)).deleteUserByUsername(adminUser.getUsername());
    }

    @Test
    public void testDeleteUserByUsernameNotFound(){
        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.empty());

        verify(userRepository, times(0)).deleteUserByUsername(adminUser.getUsername());
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUserByUsername(adminUser.getUsername()));
    }

    @Test
    public void testDeleteUserByEmailFound(){
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));

        userService.deleteUserByEmail(adminUser.getEmail());

        verify(userRepository, times(1)).deleteUserByEmail(adminUser.getEmail());
    }

    @Test
    public void testDeleteUserByEmailNotFound(){
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.empty());

        verify(userRepository, times(0)).deleteUserByEmail(adminUser.getEmail());
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUserByEmail(adminUser.getEmail()));
    }

    @Test
    public void testDeleteAllUsers(){
        userService.deleteAllUsers();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    public void testRegisterUserSuccess(){
        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encrypted password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(userDto);
        System.out.println("UT: " + normalUser);

        assertNotNull(result);
        assertEquals(normalUser.getUsername(), result.getUsername());
        assertEquals(normalUser.getPassword(), result.getPassword());
        assertEquals(normalUser.getEmail(), result.getEmail());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(userRole));

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findByUsername(userDto.getUsername());
        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verify(passwordEncoder, times(1)).encode(userDto.getPassword());
    }

    @Test
    public void testRegisterUserRoleNotFound(){
        when(roleRepository.findByName("ROLE_USER")).thenReturn(null);

        Exception e = assertThrows(RuntimeException.class, () ->
                userService.registerUser(userDto));
        assertEquals("Default role not found", e.getMessage());
    }

    @Test
    public void testRegisterUserNameAlreadyTaken(){
        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(normalUser));


        Exception e = assertThrows(UsernameAlreadyTakenException.class, () ->
                userService.registerUser(userDto));
        assertEquals("Username already taken", e.getMessage());
    }

    @Test
    public void testRegisterUserEmailAlreadyTaken(){
        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(normalUser));

        Exception e = assertThrows(EmailAlreadyRegisteredException.class, () ->
                userService.registerUser(userDto));
        assertEquals("Email already registered", e.getMessage());
    }

    @Test
    public void testRegisterUserPasswordTooShort(){
        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        UserDto userDtoPasswordTooShort = new UserDto();
        userDtoPasswordTooShort.setEmail("test1@test.com");
        userDtoPasswordTooShort.setUsername("TestUsername1");
        userDtoPasswordTooShort.setPassword("pswrd");
        userDtoPasswordTooShort.setLastName("Last");
        userDtoPasswordTooShort.setFirstName("First");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(userRepository.findByUsername(userDtoPasswordTooShort.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDtoPasswordTooShort.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDtoPasswordTooShort.getPassword())).thenReturn("encrypted password");


        Exception e = assertThrows(WeakPasswordException.class, () ->
                userService.registerUser(userDtoPasswordTooShort));
        assertEquals("Password should be at least 6 characters long", e.getMessage());
    }

    @Test
    public void testUpdateUserSuccess(){

       // User result = userService.updateUser(userDto, normalUser.getId());
    }
}
