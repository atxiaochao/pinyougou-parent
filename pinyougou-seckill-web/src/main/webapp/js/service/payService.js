app.service("payService",function ($http) {
    //二维码
    this.createNative = function () {
        return $http.get("pay/createNative.do");
    }

    //查询订单支付状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get("pay/queryPayStatus.do?out_trade_no="+out_trade_no);
    }

})