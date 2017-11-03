package com.mmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class Shipping implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer productId;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverMobile;
    
    private String receiverProvince;
    
    private String receiverCity;
    
    private String receiverDistrict;
    
    private String receiverAddress;
    
    private String receiverZip;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverProvince() {
		return receiverProvince;
	}

	public void setReceiverProvince(String receiverProvince) {
		this.receiverProvince = receiverProvince;
	}

	public String getReceiverCity() {
		return receiverCity;
	}

	public void setReceiverCity(String receiverCity) {
		this.receiverCity = receiverCity;
	}

	public String getReceiverDistrict() {
		return receiverDistrict;
	}

	public void setReceiverDistrict(String receiverDistrict) {
		this.receiverDistrict = receiverDistrict;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getReceiverZip() {
		return receiverZip;
	}

	public void setReceiverZip(String receiverZip) {
		this.receiverZip = receiverZip;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Shipping [id=" + id + ", userId=" + userId + ", productId=" + productId + ", receiverName="
				+ receiverName + ", receiverPhone=" + receiverPhone + ", receiverMobile=" + receiverMobile
				+ ", receiverProvince=" + receiverProvince + ", receiverCity=" + receiverCity + ", receiverDistrict="
				+ receiverDistrict + ", receiverAddress=" + receiverAddress + ", receiverZip=" + receiverZip
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((receiverAddress == null) ? 0 : receiverAddress.hashCode());
		result = prime * result + ((receiverCity == null) ? 0 : receiverCity.hashCode());
		result = prime * result + ((receiverDistrict == null) ? 0 : receiverDistrict.hashCode());
		result = prime * result + ((receiverMobile == null) ? 0 : receiverMobile.hashCode());
		result = prime * result + ((receiverName == null) ? 0 : receiverName.hashCode());
		result = prime * result + ((receiverPhone == null) ? 0 : receiverPhone.hashCode());
		result = prime * result + ((receiverProvince == null) ? 0 : receiverProvince.hashCode());
		result = prime * result + ((receiverZip == null) ? 0 : receiverZip.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shipping other = (Shipping) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (receiverAddress == null) {
			if (other.receiverAddress != null)
				return false;
		} else if (!receiverAddress.equals(other.receiverAddress))
			return false;
		if (receiverCity == null) {
			if (other.receiverCity != null)
				return false;
		} else if (!receiverCity.equals(other.receiverCity))
			return false;
		if (receiverDistrict == null) {
			if (other.receiverDistrict != null)
				return false;
		} else if (!receiverDistrict.equals(other.receiverDistrict))
			return false;
		if (receiverMobile == null) {
			if (other.receiverMobile != null)
				return false;
		} else if (!receiverMobile.equals(other.receiverMobile))
			return false;
		if (receiverName == null) {
			if (other.receiverName != null)
				return false;
		} else if (!receiverName.equals(other.receiverName))
			return false;
		if (receiverPhone == null) {
			if (other.receiverPhone != null)
				return false;
		} else if (!receiverPhone.equals(other.receiverPhone))
			return false;
		if (receiverProvince == null) {
			if (other.receiverProvince != null)
				return false;
		} else if (!receiverProvince.equals(other.receiverProvince))
			return false;
		if (receiverZip == null) {
			if (other.receiverZip != null)
				return false;
		} else if (!receiverZip.equals(other.receiverZip))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

   
}