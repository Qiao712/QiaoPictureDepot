package com.qiao.picturedepot.pojo;

import com.qiao.picturedepot.util.PageHelper;

import java.math.BigInteger;
import java.util.List;

public class PageResponse {
    private BigInteger pageNo;
    private BigInteger itemNum;
    private BigInteger pageCount;
    private Integer pageSize;
    private List<?> itemsOfCurrentPage;

    public PageResponse(BigInteger pageNo, BigInteger itemNum, Integer pageSize, List<?> itemsOfCurrentPage) {
        this.pageNo = pageNo;
        this.itemNum = itemNum;
        this.pageSize = pageSize;
        this.itemsOfCurrentPage = itemsOfCurrentPage;

        this.pageCount = PageHelper.getPageCount(itemNum, pageSize);
    }

    public BigInteger getPageNo() {
        return pageNo;
    }

    public void setPageNo(BigInteger pageNo) {
        this.pageNo = pageNo;
    }

    public BigInteger getItemNum() {
        return itemNum;
    }

    public void setItemNum(BigInteger itemNum) {
        this.itemNum = itemNum;
    }

    public BigInteger getpageCount() {
        return pageCount;
    }

    public void setpageCount(BigInteger pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<?> getItemsOfCurrentPage() {
        return itemsOfCurrentPage;
    }

    public void setItemsOfCurrentPage(List<?> itemsOfCurrentPage) {
        this.itemsOfCurrentPage = itemsOfCurrentPage;
    }
}
