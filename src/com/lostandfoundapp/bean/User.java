package com.lostandfoundapp.bean;

/**
 * �û���
 * 
 * @author lee
 *
 */
public class User {
	private String phone;// �û��绰���ʺ�
	private String password;// �û�����
	private String name;// �û�����
	private String sex;// �Ա�
	private String address;// ��פ��ַ

	public User() {
	}

	public User(String phone, String password, String name, String sex,
			String address) {
		super();
		this.phone = phone;
		this.password = password;
		this.name = name;
		this.sex = sex;
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "User [phone=" + phone + ", password=" + password + ", name="
				+ name + ", sex=" + sex + ", address=" + address + "]";
	}

}
