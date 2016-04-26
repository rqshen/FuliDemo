package com.bcb.data.bean;

/**
 * Created by cain on 15/12/30.
 */
public class MyCompanyBean {
    public String CompanyId;
    public String ShortName;
    public int Status;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }
}
