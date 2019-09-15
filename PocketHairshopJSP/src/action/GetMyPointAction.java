package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MileageDAO;

/**
 * Servlet implementation class GetMyPointAction
 */
@WebServlet("/getMyPoint.do")
public class GetMyPointAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String login_idx = request.getParameter("login_idx"); 
		
		if(login_idx != null) {
			
			MileageDAO dao = MileageDAO.getInstance();
			int point = dao.getMyPoint(Integer.parseInt(login_idx));
			 
			response.getWriter().println(String.format("[{'point':'%d'}]", point));
		} 
	}

}
