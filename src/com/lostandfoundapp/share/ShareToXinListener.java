package com.lostandfoundapp.share;

import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class ShareToXinListener implements IWXAPIEventHandler{

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		String result;
		 switch (resp.errCode) {  
		    case BaseResp.ErrCode.ERR_OK:  
		        result = "success";  
		        break;  
		    case BaseResp.ErrCode.ERR_USER_CANCEL:  
		        result = "cancel";  
		        break;  
		    case BaseResp.ErrCode.ERR_AUTH_DENIED:  
		        result = "errcode_deny";  
		        break;  
		    default:  
		        result = "errcode_unknown";  
		        break;  }
		 Log.i("LAF", result);
	}

}
