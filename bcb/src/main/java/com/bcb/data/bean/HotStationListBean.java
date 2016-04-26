package com.bcb.data.bean;

import java.util.List;

public class HotStationListBean {
	public int PageNow;
	public int PageSize;
	public int TotalCount;
	public List<HotStationRecordsBean> Records;
	
	public int getPageNow() {
		return PageNow;
	}
	public void setPageNow(int pageNow) {
		this.PageNow = pageNow;
	}
	
	public int getTotalCount() {
		return TotalCount;
	}
	public void setTotalCount(int totalCount) {
		this.TotalCount = totalCount;
	}
	
	public int getPageSize() {
		return PageSize;
	}
	public void setPageSize(int pageSize) {
		PageSize = pageSize;
	}
	
	public List<HotStationRecordsBean> getRecords() {
		return Records;
	}
	public void setRecords(List<HotStationRecordsBean> Records) {
		this.Records = Records;
	}	
	
}
