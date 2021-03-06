package com.heyuan.gmall.seckill.controller;

import com.heyuan.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.List;

@Controller
public class SecKillController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    /***
     * 先到先得式秒杀 redission
     * @return
     */
    @RequestMapping("secKill")
    @ResponseBody
    public String secKill(){
        Jedis jedis = redisUtil.getJedis();

        RSemaphore semaphore = redissonClient.getSemaphore("106");
        boolean b = semaphore.tryAcquire();

        int stock = Integer.parseInt(jedis.get("106"));
        if(b){
            System.out.println("当前库存剩余数量"+stock+",某用户抢购成功，当前抢购人数："+(1000-stock));
            // 用消息队列发出订单消息
            System.out.println("发出订单的消息队列，由订单系统对当前抢购生成订单");
        }else {
            System.out.println("当前库存剩余数量"+stock+",某用户抢购失败");
        }

        jedis.close();
        return "seckill-index";
    }

    /***
     * 随机拼运气式秒杀 redis
     * @return
     */
    @RequestMapping("kill")
    public String kill(){
//        Jedis jedis = redisUtil.getJedis();
//        // 开启商品的监控
//        jedis.watch("106");//保证数据一致性
//        int stock = Integer.parseInt(jedis.get("106"));
//        if(stock>0){
//            Transaction multi = jedis.multi();//开启事务
//            multi.incrBy("106",-1);//扣减库存
//            List<Object> exec = multi.exec();//执行任务
//            if(exec!=null&&exec.size()>0){
//                System.out.println("当前库存剩余数量"+stock+",某用户抢购成功，当前抢购人数："+(1000-stock));
//                // 用消息队列发出订单消息
//
//            }else {
//                System.out.println("当前库存剩余数量"+stock+",某用户抢购失败");
//            }
//
//        }
//        jedis.close();
        return "seckill-index";
    }
    @GetMapping("seckill-item.html")
    public String seckill_item(){
        return "seckill-item";
    }
}
