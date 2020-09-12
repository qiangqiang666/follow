package com.monkey.follow.okex.bean.other;

public interface OrderBookItem<T> {
    String getPrice();

    T getSize();
}
