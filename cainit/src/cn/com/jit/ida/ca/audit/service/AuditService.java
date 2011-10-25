package cn.com.jit.ida.ca.audit.service;

import cn.com.jit.ida.Service;
import cn.com.jit.ida.ca.audit.AuditException;

public abstract class AuditService
  implements Service
{
  abstract void validateCheck()
    throws AuditException;
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.audit.service.AuditService
 * JD-Core Version:    0.6.0
 */