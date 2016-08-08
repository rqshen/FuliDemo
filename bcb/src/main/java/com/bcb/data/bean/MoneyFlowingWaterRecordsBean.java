package com.bcb.data.bean;

public class MoneyFlowingWaterRecordsBean {
    private String BillId;
    private String TransNo;
	private String Title;
	private float Amount;//float
	private String Status;
	private String Descn;
    private String StatusDescn;
	private String Time;
	private String CategoryName;
	private int Type;
    private int TopCategoryId;  //交易类型
    private int StatusGroup;    //交易分组，表示是否成功还是失败


    public String getBillId() {
        return BillId;
    }

    public void setBillId(String billId) {
        BillId = billId;
    }

    public String getTransNo() {
		return TransNo;
	}

	public void setTransNo(String transNo) {
		TransNo = transNo;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public float getAmount() {
		return Amount;
	}

	public void setAmount(float amount) {
		Amount = amount;
	}

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
	public String getTime() {
        Time = Time.substring(0, 5).replace('-','年')+ Time.substring(5, 10).replace('-', '月')+ "日";
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}

	public int getType() {
		return Type;
	}


	public void setType(int type) {
		Type = type;
	}

    public int getTopCategoryId() {
        return TopCategoryId;
    }

    public void setTopCategoryId(int topCategoryId) {
        TopCategoryId = topCategoryId;
    }

    public int getStatusGroup() {
        return StatusGroup;
    }

    public void setStatusGroup(int statusGroup) {
        StatusGroup = statusGroup;
    }

}
