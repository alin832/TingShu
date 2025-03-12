package com.atguigu.tingshu.order.api;

import com.atguigu.tingshu.common.login.GuiguLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.order.OrderInfo;
import com.atguigu.tingshu.order.service.OrderInfoService;
import com.atguigu.tingshu.vo.order.OrderInfoVo;
import com.atguigu.tingshu.vo.order.TradeVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "订单管理")
@RestController
@RequestMapping("api/order")
@SuppressWarnings({"all"})
public class OrderInfoApiController {

    @Autowired
    private OrderInfoService orderInfoService;



    /**
     * api/order/orderInfo/orderPaySuccess/{orderNo}
     * 支付成功更新订单
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo/orderPaySuccess/{orderNo}")
    public Result orderPaySuccess(@PathVariable String orderNo){

        orderInfoService.orderPaySuccess(orderNo);

        return Result.ok();
    }



    /**
     * 分页查询我的订单列表
     * api/order/orderInfo/findUserPage
     *
     * @return
     */
    @GetMapping("/orderInfo/findUserPage/{page}/{limit}")
    @GuiguLogin
    public Result<Page<OrderInfo>> findUserPage(@PathVariable Long page,
                                                @PathVariable Long limit) {


        //获取用户ID
        Long userId = AuthContextHolder.getUserId();
        //封装分页对象
        Page<OrderInfo> orderInfoPage = new Page<>(page,limit);
        //调用service
        orderInfoPage= orderInfoService.findUserPage(orderInfoPage,userId);

        return Result.ok(orderInfoPage);
    }


    /**
     * 根据订单号获取订单相关信息
     * api/order/orderInfo/getOrderInfo/{orderNo}
     *
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo/getOrderInfo/{orderNo}")
    @GuiguLogin
    public Result<OrderInfo> getOrderInfo(@PathVariable String orderNo) {

        //获取用户ID
        Long userId = AuthContextHolder.getUserId();
        //查询我的订单
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderNo, userId);


        return Result.ok(orderInfo);
    }


    /**
     * 提交订单
     * api/order/orderInfo/submitOrder
     *
     * @param orderInfoVo
     * @return
     */
    @PostMapping("/orderInfo/submitOrder")
    @GuiguLogin
    public Result<Map<String, String>> submitOrder(@RequestBody OrderInfoVo orderInfoVo) {

        //获取用户ID
        Long userId = AuthContextHolder.getUserId();
        //保存订单
        Map<String, String> resultMap = orderInfoService.submitOrder(orderInfoVo, userId);


        return Result.ok(resultMap);

    }


    /**
     * 订单确认
     * api/order/orderInfo/trade
     *
     * @param tradeVo
     * @return
     */
    @PostMapping("/orderInfo/trade")
    @GuiguLogin
    public Result<OrderInfoVo> tradeData(@RequestBody TradeVo tradeVo) {


        OrderInfoVo orderInfoVo = orderInfoService.tradeData(tradeVo);


        return Result.ok(orderInfoVo);
    }

}

