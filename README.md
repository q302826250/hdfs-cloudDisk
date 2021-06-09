环境：centos7   Tomcat8.0  IDEAL MySQL hadoop
本云盘系统是基于Hadoop hdfs的集群分布式系统
技术 :
  前端:   html5+css3+vue
  后端:   springboot+spring+jpa+spring security+验证码
  依赖管理: maven
数据库:
      user表:
 	uid
  	uname
  	upwd
功能点:
1.安全认证
实现验证码功能,使用SpringSecurity实行安全认证，对异常访问进行处理
2.用户管理
用户登录，用户注册，用户注销
3.文件管理
文件剪切，文件复制，文件删除，文件上传，文件重命名，文件下载，文件类型查看，
新建目录，hadoop配置信息查看，浏览目录属性以及子文件的属性，文件目录数量统计 

问题：
1.分不清js中的单引号和双引号
2.分不清@Controller和@RestController的区别
3.InputStream.available()
4.文件上传相关知识有所忘记
5.file.getOriginalFilename()
6.HTTP Header忘了
7.同步请求和异步请求的区别
解决方案：
1.单引号和双引号都可以作为字符串的开始符和关闭符，并且只能‍同一种单或者双引号来定义开始和结束；
   在同一种引号中使用相同的引号，需要转义后才能使用。不同引号可以嵌套使用。
2.@RestController注解相当于@ResponseBody ＋ @Controller合在一起的作用。
   @ResponseBody的作用其实是将java对象转为json格式的数据。
   使用@Controller 注解，在对应的方法上，视图解析器可以解析return 的jsp,html页面，并且跳转到相应页面
3.InputStream.available()查看流的大小
4.查看https://zhuanlan.zhihu.com/p/120834588
5.file.getOriginalFilename()是得到上传时的文件名。
6.查看https://blog.csdn.net/u010429424/article/details/78032006
7.同步是指：发送方发出数据后，等接收方发回响应以后才发下一个数据包的通讯方式。  
   异步是指：发送方发出数据后，不等接收方发回响应，接着发送下个数据包的通讯方式.

