package com.bcb.data.bean.loan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cain on 16/1/6.
 */
public class PersonInfoBean implements Serializable {
    public int MaritalStatus;//婚姻状况
    public int HousingStatus;//住房状况
    public int EducationLevel;//文化程度
    public int ChildrenStatus;//子女状况
    public String GraduateSchool;//毕业院校
    public String EmergencyContact1;//紧急联系人1
    public String EmergencyContact2;//紧急联系人2
    public String Relationship1;//关系1
    public String Relationship2;//关系2
    public String ContactPhone1;//联系电话1
    public String ContactPhone2;//联系电话2
    public String Hometown;//户口所在地
    public String Residence;//现居住地址
    public int SesameCredit;//芝麻信用分
    public List<MaritalStatusListbean> MaritalStatusList;   //婚姻状况
    public List<EducationLevelListBean> EducationLevelList; //文化程度
    public List<HousingStatusListBean> HousingStatusList;   //住房状况
    public List<RelationshipListBean> RelationshipList;     //紧急联系人关系
    public List<ChildrenStatusListBean> ChildrenStatusList; //子女个数


    //工作信息
    public String Position;//工作职位
    public boolean IsManagePost;//是否管理岗位
    public String Department;//所在事业部
    public String WorkAddress;//办公地点
    public float IncomeAmount;//月均收入
    public String Email;//邮箱地址
    public int FundLimit;//公积金缴存额度
    public String WorkUnit;//工作单位全称
    public String EntryDate;//入职时间
    public float TotalIncomePerYear;//年税后总收入

    //资产信息
    public int TotalHomeIncome;//家庭总收入
    public int HomeFixedExpenditure;//家庭固定支出
    public int HousingWorth;//房产价值
    public int CarWorth;//车产价值
    public int SecuritiesWorth;//有价证券
    public int OtherAsset;//其他资产
    public int HousingDebt;//房产负债
    public int CarDebt;//车产负债
    public int CreditCardDebt;//信用卡负债
    public int OtherDebt;//其他负债
}