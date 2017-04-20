package com.lostandfoundapp.data;

import com.lostandfoundapp.activity.R;

/**
 * 数据类 以静态数据存储用到的数据，包括存储文件名、网络连接参数、Handler-Message机制匹配数值等等
 * 
 * @author lee
 *
 */
public class MyValues {

	public static final String LAF = "LostAndFoundValues";// 文件名
	public static final String ISGUIDE = "isGuide";// 记录是否进入过新手导航界面
	public static final String USERINFO = "UserInfo";// 保存用户信息
	public static final String ISLOGIN = "isLogin";// 记录是否登录
	public static final String ISUPDATA = "isUpdata";// 记录是否更新了数据

	public static final String IP1 = "192.168.1.102";// 家庭网络局域网
	public static final String IP2 = "192.168.43.128";// 手机热点局域网
	public static String IP = IP1;//默认使用家庭局域网
	public static String URL = "http://" + IP + ":8088/LostAndFoundServers/";
	public static final String TestURL = URL + "InternetTest";
	public static final String SignURL = URL + "SignServlet";
	public static final String LoginURL = URL + "LoginServlet";
	public static final String UpdataUserURL = URL + "UserUpdataServlet";
	public static final String InsertPicURL = URL + "SavaPicNameServlet";
	public static final String SavaPicURL = URL + "SavaPicServlet";
	public static final String PublishURL = URL + "PublishServlet";
	public static final String SelectDataURL = URL + "SelectDatasServlet";
	public static final String DeleteDataURL = URL + "DataDeleteServlet";
	public static final String UpdateDataURL = URL + "DataUpdataServlet";
	public static final String SelectUserURL = URL + "SelectUserServlet";

	public static final String CheckURL = URL + "CheckDetailServlet";
	
	public static final String TestTAG = "test";
	public static final String SignTAG = "sign";
	public static final String LoginTAG = "login";
	public static final String UpdataUserTAG = "updataUser";
	public static final String InsertPicTAG = "insertPic";
	public static final String PublishTAG = "publish";
	public static final String SelectDataTAG = "selectData";
	public static final String SelectMyDataTAG = "selectMyData";
	public static final String DeleteDataTAG = "deleteData";
	public static final String UpdateDataTAG = "updateData";
	public static final String SelectUserTAG = "selectUser";

	public static final int TestWHAT = 0x1000;
	public static final int SignWHAT = 0x2000;
	public static final int LoginWHAT = 0x2001;
	public static final int UpdataUserWHAT = 0x2002;
	public static final int InsertPicWHAT = 0x2003;
	public static final int PublishWHAT = 0x2004;
	public static final int SelectDataWHAT = 0x2005;
	public static final int SelectMyDataWHAT = 0x2006;
	public static final int DeleteDataWHAT = 0x2007;
	public static final int UpdateDataWHAT = 0x2008;
	public static final int SelectUserWHAT = 0x2009;

	public static final int ADDRSS_RESULT_CODE = 100;
	public static final int SIGN_REQUEST_CODE_FOR_ADDRESS = 101;
	public static final int PUBLISH_REQUEST_CODE_FOR_ADDRESS = 102;
	public static final int PIC_RESULT_CODE = 103;
	public static final int PUBLISH_REQUEST_CODE_FOR_PIC = 104;
	public static final int UPDATA_DATA_REQUEST_CODE_FOR_ADDRESS = 105;
	public static final int UPDATA_DATA_REQUEST_CODE_FOR_PIC = 106;

	public static final String UserPicURL = URL+"userpic/";
	public static final String DataPicURL = URL+"datapic/";
	
	public static final String[] TYPES = {"所有事件","只看丢失","只看招领"};
	
	public static final String TencentAPPID = "1106015418";
	//public static final String W_APPID = "wxd930ea5d5a258f4f";
	
	public static String[] item = {"QQ好友","QQ空间"};
	public static int[] item_icon = {R.drawable.sharetoqq,R.drawable.sharetozone};
}
