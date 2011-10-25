package cn.com.jit.ida.ca.certmanager.reqinfo;

public class CertExtensions
{
  Extension[] exts;

  public CertExtensions(Extension[] paramArrayOfExtension)
  {
    this.exts = paramArrayOfExtension;
  }

  public String getValueByName(String paramString)
  {
    for (int i = 0; i < this.exts.length; i++)
      if (this.exts[i].getName().equals(paramString))
        return this.exts[i].getValue();
    return null;
  }

  public int getExtensionsCount()
  {
    return this.exts.length;
  }

  public String getValueByOID(String paramString)
  {
    for (int i = 0; i < this.exts.length; i++)
      if (this.exts[i].getOid().equals(paramString))
        return this.exts[i].getValue();
    return null;
  }

  public boolean isContained(String paramString)
  {
    String str = getValueByName(paramString);
    return str != null;
  }

  public Extension getExtension(int paramInt)
  {
    return this.exts[paramInt];
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions
 * JD-Core Version:    0.6.0
 */