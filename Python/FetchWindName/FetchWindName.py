#根据SiteID获取站点名
def FecthSiteName(SiteID):
    switchWind = {
            'a9e1bc14-1c25-46c7-9c5c-521aecdd566f':'华润海阳东方',
            '599f04bb-8629-435e-916a-412414fbc923':'中车花溪',
            '9154836a-37f8-4f63-966d-57449dad4624':'海装贾拓山',
            '6d37fd20-5bc6-4fc2-ab4a-a322a2055883':'国华柳山',
            'a2c75243-8308-4689-8ed9-591d457237c3':'海装上坝',
            'e99f56d1-d569-4d2e-bd77-13fce37e077f':'中车桃花山',
            '11363959-9163-4e54-84f1-149a1f651d29':'中车桃花山',
            'ab6b2656-ceca-4644-aff1-17a98142af23':'中车凤凰山',
            '033858a3-9120-47fd-9cf9-7ee6ef5f78d5':'中车凤凰山',
            '8a3baa7a-bf95-4840-ac42-457b6f8dfe28':'湘电广水花山',
            'd37c8448-8221-4638-9339-d4d410f9e58f':'华润龙海新村',
            '56bbe5a3-d1ad-444d-a6ad-772628ef47ab':'国华满井',
            'daa004f8-fc20-4481-9478-9a3a833401c6':'国华满井',
            '64e3cdfa-367b-4466-a99b-7a830c5eaebf':'湘电宜城板桥',
            '6735be87-da56-45a3-9090-c58385d32458':'华润大柳行',
            'ce4f0bef-68b5-42c8-9e71-1e30e8dc94da':'大唐华创木兰沟',
            '4f0f38dc-324e-42df-9065-42026952637d':'国华佳鑫',
            '89718c02-220a-4dba-bdef-84a1a80ec0fc':'华润莒县',
            '16b00999-7638-420f-bf55-5d239219d0d4':'华润平度隆鑫',
            '4410f818-4067-48f8-8b0a-cc8ae4b37dc3':'华润威海',
            'e7116294-5671-4e8d-a926-8ba5abc140ae':'华润蓬莱紫荆',
            '122d9281-6403-4822-8ead-1c518f613346':'华润凤鸣',
            'b047b5ab-1ef6-4cfd-8ff8-7f5dc626ec23':'华润千军',
            'ee592637-729b-4186-ad3b-a9e6b2943518':'华润天河口',
            'bb41d569-7e36-49a4-925c-92c08f9b48e5':'华润祥天',
            '93583cb0-619e-41db-bed9-804448752093':'华润阳西',
            'ff93b896-6249-4d9e-85e0-1d80a23891f1':'国华黄骅',
            '03bae6c1-7962-4680-998b-f2836e275af2':'湘电九江桃园',
            '81be302d-6259-4f03-b65b-86b4ff0ff248':'中车甜水堡',
            'f5fcbdb0-a29e-4cf3-8c4b-63a529155d0f':'海装远鑫三塘湖',
            '3b551043-80d8-4e1b-ba36-00d43054044e':'中车紫荆山',
            '3f4b1f42-c4f1-4236-a708-bac10ccb0267':'华润存珠',
            '700a0d2b-15d1-4d0f-bb44-295537892414':'湘电富川协和',
            '4441eb5c-8278-41ad-a25a-7dca1d7ec480':'冀东水泥',
            '6e3f4b39-0c43-4b49-9ba3-5ff01d8e3b6c':'中车西华山',
            'b8ebb679-5e24-4bd9-8b2e-75bb33fe7f3a':'中联安阳海皇',
            '310c2d55-d7f7-49fd-8657-b6d076492990':'上海悦狮传动',
            'ec0df28d-6d96-4239-9f40-61dd86853d3f':'淄博沂源徐家庄'
            }
    return switchWind[SiteID]
       
#获取风场的名称
def GetWindName():
    SiteID = input('请输入SiteID：')
    
    #判断SiteID是是否是有效
    while True:
        #判断SiteID是否为空
        while SiteID is None or SiteID == '':
            SiteID = input('SiteID不能为空，请重新输入:')
        
        try:
            SiteName = FecthSiteName(SiteID.lower())
        except KeyError as KE:
            SiteID = input('%s不在已知的SiteID内，请重新输入：'%KE)
        else:
            print('您输入的是“%s”的SiteID' %SiteName)
            print('=============================================================')
            answer = input('是否继续执行，Y(是)/任意键退出：')
            if answer.upper() == 'Y':
                GetWindName()
            break

#调用风场获取风场名的函数
GetWindName()
