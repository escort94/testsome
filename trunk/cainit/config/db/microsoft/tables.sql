#==============================================================*/
# Database name:  MSSQL                                        */
# DBMS name:      Microsoft SQL Server 2000                    */
# Created on:     2006-5-29 10:41:13                           */
#==============================================================*/


alter table admin
   drop constraint fk_admin
go


alter table ba_privilege
   drop constraint fk_ba_ctml
go


alter table ba_privilege
   drop constraint fk_ba_privilege
go


alter table ba_role
   drop constraint fk_role
go


alter table cert
   drop constraint fk_cert
go


alter table cert_selfext
   drop constraint fk_cert_selfext
go


alter table cert_selfext
   drop constraint fk_cert_selfext2
go


alter table certtbp
   drop constraint fk_certtbp
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ba_role')
            and   name  = 'ba_role_i1'
            and   indid > 0
            and   indid < 255)
   drop index ba_role.ba_role_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('cert')
            and   name  = 'cert_i1'
            and   indid > 0
            and   indid < 255)
   drop index cert.cert_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('cert')
            and   name  = 'cert_i2'
            and   indid > 0
            and   indid < 255)
   drop index cert.cert_i2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('certarc')
            and   name  = 'certarc_i1'
            and   indid > 0
            and   indid < 255)
   drop index certarc.certarc_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('certarc')
            and   name  = 'certarc_i2'
            and   indid > 0
            and   indid < 255)
   drop index certarc.certarc_i2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ctml')
            and   name  = 'ctml_i1'
            and   indid > 0
            and   indid < 255)
   drop index ctml.ctml_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlog')
            and   name  = 'operationlog_i1'
            and   indid > 0
            and   indid < 255)
   drop index operationlog.operationlog_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlog')
            and   name  = 'operationlog_i2'
            and   indid > 0
            and   indid < 255)
   drop index operationlog.operationlog_i2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlog')
            and   name  = 'operationlog_i3'
            and   indid > 0
            and   indid < 255)
   drop index operationlog.operationlog_i3
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlog')
            and   name  = 'operationlog_i4'
            and   indid > 0
            and   indid < 255)
   drop index operationlog.operationlog_i4
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlogarc')
            and   name  = 'operationlogarc_i1'
            and   indid > 0
            and   indid < 255)
   drop index operationlogarc.operationlogarc_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlogarc')
            and   name  = 'operationlogarc_i2'
            and   indid > 0
            and   indid < 255)
   drop index operationlogarc.operationlogarc_i2
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlogarc')
            and   name  = 'operationlogarc_i3'
            and   indid > 0
            and   indid < 255)
   drop index operationlogarc.operationlogarc_i3
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('operationlogarc')
            and   name  = 'operationlogarc_i4'
            and   indid > 0
            and   indid < 255)
   drop index operationlogarc.operationlogarc_i4
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('privilege')
            and   name  = 'privilege_i1'
            and   indid > 0
            and   indid < 255)
   drop index privilege.privilege_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('revokedcert')
            and   name  = 'revokedcert_i1'
            and   indid > 0
            and   indid < 255)
   drop index revokedcert.revokedcert_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('revokedcertarc')
            and   name  = 'revokedcertarc_i1'
            and   indid > 0
            and   indid < 255)
   drop index revokedcertarc.revokedcertarc_i1
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('selfext')
            and   name  = 'selfext_i1'
            and   indid > 0
            and   indid < 255)
   drop index selfext.selfext_i1
go


if exists (select 1
            from  sysobjects
           where  id = object_id('certarcforkmc')
            and   type = 'U')
   drop table certarcforkmc
go


if exists (select 1
            from  sysobjects
           where  id = object_id('cert_standard_ext')
            and   type = 'U')
   drop table cert_standard_ext
go


if exists (select 1
            from  sysobjects
           where  id = object_id('cert_standard_ext_arc')
            and   type = 'U')
   drop table cert_standard_ext_arc
go


if exists (select 1
            from  sysobjects
           where  id = object_id('pendingtask')
            and   type = 'U')
   drop table pendingtask
go


if exists (select 1
            from  sysobjects
           where  id = object_id('sysinfo')
            and   type = 'U')
   drop table sysinfo
go


if exists (select 1
            from  sysobjects
           where  id = object_id('admin')
            and   type = 'U')
   drop table admin
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ba_privilege')
            and   type = 'U')
   drop table ba_privilege
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ba_role')
            and   type = 'U')
   drop table ba_role
go


if exists (select 1
            from  sysobjects
           where  id = object_id('cert')
            and   type = 'U')
   drop table cert
go


if exists (select 1
            from  sysobjects
           where  id = object_id('cert_selfext')
            and   type = 'U')
   drop table cert_selfext
go


if exists (select 1
            from  sysobjects
           where  id = object_id('cert_selfext_arc')
            and   type = 'U')
   drop table cert_selfext_arc
go


if exists (select 1
            from  sysobjects
           where  id = object_id('certarc')
            and   type = 'U')
   drop table certarc
go


if exists (select 1
            from  sysobjects
           where  id = object_id('certtbp')
            and   type = 'U')
   drop table certtbp
go


if exists (select 1
            from  sysobjects
           where  id = object_id('config')
            and   type = 'U')
   drop table config
go


if exists (select 1
            from  sysobjects
           where  id = object_id('crl')
            and   type = 'U')
   drop table crl
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ctml')
            and   type = 'U')
   drop table ctml
go


if exists (select 1
            from  sysobjects
           where  id = object_id('operationlog')
            and   type = 'U')
   drop table operationlog
go


if exists (select 1
            from  sysobjects
           where  id = object_id('operationlogarc')
            and   type = 'U')
   drop table operationlogarc
go


if exists (select 1
            from  sysobjects
           where  id = object_id('privilege')
            and   type = 'U')
   drop table privilege
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ra_basedn')
            and   type = 'U')
   drop table ra_basedn
go


if exists (select 1
            from  sysobjects
           where  id = object_id('revokedcert')
            and   type = 'U')
   drop table revokedcert
go


if exists (select 1
            from  sysobjects
           where  id = object_id('revokedcertarc')
            and   type = 'U')
   drop table revokedcertarc
go


if exists (select 1
            from  sysobjects
           where  id = object_id('rootcert')
            and   type = 'U')
   drop table rootcert
go


if exists (select 1
            from  sysobjects
           where  id = object_id('selfext')
            and   type = 'U')
   drop table selfext
go


#==============================================================*/
# Table: certarcforkmc                                         */
#==============================================================*/
create table certarcforkmc (
   certsn               varchar(40)          not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_certarcforkmc primary key  (certsn)
)
go


#==============================================================*/
# Table: cert_standard_ext                                     */
#==============================================================*/
create table cert_standard_ext (
   certsn               varchar(40)          not null,
   ext_oid              varchar(128)         not null,
   ext_name             varchar(128)         not null,
   child_name           varchar(128)         not null,
   othername_oid        varchar(128)         null,
   value                varchar(4000)        null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_cert_standard_ext primary key  (certsn, child_name)
)
go


#==============================================================*/
# Table: cert_standard_ext_arc                                 */
#==============================================================*/
create table cert_standard_ext_arc (
   certsn               varchar(40)          not null,
   ext_oid              varchar(128)         not null,
   ext_name             varchar(128)         not null,
   child_name           varchar(128)         not null,
   othername_oid        varchar(128)         null,
   value                varchar(4000)        null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_cert_standard_ext_arc primary key  (certsn, child_name)
)
go


#==============================================================*/
# Table: pendingtask                                           */
#==============================================================*/
create table pendingtask (
   taskid               varchar(40)          not null,
   certsn               varchar(40)          null,
   subject              varchar(255)         null,
   exectime             decimal(19)          null,
   reasonid             decimal(6)           null,
   reasondesc           varchar(255)         null,
   opttype              varchar(30)          null,
   cdpid                decimal(19)          not null,
   applicant            varchar(255)         not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_pendingtask primary key  (taskid)
)
go


#==============================================================*/
# Table: sysinfo                                               */
#==============================================================*/
create table sysinfo (
   sysname              varchar(80)          not null,
   version              varchar(80)          not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_sysinfo primary key  (sysname)
)
go


#==============================================================*/
# Table: admin                                                 */
#==============================================================*/
create table admin (
   certsn               varchar(40)          not null,
   role_id              varchar(50)          not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_admin primary key clustered (certsn, role_id)
)
go


#==============================================================*/
# Table: ba_privilege                                          */
#==============================================================*/
create table ba_privilege (
   certsn               varchar(40)          not null,
   ctmlname             varchar(128)         not null,
   basedn               varchar(255)         not null,
   operation            varchar(255)         null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_ba_privilege primary key clustered (certsn, ctmlname, basedn)
)
go


#==============================================================*/
# Table: ba_role                                               */
#==============================================================*/
create table ba_role (
   role_id              varchar(50)          not null,
   rolename             varchar(50)          not null,
   privilege_id         varchar(50)          not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_role primary key clustered (role_id, privilege_id)
)
go


#==============================================================*/
# Index: ba_role_i1                                            */
#==============================================================*/
create unique  index ba_role_i1 on ba_role (
rolename,
privilege_id
)
go


#==============================================================*/
# Table: cert                                                  */
#==============================================================*/
create table cert (
   certsn               varchar(40)          not null,
   subjectuppercase     varchar(255)         not null,
   subject              varchar(255)         not null,
   notbefore            bigint               not null,
   notafter             bigint               not null,
   validity             int                  not null,
   authcode             varchar(60)          not null,
   cdpid                int                  not null,
   ctmlname             varchar(128)         not null,
   certstatus           varchar(12)          not null,
   isvalid              bigint               not null,
   createtime           bigint               null,
   applicantuppercase   varchar(255)         not null,
   applicant            varchar(255)         not null,
   certentity           varbinary(8000)      null,
   email                varchar(100)         null,
   remark               varchar(255)         null,
   authcode_updatetime  bigint               null,
   sign_server          text                 null,
   sign_client          text                 null,
   isretainkey          varchar(40)          null,
   iswaiting            char(1)              null,
   oldsn                varchar(40)          null,
   constraint pk_cert primary key clustered (certsn)
)
go


#==============================================================*/
# Index: cert_i1                                               */
#==============================================================*/
create   index cert_i1 on cert (
ctmlname,
subjectuppercase,
isvalid
)
go


#==============================================================*/
# Index: cert_i2                                               */
#==============================================================*/
create   index cert_i2 on cert (
applicantuppercase
)
go


#==============================================================*/
# Table: cert_selfext                                          */
#==============================================================*/
create table cert_selfext (
   certsn               varchar(40)          not null,
   oid                  varchar(128)         not null,
   selfext_name         varchar(128)         not null,
   value                varchar(4000)        not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_cert_selfext primary key clustered (certsn, oid)
)
go


#==============================================================*/
# Table: cert_selfext_arc                                      */
#==============================================================*/
create table cert_selfext_arc (
   certsn               varchar(40)          not null,
   oid                  varchar(128)         not null,
   selfext_name         varchar(128)         not null,
   value                varchar(4000)        not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_cert_selfext_arc primary key  (certsn, oid)
)
go


#==============================================================*/
# Table: certarc                                               */
#==============================================================*/
create table certarc (
   certsn               varchar(40)          not null,
   subjectuppercase     varchar(255)         not null,
   subject              varchar(255)         not null,
   notbefore            bigint               not null,
   notafter             bigint               not null,
   validity             int                  not null,
   authcode             varchar(60)          not null,
   cdpid                int                  not null,
   ctmlname             varchar(128)         not null,
   certstatus           varchar(12)          not null,
   isvalid              bigint               not null,
   createtime           bigint               null,
   applicantuppercase   varchar(255)         not null,
   applicant            varchar(255)         not null,
   certentity           varbinary(8000)      null,
   email                varchar(100)         null,
   remark               varchar(255)         null,
   authcode_updatetime  bigint               null,
   sign_server          text                 null,
   sign_client          text                 null,
   isretainkey          varchar(40)          null,
   iswaiting            char(1)              null,
   oldsn                varchar(40)          null,
   constraint pk_certarc primary key  (certsn)
)
go


#==============================================================*/
# Index: certarc_i1                                            */
#==============================================================*/
create   index certarc_i1 on certarc (
ctmlname,
subjectuppercase,
isvalid
)
go


#==============================================================*/
# Index: certarc_i2                                            */
#==============================================================*/
create   index certarc_i2 on certarc (
applicantuppercase
)
go


#==============================================================*/
# Table: certtbp                                               */
#==============================================================*/
create table certtbp (
   certsn               varchar(40)          not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_certtbp primary key clustered (certsn)
)
go


#==============================================================*/
# Table: config                                                */
#==============================================================*/
create table config (
   modulename           varchar(50)          not null,
   property             varchar(255)         not null,
   value                varchar(4000)        null,
   isencrypted          char(1)              null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_config primary key clustered (modulename, property)
)
go


#==============================================================*/
# Table: crl                                                   */
#==============================================================*/
create table crl (
   crl_name             varchar(255)         not null,
   crl_entity           varbinary(8000)      not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_crl primary key  (crl_name)
)
go


#==============================================================*/
# Table: ctml                                                  */
#==============================================================*/
create table ctml (
   ctml_name            varchar(128)         not null,
   ctml_id              varchar(128)         not null,
   ctml_type            varchar(64)          not null,
   ctml_status          varchar(64)          not null,
   ctml_description     varchar(1024)        null,
   ctml_policyinfo      text                 null,
   reserve              varchar(4000)        null,
   createtime           bigint               not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_ctml primary key clustered (ctml_name)
)
go


#==============================================================*/
# Index: ctml_i1                                               */
#==============================================================*/
create unique  index ctml_i1 on ctml (
ctml_id
)
go


#==============================================================*/
# Table: operationlog                                          */
#==============================================================*/
create table operationlog (
   id                   varchar(40)          not null,
   operatorsn           varchar(40)          null,
   operatorsubjectuppercase varchar(255)         not null,
   operatorsubject      varchar(255)         not null,
   objectcertsn         varchar(40)          null,
   objectsubjectuppercase varchar(255)         null,
   objectsubject        varchar(255)         null,
   objectctmlname       varchar(255)         null,
   opttype              varchar(30)          not null,
   opttime              bigint               not null,
   result               tinyint              not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_operationlog primary key  (id)
)
go


#==============================================================*/
# Index: operationlog_i1                                       */
#==============================================================*/
create   index operationlog_i1 on operationlog (
operatorsubjectuppercase
)
go


#==============================================================*/
# Index: operationlog_i2                                       */
#==============================================================*/
create   index operationlog_i2 on operationlog (
objectcertsn
)
go


#==============================================================*/
# Index: operationlog_i3                                       */
#==============================================================*/
create   index operationlog_i3 on operationlog (
objectsubjectuppercase
)
go


#==============================================================*/
# Index: operationlog_i4                                       */
#==============================================================*/
create   index operationlog_i4 on operationlog (
opttime
)
go


#==============================================================*/
# Table: operationlogarc                                       */
#==============================================================*/
create table operationlogarc (
   id                   varchar(40)          not null,
   operatorsn           varchar(40)          null,
   operatorsubjectuppercase varchar(255)         not null,
   operatorsubject      varchar(255)         not null,
   objectcertsn         varchar(40)          null,
   objectsubjectuppercase varchar(255)         null,
   objectsubject        varchar(255)         null,
   objectctmlname       varchar(255)         null,
   opttype              varchar(30)          not null,
   opttime              bigint               not null,
   result               tinyint              not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_operationlogarc primary key  (id)
)
go


#==============================================================*/
# Index: operationlogarc_i1                                    */
#==============================================================*/
create   index operationlogarc_i1 on operationlogarc (
operatorsubjectuppercase
)
go


#==============================================================*/
# Index: operationlogarc_i2                                    */
#==============================================================*/
create   index operationlogarc_i2 on operationlogarc (
objectcertsn
)
go


#==============================================================*/
# Index: operationlogarc_i3                                    */
#==============================================================*/
create   index operationlogarc_i3 on operationlogarc (
objectsubjectuppercase
)
go


#==============================================================*/
# Index: operationlogarc_i4                                    */
#==============================================================*/
create   index operationlogarc_i4 on operationlogarc (
opttime
)
go


#==============================================================*/
# Table: privilege                                             */
#==============================================================*/
create table privilege (
   privilege_id         varchar(50)          not null,
   privilege            varchar(50)          not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_privilege primary key clustered (privilege_id)
)
go


#==============================================================*/
# Index: privilege_i1                                          */
#==============================================================*/
create unique  index privilege_i1 on privilege (
privilege
)
go


#==============================================================*/
# Table: ra_basedn                                             */
#==============================================================*/
create table ra_basedn (
   certsn               varchar(40)          not null,
   ra_basedn            varchar(255)         not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_ra_basedn primary key  (certsn)
)
go


#==============================================================*/
# Table: revokedcert                                           */
#==============================================================*/
create table revokedcert (
   certsn               varchar(40)          not null,
   cdpid                bigint               not null,
   reason               tinyint              not null,
   reasondesc           varchar(255)         null,
   revoketime           bigint               not null,
   sign_server          text                 null,
   sign_client          text                 null,
   certnotafter         bigint               null,
   constraint pk_revokedcert primary key clustered (certsn)
)
go


#==============================================================*/
# Index: revokedcert_i1                                        */
#==============================================================*/
create   index revokedcert_i1 on revokedcert (
cdpid
)
go


#==============================================================*/
# Table: revokedcertarc                                        */
#==============================================================*/
create table revokedcertarc (
   certsn               varchar(40)          not null,
   cdpid                bigint               not null,
   reason               tinyint              not null,
   reasondesc           varchar(255)         null,
   revoketime           bigint               not null,
   sign_server          text                 null,
   sign_client          text                 null,
   certnotafter         bigint               null,
   constraint pk_revokedcertarc primary key  (certsn)
)
go


#==============================================================*/
# Index: revokedcertarc_i1                                     */
#==============================================================*/
create   index revokedcertarc_i1 on revokedcertarc (
cdpid
)
go


#==============================================================*/
# Table: rootcert                                              */
#==============================================================*/
create table rootcert (
   sn                   varchar(40)          not null,
   ca_id                bigint               not null,
   ca_desc              varchar(255)         null,
   subjectuppercase     varchar(255)         not null,
   private_key          varchar(4000)        not null,
   certstatus           varchar(12)          not null,
   certentity           varbinary(8000)      not null,
   device_id            varchar(20)          not null,
   remark               varchar(255)         null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_rootcert primary key  (sn)
)
go


#==============================================================*/
# Table: selfext                                               */
#==============================================================*/
create table selfext (
   selfext_name         varchar(128)         not null,
   selfext_oid          varchar(128)         not null,
   selfext_status       varchar(64)          not null,
   selfext_encodetype   varchar(64)          not null,
   selfext_description  varchar(1024)        null,
   reserve              varchar(4000)        null,
   createtime           bigint               not null,
   sign_server          text                 null,
   sign_client          text                 null,
   constraint pk_selfext primary key clustered (selfext_name)
)
go


#==============================================================*/
# Index: selfext_i1                                            */
#==============================================================*/
create unique  index selfext_i1 on selfext (
selfext_oid
)
go


alter table admin
   add constraint fk_admin foreign key (certsn)
      references cert (certsn)
go


alter table ba_privilege
   add constraint fk_ba_ctml foreign key (ctmlname)
      references ctml (ctml_name)
go


alter table ba_privilege
   add constraint fk_ba_privilege foreign key (certsn)
      references cert (certsn)
go


alter table ba_role
   add constraint fk_role foreign key (privilege_id)
      references privilege (privilege_id)
go


alter table cert
   add constraint fk_cert foreign key (ctmlname)
      references ctml (ctml_name)
go


alter table cert_selfext
   add constraint fk_cert_selfext foreign key (certsn)
      references cert (certsn)
go


alter table cert_selfext
   add constraint fk_cert_selfext2 foreign key (selfext_name)
      references selfext (selfext_name)
go


alter table certtbp
   add constraint fk_certtbp foreign key (certsn)
      references cert (certsn)
go


