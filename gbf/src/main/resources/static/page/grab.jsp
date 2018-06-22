<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<html>

<head>
    <title>参数</title>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.js"
            integrity="sha256-fNXJFIlca05BIO2Y5zh1xrShK3ME+/lYZ0j+ChxX2DA=" crossorigin="anonymous"></script>
    <script type="application/javascript">
        $(document).ready(function () {
            var max=30000;
            for(var i=1;i<=max;i++){
                $.post({
                    url:"/userRedPacket/grabRedPacket",
                    success:function (result) {
                        
                    }
                })
            }
        });
    </script>
</head>
</html>