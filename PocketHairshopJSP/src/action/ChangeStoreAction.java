package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StoreDAO;
import vo.StoreVO;

/**
 * Servlet implementation class ChangeStore
 */
@WebServlet("/changeStore.do") 
public class ChangeStoreAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String resultStr = "";
		
		String idx = request.getParameter("nickName_idx");
		String name = request.getParameter("name"); 
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String tel = request.getParameter("tel");
		String openClose = request.getParameter("openClose");
		String info = request.getParameter("info");
		
		if(idx != null && name != null && address1 != null && address2 != null && tel != null && openClose != null && info != null) {
			
			int nickName_idx = Integer.parseInt(idx); 
			
			StoreVO vo = new StoreVO();
			
			vo.setNickName_idx(nickName_idx);
			vo.setName(name);
			vo.setAddress1(address1);
			vo.setAddress2(address2);
			vo.setTel(tel); 
			vo.setOpenClose(openClose); 
			vo.setInfo(info);
			
			StoreDAO dao = StoreDAO.getInstance();
			int result = dao.changeStore(vo);
			 
			if( result >= 1 ) {
				resultStr = "success"; 
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			
			
		}
	}
}
