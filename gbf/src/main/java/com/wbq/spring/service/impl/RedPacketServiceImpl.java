package com.wbq.spring.service.impl;

import com.wbq.spring.RedPacket;
import com.wbq.spring.dao.RedPacketDao;
import com.wbq.spring.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author 吴璧钦
 * @date 2018/6/22 11:21
 * @Description
 **/
@Service
public class RedPacketServiceImpl implements RedPacketService {

    private final RedPacketDao redPacketDao;

    @Autowired
    public RedPacketServiceImpl(RedPacketDao redPacketDao) {
        this.redPacketDao = redPacketDao;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public RedPacket getRedPacket(Long id) {
        return redPacketDao.getRedPacket(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int decreaseRedPacket(Long id) {
        return redPacketDao.decreaseRedPacket(id);
    }
}
