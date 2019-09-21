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
 * Servlet implementation class RemovePointAction
 */
@WebServlet("/removePoint.do")
public class RemovePointAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String resultStr = "";
		String login_idx = request.getParameter("login_idx");
		String point = request.getParameter("point");
		
		if(login_idx != null && point != null) {
			
			MileageDAO dao = MileageDAO.getInstance();
			
			int user_point = dao.getMyPoint(Integer.parseInt(login_idx));
			
			MileageVO vo = new MileageVO();
			vo.setLogin_idx(Integer.parseInt(login_idx));
			vo.setUser_point(user_point - Integer.parseInt(point)); 
			 
			int result = dao.removePoint(vo);
			
			if( result >= 1 ) {
				resultStr = "success";  
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		} 
	}

}
