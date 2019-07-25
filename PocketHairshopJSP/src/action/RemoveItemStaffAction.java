package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StaffDAO;
import dao.SurgeryDAO;

/**
 * Servlet implementation class RemoveSurgeryAction
 */
@WebServlet("/removeItemStaff.do")
public class RemoveItemStaffAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String staff_idx = request.getParameter("staff_idx");
		
		if(staff_idx != null) {
			
			String resultStr = "";
			
			StaffDAO dao = StaffDAO.getInstance();
			int result = dao.removeItemStaff(Integer.parseInt(staff_idx));
			
			if( result >= 1 ) {
				resultStr = "success";  
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			
		}
	}

}
