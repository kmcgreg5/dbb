//XA050CHG JOB  (6000),'B.WILLIAMS', REGION=4M,                         JOB11114
//     CLASS=C,MSGCLASS=S,MSGLEVEL=(1,1), TYPRUN=HOLD,
//     NOTIFY=XA050
//*
//*
//* THE ABOVE JOB CARDS CAME FROM THE IMBED OF SKEL CMN$$JCD
//*)IM CMN$$JCD
//*
//*  JOB REQUESTED BY XA050 ON 2020/04/30 AT 10:43
//*
/*JOBPARM SYSAFF=AGFD
//*
//*)IM CMN$$DSN
//*)IM CMN$$JBL
//JOBLIB   DD  DISP=SHR,
//             DSN=OEM.AGFP.SERENA.CHGMAN.CLOAD
//         DD  DISP=SHR,
//             DSN=OEM.AGFP.SERENA.CHGMAN.SERCOMC.CLOAD
//         DD  DISP=SHR,
//             DSN=OEM.AGFP.SERENA.CHGMAN.LOAD
//         DD  DISP=SHR,
//             DSN=OEM.AGFP.SERENA.CHGMAN.SERCOMC.LOAD
//*)IM CMNEZTP
//*)IM CMN$$VAR
//*)IM CMN$PARM
//*)IM OMF$PARM
//*)IM PRM$BRCH
//*)IM CMN$$XSC
//SERCOPY EXEC PGM=SERCOPY,    *** COPY OF2730ZZ FROM STAGING
//             REGION=3M,
//             PARM=('INDSN(A.P1.CM.ZZ.STAGE.BRCH.#005217.SRC)',
//             'MEMBER=OF2730ZZ')
//SYSPRINT DD  DISP=(,PASS),DSN=LIST00,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE)
//ABNLIGNR DD  DUMMY
//SYSUT2   DD  DISP=(,PASS),DSN=SOURCE(OF2730ZZ),
//             UNIT=SYSDA,SPACE=(CYL,(1,2,1),RLSE),
//             DCB=(DSORG=PO,RECFM=FB,LRECL=80,BLKSIZE=0)
//SYSUT3   DD  UNIT=SYSDA,SPACE=(CYL,(5,5))
//SYSUT4   DD  UNIT=SYSDA,SPACE=(CYL,(5,5))
//*)IM CMN$$WRT
//WRITE   EXEC PGM=CMNWRITE,   *** PARSE/EXPAND COMPONENT OF2730ZZ
//             COND=(4,LT),
//             PARM=('SUBSYS=P,USER=XA050',
//             'NOEXPAND')
//*)IM CMN$$SPR
//SER#PARM DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.TCPIPORT
//SYSPRINT DD  DISP=(,PASS),DSN=LIST10W1,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE)
//*)IM CMN$$SYC
//SYSLIB   DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//*  EZC PROMOTION LIBRARIES (EZT INCLUDES CREATED FROM COBOL)
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.ADM.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSYS.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.INS.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETL.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.CALU.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.MKTG.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETF.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSYS.QA.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.INS.QA.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.QB.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.QA.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.CALU.QA.EZCLIB
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.LAG.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.COMM.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.BL.CPXLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.BL.TPCLIB
//* ALL APPLICATION COPY LIBRARIES AND THE COMMON COPY LIBRARY
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.COMM.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.OFFL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.SCOR.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.ADM.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSVC.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSYS.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.INS.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LAG.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LEDG.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.SOFT.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LEDG.LAGLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.CALU.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.MKTG.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETF.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.MEQ.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.DWHS.BL.CPYLIB
//* BML 07/20/12 ADDED FOLLOWING LIBRARY TO BRCH CONCATENATION LIST
//          DD DISP=SHR,DSN=OEM.AGFT.MOBIUS.VDIRECT.COPYCOB
//*)IM AGF$$SYC
//          DD DISP=SHR,DSN=SYS4.GTB.MACLIB
//          DD DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.MAINT.CTCLIB
//* RECOMPILE =    MQSERIES =
//*        DD DISP=(MOD,PASS),DSN=CPYLIB
//SYSIFILE DD  DISP=(OLD,PASS),DSN=SOURCE(OF2730ZZ)
//SYSOFILE DD  DISP=(,PASS),DSN=WRITE,
//             UNIT=SYSDA,SPACE=(CYL,(1,1)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=0)
//SYSUT3   DD  DISP=(,PASS),DSN=CPYLIB,
//             UNIT=SYSDA,SPACE=(CYL,(1,1,20),RLSE),
//             DCB=(DSORG=PO,RECFM=FB,LRECL=80,BLKSIZE=0)
//ABNLIGNR DD  DUMMY
//SYSUDUMP DD  SYSOUT=*
//SYSIN    DD  *
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
//EZT     EXEC PGM=EZTCOM,    *** COMPILE COMPONENT OF2730ZZ
//             COND=(4,LT),REGION=0M,
//             PARM=('')
//STEPLIB  DD  DISP=SHR,DSN=OEM.AGFP.CAI.EZT.CBAALOAD
//         DD  DISP=SHR,DSN=OEM.AGFP.CAI.PANSQL.CAILIB
//SYSPRINT  DD DISP=(,PASS),DSN=LIST30,
//             UNIT=SYSDA,SPACE=(CYL,(1,1),RLSE),
//             DCB=(RECFM=FBA,LRECL=165)
//*)IM CMN$$SYC
//SYSLIB   DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//*  EZC PROMOTION LIBRARIES (EZT INCLUDES CREATED FROM COBOL)
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.ADM.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSYS.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.INS.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETL.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.CALU.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.MKTG.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETF.ST.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSYS.QA.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.INS.QA.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.QB.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.QA.EZCLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.CALU.QA.EZCLIB
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.LAG.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.COMM.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.BL.CPXLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.CPYLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.BL.TPCLIB
//* ALL APPLICATION COPY LIBRARIES AND THE COMMON COPY LIBRARY
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.COMM.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.OFFL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.SCOR.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.ADM.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSVC.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.GSYS.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.INS.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LAG.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LEDG.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.SOFT.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.XYCR.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LEDG.LAGLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.CALU.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.MKTG.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.RETF.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.MEQ.BL.CPYLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.DWHS.BL.CPYLIB
//* BML 07/20/12 ADDED FOLLOWING LIBRARY TO BRCH CONCATENATION LIST
//          DD DISP=SHR,DSN=OEM.AGFT.MOBIUS.VDIRECT.COPYCOB
//*)IM AGF$$SYC
//          DD DISP=SHR,DSN=SYS4.GTB.MACLIB
//          DD DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.MAINT.CTCLIB
//* RECOMPILE =    MQSERIES =
//*        DD DISP=(MOD,PASS),DSN=CPYLIB
//EZTVFM    DD UNIT=SYSDA,SPACE=(4096,(100,100))
//SYSUT1    DD UNIT=SYSDA,SPACE=(CYL,(2,2))
//SYSUT2    DD UNIT=SYSDA,SPACE=(CYL,(2,2))
//SYSPUNCH  DD DUMMY,DCB=BLKSIZE=80
//SYSLIN  DD  DISP=(,PASS),DSN=OBJECT(OF2730ZZ),
//             UNIT=SYSDA,SPACE=(TRK,(5,15,1),RLSE),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=0)
//SYSIN     DD *,DCB=BLKSIZE=6000
PARM LINK(OF2730ZZ R) WORKFILE(YES)
/*
//          DD DISP=(OLD,DELETE),DSN=WRITE
//*********************************************************
//*)IM CMN$$SSI
//SSIDN   EXEC PGM=CMNSSIDN,   *** PROCESS LINK-EDIT CONTROL CARDS
//             COND=(4,LT)
//SYSPRINT DD  DISP=(,PASS),DSN=LIST40S1,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE),
//             DCB=(RECFM=FA,LRECL=133,BLKSIZE=0)
//SYSUDUMP DD  SYSOUT=*
//ABNLIGNR DD  DUMMY
//OBJ      DD  DISP=(OLD,PASS),DSN=OBJECT(OF2730ZZ)
//LCT      DD  DISP=(,PASS),DSN=LCT,
//             UNIT=SYSDA,SPACE=(TRK,(1,5)),
//             DCB=(RECFM=F,LRECL=80,BLKSIZE=0)
//STG      DD  DISP=(,PASS),DSN=NULLLCT,
//             UNIT=SYSDA,SPACE=(TRK,(1,1,1),RLSE),
//             DCB=(DSORG=PO,RECFM=FB,LRECL=80,BLKSIZE=0)
//SYSIN    DD  *
LCT=OF2730ZZ
SSI=717AA362
PKG=BRCH005217
RLK=
UIL=
OPT=OBJECT
OPT=NONAME
//*)IM CMN$$CND
//*)IM CMN$$LNK
//ALOCIN  EXEC PGM=IEBGENER,   *** ALLOC NULL SYSLIN FOR CONCAT
//             COND=(4,LT)
//SYSIN    DD  DUMMY
//SYSUT1   DD  DUMMY,DCB=(RECFM=FB,LRECL=80,BLKSIZE=0)
//SYSUT2   DD  DISP=(,PASS),DSN=NULLIN,
//             UNIT=SYSDA,SPACE=(TRK,(1,1)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=0)
//SYSPRINT DD  DUMMY
//*)IM CMN$$CND
//LNK     EXEC PGM=IEWL,       *** LINK-EDIT COMPONENT OF2730ZZ
//             COND=(4,LT),REGION=8M,
//             PARM=('LIST,XREF,MAP,NORENT',
//             '')
//SYSPRINT DD  DISP=(,PASS),DSN=LIST50L1,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE)
//SYSUT1   DD  UNIT=SYSDA,SPACE=(CYL,(5,5))
//*)IM CMN$$SYL
//SYSLIB   DD  DISP=SHR,DSN=A.P1.CM.ZZ.STAGE.BRCH.#005217.LOD
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.LODLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.LOOLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.LOADLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.ONLINE.LOADLIB
//         DD  DISP=SHR,DSN=SYS4.OVERLIB.MOBIUS
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.BL.TPLOAD
//*)IM AGF$$SYL
//*
//* ADDITIONAL LINKEDIT SYSLIB LIBRARIES
//*
//* EZTP   BRCH
//          DD DISP=SHR,DSN=CEE.SCEELKED
//          DD DISP=SHR,DSN=OEM.AGFP.CAI.EZT.CBAALOAD
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.ONLINE.LOADLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LOADLIB
//          DD DISP=SHR,DSN=CICS.AGFP.V55.SDFHLOAD
//          DD DISP=SHR,DSN=TCPIP.SEZATCP
//*)IM CMN$$SYL END
//*)IM CMN$$ILL
//SYSLMOD  DD  DISP=(,PASS),DSN=LOAD,
//             UNIT=SYSDA,SPACE=(CYL,(2,1,5)),
//             DSNTYPE=LIBRARY
//*             DCB=A.P1.CM.ZZ.STAGE.BRCH.#005217.LOD
//*  CICSPC  CICSSTUB Y RELINK
//SYSLIN   DD  *
//         DD  DISP=(OLD,PASS),DSN=LCT
//         DD  DISP=(OLD,PASS),DSN=OBJECT(OF2730ZZ)
//*)IM CMN$$CND
//*
//BT90LOD EXEC PGM=CMNBAT90, *** RECORD LOD NAMES
//             COND=(4,LT)
//SYSPRINT DD  DISP=(,PASS),DSN=LIST51L1,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE),
//             DCB=(RECFM=FBM,LRECL=133,BLKSIZE=0)
//BAT90IN  DD  DISP=(OLD,PASS),DSN=LOAD
//BAT90OUT DD  DISP=(MOD,PASS),DSN=BAT90CTL,
//             UNIT=SYSDA,SPACE=(CYL,(2,1)),
//             DCB=(DSORG=PS,RECFM=FB,LRECL=80,BLKSIZE=0)
//BAT90LST DD  DISP=(OLD,PASS),DSN=LIST50L1
//BAT90WRK DD  DISP=(,DELETE),DSN=BAT90WRK,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE)
//*)IM CMN$$SYL
//BAT90LIB DD  DISP=SHR,DSN=A.P1.CM.ZZ.STAGE.BRCH.#005217.LOD
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.LODLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.ST.LOOLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.LOADLIB
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.ONLINE.LOADLIB
//         DD  DISP=SHR,DSN=SYS4.OVERLIB.MOBIUS
//         DD  DISP=SHR,DSN=A.P1.CM.ZZ.BRCH.BL.TPLOAD
//*)IM AGF$$SYL
//*
//* ADDITIONAL LINKEDIT SYSLIB LIBRARIES
//*
//* EZTP   BRCH
//          DD DISP=SHR,DSN=CEE.SCEELKED
//          DD DISP=SHR,DSN=OEM.AGFP.CAI.EZT.CBAALOAD
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.ONLINE.LOADLIB
//          DD DISP=SHR,DSN=A.P1.CM.ZZ.LOADLIB
//          DD DISP=SHR,DSN=CICS.AGFP.V55.SDFHLOAD
//          DD DISP=SHR,DSN=TCPIP.SEZATCP
//*)IM CMN$$SYL END
//* ADDITIONAL SYSIN CONTROL CARDS BELOW COME FROM IMBED OF CMN$$ILC
//SYSIN    DD  *
PKG=BRCH005217
SLT=SRC
SNM=OF2730ZZ
SID=XA050
SSI=717AA362
LNG=EZTP
PRC=CMNEZTP
LLT=LOD
SUP=NO
SLB=BRCHLODA.P1.CM.ZZ.STAGE.BRCH.#005217.LOD
SLB=BRCHLODA.P1.CM.ZZ.BRCH.ST.LODLIB
SLB=BRCHLOOA.P1.CM.ZZ.BRCH.ST.LOOLIB
SLB=BRCHLODA.P1.CM.ZZ.LOADLIB
SLB=BRCHLOOA.P1.CM.ZZ.ONLINE.LOADLIB
SLB=BRCHOVRSYS4.OVERLIB.MOBIUS
SLB=BRCHLOTA.P1.CM.ZZ.BRCH.BL.TPLOAD
//SYSUDUMP DD  SYSOUT=*
//ABNLIGNR DD  DUMMY
//*)IM CMN$$CND
//*)IM CMN$$ILD
//VFYILOD  EXEC PGM=CMNBILOD,  *** Verify ILOD conflicts
//             COND=(4,LT),
//             PARM='SUBSYS=P,USER=XA050'
//*)IM CMN$$SPR
//SER#PARM DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.TCPIPORT
//SYSPRINT DD  DISP=(,PASS),DSN=LIST801,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE),
//             DCB=(RECFM=FA,LRECL=133,BLKSIZE=0)
//SERPRINT DD  SYSOUT=*
//ABNLIGNR DD  DUMMY
//SYSUDUMP DD  SYSOUT=*
//SYSIN    DD  DISP=(OLD,PASS),DSN=BAT90CTL,
//             UNIT=SYSDA,SPACE=(CYL,(2,1)),
//             DCB=(DSORG=PS,RECFM=FB,LRECL=80,BLKSIZE=0)
//         DD  *
BRCH  ?/!     LLT=LST
BRCH  ?/!     LNM=OF2730ZZ
//CHKVILOD IF (VFYILOD.RUN = TRUE) THEN
//DLTILOD EXEC PGM=IEFBR14,
//             COND=(8,GT,VFYILOD)
//BAT90CTL  DD DISP=(MOD,DELETE,DELETE),
//             UNIT=SYSDA,SPACE=(CYL,0),
//             DCB=(DSORG=PS,RECFM=FB,LRECL=80,BLKSIZE=0),
//             DSN=BAT90CTL
//CHKVILOD ENDIF
//*)IM CMN$$CND
//*)IM CMN$$PAS
//CPYLOD  EXEC PGM=SERCOPY,    *** COPY TO LOD STAGING LIB
//             REGION=3M,
//             COND=(4,LT),
//             PARM=('RETRY,REALLOC',
//             'OUTDSN(A.P1.CM.ZZ.STAGE.BRCH.#005217.LOD)')
//SYSPRINT DD  DISP=(MOD,PASS),DSN=LIST1001,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE)
//SYSUT1   DD  DISP=(OLD,DELETE),DSN=LOAD
//SYSUDUMP DD  SYSOUT=*
//ABNLIGNR DD  DUMMY
//*)IM CMN$$CND
//*)IM CMN90
//*)IM CMN00
//SUCCESS EXEC PGM=CMNBATCH,   *** Access ChangeMan ZMF started task
//             COND=(4,LT),
//             PARM='SUBSYS=P,USER=XA050'
//*)IM CMN$$SPR
//SER#PARM DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.TCPIPORT
//SYSPRINT DD  DISP=(,PASS),DSN=LIST92,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE),
//             DCB=(RECFM=FA,LRECL=133,BLKSIZE=0)
//SERPRINT DD  SYSOUT=*
//CMNDELAY DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMANP.CMNDELAY
//ABNLIGNR DD  DUMMY
//SYSUDUMP DD  SYSOUT=*
//SYSIN    DD  *
BRCH  ?/!     RTP=ISRC
BRCH  ?/!     LIB=SRC
BRCH  ?/!     LNG=EZTP
BRCH  ?/!     SID=XA050
BRCH  ?/!     CHT=0867287000005F62
BRCH  ?/!     SSI=717AA362
BRCH  ?/!     TLT=LOD
BRCH  ?/!     SUP=NO
BRCH  ?/!     PRC=CMNEZTP
BRCH  ?/!     LOP=
BRCH  ?/!     COP=
BRCH  ?/!     UO1=         Y
BRCH  ?/!     UO2=N
BRCH  ?/!     011=
BRCH  ?/!     012=
BRCH  ?/!     013=
BRCH  ?/!     014=
BRCH  ?/!     015=
BRCH  ?/!     021=
BRCH  ?/!     022=
BRCH  ?/!     023=
BRCH  ?/!     031=
BRCH  ?/!     032=
BRCH  ?/!     033=
BRCH  ?/!     041=
BRCH  ?/!     042=
BRCH  ?/!     043=
BRCH  ?/!     081=
BRCH  ?/!     082=
BRCH  ?/!     083=
BRCH  ?/!     084=
BRCH  ?/!     085=
BRCH  ?/!     101=
BRCH  ?/!     102=
BRCH  ?/!     161=
BRCH  ?/!     162=
BRCH  ?/!     341=
BRCH  ?/!     342=
BRCH  ?/!     441=
BRCH  ?/!     442=
BRCH  ?/!     641=
*
BRCH  ?/!     642=
*
BRCH  ?/!     643=
*
BRCH  ?/!     644=
*
BRCH  ?/!     645=
*
BRCH  ?/!     721=
*
BRCH  ?/!     722=
*
BRCH  ?/!     723=
*
BRCH  ?/!     724=
*
BRCH  ?/!     725=
*
BRCH  ?/!     CNM=OF2730ZZ
BRCH  ?/!     CID=
//         DD  DISP=(MOD,DELETE),DSN=BAT90CTL,
//             UNIT=SYSDA,SPACE=(CYL,(2,1)),
//             DCB=(DSORG=PS,RECFM=FB,LRECL=80,BLKSIZE=0)
//*)IM CMN99
//CHKCOND EXEC PGM=IEFBR14,    *** CHECK PREVIOUS RETURN CODES
//             COND=(8,LE)
//FAILURE EXEC PGM=CMNBATCH,   *** NOTIFY USER PROCESS HAS FAILED
//             COND=(EVEN,(0,EQ,CHKCOND)),
//             PARM='SUBSYS=P,USER=XA050'
//*)IM CMN$$SPR
//SER#PARM DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.TCPIPORT
//SERPRINT DD  SYSOUT=*
//SYSPRINT DD  SYSOUT=*
//CMNDELAY DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMANP.CMNDELAY
//ABNLIGNR DD  DUMMY
//SYSUDUMP DD  SYSOUT=*
//SYSIN    DD  DISP=(MOD,DELETE),DSN=BAT90CTL,
//             UNIT=SYSDA,SPACE=(CYL,(2,1)),
//             DCB=(DSORG=PS,RECFM=FB,LRECL=80,BLKSIZE=0)
//         DD  *
BRCH  ?/Ä     LIB=SRC
BRCH  ?/Ä     CNM=OF2730ZZ
BRCH  ?/Ä     FUN=90,TID=XA050
//*)IM AGFABSCK
//*********************************************************************
//* ROUTINE TO CHECK ABS PROCEDURE LIBRARY IF ANY MEMBERS MATCH       *
//* PROGRAM BEING COMPILED                                            *
//*LAST CARD AGFABSCK
//*)IM CMN$$PCP
//PRINT   EXEC PGM=SERPRINT,   *** MERGE SYSPRINT DATASETS
//             COND=EVEN,REGION=4M,
//             PARM=('INDSN(LIST*)',
//             'OUTFILE(PRINT1,PRINT2)')
//PRINT1    DD DISP=(,PASS),DSN=LIST,
//             UNIT=SYSDA,SPACE=(CYL,(5,5),RLSE),
//             DCB=(RECFM=VBM,LRECL=140,BLKSIZE=23476,BUFNO=50)
//PRINT2    DD SYSOUT=*,
//             DCB=(RECFM=VBM,LRECL=140,BLKSIZE=23476,BUFNO=50)
//COMPLST EXEC PGM=SERCOPY,    *** UPDATE LST STAGING LIBRARY
//             REGION=4M,
//             PARM=('COMPRESS(7),IN(S1),OUT(S2)',
//             'MEM(OF2730ZZ),REALLOC,USTATS',
//             'OUTDSN(A.P1.CM.ZZ.STAGE.BRCH.#005217.LST)')
//SYSPRINT  DD SYSOUT=*
//S1        DD DISP=(OLD,PASS),DSN=LIST,
//          DCB=BUFNO=50
//S2        DD DISP=SHR,DSN=A.P1.CM.ZZ.STAGE.BRCH.#005217.LST,
//          DCB=BUFNO=50
//*)IM CMN$$ENQ
//SYSUT3   DD  DISP=(MOD,DELETE),
//             DSN=A.P1.CM.ZZ.STAGE.BRCH.#005217.LST.ENQ,
//             UNIT=SYSDA,SPACE=(CYL,(5,5))
//SYSUT4   DD  UNIT=SYSDA,SPACE=(CYL,(5,5))
//ILODLST EXEC PGM=CMNBATCH,   *** ILOD FOR LIST STAGING MEMBER
//             PARM='SUBSYS=P,USER=XA050'
//*)IM CMN$$SPR
//SER#PARM DD  DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMAN.TCPIPORT
//SERPRINT DD SYSOUT=*
//SYSPRINT DD SYSOUT=*
//CMNDELAY DD DISP=SHR,DSN=OEM.AGFP.SERENA.CHGMANP.CMNDELAY
//ABNLIGNR DD DUMMY
//SYSUDUMP DD SYSOUT=*
//SYSIN    DD *
BRCH  ?/!     RTP=ILOD
BRCH  ?/!     SLT=SRC
BRCH  ?/!     SNM=OF2730ZZ
BRCH  ?/!     SID=XA050
BRCH  ?/!     SSI=717AA362
BRCH  ?/!     LNG=EZTP
BRCH  ?/!     PRC=CMNEZTP
BRCH  ?/!     LLT=LST
BRCH  ?/!     LNM=OF2730ZZ
BRCH  ?/!     CID=
// IF EZT.RUN THEN
//EZTOUT  EXEC PGM=IDCAMS
//DD1     DD DSN=&LIST30,DISP=(OLD,PASS)
//EZTOUT DD SYSOUT=*,LRECL=165,RECFM=FBA
//SYSPRINT DD DUMMY
//SYSIN  DD  *
 REPRO INFILE(DD1) OUTFILE(EZTOUT)
/*
// ENDIF

