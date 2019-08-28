package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MileageDAO;
import vo.MileageVO;

/**
 * Servlet implementation class AddPointAction
 */
@WebServlet("/addUserPoint.do")
public class AddPointAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
        
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");  
		
		String resultStr = "";
		
		String login_idx = request.getParameter("login_idx");
		String store_idx = request.getParameter("store_idx"); 
		String price = request.getParameter("price");
		
		if(login_idx != null && store_idx != null && price != null) {
			
			int point = (Integer.parseInt(price) * 5 / 100);
			int result; 
			
			 MileageVO vo = new MileageVO();
			 vo.setLogin_idx(Integer.parseInt(login_idx));
			 vo.setStore_idx(Integer.parseInt(store_idx));
			 
			 MileageDAO dao = MileageDAO.getInstance();
			 MileageVO vo2 = dao.findUserPoint(vo); 
			  
			 if(vo2 == null) {
				 vo.setUser_point(point);
				 result = dao.insertPoint(vo);
			 }	else {
				 vo.setUser_point(point + vo2.getUser_point());
				 result = dao.updateUserPoint(vo); 
			 }
			 
			 if( result >= 1 ) {
					resultStr = "success"; 
			}else {
				resultStr = "fail"; 
			}
		  
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}
	}

}
