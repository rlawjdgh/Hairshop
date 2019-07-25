package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MemberDAO;
import dao.StoreDAO;
import vo.MemberVO;
import vo.StoreVO;

/**
 * Servlet implementation class GetStoreInfoAction
 */
@WebServlet("/getStoreInfo.do")
public class GetStoreInfoAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String idx = request.getParameter("nickName_idx");
		
		String result = "";
		String name = "";
		String address1 = "";
		String address2 = "";
		String tel = "";
		String openClose = "";
		String photo1 = "";
		String photo2 = "";
		String info = "";
			
		if(idx != null) {
			
			StoreDAO dao = StoreDAO.getInstance();
			StoreVO vo = dao.getStoreInfo(Integer.parseInt(idx)); 
			
			if(vo == null) {
				
				result = "fail";
				response.getWriter().println(String.format("[{'result':'%s'}]", result)); 
			} else {
				
				result = "success";
				name = vo.getName();
				address1 = vo.getAddress1();
				address2 = vo.getAddress2();
				tel = vo.getTel();
				openClose = vo.getOpenClose();
				photo1 = vo.getPhoto1();
				photo2 = vo.getPhoto2();
				info = vo.getInfo(); 
				
				String str = String.format("[{'name':'%s', 'address1':'%s', 'address2':'%s', 'tel':'%s', 'openClose':'%s', 'photo1':'%s', 'photo2':'%s', 'info':'%s', 'result':'%s'}]", name, address1, address2, tel, openClose, photo1, photo2, info, result);
				
				response.setCharacterEncoding("UTF-8"); 
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().println(str);
			} 
		}
	}  
}
 