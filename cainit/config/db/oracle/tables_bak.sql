#==============================================================*/
# Database name:  Oracle                                       */
# DBMS name:      ORACLE Version 9i                            */
# Created on:     2006-3-27 15:49:39                           */
#==============================================================*/


drop table admin cascade constraints
/


drop table ba_privilege cascade constraints
/


drop table ba_role cascade constraints
/


drop table cert cascade constraints
/


drop table certarc cascade constraints
/


drop table certarcforkmc cascade constraints
/


drop table certtbp cascade constraints
/


drop table cert_selfext cascade constraints
/


drop table cert_selfext_arc cascade constraints
/


drop table cert_standard_ext cascade constraints
/


drop table cert_standard_ext_arc cascade constraints
/


drop table config cascade constraints
/


drop table crl cascade constraints
/


drop table ctml cascade constraints
/


drop table operationlog cascade constraints
/


drop table operationlogarc cascade constraints
/


drop table pendingtask cascade constraints
/


drop table privilege cascade constraints
/


drop table ra_basedn cascade constraints
/


drop table revokedcert cascade constraints
/


drop table revokedcertarc cascade constraints
/


drop table rootcert cascade constraints
/


drop table selfext cascade constraints
/


drop table sysinfo cascade constraints
/


#==============================================================*/
# Table: admin                                                 */
#==============================================================*/


create table admin  (
   certsn               varchar2(40)                     not null,
   role_id              varchar2(50)                     not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_admin primary key (certsn, role_id)
)
/


#==============================================================*/
# Table: ba_privilege                                          */
#==============================================================*/


create table ba_privilege  (
   certsn               varchar2(40)                     not null,
   ctmlname             varchar2(128)                    not null,
   basedn               varchar2(255)                    not null,
   operation            varchar2(255),
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_ba_privilege primary key (certsn, ctmlname, basedn)
)
/


#==============================================================*/
# Table: ba_role                                               */
#==============================================================*/


create table ba_role  (
   role_id              varchar2(50)                     not null,
   rolename             varchar2(50)                     not null,
   privilege_id         varchar2(50)                     not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_ba_role primary key (role_id, privilege_id)
)
/


#==============================================================*/
# Index: ba_role_i1                                            */
#==============================================================*/
create unique index ba_role_i1 on ba_role (
   rolename asc,
   privilege_id asc
)
/


#==============================================================*/
# Table: cert                                                  */
#==============================================================*/


create table cert  (
   certsn               varchar2(40)                     not null,
   subjectuppercase     varchar2(255)                    not null,
   subject              varchar2(255)                    not null,
   notbefore            number(19)                       not null,
   notafter             number(19)                       not null,
   validity             number(6)                        not null,
   authcode             varchar2(60)                     not null,
   cdpid                number(19)                       not null,
   ctmlname             varchar2(128)                    not null,
   certstatus           varchar2(12)                     not null,
   isvalid              number(19)                       not null,
   createtime           number(19),
   applicantuppercase   varchar2(255)                    not null,
   applicant            varchar2(255)                    not null,
   certentity           blob,
   email                varchar2(100),
   remark               varchar2(255),
   authcode_updatetime  number(19),
   sign_server          varchar2(4000),
   sign_client          varchar2(4000),
   isretainkey          varchar2(40),
   iswaiting            char(1),
   oldsn                varchar(40),
   constraint pk_cert primary key (certsn)
)
/


#==============================================================*/
# Index: cert_i1                                               */
#==============================================================*/
create index cert_i1 on cert (
   ctmlname asc,
   subjectuppercase asc,
   isvalid asc
)
/


#==============================================================*/
# Index: cert_i2                                               */
#==============================================================*/
create index cert_i2 on cert (
   applicantuppercase asc
)
/


#==============================================================*/
# Table: certarc                                               */
#==============================================================*/


create table certarc  (
   certsn               varchar2(40)                     not null,
   subjectuppercase     varchar2(255)                    not null,
   subject              varchar2(255)                    not null,
   notbefore            number(19)                       not null,
   notafter             number(19)                       not null,
   validity             number(6)                        not null,
   authcode             varchar2(60)                     not null,
   cdpid                number(19)                       not null,
   ctmlname             varchar2(128)                    not null,
   certstatus           varchar2(12)                     not null,
   isvalid              number(19)                       not null,
   createtime           number(19),
   applicantuppercase   varchar2(255)                    not null,
   applicant            varchar2(255)                    not null,
   certentity           blob,
   email                varchar2(100),
   remark               varchar2(255),
   authcode_updatetime  number(19),
   sign_server          varchar2(4000),
   sign_client          varchar2(4000),
   isretainkey          varchar2(40),
   iswaiting            char(1),
   oldsn                varchar(40),
   constraint pk_certarc primary key (certsn)
)
/


#==============================================================*/
# Index: certarc_i1                                            */
#==============================================================*/
create index certarc_i1 on certarc (
   ctmlname asc,
   subjectuppercase asc,
   isvalid asc
)
/


#==============================================================*/
# Index: certarc_i2                                            */
#==============================================================*/
create index certarc_i2 on certarc (
   applicantuppercase asc
)
/


#==============================================================*/
# Table: certarcforkmc                                         */
#==============================================================*/


create table certarcforkmc  (
   certsn               varchar2(40)                     not null,
   sign_server          varchar2(4000),
   sign_client          varchar2(4000),
   constraint pk_certarcforkmc primary key (certsn)
)
/


#==============================================================*/
# Table: certtbp                                               */
#==============================================================*/


create table certtbp  (
   certsn               varchar2(40)                     not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_certtbp primary key (certsn)
)
/


#==============================================================*/
# Table: cert_selfext                                          */
#==============================================================*/


create table cert_selfext  (
   certsn               varchar2(40)                     not null,
   oid                  varchar2(128)                    not null,
   selfext_name         varchar2(128)                    not null,
   value                varchar2(4000)                   not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_cert_selfext primary key (certsn, oid)
)
/


#==============================================================*/
# Table: cert_selfext_arc                                      */
#==============================================================*/


create table cert_selfext_arc  (
   certsn               varchar2(40)                     not null,
   oid                  varchar2(128)                    not null,
   selfext_name         varchar2(128)                    not null,
   value                varchar2(4000)                   not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_cert_selfext_arc primary key (certsn, oid)
)
/


#==============================================================*/
# Table: cert_standard_ext                                     */
#==============================================================*/


create table cert_standard_ext  (
   certsn               varchar2(40)                     not null,
   ext_oid              varchar2(128)                    not null,
   ext_name             varchar2(128)                    not null,
   child_name           varchar2(128)                    not null,
   othername_oid        varchar2(128),
   value                varchar2(4000),
   sign_server          varchar2(4000),
   sign_client          varchar2(4000),
   constraint pk_cert_standard_ext primary key (certsn, child_name)
)
/


#==============================================================*/
# Table: cert_standard_ext_arc                                 */
#==============================================================*/


create table cert_standard_ext_arc  (
   certsn               varchar2(40)                     not null,
   ext_oid              varchar2(128)                    not null,
   ext_name             varchar2(128)                    not null,
   child_name           varchar2(128)                    not null,
   othername_oid        varchar2(128),
   value                varchar2(4000),
   sign_server          varchar2(4000),
   sign_client          varchar2(4000),
   constraint pk_cert_standard_ext_arc primary key (certsn, child_name)
)
/


#==============================================================*/
# Table: config                                                */
#==============================================================*/


create table config  (
   modulename           varchar2(50)                     not null,
   property             varchar2(255)                    not null,
   value                varchar2(4000),
   isencrypted          char(1),
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_config primary key (modulename, property)
)
/


#==============================================================*/
# Table: crl                                                   */
#==============================================================*/


create table crl  (
   crl_name             varchar2(255)                    not null,
   crl_entity           blob                             not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_crl primary key (crl_name)
)
/


#==============================================================*/
# Table: ctml                                                  */
#==============================================================*/


create table ctml  (
   ctml_name            varchar2(128)                    not null,
   ctml_id              varchar2(128)                    not null,
   ctml_type            varchar2(64)                     not null,
   ctml_status          varchar2(64)                     not null,
   ctml_description     varchar2(1024),
   ctml_policyinfo      clob,
   reserve              varchar2(4000),
   createtime           number(19)                       not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_ctml primary key (ctml_name)
)
/


#==============================================================*/
# Index: ctml_i1                                               */
#==============================================================*/
create index ctml_i1 on ctml (
   ctml_id asc
)
/


#==============================================================*/
# Table: operationlog                                          */
#==============================================================*/


create table operationlog  (
   id                   varchar2(40)                     not null,
   operatorsn           varchar2(40),
   operatorsubjectuppercase varchar2(255)                    not null,
   operatorsubject      varchar2(255)                    not null,
   objectcertsn         varchar2(40),
   objectsubjectuppercase varchar2(255),
   objectsubject        varchar2(255),
   objectctmlname       varchar2(255),
   opttype              varchar2(30)                     not null,
   opttime              number(19)                       not null,
   result               number(1)                        not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_operationlog primary key (id)
)
/


#==============================================================*/
# Index: operationlog_i1                                       */
#==============================================================*/
create index operationlog_i1 on operationlog (
   operatorsubjectuppercase asc
)
/


#==============================================================*/
# Index: operationlog_i2                                       */
#==============================================================*/
create index operationlog_i2 on operationlog (
   objectcertsn asc
)
/


#==============================================================*/
# Index: operationlog_i3                                       */
#==============================================================*/
create index operationlog_i3 on operationlog (
   objectsubjectuppercase asc
)
/


#==============================================================*/
# Index: operationlog_i4                                       */
#==============================================================*/
create index operationlog_i4 on operationlog (
   opttime asc
)
/


#==============================================================*/
# Table: operationlogarc                                       */
#==============================================================*/


create table operationlogarc  (
   id                   varchar2(40)                     not null,
   operatorsn           varchar2(40),
   operatorsubjectuppercase varchar2(255)                    not null,
   operatorsubject      varchar2(255)                    not null,
   objectcertsn         varchar2(40),
   objectsubjectuppercase varchar2(255),
   objectsubject        varchar2(255),
   objectctmlname       varchar2(255),
   opttype              varchar2(30)                     not null,
   opttime              number(19)                       not null,
   result               number(1)                        not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_operationlogarc primary key (id)
)
/


#==============================================================*/
# Index: operationlogarc_i1                                    */
#==============================================================*/
create index operationlogarc_i1 on operationlogarc (
   operatorsubjectuppercase asc
)
/


#==============================================================*/
# Index: operationlogarc_i2                                    */
#==============================================================*/
create index operationlogarc_i2 on operationlogarc (
   objectcertsn asc
)
/


#==============================================================*/
# Index: operationlogarc_i3                                    */
#==============================================================*/
create index operationlogarc_i3 on operationlogarc (
   objectsubjectuppercase asc
)
/


#==============================================================*/
# Index: operationlogarc_i4                                    */
#==============================================================*/
create index operationlogarc_i4 on operationlogarc (
   opttime asc
)
/


#==============================================================*/
# Table: pendingtask                                           */
#==============================================================*/


create table pendingtask  (
   taskid               varchar2(40)                     not null,
   certsn               varchar2(40),
   subject              varchar2(255),
   exectime             decimal(19),
   reasonid             decimal(6),
   reasondesc           varchar2(255),
   opttype              varchar2(30),
   cdpid                decimal(19)                      not null,
   applicant            varchar2(255)                    not null,
   sign_server          varchar2(4000),
   sign_client          varchar2(4000),
   constraint pk_pendingtask primary key (taskid)
)
/


#==============================================================*/
# Table: privilege                                             */
#==============================================================*/


create table privilege  (
   privilege_id         varchar2(50)                     not null,
   privilege            varchar2(50)                     not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_privilege primary key (privilege_id)
)
/


#==============================================================*/
# Index: privilege_i1                                          */
#==============================================================*/
create unique index privilege_i1 on privilege (
   privilege asc
)
/


#==============================================================*/
# Table: ra_basedn                                             */
#==============================================================*/


create table ra_basedn  (
   certsn               varchar2(40)                     not null,
   ra_basedn            varchar2(255)                    not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_ra_basedn primary key (certsn)
)
/


#==============================================================*/
# Table: revokedcert                                           */
#==============================================================*/


create table revokedcert  (
   certsn               varchar2(40)                     not null,
   cdpid                number(19)                       not null,
   reason               number(2)                        not null,
   reasondesc           varchar2(255),
   revoketime           number(19)                       not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   certnotafter         number(19),
   constraint pk_revokedcert primary key (certsn)
)
/


#==============================================================*/
# Index: revokedcert_i1                                        */
#==============================================================*/
create index revokedcert_i1 on revokedcert (
   cdpid asc
)
/


#==============================================================*/
# Table: revokedcertarc                                        */
#==============================================================*/


create table revokedcertarc  (
   certsn               varchar2(40)                     not null,
   cdpid                number(19)                       not null,
   reason               number(2)                        not null,
   reasondesc           varchar2(255),
   revoketime           number(19)                       not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   certnotafter         number(19),
   constraint pk_revokedcertarc primary key (certsn)
)
/


#==============================================================*/
# Index: revokedcertarc_i1                                     */
#==============================================================*/
create index revokedcertarc_i1 on revokedcertarc (
   cdpid asc
)
/


#==============================================================*/
# Table: rootcert                                              */
#==============================================================*/


create table rootcert  (
   sn                   varchar2(40)                     not null,
   ca_id                number(19)                       not null,
   ca_desc              varchar2(255),
   subjectuppercase     varchar2(255)                    not null,
   private_key          varchar2(4000)                   not null,
   certstatus           varchar2(12)                     not null,
   certentity           blob                             not null,
   device_id            varchar2(20)                     not null,
   remark               varchar2(255),
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_rootcert primary key (sn)
)
/


#==============================================================*/
# Table: selfext                                               */
#==============================================================*/


create table selfext  (
   selfext_name         varchar2(128)                    not null,
   selfext_oid          varchar2(128)                    not null,
   selfext_status       varchar2(64)                     not null,
   selfext_encodetype   varchar2(64)                     not null,
   selfext_description  varchar2(1024),
   reserve              varchar2(4000),
   createtime           number(19)                       not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_selfext primary key (selfext_name)
)
/


#==============================================================*/
# Index: selfext_i1                                            */
#==============================================================*/
create unique index selfext_i1 on selfext (
   selfext_oid asc
)
/


#==============================================================*/
# Table: sysinfo                                               */
#==============================================================*/


create table sysinfo  (
   sysname              varchar(80)                      not null,
   version              varchar(80)                      not null,
   sign_server          varchar(4000),
   sign_client          varchar(4000),
   constraint pk_sysinfo primary key (sysname)
)
/


alter table admin
   add constraint fk_admin foreign key (certsn)
      references cert (certsn)
/


alter table ba_privilege
   add constraint fk_ba_privi_reference_ctml foreign key (ctmlname)
      references ctml (ctml_name)
/


alter table ba_privilege
   add constraint fk_ba_privilege foreign key (certsn)
      references cert (certsn)
/


alter table ba_role
   add constraint fk_role foreign key (privilege_id)
      references privilege (privilege_id)
/


alter table cert
   add constraint fk_cert foreign key (ctmlname)
      references ctml (ctml_name)
/


alter table certtbp
   add constraint fk_certtbp foreign key (certsn)
      references cert (certsn)
/


alter table cert_selfext
   add constraint fk_cert_selfext foreign key (certsn)
      references cert (certsn)
/


alter table cert_selfext
   add constraint fk_cert_selfext2 foreign key (selfext_name)
      references selfext (selfext_name)
/


