package com.nowcoder.lyy.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    public static void print(int index,Object obj){
        System.out.println(String.format("%d,%s",index,obj.toString()));
    }

    public static void main(String[] args){
        Jedis jedis = new Jedis();
        jedis.flushAll();  //把所有的数据库都删掉
      //  jedis.flushAll();  //把当前的数据库都删掉

        // get,set
        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","newhello");
        print(1,jedis.get("newhello"));
        jedis.setex("hello2",15,"world");  //设置过期时间，在验证码的时候就可以用到

        // 数值操作,
        jedis.set("pv","100");
        jedis.incr("pv");   //设置pv每次增加1
        print(2,jedis.get("pv"));
        jedis.incrBy("pv",5); //设置pv每次增加5
        jedis.incrBy("pv",7); //设置pv每次增加7
        print(2,jedis.get("pv"));

        //列表操作, 最近来访, 粉丝列表，消息队列
        String listName = "listA";
        for (int i=0;i<10;++i){
            jedis.lpush(listName,"a"+String.valueOf(i));
        }
        print(3,jedis.lrange(listName,0,12));  //返回列表中指定区间内的元素
        print(4,jedis.llen(listName));   //返回列表的长度
        print(5,jedis.lpop(listName));   //弹出列表的值
        print(6,jedis.llen(listName));
        print(7,jedis.lindex(listName,3));    //返回列表指定索引的值
        print(8,jedis.linsert(listName,BinaryClient.LIST_POSITION.AFTER,"a4","xx"));  //在a4后面插入xx
        print(9,jedis.linsert(listName,BinaryClient.LIST_POSITION.BEFORE,"a4","bb")); //在a4前面插入bb
        print(10,jedis.lrange(listName,0,12));

        // hash, 可变字段
        String userKey = "userxx";
        jedis.hset(userKey,"name","jim");
        jedis.hset(userKey,"age","12");
        jedis.hset(userKey,"phone","15102747860");

        print(12,jedis.hget(userKey,"name"));  //获取name
        print(13,jedis.hgetAll(userKey ));   //获取所有的字段
        jedis.hdel(userKey,"phone");  //删除phone
        print(14,jedis.hgetAll(userKey));
        print(15,jedis.hkeys(userKey));   //获取所有的key
        print(16,jedis.hvals(userKey));     //获取所有的value
        print(17,jedis.hexists(userKey,"email"));  //判断是否存在email
        print(17,jedis.hexists(userKey,"age"));  //判断是否存在age
        jedis.hsetnx(userKey,"school","zjz");
        jedis.hsetnx(userKey,"name","sys");  //如果存在就不更新，不存在就设置

        // set集合，点赞用户群, 共同好友
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for(int i =0 ;i<10;++i){
            jedis.sadd(likeKeys1,String.valueOf(i));
            jedis.sadd(likeKeys2,String.valueOf(i*2));
        }
        print(20,jedis.smembers(likeKeys1));   //打印所有的字段
        print(21,jedis.smembers(likeKeys2));   //打印所有的字段
        print(22,jedis.sinter(likeKeys1,likeKeys2));   //求交集
        print(23,jedis.sunion(likeKeys1,likeKeys2));   //求并集
        print(24,jedis.sdiff(likeKeys1,likeKeys2));   //求不同的
        print(25,jedis.sismember(likeKeys1,"5"));//判断集合likeKeys1中是否有5
        jedis.srem(likeKeys1,"5");   //把集合likeKeys1中的5删掉
        print(26,jedis.smembers(likeKeys1)); //打印所有的字段
        jedis.smove(likeKeys2,likeKeys1,"14"); //把14从likeKeys2移到likeKeys1
        print(28,jedis.scard(likeKeys1));  //查看该集合有多少个值

        // 排序集合，有限队列，排行榜
        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"jim");
        jedis.zadd(rankKey,60,"Ben");
        jedis.zadd(rankKey,90,"Lee");
        jedis.zadd(rankKey,80,"Mei");
        jedis.zadd(rankKey,75,"Luck");
        print(30,jedis.zcard(rankKey));    //查看有多少人
        print(31,jedis.zcount(rankKey,61,100)); //查看在60-100这个区间有多少人
        print(32,jedis.zscore(rankKey,"Lucy"));  //查看lucy的分值
        jedis.zincrby(rankKey,2,"lucy");  //给lucy增加2分
        print(33,jedis.zscore(rankKey,"Lucy"));  //查看lucy的分值
        jedis.zincrby(rankKey,2,"luc");  //没有luc,添加luc,给luc2分
        print(34,jedis.zrange(rankKey,1,3)); //在rankKey的1到3名，从小到大排序
        print(35,jedis.zrange(rankKey,1,3)); //在rankKey的1到3名，从大到小排序
        //打印出来60到100分之间的数据
        for(Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,"60","100")){
            print(36,tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        print(37,jedis.zrank(rankKey,"Ben"));  //顺序时Ben的排序，下标从0开始
        print(38,jedis.zrevrank(rankKey,"Ben"));  //逆序时Ben的排序，下标从0开始

        JedisPool pool = new JedisPool();
        for(int i =0;i<100;++i){
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            j.close();      //用完释放
        }


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost",6379)
    }
    private Jedis getJedis(){
        return pool.getResource();
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return getJedis().get(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
