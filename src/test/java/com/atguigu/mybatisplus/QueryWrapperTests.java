package com.atguigu.mybatisplus;

import com.atguigu.mybatisplus.entity.User;
import com.atguigu.mybatisplus.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryWrapperTests {
    @Autowired
    private UserMapper userMapper;

    /**
     * 使用QueryWrapper封装数据删除数据中的数据
     */
    @Test
    public void testDelete() {
        //UPDATE user SET deleted=1 WHERE deleted=0 AND name IS NULL AND age >= ? AND email IS NOT NULL
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("name").ge("age", 12).isNotNull("email");
        int result = userMapper.delete(queryWrapper);
        System.out.println("delete return count = " + result);
    }

    /**
     * seletOne返回的是一条实体记录，当出现多条时会报错
     */
    @Test
    public void testSelectOne() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", "Tom");
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
    }

    /**
     * 包含大小边界
     * between、notBetween
     */
    @Test
    public void testSelectCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.between("age", 20, 30);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
        Integer count = userMapper.selectCount(queryWrapper);
        System.out.println(count);
    }

    /**
     * allEq 多条件查询
     */
    @Test
    public void testSelectList() {
        //SELECT id,name,age,email,create_time,update_time,version,deleted FROM user WHERE deleted=0 AND name = ? AND id = ? AND age = ?
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", "2");
        map.put("name", "Jack");
        map.put("age", "120");
        queryWrapper.allEq(map);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * like、notLike、likeLeft、likeRight
     */
    @Test
    public void testSelectMaps() {
        // SELECT id,name,age,email,create_time,update_time,version,deleted FROM user WHERE deleted=0 AND name NOT LIKE ? AND email LIKE ?
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.notLike("name", "J");
        queryWrapper.like("email", "t");
        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);
        maps.forEach(System.out::println);
    }

    /**
     * 6、in、notIn、inSql、notinSql、exists、notExists
     * in、notIn：
     * notIn("age",{1,2,3})--->age not in (1,2,3)
     * notIn("age", 1, 2, 3)--->age not in (1,2,3)
     * inSql、notinSql：可以实现子查询
     * 例: inSql("age", "1,2,3,4,5,6")--->age in (1,2,3,4,5,6)
     * 例: inSql("id", "select id from table where id < 3")--->id in (select id from table where id < 3)
     * 实现子查询
     */
    @Test
    public void testSelectObjs() {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // SELECT id,name,age,email,create_time,update_time,version,deleted FROM user WHERE deleted=0 AND id IN (?,?,?,?,?,?)
        queryWrapper.in("id", 1, 2, 3, 4, 5, 6);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 嵌套sql语句实现子查询
     */
    @Test
    public void testSelectObjsLimit() {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.inSql("id", "select id from user where id<3");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * 更新数据
     * 使用的是 UpdateWrapper
     * 不调用or则默认为使用 and 连
     */
    @Test
    public void testUpdate1() {
        //UPDATE user SET name=?, age=?, update_time=? WHERE deleted=0 AND name LIKE ? OR age BETWEEN ? AND ?
        //修改值
        User user = new User();
        user.setAge(18);
        user.setName("Andy");
        //修改条件
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.like("name", "h");
        updateWrapper.or();
        updateWrapper.between("age", 20, 30);
        int update = userMapper.update(user, updateWrapper);
        System.out.println("修改成功：=" + update);
    }

    /**
     * 嵌套or、嵌套and
     * 使用了lambda表达式，or中的表达式最后翻译成sql时会被加上圆括号
     * EQ 就是 EQUAL等于
     * NE 就是 NOT EQUAL不等于
     * GT 就是 GREATER THAN大于
     * LT 就是 LESS THAN小于
     * GE 就是 GREATER THAN OR EQUAL 大于等于
     * LE 就是 LESS THAN OR EQUAL 小于等于
     */
    @Test
    public void testUpdate2() {
        //修改值
        //UPDATE user SET name=?, age=?, update_time=? WHERE deleted=0 AND name LIKE ? OR ( name = ? AND age <> ? )
        User user = new User();
        user.setAge(99);
        user.setName("Andy");
        //修改条件
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.like("name", "A").or(i -> i.eq("name", "李白").ne("age", 20));
        int update = userMapper.update(user, updateWrapper);
        System.out.println("跟新成功：=" + update);
    }

    /**
     * orderBy、orderByDesc、orderByAsc
     * 排序查询
     */
    @Test
    public void testSelectListOrderBy() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::print);
    }

    /**
     * 直接拼接到 sql 的最后
     * 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
     */
    @Test
    public void testSelectListLast() {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.last("limit 1");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::print);
    }

    /**
     * 查询指定列
     */
    @Test
    public void testSelectListColumn(){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("id","name","age");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    /**
     * set、setSql
     * 最终的sql会合并 user.setAge()，以及 userUpdateWrapper.set()  和 setSql() 中 的字段
     */
    @Test
    public void testUpdateSet(){
        // UPDATE user SET age=?, update_time=?, name=?, email = '123@qq.com' WHERE deleted=0 AND name LIKE ?
        User user = new User();
        user.setAge(99);
        UpdateWrapper<User> userUpdateWrapper=new UpdateWrapper<>();
        //userUpdateWrapper.like("name", "h").set("name", "老李头").setSql("  email = '123@qq.com'");
        userUpdateWrapper.like("name", "?").set("name", "JACK") .setSql(" email = '123@qq.com'");//可以有子查询
        int update = userMapper.update(user, userUpdateWrapper);
        System.out.println(update);
    }

}
