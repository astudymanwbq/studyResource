package com.wbq.spring.service.impl;

import com.wbq.spring.RedPacket;
import com.wbq.spring.UserRedPacket;
import com.wbq.spring.dao.RedPacketDao;
import com.wbq.spring.dao.UserRedPacketDao;
import com.wbq.spring.service.RedisRedPacketService;
import com.wbq.spring.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;


/**
 * @author 吴璧钦
 * @date 2018/6/22 11:25
 * @Description
 **/
@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {

    private final UserRedPacketDao userRedPacketDao;
    private final RedPacketDao redPacketDao;
    //失败
    private static final int FAILED = 0;

    @Autowired
    public UserRedPacketServiceImpl(UserRedPacketDao userRedPacketDao, RedPacketDao redPacketDao, RedisTemplate redisTemplate, RedisRedPacketService redisRedPacketService) {
        this.userRedPacketDao = userRedPacketDao;
        this.redPacketDao = redPacketDao;
        this.redisTemplate = redisTemplate;
        this.redisRedPacketService = redisRedPacketService;
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacket(Long redPacketId, Long userId) {
        //获取红包信息
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        //库存大于0
        if (redPacket.getStock() > 0) {
            redPacketDao.decreaseRedPacket(redPacketId);
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("抢红包: " + redPacketId);
            //插入抢红包信息
            return userRedPacketDao.grabRedPacket(userRedPacket);
        }
        return FAILED;
    }

    private final RedisTemplate redisTemplate;
    private final RedisRedPacketService redisRedPacketService;
    //保存返回的sha1编码
    private String sha1 = null;

    @Override
    public Long grabRedPacketByRedis(Long redPacketId, Long userId) {
        //当前抢红包信息和日期信息
        String args = userId + "-" + System.currentTimeMillis();
        Long result;
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        try {
            if (sha1 == null) {
                //lua脚本
                String script = "local listKey = 'red_packet_list_'..KEYS[1] \n"
                        + "local redPacket = 'red_packet_'..KEYS[1] \n"
                        + "local stock = tonumber(redis.call('hget',redPacket,'stock'))\n"
                        + "if stock <= 0 then return 0 end \n"
                        + "stock = stock-1 \n"
                        + "redis.call('hset',redPacket,'stock',tostring(stock)) \n"
                        + "redis.call('rpush',listKey,ARGV[1]) \n"
                        + "if stock == 0 then return 2 end \n"
                        + "return 1\n";
                sha1 = jedis.scriptLoad(script);
            }
            //执行脚本
            Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
            result = (Long) res;
            //最后一个红包
            if (result == 2) {
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
                Double unitAmount = Double.parseDouble(unitAmountStr);
                System.err.println("thread_name " + Thread.currentThread().getName());
                redisRedPacketService.saveUserPacketByRedis(redPacketId, unitAmount);
            }
        } finally {
            if (jedis.isConnected()) {
                jedis.close();
            }
        }
        return result;
    }


}
