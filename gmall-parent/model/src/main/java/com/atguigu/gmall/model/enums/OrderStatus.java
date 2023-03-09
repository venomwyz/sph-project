package com.atguigu.gmall.model.enums;

public enum OrderStatus {
    UNPAID("未支付"),
    PAID("已支付" ),
    WAITING_DELEVER("待发货"),
    DELEVERED("已发货"),
    CLOSED("已关闭"),
    FINISHED("已完结") ,
    ACT_CANCLE("主动取消"),
    TIMEOUT_CANCLE("超时取消"),
    SPLIT("订单已拆分");


    private String comment ;

    public static String getStatusNameByStatus(String status) {
        OrderStatus arrObj[] = OrderStatus.values();
        for (OrderStatus obj : arrObj) {
            if (obj.name().equals(status)) {
                return obj.getComment();
            }
        }
        return "";
    }

    OrderStatus(String comment ){
        this.comment=comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
