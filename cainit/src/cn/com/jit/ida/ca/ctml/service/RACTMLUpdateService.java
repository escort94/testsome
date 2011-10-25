package cn.com.jit.ida.ca.ctml.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.SelfExtensionInformation;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.ctml.service.response.CTMLItem;
import cn.com.jit.ida.ca.ctml.service.response.RACTMLUpdateResponse;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ICRegistrationNumberPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.IdentifyCodePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.InsuranceNumberPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.OrganizationCodePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.PolicyConstraintsPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.PolicyMappingsPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SubjectAltNameExtPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.TaxationNumberPolicy;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.privilege.TemplateAdmin;
import java.util.Hashtable;
import java.util.Vector;

public class RACTMLUpdateService extends CTMLService
{
  private Vector results;

  public RACTMLUpdateService()
  {
    super("RACTMLUPDATE");
    this.debuger = LogManager.getDebugLogger("cn.com.jit.ida.ca.ctml.service.RACTMLUpdateService");
  }

  void execute()
    throws IDAException
  {
    this.results = new Vector();
    TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
    String str1 = this.operator.getOperatorSN();
    String str2 = this.operator.getOperatorDN();
    if ((str1 == null) || (str1.trim().equals("")))
    {
      this.debuger.appendMsg_L1("operator is null");
      throw new IDAException("0001", "操作员为空");
    }
    DBManager localDBManager = DBManager.getInstance();
    CertInfo localCertInfo = localDBManager.getCertInfo(str1);
    if (localCertInfo == null)
    {
      this.debuger.appendMsg_L1("operatorcert is null");
      throw new IDAException("0001", "操作员证书信息不存在");
    }
    String str3 = localCertInfo.getCertStatus();
    if ((str3 == null) || (!str3.equalsIgnoreCase("Use")))
    {
      this.debuger.appendMsg_L1("operator is revoke or hold");
      throw new IDAException("0001", "操作员证书已经注销或冻结");
    }
    if (this.isDebug)
    {
      this.debuger.appendMsg_L2("operatorSN=" + str1);
      this.debuger.appendMsg_L2("operatorDN=" + str2);
    }
    Vector localVector1 = localTemplateAdmin.getAdminInfo(str1);
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    SelfExtensionManager localSelfExtensionManager = SelfExtensionManager.getInstance();
    X509V3CTML localX509V3CTML = null;
    if (localVector1 != null)
    {
      Vector localVector2 = new Vector();
      for (int i = 0; i < localVector1.size(); i++)
      {
        Vector localVector3 = new Vector();
        Vector localVector4 = (Vector)localVector1.get(i);
        String str4 = (String)localVector4.get(0);
        if (this.isDebug)
          this.debuger.appendMsg_L2("CtmlName[" + i + "]=" + str4);
        if (localVector2.contains(str4))
          continue;
        String str5 = new String("");
        for (int j = localVector4.size() - 1; j > 0; j--)
          if (j > 1)
            str5 = str5 + localVector4.get(j).toString() + ",";
          else
            str5 = str5 + localVector4.get(j).toString();
        if (this.isDebug)
          this.debuger.appendMsg_L2("BaseDN[" + i + "]=" + str5);
        if (!str5.equals(""))
          localVector3.add(str5);
        localVector2.add(str4);
        for (j = 0; j < localVector1.size(); j++)
        {
          if (j == i)
            continue;
          Vector localVector5 = (Vector)localVector1.get(j);
          localObject1 = new String();
          if (!str4.equals(localVector5.get(0)))
            continue;
          for (m = localVector5.size() - 1; m > 0; m--)
            if (m > 1)
              localObject1 = (String)localObject1 + localVector5.get(m).toString() + ",";
            else
              localObject1 = (String)localObject1 + localVector5.get(m).toString();
          if (this.isDebug)
            this.debuger.appendMsg_L2("BaseDN[" + i + "]=" + (String)localObject1);
          if (((String)localObject1).equals(""))
            continue;
          localVector3.add(localObject1);
        }
        localX509V3CTML = (X509V3CTML)localCTMLManager.getCTML(str4);
        CTMLItem localCTMLItem = new CTMLItem();
        localCTMLItem.setName(localX509V3CTML.getCTMLName());
        localCTMLItem.setId(localX509V3CTML.getCTMLID());
        localCTMLItem.setType(localX509V3CTML.getCTMLType());
        localCTMLItem.setStatus(localX509V3CTML.getCTMLStatus());
        localCTMLItem.setKeytype(localX509V3CTML.getKeyType());
        localCTMLItem.setKeySpec(localX509V3CTML.getKeySpec());
        localCTMLItem.setUpdateReplace(localX509V3CTML.getUpdateReplace());
        localCTMLItem.setUpdateTransPeriod(localX509V3CTML.getUpdTransPeriod());
        localCTMLItem.setKeysize(localX509V3CTML.getKeyLength());
        localCTMLItem.setDefaultValidate(localX509V3CTML.getDefaultValidity());
        localCTMLItem.setKeyGenLocation(localX509V3CTML.getKeyGenPlace());
        localCTMLItem.setMaxValidate(localX509V3CTML.getMaxValidty());
        localCTMLItem.setNotafterDate(Long.toString(localX509V3CTML.getNotAfter()));
        localCTMLItem.setDescription(localX509V3CTML.getCTMLDesc());
        localCTMLItem.setBaseDN(localVector3);
        int k = localX509V3CTML.getExtensionCount();
        Object localObject1 = new Vector();
        Object localObject2;
        Object localObject3;
        for (int m = 0; m < k; m++)
        {
          if ((!localX509V3CTML.getUserProcessPolicy(m).equals("NEED")) && ((!localX509V3CTML.getUserProcessPolicy(m).equals("ALLOW")) || (localX509V3CTML.isStandard(m))))
            continue;
          localObject2 = new Vector();
          localObject3 = localSelfExtensionManager.getSelfExt(localX509V3CTML.getExtensionName(m));
          ((Vector)localObject2).add(((SelfExtensionInformation)localObject3).getExtensionName());
          ((Vector)localObject2).add(((SelfExtensionInformation)localObject3).getExtensionOID());
          ((Vector)localObject2).add(((SelfExtensionInformation)localObject3).getExtensionEncoding());
          ((Vector)localObject2).add(((SelfExtensionInformation)localObject3).getExtensionStatus());
          ((Vector)localObject2).add(localX509V3CTML.getUserProcessPolicy(m));
          ((Vector)localObject2).add(((SelfExtensionInformation)localObject3).getExtensionDesc());
          ((Vector)localObject1).add(localObject2);
        }
        localCTMLItem.setExtention((Vector)localObject1);
        Vector localVector6 = null;
        if (localX509V3CTML.getStandardExt() != null)
        {
          localObject2 = localX509V3CTML.getStandardExt();
          localObject3 = (SubjectAltNameExtPolicy)((Hashtable)localObject2).get("SubjectAltNameExt");
          StandardExtension localStandardExtension = null;
          localVector6 = new Vector();
          if (localObject3 != null)
          {
            if (new Boolean(((SubjectAltNameExtPolicy)localObject3).getOtherName()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(((SubjectAltNameExtPolicy)localObject3).getOtherNameAllowNull());
              localStandardExtension.setChildName("otherName");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("otherName@SubjectAltNameExt"));
              localStandardExtension.setParentName("SubjectAltNameExt");
              localStandardExtension.setParentOID("2.5.29.17");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(((SubjectAltNameExtPolicy)localObject3).getEmail()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(((SubjectAltNameExtPolicy)localObject3).getEmailAllowNull());
              localStandardExtension.setChildName("email");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("email@SubjectAltNameExt"));
              localStandardExtension.setParentName("SubjectAltNameExt");
              localStandardExtension.setParentOID("2.5.29.17");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(((SubjectAltNameExtPolicy)localObject3).getIpAddress()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(((SubjectAltNameExtPolicy)localObject3).getIpAddressAllowNull());
              localStandardExtension.setChildName("ip");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("ip@SubjectAltNameExt"));
              localStandardExtension.setParentName("SubjectAltNameExt");
              localStandardExtension.setParentOID("2.5.29.17");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(((SubjectAltNameExtPolicy)localObject3).getDnName()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(((SubjectAltNameExtPolicy)localObject3).getDnNameAllowNull());
              localStandardExtension.setChildName("dnName");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("dnName@SubjectAltNameExt"));
              localStandardExtension.setParentName("SubjectAltNameExt");
              localStandardExtension.setParentOID("2.5.29.17");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(((SubjectAltNameExtPolicy)localObject3).getDnsName()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(((SubjectAltNameExtPolicy)localObject3).getDnsNameAllowNull());
              localStandardExtension.setChildName("dnsName");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("dnsName@SubjectAltNameExt"));
              localStandardExtension.setParentName("SubjectAltNameExt");
              localStandardExtension.setParentOID("2.5.29.17");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(((SubjectAltNameExtPolicy)localObject3).getUriName()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(((SubjectAltNameExtPolicy)localObject3).getUriNameAllowNull());
              localStandardExtension.setChildName("uriName");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("uriName@SubjectAltNameExt"));
              localStandardExtension.setParentName("SubjectAltNameExt");
              localStandardExtension.setParentOID("2.5.29.17");
              localVector6.add(localStandardExtension);
            }
          }
          IdentifyCodePolicy localIdentifyCodePolicy = (IdentifyCodePolicy)((Hashtable)localObject2).get("IdentifyCode");
          if (localIdentifyCodePolicy != null)
          {
            if (new Boolean(localIdentifyCodePolicy.getIdCardNum()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(localIdentifyCodePolicy.getIdCardNumAllowNull());
              localStandardExtension.setChildName("residenterCardNumber");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("residenterCardNumber@IdentifyCode"));
              localStandardExtension.setParentName("IdentifyCode");
              localStandardExtension.setParentOID("1.2.86.11.7.1");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(localIdentifyCodePolicy.getMilitaryCardNum()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(localIdentifyCodePolicy.getMilitaryCardNumAllowNull());
              localStandardExtension.setChildName("militaryOfficerCardNumber");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("militaryOfficerCardNumber@IdentifyCode"));
              localStandardExtension.setParentName("IdentifyCode");
              localStandardExtension.setParentOID("1.2.86.11.7.1");
              localVector6.add(localStandardExtension);
            }
            if (new Boolean(localIdentifyCodePolicy.getPassportNum()).booleanValue())
            {
              localStandardExtension = new StandardExtension();
              localStandardExtension.setAllowNull(localIdentifyCodePolicy.getPassportNumAllowNull());
              localStandardExtension.setChildName("passportNumber");
              localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("passportNumber@IdentifyCode"));
              localStandardExtension.setParentName("IdentifyCode");
              localStandardExtension.setParentOID("1.2.86.11.7.1");
              localVector6.add(localStandardExtension);
            }
          }
          InsuranceNumberPolicy localInsuranceNumberPolicy = (InsuranceNumberPolicy)((Hashtable)localObject2).get("InsuranceNumber");
          if ((localInsuranceNumberPolicy != null) && (new Boolean(localInsuranceNumberPolicy.getInsuranceNum()).booleanValue()))
          {
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localInsuranceNumberPolicy.getInsuranceNumAllowNull());
            localStandardExtension.setChildName("InsuranceNumber");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("InsuranceNumber@InsuranceNumber"));
            localStandardExtension.setParentName("InsuranceNumber");
            localStandardExtension.setParentOID("1.2.86.11.7.2");
            localVector6.add(localStandardExtension);
          }
          OrganizationCodePolicy localOrganizationCodePolicy = (OrganizationCodePolicy)((Hashtable)localObject2).get("OrganizationCode");
          if ((localOrganizationCodePolicy != null) && (new Boolean(localOrganizationCodePolicy.getOrgCode()).booleanValue()))
          {
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localOrganizationCodePolicy.getOrgCodeAllowNull());
            localStandardExtension.setChildName("OrganizationCode");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("OrganizationCode@OrganizationCode"));
            localStandardExtension.setParentName("OrganizationCode");
            localStandardExtension.setParentOID("1.2.86.11.7.3");
            localVector6.add(localStandardExtension);
          }
          ICRegistrationNumberPolicy localICRegistrationNumberPolicy = (ICRegistrationNumberPolicy)((Hashtable)localObject2).get("ICRegistrationNumber");
          if ((localICRegistrationNumberPolicy != null) && (new Boolean(localICRegistrationNumberPolicy.getIcRegistNum()).booleanValue()))
          {
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localICRegistrationNumberPolicy.getIcRegistNumAllowNull());
            localStandardExtension.setChildName("ICRegistrationNumber");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("ICRegistrationNumber@ICRegistrationNumber"));
            localStandardExtension.setParentName("ICRegistrationNumber");
            localStandardExtension.setParentOID("1.2.86.11.7.4");
            localVector6.add(localStandardExtension);
          }
          TaxationNumberPolicy localTaxationNumberPolicy = (TaxationNumberPolicy)((Hashtable)localObject2).get("TaxationNumber");
          if ((localTaxationNumberPolicy != null) && (new Boolean(localTaxationNumberPolicy.getTaxaNum()).booleanValue()))
          {
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localTaxationNumberPolicy.getTaxaNumAllowNull());
            localStandardExtension.setChildName("TaxationNumber");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("TaxationNumber@TaxationNumber"));
            localStandardExtension.setParentName("TaxationNumber");
            localStandardExtension.setParentOID("1.2.86.11.7.5");
            localVector6.add(localStandardExtension);
          }
          PolicyConstraintsPolicy localPolicyConstraintsPolicy = (PolicyConstraintsPolicy)((Hashtable)localObject2).get("PolicyConstrants");
          if ((localPolicyConstraintsPolicy != null) && (localPolicyConstraintsPolicy.getPolicyConstraintsAllowNull() != null))
          {
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localPolicyConstraintsPolicy.getPolicyConstraintsAllowNull());
            localStandardExtension.setChildName("requireExplicitPolicy");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("requireExplicitPolicy@PolicyConstrants"));
            localStandardExtension.setParentName("PolicyConstrants");
            localStandardExtension.setParentOID("2.5.29.36");
            localVector6.add(localStandardExtension);
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localPolicyConstraintsPolicy.getPolicyConstraintsAllowNull());
            localStandardExtension.setChildName("inhibitPolicyMapping");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("inhibitPolicyMapping@PolicyConstrants"));
            localStandardExtension.setParentName("PolicyConstrants");
            localStandardExtension.setParentOID("2.5.29.36");
            localVector6.add(localStandardExtension);
          }
          PolicyMappingsPolicy localPolicyMappingsPolicy = (PolicyMappingsPolicy)((Hashtable)localObject2).get("PolicyMappings");
          if ((localPolicyMappingsPolicy != null) && (localPolicyMappingsPolicy.getPolicyMappingsAllowNull() != null))
          {
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localPolicyMappingsPolicy.getPolicyMappingsAllowNull());
            localStandardExtension.setChildName("issuerDomainPolicy");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("issuerDomainPolicy@PolicyMappings"));
            localStandardExtension.setParentName("PolicyMappings");
            localStandardExtension.setParentOID("2.5.29.33");
            localVector6.add(localStandardExtension);
            localStandardExtension = new StandardExtension();
            localStandardExtension.setAllowNull(localPolicyMappingsPolicy.getPolicyMappingsAllowNull());
            localStandardExtension.setChildName("subjectDomainPolicy");
            localStandardExtension.setChildEncode((String)CTMLConstant.standardExtAndEncodeType.get("subjectDomainPolicy@PolicyMappings"));
            localStandardExtension.setParentName("PolicyMappings");
            localStandardExtension.setParentOID("2.5.29.33");
            localVector6.add(localStandardExtension);
          }
        }
        localCTMLItem.setStandardExtention(localVector6);
        this.results.add(localCTMLItem);
      }
    }
  }

  Response constructResponse()
  {
    RACTMLUpdateResponse localRACTMLUpdateResponse = new RACTMLUpdateResponse();
    localRACTMLUpdateResponse.setResult(this.results.toArray());
    this.debuger.doLog();
    return localRACTMLUpdateResponse;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.RACTMLUpdateService
 * JD-Core Version:    0.6.0
 */