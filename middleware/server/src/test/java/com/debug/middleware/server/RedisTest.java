package com.debug.middleware.server;

import com.debug.middleware.server.entity.Person;
import com.debug.middleware.server.entity.PhoneUser;
import com.debug.middleware.server.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {

    private static final Logger logger = LoggerFactory.getLogger(RedisTest.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void test01(){
        final String key = "redis:template:one:string";
        final String content = "redisTemplate测试";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key,content);
        Object o = valueOperations.get(key);
        System.out.println(o);
    }

    @Test
    public void test02() throws IOException {
        final String key = "redis:template:two:object";
        User user = new User(1, "zhangsan", "张三");
        String content = objectMapper.writeValueAsString(user);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key,content);
        Object o = valueOperations.get(key);
        User user1 = objectMapper.readValue(o.toString(), User.class);
        logger.info("content:{}",user1);
    }

    @Test
    public void test03(){
        final String key = "redis:template:three:list";
        List<Person> list=new ArrayList<>();
        list.add(new Person(1,21,"修罗", "debug", "火星"));
        list.add(new Person(2,22,"大圣","jack","水帘洞"));
        list.add(new Person(3,23,"盘古","Lee","上古"));
        //先进先出
        ListOperations listOperations = redisTemplate.opsForList();
        for (Person person : list) {
            listOperations.leftPush(key,person);
        }
        Long size = listOperations.size(key);
        while (size-->0){
            System.out.println(listOperations.rightPop(key));
        }
    }

    @Test
    public void test04(){
        final String key = "redis:template:04:zset";
        redisTemplate.delete(key);
        List<PhoneUser> list = new ArrayList<>();
        list.add(new PhoneUser("103", 130.0));
        list.add(new PhoneUser("101", 120.0));
        list.add(new PhoneUser("102", 80.0));
        list.add(new PhoneUser("105", 70.0));
        list.add(new PhoneUser("106", 50.0));
        list.add(new PhoneUser("104", 150.0));
        logger.info("构造一组无序的用户手机充值对象列表:{}", list);
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        for (PhoneUser phoneUser : list) {
            zSetOperations.add(key,phoneUser,phoneUser.getFare());
        }
        Set<PhoneUser> range = zSetOperations.range(key, 0, zSetOperations.size(key));
        for (PhoneUser phoneUser : range) {
            System.out.print(phoneUser+" ");
        }
        System.out.println();
        Set<PhoneUser> reverseRange = zSetOperations.reverseRange(key, 0, zSetOperations.size(key));
        for (PhoneUser phoneUser : reverseRange) {
            System.err.print(phoneUser+" ");
        }
    }

    @Test
    public void testDemo(){
        System.out.println("hehehe");
    }
}
