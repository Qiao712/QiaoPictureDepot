## 安全
Spring Security
### 认证
* 密码加密方式 BCrypt
* 使用form表单进行认证，自定义成功/失败时处理器，替代默认的重定向
* 若请求未认证，返回401
* 前端设置axios拦截到401，重定向到认证界面
### 授权
* PictureService中使用函数安全注解
* 其余部分操作: 在DAO层通过Id和ownerId一起查询，确保该对象由该用户所有。在Service层保证传入DAO的ownerId为当前用户Id。

## 图片文件储存（暂时）
* 简单地储存为文件
* 数据库picture表中储存图片的地址
* /{username}/{albumId}/{pictureRef file}

## 图组修改/创建(图片文件上传)
* 复杂性：图片预览、拖动排序、删除、上传的结合、事务的维护
* 方案1：分三个请求完成
    * 1.请求删除图片（上传待删除图片的id数组）
    * 2.请求上传图片（返回新上传的图片的id，将其填入前端的图片列表以进行图片顺序的上传）
    * 3.上传图片顺序（上传一个图片id的数组，以id在数组中的下标指示图片位置）
    * 问题：无原子性，上传步骤复杂
* **方案2（使用）**：通过一个请求form表单，完成图片的上传，和删除信息，位置信息
    * 位置信息: 以图片id的数组表示顺序。未上传的图片的id留空(null)，新上传的图片以此所占的次序。
* 事务的维护!!!!

## 图片查看授权
* picture以及pictureGroup表中没用储存图片属主，只有在album表中储存
* 检测一张图片的所有者，涉及三张表，查询速率较低
* **用户查看一个有多张picture的pictureGroup时，对同一pictureGroup进行多次访问 --> 使用redis缓存用户对图组的访问权限**
* 同一时间对一个用户只缓存1个pictureGroup的访问权 --> 成功时，认证时间压缩1/3
* 过程:
* 用户通过pictureGroupId和pictureId请求一张图片(/api/pictureRef-groups/{pictureGroupId}/pictureRefs/{pictureId})
    1. 检查用户是否允许访问该pictureGroup
        * 先查看redis缓存中是否存在 username - pictureGroupId 键值对，若存在则允许访问
        * 若不存在，则查询通过pictureGroupId查询其所属的album，再得到所有者，进行判断是否允许访问。若允许访问设置一个60秒过期的username - pictureGroupId 键值对,加速下次查询
    2. 若拥有pictureGroup的访问权，则继续进行picture的查询
        * 使用pictureGroupId和pictureId查询picture表，保证该picture属于该pictureGroup
        * 得到picture的储存路径，进行实际的访问

## 评论系统
* 评论分为两级
  * 直接在图组下评论的为一级评论
  * 回复一个一级评论或二级评论的评论为二级评论
* 二级评论通过repliedId指向其所回复的评论
* 二级评论通过其parentId指向其一级评论

### 消息系统
* 分为**系统消息** 与 **用户消息**（用户之间的私信，暂未实现）



## 表单验证
* 在Dto和Domain的类字段上添加注解约束
* 使用分组验证，分为Update时的约束(id != null) 和 Add时的约束
