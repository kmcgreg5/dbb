//MTLUSRPC JOB (999,POK),'CCOMP',NOTIFY=&SYSUID, 
//   CLASS=A,MSGCLASS=H 
//*---------------------------------------------------------------- 
//* CICS Metal JCL 
//* https://www.ibm.com/docs/en/zos/2.4.0?topic=examples-jcl-example
//******************************************************************* 
//* Compile the code 
//******************************************************************* 
//CCAM     PROC IDSN=,ADSN=,ODSN=,MEM= 
//CC       EXEC PGM=CCNDRVR,REGION=0M, 
// PARM=('OPTFILE(DD:OPTIONS)') 
//STEPLIB  DD DISP=SHR,DSN=MTLCICS.METALC.SCCNCMP 
//         DD DSN=CICSTS41.CICS.SDFHLOAD,DISP=SHR 
//         DD DISP=SHR,DSN=CEE.SCEERUN 
//         DD DISP=SHR,DSN=CEE.SCEERUN2 
//OPTIONS  DD DISP=SHR,DSN=MTLCICS.METALC.SAMPJCL(OPTXPI) 
//SYSLIB   DD PATH='/usr/include/metal',PATHOPTS=ORDONLY 
//         DD DSN=CICSTS41.CICS.SDFHC370,DISP=SHR 
//         DD DSN=CICSTS41.CICS.SDFHMAC,DISP=SHR 
//         DD DSN=MTLCICS.METALC.SAMPMAC,DISP=SHR 
//**************************************************** 
//SYSUT1   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3200) 
//SYSUT4   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3200) 
//SYSUT5   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=3200,BLKSIZE=12800) 
//SYSUT6   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=3200,BLKSIZE=12800) 
//SYSUT7   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=3200,BLKSIZE=12800) 
//SYSUT8   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=3200,BLKSIZE=12800) 
//SYSUT9   DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=VB,LRECL=137,BLKSIZE=882) 
//SYSUT10  DD  SYSOUT=* 
//SYSUT14  DD  UNIT=SYSDA,SPACE=(32000,(30,30)), 
//             DCB=(RECFM=FB,LRECL=3200,BLKSIZE=12800) 
//SYSUT15  DD  SYSOUT=* 
//**************************************************** 
//SYSPRINT DD SYSOUT=* 
//SYSOUT   DD SYSOUT=* 
//SYSCPRT  DD SYSOUT=* 
//*SYSLIN   DD DSN=&&SYSLIN,DISP=(NEW,PASS),SPACE=(TRK,(10,100)), 
//*         UNIT=SYSDA,BLKSIZE=3200,LRECL=80,RECFM=FB,DSORG=PS 
//SYSLIN   DD DISP=SHR,DSN=&ADSN(&MEM) 
//SYSIN    DD DISP=SHR,DSN=&IDSN(&MEM)
//MYDD     DD  *
TYP=CPY/A.P1.CM.ZZ.BRCH.ST.CPYLIB
TYP=CP1/A.P1.CM.ZZ.BRCH.ST.CPYLIB
TYP=LGC/A.P1.CM.ZZ.BRCH.ST.CPYLIB
TYP=CPY/A.P1.CM.ZZ.BRCH.CPYLIB
TYP=LGC/A.P1.CM.ZZ.LAG.CPYLIB
TYP=CP1/A.P1.CM.ZZ.COMM.CPYLIB
TYP=CPX/A.P1.CM.ZZ.BRCH.BL.CPXLIB
TYP=MPC/A.P1.CM.ZZ.BRCH.CPYLIB
TYP=TPC/A.P1.CM.ZZ.BRCH.BL.TPCLIB
CMP=OF2730ZZ.SRC
LNG=EZTP
PKG=BRCH005217
//*)IM CMN$$CND
//*)IM AGF$$EZT
//******************************************************************* 
//* Assemble the code 
//******************************************************************* 
//ASM     EXEC PGM=ASMA90,REGION=0M,PARM='GOFF' 
//SYSLIB   DD  DSN=SYS1.MACLIB,DISP=SHR 
//         DD DISP=SHR,DSN=CICSTS41.CICS.SDFHMAC 
//         DD DISP=SHR,DSN=CEE.SCEEMAC 
//         DD DSN=MTLCICS.METALC.SAMPMAC,DISP=SHR 
//SYSUT1   DD  UNIT=(SYSDA,SEP=SYSLIB),SPACE=(CYL,(10,5)),DSN=&SYSUT1
//SYSPRINT DD  SYSOUT=* 
//SYSLIN   DD  DISP=SHR,DSN=&ODSN(&MEM) 
//SYSIN    DD  DISP=SHR,DSN=&ADSN(&MEM) 
// PEND 
//* 
//COMP     EXEC CCAM,IDSN='MTLCICS.METALC.SAMPCODE', 
//         ADSN='MTLUSR.METAL.GENASM', 
//         ODSN='MTLUSR.METAL.OBJ',MEM=MTL2XPI 
//*