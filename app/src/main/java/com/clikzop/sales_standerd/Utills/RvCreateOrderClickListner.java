package com.clikzop.sales_standerd.Utills;

import com.clikzop.sales_standerd.Model.MultipleProductBean;

import java.util.List;

public interface RvCreateOrderClickListner {
    void clickPos(List<MultipleProductBean> list, int id);
}
