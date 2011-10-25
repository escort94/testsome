package cn.com.jit.ida.ca.issue.opt;

import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.entity.BaseEntity;

public abstract interface Store
{
  public abstract void add(BaseEntity paramBaseEntity)
    throws ISSException;

  public abstract void delete(BaseEntity paramBaseEntity)
    throws ISSException;
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.opt.Store
 * JD-Core Version:    0.6.0
 */