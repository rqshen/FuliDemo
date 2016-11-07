一、打包上线说明：
1打包的key是bcb2.key。别名：baicaibang 密码：123456
2apk加固工具是使用360加固，可根据自己喜好更换。
3暂时未引入代码混淆。
4上线渠道暂时只有应用包与友盟更新，帐号密码统一是如下：
 baicaibang2015@qq.com
 baicaibang123
5关闭日志打印。
6.打渠道包：channel.txt

二、Git分支说明：
1三个主要分支master、bcb_dev、bcb_hotfix，其他release分支是每次版本上线后的一个备份分支。
2master每次打包上线分支，bcb_dev是新需求开发分支，bcb_hotfix是bug修复分支。
3根据业务需求，有时新需求并不是在下一版本上线，而只上线bug修复版本，所以最好在上线后才进行分支合并。

三、遗留问题
1极光推送与美洽客服在bug收集后台有不少崩溃bug，如有更新需要尽快升级SDK，只需要修改gradle。
美洽SDK：https://github.com/Meiqia/MeiqiaSDK-Android
