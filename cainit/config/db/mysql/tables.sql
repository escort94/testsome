#==============================================================*/
# Database name:  MySQL                                        */
# DBMS name:      MySQL 3.23                                   */
# Created on:     2006-3-27 15:50:16                           */
#==============================================================*/


drop table if exists admin;

drop table if exists ba_privilege;

drop table if exists ba_role;

drop table if exists cert;

drop table if exists certarc;

drop table if exists certarcforkmc;

drop table if exists certtbp;

drop table if exists cert_selfext;

drop table if exists cert_selfext_arc;

drop table if exists cert_standard_ext;

drop table if exists cert_standard_ext_arc;

drop table if exists config;

drop table if exists crl;

drop table if exists ctml;

drop table if exists operationlog;

drop table if exists operationlogarc;

drop table if exists pendingtask;

drop table if exists privilege;

drop table if exists ra_basedn;

drop table if exists revokedcert;

drop table if exists revokedcertarc;

drop table if exists rootcert;

drop table if exists selfext;

drop table if exists sysinfo;

#==============================================================*/
# Table: admin                                                 */
#==============================================================*/
create table if not exists admin
(
   certsn                         varchar(40)                    not null,
   role_id                        varchar(50)                    not null,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn, role_id)
)
type = innodb character set gbk;

#==============================================================*/
# Table: ba_privilege                                          */
#==============================================================*/
create table if not exists ba_privilege
(
   certsn                         varchar(40)                    not null,
   ctmlname                       varchar(128)                   not null,
   basedn                         varchar(255)                   not null,
   operation                      varchar(255),
   sign_server                    text,
   sign_client                    text,
   primary key (certsn, ctmlname, basedn)
)
type = innodb character set gbk;

#==============================================================*/
# Table: ba_role                                               */
#==============================================================*/
create table if not exists ba_role
(
   role_id                        varchar(50)                    not null,
   rolename                       varchar(50)                    not null,
   privilege_id                   varchar(50)                    not null,
   sign_server                    text,
   sign_client                    text,
   primary key (role_id, privilege_id)
)
type = innodb character set gbk;

#==============================================================*/
# Index: ba_role_i1                                            */
#==============================================================*/
create unique index ba_role_i1 on ba_role
(
   rolename,
   privilege_id
);

#==============================================================*/
# Table: cert                                                  */
#==============================================================*/
create table if not exists cert
(
   certsn                         varchar(40)                    not null,
   subjectuppercase               varchar(255)                   not null,
   subject                        varchar(255)                   not null,
   notbefore                      bigint unsigned                not null,
   notafter                       bigint unsigned                not null,
   validity                       int                            not null,
   authcode                       varchar(60)                    not null,
   cdpid                          int unsigned                   not null,
   ctmlname                       varchar(128)                   not null,
   certstatus                     varchar(12)                    not null,
   isvalid                        bigint unsigned                not null,
   createtime                     bigint unsigned,
   applicantuppercase             varchar(255)                   not null,
   applicant                      varchar(255)                   not null,
   certentity                     blob,
   email                          varchar(100),
   remark                         varchar(255),
   authcode_updatetime            bigint unsigned,
   sign_server                    text,
   sign_client                    text,
   isretainkey                    varchar(40),
   iswaiting                      char(1),
   oldsn                          varchar(40),
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Index: cert_i1                                               */
#==============================================================*/
create index cert_i1 on cert
(
   ctmlname,
   subjectuppercase,
   isvalid
);

#==============================================================*/
# Index: cert_i2                                               */
#==============================================================*/
create index cert_i2 on cert
(
   applicantuppercase
);

#==============================================================*/
# Index: cert_i3                                               */
#==============================================================*/
create index cert_i3 on cert
(
   certstatus
);

#==============================================================*/
# Table: certarc                                               */
#==============================================================*/
create table if not exists certarc
(
   certsn                         varchar(40)                    not null,
   subjectuppercase               varchar(255)                   not null,
   subject                        varchar(255)                   not null,
   notbefore                      bigint unsigned                not null,
   notafter                       bigint unsigned                not null,
   validity                       int                            not null,
   authcode                       varchar(60)                    not null,
   cdpid                          int unsigned                   not null,
   ctmlname                       varchar(128)                   not null,
   certstatus                     varchar(12)                    not null,
   isvalid                        bigint unsigned                not null,
   createtime                     bigint unsigned,
   applicantuppercase             varchar(255)                   not null,
   applicant                      varchar(255)                   not null,
   certentity                     blob,
   email                          varchar(100),
   remark                         varchar(255),
   authcode_updatetime            bigint unsigned,
   sign_server                    text,
   sign_client                    text,
   isretainkey                    varchar(40),
   iswaiting                      char(1),
   oldsn                          varchar(40),
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Index: certarc_i1                                            */
#==============================================================*/
create index certarc_i1 on certarc
(
   ctmlname,
   subjectuppercase,
   isvalid
);

#==============================================================*/
# Index: certarc_i2                                            */
#==============================================================*/
create index certarc_i2 on certarc
(
   applicantuppercase
);

#==============================================================*/
# Index: certarc_i3                                            */
#==============================================================*/
create index certarc_i3 on certarc
(
   certstatus
);

#==============================================================*/
# Table: certarcforkmc                                         */
#==============================================================*/
create table if not exists certarcforkmc
(
   certsn                         varchar(40)                    not null,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Table: certtbp                                               */
#==============================================================*/
create table if not exists certtbp
(
   certsn                         varchar(40)                    not null,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Table: cert_selfext                                          */
#==============================================================*/
create table if not exists cert_selfext
(
   certsn                         varchar(40)                    not null,
   oid                            varchar(128)                   not null,
   selfext_name                   varchar(128)                   not null,
   value                          text                           not null,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn, oid)
)
type = innodb character set gbk;

#==============================================================*/
# Table: cert_selfext_arc                                      */
#==============================================================*/
create table if not exists cert_selfext_arc
(
   certsn                         varchar(40)                    not null,
   oid                            varchar(128)                   not null,
   selfext_name                   varchar(128)                   not null,
   value                          text                           not null,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn, oid)
)
type = innodb character set gbk;

#==============================================================*/
# Table: cert_standard_ext                                     */
#==============================================================*/
create table if not exists cert_standard_ext
(
   certsn                         varchar(40)                    not null,
   ext_oid                        varchar(128)                   not null,
   ext_name                       varchar(128)                   not null,
   child_name                     varchar(128)                   not null,
   othername_oid                  varchar(128),
   value                          text,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn, child_name)
)
type = innodb character set gbk;

#==============================================================*/
# Table: cert_standard_ext_arc                                 */
#==============================================================*/
create table if not exists cert_standard_ext_arc
(
   certsn                         varchar(40)                    not null,
   ext_oid                        varchar(128)                   not null,
   ext_name                       varchar(128)                   not null,
   child_name                     varchar(128)                   not null,
   othername_oid                  varchar(128),
   value                          text,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn, child_name)
)
type = innodb character set gbk;

#==============================================================*/
# Table: config                                                */
#==============================================================*/
create table if not exists config
(
   modulename                     varchar(50)                    not null,
   property                       varchar(255)                   not null,
   value                          text,
   isencrypted                    char(1),
   sign_server                    text,
   sign_client                    text,
   primary key (modulename, property)
)
type = innodb character set gbk;

#==============================================================*/
# Table: crl                                                   */
#==============================================================*/
create table if not exists crl
(
   crl_name                       varchar(255)                   not null,
   crl_entity                     blob                           not null,
   sign_server                    text,
   sign_client                    text,
   primary key (crl_name)
)
type = innodb character set gbk;

#==============================================================*/
# Table: ctml                                                  */
#==============================================================*/
create table if not exists ctml
(
   ctml_name                      varchar(128)                   not null,
   ctml_id                        varchar(128)                   not null,
   ctml_type                      varchar(64)                    not null,
   ctml_status                    varchar(64)                    not null,
   ctml_description               text,
   ctml_policyinfo                text,
   reserve                        text,
   createtime                     bigint                         not null,
   sign_server                    text,
   sign_client                    text,
   primary key (ctml_name)
)
type = innodb character set gbk;

#==============================================================*/
# Index: ctml_i1                                               */
#==============================================================*/
create unique index ctml_i1 on ctml
(
   ctml_id
);

#==============================================================*/
# Table: operationlog                                          */
#==============================================================*/
create table if not exists operationlog
(
   id                             varchar(40)                    not null,
   operatorsn                     varchar(40),
   operatorsubjectuppercase       varchar(255)                   not null,
   operatorsubject                varchar(255)                   not null,
   objectcertsn                   varchar(40),
   objectsubjectuppercase         varchar(255),
   objectsubject                  varchar(255),
   objectctmlname                 varchar(255),
   opttype                        varchar(30)                    not null,
   opttime                        bigint unsigned                not null,
   result                         tinyint                        not null,
   sign_server                    text,
   sign_client                    text,
   primary key (id)
)
type = innodb character set gbk;

#==============================================================*/
# Index: operationlog_i1                                       */
#==============================================================*/
create index operationlog_i1 on operationlog
(
   operatorsubjectuppercase
);

#==============================================================*/
# Index: operationlog_i2                                       */
#==============================================================*/
create index operationlog_i2 on operationlog
(
   objectcertsn
);

#==============================================================*/
# Index: operationlog_i3                                       */
#==============================================================*/
create index operationlog_i3 on operationlog
(
   objectsubjectuppercase
);

#==============================================================*/
# Index: operationlog_i4                                       */
#==============================================================*/
create index operationlog_i4 on operationlog
(
   opttime
);

#==============================================================*/
# Table: operationlogarc                                       */
#==============================================================*/
create table if not exists operationlogarc
(
   id                             varchar(40)                    not null,
   operatorsn                     varchar(40),
   operatorsubjectuppercase       varchar(255)                   not null,
   operatorsubject                varchar(255)                   not null,
   objectcertsn                   varchar(40),
   objectsubjectuppercase         varchar(255),
   objectsubject                  varchar(255),
   objectctmlname                 varchar(255),
   opttype                        varchar(30)                    not null,
   opttime                        bigint unsigned                not null,
   result                         tinyint                        not null,
   sign_server                    text,
   sign_client                    text,
   primary key (id)
)
type = innodb character set gbk;

#==============================================================*/
# Index: operationlogarc_i1                                    */
#==============================================================*/
create index operationlogarc_i1 on operationlogarc
(
   operatorsubjectuppercase
);

#==============================================================*/
# Index: operationlogarc_i2                                    */
#==============================================================*/
create index operationlogarc_i2 on operationlogarc
(
   objectcertsn
);

#==============================================================*/
# Index: operationlogarc_i3                                    */
#==============================================================*/
create index operationlogarc_i3 on operationlogarc
(
   objectsubjectuppercase
);

#==============================================================*/
# Index: operationlogarc_i4                                    */
#==============================================================*/
create index operationlogarc_i4 on operationlogarc
(
   opttime
);

#==============================================================*/
# Table: pendingtask                                           */
#==============================================================*/
create table if not exists pendingtask
(
   taskid                         varchar(40)                    not null,
   certsn                         varchar(40),
   subject                        varchar(255),
   exectime                       decimal(19),
   reasonid                       decimal(6),
   reasondesc                     varchar(255),
   opttype                        varchar(30),
   cdpid                          decimal(19)                    not null,
   applicant                      varchar(255)                   not null,
   sign_server                    text,
   sign_client                    text,
   primary key (taskid)
)
type = innodb character set gbk;

#==============================================================*/
# Table: privilege                                             */
#==============================================================*/
create table if not exists privilege
(
   privilege_id                   varchar(50)                    not null,
   privilege                      varchar(50)                    not null,
   sign_server                    text,
   sign_client                    text,
   primary key (privilege_id)
)
type = innodb character set gbk;

#==============================================================*/
# Index: privilege_i1                                          */
#==============================================================*/
create unique index privilege_i1 on privilege
(
   privilege
);

#==============================================================*/
# Table: ra_basedn                                             */
#==============================================================*/
create table if not exists ra_basedn
(
   certsn                         varchar(40)                    not null,
   ra_basedn                      varchar(255)                   not null,
   sign_server                    text,
   sign_client                    text,
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Table: revokedcert                                           */
#==============================================================*/
create table if not exists revokedcert
(
   certsn                         varchar(40)                    not null,
   cdpid                          bigint unsigned                not null,
   reason                         tinyint unsigned               not null,
   reasondesc                     varchar(255),
   revoketime                     bigint unsigned                not null,
   sign_server                    text,
   sign_client                    text,
   certnotafter                   bigint unsigned,
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Index: revokedcert_i1                                        */
#==============================================================*/
create index revokedcert_i1 on revokedcert
(
   cdpid
);

#==============================================================*/
# Table: revokedcertarc                                        */
#==============================================================*/
create table if not exists revokedcertarc
(
   certsn                         varchar(40)                    not null,
   cdpid                          bigint unsigned                not null,
   reason                         tinyint unsigned               not null,
   reasondesc                     varchar(255),
   revoketime                     bigint unsigned                not null,
   sign_server                    text,
   sign_client                    text,
   certnotafter                   bigint unsigned,
   primary key (certsn)
)
type = innodb character set gbk;

#==============================================================*/
# Index: revokedcertarc_i1                                     */
#==============================================================*/
create index revokedcertarc_i1 on revokedcertarc
(
   cdpid
);

#==============================================================*/
# Table: rootcert                                              */
#==============================================================*/
create table if not exists rootcert
(
   sn                             varchar(40)                    not null,
   ca_id                          bigint unsigned                not null,
   ca_desc                        varchar(255),
   subjectuppercase               varchar(255)                   not null,
   private_key                    text                           not null,
   certstatus                     varchar(12)                    not null,
   certentity                     blob                           not null,
   device_id                      varchar(20)                    not null,
   remark                         varchar(255),
   sign_server                    text,
   sign_client                    text,
   primary key (sn)
)
type = innodb character set gbk;

#==============================================================*/
# Table: selfext                                               */
#==============================================================*/
create table if not exists selfext
(
   selfext_name                   varchar(128)                   not null,
   selfext_oid                    varchar(128)                   not null,
   selfext_status                 varchar(64)                    not null,
   selfext_encodetype             varchar(64)                    not null,
   selfext_description            text,
   reserve                        text,
   createtime                     bigint                         not null,
   sign_server                    text,
   sign_client                    text,
   primary key (selfext_name)
)
type = innodb character set gbk;

#==============================================================*/
# Index: selfext_i1                                            */
#==============================================================*/
create unique index selfext_i1 on selfext
(
   selfext_oid
);

#==============================================================*/
# Table: sysinfo                                               */
#==============================================================*/
create table if not exists sysinfo
(
   sysname                        varchar(80)                    not null,
   version                        varchar(80)                    not null,
   sign_server                    text,
   sign_client                    text,
   primary key (sysname)
)
type = innodb character set gbk;

