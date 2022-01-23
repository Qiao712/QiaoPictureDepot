package com.qiao.picturedepot.util;

import java.math.BigInteger;

public class PageHelper {
    public static BigInteger getPageCount(BigInteger count, int pageSize){
        //(count + pageSize - 1) / pageSize
        return count.add(BigInteger.valueOf(pageSize - 1)).divide(BigInteger.valueOf(pageSize));
    }

    public static BigInteger getStart(BigInteger pageNo, int pageSize){
        //(pageNo - 1)*pageSize
        return pageNo.subtract(BigInteger.valueOf(1)).multiply(BigInteger.valueOf(pageSize));
    }
}
