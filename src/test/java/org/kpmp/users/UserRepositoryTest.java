package org.kpmp.users;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kpmp.packages.TestMongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestMongoConfig.class })
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    public void testFindByEmail() {
        User testUser = new User();
        testUser.setEmail("jimminy@cricket.com");
        userRepository.save(testUser);
        User user = userRepository.findByEmail("jimminy@cricket.com");
        assertEquals(testUser.getEmail(), user.getEmail());
    }
}
