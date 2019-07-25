package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SurgeryDAO;

/**
 * Servlet implementation class RemoveSurgeryAction
 */
@WebServlet("/removeItemSurgery.do")
public class RemoveItemSurgeryAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String surgery_idx = request.getParameter("surgery_idx");
		
		if(surgery_idx != null) {
			
			String resultStr = "";
			
			SurgeryDAO dao = SurgeryDAO.getInstance();
			int result = dao.removeItemSurgery(Integer.parseInt(surgery_idx));
			
			if( result >= 1 ) {
				resultStr = "success";  
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			
		}
	}

}
