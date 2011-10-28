package cn.com.jit.ida.ca.display;

import cn.com.jit.ida.ca.displayrelated.subca.DealSubCAOperate;

/**
 * 关于子CA的全部操作都在此类
 * 子CA通信证书的 1.证书申请 2.更新 3.导入 4.导入根证书
 * @author kmc
 */
public class DisplaySubCAAll extends DisplayOperate {

	public void operate() {
		DealSubCAOperate dco = new DealSubCAOperate();
		dco.dealCommuCert();
	}

}
