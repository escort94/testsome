#==============================================================
# Database name:  DB2
# DBMS name:      IBM DB2 UDB 7.x Common Server
# Created on:     2006-3-27 15:49:58
#==============================================================


drop table ADMIN;

drop table BA_PRIVILEGE;

drop table BA_ROLE;

drop table CERT;

drop table CERTARC;

drop table CERTARCFORKMC;

drop table CERTTBP;

drop table CERT_SELFEXT;

drop table CERT_SELFEXT_ARC;

drop table CERT_STANDARD_EXT;

drop table CERT_STANDARD_EXT_ARC;

drop table CONFIG;

drop table CRL;

drop table CTML;

drop table OPERATIONLOG;

drop table OPERATIONLOGARC;

drop table PENDINGTASK;

drop table PRIVILEGE;

drop table RA_BASEDN;

drop table REVOKEDCERT;

drop table REVOKEDCERTARC;

drop table ROOTCERT;

drop table SELFEXT;

drop table SYSINFO;

#==============================================================
# Table: ADMIN
#==============================================================
create table ADMIN
(
   CERTSN               VARCHAR(80)            not null,
   ROLE_ID              VARCHAR(100)           not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_ADMIN primary key (CERTSN, ROLE_ID)
);

#==============================================================
# Table: BA_PRIVILEGE
#==============================================================
create table BA_PRIVILEGE
(
   CERTSN               VARCHAR(80)            not null,
   CTMLNAME             VARCHAR(256)           not null,
   BASEDN               VARCHAR(510)           not null,
   OPERATION            VARCHAR(510),
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_BA_PRIVILEGE primary key (CERTSN, CTMLNAME, BASEDN)
);

#==============================================================
# Table: BA_ROLE
#==============================================================
create table BA_ROLE
(
   ROLE_ID              VARCHAR(100)           not null,
   ROLENAME             VARCHAR(100)           not null,
   PRIVILEGE_ID         VARCHAR(100)           not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_ROLE primary key (ROLE_ID, PRIVILEGE_ID)
);

#==============================================================
# Index: BA_ROLE_I1
#==============================================================
create unique index BA_ROLE_I1 on BA_ROLE (
   ROLENAME             ASC,
   PRIVILEGE_ID         ASC
);

#==============================================================
# Table: CERT
#==============================================================
create table CERT
(
   CERTSN               VARCHAR(80)            not null,
   SUBJECTUPPERCASE     VARCHAR(510)           not null,
   SUBJECT              VARCHAR(510)           not null,
   NOTBEFORE            BIGINT                 not null,
   NOTAFTER             BIGINT                 not null,
   VALIDITY             INT                    not null,
   AUTHCODE             VARCHAR(120)           not null,
   CDPID                INT                    not null,
   CTMLNAME             VARCHAR(256)           not null,
   CERTSTATUS           VARCHAR(24)            not null,
   ISVALID              BIGINT                 not null,
   CREATETIME           BIGINT,
   APPLICANTUPPERCASE   VARCHAR(510),
   APPLICANT            VARCHAR(510)           not null,
   CERTENTITY           BLOB,
   EMAIL                VARCHAR(200),
   REMARK               VARCHAR(510),
   AUTHCODE_UPDATETIME  BIGINT,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   ISRETAINKEY          VARCHAR(80),
   ISWAITING            CHAR(1),
   OLDSN                VARCHAR(80),
   constraint P_CERT primary key (CERTSN)
);

#==============================================================
# Index: CERT_I1
#==============================================================
create  index CERT_I1 on CERT (
   CTMLNAME             ASC,
   SUBJECTUPPERCASE     ASC,
   ISVALID              ASC
);

#==============================================================
# Index: CERT_I2
#==============================================================
create  index CERT_I2 on CERT (
   APPLICANTUPPERCASE   ASC
);

#==============================================================
# Table: CERTARC
#==============================================================
create table CERTARC
(
   CERTSN               VARCHAR(80)            not null,
   SUBJECTUPPERCASE     VARCHAR(510)           not null,
   SUBJECT              VARCHAR(510)           not null,
   NOTBEFORE            BIGINT                 not null,
   NOTAFTER             BIGINT                 not null,
   VALIDITY             INT                    not null,
   AUTHCODE             VARCHAR(120)           not null,
   CDPID                INT                    not null,
   CTMLNAME             VARCHAR(256)           not null,
   CERTSTATUS           VARCHAR(24)            not null,
   ISVALID              BIGINT                 not null,
   CREATETIME           BIGINT,
   APPLICANTUPPERCASE   VARCHAR(510),
   APPLICANT            VARCHAR(510)           not null,
   CERTENTITY           BLOB,
   EMAIL                VARCHAR(200),
   REMARK               VARCHAR(4000),
   AUTHCODE_UPDATETIME  BIGINT,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   ISRETAINKEY          VARCHAR(80),
   ISWAITING            CHAR(1),
   OLDSN                VARCHAR(80),
   constraint "P_Key_1" primary key (CERTSN)
);

#==============================================================
# Index: CERTARC_I1
#==============================================================
create  index CERTARC_I1 on CERTARC (
   CTMLNAME             ASC,
   SUBJECTUPPERCASE     ASC,
   ISVALID              ASC
);

#==============================================================
# Index: CERTARC_I2
#==============================================================
create  index CERTARC_I2 on CERTARC (
   APPLICANTUPPERCASE   ASC
);

#==============================================================
# Table: CERTARCFORKMC
#==============================================================
create table CERTARCFORKMC
(
   CERTSN               VARCHAR(80)            not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (CERTSN)
);

#==============================================================
# Table: CERTTBP
#==============================================================
create table CERTTBP
(
   CERTSN               VARCHAR(80)            not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_CERTTBP primary key (CERTSN)
);

#==============================================================
# Table: CERT_SELFEXT
#==============================================================
create table CERT_SELFEXT
(
   CERTSN               VARCHAR(80)            not null,
   OID                  VARCHAR(256)           not null,
   SELFEXT_NAME         VARCHAR(256)           not null,
   VALUE                VARCHAR(8000)          not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_CERT_SELFEXT primary key (CERTSN, OID)
);

#==============================================================
# Table: CERT_SELFEXT_ARC
#==============================================================
create table CERT_SELFEXT_ARC
(
   CERTSN               VARCHAR(80)            not null,
   OID                  VARCHAR(256)           not null,
   SELFEXT_NAME         VARCHAR(256)           not null,
   VALUE                VARCHAR(8000)          not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (CERTSN, OID)
);

#==============================================================
# Table: CERT_STANDARD_EXT
#==============================================================
create table CERT_STANDARD_EXT
(
   CERTSN               VARCHAR(80)            not null,
   EXT_OID              VARCHAR(256)           not null,
   EXT_NAME             VARCHAR(256)           not null,
   CHILD_NAME           VARCHAR(256)           not null,
   OTHERNAME_OID        VARCHAR(256),
   VALUE                VARCHAR(8000),
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (CERTSN, CHILD_NAME)
);

#==============================================================
# Table: CERT_STANDARD_EXT_ARC
#==============================================================
create table CERT_STANDARD_EXT_ARC
(
   CERTSN               VARCHAR(80)            not null,
   EXT_OID              VARCHAR(256)           not null,
   EXT_NAME             VARCHAR(256)           not null,
   CHILD_NAME           VARCHAR(256)           not null,
   OTHERNAME_OID        VARCHAR(256),
   VALUE                VARCHAR(8000),
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (CERTSN, CHILD_NAME)
);

#==============================================================
# Table: CONFIG
#==============================================================
create table CONFIG
(
   MODULENAME           VARCHAR(100)           not null,
   PROPERTY             VARCHAR(510)           not null,
   VALUE                VARCHAR(8000),
   ISENCRYPTED          CHAR(1),
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_CONFIG primary key (MODULENAME, PROPERTY)
);

#==============================================================
# Table: CRL
#==============================================================
create table CRL
(
   CRL_NAME             VARCHAR(255)           not null,
   CRL_ENTITY           BLOB                   not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (CRL_NAME)
);

#==============================================================
# Table: CTML
#==============================================================
create table CTML
(
   CTML_NAME            VARCHAR(256)           not null,
   CTML_ID              VARCHAR(256)           not null,
   CTML_TYPE            VARCHAR(128)           not null,
   CTML_STATUS          VARCHAR(128)           not null,
   CTML_DESCRIPTION     VARCHAR(2048),
   CTML_POLICYINFO      VARCHAR(16000),
   RESERVE              VARCHAR(4000),
   CREATETIME           BIGINT                 not null,
   SIGN_SERVER          VARCHAR(4000),
   SIGN_CLIENT          VARCHAR(4000),
   constraint P_CTML primary key (CTML_NAME)
);

#==============================================================
# Index: CTML_I1
#==============================================================
create unique index CTML_I1 on CTML (
   CTML_ID              ASC
);

#==============================================================
# Table: OPERATIONLOG
#==============================================================
create table OPERATIONLOG
(
   ID                   VARCHAR(80)            not null,
   OPERATORSN           VARCHAR(80),
   OPERATORSUBJECTUPPERCASE VARCHAR(510)           not null,
   OPERATORSUBJECT      VARCHAR(510)           not null,
   OBJECTCERTSN         VARCHAR(80),
   OBJECTSUBJECTUPPERCASE VARCHAR(510),
   OBJECTSUBJECT        VARCHAR(510),
   OBJECTCTMLNAME       VARCHAR(510),
   OPTTYPE              VARCHAR(60)            not null,
   OPTTIME              BIGINT                 not null,
   "RESULT"             SMALLINT               not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (ID)
);

#==============================================================
# Index: OPERATIONLOG_I1
#==============================================================
create  index OPERATIONLOG_I1 on OPERATIONLOG (
   OPERATORSUBJECTUPPERCASE ASC
);

#==============================================================
# Index: OPERATIONLOG_I2
#==============================================================
create  index OPERATIONLOG_I2 on OPERATIONLOG (
   OBJECTCERTSN         ASC
);

#==============================================================
# Index: OPERATIONLOG_I3
#==============================================================
create  index OPERATIONLOG_I3 on OPERATIONLOG (
   OBJECTSUBJECTUPPERCASE ASC
);

#==============================================================
# Index: OPERATIONLOG_I4
#==============================================================
create  index OPERATIONLOG_I4 on OPERATIONLOG (
   OPTTIME              ASC
);

#==============================================================
# Table: OPERATIONLOGARC
#==============================================================
create table OPERATIONLOGARC
(
   ID                   VARCHAR(80)            not null,
   OPERATORSN           VARCHAR(80),
   OPERATORSUBJECTUPPERCASE VARCHAR(510)           not null,
   OPERATORSUBJECT      VARCHAR(510)           not null,
   OBJECTCERTSN         VARCHAR(80),
   OBJECTSUBJECTUPPERCASE VARCHAR(510),
   OBJECTSUBJECT        VARCHAR(510),
   OBJECTCTMLNAME       VARCHAR(510),
   OPTTYPE              VARCHAR(60)            not null,
   OPTTIME              BIGINT                 not null,
   "RESULT"             SMALLINT               not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (ID)
);

#==============================================================
# Index: OPERATIONLOGARC_I1
#==============================================================
create  index OPERATIONLOGARC_I1 on OPERATIONLOGARC (
   OPERATORSUBJECTUPPERCASE ASC
);

#==============================================================
# Index: OPERATIONLOGARC_I2
#==============================================================
create  index OPERATIONLOGARC_I2 on OPERATIONLOGARC (
   OBJECTCERTSN         ASC
);

#==============================================================
# Index: OPERATIONLOGARC_I3
#==============================================================
create  index OPERATIONLOGARC_I3 on OPERATIONLOGARC (
   OBJECTSUBJECTUPPERCASE ASC
);

#==============================================================
# Index: OPERATIONLOGARC_I4
#==============================================================
create  index OPERATIONLOGARC_I4 on OPERATIONLOGARC (
   OPTTIME              ASC
);

#==============================================================
# Table: PENDINGTASK
#==============================================================
create table PENDINGTASK
(
   TASKID               VARCHAR(80)            not null,
   CERTSN               VARCHAR(80),
   SUBJECT              VARCHAR(510),
   EXECTIME             DECIMAL(19),
   REASONID             DECIMAL(6),
   REASONDESC           VARCHAR(510),
   OPTTYPE              VARCHAR(60),
   CDPID                DECIMAL(19)            not null,
   APPLICANT            VARCHAR(510)           not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (TASKID)
);

#==============================================================
# Table: PRIVILEGE
#==============================================================
create table PRIVILEGE
(
   PRIVILEGE_ID         VARCHAR(100)           not null,
   PRIVILEGE            VARCHAR(100)           not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_PRIVILEGE primary key (PRIVILEGE_ID)
);

#==============================================================
# Index: PRIVILEGE_I1
#==============================================================
create unique index PRIVILEGE_I1 on PRIVILEGE (
   PRIVILEGE            ASC
);

#==============================================================
# Table: RA_BASEDN
#==============================================================
create table RA_BASEDN
(
   CERTSN               VARCHAR(80)            not null,
   RA_BASEDN            VARCHAR(510)           not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_RA_BASEDN primary key (CERTSN)
);

#==============================================================
# Table: REVOKEDCERT
#==============================================================
create table REVOKEDCERT
(
   CERTSN               VARCHAR(80)            not null,
   CDPID                BIGINT                 not null,
   REASON               SMALLINT               not null,
   REASONDESC           VARCHAR(510),
   REVOKETIME           BIGINT                 not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   CERTNOTAFTER         BIGINT,
   constraint P_REVOKEDCERT primary key (CERTSN)
);

#==============================================================
# Index: REVOKEDCERT_I1
#==============================================================
create  index REVOKEDCERT_I1 on REVOKEDCERT (
   CDPID                ASC
);

#==============================================================
# Table: REVOKEDCERTARC
#==============================================================
create table REVOKEDCERTARC
(
   CERTSN               VARCHAR(80)            not null,
   CDPID                BIGINT                 not null,
   REASON               SMALLINT               not null,
   REASONDESC           VARCHAR(510),
   REVOKETIME           BIGINT                 not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   CERTNOTAFTER         BIGINT,
   constraint "P_Key_1" primary key (CERTSN)
);

#==============================================================
# Index: REVOKEDCERTARC_I1
#==============================================================
create  index REVOKEDCERTARC_I1 on REVOKEDCERTARC (
   CDPID                ASC
);

#==============================================================
# Table: ROOTCERT
#==============================================================
create table ROOTCERT
(
   SN                   VARCHAR(80)            not null,
   CA_ID                BIGINT                 not null,
   CA_DESC              VARCHAR(510),
   SUBJECTUPPERCASE     VARCHAR(510)           not null,
   PRIVATE_KEY          VARCHAR(8000)          not null,
   CERTSTATUS           VARCHAR(24)            not null,
   CERTENTITY           BLOB                   not null,
   DEVICE_ID            VARCHAR(40)            not null,
   REMARK               VARCHAR(510),
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_ROOTCERT primary key (SN)
);

#==============================================================
# Table: SELFEXT
#==============================================================
create table SELFEXT
(
   SELFEXT_NAME         VARCHAR(256)           not null,
   SELFEXT_OID          VARCHAR(256)           not null,
   SELFEXT_STATUS       VARCHAR(128)           not null,
   SELFEXT_ENCODETYPE   VARCHAR(128)           not null,
   SELFEXT_DESCRIPTION  VARCHAR(2048),
   RESERVE              VARCHAR(8000),
   CREATETIME           BIGINT                 not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint P_SELFEXT primary key (SELFEXT_NAME)
);

#==============================================================
# Index: SELFEXT_I1
#==============================================================
create unique index SELFEXT_I1 on SELFEXT (
   SELFEXT_OID          ASC
);

#==============================================================
# Table: SYSINFO
#==============================================================
create table SYSINFO
(
   SYSNAME              VARCHAR(160)           not null,
   VERSION              VARCHAR(160)           not null,
   SIGN_SERVER          VARCHAR(8000),
   SIGN_CLIENT          VARCHAR(8000),
   constraint "P_Key_1" primary key (SYSNAME)
);

alter table ADMIN
   add constraint FK_ADMIN foreign key (CERTSN)
      references CERT (CERTSN)
      on delete no action on update no action;

alter table BA_PRIVILEGE
   add constraint FK_BA_PRIVILEGE foreign key (CERTSN)
      references CERT (CERTSN)
      on delete no action on update no action;

alter table BA_PRIVILEGE
   add constraint FK_BA_CTML foreign key (CTMLNAME)
      references CTML (CTML_NAME)
      on delete restrict on update restrict;

alter table BA_ROLE
   add constraint FK_ROLE foreign key (PRIVILEGE_ID)
      references PRIVILEGE (PRIVILEGE_ID)
      on delete no action on update no action;

alter table CERT
   add constraint FK_CERT foreign key (CTMLNAME)
      references CTML (CTML_NAME)
      on delete no action on update no action;

alter table CERTTBP
   add constraint FK_CERTTBP foreign key (CERTSN)
      references CERT (CERTSN)
      on delete no action on update no action;

alter table CERT_SELFEXT
   add constraint FK_CERT_SELFEXT foreign key (CERTSN)
      references CERT (CERTSN)
      on delete no action on update no action;

alter table CERT_SELFEXT
   add constraint FK_CERT_SELFEXT2 foreign key (SELFEXT_NAME)
      references SELFEXT (SELFEXT_NAME)
      on delete no action on update no action;

