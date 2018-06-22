package com.wbq.spring.dao;

import com.wbq.spring.RedPacket;
import com.wbq.spring.config.RootConfig;
import com.wbq.spring.config.WebAppInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author 吴璧钦
 * @date 2018/6/22 15:03
 * @Description
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppInitializer.class,RootConfig.class})
public class RedPacketDaoTest {

    @Resource
    private RedPacketDao redPacketDao;

    @Test
    public void getRedPacket() {
        RedPacket redPacket=redPacketDao.getRedPacket(1L);
        System.out.println(redPacket.toString());
    }
}