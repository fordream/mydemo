package com.iwgame.msgs.localdb;

/*
 * 本地sqlite的数据库，所有版本的初始化语句，只可以增加新的sql语句，不可以删除
 * create by yuqingwu 2013.08.16
 */
public class InitSql {
	public static final String V1_SQL_CREAT_TB_USER =
			"create table if not exists user ("
			+ "id integer primary key autoincrement not null"
			+ ",userid bigint"
			+ ",username varchar(64)"
			+ ",serial bigint"
			+ ",avatar varchar(256)"
			+ ",rel_positive  integer"
			+ ",rel_inverse  integer"
			+ ",grade integer"
			+ ",sex integer"
			+ ",city varchar(32)"
			+ ",mood varchar(512)"
			+ ",description varchar(512)"
			+ ",updatetime bigint"
			+ ",age integer"
			+ ",job varchar(64)"
			+ ",gametime varchar(32)"
			+ ",likegametype varchar(32)"
			+ ",atime bigint"
			+ ")";
	
	

	public static final String V1_SQL_CREAT_TB_GAME =
			"create table if not exists game  ("
			+ "id integer primary key autoincrement not null"
			+ ",gameid bigint"
			+ ",gamename varchar(64)"
			+ ",gamelogo varchar(256)"
			+ ",gamepackageid bigint"
			+ ",type varchar(64)"
			+ ",publisher varchar(256)"
			+ ",like integer"
			+ ",dislike integer"
			+ ",mlike integer"
			+ ",mdislike integer"
			+ ",status integer"
			+ ",utime bigint"
			+ ",desc varchar(1024)"
			+ ",gputime bigint"
			+ ",gtype integer"
			+ ")";
	
//	id	id	bigint	 	 	 	是	是	 
//	贴吧包id	packageid	bigint	 	 	 	 	 	 
//	贴吧id	gameid	bigint	 	 	 	 	 	 
//	贴吧包名	packagename	varchar	256	 	 	 	 	 
//	贴吧下载地址	downloadurl	varchar	512	 	 	 	 	 
//	贴吧类型	type	varchar	64	 	 	 	 	 
//	贴吧开发商	dev	varchar	64	 	 	 	 	 
//	贴吧版本	version	varchar	64	 	 	 	 	 
//	贴吧文件大小	filesize	bigint	 	 	 	 	 	单位Kb
//	贴吧简介	desc	varchar	512	 	 	 	 	 
//	贴吧截图	screenshot	varchar	1024	 	 	 	 	中间用“,”分隔

	
	public static final String V1_SQL_CREAT_TB_GAMEPACKAGE =
			"create table if not exists gamepackage ("
			+ "id integer primary key autoincrement not null"
			+ ",packageid bigint"
			+ ",gameid bigint"
			+ ",packagename varchar(256)"
			+ ",downloadurl varchar(512)"
			+ ",type varchar(64)"
			+ ",dev varchar(64)"
			+ ",version varchar(64)"
			+ ",filesize bigint"
			+ ",desc varchar(512)"
			+ ",screenshot varchar(1024)"
			+ ",gamename varchar(256)"
			+ ",status integer"
			+ ",utime bigint"
			+ ",gameicon varchar(256)"
			+ ",publisher varchar(256)"
			+ ",platform integer"
			+ ",createtime bigint"
			+ ")";
	
//	id	id	bigint	 	 	否	是	是	 	 	 
//	贴吧id	gameid	bigint	 	 	 	 	 	 	 	 
//	关系	relation	int	 	 	 	 	 	 	 	1：关注，2：取消关注
//	关系产生的时间	lastupdatetime	bigint	 
//	
	public static final String V1_SQL_CREAT_TB_RELATIONGAME =
			"create table if not exists relationgame ("
			+ "id integer primary key autoincrement not null"
			+ ",gameid bigint"
			+ ",relation integer"
			+ ",isbarmanager integer"
			+ ",lastupdatetime bitint"
			+ ")";
			

//	id	id	integer	 	 	 	是	是	 
//	消息产生方类型	source	integer	 	 	 	 	 	1 client ;2:server
//	通道id	channelid	integer	 	 	 	 	 	 
//	通道类型	channeltype	varchar	32	 	 	 	 	 
//	服务器消息id	msgid	bigint	 	 	 	 	 	 
//	发送者id	fromid	bigint	 	 	 	 	 	
//	 
//	发送者类型	fromdomain	varchar	32	 	 	 	 	
//	对应domain
//	接收者id	toid	bigint	 	 	 	 	 	 
//	接受者类型	todomain	varchar	32	 	 	 	 	
//	对应domain
//	主体id	subjectid	bigint	 	 	 	 	 	 
//	主体domain	subjectdomain	varchar	32	 	 	 	 	对应domain
//	消息内容类别	category	varchar	32	 	 	 	 	 
//	消息摘要	summary	varchar	512	 	 	 	 	 
//	内容类型	contenttype	integer	 	 	 	 	 	
//	 对应MessageContentType
//	消息内容体	content	varchar	4096	 	 	 	 	 
//	消息创建时间	createtime	bigint	 	 	 	 	 	 
//	该消息发送时的位置	position	varchar	256	 	 	 	 	 
//	读的状态	readstatus	integer	 	 	 	 	 	1，已读，2未读
//	消息状态	status	integer	 	 	 	 	 	
//	1：发送中（发送时有效）:
//	2：发送成功（发送时有效）
//	3：发送失败（发送时有效）

    //消息表	
	public static final String V1_SQL_CREAT_TB_MESSAGE =
			"create table if not exists message ("
			+ "id integer primary key autoincrement not null"
			+ ",source integer"	
			+ ",channeltype varchar(64)"
			+ ",msgid bigint"
			+ ",fromid bigint"
			+ ",fromdomain varchar(32)"
			+ ",toid  bigint"
	        + ",todomain varchar(32)"
	        + ",subjectid  bigint"
	        + ",subjectdomain varchar(32)"
	        + ",category varchar(32)"
	        + ",summary varchar(512)"
	        + ",contenttype integer"
	        + ",content varchar(4000)"
	        + ",createtime bigint" 
	        + ",position varchar(256)"
	        + ",readstatus integer"
	        + ",status integer"
	        + ",ext varchar(512)"
	        + ",estimatetype integer"
	        + ",estimateop integer"
	        + ",notnotify integer" 
	        + ")";
	
	//内容详情表	
	public static final String V1_SQL_CREAT_TB_CONTENT =
			"create table if not exists content ("
			+ "id integer primary key autoincrement not null"
			+ ",publishingid bigint"	
			+ ",publishingtype varchar(32)"
			+ ",contentid bigint"
			+ ",content varchar(8000)"
			+ ",type integer"
			+ ",parentid bigint"
			+ ",parenttype integer"
			+ ",parentpublishingid bigint"
			+ ",parentpublishingtype varchar(32)"
			+ ",ancestorid bigint"
			+ ",ancestortype integer"
			+ ",ancestorpublishingid bigint"
			+ ",ancestorpublishingtype varchar(32)"
			+ ",status  integer"
	        + ",createtime bigint"
			+ ",praisecount integer"
			+ ",treadcount integer"
			+ ",commentcount integer"
			+ ",forwardcount integer"
			+ ",allowpraise integer"
			+ ",allowtread integer"
            + ",allowcomment integer"
            + ",allowforward integer"
            + ",ispraise integer"
            + ",istread integer"
            + ",iscomment integer"
            + ",isforward integer"
	        + ")";
	
	/**
	 * 创建资源表
	 */
	public static final String V1_SQL_CREAT_TB_RESOURCE =
			"create table if not exists resource ("
			+ "id integer primary key autoincrement not null"
			+ ",userid bigint"	
			+ ",resourceId varchar(256)"
			+ ",createtime bigint"
			+ ",type integer"
	        + ")";
	
	//地球（省、城市）	
	public static final String V1_SQL_CREAT_TB_AREA =
			"create table if not exists area ("
			+ "id integer primary key not null"
			+ ",areaname varchar(256)"	
			+ ",parentid integer"
			+ ",type integer"
	        + ")";
	
	//城市值
	public static final String V1_SQL_CREAT_TB_AREA_INIT =
		"(110000,'北京市',110000,103);(110101,'东城区',110000,601);(110102,'西城区',110000,601);(110105,'朝阳区',110000,601);(110106,'丰台区',110000,601);(110107,'石景山区',110000,601);(110108,'海淀区',110000,601);(110109,'门头沟区',110000,601);(110111,'房山区',110000,601);(110112,'通州区',110000,601);(110113,'顺义区',110000,601);(110114,'昌平区',110000,601);(110115,'大兴区',110000,601);(110116,'怀柔区',110000,601);(110117,'平谷区',110000,601);(110228,'密云县',110000,301);(110229,'延庆县',110000,301);(120000,'天津市',120000,103);(120101,'和平区',120000,601);(120102,'河东区',120000,601);(120103,'河西区',120000,601);(120104,'南开区',120000,601);(120105,'河北区',120000,601);(120106,'红桥区',120000,601);(120110,'东丽区',120000,601);(120111,'西青区',120000,601);(120112,'津南区',120000,601);(120113,'北辰区',120000,601);(120114,'武清区',120000,601);(120115,'宝坻区',120000,601);(120116,'滨海新区',120000,601);(120221,'宁河县',120000,301);(120223,'静海县',120000,301);(120225,'蓟县',120000,301);(130000,'河北省',130000,101);(130100,'石家庄市',130000,401);(130200,'唐山市',130000,401);(130300,'秦皇岛市',130000,401);(130400,'邯郸市',130000,401);(130500,'邢台市',130000,401);(130600,'保定市',130000,401);(130700,'张家口市',130000,401);(130800,'承德市',130000,401);(130900,'沧州市',130000,401);(131000,'廊坊市',130000,401);(131100,'衡水市',130000,401);(140000,'山西省',140000,101);(140100,'太原市',140000,401);(140200,'大同市',140000,401);(140300,'阳泉市',140000,401);(140400,'长治市',140000,401);(140500,'晋城市',140000,401);(140600,'朔州市',140000,401);(140700,'晋中市',140000,401);(140800,'运城市',140000,401);(140900,'忻州市',140000,401);(141000,'临汾市',140000,401);(141100,'吕梁市',140000,401);(150000,'内蒙古',150000,102);(150100,'呼和浩特',150000,401);(150200,'包头市',150000,401);(150300,'乌海市',150000,401);(150400,'赤峰市',150000,401);(150500,'通辽市',150000,401);(150600,'鄂尔多斯',150000,401);(150700,'呼伦贝尔',150000,401);(150800,'巴彦淖尔',150000,401);(150900,'乌兰察布',150000,401);(152200,'兴安盟',150000,204);(152500,'锡林郭勒盟',150000,204);(152900,'阿拉善盟',150000,204);(210000,'辽宁省',210000,101);(210100,'沈阳市',210000,401);(210200,'大连市',210000,401);(210300,'鞍山市',210000,401);(210400,'抚顺市',210000,401);(210500,'本溪市',210000,401);(210600,'丹东市',210000,401);(210700,'锦州市',210000,401);(210800,'营口市',210000,401);(210900,'阜新市',210000,401);(211000,'辽阳市',210000,401);(211100,'盘锦市',210000,401);(211200,'铁岭市',210000,401);(211300,'朝阳市',210000,401);(211400,'葫芦岛市',210000,401);(220000,'吉林省',220000,101);(220100,'长春市',220000,401);(220200,'吉林市',220000,401);(220300,'四平市',220000,401);(220400,'辽源市',220000,401);(220500,'通化市',220000,401);(220600,'白山市',220000,401);(220700,'松原市',220000,401);(220800,'白城市',220000,401);(222400,'延边朝鲜族',220000,202);(230000,'黑龙江省',230000,101);(230100,'哈尔滨市',230000,401);(230200,'齐齐哈尔',230000,401);(230300,'鸡西市',230000,401);(230400,'鹤岗市',230000,401);(230500,'双鸭山市',230000,401);(230600,'大庆市',230000,401);(230700,'伊春市',230000,401);(230800,'佳木斯市',230000,401);(230900,'七台河市',230000,401);(231000,'牡丹江市',230000,401);(231100,'黑河市',230000,401);(231200,'绥化市',230000,401);(232700,'大兴安岭',230000,201);(310000,'上海市',310000,103);(310101,'黄浦区',310000,601);(310103,'卢湾区',310000,601);(310104,'徐汇区',310000,601);(310105,'长宁区',310000,601);(310106,'静安区',310000,601);(310107,'普陀区',310000,601);(310108,'闸北区',310000,601);(310109,'虹口区',310000,601);(310110,'杨浦区',310000,601);(310112,'闵行区',310000,601);(310113,'宝山区',310000,601);(310114,'嘉定区',310000,601);(310115,'浦东新区',310000,601);(310116,'金山区',310000,601);(310117,'松江区',310000,601);(310118,'青浦区',310000,601);(310120,'奉贤区',310000,601);(310230,'崇明县',310000,301);(320000,'江苏省',320000,101);(320100,'南京市',320000,401);(320200,'无锡市',320000,401);(320300,'徐州市',320000,401);(320400,'常州市',320000,401);(320500,'苏州市',320000,401);(320600,'南通市',320000,401);(320700,'连云港市',320000,401);(320800,'淮安市',320000,401);(320900,'盐城市',320000,401);(321000,'扬州市',320000,401);(321100,'镇江市',320000,401);(321200,'泰州市',320000,401);(321300,'宿迁市',320000,401);(330000,'浙江省',330000,101);(330100,'杭州市',330000,401);(330200,'宁波市',330000,401);(330300,'温州市',330000,401);(330400,'嘉兴市',330000,401);(330500,'湖州市',330000,401);(330600,'绍兴市',330000,401);(330700,'金华市',330000,401);(330800,'衢州市',330000,401);(330900,'舟山市',330000,401);(331000,'台州市',330000,401);(331100,'丽水市',330000,401);(340000,'安徽省',340000,101);(340100,'合肥市',340000,401);(340200,'芜湖市',340000,401);(340300,'蚌埠市',340000,401);(340400,'淮南市',340000,401);(340500,'马鞍山市',340000,401);(340600,'淮北市',340000,401);(340700,'铜陵市',340000,401);(340800,'安庆市',340000,401);(341000,'黄山市',340000,401);(341100,'滁州市',340000,401);(341200,'阜阳市',340000,401);(341300,'宿州市',340000,401);(341400,'巢湖市',340000,401);(341500,'六安市',340000,401);(341600,'亳州市',340000,401);(341700,'池州市',340000,401);(341800,'宣城市',340000,401);(350000,'福建省',350000,101);(350100,'福州市',350000,401);(350200,'厦门市',350000,401);(350300,'莆田市',350000,401);(350400,'三明市',350000,401);(350500,'泉州市',350000,401);(350600,'漳州市',350000,401);(350700,'南平市',350000,401);(350800,'龙岩市',350000,401);(350900,'宁德市',350000,401);(360000,'江西省',360000,101);(360100,'南昌市',360000,401);(360200,'景德镇市',360000,401);(360300,'萍乡市',360000,401);(360400,'九江市',360000,401);(360500,'新余市',360000,401);(360600,'鹰潭市',360000,401);(360700,'赣州市',360000,401);(360800,'吉安市',360000,401);(360900,'宜春市',360000,401);(361000,'抚州市',360000,401);(361100,'上饶市',360000,401);(370000,'山东省',370000,101);(370100,'济南市',370000,401);(370200,'青岛市',370000,401);(370300,'淄博市',370000,401);(370400,'枣庄市',370000,401);(370500,'东营市',370000,401);(370600,'烟台市',370000,401);(370700,'潍坊市',370000,401);(370800,'济宁市',370000,401);(370900,'泰安市',370000,401);(371000,'威海市',370000,401);(371100,'日照市',370000,401);(371200,'莱芜市',370000,401);(371300,'临沂市',370000,401);(371400,'德州市',370000,401);(371500,'聊城市',370000,401);(371600,'滨州市',370000,401);(371700,'菏泽市',370000,401);(410000,'河南省',410000,101);(410100,'郑州市',410000,401);(410200,'开封市',410000,401);(410300,'洛阳市',410000,401);(410400,'平顶山市',410000,401);(410500,'安阳市',410000,401);(410600,'鹤壁市',410000,401);(410700,'新乡市',410000,401);(410800,'焦作市',410000,401);(410900,'濮阳市',410000,401);(411000,'许昌市',410000,401);(411100,'漯河市',410000,401);(411200,'三门峡市',410000,401);(411300,'南阳市',410000,401);(411400,'商丘市',410000,401);(411500,'信阳市',410000,401);(411600,'周口市',410000,401);(411700,'驻马店市',410000,401);(420000,'湖北省',420000,101);(420100,'武汉市',420000,401);(420200,'黄石市',420000,401);(420300,'十堰市',420000,401);(420500,'宜昌市',420000,401);(420600,'襄樊市',420000,401);(420700,'鄂州市',420000,401);(420800,'荆门市',420000,401);(420900,'孝感市',420000,401);(421000,'荆州市',420000,401);(421100,'黄冈市',420000,401);(421200,'咸宁市',420000,401);(421300,'随州市',420000,401);(422800,'恩施土家',420000,202);(430000,'湖南省',430000,101);(430100,'长沙市',430000,401);(430200,'株洲市',430000,401);(430300,'湘潭市',430000,401);(430400,'衡阳市',430000,401);(430500,'邵阳市',430000,401);(430600,'岳阳市',430000,401);(430700,'常德市',430000,401);(430800,'张家界市',430000,401);(430900,'益阳市',430000,401);(431000,'郴州市',430000,401);(431100,'永州市',430000,401);(431200,'怀化市',430000,401);(431300,'娄底市',430000,401);(433100,'湘西土家',430000,202);(440000,'广东省',440000,101);(440100,'广州市',440000,401);(440200,'韶关市',440000,401);(440300,'深圳市',440000,401);(440400,'珠海市',440000,401);(440500,'汕头市',440000,401);(440600,'佛山市',440000,401);(440700,'江门市',440000,401);(440800,'湛江市',440000,401);(440900,'茂名市',440000,401);(441200,'肇庆市',440000,401);(441300,'惠州市',440000,401);(441400,'梅州市',440000,401);(441500,'汕尾市',440000,401);(441600,'河源市',440000,401);(441700,'阳江市',440000,401);(441800,'清远市',440000,401);(441900,'东莞市',440000,401);(442000,'中山市',440000,401);(445100,'潮州市',440000,401);(445200,'揭阳市',440000,401);(445300,'云浮市',440000,401);(450000,'广西壮族',450000,102);(450100,'南宁市',450000,401);(450200,'柳州市',450000,401);(450300,'桂林市',450000,401);(450400,'梧州市',450000,401);(450500,'北海市',450000,401);(450600,'防城港市',450000,401);(450700,'钦州市',450000,401);(450800,'贵港市',450000,401);(450900,'玉林市',450000,401);(451000,'百色市',450000,401);(451100,'贺州市',450000,401);(451200,'河池市',450000,401);(451300,'来宾市',450000,401);(451400,'崇左市',450000,401);(460000,'海南省',460000,101);(460100,'海口市',460000,401);(460200,'三亚市',460000,401);(469001,'五指山市',460000,401);(469002,'琼海市',460000,401);(469003,'儋州市',460000,401);(469005,'文昌市',460000,401);(469006,'万宁市',460000,401);(469007,'东方市',460000,401);(469021,'定安县',460000,302);(469022,'屯昌县',460000,302);(469023,'澄迈县',460000,302);(469024,'临高县',460000,302);(469025,'白沙黎族',460000,302);(469026,'昌江黎族',460000,302);(469027,'乐东黎族',460000,302);(469028,'陵水黎族',460000,302);(469029,'保亭黎族',460000,302);(469030,'琼中黎族',460000,302);(469031,'西沙群岛',460000,701);(469032,'南沙群岛',460000,701);(469033,'中沙群岛',460000,701);(500000,'重庆市',500000,103);(500101,'万州区',500000,601);(500102,'涪陵区',500000,601);(500103,'渝中区',500000,601);(500104,'大渡口区',500000,601);(500105,'江北区',500000,601);(500106,'沙坪坝区',500000,601);(500107,'九龙坡区',500000,601);(500108,'南岸区',500000,601);(500109,'北碚区',500000,601);(500110,'万盛区',500000,601);(500111,'双桥区',500000,601);(500112,'渝北区',500000,601);(500113,'巴南区',500000,601);(500114,'黔江区',500000,601);(500115,'长寿区',500000,601);(500116,'江津区',500000,601);(500117,'合川区',500000,601);(500118,'永川区',500000,601);(500119,'南川区',500000,601);(500222,'綦江县',500000,301);(500223,'潼南县',500000,301);(500224,'铜梁县',500000,301);(500225,'大足县',500000,301);(500226,'荣昌县',500000,301);(500227,'璧山县',500000,301);(500228,'梁平县',500000,301);(500229,'城口县',500000,301);(500230,'丰都县',500000,301);(500231,'垫江县',500000,301);(500232,'武隆县',500000,301);(500233,'忠县',500000,301);(500234,'开县',500000,301);(500235,'云阳县',500000,301);(500236,'奉节县',500000,301);(500237,'巫山县',500000,301);(500238,'巫溪县',500000,301);(500240,'石柱土家',500000,302);(500241,'秀山土家',500000,302);(500242,'酉阳土家',500000,302);(500243,'彭水苗族',500000,302);(510000,'四川省',510000,101);(510100,'成都市',510000,401);(510300,'自贡市',510000,401);(510400,'攀枝花市',510000,401);(510500,'泸州市',510000,401);(510600,'德阳市',510000,401);(510700,'绵阳市',510000,401);(510800,'广元市',510000,401);(510900,'遂宁市',510000,401);(511000,'内江市',510000,401);(511100,'乐山市',510000,401);(511300,'南充市',510000,401);(511400,'眉山市',510000,401);(511500,'宜宾市',510000,401);(511600,'广安市',510000,401);(511700,'达州市',510000,401);(511800,'雅安市',510000,401);(511900,'巴中市',510000,401);(512000,'资阳市',510000,401);(513200,'阿坝藏族',510000,202);(513300,'甘孜藏族',510000,202);(513400,'凉山彝族',510000,202);(520000,'贵州省',520000,101);(520100,'贵阳市',520000,401);(520200,'六盘水市',520000,401);(520300,'遵义市',520000,401);(520400,'安顺市',520000,401);(522200,'铜仁地区',520000,201);(522300,'黔西南布',520000,202);(522400,'毕节地区',520000,201);(522600,'黔东南苗',520000,202);(522700,'黔南布依',520000,202);(530000,'云南省',530000,101);(530100,'昆明市',530000,401);(530300,'曲靖市',530000,401);(530400,'玉溪市',530000,401);(530500,'保山市',530000,401);(530600,'昭通市',530000,401);(530700,'丽江市',530000,401);(530800,'普洱市',530000,401);(530900,'临沧市',530000,401);(532300,'楚雄彝族自治州',530000,202);(532500,'红河哈尼族',530000,202);(532600,'文山壮族',530000,202);(532800,'西双版纳',530000,202);(532900,'大理白族',530000,202);(533100,'德宏傣族',530000,202);(533300,'怒江傈僳',530000,202);(533400,'迪庆藏族',530000,202);(540000,'西藏',540000,101);(540100,'拉萨市',540000,401);(542100,'昌都地区',540000,201);(542200,'山南地区',540000,201);(542300,'日喀则',540000,201);(542400,'那曲地区',540000,201);(542500,'阿里地区',540000,201);(542600,'林芝地区',540000,201);(610000,'陕西省',610000,101);(610100,'西安市',610000,401);(610200,'铜川市',610000,401);(610300,'宝鸡市',610000,401);(610400,'咸阳市',610000,401);(610500,'渭南市',610000,401);(610600,'延安市',610000,401);(610700,'汉中市',610000,401);(610800,'榆林市',610000,401);(610900,'安康市',610000,401);(611000,'商洛市',610000,401);(620000,'甘肃省',620000,101);(620100,'兰州市',620000,401);(620200,'嘉峪关市',620000,401);(620300,'金昌市',620000,401);(620400,'白银市',620000,401);(620500,'天水市',620000,401);(620600,'武威市',620000,401);(620700,'张掖市',620000,401);(620800,'平凉市',620000,401);(620900,'酒泉市',620000,401);(621000,'庆阳市',620000,401);(621100,'定西市',620000,401);(621200,'陇南市',620000,401);(622900,'临夏回族',620000,202);(623000,'甘南藏族',620000,202);(630000,'青海省',630000,101);(630100,'西宁市',630000,401);(632100,'海东地区',630000,201);(632200,'海北藏族',630000,202);(632300,'黄南藏族',630000,202);(632500,'海南藏族',630000,202);(632600,'果洛藏族',630000,202);(632700,'玉树藏族',630000,202);(632800,'海西蒙古',630000,202);(640000,'宁夏',640000,101);(640100,'银川市',640000,401);(640200,'石嘴山市',640000,401);(640300,'吴忠市',640000,401);(640400,'固原市',640000,401);(640500,'中卫市',640000,401);(650000,'新疆',650000,101);(650100,'乌鲁木齐',650000,401);(650200,'克拉玛依',650000,401);(652100,'吐鲁番',650000,201);(652200,'哈密地区',650000,201);(652300,'昌吉',650000,202);(652700,'博尔塔拉',650000,202);(652800,'巴音郭楞',650000,202);(652900,'阿克苏',650000,201);(653000,'克孜勒苏',650000,202);(653100,'喀什地区',650000,201);(653200,'和田地区',650000,201);(654000,'伊犁哈萨',650000,202);(654200,'塔城地区',650000,201);(654300,'阿勒泰',650000,201);(659000,'自治区',650000,203);(659001,'石河子市',650000,401);(659002,'阿拉尔市',650000,401);(659003,'木舒克市',650000,401);(659004,'五家渠市',650000,401);(999077,'香港',999077,104);(999078,'澳门',999078,104);(999079,'台湾',999079,NULL);(999080,NULL,NULL,NULL);(1,NULL,NULL,NULL);(100000,'其他',100000,103);(100001,'其他地区',100000,601)";
	
	//公会
	public static final String V1_SQL_CREAT_TB_GROUP =
		"create table if not exists groups  ("
		+ "id integer primary key autoincrement not null"
		+ ",grid bigint"
		+ ",name varchar(64)"
		+ ",avatar varchar(256)"
		+ ",serial bigint"
		+ ",presidentId bigint"
		+ ",gid bigint"
		+ ",notice varchar(8000)"
		+ ",undesc varchar(8000)"
		+ ",creatTime bigint"
		+ ",utime bigint"
		+ ",ureltime bigint"
		+ ",cleanMaxUid bigint"
		+ ",total integer"
		+ ",presidentName varchar(64)"
		+ ",needValidate integer"
		+ ",netoffon integer"
		+ ",msgoffon integer"
		+ ",relwithgroup integer"
		+ ",maxcount integer"
		+ ",sid bigint"
		+ ")";
	
	//公会关系
	public static final String V1_SQL_CREAT_TB_GROPUREL =
		"create table if not exists groupuserrel  ("
		+ "id integer primary key autoincrement not null"
		+ ",uid bigint"
		+ ",grid bigint"
		+ ",rel integer" 
		+ ",atime bigint" 
		+",remark varchar(128)"
	    +",name varchar(128)"
		+ ")";
	
	//用户行为表
	public static final String V1_SQL_CREAT_TB_ACTION =
		"create table if not exists useraction  ("
		+ "id integer primary key autoincrement not null"
		+ ",entitytype integer"
		+ ",entityid bigint"
		+ ",count integer"
		+ ",optype integer"
		+ ",content varchar(512)"
		+ ",creattime bitint"
		+ ")";
	
	public static final String V1_SQL_CREAT_TB_USERGRADE =
		"create table if not exists usergrade  ("
		+ "id integer primary key autoincrement not null"
		+ ",grade integer"
		+ ",point integer"
		+ ",joingroup integer"
		+ ",creategroup integer"
		+ ",followgame integer"
		+ ",sendpost integer"
		+ ",datelimit integer"
		+ ",status integer"
		+",options varchar(30)"
		+",multiple varchar(20)"
		+ ")";

	public static final String V1_SQL_CREAT_TB_GROUPGRADE =
		"create table if not exists groupgrade  ("
		+ "id integer primary key autoincrement not null"
		+ ",grade integer"
		+ ",point integer"
		+ ",members integer"
		+ ",userlimit integer"
		+ ",grouplimit integer"
		+ ",status integer"
		+ ")";
	
	public static final String V1_SQL_CREAT_TB_POINTTASK =
			"create table if not exists pointtask  ("
			+ "id integer primary key autoincrement not null"
			+ ",taskid integer"
			+ ",taskname varchar(256)"
			+ ",taskdesc varchar(1024)"
			+ ",point integer"
			+ ",type integer"
			+ ",times integer"
			+ ",toid integer"
			+ ",status integer"
			+",exp integer"
			+",exptimes integer"
			+",detailsBytes varchar(8000)"
			+",gids varchar(512)"
			+",postcontent blod"
			+ ",detail varchar(1024)"
			+ ")";
	
	public static final String V16_SQL_CREAT_TB_TOPICTAG =
			"create table if not exists topictag  ("
			+ "id integer primary key autoincrement not null"
			+ ",tagname varchar(256)"
			+ ",utime bitint"
			+ ")";
	
	
	public static final String V2_SQL_CREAT_INDEX_MESSAGE=
		"create index if not exists idx_message on message(channelType,subjectid,subjectdomain,category)";
	
	public static final String V3_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_FORWARDID = "alter table message add forwardId bigint" ;
	public static final String V3_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_FORWARDTYPE = "alter table message add forwardType varchar(32)" ;
	
	public static final String V4_SQL_ALTER_TB_USER_ADD_COLUMN_MOBILE = "alter table user add mobile varchar(32)" ;
	public static final String V4_SQL_ALTER_TB_USER_ADD_COLUMN_WEIBO = "alter table user add weibo varchar(32)" ;
	public static final String V4_SQL_ALTER_TB_USER_ADD_COLUMN_MOBILENAME = "alter table user add mobilename varchar(32)" ;
	public static final String V4_SQL_ALTER_TB_USER_ADD_COLUMN_WEIBONAME = "alter table user add weiboname varchar(32)" ;
	
	//更新之前异常的数据
	public static final String V5_SQL_UPDATE_TB_MESSAGE_UPDATE_DATA = "update message set subjectid= fromid ,subjectdomain=fromdomain where channeltype ='notify' and category = 'announce'" ;
	
	public static final String V6_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_MSGINDEX = "alter table message add msgindex bigint" ;

	public static final String V7_SQL_DEL_ALL_MESSAGE= "delete from message";

	public static final String V8_SQL_ALTER_TB_USER_ADD_COLUMN_POINT= "alter table user add point integer" ;
	public static final String V8_SQL_ALTER_TB_GROUPS_ADD_COLUMN_GRADE= "alter table groups add grade integer" ;
	public static final String V8_SQL_ALTER_TB_GROUPS_ADD_COLUMN_POINT= "alter table groups add point integer" ;
	public static final String V8_SQL_ALTER_TB_GROUPSUSERREL_ADD_COLUMN_CPOINT= "alter table groupuserrel add cpoint integer" ;
	
	public static final String V9_SQL_ALTER_TB_USER_ADD_COLUMN_REMARKNAME = "alter table user add remarkname varchar(64)";
	public static final String V9_SQL_ALTER_TB_USER_ADD_COLUMN_ATIME = "alter table user add atime bigint" ;
	
	public static final String V10_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_DETAIL = "alter table pointtask add detail varchar(1024)";
	public static final String V10_SQL_ALTER_TB_GROUPSUSERREL_ADD_COLUMN_ATIME= "alter table groupuserrel add atime bigint" ;
	public static final String V11_SQL_alter_TB_USER_GRADE_ADD_COLUMN_OPTIONS = "alter table usergrade add options varchar(30)";
	public static final String V11_SQL_alter_TB_USER_GRADE_ADD_COLUMN_MULTIPLE = "alter table usergrade add multiple varchar(20)";
	public static final String V12_SQL_ALTER_TB_USER_GRADE_ADD_COLUMN_MULTIPLE = "alter table groupuserrel add remark varchar(128)";
	public static final String V13_SQL_ALTER_TB_GROUP_ADD_COLUMN_RELWITHGROUP = "alter table groups add relwithgroup integer";
	public static final String V14_SQL_ALTER_TB_GROUP_ADD_COLUMN_MAXCOUNT = "alter table groups add maxcount integer";

	public static final String V15_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_EXT = "alter table message add ext varchar(512)";
	
	public static final String V17_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_ESTIMATETYPE = "alter table message add estimatetype integer";
	public static final String V17_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_ESTIMATEOP = "alter table message add estimateop integer";
	public static final String V17_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_NOTNOTIFY = "alter table message add notnotify integer";
	
	public static final String V18_SQL_ALTER_TB_DEL_POINTTASK_TABLE = "drop table pointtask";
	public static final String V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_EXP = "alter table pointtask add exp integer";
	public static final String V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_EXPTIMES = "alter table pointtask add exptimes integer";
	public static final String V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_DETAILSBYTES = "alter table pointtask add detailsBytes varchar(8000)";
	public static final String V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_GIDS = "alter table pointtask add gids varchar(512)";
	public static final String V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_POSTCONTENT ="alter table pointtask add postcontent blod";
	public static final String V18_SQL_ALTER_TB_USER_ADD_COLUMN_EXP= "alter table user add exp integer" ;
	public static final String V18_SQL_ALTER_TB_USER_GRADE_ADD_COLUMN_EXP= "alter table usergrade add exp integer" ;
	
	public static final String V19_SQL_ALTER_TB_GROUP_ADD_COLUMN_SID = "alter table groups add sid integer";
}
