package com.atguigu.cloud.apis;

import com.atguigu.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @auther zzyy
 * @create 2023-12-01 17:41
 */

/**
 * 库存微服务模块暴露的feign接口
 */
@FeignClient(value = "seata-storage-service")
public interface StorageFeignApi {
    /**
     * 扣减库存
     */
    @PostMapping(value = "/storage/decrease")
    ResultData decrease(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);
}

