
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
  <title>第一个 百度地图入门</title>
  <!--步骤-->
  <!-- 第一步:申请百度账号和ak   E2jtd9drVKnsLEWmqNyXhq2jHLr7DWMG
   第二步: 创建 容器  设置容器样式
   第三步: 引用百度地图API文件
  第四步:  创建地图实例    设置中心点坐标   地图初始化，同时设置地图展示级别-->
  <script type="text/javascript" src="http://api.map.baidu.com/api?v=3.0&ak=E2jtd9drVKnsLEWmqNyXhq2jHLr7DWMG"></script>


  <style type="text/css">
    html {
      height: 100%
    }

    body {
      height: 100%;
      margin: 0px;
      padding: 0px
    }

    #container {
      height: 100%
    }
  </style>
</head>

<body>
<!--创建 容器-->
<div id="container">

</div>

<script>

  var map = new BMap.Map("container");
  var point = new BMap.Point(116.404, 39.915);
  map.centerAndZoom(point, 15);
  map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
</script>


</body>

</html>