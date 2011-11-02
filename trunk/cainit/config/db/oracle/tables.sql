drop table TCA_ADMIN purge
/
drop table TCA_ARL purge
/
drop table TCA_CASYS purge
/
drop table TCA_CA_CERT purge
/
drop table TCA_CERT_SELFEXT purge
/
drop table TCA_CERT_STANDARDEXT purge
/
drop table TCA_CERT_TEMPLATE purge
/
drop table TCA_CRL purge
/
drop table TCA_CRL_ARC purge
/
drop table TCA_CT_SELF_EXT purge
/
drop table TCA_CT_STANDARD_EXT purge
/
drop table TCA_DCRYPTO_CERT purge
/
drop table TCA_DHISTORY_CERT purge
/
drop table TCA_DREVOKED_CERT purge
/
drop table TCA_DSIGN_CERT purge
/
drop table TCA_ERR_LOG purge
/
drop table TCA_ERR_LOG_ARC purge
/
drop table TCA_ERR_LOG_EXCEPTION purge
/
drop table TCA_ERR_LOG_EXCEPTION_ARC purge
/
drop table TCA_FULL_CRL purge
/
drop table TCA_FULL_CRL_ARC purge
/
drop table TCA_NODE purge
/
drop table TCA_OPERATE_LOG purge
/
drop table TCA_OPERATE_LOG_ARC purge
/
drop table TCA_OPERATOR purge
/
drop table TCA_PRIVILEGE purge
/
drop table TCA_RA_CERT purge
/
drop table TCA_RA_CERT_TEMPLATE purge
/
drop table TCA_REFCODE purge
/
drop table TCA_ROLE purge
/
drop table TCA_ROLE_OPERATOR purge
/
drop table TCA_ROLE_PRIVILEGE purge
/
drop table TCA_SCRYPTO_CERT purge
/
drop table TCA_SELF_EXT purge
/
drop table TCA_SHISTORY_CERT purge
/
drop table TCA_SREVOKED_CERT purge
/
drop table TCA_SSIGN_CERT purge
/
drop table TCA_STANDARD_EXT purge
/
drop table TCA_SUB_CA purge
/
drop table TCA_SUB_CA_CERT purge
/
drop table TCA_SUB_CA_CERTSN purge
/
drop table TCA_SUB_CA_HISTORY_CERT purge
/
drop table TCA_SUB_CA_REVOKED_CERT purge
/
drop table TCA_SUP_CA_CERT purge
/
drop table TCA_URL purge
/
drop table TCA_URL_PRIVILEGE purge
/
drop table TCA_VALID_CERT_SN purge
/
drop table TCA_RA purge
/
drop table TCA_CONFIG purge
/
# Create table
create table TCA_ADMIN
(
CERT_SN     VARCHAR2(64) not null,
DN          VARCHAR2(256),
NOT_BEFORE  DATE,
NOT_AFTER   DATE,
SIGN_TIME   DATE,
CERT        CLOB,
KEYT_YPE    NUMBER(3),
KEY_LENGTH  NUMBER(4),
LOGON_TIMES NUMBER(15),
STATUS      NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_ADMIN
add constraint PK_TCA_ADMIN primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ARL
(
NAME        VARCHAR2(64) not null,
FILE_NAME   VARCHAR2(16),
MIN_CERT_ID VARCHAR2(64),
MAX_CERT_ID VARCHAR2(64),
SIGN_TIME   DATE,
UPDATE_TIME DATE,
URL         VARCHAR2(64),
ARL         CLOB,
LDAP_PUB    NUMBER(3),
ID          NUMBER(15) not null
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_ARL
add constraint PK_TCA_ARL primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CASYS
(
NAME         VARCHAR2(32) not null,
VALUE        VARCHAR2(4000),
VALUE_TYPE   VARCHAR2(32),
PDESCRIPTION VARCHAR2(128)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_CASYS
add constraint PK_TCA_CASYS primary key (NAME)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CA_CERT
(
CERT_SN        VARCHAR2(32) not null,
DN             VARCHAR2(256),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
SIGN_TIME      DATE,
KEY_TYPE       NUMBER(3),
KEY_NO         NUMBER(3),
KEY_LENGTH     NUMBER(4),
SIGN_ALG       NUMBER(3),
TYPE           NUMBER(3),
SUP_CA_CERT_SN VARCHAR2(32),
UPDATE_CERT_SN VARCHAR2(32),
LDAP_PUB       NUMBER(3),
STATUS         NUMBER(3),
CERT_CHAIN     BLOB,
CERT           BLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_CA_CERT
add constraint PK_TCA_CACERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CERT_SELFEXT
(
CERT_SN       VARCHAR2(64),
SELFEXT_ID    NUMBER(15),
SELFEXT_VALUE VARCHAR2(2048)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CERT_STANDARDEXT
(
CERT_SN   VARCHAR2(64),
EXT_ID    NUMBER(15),
EXT_VALUE VARCHAR2(2048)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CERT_TEMPLATE
(
CT_ID             NUMBER(15) not null,
CT_NAME           VARCHAR2(64),
CT_TYPE           VARCHAR2(32),
CT_STATUS         VARCHAR2(32),
CT_DESCRIPTION    VARCHAR2(256),
CT_XML            BLOB,
CT_SUPERIOR_CT_ID NUMBER(15),
CT_LEVEL          NUMBER(3),
CT_FLAG           VARCHAR2(32),
CREATE_TIME       DATE,
EXT               VARCHAR2(256)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_CERT_TEMPLATE
add constraint PK_TCA_CERT_TEMPLATE primary key (CT_ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CRL
(
ID          NUMBER(15) not null,
NAME        VARCHAR2(64) not null,
FILE_NAME   VARCHAR2(16),
MIN_CERT_ID VARCHAR2(64),
MAX_CERT_ID VARCHAR2(64),
SIGN_TIME   DATE,
UPDATE_TIME DATE,
URL         VARCHAR2(64),
LDAP_PUB    NUMBER(3),
CRL         BLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_CRL
add constraint PK_TCA_CRL primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CRL_ARC
(
ID          NUMBER(15) not null,
NAME        VARCHAR2(64) not null,
FILE_NAME   VARCHAR2(16),
MIN_CERT_ID VARCHAR2(64),
MAX_CERT_ID VARCHAR2(64),
SIGN_TIME   DATE,
UPDATE_TIME DATE,
URL         VARCHAR2(64),
LDAP_PUB    NUMBER(3),
CRL         BLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_CRL_ARC
add constraint PK_TCA_CRL_ARC primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CT_SELF_EXT
(
CT_ID      NUMBER(15),
SELFEXT_ID NUMBER(15),
NULLABLE   VARCHAR2(10),
VALUE      VARCHAR2(2000)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CT_STANDARD_EXT
(
CT_ID    NUMBER(15),
EXT_ID   NUMBER(15),
NULLABLE VARCHAR2(10)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_DCRYPTO_CERT
(
CERT_SN        VARCHAR2(64) not null,
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
CRL_URL        VARCHAR2(64),
CRL_NAME       VARCHAR2(16),
CERT           CLOB,
LDAP_PUB       NUMBER(3),
KEY_FROM       NUMBER(3),
KEY_ID         NUMBER(3),
RA_ID          NUMBER(15),
CT_ID          NUMBER(15),
UPDATE_CERT_SN VARCHAR2(64),
SIGN_CERT_SN   VARCHAR2(64),
STATUS         NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_DCRYPTO_CERT
add constraint PK_TCA_DCRYPTOCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_DCRYPTO_CERT
add constraint FK_TCA_DCRYPT_FK_TCA_DC_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create/Recreate indexes
create index ICA_VALID_CRYPT_CERT on TCA_DCRYPTO_CERT (CERT_SN, USER_TYPE)
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_DHISTORY_CERT
(
CERT_SN        VARCHAR2(64) not null,
DCERT_SN       VARCHAR2(64),
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
CERT_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
KEY_FROM       NUMBER(3),
KEY_NO         NUMBER(15),
CERT_URL       VARCHAR2(64),
CT_ID          NUMBER(15),
CRL_NAME       VARCHAR2(16),
RA_ID          NUMBER(15),
CERT           CLOB,
REVOKED        NUMBER(3),
REV_TIME       DATE,
REV_REASON     NUMBER(3),
LDAP_PUB       NUMBER(3),
UPDATE_CERT_SN VARCHAR2(64),
BACKUP_TIME    DATE
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_DHISTORY_CERT
add constraint PK_TCA_DHISTORYCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_DHISTORY_CERT
add constraint FK_TCA_DHIS_FK_TCA_DH_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create table
create table TCA_DREVOKED_CERT
(
CERT_SN       VARCHAR2(64) not null,
DCERT_SN      VARCHAR2(64),
DN            VARCHAR2(256),
VER           NUMBER(3),
NOT_BEFORE    DATE,
NOT_AFTER     DATE,
VALIDATE_TIME NUMBER(15),
SIGN_TIME     DATE,
SIGN_ALG      NUMBER(3),
KEY_TYPE      NUMBER(3),
KEY_LENGTH    NUMBER(4),
CERT_TYPE     NUMBER(3),
USER_TYPE     NUMBER(3),
KEY_FROM      NUMBER(3),
KEY_NO        NUMBER(15),
CT_ID         NUMBER(15),
REVOKE_TIME   DATE,
REVOKE_REASON NUMBER(3),
CRL_PUB       NUMBER(3),
CRL_URL       VARCHAR2(128),
CRL_NAME      VARCHAR2(16),
RA_ID         NUMBER(15),
LDAP_PUB      NUMBER(3),
UPDATE_CERTSN VARCHAR2(64),
CERT          CLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_DREVOKED_CERT
add constraint PK_TCA_DREVOKEDCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_DREVOKED_CERT
add constraint FK_TCA_DREV_FK_TCA_DR_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create table
create table TCA_DSIGN_CERT
(
CERT_SN        VARCHAR2(64) not null,
RA_ID          NUMBER(15),
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
CRL_URL        VARCHAR2(64),
CRL_NAME       VARCHAR2(16),
CT_ID          NUMBER(15),
CERT           CLOB,
LDAP_PUB       NUMBER(3),
UPDATE_CERT_SN VARCHAR2(64),
CRYPTO_CERT_SN VARCHAR2(64),
STATUS         NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_DSIGN_CERT
add constraint PK_TCA_DSIGNCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_DSIGN_CERT
add constraint FK_TCA_DSIG_FK_TCA_DS_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create table
create table TCA_ERR_LOG
(
ID           NUMBER(15) not null,
MODULE       VARCHAR2(64),
ERROR_CODE   VARCHAR2(16),
REASON       VARCHAR2(128),
OPERATE_TIME DATE,
EXCEPTION_ID NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 192
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_ERR_LOG
add constraint PK_TCA_ERR_LOG primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 128K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ERR_LOG_ARC
(
ID           NUMBER(15) not null,
MODULE       VARCHAR2(64),
ERROR_CODE   VARCHAR2(16),
LOCATION     VARCHAR2(64),
REASON       VARCHAR2(64),
OPERATE_TIME DATE,
EXCEPTION_ID NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 192
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_ERR_LOG_ARC
add constraint PK_TCA_ERR_LOG_ARC primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 128K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ERR_LOG_EXCEPTION
(
ID        NUMBER(15),
EXCEPTION CLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 128
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ERR_LOG_EXCEPTION_ARC
(
ID        NUMBER(15),
EXCEPTION CLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 128
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_FULL_CRL
(
ID          NUMBER(15) not null,
SIGN_TIME   DATE,
UPDATE_TIME DATE,
URL         VARCHAR2(64),
LDAP_PUB    NUMBER(3),
CRL         BLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 7
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_FULL_CRL
add constraint PK_TCA_FULLCRL primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 192K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_FULL_CRL_ARC
(
ID          NUMBER(15) not null,
SIGN_TIME   DATE,
UPDATE_TIME DATE,
URL         VARCHAR2(64),
LDAP_PUB    NUMBER(3),
CRL         BLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_FULL_CRL_ARC
add constraint PK_TCA_FULLCRL_ARC primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_NODE
(
OBJ_ID      VARCHAR2(50) not null,
FATHER_ID   VARCHAR2(50),
OBJ_NAME    VARCHAR2(100),
OBJ_TYPE    VARCHAR2(10),
OBJ_URL     VARCHAR2(256),
DESCRIPTION VARCHAR2(256),
NODE_INDEX  NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_NODE
add constraint PK_TRA_NODE primary key (OBJ_ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_OPERATE_LOG
(
ID               NUMBER(15) not null,
OPERATOR_CERT_SN VARCHAR2(80),
OPERATE_TYPE     VARCHAR2(64),
OBJECT_TYPE      VARCHAR2(32),
OBJECT_ID        VARCHAR2(32),
RESULT           VARCHAR2(32),
ERR_LOG_ID       NUMBER(15),
CLIENT_IP        VARCHAR2(32),
OPERATE_TIME     DATE,
DESCRIPTION      VARCHAR2(128)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 640
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_OPERATE_LOG
add constraint PK_TCA_OPERATE_LOG primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 128K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_OPERATE_LOG_ARC
(
ID               NUMBER(15) not null,
OPERATOR_CERT_SN VARCHAR2(80),
OPERATE_TYPE     VARCHAR2(64),
OBJECT_TYPE      VARCHAR2(32),
OBJECT_ID        VARCHAR2(32),
RESULT           VARCHAR2(32),
ERR_LOG_ID       NUMBER(15),
CLIENT_IP        VARCHAR2(32),
OPERATE_TIME     DATE,
DESCRIPTION      VARCHAR2(128)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 640
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_OPERATE_LOG_ARC
add constraint PK_TCA_OPERATE_LOG_ARC primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 128K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_OPERATOR
(
CERT_SN       VARCHAR2(64) not null,
SUBJECT       VARCHAR2(256) not null,
NOT_BEFORE    DATE,
NOT_AFTER     DATE,
SIGN_TIME     DATE,
CERT          VARCHAR2(4000),
KEY_TYPE      VARCHAR2(50),
KEY_LENGTH    NUMBER(15),
LOGON_TIMES   NUMBER(15),
AGENT_ID      NUMBER(15),
ROLE_STATUS   NUMBER(3),
CT_STATUS     NUMBER(3),
STATUS        NUMBER(3),
FREEZE_STATUS NUMBER(3),
AUTH_TYPE     VARCHAR2(2)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_OPERATOR
add constraint PK_TRA_OPERATOR primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create/Recreate indexes
create index INDEX_TCA_OPERATOR on TCA_OPERATOR (SUBJECT)
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_PRIVILEGE
(
ID          NUMBER(15) not null,
NAME        VARCHAR2(64) not null,
CODE        VARCHAR2(50),
DESCRIPTION VARCHAR2(64),
FLAG        VARCHAR2(20),
MODEFLAG    VARCHAR2(2)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_PRIVILEGE
add constraint PK_TRA_PRIVILEGE primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_RA
(
ID               NUMBER(15) not null,
NAME             VARCHAR2(32),
CRED_TYPE        NUMBER(3),
CRED_ID          VARCHAR2(32),
POST_CODE        VARCHAR2(32),
ADDRESS          VARCHAR2(128),
PHONE            VARCHAR2(16),
FAX              VARCHAR2(32),
EMAIL            VARCHAR2(64),
DEPUTY_NAME      VARCHAR2(12),
DEPUTY_CARD_ID   VARCHAR2(32),
DEPUTY_PHONE     VARCHAR2(16),
DEPUTY_MOBILE    VARCHAR2(16),
DEPUTY_POST_CODE VARCHAR2(32),
DEPUTY_ADDRESS   VARCHAR2(128),
DEPUTY_EMAIL     VARCHAR2(64),
REG_TIME         DATE,
STATUS           NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_RA
add constraint PK_TCA_RA primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_RA_CERT
(
CERT_SN        VARCHAR2(64) not null,
RA_ID          NUMBER(15),
DN             VARCHAR2(256),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
SIGN_TIME      DATE,
CERT           CLOB,
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
LOGON_TIMES    NUMBER(15),
UPDATE_CERT_SN VARCHAR2(64),
STATUS         NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_RA_CERT
add constraint PK_TCA_RACERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_RA_CERT_TEMPLATE
(
RA_ID            NUMBER(15) not null,
CERT_TEMPLATE_ID NUMBER(15) not null
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_RA_CERT_TEMPLATE
add constraint PK_TCA_RA_CERT_TPMPLATE primary key (RA_ID, CERT_TEMPLATE_ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_REFCODE
(
CERT_REF  VARCHAR2(64) not null,
AUTH_CODE VARCHAR2(64),
STATUS    NUMBER(3),
REG_TIME  DATE,
CERT_TYPE NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_REFCODE
add constraint PK_TCA_REFCODE primary key (CERT_REF)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ROLE
(
ID          NUMBER(15) not null,
NAME        VARCHAR2(32) not null,
DESCRIPTION VARCHAR2(64)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_ROLE
add constraint PK_TRA_ROLE primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ROLE_OPERATOR
(
OPERATOR_CERT_SN VARCHAR2(64),
ROLE_ID          NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_ROLE_PRIVILEGE
(
ROLE_ID      NUMBER(15),
PRIVILEGE_ID NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SCRYPTO_CERT
(
CERT_SN        VARCHAR2(64) not null,
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
CRL_URL        VARCHAR2(64),
CRL_NAME       VARCHAR2(16),
RA_ID          NUMBER(15),
CERT           CLOB,
LDAP_PUB       NUMBER(3),
KEY_FROM       NUMBER(3),
KEY_ID         NUMBER(3),
CT_ID          NUMBER(15),
UPDATE_CERT_SN VARCHAR2(64),
SIGN_CERT_SN   VARCHAR2(64),
STATUS         NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SCRYPTO_CERT
add constraint PK_TCA_SCRYPTOCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_SCRYPTO_CERT
add constraint FK_TCA_SCRY_FK_TCA_SC_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create/Recreate indexes
create index ICA_VALID_SCRYPT_CERT on TCA_SCRYPTO_CERT (CERT_SN, USER_TYPE)
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SELF_EXT
(
SELFEXT_ID     NUMBER(15) not null,
SELFEXT_OID    VARCHAR2(64) not null,
SELFEXT_NAME   VARCHAR2(128),
ENCODING_TYPE  VARCHAR2(40),
SELFEXT_STATUS VARCHAR2(40),
SELFEXT_DESC   VARCHAR2(1024),
USE_COUNT      NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SELF_EXT
add constraint PK_TCA_SELFEXT primary key (SELFEXT_ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SHISTORY_CERT
(
CERT_SN        VARCHAR2(64) not null,
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
CERT_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
KEY_FROM       NUMBER(3),
KEY_NO         NUMBER(15),
CERT_URL       VARCHAR2(64),
CRL_NAME       VARCHAR2(16),
RA_ID          NUMBER(15),
CERT           CLOB,
REVOKED        NUMBER(3),
REV_TIME       DATE,
REV_REASON     NUMBER(3),
CT_ID          NUMBER(15),
LDAP_PUB       NUMBER(3),
UPDATE_CERT_SN VARCHAR2(64),
BACKUP_TIME    DATE
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SHISTORY_CERT
add constraint PK_TCA_SHISTORYCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_SHISTORY_CERT
add constraint FK_TCA_SHIS_FK_TCA_SH_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create table
create table TCA_SREVOKED_CERT
(
CERT_SN       VARCHAR2(64) not null,
DN            VARCHAR2(256),
VER           NUMBER(3),
NOT_BEFORE    DATE,
NOT_AFTER     DATE,
VALIDATE_TIME NUMBER(15),
SIGN_TIME     DATE,
SIGN_ALG      NUMBER(3),
KEY_TYPE      NUMBER(3),
KEY_LENGTH    NUMBER(4),
CERT_TYPE     NUMBER(3),
USER_TYPE     NUMBER(3),
KEY_FROM      NUMBER(3),
KEY_NO        NUMBER(15),
REVOKE_TIME   DATE,
REVOKE_REASON NUMBER(3),
CRL_PUB       NUMBER(3),
CRL_URL       VARCHAR2(128),
CRL_NAME      VARCHAR2(16),
RA_ID         NUMBER(15),
CT_ID         NUMBER(15),
LDAP_PUB      NUMBER(3),
UPDATE_CERTSN VARCHAR2(64),
CERT          CLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SREVOKED_CERT
add constraint PK_TCA_SREVOKEDCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_SREVOKED_CERT
add constraint FK_TCA_SREV_FK_TCA_SR_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create table
create table TCA_SSIGN_CERT
(
CERT_SN        VARCHAR2(64) not null,
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
CRL_URL        VARCHAR2(256),
CRL_NAME       VARCHAR2(16),
RA_ID          NUMBER(15),
CT_ID          NUMBER(15),
CERT           CLOB,
LDAP_PUB       NUMBER(3),
UPDATE_CERT_SN VARCHAR2(64),
STATUS         NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 384
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SSIGN_CERT
add constraint PK_TCA_SSIGNCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
alter table TCA_SSIGN_CERT
add constraint FK_TCA_SSIG_FK_TCA_SS_TCA_RA foreign key (RA_ID)
references TCA_RA (ID)
/
# Create/Recreate indexes
create index ICA_VALID_SIGN_CERT on TCA_SSIGN_CERT (CERT_SN, USER_TYPE, KEY_TYPE)
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_STANDARD_EXT
(
EXT_ID          NUMBER(15) not null,
EXT_OID         VARCHAR2(128) not null,
EXT_NAME        VARCHAR2(128),
EXT_CHILDNAME   VARCHAR2(128),
EXT_CHILDOID    VARCHAR2(128),
EXT_ENCODETYPE  VARCHAR2(64),
EXT_DESCRIPTION VARCHAR2(1024)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_STANDARD_EXT
add constraint PK_TCA_STANDARDEXT primary key (EXT_ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SUB_CA
(
ID               NUMBER not null,
CA_LEVEL         NUMBER(3),
NAME             VARCHAR2(32),
CRED_TYPE        NUMBER(3),
CRED_ID          VARCHAR2(32),
PHONE            VARCHAR2(16),
FAX              VARCHAR2(32),
POST_CODE        VARCHAR2(32),
ADDRESS          VARCHAR2(64),
EMAIL            VARCHAR2(64),
DEPUTY_NAME      VARCHAR2(12),
DEPUTY_PHONE     VARCHAR2(16),
DEPUTY_MOBILE    VARCHAR2(16),
DEPUTY_CARDID    VARCHAR2(32),
DEPUTY_POST_CODE VARCHAR2(32),
DEPUTY_ADDRESS   VARCHAR2(128),
DEPUTY_EMAIL     VARCHAR2(32),
REG_TIME         DATE
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SUB_CA
add constraint PK_TCA_SUBCA primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SUB_CA_CERT
(
CERT_SN        VARCHAR2(64) not null,
SUB_CA_ID      NUMBER(15),
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
SIGN_ALG       NUMBER(3),
CRL_URL        VARCHAR2(64),
CRL_NAME       VARCHAR2(16),
LDAP_PUB       NUMBER(3),
CERT           CLOB,
UPDATE_CERT_SN VARCHAR2(64),
STATUS         NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SUB_CA_CERT
add constraint PK_TCA_SUBCACERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SUB_CA_CERTSN
(
ID            NUMBER(15) not null,
BEGIN_CERT_SN VARCHAR2(64),
END_CERT_SN   VARCHAR2(64),
STATUS        NUMBER(3),
REG_TIME      DATE,
SUB_CA_ID     NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SUB_CA_CERTSN
add constraint PK_TCA_SUBCACERTSN primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SUB_CA_HISTORY_CERT
(
CERT_SN        VARCHAR2(64) not null,
SUB_CA_ID      NUMBER(15),
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
USER_TYPE      NUMBER(3),
CERT_TYPE      NUMBER(3),
SIGN_ALG       NUMBER(3),
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
KEY_NO         NUMBER(15),
CRL_URL        VARCHAR2(64),
CRL_NAME       VARCHAR2(16),
CERT           CLOB,
REVOKED        NUMBER(3),
REV_TIME       DATE,
REV_REASON     NUMBER(3),
LDAP_PUB       NUMBER(3),
UPDATE_CERT_SN VARCHAR2(64),
BACKUP_TIME    DATE
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SUB_CA_HISTORY_CERT
add constraint PK_TCA_SUBCAHISTORYCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SUB_CA_REVOKED_CERT
(
CERT_SN        VARCHAR2(64) not null,
SUB_CA_ID      NUMBER(15),
DN             VARCHAR2(256),
VER            NUMBER(3),
NOT_BEFORE     DATE,
NOT_AFTER      DATE,
VALIDATE_TIME  NUMBER(15),
SIGN_TIME      DATE,
SIGN_ALG       NUMBER(3),
KEY_TYPE       NUMBER(3),
KEY_LENGTH     NUMBER(4),
CERT_TYPE      NUMBER(3),
REVOKE_TIME    DATE,
REVOKE_REASON  NUMBER(3),
CRL_PUB        NUMBER(3),
CRL_URL        VARCHAR2(128),
CRL_NAME       VARCHAR2(16),
LDAP_PUB       NUMBER(3),
UPDATE_CERT_SN VARCHAR2(64),
CERT           CLOB
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SUB_CA_REVOKED_CERT
add constraint PK_TCA_SUBCAREVOKEDCERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_SUP_CA_CERT
(
CERT_SN    VARCHAR2(64) not null,
DN         VARCHAR2(256),
NOT_BEFORE DATE,
NOT_AFTER  DATE,
SIGN_TIME  DATE,
CERT       CLOB,
KEY_TYPE   NUMBER(3),
KEY_LENGTH NUMBER(4),
CERT_CHAIN CLOB,
STATUS     NUMBER(3)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_SUP_CA_CERT
add constraint PK_TCA_SUPCACERT primary key (CERT_SN)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_URL
(
ID          NUMBER(15) not null,
NAME        VARCHAR2(256),
DESCRIPTION VARCHAR2(256)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_URL
add constraint PK_TCA_URL primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_URL_PRIVILEGE
(
URL_ID       NUMBER(15),
PRIVILEGE_ID NUMBER(15)
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create table
create table TCA_CONFIG
(
  MODULENAME  VARCHAR2(50) not null,
  PROPERTY    VARCHAR2(255) not null,
  VALUE       VARCHAR2(4000),
  ISENCRYPTED CHAR(1),
  SIGN_SERVER VARCHAR2(255),
  SIGN_CLIENT VARCHAR2(255)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 16
    minextents 1
    maxextents unlimited
  )
  /
# Create/Recreate primary, unique and foreign key constraints 
alter table TCA_CONFIG
  add constraint PK_CONFIG primary key (MODULENAME, PROPERTY)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  )
  /
# Create table
create table TCA_VALID_CERT_SN
(
ID  NUMBER(15) not null,
BEGIN_CERT_SN VARCHAR2(64),
END_CERT_SN   VARCHAR2(64),
STATUS NUMBER(3),
REG_TIME  DATE
)
tablespace SIRA
pctfree 10
initrans 1
maxtrans 255
storage
(
initial 64
minextents 1
maxextents unlimited
)
/
# Create/Recreate primary, unique and foreign key constraints
alter table TCA_VALID_CERT_SN
add constraint PK_TCA_VALIDCERTSN primary key (ID)
using index
tablespace SIRA
pctfree 10
initrans 2
maxtrans 255
storage
(
initial 64K
minextents 1
maxextents unlimited
)
/
