package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.apis.AccountFeignApi;
import com.atguigu.cloud.apis.StorageFeignApi;
import com.atguigu.cloud.entities.Order;
import com.atguigu.cloud.mapper.OrderMapper;
import com.atguigu.cloud.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private StorageFeignApi storageFeignApi;
    @Resource
    private AccountFeignApi accountFeignApi;

    @Override
    /**
     * 分布式事务注解，name指定事务名称，rollbackFor指定出现何种异常时回滚
     */
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public void create(Order order) {
        String xid = RootContext.getXID();
        log.info("==================>开始新建订单" + "\t" + "xid_order:" + xid);
        order.setStatus(0);
        int result = orderMapper.insertSelective(order);
        Order orderFromDB = null;
        if (result > 0) {
            orderFromDB = orderMapper.selectOne(order);
            log.info("-------> 新建订单成功，orderFromDB info: " + orderFromDB);
            System.out.println();
            log.info("-------> 订单微服务开始调用Storage库存，做扣减count");
            storageFeignApi.decrease(orderFromDB.getProductId(), orderFromDB.getCount());
            log.info("-------> 订单微服务结束调用Storage库存，做扣减完成");
            System.out.println();
            //3. 扣减账号余额
            log.info("-------> 订单微服务开始调用Account账号，做扣减money");
            accountFeignApi.decrease(orderFromDB.getUserId(), orderFromDB.getMoney());
            log.info("-------> 订单微服务结束调用Account账号，做扣减完成");
            System.out.println();
            //4. 修改订单状态
            //订单状态status：0：创建中；1：已完结
            log.info("-------> 修改订单状态");
            orderFromDB.setStatus(1);
            Example whereCondition = new Example(Order.class);
            Example.Criteria criteria = whereCondition.createCriteria();
            criteria.andEqualTo("userId", orderFromDB.getUserId());
            criteria.andEqualTo("status", "0");
            int updateResult = orderMapper.updateByExampleSelective(orderFromDB, whereCondition);
            log.info("-------> 修改订单状态完成" + "\t" + updateResult);
            log.info("-------> orderFromDB info: " + orderFromDB);
        }
        System.out.println();
        log.info("==================>结束新建订单" + "\t" + "xid_order:" + xid);
    }
}
