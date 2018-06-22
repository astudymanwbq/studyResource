package com.wbq.spring.service.impl;

import com.wbq.spring.UserRedPacket;
import com.wbq.spring.service.RedisRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 吴璧钦
 * @date 2018/6/22 16:58
 * @Description
 **/
@Service
public class RedisRedPacketServiceImpl implements RedisRedPacketService {
    private static final String PREFIX = "red_packet_list_";
    //每次取出1000条
    private static final int TIME_SIZE = 1000;

    private final RedisTemplate redisTemplate;

    private final DataSource dataSource;

    @Autowired
    public RedisRedPacketServiceImpl(RedisTemplate redisTemplate, @Qualifier("dataSource") DataSource dataSource) {
        this.redisTemplate = redisTemplate;
        this.dataSource = dataSource;
    }

    @Override
    @Async//开启新线程执行
    public void saveUserPacketByRedis(Long redPacketId, Double unitAmount) {
        System.out.println("开始保存数据");
        Long start = System.currentTimeMillis();
        //获取列表操作对象
        BoundListOperations ops = redisTemplate.boundListOps(PREFIX + redPacketId);
        Long size = ops.size();
        Long times = size % TIME_SIZE == 0 ? size / TIME_SIZE : size / TIME_SIZE + 1;
        int count = 0;
        List<UserRedPacket> userRedPackets = new ArrayList<>(TIME_SIZE);
        for (int i = 0; i < times; i++) {
            List userIdList;
            if (i == 0) {
                userIdList = ops.range(i * TIME_SIZE, (i + 1) * TIME_SIZE);
            } else {
                userIdList = ops.range(i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
            }
            userRedPackets.clear();
            //保存红包信息
            for (int j = 0; j < userIdList.size(); j++) {
                String args = userIdList.get(j).toString();
                String[] arr = args.split("-");
                String userIdStr = arr[0];
                String timeStr = arr[1];
                Long userId = Long.parseLong(userIdStr);
                Long time = Long.parseLong(timeStr);
                //生成抢红包信息
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setUserId(userId);
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setAmount(unitAmount);
                userRedPacket.setGrabTime(new Timestamp(time));
                userRedPacket.setNote("抢红包 " + redPacketId);
                userRedPackets.add(userRedPacket);
            }
            //插入抢红包信息
            count += executeBatch(userRedPackets);
        }
        //删除redis列表
        redisTemplate.delete(PREFIX + redPacketId);
        Long end = System.currentTimeMillis();
        System.out.println("保存数据结束 耗时 " + (end - start) + " 毫秒，共" + count + "条记录被保存");
    }

    /**
     * 使用jdbc处理redis缓存记录
     *
     * @param userRedPackets
     * @return
     */
    private int executeBatch(List<UserRedPacket> userRedPackets) {
        Connection con = null;
        Statement stat;
        int[] count;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            stat = con.createStatement();
            for (UserRedPacket userRedPacket : userRedPackets) {
                String sql = "update t_red_packet set stock=stock-1 where id=" + userRedPacket.getRedPacketId();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sql1 = "insert into t_user_red_packet(red_packet_id,user_id,amount,grab_time,note)" +
                        "values(" + userRedPacket.getRedPacketId() + "," + userRedPacket.getUserId() + "," +
                        userRedPacket.getAmount() + ",'" + df.format(userRedPacket.getGrabTime()) + "'," + "'"
                        + userRedPacket.getNote() + "')";
                stat.addBatch(sql);
                stat.addBatch(sql1);
            }
            //执行批量
            count = stat.executeBatch();
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("抢红包出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //放回抢红包数据记录
        return count.length / 2;
    }
}
