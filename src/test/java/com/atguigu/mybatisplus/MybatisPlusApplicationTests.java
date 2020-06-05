package com.atguigu.mybatisplus;

import com.atguigu.mybatisplus.entity.User;
import com.atguigu.mybatisplus.mapper.UserMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MybatisPlusApplicationTests {
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取到所有的用户数据
     */
    @Test
    public void contextLoads() {
        List<User> users = userMapper.selectList(null);
        users.forEach((user) -> {
            System.out.println(user.toString());
        });
    }

    /**
     * 添加用户
     */
    @Test
    public void addUser() {
        User user = new User();
        user.setName("mary");
        user.setAge(19);
        user.setEmail("mary@136.com");
        int insert = userMapper.insert(user);
        System.out.println(insert);
    }

    /**
     * 更新测试
     */
    @Test
    public void updateUser() {
        User user = new User();
        user.setId(1268334627295088641L);
        user.setEmail("lilei@136.com");
        user.setAge(120);
        int i = userMapper.updateById(user);
        System.out.println(i);
    }

    /**
     * 乐观锁的测试 成功
     */
    @Test
    public void testOptimisticLocker() {
        User user = userMapper.selectById(1L);
        user.setEmail("one@163.com");
        user.setName("one");
        int i = userMapper.updateById(user);
        System.out.println(i);
    }

    /**
     * 乐观锁的测试 失败
     */
    @Test
    public void testOptimisticLockerFail() {

        //查询
        User user = userMapper.selectById(1L);
        user.setName("Helen Yao1");
        user.setEmail("helen@qq.com1");
        //模拟取出数据后，数据库中version实际数据比取出的值大，即已被其它线程修改并更新了version
        user.setVersion(user.getVersion() - 1);
        //执行更新
        userMapper.updateById(user);
    }

    /**
     * 根据id查询记录
     */
    @Test
    public void testSelctUserById() {
        User user = userMapper.selectById(1L);
        System.out.println(user.toString());
    }

    /**
     * 通过多个id批量查询
     */
    @Test
    public void testSelectBatchIds() {
        List<User> users = userMapper.selectBatchIds(Arrays.asList(1, 2, 3));
        users.forEach(System.out::println);

    }

    /**
     * 简单的条件查询
     */
    @Test
    public void testSelectByMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "lilei");
        map.put("age", 120);
        List<User> users = userMapper.selectByMap(map);
        users.forEach(System.out::println);
    }

    /**
     * 算法，字符串反转
     */
    @Test
    public void test() {
        String A = "abcde", B = "cdeab";
        if (A.length() != B.length())
            System.out.println("false");
        if (A.length() == 0)
            System.out.println("length==0" + true);
        search:
        for (int s = 0; s < A.length(); ++s) {
            for (int i = 0; i < A.length(); ++i) {
                char a = A.charAt((s + i) % A.length());
                char b = B.charAt(i);
                if (a != b)
                    continue search;
            }
            System.out.println("判读过后的" + true);
        }
        System.out.println("false");
    }

    /**
     * 测试selectPage分页
     */
    @Test
    public void testSelectPage() {
        // SELECT id,name,age,email,create_time,update_time,version FROM user LIMIT 0,10
        Page<User> page = new Page<>(1, 10);
        userMapper.selectPage(page, null);
        page.getRecords().forEach(System.out::println);
        System.out.println("getCurrent" + page.getCurrent());

        System.out.println("pages" + page.getPages());

        System.out.println("size" + page.getSize());

        System.out.println("total" + page.getTotal());
        System.out.println("next" + page.hasNext());

        System.out.println("previous" + page.hasPrevious());
    }

    /**
     * 测试selectMapsPage分页：结果集是Map
     */
    @Test
    public void testSelectMapsPage() {
        Page<User> page = new Page<>(1, 5);
        IPage<Map<String, Object>> mapIPage = userMapper.selectMapsPage(page, null);
        //注意：此行必须使用 mapIPage 获取记录列表，否则会有数据类型转换错误
        System.out.println("///////////////////////////////////////");
        mapIPage.getRecords().forEach(System.out::println);
        System.out.println(page.getCurrent());
        System.out.println(page.getPages());
        System.out.println(page.getSize());
        System.out.println(page.getTotal());
        System.out.println(page.hasNext());
        System.out.println(page.hasPrevious());
    }

    /**
     * 根据id删除
     */
    @Test
    public void delUseuById() {
        int i = userMapper.deleteById(1L);
        System.out.println(i + "删除成功");
    }

    /**
     * 评量删除根据id
     */
    @Test
    public void testDeleteBatchIds() {
        int i = userMapper.deleteBatchIds(Arrays.asList(1268322173622173697L, 1268331216940081154L, 5L));
        System.out.println(i + "删除成功");
    }

    /**
     * 简单的条件查询删除
     */
    @Test
    public void testDeleteByMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "lilei");
        map.put("age", "120");
        int i = userMapper.deleteByMap(map);
        System.out.println(i + "=删除成功");
    }

    @Test
    public void testLogicDelete() {
        int result = userMapper.deleteById(1268360234951278594L);
        System.out.println(result);
    }
    /**
     * 测试 逻辑删除后的查询：
     * 不包括被逻辑删除的记录
     */
    @Test
    public void testLogicDeleteSelect(){
        //SELECT id,name,age,email,create_time,update_time,version,deleted FROM user WHERE deleted=0
        //
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }

}
