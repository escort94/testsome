package cn.com.jit.ida.ca.issue.opt;

import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;
import cn.com.jit.ida.ca.issue.entity.CAEntity;
import cn.com.jit.ida.ca.issue.entity.CAEntityAttribute;
import cn.com.jit.ida.ca.issue.entity.CRLEntity;
import cn.com.jit.ida.ca.issue.entity.CRLEntityAttribute;
import cn.com.jit.ida.ca.issue.entity.CertEntity;
import cn.com.jit.ida.ca.issue.entity.CertEntityAttribute;
import cn.com.jit.ida.ca.issue.entity.CrossCertPairEntity;
import java.util.ArrayList;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

public class LDAPStore
  implements Store
{
  private LDAPOperation operation = null;
  private String distributionDN = null;
  private String endDN = null;
  private String otherName = null;
  private int ADD_ATTRI = 1;
  private int REPLACE_ATTRI = 2;
  private int REMOVE_ATTRI = 3;
  private CertEntityAttribute certEntityAttribute = new CertEntityAttribute();
  private CAEntityAttribute caEntityAttribute = new CAEntityAttribute();
  private CRLEntityAttribute crlEntityAttribute = new CRLEntityAttribute();

  public LDAPStore()
    throws ISSException
  {
  }

  public void add(BaseEntity paramBaseEntity)
    throws ISSException
  {
    if (paramBaseEntity == null)
      throw new ISSException("8404", "待颁发实体不能为空");
    this.distributionDN = paramBaseEntity.getDistributionPath();
    addDistributionDN(paramBaseEntity);
    NamingEnumeration localNamingEnumeration = this.operation.IsExistEntry(this.distributionDN);
    SearchResult localSearchResult = (SearchResult)localNamingEnumeration.nextElement();
    Attributes localAttributes = localSearchResult.getAttributes();
    Attribute localAttribute = localAttributes.get("objectClass");
    if (paramBaseEntity.getEntityType() == "Cert")
      issueUser(paramBaseEntity, localAttributes, localAttribute);
    else if (paramBaseEntity.getEntityType() == "CRL")
      issueCRL(paramBaseEntity, localAttribute);
    else if (paramBaseEntity.getEntityType() == "CACert")
      issueCA(paramBaseEntity, localAttributes, localAttribute);
    else if (paramBaseEntity.getEntityType() == "crossCertPair")
      isssueCCP(paramBaseEntity, localAttributes);
    else
      throw new ISSException("8414", "实体不存在");
  }

  private void issueUser(BaseEntity paramBaseEntity, Attributes paramAttributes, Attribute paramAttribute)
    throws ISSException
  {
    if (certExists(paramBaseEntity))
      return;
    CertEntity localCertEntity = (CertEntity)paramBaseEntity;
    boolean bool = paramAttribute.contains(this.certEntityAttribute.getIdaPersonObj());
    Object localObject1;
    if (!bool)
    {
      localObject1 = new BasicAttributes(true);
      ((Attributes)localObject1).put(new BasicAttribute("objectClass", this.certEntityAttribute.getIdaPersonObj()));
      ((Attributes)localObject1).put(new BasicAttribute(this.certEntityAttribute.getAttriIdCN(), localCertEntity.getCN()));
      ((Attributes)localObject1).put(new BasicAttribute(this.certEntityAttribute.getAttriIdSN(), localCertEntity.getSN()));
      ((Attributes)localObject1).put(new BasicAttribute(this.certEntityAttribute.getAttriIdSerialNumber(), localCertEntity.getCertSN(), true));
      ((Attributes)localObject1).put(new BasicAttribute(this.certEntityAttribute.getAttriIdUserCertificate(), localCertEntity.getCertContent(), true));
      if (localCertEntity.getEmail() != null)
        ((Attributes)localObject1).put(new BasicAttribute(this.certEntityAttribute.getAttriIdEMail(), localCertEntity.getEmail()));
      this.operation.modify(this.distributionDN, this.ADD_ATTRI, (Attributes)localObject1);
      this.operation.closeConnection();
    }
    else
    {
      localObject1 = paramAttributes.get(this.certEntityAttribute.getAttriIdUserCertificate());
      Object localObject2 = paramAttributes.get(this.certEntityAttribute.getAttriIdSerialNumber());
      if (localObject1 == null)
      {
        localObject1 = new BasicAttribute(this.certEntityAttribute.getAttriIdUserCertificate(), localCertEntity.getCertContent(), true);
        localObject2 = new BasicAttribute(this.certEntityAttribute.getAttriIdSerialNumber(), localCertEntity.getCertSN(), true);
      }
      else
      {
        ((Attribute)localObject1).add(localCertEntity.getCertContent());
        ((Attribute)localObject2).add(localCertEntity.getCertSN());
      }
      Object localObject3 = null;
      if (localCertEntity.getEmail() != null)
      {
        localObject3 = paramAttributes.get(this.certEntityAttribute.getAttriIdEMail());
        if (localObject3 == null)
          localObject3 = new BasicAttribute(this.certEntityAttribute.getAttriIdEMail(), localCertEntity.getEmail());
      }
      BasicAttributes localBasicAttributes = new BasicAttributes(true);
      localBasicAttributes.put((Attribute)localObject1);
      localBasicAttributes.put((Attribute)localObject2);
      if (localObject3 != null)
        localBasicAttributes.put((Attribute)localObject3);
      this.operation.modify(this.distributionDN, this.REPLACE_ATTRI, localBasicAttributes);
      this.operation.closeConnection();
    }
  }

  private void issueCRL(BaseEntity paramBaseEntity, Attribute paramAttribute)
    throws ISSException
  {
    CRLEntity localCRLEntity = (CRLEntity)paramBaseEntity;
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    localBasicAttributes.put(new BasicAttribute(this.crlEntityAttribute.getAttriIdCRL(), localCRLEntity.getCrlContent()));
    this.operation.modify(this.distributionDN, this.REPLACE_ATTRI, localBasicAttributes);
    this.operation.closeConnection();
  }

  private void issueCA(BaseEntity paramBaseEntity, Attributes paramAttributes, Attribute paramAttribute)
    throws ISSException
  {
    if (certExists(paramBaseEntity))
      return;
    CAEntity localCAEntity = (CAEntity)paramBaseEntity;
    BasicAttributes localBasicAttributes1 = new BasicAttributes(true);
    boolean bool1 = paramAttribute.contains(this.caEntityAttribute.getCaObj());
    boolean bool2 = paramAttribute.contains(this.caEntityAttribute.getIdaPersonObj());
    Object localObject1;
    Object localObject2;
    Object localObject4;
    if (!bool1)
    {
      localObject1 = new BasicAttributes(true);
      if (!bool2)
      {
        ((Attributes)localObject1).put(new BasicAttribute("objectClass", this.caEntityAttribute.getIdaPersonObj()));
        ((Attributes)localObject1).put(new BasicAttribute(this.caEntityAttribute.getAttriIdCN(), localCAEntity.getCN()));
        ((Attributes)localObject1).put(new BasicAttribute(this.caEntityAttribute.getAttriIdSN(), localCAEntity.getSN()));
        ((Attributes)localObject1).put(new BasicAttribute(this.caEntityAttribute.getAttriIdSerialNumber(), localCAEntity.getCACertSN(), true));
        this.operation.modify(this.distributionDN, this.ADD_ATTRI, (Attributes)localObject1);
      }
      else
      {
        localObject2 = paramAttributes.get(this.caEntityAttribute.getAttriIdSerialNumber());
        if (localObject2 == null)
        {
          ((Attributes)localObject1).put(new BasicAttribute(this.caEntityAttribute.getAttriIdSerialNumber(), localCAEntity.getCACertSN(), true));
          this.operation.modify(this.distributionDN, this.ADD_ATTRI, (Attributes)localObject1);
        }
      }
      localBasicAttributes1.put(new BasicAttribute("objectClass", this.caEntityAttribute.getCaObj()));
      localBasicAttributes1.put(new BasicAttribute(this.caEntityAttribute.getAttriIdCACertificate(), localCAEntity.getCACert(), true));
      localBasicAttributes1.put(new BasicAttribute(this.caEntityAttribute.getAttriIdAuthRVKList(), localCAEntity.getCACert(), true));
      localBasicAttributes1.put(new BasicAttribute(this.caEntityAttribute.getAttriIdCertRVKList(), localCAEntity.getCACert(), true));
      if ((localCAEntity.getCrossCertPairs() != null) && (localCAEntity.getCrossCertPairs().size() != 0))
      {
        localObject2 = new BasicAttribute(this.caEntityAttribute.getAttriIdCrossCertificatePair(), true);
        for (int i = 0; i < localCAEntity.getCrossCertPairs().size(); i++)
        {
          localObject4 = (byte[])localCAEntity.getCrossCertPairs().get(i);
          ((BasicAttribute)localObject2).add(localObject4);
        }
        localBasicAttributes1.put((Attribute)localObject2);
      }
      this.operation.modify(this.distributionDN, this.ADD_ATTRI, localBasicAttributes1);
      this.operation.closeConnection();
    }
    else
    {
      localObject1 = paramAttributes.get(this.caEntityAttribute.getAttriIdSerialNumber());
      if (localObject1 == null)
        localObject1 = new BasicAttribute(this.caEntityAttribute.getAttriIdSerialNumber(), localCAEntity.getCACertSN(), true);
      else
        ((Attribute)localObject1).add(localCAEntity.getCACertSN());
      localObject2 = paramAttributes.get(this.caEntityAttribute.getAttriIdCACertificate());
      if (localObject2 == null)
        localObject2 = new BasicAttribute(this.caEntityAttribute.getAttriIdCACertificate(), localCAEntity.getCACert(), true);
      else
        ((Attribute)localObject2).add(localCAEntity.getCACert());
      Object localObject3 = paramAttributes.get(this.caEntityAttribute.getAttriIdAuthRVKList());
      if (localObject3 == null)
        localObject3 = new BasicAttribute(this.caEntityAttribute.getAttriIdAuthRVKList(), localCAEntity.getCACert(), true);
      else
        ((Attribute)localObject3).add(localCAEntity.getCACert());
      localObject4 = paramAttributes.get(this.caEntityAttribute.getAttriIdCertRVKList());
      if (localObject4 == null)
        localObject4 = new BasicAttribute(this.caEntityAttribute.getAttriIdCertRVKList(), localCAEntity.getCACert(), true);
      else
        ((Attribute)localObject4).add(localCAEntity.getCACert());
      Object localObject5 = paramAttributes.get(this.caEntityAttribute.getAttriIdCrossCertificatePair());
      if ((localCAEntity.getCrossCertPairs() != null) && (localCAEntity.getCrossCertPairs().size() != 0))
      {
        if (localObject5 == null)
        {
          localObject5 = new BasicAttribute(this.caEntityAttribute.getAttriIdCrossCertificatePair(), true);
          for (int j = 0; j < localCAEntity.getCrossCertPairs().size(); j++)
          {
            byte[] arrayOfByte1 = (byte[])localCAEntity.getCrossCertPairs().get(j);
            ((Attribute)localObject5).add(arrayOfByte1);
          }
        }
        ((Attribute)localObject5).clear();
        localBasicAttributes2 = new BasicAttributes(true);
        for (int k = 0; k < localCAEntity.getCrossCertPairs().size(); k++)
        {
          byte[] arrayOfByte2 = (byte[])localCAEntity.getCrossCertPairs().get(k);
          ((Attribute)localObject5).add(arrayOfByte2);
        }
        localBasicAttributes2.put((Attribute)localObject5);
        this.operation.modify(this.distributionDN, this.REPLACE_ATTRI, localBasicAttributes2);
      }
      BasicAttributes localBasicAttributes2 = new BasicAttributes(true);
      localBasicAttributes2.put((Attribute)localObject1);
      localBasicAttributes2.put((Attribute)localObject2);
      localBasicAttributes2.put((Attribute)localObject3);
      localBasicAttributes2.put((Attribute)localObject4);
      this.operation.modify(this.distributionDN, this.REPLACE_ATTRI, localBasicAttributes2);
      this.operation.closeConnection();
    }
  }

  private void isssueCCP(BaseEntity paramBaseEntity, Attributes paramAttributes)
    throws ISSException
  {
    if (!certExists(paramBaseEntity))
      throw new ISSException("8421", "8421");
    CrossCertPairEntity localCrossCertPairEntity = (CrossCertPairEntity)paramBaseEntity;
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    Object localObject = paramAttributes.get(this.caEntityAttribute.getAttriIdCrossCertificatePair());
    byte[] arrayOfByte;
    if (localObject == null)
    {
      localObject = new BasicAttribute(this.caEntityAttribute.getAttriIdCrossCertificatePair(), true);
      for (i = 0; i < localCrossCertPairEntity.getCrossCertPairs().size(); i++)
      {
        arrayOfByte = (byte[])localCrossCertPairEntity.getCrossCertPairs().get(i);
        ((Attribute)localObject).add(arrayOfByte);
      }
    }
    ((Attribute)localObject).clear();
    for (int i = 0; i < localCrossCertPairEntity.getCrossCertPairs().size(); i++)
    {
      arrayOfByte = (byte[])localCrossCertPairEntity.getCrossCertPairs().get(i);
      ((Attribute)localObject).add(arrayOfByte);
    }
    localBasicAttributes.put((Attribute)localObject);
    this.operation.modify(this.distributionDN, this.REPLACE_ATTRI, localBasicAttributes);
    this.operation.closeConnection();
  }

  public void delete(BaseEntity paramBaseEntity)
    throws ISSException
  {
    String str = paramBaseEntity.getDistributionPath();
    NamingEnumeration localNamingEnumeration = this.operation.IsExistEntry(str);
    if (localNamingEnumeration == null)
      throw new ISSException("8414", "实体不存在");
    this.operation.delete(str);
    this.operation.closeConnection();
  }

  private boolean certExists(BaseEntity paramBaseEntity)
  {
    String str1 = paramBaseEntity.getDistributionPath();
    NamingEnumeration localNamingEnumeration = this.operation.IsExistEntry(str1);
    if (localNamingEnumeration == null)
      return false;
    SearchResult localSearchResult = (SearchResult)localNamingEnumeration.nextElement();
    Attributes localAttributes = localSearchResult.getAttributes();
    String str2 = paramBaseEntity.getEntityType();
    if (str2.equalsIgnoreCase("CACert"))
    {
      localObject = (CAEntity)paramBaseEntity;
      localAttribute = localAttributes.get(this.caEntityAttribute.getAttriIdSerialNumber());
      if (localAttribute == null)
        return false;
      bool = localAttribute.contains(((CAEntity)localObject).getCACertSN());
      return bool;
    }
    if (str2.equalsIgnoreCase("crossCertPair"))
    {
      localObject = (CrossCertPairEntity)paramBaseEntity;
      localAttribute = localAttributes.get(this.caEntityAttribute.getAttriIdSerialNumber());
      if (localAttribute == null)
        return false;
      bool = localAttribute.contains(((CrossCertPairEntity)localObject).getCaSerialNumber());
      return bool;
    }
    Object localObject = (CertEntity)paramBaseEntity;
    Attribute localAttribute = localAttributes.get(this.certEntityAttribute.getAttriIdSerialNumber());
    if (localAttribute == null)
      return false;
    boolean bool = localAttribute.contains(((CertEntity)localObject).getCertSN());
    return bool;
  }

  private void addEntry(String paramString)
    throws ISSException
  {
    String str1 = paramString.substring(0, paramString.indexOf("=")).toLowerCase();
    String str2 = "";
    if (paramString.indexOf(",") == -1)
      str2 = paramString.substring(paramString.indexOf("=") + 1);
    else
      str2 = paramString.substring(paramString.indexOf("=") + 1, paramString.indexOf(","));
    BasicAttributes localBasicAttributes;
    BasicAttribute localBasicAttribute;
    if (str1.trim().equals("c"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("country");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
    else if ((str1.trim().equals("l")) || (str1.trim().equals("st")) || (str1.trim().equals("street")))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("locality");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("o"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("organization");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("ou"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("organizationalUnit");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("cn"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("inetOrgPerson");
      localBasicAttribute.add("idaPerson");
      localBasicAttributes.put(localBasicAttribute);
      localBasicAttributes.put(new BasicAttribute("cn", str2));
      localBasicAttributes.put(new BasicAttribute("sn", str2));
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("sn"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("inetOrgPerson");
      localBasicAttribute.add("idaPerson");
      localBasicAttributes.put(localBasicAttribute);
      localBasicAttributes.put(new BasicAttribute("cn", str2));
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("dc"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("domain");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("uid"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("inetOrgPerson");
      localBasicAttribute.add("idaPerson");
      localBasicAttributes.put(localBasicAttribute);
      localBasicAttributes.put(new BasicAttribute("sn", str2));
      localBasicAttributes.put(new BasicAttribute("cn", str2));
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("e"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("email");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
    else if (str1.trim().equals("t"))
    {
      localBasicAttributes = new BasicAttributes(true);
      localBasicAttribute = new BasicAttribute("objectClass");
      localBasicAttribute.add("top");
      localBasicAttribute.add("titleObject");
      localBasicAttributes.put(localBasicAttribute);
      this.operation.add(paramString, localBasicAttributes);
    }
  }

  private void addDistributionDN(BaseEntity paramBaseEntity)
    throws ISSException
  {
    String str1 = paramBaseEntity.getBaseDN().trim();
    String str2 = paramBaseEntity.getDistributionPath().trim();
    int i = str1.length();
    int j = str2.length();
    if (str2.indexOf(str1) != -1)
    {
      localObject1 = "";
      if (j > i)
        localObject1 = str2.substring(0, str2.length() - str1.length() - 1).trim();
      localObject2 = this.operation.IsExistEntry(str1);
      NamingEnumeration localNamingEnumeration2;
      if (localObject2 == null)
      {
        localObject3 = new ArrayList();
        for (localObject4 = str1; ((String)localObject4).indexOf(",") != -1; localObject4 = ((String)localObject4).substring(((String)localObject4).indexOf(",") + 1, ((String)localObject4).length()))
          ((ArrayList)localObject3).add(((String)localObject4).substring(0, ((String)localObject4).indexOf(",")));
        ((ArrayList)localObject3).add(localObject4);
        str3 = null;
        localStringBuffer = null;
        n = 0;
        i1 = ((ArrayList)localObject3).size();
        for (i2 = i1 - 1; i2 >= 0; i2--)
        {
          localStringBuffer = new StringBuffer();
          if (n == 0)
          {
            localStringBuffer.append(((ArrayList)localObject3).get(i2));
          }
          else
          {
            localStringBuffer.append(((ArrayList)localObject3).get(i2));
            localStringBuffer.append(",");
            localStringBuffer.append(str3);
          }
          n++;
          str3 = localStringBuffer.toString().trim();
          localNamingEnumeration2 = this.operation.IsExistEntry(str3);
          if (localNamingEnumeration2 != null)
            continue;
          addEntry(str3);
        }
      }
      if (((String)localObject1).equals(""))
        return;
      localObject3 = new ArrayList();
      for (localObject4 = localObject1; ((String)localObject4).indexOf(",") != -1; localObject4 = ((String)localObject4).substring(((String)localObject4).indexOf(",") + 1, ((String)localObject4).length()))
        ((ArrayList)localObject3).add(((String)localObject4).substring(0, ((String)localObject4).indexOf(",")));
      ((ArrayList)localObject3).add(localObject4);
      String str3 = null;
      StringBuffer localStringBuffer = null;
      n = 0;
      int i1 = ((ArrayList)localObject3).size();
      for (int i2 = i1 - 1; i2 >= 0; i2--)
      {
        localStringBuffer = new StringBuffer();
        if (n == 0)
        {
          localStringBuffer.append(((ArrayList)localObject3).get(i2));
        }
        else
        {
          localStringBuffer.append(((ArrayList)localObject3).get(i2));
          localStringBuffer.append(",");
          localStringBuffer.append(str3);
        }
        n++;
        str3 = localStringBuffer.toString().trim();
        localNamingEnumeration2 = this.operation.IsExistEntry(str3 + "," + str1);
        if (localNamingEnumeration2 != null)
          continue;
        addEntry(str3 + "," + str1);
      }
    }
    Object localObject1 = new ArrayList();
    for (Object localObject2 = str2; ((String)localObject2).indexOf(",") != -1; localObject2 = ((String)localObject2).substring(((String)localObject2).indexOf(",") + 1, ((String)localObject2).length()))
      ((ArrayList)localObject1).add(((String)localObject2).substring(0, ((String)localObject2).indexOf(",")));
    ((ArrayList)localObject1).add(localObject2);
    Object localObject3 = null;
    Object localObject4 = null;
    int k = 0;
    int m = ((ArrayList)localObject1).size();
    for (int n = m - 1; n >= 0; n--)
    {
      localObject4 = new StringBuffer();
      if (k == 0)
      {
        ((StringBuffer)localObject4).append(((ArrayList)localObject1).get(n));
      }
      else
      {
        ((StringBuffer)localObject4).append(((ArrayList)localObject1).get(n));
        ((StringBuffer)localObject4).append(",");
        ((StringBuffer)localObject4).append((String)localObject3);
      }
      k++;
      localObject3 = ((StringBuffer)localObject4).toString().trim();
      NamingEnumeration localNamingEnumeration1 = this.operation.IsExistEntry((String)localObject3);
      if (localNamingEnumeration1 != null)
        continue;
      addEntry((String)localObject3);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.opt.LDAPStore
 * JD-Core Version:    0.6.0
 */