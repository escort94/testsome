package cn.com.jit.ida.privilege;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class Admin {
	private static Admin instance;
	Hashtable hashTable;
	public static final int SUPER_ADMIN = -1;
	public static final int AUDIT_ADMIN = -3;
	
	private Admin() throws PrivilegeException {
		DBManager localDBManager = null;
		try {
			localDBManager = DBManager.getInstance();
		} catch (DBException localDBException1) {
			throw new PrivilegeException(localDBException1.getErrCode(),
					localDBException1.getErrDesc());
		}
		try {
			this.hashTable = localDBManager.getAdminsInfo();
		} catch (DBException localDBException2) {
			throw new PrivilegeException(localDBException2.getErrCode(),
					localDBException2.getErrDesc());
		}
	}

	static Admin getInstance() throws PrivilegeException {
		if (instance == null)
			instance = new Admin();
		return instance;
	}

	void refresh() throws PrivilegeException {
		DBManager localDBManager = null;
		try {
			localDBManager = DBManager.getInstance();
		} catch (DBException localDBException1) {
			throw new PrivilegeException(localDBException1.getErrCode(),
					localDBException1.getErrDesc());
		}
		Hashtable localHashtable = null;
		try {
			localHashtable = localDBManager.getAdminsInfo();
		} catch (DBException localDBException2) {
			throw new PrivilegeException(localDBException2.getErrCode(),
					localDBException2.getErrDesc());
		}
		this.hashTable = localHashtable;
	}

	Vector getAdmin(String paramString) {
		if ((this.hashTable == null) || (this.hashTable.isEmpty()))
			return null;
		if (this.hashTable.containsKey(paramString)) {
			Vector localVector = (Vector) this.hashTable.get(paramString);
			return localVector;
		}
		return null;
	}

	Vector getAdminList() {
		if ((this.hashTable == null) || (this.hashTable.isEmpty()))
			return null;
		Set localSet = this.hashTable.keySet();
		Vector localVector = new Vector(localSet);
		return localVector;
	}

	Vector getAdminCertList() throws PrivilegeException {
		DBManager localDBManager = null;
		try {
			localDBManager = DBManager.getInstance();
		} catch (DBException localDBException) {
			throw new PrivilegeException(localDBException.getErrCode(),
					localDBException.getErrDesc());
		}
		Properties localProperties = new Properties();
		String str1 = null;
		String str2 = null;
		String str3 = null;
		try {
			str1 = InternalConfig.getInstance().getAdminTemplateName();
			str2 = CAConfig.getInstance().getCaAdminSN();
			str3 = CAConfig.getInstance().getCAAuditAdminSN();
		} catch (IDAException localIDAException1) {
			throw new PrivilegeException("0507", "获取配置信息出错", localIDAException1);
		}
		localProperties.setProperty("ctmlname", str1);
		localProperties.setProperty("certstatus", "Use");
		Vector localVector1 = null;
		try {
			localVector1 = localDBManager.getCertInfo(localProperties, 1, 1000,
					false);
		} catch (IDAException localIDAException2) {
			throw new PrivilegeException(localIDAException2.getErrCode(),
					localIDAException2.getErrDesc());
		}
		Vector localVector2 = null;
		Set localSet = null;
		if (this.hashTable != null)
			localSet = this.hashTable.keySet();
		if ((localVector1 != null) && (localVector1.size() > 1)) {
			CertInfo[] arrayOfCertInfo = (CertInfo[]) localVector1.get(1);
			localVector2 = new Vector();
			for (int i = 0; i < arrayOfCertInfo.length; i++) {
				String str4 = arrayOfCertInfo[i].getCertSN();
				if ((str4.equalsIgnoreCase(str2))
						|| (str4.equalsIgnoreCase(str3)))
					continue;
				AdminInfo localAdminInfo = new AdminInfo();
				localAdminInfo.setSn(str4);
				localAdminInfo.setDn(arrayOfCertInfo[i].getSubject());
				if ((localSet != null) && (localSet.contains(str4))) {
					localAdminInfo.setStatus("已授权");
					Vector localVector3 = getAdmin(str4);
					Role localRole = Role.getInstance();
					Vector localVector4 = localRole.getNameFromID(localVector3);
					String str5 = new String();
					int j = 0;
					for (int k = 0; k < localVector4.size(); k++) {
						str5 = str5 + (String) localVector4.get(k);
						str5 = str5 + ";";
						if (j != 0) {
							str5 = str5 + "\n\r";
							j = 0;
						} else {
							j = 1;
						}
					}
					localAdminInfo.setRoleinfo(str5);
				} else {
					localAdminInfo.setStatus("未授权");
					localAdminInfo.setRoleinfo("");
				}
				localVector2.add(localAdminInfo);
			}
		}
		return localVector2;
	}

	void setAdmin(String paramString, Collection paramCollection)
			throws PrivilegeException {
		CertInfo localCertInfo = null;
		try {
			localCertInfo = CertQueryOpt.queryCertInfo(paramString);
		} catch (Exception localException) {
			throw new PrivilegeException("0126", "获取该SN对应证书信息时出错",
					localException);
		}
		if (localCertInfo == null)
			throw new PrivilegeException("0130", "没有该SN对应的证书");
		String str = null;
		try {
			str = InternalConfig.getInstance().getAdminTemplateName();
		} catch (IDAException localIDAException) {
			throw new PrivilegeException("0507", "获取配置信息出错", localIDAException);
		}
		if (!localCertInfo.getCtmlName().equals(str))
			throw new PrivilegeException("0246", "该SN对应证书不是管理员证书");
		DBManager localDBManager = null;
		try {
			localDBManager = DBManager.getInstance();
		} catch (DBException localDBException1) {
			throw new PrivilegeException(localDBException1.getErrCode(),
					localDBException1.getErrDesc());
		}
		int i = -1;
		Vector localVector = new Vector(paramCollection);
		try {
			i = localDBManager.setAdminInfo(paramString, localVector);
		} catch (DBException localDBException2) {
			throw new PrivilegeException(localDBException2.getErrCode(),
					localDBException2.getErrDesc());
		}
		if (i < 0)
			throw new PrivilegeException("0121", "管理员授权失败");
		if (this.hashTable == null)
			this.hashTable = new Hashtable();
		else if (this.hashTable.containsKey(paramString))
			this.hashTable.remove(paramString);
		this.hashTable.put(paramString, localVector);
	}

	void setMeMAdmin(String paramString, Vector paramVector) {
		if (this.hashTable == null)
			this.hashTable = new Hashtable();
		else if (this.hashTable.contains(paramString))
			this.hashTable.remove(paramString);
		this.hashTable.put(paramString, paramVector);
	}

	void setSuperAdmin(String sn, String dn) throws PrivilegeException {
		Role localRole = Role.getInstance();
		Vector localVector = new Vector();
		localVector.add(localRole.getIDFromName("证书管理角色"));
		localVector.add(localRole.getIDFromName("模板管理角色"));
		localVector.add(localRole.getIDFromName("授权管理角色"));
		localVector.add(localRole.getIDFromName("超级管理员角色"));
		String str1 = null;
		String str2 = null;
		try {
			str1 = CAConfig.getInstance().getCaAdminSN();
			str2 = CAConfig.getInstance().getCAAuditAdminSN();
		} catch (Exception localException1) {
		}
		Object localObject;
		if ((str1 != null) && (!str1.equals(""))) {
			localObject = getAdmin(str1);
			if (localObject != null) {
				((Vector) localObject).removeAll(localVector);
				setAdmin(str1, (Collection) localObject);
			}
		}
		if (sn.equalsIgnoreCase(str2))
			localVector.add(localRole.getIDFromName("审计管理角色"));
		setAdmin(sn, localVector);
		try {
			localObject = GlobalConfig.getInstance();
			((GlobalConfig) localObject).getCAConfig().setCaAdminSN(sn);
			((GlobalConfig) localObject).getCAConfig().setCaAdminDN(dn);
		} catch (Exception localException2) {
		}
	}

	void setAuditAdmin(String paramString1, String paramString2)
			throws PrivilegeException {
		Role localRole = Role.getInstance();
		Vector localVector = new Vector();
		localVector.add(localRole.getIDFromName("审计管理角色"));
		String str1 = null;
		String str2 = null;
		try {
			str1 = CAConfig.getInstance().getCaAdminSN();
			str2 = CAConfig.getInstance().getCAAuditAdminSN();
		} catch (Exception localException1) {
		}
		Object localObject;
		if ((str2 != null) && (!str2.equals(""))) {
			localObject = getAdmin(str2);
			if (localObject != null) {
				((Vector) localObject).removeAll(localVector);
				setAdmin(str2, (Collection) localObject);
			}
		}
		if (paramString1.equalsIgnoreCase(str1)) {
			localVector.add(localRole.getIDFromName("证书管理角色"));
			localVector.add(localRole.getIDFromName("模板管理角色"));
			localVector.add(localRole.getIDFromName("授权管理角色"));
			localVector.add(localRole.getIDFromName("超级管理员角色"));
		}
		setAdmin(paramString1, localVector);
		try {
			localObject = GlobalConfig.getInstance();
			((GlobalConfig) localObject).getCAConfig().setCAAuditAdminSN(
					paramString1);
			((GlobalConfig) localObject).getCAConfig().setCAAuditAdminDN(
					paramString2);
		} catch (Exception localException2) {
		}
	}

	void delSuperAdmin() throws PrivilegeException {
		Role localRole = Role.getInstance();
		Vector localVector1 = new Vector();
		localVector1.add(localRole.getIDFromName("证书管理角色"));
		localVector1.add(localRole.getIDFromName("模板管理角色"));
		localVector1.add(localRole.getIDFromName("授权管理角色"));
		localVector1.add(localRole.getIDFromName("超级管理员角色"));
		String str = null;
		CAConfig localCAConfig = null;
		try {
			localCAConfig = CAConfig.getInstance();
			str = localCAConfig.getCaAdminSN();
		} catch (Exception localException1) {
		}
		if ((str != null) && (!str.equals(""))) {
			try {
				localCAConfig.setCaAdminSN("");
				localCAConfig.setCaAdminDN("");
			} catch (Exception localException2) {
			}
			Vector localVector2 = getAdmin(str);
			if (localVector2 != null) {
				localVector2.removeAll(localVector1);
				setAdmin(str, localVector2);
			}
			String[] arrayOfString = new String[0];
			try {
				DBManager localDBManager = DBManager.getInstance();
				localDBManager.setTemplateAdmin(str, arrayOfString,
						arrayOfString, arrayOfString);
			} catch (DBException localDBException) {
				throw new PrivilegeException("0131", "删除超级管理员对应的证书业务权限出错");
			}
			refresh();
		}
	}

	void delAuditAdmin() throws PrivilegeException {
		Role localRole = Role.getInstance();
		Vector localVector1 = new Vector();
		localVector1.add(localRole.getIDFromName("审计管理角色"));
		String str = null;
		CAConfig localCAConfig = null;
		try {
			localCAConfig = CAConfig.getInstance();
			str = localCAConfig.getCAAuditAdminSN();
		} catch (Exception localException) {
		}
		if ((str != null) && (!str.equals(""))) {
			try {
				localCAConfig.setCAAuditAdminSN("");
				localCAConfig.setCAAuditAdminDN("");
			} catch (IDAException localIDAException) {
			}
			Vector localVector2 = getAdmin(str);
			if (localVector2 != null) {
				localVector2.removeAll(localVector1);
				setAdmin(str, localVector2);
			}
			refresh();
		}
	}

	void delAdmin(String paramString) {
		if ((this.hashTable != null)
				&& (this.hashTable.containsKey(paramString)))
			this.hashTable.remove(paramString);
	}

	void updateAdmin(String paramString1, String paramString2) {
		if ((this.hashTable != null)
				&& (this.hashTable.containsKey(paramString1))) {
			Vector localVector = (Vector) this.hashTable.get(paramString1);
			this.hashTable.remove(paramString1);
			this.hashTable.put(paramString2, localVector);
		}
	}

	boolean isPass(String paramString1, String paramString2) {
		if ((this.hashTable != null)
				&& (this.hashTable.containsKey(paramString1))) {
			Vector localVector = (Vector) this.hashTable.get(paramString1);
			return localVector.contains(paramString2);
		}
		return false;
	}

	void setPendingAdmin(String paramString1, String paramString2) {
		if ((this.hashTable != null)
				&& (this.hashTable.containsKey(paramString1))) {
			Vector localVector = (Vector) this.hashTable.get(paramString1);
			this.hashTable.put(paramString2, localVector);
		}
	}

	void deletePendingAdmin(String paramString) {
		if ((this.hashTable != null)
				&& (this.hashTable.containsKey(paramString))) {
			Vector localVector = (Vector) this.hashTable.get(paramString);
			this.hashTable.remove(paramString);
		}
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.privilege.Admin JD-Core Version: 0.6.0
 */