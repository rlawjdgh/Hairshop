<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
	<script type="text/javascript" src="https://code.jquery.com/jquery-1.12.4.min.js" ></script>
	<script type="text/javascript" src="https://cdn.iamport.kr/js/iamport.payment-1.1.5.js"></script>
</head>
<body>

	<input type="hidden" id="login_idx" value="${login_idx}">
	<input type="hidden" id="staff_idx" value="${staff_idx}">
	<input type="hidden" id="regdate" value="${regdate}"> 
	<input type="hidden" id="time" value="${time}">
	<input type="hidden" id="surgery_name" value="${surgery_name}">
	
	<input type="hidden" id="price" value="${price}">
	 
	
	
	<script> 
	 
		$(function(){
			
	        IMP.init('imp11160715');
	        var msg;
	        
	        IMP.request_pay({
	      
	            pg : 'kakaopay',
	            pay_method : 'card',
	            merchant_uid : 'merchant_' + new Date().getTime(),
	            name : 'hairshop 예약',
	            amount : $('#price').val(),
	            app_scheme : 'iamportapp' 
	        }, function(rsp) { 
	        	 
	            if ( rsp.success ) { 
	            	
	            	/* $.getJSON({
	                    url: "/tour/kakaoPay",
	                    type: 'POST',
	                    data: {
	                    	idx : rsp.imp_uid,
	                        memberIdx : $('#logon_idx').val(),
	                        boxIdx : $('#boxIdx').val(),
	                        day : $('#day').val(),
	                        movieNm : $('#movieNm').val(),
	                        seatNum : $('#seatNum').val(),
	                        price : $('#price').val()
	                    }
	                }); */
	            	
	            	msg = "success";
	                //$(location).attr('href', '/tour/');
	                 
	            } else {
	                  
	            	msg = "fail";
            	 	//$(location).attr('href', '/tour/');
	            }
	        }); 
	        
	    });
	</script>
</body>
</html> 