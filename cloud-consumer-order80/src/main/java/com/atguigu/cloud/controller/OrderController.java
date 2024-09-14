package com.atguigu.cloud.controller;

import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {
    //private static final String PaymentSerUrl = "http://localhost:8001";
    private static final String PaymentSerUrl = "http://cloud-payment-service";
    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/pay/add")
    public ResultData addPay(PayDTO payDTO) {
        return restTemplate.postForObject(PaymentSerUrl + "/pay/add", payDTO, ResultData.class);
    }

    @GetMapping("/consumer/pay/del/{id}")
    public ResultData delPay(@PathVariable("id") Integer id) {
        return restTemplate.exchange(PaymentSerUrl + "/pay/del/" + id, HttpMethod.DELETE, null, ResultData.class).getBody();
    }

    @GetMapping("/consumer/pay/update")
    public ResultData updatePay(PayDTO payDTO) {
        HttpEntity<PayDTO> requestEntity = new HttpEntity<>(payDTO, new HttpHeaders());
        return restTemplate.exchange(PaymentSerUrl + "/pay/update", HttpMethod.PUT, requestEntity, ResultData.class).getBody();
    }

    @GetMapping("/consumer/pay/get/{id}")
    public ResultData getPay(@PathVariable("id") Integer id) {
        return restTemplate.getForObject(PaymentSerUrl + "/pay/get/" + id, ResultData.class, id);
    }

    @GetMapping(value = "")
    private String getInfoByConsul()
    {
        return restTemplate.getForObject(PaymentSerUrl + "/pay/get/info", String.class);
    }
}
